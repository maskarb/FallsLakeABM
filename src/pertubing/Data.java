package pertubing;

public class Data implements Comparable<Data> {

	private final Integer time;
	private final Double value;

	public Data(final Integer time, final Double value) {
		this.time = time;
		this.value = value;
	}

	public Integer getTime() {
		return time;
	}

	public Double getValue() {
		return value;
	}

	public int compareTo(Data another) {
		if (this.getValue() < another.getValue()) {
			return -1;
		} else {
			return 1;
		}
	}

}
