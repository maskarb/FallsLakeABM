package pertubing;

// import java.util.Arrays;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.distribution.GammaDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.random.RandomDataGenerator;
// import org.apache.commons.math3.random.EmpiricalDistribution;

public class IndependentJointDistribution {

	private static int c = 0;

	private final double meanFunc1;

	private final double meanFunc2;

	private final double stDvFun1;

	private final double stDvFun2;

	public IndependentJointDistribution(double meanFunc1, double meanFunc2, double stDvFunc1, double stDvFunc2) {

		this.meanFunc1 = meanFunc1;

		this.meanFunc2 = meanFunc2;

		this.stDvFun1 = stDvFunc1;

		this.stDvFun2 = stDvFunc2;
	}

	/* public static double sampleUsingData(DataList datalist1, DataList datalist2, double sample, boolean scale) {

		boolean print = true;
		int scaleFactor = 10000;

		datalist1.sortList();
		datalist2.sortList();
		// create distributions
		double[] values = new double[datalist1.size()];

		double[] pdf = new double[datalist1.size()];

		for (int i = 0; i < values.length; i++) {

			if (i == 0) {

				pdf[0] = 0;
				if (!scale)
					values[i] = datalist1.value(i);
				else
					values[i] = datalist1.value(i) * scaleFactor;

			} else {
				pdf[i] = pdf[i - 1] + 1.0 / (datalist1.size() - 1);
				if (!scale)
					values[i] = datalist1.value(i);
				else
					values[i] = datalist1.value(i) * scaleFactor;
			}
			if (print) {
				System.out.println("F1 " + values[i] + " " + pdf[i]);
			}
		}

		// reset the last element of pdf to 1.0
		pdf[pdf.length - 1] = 1.0;

		PolynomialSplineFunction function = new SplineInterpolator().interpolate(values, pdf);

		double[] values2 = new double[datalist2.size()];

		double[] pdf2 = new double[datalist2.size()];

		for (int i = 0; i < values2.length; i++) {

			if (i == 0) {
				pdf2[0] = 0;
				if (!scale)
					values2[i] = datalist2.value(i);
				else
					values2[i] = datalist2.value(i) * scaleFactor;

			} else {
				pdf2[i] = pdf2[i - 1] + 1.0 / (datalist2.size() - 1);
				if (!scale)
					values2[i] = datalist2.value(i);
				else
					values2[i] = datalist2.value(i) * scaleFactor;
			}
			if (print)
				System.out.println("F2 " + pdf2[i] + " " + values2[i]);

		}
		System.out.println(pdf2.length);
		// reset the last element of pdf to 1.0
		pdf2[pdf2.length - 1] = 1.0;

		PolynomialSplineFunction function2 = new SplineInterpolator().interpolate(pdf2, values2);

		double prob1 = function.value(sample);
		if (print)
			System.out.println(sample + " sample " + prob1);

		if (prob1 <= 0) {
			prob1 = function.value(sample + 0.5 * sample);
		}
		System.out.println(sample + " sample2 " + prob1);
		double r = new RandomDataGenerator().nextUniform(0, prob1);

		double prob2 = r / prob1;

		double f = function2.value(prob2);

		if (print)
			System.out.println("F is " + f + " " + prob2);
		if (c == 10) {
			// System.exit(0);
		} else {
			c++;
		}

		if (scale) {
			f = f / scaleFactor;
		}
		return f;
	} */

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

		return new double[] { f, prob };
	}

	public static double[] sampleUsingData_2(DataList datalist1) {

		datalist1.sortList();

		// create distributions
		double[] values = new double[datalist1.size() - 4];
		double[] pdf = new double[datalist1.size() - 4];

		for (int i = 0; i < values.length; i++) {
			if (i == 0) {
				pdf[0] = 0;
				values[i] = datalist1.value(i + 2);
			} else {
				pdf[i] = pdf[i - 1] + 1.0 / (datalist1.size() - 4 - 1);
				values[i] = datalist1.value(i + 2);
			}
		}

		// reset the last element of pdf to 1.0
		pdf[pdf.length - 1] = 1.0;

		PolynomialSplineFunction function = new SplineInterpolator().interpolate(pdf, values);

		double prob = new RandomDataGenerator().nextUniform(0, 1);
		double f = function.value(prob);

		return new double[] { f, prob };
	}
	
	public static double[] sampleUsingData_3(DataList datalist1) {

		datalist1.sortList();

		// create distributions
		double[] values = new double[datalist1.size() - 4];
		double[] pdf = new double[datalist1.size() - 4];

		for (int i = 0; i < values.length; i++) {
			if (i == 0) {
				pdf[0] = 0;
				values[i] = datalist1.value(i + 2);
			} else {
				pdf[i] = pdf[i - 1] + 1.0 / (datalist1.size() - 4 - 1);
				values[i] = datalist1.value(i + 2);
			}
		}

		// reset the last element of pdf to 1.0
		pdf[pdf.length - 1] = 1.0;

		PolynomialSplineFunction function = new SplineInterpolator().interpolate(pdf, values);

		double prob = new RandomDataGenerator().nextUniform(0, 1);
		double f = function.value(prob);

		return new double[] { f, prob };
	}


	public double sample(double estimatedValFunc1) {

		NormalDistribution normalDistribution1 = new NormalDistribution(meanFunc1, stDvFun1);

		NormalDistribution normalDistribution2 = new NormalDistribution(meanFunc2, stDvFun2);

		double prob1 = normalDistribution1.cumulativeProbability(estimatedValFunc1);

		double r = new RandomDataGenerator().nextUniform(0, prob1);

		double prob2 = r / prob1;

		double f = normalDistribution2.inverseCumulativeProbability(prob2);

		return f;
	}

	public double sampleUsingExponential(double estimatedValFunc1) {

		ExponentialDistribution ex1 = new ExponentialDistribution(meanFunc1);

		ExponentialDistribution ex2 = new ExponentialDistribution(meanFunc2);

		double prob1 = ex1.cumulativeProbability(estimatedValFunc1);

		// System.out.println(prob1 + " sssss");
		if (prob1 <= 0) {
			return 0;
		}

		double r = new RandomDataGenerator().nextUniform(0, prob1);

		double prob2 = r / prob1;

		double f = ex2.inverseCumulativeProbability(prob2);

		return f;
	}

	public double sampleUsingGamma(double estimatedValFunc1) {

		GammaDistribution ex1 = new GammaDistribution((meanFunc1 * meanFunc1) / (stDvFun1 * stDvFun1),
				meanFunc1 / (stDvFun1 * stDvFun1));

		GammaDistribution ex2 = new GammaDistribution((meanFunc2 * meanFunc2) / (stDvFun2 * stDvFun2),
				meanFunc2 / (stDvFun2 * stDvFun2));

		double prob1 = ex1.cumulativeProbability(estimatedValFunc1);
		double r = new RandomDataGenerator().nextUniform(0, prob1);

		double prob2 = r / prob1;

		double f = ex2.inverseCumulativeProbability(prob2);

		return f;
	}

	public static void main(String[] args) {
		double[][] data1 = { { 1.023032955, 0.05, }, { 1.070322866, 0.1, }, { 9.774503909, 0.15, },
				{ 31.87516856, 0.2, }, { 54.10289802, 0.25, }, { 58.4700756, 0.3, }, { 77.78253307, 0.35, },
				{ 84.37139866, 0.4, }, { 87.16082635, 0.45, }, { 136.3127817, 0.5, }, { 138.0590655, 0.55, },
				{ 212.7455932, 0.6, }, { 269.6938031, 0.65, }, { 296.1981006, 0.7, }, { 422.4157002, 0.75, },
				{ 583.8853265, 0.8, }, { 619.8331051, 0.85, }, { 977.9229295, 0.9, }, { 1668.151168, 0.95, },
				{ 2564.999955, 1 } };

		double[][] data2 = { { 0.047619048, 52.73987935, }, { 0.095238095, 71.54160933, },
				{ 0.142857143, 106.0382066, }, { 0.19047619, 125.9778778, }, { 0.238095238, 148.7178317, },
				{ 0.285714286, 165.7184402, }, { 0.333333333, 238.8090519, }, { 0.380952381, 244.2605078, },
				{ 0.428571429, 268.4629014, }, { 0.476190476, 315.87387, }, { 0.523809524, 387.3319427, },
				{ 0.571428571, 468.3234345, }, { 0.619047619, 615.7390245, }, { 0.666666667, 667.4146472, },
				{ 0.714285714, 699.8776352, }, { 0.761904762, 717.9256707, }, { 0.80952381, 1004.851599, },
				{ 0.857142857, 1121.229125, }, { 0.904761905, 1483.170432, }, { 0.952380952, 2767.999274 } };

		DataList d1 = new DataList();
		DataList d2 = new DataList();
		for (int i = 0; i < data2.length; i++) {
			d1.addData(i, data1[i][0]);
			d2.addData(i, data2[i][1]);
		}

		// System.out.println(sampleUsingData(d1, d2, 22.19248, false));

	}
}
