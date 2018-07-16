package app;

import java.util.*;
import objects.*;
import util.*;

public class Simulation {
	private static Vector<Firm> firms; 
//	private static Vector<Consumer> consumers; 
	public static Landscape landscape;
	private static String[] commonResourceConfig = new String[Globals.getN()];
	
	public static void main(String[] args) {
		FileIO.loadParameters(args[0]);
		// Globals.setInfMatFile(args[0]);
		// System.out.println("InfMatFile:" + Globals.influenceMatrixFile);
		landscape  = new Landscape(0, new InfluenceMatrix(Globals.getInfluenceMatrix()));

		// output fitness landscape
		// for (int i = 0; i < (int)(Math.pow(2, Globals.N)); i++) {
		// 	System.out.println(Location.getLocationStringFromInt(i) + "\t" + landscape.getFitness(Location.getLocationFromInt(i)));
		// }

		// INITIALIZE FIRMS 
		firms = new Vector<Firm>();			
		for (int i = 0; i < Globals.getNumFirms(); i++) {
			firms.add(new Firm(i));
		}

		summarizeCommonResourceConfig();

		//System.out.println("COMMON RES CONFIG:\t" + landscape.commonConfigToString());
		//System.out.println(commonConfigToString());
		// for (Firm f : firms) {
		// 	System.out.println(f.toStringWithFitness(landscape));
		// }

		/**
		 *  RUN ITERATIONS
		 */
		for (int t = 0; t < Globals.getIterations(); t++) {
			// System.out.print("ITERATION:\t" + t);
			
			for (Firm f : firms) {
				f.makeDecision();
				// System.out.println(f.toStringWithFitness(landscape));
			}
			summarizeCommonResourceConfig();

			Collections.sort(firms);


			// assign rankings
			int currentRank = 1;
			double currentFitness = 1.0d;
			for (int i = 0; i < firms.size(); i++) {
				Firm f = (Firm)firms.get(i);
				double focalFitness = f.getFitness();
				// System.out.println(focalFitness);
				if (currentFitness == focalFitness) {
					f.setRank(currentRank);
				} else {
					currentRank = i + 1; 
					f.setRank(currentRank);
					currentFitness = focalFitness;
				}
			}

			// output results
			for (Firm f : firms) {
				// Globals.out.println(t + "\t" + f.toStringWithFitness(landscape));
				Globals.out.println(t + "\t" + f.toStringFull(landscape));
			}
		}
		
	}
	
	private static void run(int t) {
		// first summarize common resource configurations
		summarizeCommonResourceConfig();


	}

	private static void summarizeCommonResourceConfig() {
		int[] configCounts = new int[Globals.getN()];
		for (Firm f : firms) {
			for (int i = 0; i < Globals.getN(); i++) {
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
		for (int i = 0; i < Globals.getN(); i++) {
			if (configCounts[i] < 0) {
				commonResourceConfig[i] = "0";
				
			} else if (configCounts[i] > 0) {
				commonResourceConfig[i] = "1";
			} else {
				commonResourceConfig[i] = Integer.toString(Globals.rand.nextInt(2));
			}
			landscape.setCommonResourceConfig(i, commonResourceConfig[i]);
		}
		// System.out.println("SUMMARIZING COMMON RESOURCE CONFIG:\t" + landscape.commonConfigToString());
	}
	
	private static String commonConfigToString() {
		String retString = "";
		for (int i = 0; i < commonResourceConfig.length; i++) {
			retString += commonResourceConfig[i];
		}
		return retString;
	}
	
	
}
