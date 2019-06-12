import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;
import pertubing.DataList;
import pertubing.GenerateTimeseries;
import pertubing.Timeseries;
import pertubing.WrrProject;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.grid.Grid2D;
import sim.field.grid.SparseGrid2D;
import sim.field.network.Network;
import sim.util.Bag;
import sim.util.IntBag;

public class WRRSim extends SimState {
  static final long serialVersionUID = 1L;

  public SparseGrid2D city = new SparseGrid2D(570, 570);
  public Network socialNetwork = new Network(false);
  public PopulationGrowth populationGrowth = null;

  private static int numOfRun;
  private static int numOfManagementScenarios;
  private static int numOfShifts;
  private static int endTime;
  private static long randomseed;

  private String scenario = "enduse";
  private String climate = "DRY_new";

  private boolean isRetrofitting = true;
  private boolean isDroughtRestriction = true;
  private double[] reductionPercentages = {0.8, 0.6, 0.4, 0.2};
  private HashMap<Integer, double[]> droughtStages;
  private HashMap<Integer, double[]> recisionStages;
  private int retrofitPeriod = 30;

  private boolean isReleaseModel = true;
  private double lowestStorage = 25073; // acre-feet

  private boolean isReclaimed = false;
  private int neighborDist = 2;
  private double threshold = 0.5;

  private int runNum;
  public static ArrayList<HashMap<Integer, ArrayList<Double>>> finalResult =
      new ArrayList<HashMap<Integer, ArrayList<Double>>>();

  private double[] shift;
  private static double endShift;
  private double endShiftFac;
  private int manage;

  public WRRSim(
      long seed,
      int runNum,
      double endShiftFac,
      String climate,
      boolean r,
      boolean d,
      double[] per,
      HashMap<Integer, double[]> droughtStages,
      HashMap<Integer, double[]> recisionStages,
      int manage) {
    super(seed);
    this.runNum = runNum;
    this.endShiftFac = endShiftFac;
    this.climate = climate;
    this.isRetrofitting = r;
    this.isDroughtRestriction = d;
    this.reductionPercentages = per;
    this.droughtStages = droughtStages;
    this.recisionStages = recisionStages;
    this.manage = manage;
  }

  public double getPopulation() {
    Bag bag = socialNetwork.getAllNodes();
    return bag.size();
  }

  public double getReservoirStorage_model() {
    double storage = 0;
    Bag bag = socialNetwork.getAllNodes();
    if (bag.size() > 0) {
      storage = ((Reservoir) bag.get(1)).getStorage();
    }
    return storage;
  }

  public double getReservoirStorage_data() {
    double storage = 0;
    Bag bag = socialNetwork.getAllNodes();
    if (bag.size() > 0) {
      storage = ((FallsLake) bag.get(1)).getObservedStorage();
    }
    return storage;
  }

  public double getOutdoor_Demand() {
    int time = (int) schedule.getTime();
    HashMap<Integer, ArrayList<Double>> totalDemand = Household.getTotalConsumption();
    double totalOutdoor = (double) ((ArrayList<Double>) totalDemand.get(time)).get(1);
    double population = (double) ((ArrayList<Double>) totalDemand.get(time)).get(4);
    System.out.println("Population: " + population);

    int currentMonth = (int) (time % 12);
    int days = 0;

    switch (currentMonth) {
      case 1:
        days = 28;
        break;
      case 3:
        days = 30;
        break;
      case 5:
        days = 30;
        break;
      case 8:
        days = 30;
        break;
      case 10:
        days = 30;
        break;
      default:
        days = 31;
        break;
    }
    return totalOutdoor / (population * days);
  }

  public double getIndoor_Demand() {
    int time = (int) schedule.getTime();
    HashMap<Integer, ArrayList<Double>> totalDemand = Household.getTotalConsumption();
    double totalIndoor = (double) ((ArrayList<Double>) totalDemand.get(time)).get(0);
    double population = (double) ((ArrayList<Double>) totalDemand.get(time)).get(4);
    System.out.println("Population: " + population);

    int currentMonth = (int) (time % 12);
    ;
    int days = 0;

    switch (currentMonth) {
      case 1:
        days = 28;
        break;
      case 3:
        days = 30;
        break;
      case 5:
        days = 30;
        break;
      case 8:
        days = 30;
        break;
      case 10:
        days = 30;
        break;
      default:
        days = 31;
        break;
    }

    return totalIndoor / (population * days);
  }

  public boolean getIsDroughtRestriction() {
    return isDroughtRestriction;
  }

  public void setIsDroughtRestriction(boolean b) {
    isDroughtRestriction = b;
  }

  public boolean getIsRetrofitting() {
    return isRetrofitting;
  }

  public void setIsRetrofitting(boolean b) {
    isRetrofitting = b;
  }

  public boolean getIsReclaimed() {
    return isReclaimed;
  }

  public void setIsReclaimed(boolean b) {
    isReclaimed = b;
  }

  public boolean getIsReleaseModel() {
    return isReleaseModel;
  }

  public void setIsReleaseModel(boolean b) {
    isReleaseModel = b;
  }

  public int getDistance() {
    return neighborDist;
  }

  public void setDistance(int dist) {
    if (dist > 0) neighborDist = dist;
  }

  public double getThreshold() {
    return threshold;
  }

  public void setThreshold(double value) {
    if (value >= 0) threshold = value;
  }

  public double getLowestStorage() {
    return lowestStorage;
  }

  public void setLowestStorage(double value) {
    if (value >= 0) lowestStorage = value;
  }

  public int getRetrofitPeriod() {
    return retrofitPeriod;
  }

  public void setRetrofitPeriod(int period) {
    if (period > 0) retrofitPeriod = period;
  }

  public void start() {

    long ranSeed = this.seed();
    super.start();

    PrintWriter outputStream = null;
    try {
      outputStream = new PrintWriter(new FileWriter("Household-timeSeries.txt"));
    } catch (IOException e) {
      e.printStackTrace();
    }

    PrintWriter outputStreamPopGrowth = null;
    try {
      outputStreamPopGrowth = new PrintWriter(new FileWriter("population-timeSeries.txt"));
    } catch (IOException e) {
      e.printStackTrace();
    }

    PrintWriter outputStreamReservoir = null;
    try {
      String output = String.format("res-mgmt-%d-s-%.1f-%d.csv", manage, endShiftFac, runNum);
      outputStreamReservoir = new PrintWriter(new FileWriter(output));
    } catch (IOException e) {
      e.printStackTrace();
    }

    // clear the socialNetwork
    socialNetwork.clear();

    // clear the city
    city.clear();

    System.out.println("Generate timeseries.");
    Timeseries timeSeries = GenerateTimeseries.execute(shift, 81, manage, runNum, ranSeed);
    System.out.println("Timeseries generated.");

    DataList flow = timeSeries.getFlow();
    DataList precipitation = timeSeries.getPrecipitation();
    DataList evaporation = timeSeries.getEvapotranspiration();
    DataList shiftFac = timeSeries.getShiftFactor();

    // instead of 90626 for June 83 , this for January 83
    double initialStorage = 135985.83; // acre-feet
    double initialElevation = 251.86; // feet -> 251.86 is end elevation in Jan 2014
    Reservoir reservoir =
        new FallsLake(
            isReleaseModel,
            initialStorage,
            initialElevation,
            lowestStorage,
            readData("FALLSMSR_" + climate + ".txt"),
            readStageStorageData("elevationAreaStorage.txt"),
            outputStreamReservoir,
            flow,
            shiftFac);

    PolicyMaker policyMaker =
        new PolicyMaker(
            reservoir,
            initialElevation,
            initialStorage,
            isDroughtRestriction,
            reductionPercentages,
            droughtStages,
            recisionStages);

    Climate climate = new Climate();
    climate.setPrecipitation(precipitation);
    climate.setEvapotrans(evaporation);

    socialNetwork.addNode(policyMaker);
    socialNetwork.addNode(reservoir);

    // instead of pop for 80 , this is for January 2013
    int popSize = 316605; // 151951
    int totalNumOfHouseholds = (int) Math.round(popSize / 2.55);

    int sizeOfHousehold;
    Household user = null;

    int houseType = 0;
    double[] percentages = {10.5, 6.4, 13.8, 13.9, 17.7, 19.4, 8.9, 6.0, 1.6, 1.8};
    int[] incomeTable = {10, 15, 25, 35, 50, 75, 100, 150, 200, 500};
    for (int k = 1; k < 10; k++) {
      percentages[k] = percentages[k] + percentages[k - 1];
    }

    double totalArea = 1422941414; // square foot
    double householdArea = totalArea / totalNumOfHouseholds;
    // householdArea = 9908.54;
    double perviousAreaPercentage = 0;

    int builtYear = 0;
    double wateringFreq = 0;

    int xCor = 0;
    int yCor = 0;

    double complianceDegree = 1; // random.nextInt(5) * 0.25;
    double randomNum = random.nextDouble() * 100;

    for (int i = 0; i < totalNumOfHouseholds; i++) {
      complianceDegree = 1; // random.nextInt(5) * 0.25;
      randomNum = random.nextDouble() * 100;

      if (randomNum <= 75.71) {
        perviousAreaPercentage = 0.62;
      } else if (randomNum <= 90.96) {
        perviousAreaPercentage = 0.35;
      } else if (randomNum <= 93.56) {
        perviousAreaPercentage = 0.75;
      } else {
        perviousAreaPercentage = 0.8;
      }

      if (randomNum <= 22.8) {
        sizeOfHousehold = 1;
      } else if (randomNum <= 54.6) {
        sizeOfHousehold = 2;
      } else if (randomNum <= 70.1) {
        sizeOfHousehold = 3;
      } else if (randomNum <= 88.1) {
        sizeOfHousehold = 4;
      } else if (randomNum <= 97.0) {
        sizeOfHousehold = 5;
      } else if (randomNum <= 98.8) {
        sizeOfHousehold = 6;
      } else {
        sizeOfHousehold = (int) Math.round((-1 * Math.log(1 - (randomNum / 100.0))));
        // System.out.println((double) sizeOfHousehold);
      }

      if (randomNum <= 5.8) {
        houseType = 1;
      } else if (randomNum <= 67.5) {
        houseType = 2;
      } else {
        houseType = 0;
      }
      double p = 0;
      int index = 0;
      int income = 0;
      while (p < randomNum) {
        p = percentages[index];
        income = incomeTable[index];
        index++;
      }

      if (randomNum <= 8.71) {
        builtYear = (int) (1939 - random.nextLong(40)); // 1939 or
        // Earlier
      } else if (randomNum <= (16.27)) {
        builtYear = (int) (1940 + random.nextLong(10)); // 1940 to 1949
      } else if (randomNum <= (31.11)) {
        builtYear = (int) (1950 + random.nextLong(10)); // 1950 to 1959
      } else if (randomNum <= (55.34)) {
        builtYear = (int) (1960 + random.nextLong(10)); // 1960 to 1969
      } else if (randomNum <= (87)) {
        builtYear = (int) (1970 + random.nextLong(10)); // 1970 to 1979
      } else {
        builtYear = (int) (1980 + random.nextLong(4));
      }

      if (randomNum <= 30.2) {
        wateringFreq = 0.1;
      } else if (randomNum <= 72.5) {
        wateringFreq = 0.4; // 0.3
      } else {
        wateringFreq = 0.8; // 0.7
      }

      boolean isWarmCrop = true;
      if (randomNum <= 40.0) {
        isWarmCrop = false;
      }

      // wateringFreq = 1;

      int month = 0;
      double conservationFactor = 1;

      // xCor = i % endTime;
      // yCor = i / endTime;
      xCor = random.nextInt(city.getWidth());
      yCor = random.nextInt(city.getHeight());

      boolean isWaterReuser = false;

      if (isReclaimed && randomNum < 10) { // && xCor > city.getWidth() *
        // 0.4 && xCor <
        // city.getWidth() * 0.6 &&
        // yCor > city.getHeight() *
        // 0.4 && yCor <
        // city.getHeight() * 0.6){
        isWaterReuser = true;
      }
      double external = randomNum / 100.0;

      IndoorEnduses indoorEnduses =
          new IndoorEnduses(this, randomNum / 100.0, sizeOfHousehold, builtYear, scenario);

      OutdoorEnduses outdoorEnduses =
          new OutdoorEnduses(
              month,
              householdArea * perviousAreaPercentage,
              wateringFreq,
              isWarmCrop,
              conservationFactor,
              climate);

      user =
          new Household(
              sizeOfHousehold,
              income,
              houseType,
              complianceDegree,
              indoorEnduses,
              outdoorEnduses,
              reservoir,
              outputStream,
              builtYear,
              isRetrofitting,
              retrofitPeriod,
              xCor,
              yCor,
              isWaterReuser,
              threshold,
              external);

      city.setObjectLocation(user, xCor, yCor);
      // city.setObjectLocation(user, position);

      socialNetwork.addNode(user);
      // socialNetwork.addEdge(policyMaker, user, new Double(
      // complianceDegree));

      if (isReclaimed) {
        IntBag xPos = null;
        IntBag yPos = null;
        Bag neighbors = null;
        neighbors =
            city.getVonNeumannNeighbors(
                xCor, yCor, neighborDist, Grid2D.BOUNDED, neighbors, xPos, yPos);
        for (int j = 0; j < neighbors.size(); j++) {
          socialNetwork.addEdge(user, (Household) neighbors.get(j), 0.20); // random.nextDouble()
        }
      }

      // schedule.scheduleRepeating(user);
      // schedule.scheduleRepeating(0, 2, user, 1);
      schedule.scheduleRepeating(user, 2, 1);
    }

    // Household.setCity(city);
    Household.setSocialNet(socialNetwork);

    Bag bag = socialNetwork.getAllNodes();

    outputStreamPopGrowth.println(schedule.getTime() + " " + bag.size() + " " + popSize);

    populationGrowth =
        new PopulationGrowth(
            outputStreamPopGrowth,
            outputStream,
            scenario,
            isRetrofitting,
            retrofitPeriod,
            xCor,
            yCor,
            isReclaimed,
            neighborDist,
            threshold,
            climate);

    // schedule.scheduleRepeating(0, populationGrowth, 1);
    // schedule.scheduleRepeating(0, 1, populationGrowth, 1);
    schedule.scheduleRepeating(populationGrowth, 1, 1);

    // schedule.scheduleRepeating(0, (Steppable) reservoir, 1);
    // schedule.scheduleRepeating(0, 2, (Steppable) reservoir, 1);
    schedule.scheduleRepeating((Steppable) reservoir, 3, 1);

    // schedule.scheduleRepeating(0, policyMaker, 1);
    schedule.scheduleRepeating(policyMaker, 4, 1);
  }

  public static double[] spacedArray(double min, double max, int points) {
    double[] spaced = new double[points];
    for (int i = 0; i < points; i++) {
      spaced[i] = min + i * (max - min) / (points - 1);
    }
    return spaced;
  }

  public WRRSim setShift(double number) {
    this.shift = spacedArray(1, number, ((81 - WrrProject.time.length) * 12 + 1));
    return this;
  }

  public HashMap<Integer, ArrayList<Double>> readData(String fileName) {
    int month = 0;
    HashMap<Integer, ArrayList<Double>> map = new HashMap<Integer, ArrayList<Double>>();
    ArrayList<Double> list = new ArrayList<Double>();
    Scanner s = null;
    try {
      s = new Scanner(new BufferedReader(new FileReader(fileName)));
      String comment = null;
      for (int i = 0; i < 27; i++) { // for omitting first 27 lines
        comment = s.nextLine();
      }
      while (s.hasNext()) {
        comment = s.nextLine();
        String[] splitedCmt = comment.split("\\s+");

        list.add(Double.parseDouble(splitedCmt[1]));
        list.add(Double.parseDouble(splitedCmt[2]));
        list.add(Double.parseDouble(splitedCmt[3]));
        list.add(Double.parseDouble(splitedCmt[4]));
        list.add(Double.parseDouble(splitedCmt[5]));
        list.add(Double.parseDouble(splitedCmt[6]));
        list.add(Double.parseDouble(splitedCmt[7]));
        list.add(Double.parseDouble(splitedCmt[8]));
        list.add(Double.parseDouble(splitedCmt[9]));
        list.add(Double.parseDouble(splitedCmt[10]));
        list.add(Double.parseDouble(splitedCmt[11]));
        list.add(Double.parseDouble(splitedCmt[12]));

        map.put(month, list);
        month++;
        list = new ArrayList<Double>();
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (s != null) {
        s.close();
      }
    }
    return map;
  }

  public ArrayList<ArrayList<Double>> readStageStorageData(String fileName) {

    ArrayList<ArrayList<Double>> list = new ArrayList<ArrayList<Double>>();
    ArrayList<Double> storageList = new ArrayList<Double>();
    ArrayList<Double> elevationList = new ArrayList<Double>();
    ArrayList<Double> areaList = new ArrayList<Double>();

    Scanner s = null;
    try {
      s = new Scanner(new BufferedReader(new FileReader(fileName)));
      while (s.hasNext()) {
        String comment = s.nextLine();
        String[] splitedCmt = comment.split("\\s+");

        elevationList.add(Double.parseDouble(splitedCmt[0]));
        areaList.add(Double.parseDouble(splitedCmt[1]));
        storageList.add(Double.parseDouble(splitedCmt[2]));
      }
      list.add(elevationList);
      list.add(areaList);
      list.add(storageList);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (s != null) {
        s.close();
      }
    }
    return list;
  }

  public static void main(String[] args) {
    System.out.println("Starting simulation");
    long t1 = System.currentTimeMillis();

    numOfShifts = 8; // 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.9, 1.0
    numOfRun = 50;
    numOfManagementScenarios = 4;
    endTime = 600;

    SimState state = null;

    String climate = "DRY_new";
    boolean[] isRetrofitting = {false, true, true, true, true, true};
    boolean[] isDroughtRestriction = {false, false, true, true, true, true};
    ArrayList<double[]> percentages = new ArrayList<double[]>();
    double[] reductionPercentages1 = {0.6, 0.4, 0.2, 0.0};
    double[] reductionPercentages2 = {0.7, 0.5, 0.3, 0.1};
    double[] reductionPercentages3 = {0.8, 0.6, 0.4, 0.2};
    double[] reductionPercentages4 = {0.9, 0.7, 0.5, 0.3};
    percentages.add(reductionPercentages1); // Sc 1
    percentages.add(reductionPercentages1); // Sc 2
    percentages.add(reductionPercentages1); // Sc 3
    percentages.add(reductionPercentages2);
    percentages.add(reductionPercentages3);
    percentages.add(reductionPercentages4);

    HashMap<Integer, double[]> droughtStages = new HashMap<Integer, double[]>();
    HashMap<Integer, double[]> recisionStages = new HashMap<Integer, double[]>();

    droughtStages.put(1, new double[]{.40, .30, .25});
    droughtStages.put(2, new double[]{.50, .35, .25});
    droughtStages.put(3, new double[]{.65, .45, .30});
    droughtStages.put(4, new double[]{.85, .60, .35});
    droughtStages.put(5, new double[]{.75, .55, .35});
    droughtStages.put(6, new double[]{.65, .45, .30});
    droughtStages.put(7, new double[]{.55, .45, .25});
    droughtStages.put(8, new double[]{.50, .40, .25});
    droughtStages.put(9, new double[]{.45, .35, .25});
    droughtStages.put(10, new double[]{.40, .30, .25});
    droughtStages.put(11, new double[]{.35, .30, .25});
    droughtStages.put(12, new double[]{.35, .30, .25});

    recisionStages.put(1, new double[]{.60, .50, .45});
    recisionStages.put(2, new double[]{.70, .55, .45});
    recisionStages.put(3, new double[]{.85, .55, .45});
    recisionStages.put(4, new double[]{1.00, .80, .55});
    recisionStages.put(5, new double[]{.95, .75, .55});
    recisionStages.put(6, new double[]{.85, .65, .50});
    recisionStages.put(7, new double[]{.75, .65, .50});
    recisionStages.put(8, new double[]{.70, .60, .45});
    recisionStages.put(9, new double[]{.65, .55, .45});
    recisionStages.put(10, new double[]{.60, .50, .45});
    recisionStages.put(11, new double[]{.55, .50, .45});
    recisionStages.put(12, new double[]{.55, .50, .45});

    for (int n = 3; n < numOfManagementScenarios; n++) {
      for (int m = 7; m < numOfShifts; m++) {
        endShift = 0.1 * (m + 1);
        finalResult = new ArrayList<HashMap<Integer, ArrayList<Double>>>();
        for (int i = 0; i < numOfRun; i++) {
          long ranSeed = System.currentTimeMillis();
          int z = n + 1;
          System.out.printf("Trial " + i + " Management Scenario " + z + " Shift %.1f\n", endShift);
          System.out.println("Seed: " + Long.toString(ranSeed));
          state =
              (new WRRSim(
                      ranSeed,
                      i,
                      endShift,
                      climate,
                      isRetrofitting[n],
                      isDroughtRestriction[n],
                      percentages.get(n),
                      droughtStages,
                      recisionStages,
                      z))
                  .setShift(endShift);
          state.start();
          do if (!state.schedule.step(state)) break;
          while (state.schedule.getSteps() < endTime);
          Household.totalWithdrawal = null;
          Household.conservationFactor = 1;
          FallsLake.finalResultMap = new HashMap<Integer, ArrayList<Double>>();
          state.finish();
        }

        PrintWriter outputStream = null;
        try {
          String output = String.format("final-result-s-%.1f-sc-%d.csv", endShift, n + 1);
          outputStream = new PrintWriter(new FileWriter(output));
        } catch (IOException e) {
          e.printStackTrace();
        }
        HashMap<Integer, ArrayList<Double>> runMap = new HashMap<Integer, ArrayList<Double>>();
        ArrayList<Double> mapList = new ArrayList<Double>();

        outputStream.print(
            "storage_mean"
                + ","
                + "storage_std"
                + ","
                + "storage_rsd"
                + ","
                + "outflow_mean"
                + ","
                + "outflow_std"
                + ","
                + "outflow_rsd"
                + ","
                + "waterSupply_mean"
                + ","
                + "waterSupply_std"
                + ","
                + "waterSupply_rsd"
                + ","
                + "waterDelivered_mean"
                + ","
                + "waterDelivered_std"
                + ","
                + "waterDelivered_rsd"
                + ","
                + "elevation_mean"
                + ","
                + "elevation_std"
                + ","
                + "elevation_rsd"
                + ","
                + "totalIndoor_mean"
                + ","
                + "totalIndoor_std"
                + ","
                + "totalIndoor_rsd"
                + ","
                + "totalOutdoor_mean"
                + ","
                + "totalOutdoor_std"
                + ","
                + "totalOutdoor_rsd"
                + ","
                + "numOfHouseholds_mean"
                + ","
                + "numOfHouseholds_std"
                + ","
                + "numOfHouseholds_rsd"
                + ","
                + "population_mean"
                + ","
                + "population_std"
                + ","
                + "population_rsd"
                + ","
                + "inflow_mean"
                + ","
                + "inflow_std"
                + ","
                + "inflow_rsd"
                + ","
                + "deficit_mean"
                + ","
                + "deficit_std"
                + ","
                + "deficit_rsd"
                + ",");
        outputStream.print(
            "storage_max"
                + ","
                + "outflow_max"
                + ","
                + "waterSupply_max"
                + ","
                + "waterDelivered_max"
                + ","
                + "elevation_max"
                + ","
                + "totalIndoor_max"
                + ","
                + "totalOutdoor_max"
                + ","
                + "numOfHouseholds_max"
                + ","
                + "population_max"
                + ","
                + "inflow_max"
                + ","
                + "deficit_max"
                + ",");
        outputStream.print(
            "storage_min"
                + ","
                + "outflow_min"
                + ","
                + "waterSupply_min"
                + ","
                + "waterDelivered_min"
                + ","
                + "elevation_min"
                + ","
                + "totalIndoor_min"
                + ","
                + "totalOutdoor_min"
                + ","
                + "numOfHouseholds_min"
                + ","
                + "population_min"
                + ","
                + "inflow_min"
                + ","
                + "deficit_min"
                + ",");
        outputStream.println(
            "shiftFactor"
                + ","
                + "reliability"
                + ","
                + "resilience"
                + ","
                + "sumDeficit"
                + ","
                + "nonzerodeficit"
                + ","
                + "maxDeficit"
                + ","
                + "averageDemand"
                + ","
                + "sustainabilityIndex");

        for (int j = 0; j < endTime; j++) {

          ArrayList<Double> storage = new ArrayList<Double>();
          ArrayList<Double> outflow = new ArrayList<Double>();
          ArrayList<Double> waterSupply = new ArrayList<Double>();
          ArrayList<Double> waterDelivered = new ArrayList<Double>();
          ArrayList<Double> elevation = new ArrayList<Double>();
          ArrayList<Double> totalIndoor = new ArrayList<Double>();
          ArrayList<Double> totalOutdoor = new ArrayList<Double>();
          ArrayList<Double> numOfHouseholds = new ArrayList<Double>();
          ArrayList<Double> population = new ArrayList<Double>();
          ArrayList<Double> shiftFac = new ArrayList<Double>();
          ArrayList<Double> inflow = new ArrayList<Double>();
          ArrayList<Double> deficit = new ArrayList<Double>();

          ArrayList<Double> mean = new ArrayList<Double>();
          ArrayList<Double> std = new ArrayList<Double>();
          ArrayList<Double> rsd = new ArrayList<Double>();
          ArrayList<Double> max = new ArrayList<Double>();
          ArrayList<Double> min = new ArrayList<Double>();
          double std_temp = 0;
          double sd = 0;
          double mean_temp = 0;
          ArrayList<Double> reliability = new ArrayList<Double>();
          ArrayList<Double> resilience = new ArrayList<Double>();
          ArrayList<Double> sumDeficit = new ArrayList<Double>();
          ArrayList<Double> nonzerodeficit = new ArrayList<Double>();
          ArrayList<Double> maxDeficit = new ArrayList<Double>();
          ArrayList<Double> averageDemand = new ArrayList<Double>();
          ArrayList<Double> sustainabilityindex = new ArrayList<Double>();

          for (int i = 0; i < finalResult.size(); i++) {
            reliability.add(
                (Double) ((ArrayList<Double>) finalResult.get(i).get(endTime - 1)).get(12));
            resilience.add(
                (Double) ((ArrayList<Double>) finalResult.get(i).get(endTime - 1)).get(13));
            sumDeficit.add(
                (Double) ((ArrayList<Double>) finalResult.get(i).get(endTime - 1)).get(14));
            nonzerodeficit.add(
                (Double) ((ArrayList<Double>) finalResult.get(i).get(endTime - 1)).get(15));
            maxDeficit.add(
                (Double) ((ArrayList<Double>) finalResult.get(i).get(endTime - 1)).get(16));
            averageDemand.add(
                (Double) ((ArrayList<Double>) finalResult.get(i).get(endTime - 1)).get(17));
            sustainabilityindex.add(
                (Double) ((ArrayList<Double>) finalResult.get(i).get(endTime - 1)).get(18));
          }

          for (int i = 0; i < finalResult.size(); i++) {
            runMap = finalResult.get(i);
            mapList = (ArrayList<Double>) runMap.get(j);

            storage.add((Double) mapList.get(0));
            outflow.add((Double) mapList.get(1));
            waterSupply.add((Double) mapList.get(2));
            waterDelivered.add((Double) mapList.get(3));
            elevation.add((Double) mapList.get(4));
            totalIndoor.add((Double) mapList.get(5));
            totalOutdoor.add((Double) mapList.get(6));
            numOfHouseholds.add((Double) mapList.get(7));
            population.add((Double) mapList.get(8));
            shiftFac.add((Double) mapList.get(9));
            inflow.add((Double) mapList.get(10));
            deficit.add((Double) mapList.get(11));
          }

          mean.add(sum(storage) / finalResult.size());
          mean.add(sum(outflow) / finalResult.size());
          mean.add(sum(waterSupply) / finalResult.size());
          mean.add(sum(waterDelivered) / finalResult.size());
          mean.add(sum(elevation) / finalResult.size());
          mean.add(sum(totalIndoor) / finalResult.size());
          mean.add(sum(totalOutdoor) / finalResult.size());
          mean.add(sum(numOfHouseholds) / finalResult.size());
          mean.add(sum(population) / finalResult.size());
          mean.add(sum(inflow) / finalResult.size());
          mean.add(sum(deficit) / finalResult.size());

          max.add(Collections.max(storage));
          max.add(Collections.max(outflow));
          max.add(Collections.max(waterSupply));
          max.add(Collections.max(waterDelivered));
          max.add(Collections.max(elevation));
          max.add(Collections.max(totalIndoor));
          max.add(Collections.max(totalOutdoor));
          max.add(Collections.max(numOfHouseholds));
          max.add(Collections.max(population));
          max.add(Collections.max(inflow));
          max.add(Collections.max(deficit));

          min.add(Collections.min(storage));
          min.add(Collections.min(outflow));
          min.add(Collections.min(waterSupply));
          min.add(Collections.min(waterDelivered));
          min.add(Collections.min(elevation));
          min.add(Collections.min(totalIndoor));
          min.add(Collections.min(totalOutdoor));
          min.add(Collections.min(numOfHouseholds));
          min.add(Collections.min(population));
          min.add(Collections.min(inflow));
          min.add(Collections.min(deficit));

          for (int k = 0; k < 11; k++) {
            for (int i = 0; i < finalResult.size(); i++) {
              runMap = finalResult.get(i);
              mapList = (ArrayList<Double>) runMap.get(j);
              sd += Math.pow((Double) mapList.get(k) - (Double) mean.get(k), 2);
            }
            mean_temp = mean.get(k);
            std_temp = Math.sqrt(sd / (finalResult.size() - 1));
            std.add(std_temp);
            rsd.add(std_temp / mean_temp * 100);
            sd = 0;
          }
          for (int k = 0; k < mean.size(); k++) {
            outputStream.print(mean.get(k) + ",");
            outputStream.print(std.get(k) + ",");
            outputStream.print(rsd.get(k) + ",");
          }
          for (int i = 0; i < max.size(); i++) {
            outputStream.print(max.get(i) + ",");
          }
          for (int i = 0; i < min.size(); i++) {
            outputStream.print(min.get(i) + ",");
          }
          outputStream.print(shiftFac.get(0) + ",");
          if (j < sustainabilityindex.size()) {
            outputStream.print(reliability.get(j) + ",");
            outputStream.print(resilience.get(j) + ",");
            outputStream.print(sumDeficit.get(j) + ",");
            outputStream.print(nonzerodeficit.get(j) + ",");
            outputStream.print(maxDeficit.get(j) + ",");
            outputStream.print(averageDemand.get(j) + ",");
            outputStream.print(sustainabilityindex.get(j) + ",");
          }
          outputStream.println();
        }

        outputStream.close();
      }
    }
    long t2 = System.currentTimeMillis();
    System.out.println("Total time:  " + (t2 - t1) * 1E-3 + " secs");

    System.exit(0);
  }

  public static double sum(ArrayList<Double> m) {
    double sum = 0;
    for (int i = 0; i < m.size(); i++) {
      sum += m.get(i);
    }
    return sum;
  }
}
