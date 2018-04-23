package app;

import java.util.*;
import objects.*;
import util.*;

public class Simulation {
	private static Vector<Firm> firms; 
//	private static Vector<Consumer> consumers; 
	private static Landscape landscape;
	private static String[] commonResourceConfig = new String[Globals.N];
	
	public static void main(String[] args) {
		FileIO.loadParameters(args[0]);
		// Globals.setInfMatFile(args[0]);
		// System.out.println("InfMatFile:" + Globals.influenceMatrixFile);
		landscape  = new Landscape(0, new InfluenceMatrix(Globals.influenceMatrixFile));
		firms = new Vector<Firm>();			
		for (int i = 0; i < Globals.numFirms; i++) {
			firms.add(new Firm(i));
		}
		// System.out.println("INITIALIZE FIRMS");
		// for (Firm f : firms) {
		// 	System.out.println(f.toString());
		// }
		summarizeCommonResourceConfig();
		//System.out.println("COMMON RES CONFIG:\t" + landscape.commonConfigToString());
		//System.out.println(commonConfigToString());
		// for (Firm f : firms) {
		// 	System.out.println(f.toStringWithFitness(landscape));
		// }

		/**
		 *  RUN ITERATIONS
		 */
		for (int i = 0; i < Globals.iterations; i++) {
			// System.out.print("ITERATION:\t" + i);
			summarizeCommonResourceConfig();
			for (Firm f : firms) {
				f.makeDecision(landscape);
				// System.out.println(f.toStringWithFitness(landscape));
			}
			Collections.sort(firms);
			for (Firm f : firms) {
				Globals.out.println(i + "\t" + f.toStringWithFitness(landscape));
			}
		}
		// for (Firm f : firms) {
		// 	f.makeDecision(landscape);
		// 	System.out.println(f.toStringWithFitness(landscape));
		// }
		
	}
	
	private static void run(int t) {
		// first summarize common resource configurations
		summarizeCommonResourceConfig();


	}

	private static void summarizeCommonResourceConfig() {
		int[] configCounts = new int[Globals.N];
		for (Firm f : firms) {
			for (int i = 0; i < Globals.N; i++) {
		        switch (f.getResourceConfigAt(i)) {
		        	case "0" : 
		        		configCounts[i]--;
		        		break;
		        	case "1" :
		        		configCounts[i]++;
		        		break;
		        	default: 
		        		break;
		        }
			}
		}
		// configCounts[i] < 0 if 0 is most common,
		// 				   > 0 if 1 is most common, 
		//				   = 0 if 0 and 1 are equally likely
		for (int i = 0; i < Globals.N; i++) {
			if (configCounts[i] < 0) {
				commonResourceConfig[i] = "0";
				
			} else if (configCounts[i] > 0) {
				commonResourceConfig[i] = "1";
			} else {
				commonResourceConfig[i] = Integer.toString(Globals.rand.nextInt(2));
			}
			landscape.setCommonResourceConfig(i, commonResourceConfig[i]);
		}
		//System.out.println("SUMMARIZING COMMON RESOURCE CONFIG:\t" + landscape.commonConfigToString());
	}
	
	private static String commonConfigToString() {
		String retString = "";
		for (int i = 0; i < commonResourceConfig.length; i++) {
			retString += commonResourceConfig[i];
		}
		return retString;
	}
	
	
}
