import java.util.HashMap;
import sim.engine.SimState;
import sim.engine.Steppable;

/** @author amashha */
public class PolicyMaker implements Steppable {
  static final long serialVersionUID = 1L;

  private double elevation;
  private Reservoir reservoir;
  public int stage;
  private boolean isDroughtRestriction;
  private double storage;
  // private double waterQualityStorage = 61322; // acre-feet
  private double waterSupplyStorage = 45000; // acre-feet
  private double recision = 0;
  private double[] reductionPercentages;
  private HashMap<Integer, double[]> droughtStages;
  private HashMap<Integer, double[]> recisionStages;

  public PolicyMaker(
      Reservoir reservoir,
      double initialElevation,
      double initialStorage,
      boolean isDroughtRestriction,
      double[] reductionPercentages,
      HashMap<Integer, double[]> droughtStages,
      HashMap<Integer, double[]> recisionStages) {
    this.reservoir = reservoir;
    this.elevation = initialElevation;
    this.storage = initialStorage;
    this.stage = 0;
    this.isDroughtRestriction = isDroughtRestriction;
    this.reductionPercentages = reductionPercentages;
    this.droughtStages = droughtStages;
    this.recisionStages = recisionStages;
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
    int time = (int) state.schedule.getTime();
    int month = time % 12 + 1;
    // Household.setConservationStage(stage);
    double[] droughtPercent = droughtStages.get(month);
    System.out.println(month);

    // if drought state from previous month is greater than 0, get the recision stage
    if (stage > 0) {
      recision = recisionStages.get(month)[stage - 1];
    }

    if (isDroughtRestriction) {
      this.elevation = getelevation();
      this.storage = getStorage();

      double baseStorage = ((FallsLake) reservoir).getLowestStorage();

      double wssp = 0.423 * (storage - baseStorage);
      if (wssp >= (reductionPercentages[0] * waterSupplyStorage)
          && wssp >= (waterSupplyStorage * recision)) {
        stage = 0;
      } else if (wssp >= (droughtPercent[1] * waterSupplyStorage)
          && wssp >= (waterSupplyStorage * recision)) {
        stage = 1;
      } else if (wssp >= (droughtPercent[2] * waterSupplyStorage)
          && wssp >= (waterSupplyStorage * recision)) {
        stage = 2;
      } else if (wssp >= (0 * waterSupplyStorage) && wssp >= (waterSupplyStorage * recision)) {
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
      System.out.println(stage);
      Household.setConservationFactor(reductionFactor);
    }
  }
}
