package pertubing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
// import java.util.Random;
// import java.util.Arrays;

public class GenerateTimeseries {

	/* public static Timeseries executeOld(double shiftFactor, int numberYears) {

		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("Flow \t Preci \t Evatr\n");

		Timeseries result = new Timeseries();

		// add historic data
		double[] hFlow = WrrProject.historicData(true, false, false);

		for (int i = 0; i < hFlow.length; i++) {

			result.getFlow().addData(i, hFlow[i]);
		}

		double[] hPre = WrrProject.historicData(false, true, false);

		for (int i = 0; i < hPre.length; i++) {
			result.getPrecipitation().addData(i, hPre[i]);
		}

		double[] hEva = WrrProject.historicData(false, false, true);

		for (int i = 0; i < hEva.length; i++) {
			result.getEvapotranspiration().addData(i, hEva[i]);
		}

		int ref = hEva.length;

		int month = 1;
		Timeseries tMonth1 = null;

		Timeseries tMonth2 = null;

		double meanF1 = 0;

		double stF1 = 0;

		double meanF2 = 0;

		double stF2 = 0;

		double sampleFlow = 0;

		double meanE1 = 0;

		double stE1 = 0;

		double meanE2 = 0;

		double stE2 = 0;

		double sampleEva = 0;

		double meanP1 = 0;

		double stP1 = 0;

		double meanP2 = 0;

		double stP2 = 0;

		double samplePre = 0;

		for (int i = 0; i < (numberYears - WrrProject.time.length) * 12; i++) {

			if (tMonth1 == null) {

				tMonth1 = WrrProject.reconstructAllTimeseriesWithRespectToShiftInFlow(month++, 0.05, shiftFactor);

				DataList flow = tMonth1.getFlow();

				DataList pre = tMonth1.getPrecipitation();

				DataList eva = tMonth1.getEvapotranspiration();

				flow.calculateStats();

				pre.calculateStats();

				eva.calculateStats();

				meanF1 = flow.getMean();

				meanP1 = pre.getMean();

				meanE1 = eva.getMean();

				stF1 = flow.getStDeviation();

				stP1 = pre.getStDeviation();

				stE1 = eva.getStDeviation();

				sampleFlow = flow.sample();

				samplePre = pre.sample();

				sampleEva = eva.sample();

			} else {

				++month;
			}

			if (month == 13) {

				month = 1;
			}

			tMonth2 = WrrProject.reconstructAllTimeseriesWithRespectToShiftInFlow(month, 0.05, shiftFactor);

			DataList flow2 = tMonth2.getFlow();

			DataList pre2 = tMonth2.getPrecipitation();

			DataList eva2 = tMonth2.getEvapotranspiration();

			flow2.calculateStats();

			pre2.calculateStats();

			eva2.calculateStats();

			meanF2 = flow2.getMean();

			meanP2 = pre2.getMean();

			meanE2 = eva2.getMean();

			stF2 = flow2.getStDeviation();

			stP2 = pre2.getStDeviation();

			stE2 = eva2.getStDeviation();

			IndependentJointDistribution inJointDisFlow = new IndependentJointDistribution(meanF1, meanF2, stF1, stF2);

			IndependentJointDistribution inJointDisPre = new IndependentJointDistribution(meanP1, meanP2, stP1, stP2);

			IndependentJointDistribution inJointDisEva = new IndependentJointDistribution(meanE1, meanE2, stE1, stE2);

			double sampleFlow2 = inJointDisFlow.sample(sampleFlow);

			double sampleEva2 = inJointDisEva.sample(sampleEva);

			double samplePre2 = inJointDisPre.sample(samplePre);

			if (i == 0) {
				// System.out.println("adding data to " + (i + 1 + ref) + " " +
				// sampleFlow2);
				result.getFlow().addData(i + ref, sampleFlow);

				result.getFlow().addData(i + 1 + ref, sampleFlow2);

				result.getEvapotranspiration().addData(i + ref, sampleEva);

				result.getEvapotranspiration().addData(i + 1 + ref, sampleEva2);

				result.getPrecipitation().addData(i + ref, samplePre);

				result.getPrecipitation().addData(i + 1 + ref, samplePre2);

				stringBuffer.append(sampleFlow + "\t" + samplePre + "\t" + sampleEva + "\n");

				stringBuffer.append(sampleFlow2 + "\t" + samplePre2 + "\t" + sampleEva2 + "\n");

			} else {

				result.getFlow().addData(i + 1 + ref, sampleFlow2);

				result.getEvapotranspiration().addData(i + 1 + ref, sampleEva2);

				result.getPrecipitation().addData(i + 1 + ref, samplePre2);

				stringBuffer.append(sampleFlow2 + "\t" + samplePre2 + "\t" + sampleEva2 + "\n");

			}

			tMonth1 = tMonth2;

			sampleFlow = sampleFlow2;

			samplePre = samplePre2;

			sampleEva = sampleEva2;

			meanF1 = meanF2;

			meanE1 = meanE2;

			meanP1 = meanP2;

			stF1 = stF2;

			stE1 = stE2;

			stP1 = stP2;

		}

		try {

			File file = new File("timeseries_.txt");

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(stringBuffer.toString());
			bw.close();

			System.out.println("Done");

		} catch (IOException e) {
			e.printStackTrace();
		}
		// System.out.println(result.getFlow().size());

		return result;
	} */

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

		// int ref = hEva.length;

		int month = 1;
		int len_shift = shiftFactor.length;
		String shifacStr = String.format("%.1f", shiftFactor[len_shift - 1]);
		Timeseries tMonth1 = null;
		Timeseries tMonth2 = null;

		double meanF1 = 0;
		double meanF2 = 0;
		double meanE1 = 0;
		double meanE2 = 0;
		double meanP1 = 0;
		double meanP2 = 0;

		double stF1 = 0;
		double stF2 = 0;
		double stE1 = 0;
		double stE2 = 0;
		double stP1 = 0;
		double stP2 = 0;

		double sampleFlow = 0;
		double sampleEva = 0;
		double samplePre = 0;

		double prob = 0.5;
		double probEva = 0.5;
		double probPre = 0.5;

		DataList flow = null;
		DataList pre = null;
		DataList eva = null;

		for (int i = 0; i < (numberYears - WrrProject.time.length) * 12; i++) {

			if (tMonth1 == null) {
				//System.out.println("tMonth1 == null");
				tMonth1 = WrrProject.reconstructAllTimeseriesWithRespectToShiftInFlow(month++, 0.05, shiftFactor[i]);

				flow = tMonth1.getFlow();
				pre = tMonth1.getPrecipitation();
				eva = tMonth1.getEvapotranspiration();

				flow.calculateStats();
				pre.calculateStats();
				eva.calculateStats();

				meanF1 = flow.getMean();
				meanP1 = pre.getMean();
				meanE1 = eva.getMean();

				stF1 = flow.getStDeviation();
				stP1 = pre.getStDeviation();
				stE1 = eva.getStDeviation();

				sampleFlow = flow.sample();
				samplePre = pre.sample();
				sampleEva = eva.sample();

			} else {
				++month;
			}

			if (month == 13) {
				month = 1;
			}
			//System.out.println("tMonth2 " + (i+1));
			tMonth2 = WrrProject.reconstructAllTimeseriesWithRespectToShiftInFlow(month, 0.05, shiftFactor[i+1]);

			DataList flow2 = tMonth2.getFlow();
			DataList pre2 = tMonth2.getPrecipitation();
			DataList eva2 = tMonth2.getEvapotranspiration();

			flow2.calculateStats();
			pre2.calculateStats();
			eva2.calculateStats();

			meanF2 = flow2.getMean();
			meanP2 = pre2.getMean();
			meanE2 = eva2.getMean();

			stF2 = flow2.getStDeviation();
			stP2 = pre2.getStDeviation();
			stE2 = eva2.getStDeviation();

			// IndependentJointDistribution inJointDisPre = new
			// IndependentJointDistribution(
			// meanP1, meanP2, stP1, stP2);

			// IndependentJointDistribution inJointDisEva = new
			// IndependentJointDistribution(
			// meanE1, meanE2, stE1, stE2);

			// double sampleEva2 = inJointDisEva.sample(sampleEva);

			// double samplePre2 =
			// inJointDisPre.sampleUsingExponential(samplePre);

			// double samplePre2 = IndependentJointDistribution.sampleUsingData(
			// pre, pre2, samplePre, true);

			double[] fa = IndependentJointDistribution.sampleUsingData(flow2, sampleFlow, prob);

			double sampleFlow2 = fa[0];
			prob = fa[1];

			double[] pa = IndependentJointDistribution.sampleUsingData_2(pre2, samplePre, probPre);
			double samplePre2 = pa[0];
			probPre = pa[1];

			double[] ea = IndependentJointDistribution.sampleUsingData_3(eva2, sampleEva, probEva);
			double sampleEva2 = ea[0];
			probEva = ea[1];
			// double sampleEva2 = IndependentJointDistribution.sampleUsingData(
			// eva, eva2, sampleEva, false);

			if (i == 0 && i == 1) { // impossible logic. not sure why this is here.
				// System.out.println("adding data to " + (i + 1 + ref) + " " +
				// sampleFlow2);
				result.getFlow().addData(i, sampleFlow);
				result.getFlow().addData(i + 1, sampleFlow2);

				// result.getFlow().addData(i + ref, result.getFlow().value(i));

				// result.getFlow().addData(i + 1 + ref,
				// result.getFlow().value(i + 1));

				result.getEvapotranspiration().addData(i, sampleEva);
				result.getEvapotranspiration().addData(i + 1, sampleEva2);

				result.getPrecipitation().addData(i, samplePre);
				result.getPrecipitation().addData(i + 1, samplePre2);

				// result.getEvapotranspiration().addData(i + 1 + ref,
				// result.getEvapotranspiration().value(i + 1));

				// result.getPrecipitation().addData(i + 1 + ref,
				// result.getPrecipitation().value(i + 1));

				// result.getEvapotranspiration().addData(i + ref,
				// result.getEvapotranspiration().value(i));

				// result.getPrecipitation().addData(i + ref,
				// result.getPrecipitation().value(i));
			} else {
				result.getFlow().addData(i + 1, sampleFlow2);
				result.getEvapotranspiration().addData(i + 1, sampleEva2);
				result.getPrecipitation().addData(i + 1, samplePre2);
				result.getShiftFactor().addData(i + 1, shiftFactor[i]);
				result.getFlowProb().addData(i + 1, prob);
				result.getPrecipProb().addData(i + 1, probPre);
				result.getEvapProb().addData(i + 1, probEva);
			}

			flow = flow2;
			pre = pre2;
			eva = eva2;

			tMonth1 = tMonth2;

			sampleFlow = sampleFlow2;
			samplePre = samplePre2;
			sampleEva = sampleEva2;

			meanF1 = meanF2;
			meanE1 = meanE2;
			meanP1 = meanP2;

			stF1 = stF2;
			stE1 = stE2;
			stP1 = stP2;

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

			// System.out.println("Done");

		} catch (IOException e) {
			e.printStackTrace();
		}

		// System.out.println(result.getFlow().size());

		return result;
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
