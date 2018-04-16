package pertubing;

public class Timeseries {
	private final DataList flow = new DataList();
	private final DataList precipitation = new DataList();
	private final DataList evapotranspiration = new DataList();
	
	public DataList getFlow() {
		return flow;
	}

	public DataList getPrecipitation() {
		return precipitation;
	}

	public DataList getEvapotranspiration() {
		return evapotranspiration;
	}
}
