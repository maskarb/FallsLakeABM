import java.util.HashMap;
import sim.engine.SimState;
import sim.engine.Steppable;

/** @author amashha */
public class PolicyMaker implements Steppable {
  static final long serialVersionUID = 1L;
  private double elevation;
  private Reservoir reservoir;
  private int stage = 0;
  private boolean isDroughtRestriction;
  private double storage;
  // private double waterQualityStorage = 61322; // acre-feet
  private double waterSupplyStorage = 45000; // acre-feet
  private double[] reductionPercentages;
  private HashMap<Integer, double[]> droughtStages;
  private HashMap<Integer, double[]> recisionStages;
  public static HashMap<Integer, Double> reportedStages;

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
    this.isDroughtRestriction = isDroughtRestriction;
    this.reductionPercentages = reductionPercentages;
    this.stage = 0;
    this.droughtStages = droughtStages;
    this.recisionStages = recisionStages;
  }

  public static double getStage(int time) {
    return reportedStages.get(time);
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
    double[] recisionPercent = recisionStages.get(month);

    if (isDroughtRestriction) {
      this.elevation = getelevation();
      this.storage = getStorage();

      double baseStorage = ((FallsLake) reservoir).getLowestStorage();

      double wssp = 0.423 * (storage - baseStorage);
      if (wssp >= (droughtPercent[0] * waterSupplyStorage)
          && wssp >= (recisionPercent[0] * waterSupplyStorage)) {
        stage = 0;
      } else if (wssp >= (droughtPercent[1] * waterSupplyStorage)
          && wssp >= (recisionPercent[1] * waterSupplyStorage)) {
        stage = 1;
      } else if (wssp >= (droughtPercent[2] * waterSupplyStorage)
          && wssp >= (recisionPercent[2] * waterSupplyStorage)) {
        stage = 2;
      } else if (wssp >= 0) {
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
      Household.setConservationFactor(reductionFactor);
      if (reportedStages == null) {
        reportedStages = new HashMap<Integer, Double>();
      }
      double temp = stage;
      reportedStages.put(time, temp);
    }
  }
}
