package pertubing;

import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;

public class BivariateNormalDistributionCopula {

	private final MultivariateNormalDistribution multivariateNormalDistribution;

	private final NormalDistribution normalDistribution = new NormalDistribution(0, 1);

	public BivariateNormalDistributionCopula(double v, double w) {

		double m = normalDistribution.inverseCumulativeProbability(v);

		double s = normalDistribution.inverseCumulativeProbability(w);

		if (m < 0)
			m = -1 * m;

		if (s < 0)
			s = -1 * s;

		System.out.println(m + " " + s);

		double[] means = new double[] { m };

		double[][] covariances = new double[][] { { s } };

		multivariateNormalDistribution = new MultivariateNormalDistribution(means, covariances);
	}

	public double sample() {

		return multivariateNormalDistribution.sample()[0];
	}

	public static void main(String[] args) {

		BivariateNormalDistributionCopula b = new BivariateNormalDistributionCopula(0.05, 0.05);

		System.out.println(b.multivariateNormalDistribution.sample()[0]);

	}

}
