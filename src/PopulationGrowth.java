import java.io.PrintWriter;
import java.util.HashMap;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.continuous.Continuous2D;
import sim.field.grid.Grid2D;
//import sim.field.continuous.Continuous2D;
import sim.field.grid.SparseGrid2D;
import sim.field.network.Network;
import sim.util.Bag;
import sim.util.Double2D;
import sim.util.IntBag;

/**
 * 
 */

/**
 * @author amashha
 * 
 */
public class PopulationGrowth implements Steppable {

	private double householdTracker = 0;
	public PrintWriter outputStreamPopGrowth;
	public PrintWriter outputStreamHousehold;
	private double increasedPop;
	private int totalNumOfHouseholds;
	private double aveSize;
	private double increasedArea;
	private double householdArea;

	private int oldTotalNumOfHouseHold;

	private int numofDays;
	private int monthNum;

	private String scenario;
	private boolean isRetrofitting;
	private int retrofitPeriod;

	private int xCor;
	private int yCor;

	private boolean isReclaimed;
	private int neighborDist;
	private double threshold;

	private Climate climate;

	public PopulationGrowth(PrintWriter outputStreamPopGrowth, PrintWriter outputStreamHousehold, String scenario,
			boolean isRetrofitting, int retrofitPeriod, int xCor, int yCor, boolean isReclaimed, int neighborDist,
			double threshold, Climate climate) {

		this.outputStreamPopGrowth = outputStreamPopGrowth;
		this.outputStreamHousehold = outputStreamHousehold;

		this.scenario = scenario;
		this.isRetrofitting = isRetrofitting;
		this.retrofitPeriod = retrofitPeriod;

		this.climate = climate;

		this.xCor = xCor;
		this.yCor = yCor;

		this.isReclaimed = isReclaimed;
		this.neighborDist = neighborDist;
		this.threshold = threshold;
	}

	public void calculateNumOfDays(int month) {
		int currentMonth = month;
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
		numofDays = days;
	}

	public int getNumOfDays(int month) {
		return numofDays;
	}

	public int getMonthNum() {
		return monthNum;
	}

	public void setMonthNum(int month) {
		monthNum = month;
	}

	public void step(SimState state) {
		WRRSim wrrSim = (WRRSim) state;
		Network socialNetwork = wrrSim.socialNetwork;

		SparseGrid2D city = wrrSim.city;
		// Continuous2D city = wrrSim.city;
		// Reservoir reservoir = wrrSim.reservoir;

		Bag bag = socialNetwork.getAllNodes();

		Reservoir reservoir = (Reservoir) bag.get(1);

		// int[] year = { 1990, 2000, 2010, 2020, 2030, 2040 };
		int[] year = { 1993, 2003, 2013, 2023, 2033 };
	
		int[] population = { 151951, 213498, 316605, 483253, 638544, 799142 };

		double[] area = { 692123097.6, 1121157734, 1422941414, 1688544507, 2870094335.0, 3416725186.0 };
		// double householdArea = 0;

		HashMap<Integer, Double> averageSize = new HashMap<Integer, Double>();

		// averageSize.put(1930, 3.67);
		// averageSize.put(1950, 3.37);
		// averageSize.put(1960, 3.33);
		// averageSize.put(1970, 3.14);
		// averageSize.put(1980, 2.76);
		averageSize.put(1993, 2.56);
		averageSize.put(2003, 2.54);
		averageSize.put(2013, 2.55);
		averageSize.put(2023, 2.55);
		averageSize.put(2033, 2.55);
		// averageSize.put(2040, 2.63);
		// int increasedPop = 0;
		// int totalNumOfHouseholds = 0;
		// double aveSize = 0;

		int time = (int) wrrSim.schedule.getTime();
		monthNum = (int) (time % 12);
		calculateNumOfDays(monthNum);

		int g = (int) (time / 120);
		boolean garbage = true;
		if (!garbage) {
			if ((time % 120) == 0) {

				increasedPop = (population[g + 1] - population[g]) / 120;
				aveSize = averageSize.get(year[g]);

				increasedArea = (area[g + 1] - area[g]) / 120;

				// System.out.println("area " + householdArea
				// + "num of households " + totalNumOfHouseholds * 120);
			}

			totalNumOfHouseholds = (int) Math.round(increasedPop / aveSize);
			householdArea = increasedArea / totalNumOfHouseholds;
			System.out.println(householdArea + " " + totalNumOfHouseholds);

		} else {
			// Ehsan added here-- basically increasedPop is the population.
			// Didn't change the name. Same logic for increased Area
			increasedPop = (population[g + 1] - population[g]) * ((time - g * 120) / 120d) + population[g];

			aveSize = averageSize.get(year[g]);
			totalNumOfHouseholds = (int) Math.round(increasedPop / aveSize);
			increasedArea = (area[g + 1] - area[g]) * ((time - g * 120) / 120d) + area[g];
			householdArea = increasedArea / totalNumOfHouseholds;
			if (oldTotalNumOfHouseHold == 0) {
				double b = population[g] / aveSize;
				oldTotalNumOfHouseHold = (int) Math.round(b);
			}

			// householdArea = 9908.54;

			if (householdTracker != totalNumOfHouseholds && !garbage) {
				System.out.println(householdArea + " at time " + time + " " + increasedPop + " "
						+ (totalNumOfHouseholds - oldTotalNumOfHouseHold) + " " + totalNumOfHouseholds + " "
						+ (population[g + 1] - population[g]) + " " + population[g] + " " + population[g + 1] + " ");
				householdTracker = totalNumOfHouseholds;
			}
			// System.out.println(householdArea + " at time " + time + " " +
			// totalNumOfHouseholds + " " + increasedArea
			// + " g " + g + " " + population[g + 1] + " " + ((time - g * 120) /
			// 120d));
		}

		double perviousAreaPercentage = 0;

		// increasedPop = population[g + 1] - population[g];
		// aveSize = averageSize.get(year[g]);
		// totalNumOfHouseholds = (int) Math.round(increasedPop / aveSize);

		// int singleHouseholds = (int) Math.round(totalNumOfHouseholds *
		// 0.328);
		// int coupleHouseholds = (int) (Math.round(totalNumOfHouseholds * 0.441
		// - singleHouseholds) + Math.round(totalNumOfHouseholds * 0.384));
		// int restOfPop = increasedPop - singleHouseholds - 2 *
		// coupleHouseholds;
		// int restOfHouseholds = totalNumOfHouseholds - singleHouseholds
		// - coupleHouseholds;
		// double averageHouseholdSize = restOfPop / restOfHouseholds;

		int sizeOfHousehold;
		Household user = null;
		// double defaultConsumption = 0;
		int houseType = 0;
		double[] percentages = { 10.5, 6.4, 13.8, 13.9, 17.7, 19.4, 8.9, 6.0, 1.6, 1.8 };
		int[] incomeTable = { 10, 15, 25, 35, 50, 75, 100, 150, 200, 500 };
		for (int k = 1; k < 10; k++) {
			percentages[k] = percentages[k] + percentages[k - 1];
		}

		int builtYear = 1983 + (time / 12);

		double wateringFreq = 0;

		int newXCor = 0;
		int newYCor = 0;

		double complianceDegree = 1;// wrrSim.random.nextInt(5) * 0.25;
		double randomNum = wrrSim.random.nextDouble() * 100;

		for (int i = 0; i < totalNumOfHouseholds - oldTotalNumOfHouseHold; i++) {
			complianceDegree = 1;// wrrSim.random.nextInt(5) * 0.25;
			randomNum = wrrSim.random.nextDouble() * 100;

			if (randomNum <= 75.71) {
				perviousAreaPercentage = 0.62;
			} else if (randomNum <= 90.96) {
				perviousAreaPercentage = 0.35;
			} else if (randomNum <= 93.56) {
				perviousAreaPercentage = 0.75;
			} else {
				perviousAreaPercentage = 0.8;
			}

			// if (randomNum <= 32.8) {
			// sizeOfHousehold = 1;
			// } else if (randomNum <= 76.9) {
			// sizeOfHousehold = 2;
			// } else {
			// sizeOfHousehold = (int) Math
			// .round((-averageHouseholdSize * Math.log(1 - (randomNum /
			// 100.0))));
			// }
			// if (randomNum <= 29.32) {
			// sizeOfHousehold = 1;
			// } else if (randomNum <= 63.81) {
			// sizeOfHousehold = 2;
			// } else if (randomNum <= 79.35) {
			// sizeOfHousehold = 3;
			// } else if (randomNum <= 92.86) {
			// sizeOfHousehold = 4;
			// } else if (randomNum <= 97.80) {
			// sizeOfHousehold = 5;
			// } else if (randomNum <= 99.93) {
			// sizeOfHousehold = 6;
			// } else {
			// sizeOfHousehold = (int) Math
			// .round((-1 * Math.log(1 - (randomNum / 100.0))));
			// //System.out.println((double) sizeOfHousehold);
			// }
			if (randomNum <= 22.8) {
				sizeOfHousehold = 1;
			} else if (randomNum <= 54.6) {
				sizeOfHousehold = 2;
			} else if (randomNum <= 77.6) {
				sizeOfHousehold = 3;
			} else if (randomNum <= 92.1) {
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

			if (randomNum <= 30.2) {
				wateringFreq = 0.1;
			} else if (randomNum <= 72.5) {
				wateringFreq = 0.4;// 0.3
			} else {
				wateringFreq = 0.8;// 0.7
			}

			boolean isWarmCrop = true;
			if (randomNum <= 40.0) {
				isWarmCrop = false;
			}

			// String scenario = "prob";
			// wateringFreq = 1;

			double conservationFactor = 1;

			// newXCor = (xCor + i) % 600;
			// newYCor = yCor + (xCor + i) / 600; //(bag.size() - 2 + i) / 600;
			// newXCor = wrrSim.random.nextInt(570);
			// newYCor = wrrSim.random.nextInt(570);
			// Double2D position = new Double2D(city.getWidth() * 0.5 +
			// wrrSim.random.nextInt(300),city.getHeight() * 0.5 +
			// wrrSim.random.nextInt(300));

			// System.out.println(newXCor + " " + newYCor);

			boolean isWaterReuser = false;
			// if(isReclaimed && randomNum < 30){
			// isWaterReuser = true;
			// }
			double external = randomNum / 100.0;

			IndoorEnduses indoorEnduses = new IndoorEnduses(wrrSim, randomNum / 100.0, sizeOfHousehold, builtYear,
					scenario);
			// OutdoorEnduses outdoorEnduses = new OutdoorEnduses(time,
			// householdArea, wateringFreq, isWarmCrop);
			// System.out.println("household " + householdArea);
			OutdoorEnduses outdoorEnduses = new OutdoorEnduses(time, householdArea * perviousAreaPercentage,
					wateringFreq, isWarmCrop, conservationFactor, climate);
			user = new Household(sizeOfHousehold, income, houseType, complianceDegree, indoorEnduses, outdoorEnduses,
					reservoir, outputStreamHousehold, builtYear, isRetrofitting, retrofitPeriod, newXCor, newYCor,
					isWaterReuser, threshold, external);

			city.setObjectLocation(user, newXCor, newYCor);
			// city.setObjectLocation(user, position);

			socialNetwork.addNode(user);
			// PolicyMaker policyMaker = (PolicyMaker) bag.get(0);
			// if (policyMaker != null) {
			// socialNetwork.addEdge(policyMaker, user, new Double(
			// complianceDegree));
			// }

			if (isReclaimed) {
				IntBag xPos = null;
				IntBag yPos = null;
				Bag neighbors = null;
				neighbors = city.getVonNeumannNeighbors(newXCor, newYCor, neighborDist, Grid2D.BOUNDED, neighbors, xPos,
						yPos);
				// System.out.println(neighbors.size());
				for (int j = 0; j < neighbors.size(); j++) {
					socialNetwork.addEdge(user, neighbors.get(j), new Double(0.25));// wrrSim.random.nextDouble()
				}
			}

			// wrrSim.schedule.scheduleRepeating(user);
			// schedule.scheduleRepeating(0, 2, user, 1);
			wrrSim.schedule.scheduleRepeating(user, 2, 1);

		}
		// Ehsan
		oldTotalNumOfHouseHold = totalNumOfHouseholds;

		// xCor = (bag.size()-2) % 600;
		// yCor = (bag.size()-2) / 600;
		// System.out.println(xCor + " end " + yCor);

		outputStreamPopGrowth.println(
				wrrSim.schedule.getTime() + " " + bag.size() + " " + (increasedPop * (time % 120) + population[g]));

		if (wrrSim.schedule.getTime() == 599) {
			outputStreamHousehold.close();
			outputStreamPopGrowth.close();
		}

	}

}
