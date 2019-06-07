package pertubing;

public class Timeseries {
  private final DataList flow = new DataList();
  private final DataList precipitation = new DataList();
  private final DataList evapotranspiration = new DataList();
  private final DataList shiftFac = new DataList();
  private final DataList flowProb = new DataList();
  private final DataList precipProb = new DataList();
  private final DataList evapProb = new DataList();

  public DataList getFlow() {
    return flow;
  }

  public DataList getPrecipitation() {
    return precipitation;
  }

  public DataList getEvapotranspiration() {
    return evapotranspiration;
  }

  public DataList getShiftFactor() {
    return shiftFac;
  }

  public DataList getFlowProb() {
    return flowProb;
  }

  public DataList getPrecipProb() {
    return precipProb;
  }

  public DataList getEvapProb() {
    return evapProb;
  }
}
