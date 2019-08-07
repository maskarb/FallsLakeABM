import sim.engine.SimState;
import sim.engine.Steppable;

/** @author amashha */
public class Watershed implements Steppable {
  static final long serialVersionUID = 1L;

  // private Climate climate;
  private double streamflow;

  public void step(SimState state) {}

  public double getStreamflow() {
    return streamflow;
  }
}
