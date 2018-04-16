import sim.engine.SimState;
import sim.engine.Steppable;

/**
 * 
 */

/**
 * @author amashha
 * 
 */
public class PolicyMaker implements Steppable {

	private double elevation;
	private Reservoir reservoir;
	public int stage;
	private boolean isDroughtRestriction;
	private double storage;
	private double waterQualityStorage = 61322;// acre-feet
	private double waterSupplyStorage = 45000;// acre-feet
	private double[] reductionPercentages;

	public PolicyMaker(Reservoir reservoir, double initialElevation, double initialStorage,
			boolean isDroughtRestriction, double[] reductionPercentages) {
		this.reservoir = reservoir;
		this.elevation = initialElevation;
		this.storage = initialStorage;
		this.stage = 0;
		this.isDroughtRestriction = isDroughtRestriction;
		this.reductionPercentages = reductionPercentages;
	}

	public int getStage() {
		return stage;
	}

	public double getelevation() {
		return this.reservoir.getElevation();
	}

	public double getStorage() {
		return this.reservoir.getStorage();
	}

	public void step(SimState state) {
		// WRRSim wrrSim = (WRRSim) state;

		// int time = (int) wrrSim.schedule.getTime();

		// Household.setConservationStage(stage);

		if (isDroughtRestriction) {
			this.elevation = getelevation();
			this.storage = getStorage();

			// if (elevation >= 250.8) {
			// stage = 0;
			// } else if (elevation >= 250.06) {
			// stage = 1;
			// } else if (elevation >= 249.24) {
			// stage = 2;
			// } else if (elevation >= 248.33) {
			// stage = 3;
			// } else {
			// stage = 4;
			// }
			double baseStorage = ((FallsLake) reservoir).getLowestStorage();// +
																			// waterQualityStorage;
			double conservationPool = waterSupplyStorage + waterQualityStorage;
			// if (storage >= (baseStorage +
			// reductionPercentages[0]*conservationPool)) {
			// stage = 0;
			// } else if (storage >= (baseStorage +
			// reductionPercentages[1]*conservationPool)) {
			// stage = 1;
			// } else if (storage >= (baseStorage +
			// reductionPercentages[2]*conservationPool)) {
			// stage = 2;
			// } else if (storage >= (baseStorage +
			// reductionPercentages[3]*conservationPool)) {
			// stage = 3;
			// } else {
			// stage = 4;
			// }

			double wssp = 0.423 * (storage - baseStorage);
			if (wssp >= (reductionPercentages[0] * waterSupplyStorage)) {
				stage = 0;
			} else if (wssp >= (reductionPercentages[1] * waterSupplyStorage)) {
				stage = 1;
			} else if (wssp >= (reductionPercentages[2] * waterSupplyStorage)) {
				stage = 2;
			} else if (wssp >= (reductionPercentages[3] * waterSupplyStorage)) {
				stage = 3;
			} else {
				stage = 4;
			}

			double reductionFactor = 1;
			switch (stage) {
			case 0:
				break;
			case 1:
				reductionFactor = (60 / 65.0);
				break;
			case 2:
				reductionFactor = (55 / 65.0);
				break;
			case 3:
				reductionFactor = (35 / 65.0);
				break;
			case 4:
				reductionFactor = (25 / 65.0);
				break;
			}
			// System.out.println(time + " " + stage + " " + elevation);
			// reductionFactor = 1;
			Household.setConservationFactor(reductionFactor);
		}

	}

}
