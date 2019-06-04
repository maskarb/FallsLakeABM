import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.JFrame;

//import org.jfree.data.xy.XYSeries;

import sim.display.Console;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
// import sim.util.media.chart.TimeSeriesChartGenerator;

import sim.portrayal.DrawInfo2D;
import sim.portrayal.Inspector;
// import sim.portrayal.continuous.ContinuousPortrayal2D;
import sim.portrayal.grid.SparseGridPortrayal2D;
import sim.portrayal.network.*;
import sim.portrayal.simple.OvalPortrayal2D;

/**
 * 
 */

/**
 * @author amashha
 *
 */
public class WRRWithUI extends GUIState{
	
//	XYSeries series;    // the data series we'll add to
//    TimeSeriesChartGenerator chart;  // the charting facility
	
	public Display2D display;
	public JFrame displayFrame;
	
	NetworkPortrayal2D socialNetworkPortrayal = new NetworkPortrayal2D();
//	ContinuousPortrayal2D cityPortrayal = new ContinuousPortrayal2D();
	SparseGridPortrayal2D cityPortrayal = new SparseGridPortrayal2D() ;

	public WRRWithUI(SimState state) {
		super(state);
	}
	
	public WRRWithUI(int runNum, double shiftFac, String climate, boolean r, boolean d, double[] per, int z) {
		super(new WRRSim(System.currentTimeMillis(), runNum, shiftFac, climate, r,d,per, z));
	}
	
	public static String getName() {
		return "Water Resources Research";
	}
	
	public static void main(String[] args){
		String climate = "DRY_new";
		boolean isRetrofitting = false;
		boolean isDroughtRestriction = false;
		double[] reductionPercentages = {0.8,0.6,0.4,0.2};
		WRRWithUI vid = new WRRWithUI(0,0.1, climate, isRetrofitting,isDroughtRestriction, reductionPercentages, 1);
		Console c = new Console(vid);
		c.setVisible(true);
		
	}
	
	
	public Object getSimulationInspectedObject() { 
		return state; 
	}
	
	public Inspector getInspector(){
		Inspector i = super.getInspector();
		i.setVolatile(true);
		return i;
	}
	
	public void start(){
		super.start();
		setupPortrayals();
	}
	
	public void load(SimState state){
		super.load(state);
		setupPortrayals();
	}
	
	public void setupPortrayals(){
		WRRSim wrrSim = (WRRSim) state;
		// tell the portrayals what to portray and how to portray them
		cityPortrayal.setField( wrrSim.city );
//		cityPortrayal.setPortrayalForAll(new OvalPortrayal2D());
//		cityPortrayal.setPortrayalForAll(new OvalPortrayal2D(Color.gray,0.5, true));
//		socialNetworkPortrayal.setPortrayalForAll(new OvalPortrayal2D()
//		{
//			public void draw(Object object, Graphics2D graphics, DrawInfo2D info){
//				Household user = (Household)object;
//				super.draw(object, graphics, info);
//			}
//		});
		
		cityPortrayal.setPortrayalForAll(new OvalPortrayal2D()
		{
			static final long serialVersionUID = 1L;
			public void draw(Object object, Graphics2D graphics, DrawInfo2D info){
				Household user = (Household)object;
				scale = 1;
				paint =  Color.BLACK;
				if (user.isWaterReuser()){
					paint = Color.RED;
//					scale = 0.5;
				}
				
				super.draw(object, graphics, info);
			}
		});
	
		socialNetworkPortrayal.setField(new SpatialNetwork2D(wrrSim.city, wrrSim.socialNetwork));
		socialNetworkPortrayal.setPortrayalForAll(new SimpleEdgePortrayal2D());
		
		// reschedule the displayer
		display.reset();
		display.setBackdrop(Color.white);
		// redraw the display
		display.repaint();
	}
	
	
	public void init(Controller c){
		super.init(c);
		display = new Display2D(600,600,this);
		display.setClipping(false);
		displayFrame = display.createFrame();
		displayFrame.setTitle("WRR Display");
		c.registerFrame(displayFrame); // so the frame appears in the "Display" list
		displayFrame.setVisible(true);
//		display.attach( socialNetworkPortrayal, "Users" );
		display.attach( cityPortrayal, "city" );
	}
	
	public void quit(){
	super.quit();
	if (displayFrame!=null) displayFrame.dispose();
	displayFrame = null;
	display = null;
	}
}
