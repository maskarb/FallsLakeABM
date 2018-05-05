package pertubing;

public class PerturbationManager {

	public static double[] executeRecon(final int[] time, final double[] values, final double interval,
			final double shiftFactor) {

		System.out.println(shiftFactor);

		DataList dataList = new DataList();

		for (int i = 0; i < time.length; i++) {

			dataList.addData(time[i], values[i]);
		}

		int times = (int) (1.0 / interval) + 1;

		double[] a = new double[times];

		for (int i = 0; i < times; i++) {

			double n = interval * i;

			Perturbation perturbation = new Perturbation(shiftFactor, n, dataList);

			a[i] = perturbation.workflow();

		}

		return a;
	}

}
