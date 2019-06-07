/** */

/** @author amashha */
public abstract class Reservoir {

  protected double storage; // ACRE-FEET
  protected double elevation;
  protected double spreadArea;
  protected Watershed watershed;

  protected double outflow;
  protected double inflow;
  protected double waterSupply;

  public abstract void withdraw(double amount);

  public abstract void inflow();

  public abstract void precipitation(double depth);

  public abstract void evaporation();

  public abstract void release(double d);

  public abstract double lookupElevation(double storage);

  public abstract double lookupSpreadArea(double storage);

  public double getElevation() {
    return elevation;
  }

  public double getSpreadArea() {
    return spreadArea;
  }

  public double getStorage() {
    return storage;
  }
}
