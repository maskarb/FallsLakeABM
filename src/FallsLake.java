import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.channels.NonReadableChannelException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.media.chart.TimeSeriesAttributes;
import pertubing.*;

/**
 * 
 */

/**
 * @author amashha
 * 
 */
public class FallsLake extends Reservoir implements Steppable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private HashMap<Integer, ArrayList<Double>> data;
	public double observedStorage;
	private double observedInflow;
	private double observedOutflow;
	private double observedWaterSupply;
	private double rainfall;
	private double deficit;
	private double reliability;
	private double resilience;
	private double sumDeficit;
	private int nonzerodeficit;
	private double maxDeficit;
	private double averageDemand;
	private double sustainabilityindex;

	// private double capacity;

	private final ArrayList<Double> elevationArray;
	private final ArrayList<Double> areaArray;
	private final ArrayList<Double> storageArray;
	// private final ArrayList<Double> deficitArray;

	public PrintWriter outputStream;

	private boolean isReleaseModel;
	private double lowestStorage;// 25073

	DataList flow;

	public static HashMap<Integer, ArrayList<Double>> finalResultMap = new HashMap<Integer, ArrayList<Double>>();

	public FallsLake(boolean isReleaseModel, double initialStorage, double elevation, double lowestStorage,
			HashMap<Integer, ArrayList<Double>> hashMap, ArrayList<ArrayList<Double>> list,
			PrintWriter outputStreamReservoir, DataList flow) {

		this.flow = flow;

		this.storage = initialStorage;
		this.observedStorage = initialStorage;
		this.elevation = elevation;
		this.data = hashMap;

		// this.capacity = 450000;// 352577;// sum of all storage parts of the
		// reservoir
		this.inflow = 0;
		this.outflow = 0;
		this.rainfall = 0;

		this.outputStream = outputStreamReservoir;
		// This is for labeling the output file.
		outputStream.println("observedStorage" + " " + "observedOutflow" + " " + "observedWaterSupply" + " "
				+ "elevationEnd" + " " + "observedInflow" + " " + "storage" + " " + "outflow" + "  "
				+ "totalWaterSupply" + "  " + "waterSupply" + " " + "elevation" + "  " + "totalIndoor" + "  "
				+ "totalOutdoor" + "  " + "numOfHouseholds" + "  " + "population" + " " + "inflow" + " " + "deficit");

		elevationArray = (ArrayList<Double>) list.get(0);
		areaArray = (ArrayList<Double>) list.get(1);
		storageArray = (ArrayList<Double>) list.get(2);

		this.isReleaseModel = isReleaseModel;
		this.lowestStorage = lowestStorage;

	}

	@Override
	public void withdraw(double totalWater) {
		double remainedWater = storage - lowestStorage;
		double actual = 0;
		if (remainedWater >= totalWater) {
			storage -= totalWater;
			actual = totalWater;
		} else if (remainedWater > 0) {
			;
			storage -= remainedWater;
			actual = remainedWater;
		}
		waterSupply += actual;
		// System.out.println(totalWater + " " + actual);
	}

	@Override
	public void inflow() {
		storage += watershed.getStreamflow();
	}

	private void inflow(double d) {
		storage += d;
	}

	@Override
	public void precipitation(double depth) {
		rainfall = lookupSpreadArea(storage) * (depth / 12);
		storage += rainfall;
	}

	@Override
	public void evaporation() {

	}

	@Override
	public void release(double totalWater) {
		double remainedWater = storage - lowestStorage;
		double actualRelease = 0;
		if (remainedWater >= totalWater) {
			storage -= totalWater;
			actualRelease = totalWater;
		} else if (remainedWater > 0) {
			storage -= remainedWater;
			actualRelease = remainedWater;
		}
		outflow = actualRelease;

	}

	public void release(int month, int days, double randomNum) {

		elevation = lookupElevation(storage);
		double normalElavation = 0;
		double release = 0;
		double clayton = 6000;// 2000 + randomNum * 5000;//6000
		double liftedAmount = 2000;
		double maxDischarge = 8000;// (cfs) approximate at elevation 250 m.s.l.
		double lakeLevelForecast = 268;
		double freeOverflowing = lookupStorage(elevation) - lookupStorage(268);
		double uncontroledDrainageAreaFlow = 4000;// 1000 + randomNum *
													// 6000;//4000

		double minimumReleaseSummer = 100;
		double minimumReleaseWinter = 60;

		int counter = 0;
		double totalRelease = 0;
		if (month >= 4 && month <= 8) {
			normalElavation = 251.5;
			if (elevation <= normalElavation) {
				release = 100;// + 254 ;
				storage -= release * 3600 * 24 * days / 43560;
				totalRelease = release * 3600 * 24 * days / 43560;
			} else {
				while (elevation > normalElavation && counter < days) {
					if (elevation <= 255) {
						if (clayton >= 7000) {
							release = 100;
						} else {
							// release = Math.min(7000 - clayton, 4000);
							// release = 7000 -
							// Math.min(uncontroledDrainageAreaFlow, 4000);
							// release = Math.min(7000 -
							// uncontroledDrainageAreaFlow, 7000 - 4000);
							release = Math.min(7000 - uncontroledDrainageAreaFlow, 4000);
							// release = 3000;//3000
						}
					} else if (elevation <= 258) {
						if (clayton >= 7000) {
							release = 100;
						} else {
							// release = Math.min(7000 - clayton, 4000 +
							// liftedAmount);
							release = Math.min(7000 - uncontroledDrainageAreaFlow, 4000 + liftedAmount);
							// release = 3500;//3000
						}
					} else if (elevation <= 264) {
						if (clayton >= 8000) {
							release = 100;
						} else {
							// release = Math.min(8000 - clayton, 4000 +
							// liftedAmount);
							release = Math.min(8000 - uncontroledDrainageAreaFlow, 4000 + liftedAmount);
							// release = 4500;//4000
						}
					} else if (elevation <= 268) {
						if (lakeLevelForecast <= 268 && clayton >= 8000) {
							release = freeOverflowing;
						} else {
							release = maxDischarge;
						}
					} else {
						release = maxDischarge;
					}
					storage -= (release * 3600 * 24) / 43560;
					elevation = lookupElevation(storage);
					counter++;
					totalRelease += (release * 3600 * 24 / 43560);
					// System.out.println(release * 3600 * 24 * days / 43560
					// + " month " + month + " counter " + counter
					// + " ele " + elevation);
				}
			}
		} else if (month >= 9 || month <= 3) {
			normalElavation = 250.1;
			if (elevation <= normalElavation) {
				release = 60;// + 184;
				storage -= release * 3600 * 24 * days / 43560;
				totalRelease += (release * 3600 * 24 * days / 43560);
			} else {
				while (elevation > normalElavation && counter < days) {
					if (elevation <= 255) {
						if (clayton >= 7000) {
							release = 60;
						} else {
							release = Math.min(7000 - clayton, 4000);
							// release =
						}
					} else if (elevation <= 258) {
						if (clayton >= 7000) {
							release = 60;
						} else {
							release = Math.min(7000 - clayton, 4000 + liftedAmount);
							// release =
						}
					} else if (elevation <= 264) {
						if (clayton >= 8000) {
							release = 60;
						} else {
							release = Math.min(8000 - clayton, 4000 + liftedAmount);
						}
					} else if (elevation <= 268) {
						if (lakeLevelForecast <= 268 && clayton >= 8000) {
							release = freeOverflowing;
						} else {
							release = maxDischarge;
						}
					} else {
						release = maxDischarge;
					}
					storage -= release * 3600 * 24 / 43560;
					elevation = lookupElevation(storage);
					counter++;
					totalRelease += (release * 3600 * 24 / 43560);
					// System.out.println(release * 3600 * 24 * days / 43560
					// + " month " + month + " counter " + counter
					// + " ele " + elevation);
				}
			}
		}
		// System.out.println(totalRelease);
		outflow = totalRelease;
	}

	public boolean investigateReservoirStatus(double storage, double inflow, double waterSupply, int month, int days) {
		double normalElavation = 0;
		double instantaneousRelease = 0;
		double totalRelease = 0;
		if (month >= 4 && month <= 8) {
			normalElavation = 251.5;
			instantaneousRelease = 100;

		} else {
			normalElavation = 250.1;
			instantaneousRelease = 60;// 60 or 184;

		}
		totalRelease += (instantaneousRelease * 3600 * 24 * days / 43560);

		if (storage + inflow - waterSupply - totalRelease <= lookupStorage(237)) {
			return true;
		} else {
			return false;
		}
	}

	// public void release(boolean isFlood, int month, int days){
	// double normalElavation = 0;
	// double instantaneousRelease = 0;
	// double otherRelease = 0;
	// double totalRelease = 0;
	//
	//
	// elevation = lookupElevation(storage);
	//// double normalElavation = 0;
	//// double release = 0;
	// double clayton = 6000;
	// double liftedAmount = 2000;
	// double maxDischarge = 8000;// (cfs) approximate at elevation 250 m.s.l.
	// double lakeLevelForecast = 268;
	// double freeOverflowing = lookupStorage(elevation) - lookupStorage(268);
	// double uncontroledDrainageAreaFlow = 4000;
	// int counter = 0;
	//
	// if (isFlood) {
	// if (month >= 4 && month <= 8) {
	// normalElavation = 251.5;
	// while (elevation > normalElavation && counter < days) {
	// if (elevation <= 255) {
	// if (clayton >= 7000) {
	// otherRelease = 100;
	// } else {
	// // release = Math.min(7000 - clayton, 4000);
	// // release = 7000 -
	// // Math.min(uncontroledDrainageAreaFlow, 4000);
	// // release = Math.min(7000 -
	// // uncontroledDrainageAreaFlow, 7000 - 4000);
	// otherRelease = Math.min(
	// 7000 - uncontroledDrainageAreaFlow, 4000);
	// }
	// } else if (elevation <= 258) {
	// if (clayton >= 7000) {
	// otherRelease = 100;
	// } else {
	// // release = Math.min(7000 - clayton, 4000 +
	// // liftedAmount);
	// otherRelease = Math.min(
	// 7000 - uncontroledDrainageAreaFlow,
	// 4000 + liftedAmount);
	// }
	// } else if (elevation <= 264) {
	// if (clayton >= 8000) {
	// otherRelease = 100;
	// } else {
	// // release = Math.min(8000 - clayton, 4000 +
	// // liftedAmount);
	// otherRelease = Math.min(
	// 8000 - uncontroledDrainageAreaFlow,
	// 4000 + liftedAmount);
	// }
	// } else if (elevation <= 268) {
	// if (lakeLevelForecast <= 268 && clayton >= 8000) {
	// otherRelease = freeOverflowing;
	// } else {
	// otherRelease = maxDischarge;
	// }
	// } else {
	// otherRelease = maxDischarge;
	// }
	// storage -= (otherRelease * 3600 * 24) / 43560;
	// elevation = lookupElevation(storage);
	// counter++;
	// totalRelease += (otherRelease * 3600 * 24 / 43560);
	//// System.out.println(release * 3600 * 24 * days / 43560
	//// + " month " + month + " counter " + counter
	//// + " ele " + elevation);
	// }
	// } else {
	// normalElavation = 250.1;
	// while (elevation > normalElavation && counter < days) {
	// if (elevation <= 255) {
	// if (clayton >= 7000) {
	// otherRelease = 60;
	// } else {
	// otherRelease = Math.min(7000 - clayton, 4000);
	// }
	// } else if (elevation <= 258) {
	// if (clayton >= 7000) {
	// otherRelease = 60;
	// } else {
	// otherRelease = Math.min(7000 - clayton,
	// 4000 + liftedAmount);
	// }
	// } else if (elevation <= 264) {
	// if (clayton >= 8000) {
	// otherRelease = 60;
	// } else {
	// otherRelease = Math.min(8000 - clayton,
	// 4000 + liftedAmount);
	// }
	// } else if (elevation <= 268) {
	// if (lakeLevelForecast <= 268 && clayton >= 8000) {
	// otherRelease = freeOverflowing;
	// } else {
	// otherRelease = maxDischarge;
	// }
	// } else {
	// otherRelease = maxDischarge;
	// }
	// storage -= otherRelease * 3600 * 24 / 43560;
	// elevation = lookupElevation(storage);
	// counter++;
	// totalRelease += (otherRelease * 3600 * 24 / 43560);
	//
	// }
	// }
	// }else{
	// if (month >= 4 && month <= 8) {
	// normalElavation = 251.5;
	// instantaneousRelease = 100;// 100 or 254 ;
	// totalRelease = instantaneousRelease * 3600 * 24 * days / 43560;
	// }else {
	// normalElavation = 250.1;
	// instantaneousRelease = 60 ;//60 or 184;
	// totalRelease += (instantaneousRelease * 3600 * 24 * days / 43560);
	// }
	// storage -= totalRelease;
	// }
	// outflow = totalRelease;
	// }

	public void step(SimState state) {
		WRRSim wrrSim = (WRRSim) state;
		int time = (int) wrrSim.schedule.getTime();

		PopulationGrowth popGrowth = wrrSim.populationGrowth;
		int month = (int) popGrowth.getMonthNum();
		int days = (int) popGrowth.getNumOfDays(month);

		waterSupply = 0;

		double elevationStart = (double) ((ArrayList<Double>) data.get(time)).get(2);
		double elevationEnd = (double) ((ArrayList<Double>) data.get(time)).get(3);
		double elevationMax = (double) ((ArrayList<Double>) data.get(time)).get(4);
		double elevationMin = (double) ((ArrayList<Double>) data.get(time)).get(6);

		// cfs * (24 * 3600 / 43560) ((1.98347)) = acre-feet

		// observedInflow = (double) ((ArrayList<Double>) data.get(time)).get(8)
		// * 1.98347
		// * days;
		observedInflow = (double) flow.value(time) * 1.98347 * days;

		observedOutflow = (double) ((ArrayList<Double>) data.get(time)).get(9) * 1.98347 * days;

		observedWaterSupply = (double) ((ArrayList<Double>) data.get(time)).get(10) * 1.98347 * days;

		// double rainfallDepth = (double) ((ArrayList<Double>)
		// data.get(time)).get(11);

		// Residential consumption
		double residentialUsageFromData = observedWaterSupply * 0.566;// *
																		// 0.638;

		HashMap<Integer, ArrayList<Double>> totalDemand = Household.getTotalConsumption();
		double residentialWaterSupply = (double) ((ArrayList<Double>) totalDemand.get(time)).get(2);
		double totalIndoor = (double) ((ArrayList<Double>) totalDemand.get(time)).get(0);
		double totalOutdoor = (double) ((ArrayList<Double>) totalDemand.get(time)).get(1);
		double numOfHouseholds = (double) ((ArrayList<Double>) totalDemand.get(time)).get(3);
		double population = (double) ((ArrayList<Double>) totalDemand.get(time)).get(4);
		// double numOfRW = (double) ((ArrayList<Double>)
		// totalDemand.get(time)).get(5);

		// Non-residential consumption
		// double nonResidentialUsage = observedWaterSupply *
		// 0.362;//nonResidentail from data
		double nonResidentialWaterSupply = residentialWaterSupply * 0.767;// *
																			// (36/64)
																			// nonresidentail
																			// from
																			// agents
		// Residential = 0.566 of total then nonResidential = (1 - 0.566)/0.566
		// = 0.767

		double totalWaterSupply = 0.855 * (residentialWaterSupply + nonResidentialWaterSupply);

		// boolean isDrought = investigateReservoirStatus(storage,
		// observedInflow, totalWaterSupply, month, days);
		//
		// if(isDrought){
		// this.inflow(observedInflow);
		//
		// if (isReleaseModel) {
		// this.release(month, days, wrrSim.random.nextDouble());
		// }else {
		// this.release(observedOutflow);
		// }
		//
		// this.withdraw(totalWaterSupply);
		// } else {
		// this.inflow(observedInflow);
		// this.withdraw(totalWaterSupply);
		// if (isReleaseModel) {
		// this.release(month, days, wrrSim.random.nextDouble());
		// }else {
		// this.release(observedOutflow);
		// }
		//
		// }

		this.inflow(observedInflow);

		// this.withdraw(nonResidentialUsage);

		if (isReleaseModel) {
			this.release(month, days, wrrSim.random.nextDouble());
			// this.release(isFlood, month, days);
		} else {
			this.release(observedOutflow);
		}

		// this.precipitation(rainfallDepth);
		// this.evaporation();

		this.withdraw(totalWaterSupply);// 66.1/77.3 = 0.855
		// this.withdraw(residentialUsageFromData + nonResidentialUsage);
		// System.out.println(storage);
		this.elevation = lookupElevation(storage);

		this.observedStorage = this.observedStorage + observedInflow - observedOutflow - observedWaterSupply;
		// if (observedStorage < lowestStorage)
		// observedStorage = lowestStorage;
		this.deficit = 0;
		if (totalWaterSupply > waterSupply) {
			deficit = totalWaterSupply - waterSupply;
		}
		
		ArrayList<Double> finalResultArray = new ArrayList<Double>();
		finalResultArray.add(storage);
		finalResultArray.add(outflow);
		finalResultArray.add(totalWaterSupply);// water demands
		finalResultArray.add(waterSupply);// water that is actually delivered
		finalResultArray.add(elevation);
		finalResultArray.add(totalIndoor);
		finalResultArray.add(totalOutdoor);
		finalResultArray.add(numOfHouseholds);
		finalResultArray.add(population);
		finalResultArray.add(observedInflow);
		finalResultArray.add(deficit);//10

		finalResultMap.put(time, finalResultArray);
		
		if (time < 599) {
			resilience = 0.0;
			reliability = 0.0;
			sumDeficit = 0.0;
			nonzerodeficit = 0;
			maxDeficit = 0.0;
			averageDemand = 0.0;
			sustainabilityindex = 0.0;
		}
		if (time == 599) {

			////////////////
			// calculate sustainability metrics based on deficit
			int zeroDeficit = 0;
			int resilienceCount = 0;
			double vulnerability;
			maxDeficit = 0;
			sumDeficit = 0;
			averageDemand = 0;

			for (int m = 372; m < 600; m++) {// m = 372 for future data
				sumDeficit += finalResultMap.get(m).get(10);
				if (finalResultMap.get(m).get(10) == 0) {// deficit is equal to
															// zero
					zeroDeficit++;
					//if (m > 0) {
						if (finalResultMap.get(m - 1).get(10) > 0)
							resilienceCount++;
					//}
				} else {// deficit is greater than zero
					if (finalResultMap.get(m).get(10) > maxDeficit)
						maxDeficit = finalResultMap.get(m).get(10);
				}
				averageDemand += finalResultMap.get(m).get(2);
			}

			nonzerodeficit = (600 - 372) - zeroDeficit;// 228 points in future data

			if (nonzerodeficit == 0) {// if there are no deficits
				resilience = 1.0;
				reliability = 1.0;
				vulnerability = 0.0;
				maxDeficit = 0.0;
				averageDemand = averageDemand / (600-372);
				sustainabilityindex = Math.pow(reliability * resilience * (1 - vulnerability / averageDemand)
						* (1 - maxDeficit / averageDemand), .25);
			} else {// if there are non-zero deficits
				resilience = 1.0 * resilienceCount / (1.0 * nonzerodeficit);
				reliability = 1.0 * zeroDeficit / (600-372) ;
				averageDemand = averageDemand / (600-372);
				vulnerability = (sumDeficit / nonzerodeficit);
				sustainabilityindex = Math.pow(reliability * resilience * (1 - vulnerability / averageDemand)
						* (1 - maxDeficit / averageDemand), .25);
			}
			System.out.println(reliability + " " + resilience + " " + sumDeficit + " " + nonzerodeficit + " "
					+ maxDeficit + " " + averageDemand + " " + sustainabilityindex + " " + vulnerability/averageDemand 
					+ " " + maxDeficit/averageDemand);
		}
		////////////////

		outputStream.println(observedStorage + " " + observedOutflow + " " + observedWaterSupply + " " + elevationEnd
				+ " " + observedInflow + " " + storage + " " + outflow + "  " + totalWaterSupply + "  " + waterSupply
				+ " " + elevation + "  " + totalIndoor + "  " + totalOutdoor + "  " + numOfHouseholds + "  "
				+ population + " " + deficit + " " + reliability + " " + resilience + " " + sumDeficit + " "
				+ nonzerodeficit + " " + maxDeficit + " " + averageDemand + " " + sustainabilityindex);
		// + " " + elevationMax + " " + elevationMin + " " + rainfall);

		
		finalResultArray.add(reliability);
		finalResultArray.add(resilience);//12
		finalResultArray.add(sumDeficit);
		finalResultArray.add(1.0*nonzerodeficit);//14
		finalResultArray.add(maxDeficit);
		finalResultArray.add(averageDemand);//16
		finalResultArray.add(sustainabilityindex);
		
		finalResultMap.put(time, finalResultArray);

		if (time == 599) {

			outputStream.close();

			WRRSim.finalResult.add(finalResultMap);
		}

	}

	public double getObservedStorage() {
		return observedStorage;
	}

	@Override
	public double lookupElevation(double storage) {
		double y0 = 0;
		double y1 = 0;
		double x0 = 0;
		double x1 = 0;
		double elevation = 0;
		for (int i = 1; i < storageArray.size(); i++) {
			if (storage == (double) storageArray.get(i)) {
				elevation = (double) elevationArray.get(storageArray.indexOf((double) storageArray.get(i)));
				break;

			} else if (storage < (double) storageArray.get(i)) {
				y1 = (double) elevationArray.get(storageArray.indexOf((double) storageArray.get(i)));
				y0 = (double) elevationArray.get(storageArray.indexOf((double) storageArray.get(i)) - 1);
				x1 = (double) storageArray.get(i);
				x0 = (double) storageArray.get(i - 1);

				elevation = (y0 + (y1 - y0) * ((storage - x0) / (x1 - x0)));
				break;
			}
		}
		return elevation;
	}

	@Override
	public double lookupSpreadArea(double storage) {
		double y0 = 0;
		double y1 = 0;
		double x0 = 0;
		double x1 = 0;
		double area = 0;
		for (int i = 0; i < storageArray.size(); i++) {
			if (storage == (double) storageArray.get(i)) {
				area = (double) areaArray.get(storageArray.indexOf((double) storageArray.get(i)));
				break;

			} else if (storage < (double) storageArray.get(i)) {
				y1 = (double) areaArray.get(storageArray.indexOf((double) storageArray.get(i)));
				y0 = (double) areaArray.get(storageArray.indexOf((double) storageArray.get(i)) - 1);
				x1 = (double) storageArray.get(i);
				x0 = (double) storageArray.get(i - 1);

				area = (y0 + (y1 - y0) * ((storage - x0) / (x1 - x0)));
				break;
			}
		}
		return area;
	}

	private double lookupStorage(double elevation) {
		double y0 = 0;
		double y1 = 0;
		double x0 = 0;
		double x1 = 0;
		double storage = 0;
		for (int i = 1; i < elevationArray.size(); i++) {
			if (elevation == (double) elevationArray.get(i)) {
				storage = (double) storageArray.get(elevationArray.indexOf((double) elevationArray.get(i)));
				break;

			} else if (elevation < (double) elevationArray.get(i)) {
				y1 = (double) storageArray.get(elevationArray.indexOf((double) elevationArray.get(i)));
				y0 = (double) storageArray.get(elevationArray.indexOf((double) elevationArray.get(i)) - 1);
				x1 = (double) elevationArray.get(i);
				x0 = (double) elevationArray.get(i - 1);

				storage = (y0 + (y1 - y0) * ((elevation - x0) / (x1 - x0)));
				break;
			}
		}
		return storage;
	}

	public double getLowestStorage() {
		return lowestStorage;
	}

	public void release_2(int month, int days) {

		elevation = lookupElevation(storage);
		double normalElavation = 0;
		double release = 0;
		double clayton = 6000;
		double liftedAmount = 2000;
		double maxDischarge = 8000;// (cfs) approximate at elevation 250 m.s.l.
		double lakeLevelForecast = 268;
		double freeOverflowing = lookupStorage(elevation) - lookupStorage(268);
		double uncontroledDrainageAreaFlow = 4000;

		double minimumReleaseSummer = 100;
		double minimumReleaseWinter = 60;

		int counter = 0;
		double totalRelease = 0;

		double currentStorage = storage;
		double currentElevation = elevation;

		if (month >= 4 && month <= 8) {
			normalElavation = 251.5;
			if (currentElevation <= normalElavation) {
				release = 100;// + 254 ;
				currentStorage -= release * 3600 * 24 * days / 43560;
				totalRelease = release * 3600 * 24 * days / 43560;
			} else {
				while (currentElevation > normalElavation && counter < days) {
					if (currentElevation <= 255) {
						if (clayton >= 7000) {
							release = 100;
						} else {
							// release = Math.min(7000 - clayton, 4000);
							// release = 7000 -
							// Math.min(uncontroledDrainageAreaFlow, 4000);
							// release = Math.min(7000 -
							// uncontroledDrainageAreaFlow, 7000 - 4000);
							release = Math.min(7000 - uncontroledDrainageAreaFlow, 4000);
						}
					} else if (currentElevation <= 258) {
						if (clayton >= 7000) {
							release = 100;
						} else {
							// release = Math.min(7000 - clayton, 4000 +
							// liftedAmount);
							release = Math.min(7000 - uncontroledDrainageAreaFlow, 4000 + liftedAmount);
						}
					} else if (currentElevation <= 264) {
						if (clayton >= 8000) {
							release = 100;
						} else {
							// release = Math.min(8000 - clayton, 4000 +
							// liftedAmount);
							release = Math.min(8000 - uncontroledDrainageAreaFlow, 4000 + liftedAmount);
						}
					} else if (currentElevation <= 268) {
						if (lakeLevelForecast <= 268 && clayton >= 8000) {
							release = freeOverflowing;
						} else {
							release = maxDischarge;
						}
					} else {
						release = maxDischarge;
					}
					// storage -= (release * 3600 * 24) / 43560;
					currentStorage -= (release * 3600 * 24) / 43560;
					// elevation = lookupElevation(storage);
					currentElevation = lookupElevation(currentStorage);
					counter++;
					totalRelease += (release * 3600 * 24 / 43560);
					// System.out.println(release * 3600 * 24 * days / 43560
					// + " month " + month + " counter " + counter
					// + " ele " + elevation);
				}
			}
		} else if (month >= 9 || month <= 3) {
			normalElavation = 250.1;
			if (currentElevation <= normalElavation) {
				release = 60;// + 184;
				currentStorage -= release * 3600 * 24 * days / 43560;
				totalRelease += (release * 3600 * 24 * days / 43560);
			} else {
				while (currentElevation > normalElavation && counter < days) {
					if (currentElevation <= 255) {
						if (clayton >= 7000) {
							release = 60;
						} else {
							release = Math.min(7000 - clayton, 4000);
						}
					} else if (currentElevation <= 258) {
						if (clayton >= 7000) {
							release = 60;
						} else {
							release = Math.min(7000 - clayton, 4000 + liftedAmount);
						}
					} else if (currentElevation <= 264) {
						if (clayton >= 8000) {
							release = 60;
						} else {
							release = Math.min(8000 - clayton, 4000 + liftedAmount);
						}
					} else if (currentElevation <= 268) {
						if (lakeLevelForecast <= 268 && clayton >= 8000) {
							release = freeOverflowing;
						} else {
							release = maxDischarge;
						}
					} else {
						release = maxDischarge;
					}
					currentStorage -= release * 3600 * 24 / 43560;
					currentElevation = lookupElevation(currentStorage);
					counter++;
					totalRelease += (release * 3600 * 24 / 43560);
					// System.out.println(release * 3600 * 24 * days / 43560
					// + " month " + month + " counter " + counter
					// + " ele " + elevation);
				}
			}
		}
		// System.out.println(storage - totalRelease - currentStorage);
		storage = currentStorage;
		elevation = currentElevation;
		outflow = totalRelease;
	}

	public static void main(String[] args) {

	}

}
