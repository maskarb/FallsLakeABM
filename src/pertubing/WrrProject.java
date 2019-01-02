package pertubing;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

public class WrrProject {

	private static final double[] flowJan = { 816, 1884, 1039, 340, 1919, 784,
			340, 1310, 2084, 1174, 1868, 784, 843, 1682, 1151, 3006, 1598,
			1127, 160, 568, 658, 240, 857, 368, 1082, 101, 736, 1546, 110, 155,
			854 };

	private static final double[] flowFeb = { 2394, 2558, 2497, 453, 1908, 607,
			2218, 2029, 347, 575, 792, 1213, 1406, 1341, 1533, 3393, 480, 1579,
			473, 210, 2131, 961, 636, 343, 586, 221, 298, 2200, 237, 275, 980 };

	private static final double[] flowMar = { 3144, 3177, 386, 1120, 2274, 259,
			3239, 1079, 1728, 978, 3718, 2704, 1187, 1300, 1089, 4393, 919,
			941, 1986, 333, 2953, 403, 1463, 103, 1023, 887, 2357, 835, 621,
			913, 494 };

	private static final double[] flowApr = { 2366, 1981, 87, 134, 1776, 400,
			1743, 1409, 529, 497, 2403, 508, 102, 864, 1712, 959, 859, 1372,
			799, 159, 2923, 479, 589, 324, 1060, 1114, 542, 185, 404, 125, 614 };

	private static final double[] flowMay = { 1499, 1079, 248, 149, 305, 229,
			1457, 1565, 219, 148, 321, 115, 156, 837, 376, 373, 214, 441, 121,
			-11, 1617, 289, 135, 131, 137, 223, 197, 1060, 342, 348, 995 };

	private static final double[] flowJun = { 357, 221, -4, -52, 103, 48, 723,
			195, 62, 325, 20, 55, 1935, 230, 371, 114, 110, 281, 332, -66, 973,
			67, 141, 1259, 47, 81, 638, 130, -19, 25, 1348 };

	private static final double[] flowJul = { 71, 1006, 103, 24, 31, 11, 981,
			57, 59, 198, 0, 201, 496, 164, 273, 28, 34, 630, 326, -3, 882, 179,
			36, 516, -25, 174, 30, 40, -8, 50, 1262 };

	private static final double[] flowAug = { 59, 424, 566, 709, -35, -3, 1151,
			163, 25, 119, -16, 136, 153, 402, -47, 31, 201, 455, 247, 56, 823,
			995, 17, 16, -46, 252, 26, 30, -15, 141, 268 };

	private static final double[] flowSep = { -35, 25, 101, -21, 194, 85, 239,
			-53, 146, 72, 5, -19, 13, 4153, 37, 80, 5421, 279, 82, 99, 646,
			535, -56, 294, -44, 1930, 117, 174, 107, 465, 12 };

	private static final double[] flowOct = { 98, 76, 31, -38, -6, 172, 904,
			501, 18, 136, 28, 190, 1178, 819, 47, 37, 607, -9, -7, 1814, 189,
			183, 37, 179, 86, 66, 17, 78, 41, 57, 5 };

	private static final double[] flowNov = { 331, 53, 2565, 63, 79, 558, 622,
			140, 36, 747, 136, 83, 1295, 597, 285, 57, 296, 91, 4, 1026, 268,
			585, 132, 2360, 1, 230, 1787, 18, 195, 1, 86 };

	private static final double[] flowDec = { 1757, 339, 742, 610, 241, 148,
			1247, 613, 123, 724, 398, 58, 700, 1331, 312, 278, 419, 148, 74,
			1965, 1092, 707, 688, 630, 186, 990, 2768, 107, 245, 250, 1000 };

	private static final double[] prcipitationJan = { 2.03, 4.64, 4.61, 1.32,
			5.88, 3.35, 1.7, 3.93, 5.15, 4.84, 5.13, 3.99, 4.25, 3.83, 3.56,
			7.91, 5.51, 2.48, 1.98, 4.29, 2.13, 1.2, 2.87, 2.67, 2.13, 1.52,
			3.21, 2.99, 0.78, 1.58, 4 };

	private static final double[] prcipitationFeb = { 5.09, 5.2, 5.37, 1.87,
			4.23, 2.24, 5.91, 4.32, 1.31, 2.45, 2.66, 3.31, 3.82, 2.33, 2.89,
			5.13, 1.9, 2.56, 2.91, 1.02, 4.77, 1.99, 1.91, 1.84, 2.11, 3.26,
			1.54, 2.79, 1.72, 1.94, 3.82 };

	private static final double[] prcipitationMar = { 7.49, 7.66, 1.39, 2.98,
			4.54, 2.09, 7.06, 4.17, 5.94, 3.77, 7.57, 6.9, 3.13, 4.1, 1.92,
			8.37, 3, 3.2, 6.28, 3.01, 6.71, 1.54, 4.34, 0.84, 3.41, 4.81, 6.3,
			2.71, 5.37, 3.34, 2.22 };

	private static final double[] prcipitationApr = { 4.49, 4.48, 0.46, 1.1,
			5.46, 3.48, 5.61, 4.42, 2.63, 2.4, 4.99, 2.57, 1.18, 3.63, 5.65,
			3.74, 4.87, 5.66, 2.6, 1.57, 6.29, 2.32, 0.74, 3.79, 3.54, 5.82,
			1.98, 1.06, 2.9, 2.73, 3.75 };

	private static final double[] prcipitationMay = { 6.44, 6.02, 4.79, 2.73,
			3.08, 4.97, 4.42, 6.95, 2.72, 3.23, 4.32, 2.22, 3.66, 5.03, 1.38,
			3.95, 1.22, 3.18, 3.12, 2.02, 5.47, 3.48, 3.52, 2.87, 2.7, 2.92,
			3.93, 7.08, 4.37, 3.58, 4.65 };

	private static final double[] prcipitationJun = { 2.67, 2.04, 2.23, 0.84,
			2.48, 2.43, 6.62, 1.14, 2.03, 4.67, 1.65, 2.7, 11, 1.89, 3.63,
			2.36, 3.28, 3.82, 4.59, 2.32, 3.15, 3.65, 2.66, 7.2, 4.28, 3.73,
			6.29, 2.46, 2.81, 2.08, 7.62 };

	private static final double[] prcipitationJul = { 1.67, 9.82, 4.68, 4.2,
			2.58, 3.23, 7.3, 3.67, 5.35, 3.98, 3.22, 5.66, 3.97, 3.5, 4.26,
			2.83, 3.28, 5.34, 6.46, 2.87, 6.48, 4.68, 2.44, 4.37, 2.56, 3.17,
			1.94, 4.07, 3.43, 3.87, 4.77 };

	private static final double[] prcipitationAug = { 2.25, 2.12, 6.39, 6.99,
			2.71, 4.12, 6.3, 4.67, 3.13, 4.19, 2.2, 3.91, 3.23, 5.28, 1.31,
			3.07, 3.48, 4.78, 2.71, 5.18, 4.28, 8.72, 2.15, 2.83, 1.47, 6.6,
			3.43, 3.2, 4.08, 5.71, 2.33 };

	private static final double[] prcipitationSep = { 7.25, 0.55, 0.33, 0.53,
			5.83, 4.79, 3.96, 0.46, 4.96, 2.35, 2.68, 1.49, 1.73, 13.24, 3.77,
			3.82, 17.8, 4.42, 2.33, 3.95, 5.77, 3.95, 0.76, 5.5, 0.92, 8.02,
			3.64, 4.75, 6.02, 7.51, 2.49 };

	private static final double[] prcipitationOct = { 4.48, 2.93, 2.85, 2,
			2.65, 4.14, 4.91, 8.16, 1.93, 4.54, 3.27, 3.65, 8.27, 2.91, 2.67,
			2.99, 2.67, 0.08, 1.41, 7.96, 2.87, 1.76, 2.77, 3.68, 4.55, 0.85,
			2.18, 2.49, 2.55, 2.8, 2.69 };

	private static final double[] prcipitationNov = { 4.8, 1.35, 9.97, 3.26,
			2.48, 3.85, 3.09, 2.08, 0.98, 5.08, 3.6, 2.38, 4.77, 3.32, 3.66,
			2.19, 1.59, 2.03, 0.49, 3.38, 2.18, 3.25, 4.39, 7.53, 0.58, 3.31,
			8.49, 1.05, 4.27, 0.43, 2.77 };

	private static final double[] prcipitationDec = { 6.47, 2.67, 1.39, 4.1,
			3.86, 0.76, 3.12, 3.78, 2.73, 2.84, 3.69, 2.38, 1.84, 3.82, 2.66,
			4.75, 1.74, 1.85, 1.92, 3.32, 4.12, 2.11, 4.98, 3.42, 3.98, 4.12,
			8.13, 2.77, 2.54, 3.43, 4.75 };

	private static final double[] evaJan = { 0.97, 5.52, 3.84, 0.54, 11.23,
			2.03, 0.67, 0, 0, 3.84, 2.03, 3.18, 0, 4.52, 0, 0.85, 2.32, 4.52,
			1.22, 4.83, 1.8, 0.92, 2.31, 3.18, 2.44, 0.85, 2.32, 3.82, 1.15,
			2.04, 2.65 };

	private static final double[] evaFeb = { 3.75, 4.64, 3.34, 0.03, 4.98, 2.5,
			0.59, 0.1, 0, 3.34, 2.5, 2.54, 0.1, 1.92, 0, 2.41, 1.46, 1.92,
			1.87, 1.21, 4.29, 2.76, 0.25, 2.54, 1.49, 2.41, 1.46, 2.68, 2.05,
			1.97, 3.54 };

	private static final double[] evaMar = { 6.64, 6.85, 1.3, 0, 4.01, 2.48,
			6.39, 0, 0, 1.3, 2.48, 1.85, 0, 3, 0, 3.59, 5.51, 3, 5.78, 4.24,
			5.17, 2.28, 0.39, 1.85, 3.04, 3.59, 5.51, 3.1, 2.81, 4.2, 1.57 };

	private static final double[] evaApr = { 4.02, 3.45, 0.2, 0.18, 7.38, 2.02,
			7.17, 1.1, 0.66, 0.2, 2.02, 5.49, 1.1, 2.75, 0.66, 3.89, 1.09,
			2.75, 1.16, 0.54, 4.54, 0.91, 0.34, 5.49, 2.75, 3.89, 1.09, 1.63,
			3.2, 1.48, 3.31 };

	private static final double[] evaMay = { 1.67, 7.84, 2.68, 5.76, 1.51,
			1.67, 1.39, 4.06, 2.06, 2.68, 1.67, 4.92, 4.06, 0, 2.06, 2.79,
			3.78, 0, 0.44, 1.41, 1.65, 3.1, 2.75, 4.92, 1.91, 2.79, 3.78, 5.78,
			3.08, 4.17, 3.34 };

	private static final double[] evaJun = { 4.25, 4.78, 2.08, 2.77, 1.48,
			9.58, 0.47, 0, 3.02, 2.08, 9.58, 9.64, 0, 2.29, 3.02, 2.61, 6.78,
			2.29, 5.83, 2.29, 1.94, 4.35, 2.8, 9.64, 4.85, 2.61, 6.78, 3.04,
			3.72, 2.56, 8.36 };

	private static final double[] evaJul = { 1.01, 13.04, 3.34, 2.48, 6.79,
			3.47, 0, 10.38, 1, 3.34, 3.47, 2.25, 10.38, 3.27, 1, 3.24, 3.29,
			3.27, 7.89, 3.67, 4.9, 3.81, 5.54, 2.25, 3.7, 3.24, 3.29, 2.07,
			1.78, 5.27, 3.41 };

	private static final double[] evaAug = { 4.11, 1.01, 5.56, 13.27, 4.48,
			1.8, 0.56, 6.64, 0.32, 5.56, 1.8, 2.13, 6.64, 6.28, 0.32, 3.34,
			2.15, 6.28, 2.03, 5.84, 9.7, 9.88, 2.14, 2.13, 1.33, 3.34, 2.15,
			6.62, 5.14, 2.71, 2.24 };

	private static final double[] evaSep = { 16.67, 1.64, 0.32, 0.62, 6.68,
			1.8, 0, 0.1, 1.84, 0.32, 1.8, 2.75, 0.1, 2.16, 1.84, 6.91, 3.25,
			2.16, 1.41, 1.81, 3.48, 4.43, 1.24, 2.75, 1.57, 6.91, 3.25, 6.05,
			3.85, 6.47, 3.45 };

	private static final double[] evaOct = { 3.46, 0.78, 2.04, 2.34, 2.2, 1.9,
			0.04, 2.26, 0.12, 2.04, 1.9, 3.42, 2.26, 0, 0.12, 1.36, 1.2, 0,
			1.24, 7.58, 2.97, 1.42, 3.87, 3.42, 5.68, 1.36, 1.2, 0.88, 1.99,
			2.5, 0.79 };

	private static final double[] evaNov = { 2.26, 3.3, 3.82, 2.47, 4.12, 2.71,
			0.1, 1.48, 0.12, 3.82, 2.71, 6.25, 1.48, 2.2, 0.12, 3.3, 4.23, 2.2,
			0.91, 3.43, 1.49, 3.14, 6.73, 6.25, 0.53, 3.3, 4.23, 0.78, 3.14,
			0.59, 3.15 };

	private static final double[] evaDec = { 6.19, 2.9, 0.7, 4.52, 3.45, 1.18,
			0, 0.44, 0, 0.7, 1.18, 3.41, 0.44, 1.15, 0, 2.96, 5.7, 1.15, 2.04,
			4.07, 2.84, 1.85, 7.14, 3.41, 3.06, 2.96, 5.7, 1.43, 1.4, 3, 5.24 };

	public static final int[] time = { 1983, 1984, 1985, 1986, 1987, 1988,
			1989, 1990, 1991, 1992, 1993, 1994, 1995, 1996, 1997, 1998, 1999,
			2000, 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010,
			2011, 2012, 2013 };

	public static double[] historicData(final boolean isFlow,
			final boolean isPrecipitation, final boolean isEvapotranspiration) {
		List<Double> a = new ArrayList<Double>();

		for (int i = 0; i < time.length; i++) {
			for (int j = 0; j < 12; j++) {
				double[] b = getValueArray(j + 1, isFlow, isPrecipitation,
						isEvapotranspiration);
				a.add(b[i]);
			}
		}

		double[] aa = new double[a.size()];

		for (int i = 0; i < aa.length; i++) {
			aa[i] = a.get(i);
		}
		return aa;
	}


	public static Timeseries reconstructAllTimeseriesWithRespectToShiftInFlow(
			final int monthNumber1To12, final double interval,
			final double shiftFactor) {
//		System.out.println("reconstructAllTimeseriesWRTShiftInflow");
		
		Timeseries t = new Timeseries();

		PearsonsCorrelation p = new PearsonsCorrelation();

		double[] flowsVal = getValueArray(monthNumber1To12, true, false, false);

//		System.out.println("flows");
		double[] flows = PerturbationManager.executeRecon(time, flowsVal,
				interval, shiftFactor);

		double[] preVal = getValueArray(monthNumber1To12, false, true, false);

		double corF_P = p.correlation(flowsVal, preVal);
//		System.out.println("corF_P : " + corF_P);
		double shift4Pre = 0;

		if (shiftFactor > 1) {
			shift4Pre = (shiftFactor - 1) * corF_P + 1;
		} else {
			shift4Pre = 1 - (1 - shiftFactor) * corF_P;
		}
//		System.out.println("shift4Pre :");
		double[] precipitation = PerturbationManager.executeRecon(time, preVal,
				interval, shift4Pre);

		double[] evaVal = getValueArray(monthNumber1To12, false, false, true);

		double corF_E = p.correlation(flowsVal, evaVal);
//		System.out.println("corF_E : " + corF_E);
		double shift4Eva = 0;

		if (shiftFactor > 1) {
			shift4Eva = (shiftFactor - 1) * corF_E + 1;
		} else {
			shift4Eva = 1 - (1 - shiftFactor) * corF_E;
		}
//		System.out.println("shift4Eva :");
		double[] evatr = PerturbationManager.executeRecon(time, evaVal,
				interval, shift4Eva);

		for (int i = 0; i < evatr.length; i++) {
			t.getFlow().addData(i, flows[i]);
			t.getPrecipitation().addData(i, precipitation[i]);
			t.getEvapotranspiration().addData(i, evatr[i]);
			t.getShiftFactor().addData(i, shiftFactor);
		}
		return t;
	}

	private static double[] getValueArray(final int monthNumber1To12,
			final boolean isFlow, final boolean isPrecipitation,
			final boolean isEvapotranspiration) {

		if (isFlow) {
			switch (monthNumber1To12) {
			case (1):
				return convert(flowJan);
			case (2):
				return convert(flowFeb);
			case (3):
				return convert(flowMar);
			case (4):
				return convert(flowApr);
			case (5):
				return convert(flowMay);
			case (6):
				return convert(flowJun);
			case (7):
				return convert(flowJul);
			case (8):
				return convert(flowAug);
			case (9):
				return convert(flowSep);
			case (10):
				return convert(flowOct);
			case (11):
				return convert(flowNov);
			case (12):
				return convert(flowDec);

			}
		}

		if (isPrecipitation) {
			switch (monthNumber1To12) {
			case (1):
				return convert(prcipitationJan);
			case (2):
				return convert(prcipitationFeb);
			case (3):
				return convert(prcipitationMar);
			case (4):
				return convert(prcipitationApr);
			case (5):
				return convert(prcipitationMay);
			case (6):
				return convert(prcipitationJun);
			case (7):
				return convert(prcipitationJul);
			case (8):
				return convert(prcipitationAug);
			case (9):
				return convert(prcipitationSep);
			case (10):
				return convert(prcipitationOct);
			case (11):
				return convert(prcipitationNov);
			case (12):
				return convert(prcipitationDec);

			}
		}

		if (isEvapotranspiration) {
			switch (monthNumber1To12) {
			case (1):
				return convert(evaJan);
			case (2):
				return convert(evaFeb);
			case (3):
				return convert(evaMar);
			case (4):
				return convert(evaApr);
			case (5):
				return convert(evaMay);
			case (6):
				return convert(evaJun);
			case (7):
				return convert(evaJul);
			case (8):
				return convert(evaAug);
			case (9):
				return convert(evaSep);
			case (10):
				return convert(evaOct);
			case (11):
				return convert(evaNov);
			case (12):
				return convert(evaDec);

			}
		}

		return null;
	}

	private static double[] convert(double[] vals) {
		double[] logvals = new double[vals.length];

		for (int i = 0; i < vals.length; i++) {
			if (vals[i] == 0) {
				vals[i] = 0.0000001;
				// System.out.println("Convert 0 to 0.0000001");
			}

			logvals[i] = (Math.log(vals[i]));

		}
		return logvals;
	}

	public static void main(String[] args) {
		/* double[] p = WrrProject.reconstruct(1, 0.05, 0.75, true, false, false);
		for (int i = 0; i < p.length; i++) {
			System.out.println(p[i]);
		}*/

		WrrProject.reconstructAllTimeseriesWithRespectToShiftInFlow(1, 0.05, 0.75);
	}
}
