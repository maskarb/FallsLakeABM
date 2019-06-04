import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import sim.engine.SimState;
import sim.engine.Steppable;
// import sim.field.continuous.Continuous2D;
import sim.field.grid.SparseGrid2D;
import sim.util.Bag;
import sim.util.IntBag;
// import sim.field.grid.Grid2D;
// import sim.field.network.Edge;
import sim.field.network.Network;

/**
 * 
 */

/**
 * @author amashha
 * 
 */
public class Household implements Steppable {

	/**
	 * 
	 */
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
	public static HashMap<Integer, ArrayList<Double>> totalWithdrawal;// = new
																		// HashMap<>();
	// public static double popCounter;
	private int builtYear;
	// public static int conservationStage;
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

	public Household(int householdSize, int income, int houseType, double complianceDegree, double consumption,
			Reservoir reservoir) {
		this.complianceRate = complianceDegree;
		this.income = income;
		this.houseType = houseType;
		this.defaultConsumption = consumption;
		this.householdSize = householdSize;
		this.reservoir = reservoir;

	}

	public Household(int householdSize, int income, int houseType, double complianceDegree, IndoorEnduses indoor,
			OutdoorEnduses outdoor, Reservoir reservoir, PrintWriter outputStream, int builtYear,
			boolean isRetrofitting, int retrofitPeriod, int xCor, int yCor, boolean isWaterReuser, double threshold,
			double external) {
		this.complianceRate = complianceDegree;
		this.income = income;
		this.houseType = houseType;

		this.outdoorEnduses = outdoor;
		this.indoorEnduses = indoor;

		this.defaultConsumption = (indoorEnduses.getIndoorDemand() + outdoorEnduses.getOutdoorDemand() * 31)
				* 3.06888328 * Math.pow(10, -6);

		this.householdSize = householdSize;
		this.reservoir = reservoir;

		// totalWithdrawal = new double[365];

		this.outputStream = outputStream;

		this.builtYear = builtYear;

		// conservationStage = 0;
		// conservationFactor = 1;

		this.isRetrofitting = isRetrofitting;
		this.retrofitPeriod = retrofitPeriod;

		this.xCor = xCor;
		this.yCor = yCor;
		// city.getVonNeumannNeighbors(xCor, yCor, 1, Grid2D.BOUNDED, neighbors,
		// xPos, yPos);
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

		// double tempIndoorDemand = 0;

		// boolean isRetrofitting = true;

		if (isRetrofitting) {
			int currentYear = (int) (2014 + (time / 12));
			if ((this.builtYear + retrofitPeriod) <= currentYear) {

				// this.builtYear = currentYear;
				// this.indoorEnduses.calculateAll(wrrSim, randomNum,
				// this.householdSize, this.builtYear);

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

		// conservationStage = PolicyMaker.getStage();

		// indoorEnduses.calculateAll(wrrSim, randomNum, householdSize);
		// indoorEnduses.calculateAll(wrrSim, randomNum, householdSize,
		// builtYear);
		// totalIndoorDemand = indoorEnduses.getIndoorDemand() * days;
		//
		// defaultConsumption = (outdoorEnduses.getOutdoorDemand() +
		// totalIndoorDemand)
		// * 3.06888328 * Math.pow(10, -6);// convert
		// to
		// acre-feet

		indoorDemand = indoorEnduses.getIndoorDemand() * days;

		// outdoorEnduses.calculateOutdoorDemand(month);
		// System.out.println(conservationFactor);
		if (isWaterReuser) {// && receiptTime + 3 < time){
			// outdoorEnduses.calculateOutdoorDemand(month, 0);
		} else {
			outdoorEnduses.calculateOutdoorDemand((int) time, conservationFactor);
		}
		// outdoorEnduses.calculateOutdoorDemand(month, conservationFactor);
		outdoorDemand = outdoorEnduses.getOutdoorDemand();
		defaultConsumption = (indoorDemand + outdoorDemand) * 3.06888328 * Math.pow(10, -6);// convert
																							// to
																							// acre-feet

		ArrayList<Double> agentDemands = new ArrayList<Double>();
		agentDemands.add(indoorDemand);
		agentDemands.add(outdoorDemand);
		agentDemands.add(defaultConsumption);
		agentDemands.add(1.0);
		agentDemands.add((double) householdSize);
		// if (!isWaterReuser) {
		// agentDemands.add(0.0);
		// } else {
		// agentDemands.add(1.0);
		// }

		// reservoir.withdraw(defaultConsumption);

		int index = (int) time;
		// totalWithdrawal[index] += defaultConsumption;

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

		// }

		// double utility = 0.0;
		// double alpha = 0;
		// double beta = 1;
		// if (!isWaterReuser) {
		// Bag relations = socialNet.getEdgesIn(this);
		// double numOfNeighbors = 0;
		// double influence = 0;
		// for (int i = 0; i < relations.size(); i++) {
		// Edge e = (Edge) (relations.get(i));
		//
		// if (( (Household) e.getOtherNode(this)).isWaterReuser()) {
		// numOfNeighbors += 1;
		// influence += ((Double) (e.info)).doubleValue();
		// }
		// }
		// double percentOfNeighbors = numOfNeighbors / relations.size();
		//// if (numOfNeighbors != 0 && (influence / relations.size() ) >
		// threshold){
		// utility = alpha * external + beta * (influence);
		// if (utility > threshold){
		//// if (percentOfNeighbors > threshold){
		//
		//// if ((influence / relations.size()) > 0.1 && percentOfNeighbors >
		// 0.5) {
		// isWaterReuser = true;
		// receiptTime = time;
		// }
		// }
		// } else {
		// Bag relations = socialNet.getEdgesOut(this);
		// for (int i = 0; i < relations.size(); i++) {
		// Edge e = (Edge) (relations.get(i));
		// e.setInfo(((Double) (e.info)).doubleValue() + 0.0015);
		// }
		// }

		// if (time == 0) {
		// System.out.println("Totalwithdrawal " + totalWithdrawal[1]);
		// outputStream.println("consumption " + defaultConsumption
		// / (3.06888328 * Math.pow(10, -6)));
		// }

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
