package pertubing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.analysis.function.Log;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.random.RandomDataGenerator;

public class DataList {

	private final List<Data> dataList = new ArrayList<Data>();
	private double mean;
	private double stDeviation;

	public void addData(final Integer time, final Double value) {
		dataList.add(new Data(time, Log(value)));
	}

	public void sortList() {
		Collections.sort(dataList);
	}

	public int size() {
		return dataList.size();
	}

	public Double value(final int order) {
		return dataList.get(order).getValue();
	}

	public void calculateStats() {
		setMean(Stats.mean(this));
		setStDeviation(Stats.standardDeviation(this));
	}

	public double getMean() {

		return mean;
	}

	private void setMean(double mean) {
		this.mean = mean;
	}

	public double getStDeviation() {
		return stDeviation;
	}

	private void setStDeviation(double stDeviation) {
		this.stDeviation = stDeviation;
	}

	public double sample() {

		double r = new RandomDataGenerator().nextUniform(0, 1);

		double[] values = new double[dataList.size() + 1];
		double[] pdf = new double[dataList.size() + 1];

		for (int i = 0; i < values.length; i++) {

			if (i == 0 || i == 1) {
				if (i == 0) {

					pdf[0] = 0;

					values[i] = 0;

				} else {

					pdf[1] = 1.0 / dataList.size();

					values[i] = dataList.get(i - 1).getValue();
				}
			} else {

				pdf[i] = pdf[i - 1] + 1.0 / dataList.size();

				values[i] = dataList.get(i - 1).getValue();

			}

		}
		// reset the last element of pdf to 1.0
		pdf[pdf.length - 1] = 1.0;

		PolynomialSplineFunction function = new SplineInterpolator()
				.interpolate(pdf, values);

		double quantile = function.value(r);

		return quantile;
	}

}
