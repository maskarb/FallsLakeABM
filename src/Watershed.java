/* import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
*/
import sim.engine.SimState;
import sim.engine.Steppable;

/**
 * 
 */

/**
 * @author amashha
 *
 */
public class Watershed implements Steppable {

	private Climate climate;
	private double streamflow;

	public void step(SimState state) {
		// TODO Auto-generated method stub

	}

	public double getStreamflow() {
		return streamflow;
	}

}
