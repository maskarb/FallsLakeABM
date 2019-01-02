package pertubing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.random.RandomDataGenerator;

public class GenerateTimeseries {

	public static Timeseries execute(double[] shiftFactor, int numberYears, int RunNum) {

		Timeseries result = new Timeseries();
		Timeseries hresult = new Timeseries(); // historical data is imported. not used.

		// add historic data
		double[] hFlow = WrrProject.historicData(true, false, false);

		for (int i = 0; i < hFlow.length; i++) {
			hresult.getFlow().addData(i, hFlow[i]);
		}

		double[] hPre = WrrProject.historicData(false, true, false);

		for (int i = 0; i < hPre.length; i++) {
			hresult.getPrecipitation().addData(i, hPre[i]);
		}

		double[] hEva = WrrProject.historicData(false, false, true);

		for (int i = 0; i < hEva.length; i++) {
			hresult.getEvapotranspiration().addData(i, hEva[i]);
		}

		int month = 1;
		int len_shift = shiftFactor.length;
		String shifacStr = String.format("%.1f", shiftFactor[len_shift - 1]);

		Timeseries tMonth2 = null;

		double prob = 0;
		double probEva = 0;
		double probPre = 0;

		for (int i = 0; i < (numberYears - WrrProject.time.length) * 12; i++) {


			tMonth2 = WrrProject.reconstructAllTimeseriesWithRespectToShiftInFlow(month, 0.05, shiftFactor[i]);

			DataList flow2 = tMonth2.getFlow();
			DataList pre2 = tMonth2.getPrecipitation();
			DataList eva2 = tMonth2.getEvapotranspiration();

			double[] fa = sampleUsingData(flow2);

			double sampleFlow2 = fa[0];
			prob = fa[1];

			double[] pa = sampleUsingData(pre2);
			double samplePre2 = pa[0];
			probPre = pa[1];

			double[] ea = sampleUsingData(eva2);
			double sampleEva2 = ea[0];
			probEva = ea[1];

			result.getFlow().addData(i, sampleFlow2);
			result.getEvapotranspiration().addData(i, sampleEva2);
			result.getPrecipitation().addData(i, samplePre2);
			result.getShiftFactor().addData(i, shiftFactor[i]);
			result.getFlowProb().addData(i, prob);
			result.getPrecipProb().addData(i, probPre);
			result.getEvapProb().addData(i, probEva);
		}

		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("Flow,Preci,Evatr,ShiftFac,FlowProb,PreciProb,EvatrProb\n");

		for (int i = 0; i < result.getFlow().size(); i++) {
			stringBuffer.append(result.getFlow().value(i) + "," + result.getPrecipitation().value(i) + ","
					+ result.getEvapotranspiration().value(i) + "," + result.getShiftFactor().value(i) + ","
					+ result.getFlowProb().value(i) + "," + result.getPrecipProb().value(i) + ","
					+ result.getEvapProb().value(i) + "\n");
		}
		try {
			//File file = new File("timeseries_" + new Random().nextInt(10000) + ".txt");
			File file = new File("timeseries_" + RunNum + "_" + shifacStr + ".csv");

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);

			bw.write(stringBuffer.toString());
			bw.close();


		} catch (IOException e) {
			e.printStackTrace();
		}


		return result;
	}

	public static double[] sampleUsingData(DataList datalist1) {

		datalist1.sortList();

		// create distributions
		double[] values = new double[datalist1.size()];
		double[] pdf = new double[datalist1.size()];

		for (int i = 0; i < values.length; i++) {
			if (i == 0) {
				pdf[0] = 0;
				values[i] = datalist1.value(i);
			} else {
				pdf[i] = pdf[i - 1] + 1.0 / (datalist1.size() - 1);
				values[i] = datalist1.value(i);
			}
		}

		// reset the last element of pdf to 1.0
		pdf[pdf.length - 1] = 1.0;

		PolynomialSplineFunction function = new SplineInterpolator().interpolate(pdf, values);

		double prob = new RandomDataGenerator().nextUniform(0, 1);

		double f = function.value(prob);
		f = Math.exp(f);
		// System.out.println(f);

		return new double[] { f, prob };
	}

	public static void main(String[] args) {

		Timeseries t = null;
		for (int i = 0; i < 1; i++) {
			// System.out.println("Run " + i);
			double[] shift = {1,0.9,0.8,0.7};
			t = GenerateTimeseries.execute(shift, 80, 0);
		}

		// System.out.println(t.getFlow().size());
		for (int i = 0; i < t.getFlow().size(); i++) {
			System.out.println(t.getFlow().value(i) + " " + t.getPrecipitation().value(i) + " "
					+ t.getEvapotranspiration().value(i));
		}
	}
}
