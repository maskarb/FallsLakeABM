import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.grid.SparseGrid2D;
import sim.field.network.Network;
import sim.util.Bag;
import sim.util.IntBag;

/** @author amashha */
public class Household implements Steppable {

  private static final long serialVersionUID = 1L;
  private int householdSize;
  private int income;
  private int houseType;
  private IndoorEnduses indoorEnduses;
  private OutdoorEnduses outdoorEnduses;
  private double defaultConsumption;
  private double indoorDemand;
  private double outdoorDemand;
  private double complianceRate;
  private Reservoir reservoir;
  public PrintWriter outputStream;
  public static HashMap<Integer, ArrayList<Double>> totalWithdrawal;
  private int builtYear;
  public static double conservationFactor = 1;
  public boolean isRetrofitting;
  private int retrofitPeriod;

  private static SparseGrid2D city;
  private static Network socialNet;
  private int xCor;
  private int yCor;
  private Bag neighbors;
  private IntBag xPos;
  private IntBag yPos;
  private boolean isWaterReuser;
  private double receiptTime;
  private double threshold;
  private double external;

  public Household(
      int householdSize,
      int income,
      int houseType,
      double complianceDegree,
      double consumption,
      Reservoir reservoir) {
    this.complianceRate = complianceDegree;
    this.income = income;
    this.houseType = houseType;
    this.defaultConsumption = consumption;
    this.householdSize = householdSize;
    this.reservoir = reservoir;
  }

  public Household(
      int householdSize,
      int income,
      int houseType,
      double complianceDegree,
      IndoorEnduses indoor,
      OutdoorEnduses outdoor,
      Reservoir reservoir,
      PrintWriter outputStream,
      int builtYear,
      boolean isRetrofitting,
      int retrofitPeriod,
      int xCor,
      int yCor,
      boolean isWaterReuser,
      double threshold,
      double external) {
    this.complianceRate = complianceDegree;
    this.income = income;
    this.houseType = houseType;
    this.outdoorEnduses = outdoor;
    this.indoorEnduses = indoor;
    this.defaultConsumption =
        (indoorEnduses.getIndoorDemand() + outdoorEnduses.getOutdoorDemand() * 31)
            * 3.06888328
            * Math.pow(10, -6);
    this.householdSize = householdSize;
    this.reservoir = reservoir;
    this.outputStream = outputStream;
    this.builtYear = builtYear;
    this.isRetrofitting = isRetrofitting;
    this.retrofitPeriod = retrofitPeriod;
    this.xCor = xCor;
    this.yCor = yCor;
    this.isWaterReuser = isWaterReuser;
    this.receiptTime = 0;
    this.threshold = threshold;
    this.external = external;
  }

  public void step(SimState state) {
    WRRSim wrrSim = (WRRSim) state;
    double time = wrrSim.schedule.getTime();

    PopulationGrowth popGrowth = wrrSim.populationGrowth;
    int month = popGrowth.getMonthNum();
    int days = popGrowth.getNumOfDays(month);

    double randomNum = wrrSim.random.nextDouble();

    if (isRetrofitting) {
      int currentYear = (int) (2014 + (time / 12));
      if ((this.builtYear + retrofitPeriod) <= currentYear) {

        double builtYearPrecentage;
        if (currentYear < 1990) {
          builtYearPrecentage = 62.67;
        } else if (currentYear < 2000) {
          builtYearPrecentage = 71.98;
        } else {
          builtYearPrecentage = 69.60;
        }
        if (randomNum * 100 <= builtYearPrecentage) {
          this.builtYear = currentYear;
          this.indoorEnduses.calculateAll(wrrSim, randomNum, this.householdSize, this.builtYear);
        }
      }
    }

    indoorDemand = indoorEnduses.getIndoorDemand() * days;

    if (isWaterReuser) { // && receiptTime + 3 < time){
      // outdoorEnduses.calculateOutdoorDemand(month, 0);
    } else {
      outdoorEnduses.calculateOutdoorDemand((int) time, conservationFactor);
    }
    // outdoorEnduses.calculateOutdoorDemand(month, conservationFactor);
    outdoorDemand = outdoorEnduses.getOutdoorDemand();
    defaultConsumption = (indoorDemand + outdoorDemand) * 3.06888328 * Math.pow(10, -6); // convert
    // to
    // acre-feet

    ArrayList<Double> agentDemands = new ArrayList<Double>();
    agentDemands.add(indoorDemand);
    agentDemands.add(outdoorDemand);
    agentDemands.add(defaultConsumption);
    agentDemands.add(1.0);
    agentDemands.add((double) householdSize);

    int index = (int) time;

    ArrayList<Double> tempArray = new ArrayList<Double>();
    double tempVar = 0;
    if (totalWithdrawal == null) {
      totalWithdrawal = new HashMap<Integer, ArrayList<Double>>();
    }
    if (totalWithdrawal.containsKey(index)) {
      tempArray = totalWithdrawal.get(index);
      for (int i = 0; i < tempArray.size(); i++) {
        tempVar = (double) tempArray.get(i);
        tempVar += (double) agentDemands.get(i);
        tempArray.set(i, tempVar);
      }
      totalWithdrawal.put(index, tempArray);
    } else {
      totalWithdrawal.put(index, agentDemands);
    }
  }

  public double getConsumption() {
    return defaultConsumption;
  }

  public static HashMap<Integer, ArrayList<Double>> getTotalConsumption() {
    return totalWithdrawal;
  }

  // public static void setConservationStage(int stage){
  // conservationStage = stage;
  // }

  public static void setConservationFactor(double factor) {
    conservationFactor = factor;
  }

  public static void setCity(SparseGrid2D theCity) {
    city = theCity;
  }

  public static void setSocialNet(Network socialNetwork) {
    socialNet = socialNetwork;
  }

  public boolean isWaterReuser() {
    return isWaterReuser;
  }
}
