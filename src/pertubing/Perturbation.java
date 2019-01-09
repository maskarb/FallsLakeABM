package pertubing;

// import java.util.Arrays;

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.distribution.NormalDistribution;

public class Perturbation {
	private final Double probOfQuantile;
	private final Double shiftFactor; // for decrease it should be a factor of
										// 1.0. One means no change and 0.75
										// means 25% decrease and 1.25 means 25%
										// increase
	private final DataList dataList;

	public Perturbation(final double shiftFactor, final double quantileProbability, final DataList dataList) {

		double v = quantileProbability;

		if (quantileProbability == 1)
			v = 0.999999995;

		this.probOfQuantile = v;

		this.shiftFactor = shiftFactor;

		this.dataList = dataList;

		dataList.sortList();

		dataList.calculateStats();
	}

	public double workflow() {

		dataList.sortList();

		double mean = dataList.getMean();

		double meanNew = mean + Math.log(shiftFactor);

		double stDev = dataList.getStDeviation();

		// Normal distribution for shifted mean:
		NormalDistribution normalDistribution = new NormalDistribution(meanNew, stDev);

		if (probOfQuantile < 0 || probOfQuantile > 1) {
			new Exception("Wrong input. Probability must be between 0 and 1.").printStackTrace();
		}

		// inverseCumulativeProbability = quantile function => returns quantile at probOfQuantile (the cumulative probability):
		double inverseValue = normalDistribution.inverseCumulativeProbability(probOfQuantile);
//		System.out.println("inverse Value: " + inverseValue);

		// Normal distribution for original mean:
		NormalDistribution normalDistributionOriginal = new NormalDistribution(mean, stDev);

		// returns P(X <= inverseValue) from original CDF:
		double newProbability = normalDistributionOriginal.cumulativeProbability(inverseValue);
//		System.out.println("new probability: " + newProbability);

		// create distributions
		double[] values = new double[dataList.size() + 1];

		double[] pdf = new double[dataList.size() + 1];

		for (int i = 0; i < values.length; i++) {

			if (i == 0 || i == 1) {
				if (i == 0) {
					pdf[0] = 0;
					values[i] = 0;
				} else {
					pdf[1] = 1.0 / dataList.size();
					values[i] = dataList.value(i - 1);
				}
			} else {
				pdf[i] = pdf[i - 1] + 1.0 / dataList.size();
				values[i] = dataList.value(i - 1);

			}

		}
		// reset the last element of pdf to 1.0
		pdf[pdf.length - 1] = 1.0;
//		System.out.println(Arrays.toString(pdf));
//		System.out.println(Arrays.toString(values));

		// creates PDF from original values
		PolynomialSplineFunction function = new SplineInterpolator().interpolate(pdf, values);

		// samples the original PDF at newProbability
		double newValue4GivenQuantile = function.value(newProbability);
		
		return newValue4GivenQuantile;
	}

	public static void main(String[] args) {

		double[] value = { 816, 1884, 1039, 340, 1919, 784, 340, 1310, 2084, 1174, 1868, 784, 843, 1682, 1151, 3006,
				1598, 1127, 160, 568, 658, 240, 857, 368, 1082, 101, 736, 1546, 110, 155, 854 };

		int[] time = { 1983, 1984, 1985, 1986, 1987, 1988, 1989, 1990, 1991, 1992, 1993, 1994, 1995, 1996, 1997, 1998,
				1999, 2000, 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2012, 2013 };

		DataList dataList = new DataList();

		for (int i = 0; i < time.length; i++) {

			dataList.addData(time[i], value[i]);
		}
		dataList.sortList();

		for (int i = 0; i < 21; i++) {
			double n = 0.05 * i;

			Perturbation perturbaion = new Perturbation(0.75, n, dataList);

			System.out.format("%.2f %f %n", n, perturbaion.workflow());

		}
	}

}
