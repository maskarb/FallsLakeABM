package pertubing;

import org.apache.commons.math3.stat.StatUtils;

public class Stats {

  public static double mean(final DataList dataList) {
    double[] timeseries = new double[dataList.size()];

    for (int i = 0; i < timeseries.length; i++) {
      timeseries[i] = dataList.value(i);
    }
    return StatUtils.mean(timeseries);
  }

  public static double standardDeviation(final DataList dataList) {
    double[] timeseries = new double[dataList.size()];

    for (int i = 0; i < timeseries.length; i++) {
      timeseries[i] = dataList.value(i);
    }
    return Math.sqrt(StatUtils.variance(timeseries));
  }
}
