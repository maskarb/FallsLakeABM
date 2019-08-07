/** @author amashha */
public class OutdoorEnduses {

  private double swimmingPool;
  private double gardenWatering; // theoretical irrigation requirement (gal)
  // TIR monthly
  private double washingCar;

  private double wateringFreq;
  private double area;

  private double[] cropCoefficient;

  private Climate climate;

  public OutdoorEnduses(
      int month,
      double area,
      double wateringFreq,
      boolean isWarmCrop,
      double conservationFactor,
      Climate climate) {
    double[] warmTurfCoefficient = {
      0.55, 0.54, 0.76, 0.72, 0.79, 0.68, 0.71, 0.71, 0.62, 0.54, 0.58, 0.55
    };

    double[] coldTurfCoefficient = {
      0.61, 0.64, 0.75, 1.04, 0.95, 0.88, 0.94, 0.86, 0.74, 0.75, 0.69, 0.6
    };

    this.climate = climate;

    if (isWarmCrop) {
      this.cropCoefficient = warmTurfCoefficient;
    } else {
      this.cropCoefficient = coldTurfCoefficient;
    }

    this.wateringFreq = wateringFreq;
    this.area = area;
    if (wateringFreq == 0) {
      swimmingPool = washingCar = gardenWatering = 0;
    } else {
      // calculateOutdoorDemand(month);
      calculateOutdoorDemand(month, conservationFactor);
    }

    // this.conservationFactor = conservationFactor;

  }

  public void calculateOutdoorDemand(int month, double conservationFactor) {

    int monthNum = month % 12;
    // gardenWatering = 0.624 * (evapoTrans[month])
    // * area * (1.13 / 0.71) / 12; // gallon PER MONTH

    // if (wateringFreq == 0 || evapoTrans[month] <= rainFall[month]) {
    // if (wateringFreq == 0 || Climate.getEvapotrans(month) <=
    // Climate.getPrecip(month)) {
    if (climate.getEvapotrans(month) <= climate.getPrecip(month)) {
      // gardenWatering = 0;
      gardenWatering =
          -0.1
              * conservationFactor
              * (climate.getEvapotrans(month) - climate.getPrecip(month))
              * area
              * cropCoefficient[monthNum];

    } else {
      // gardenWatering = wateringFreq * 0.624
      // * (evapoTrans[month] - rainFall[month]) * area
      // * (1.13 / 0.71); // gallon PER MONTH **we eliminated
      // devision by 12**

      // gardenWatering = wateringFreq
      // * (evapoTrans[month] - rainFall[month]) * area
      // * cropCoefficient[monthNum];

      // gardenWatering = conservationFactor * wateringFreq
      // * (evapoTrans[month] - rainFall[month]) * area
      // * cropCoefficient[monthNum];

      // gardenWatering = conservationFactor * wateringFreq
      // * (Climate.getEvapotrans(month) - Climate.getPrecip(month)) *
      // area
      // * cropCoefficient[monthNum];
      // System.out.println(month + " " +Climate.getPrecip(month) + " " +
      // Climate.getEvapotrans(month));

      gardenWatering =
          conservationFactor
              * wateringFreq
              * (0.624 / 0.71)
              * (climate.getEvapotrans(month) - climate.getPrecip(month))
              * area
              * cropCoefficient[monthNum];
    }

    // System.out.println("gardenWatering " + gardenWatering);

    swimmingPool = washingCar = 0;
  }

  public double getOutdoorDemand() {
    return swimmingPool + gardenWatering + washingCar;
  }
}
