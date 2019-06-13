import sim.engine.SimState;
import sim.engine.Steppable;

/** @author amashha */
public class PolicyMaker implements Steppable {
  static final long serialVersionUID = 1L;
  private double elevation;
  private Reservoir reservoir;
  private boolean isDroughtRestriction;
  private double storage;
  // private double waterQualityStorage = 61322; // acre-feet
  // private double waterSupplyStorage = 45000; // acre-feet
  private double[] reductionPercentages;

  public PolicyMaker(
      Reservoir reservoir,
      double initialElevation,
      double initialStorage,
      boolean isDroughtRestriction,
      double[] reductionPercentages) {
    this.reservoir = reservoir;
    this.elevation = initialElevation;
    this.storage = initialStorage;
    this.isDroughtRestriction = isDroughtRestriction;
    this.reductionPercentages = reductionPercentages;
  }

  public double getelevation() {
    return this.reservoir.getElevation();
  }

  public double getStorage() {
    return this.reservoir.getStorage();
  }

  public void step(SimState state) {
    // Household.setConservationStage(stage);
    if (isDroughtRestriction) {
      int s = (int) ((FallsLake) reservoir).droughtStage;

      double reductionFactor = 1;
      switch (s) {
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
      Household.setConservationFactor(reductionFactor);
    }
  }
}
