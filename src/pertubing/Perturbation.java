package pertubing;

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

		double meanNew = mean * shiftFactor;

		double stDev = dataList.getStDeviation();

		NormalDistribution normalDistribution = new NormalDistribution(meanNew, stDev);

		if (probOfQuantile < 0 && probOfQuantile > 1) {
			new Exception("Wrong input. Probability must be between 0 and 1.").printStackTrace();
		}

		double inverseValue = normalDistribution.inverseCumulativeProbability(probOfQuantile);
		// find p in the new distribution
		NormalDistribution normalDistributionOriginal = new NormalDistribution(mean, stDev);

		double newProbability = normalDistributionOriginal.cumulativeProbability(inverseValue);

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

		PolynomialSplineFunction function = new SplineInterpolator().interpolate(pdf, values);

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

			System.out.println(n + " " + perturbaion.workflow());

		}
	}

}
