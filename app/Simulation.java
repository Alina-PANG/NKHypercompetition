package app;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import objects.*;
import util.*;

public class Simulation {
	private static Vector<Firm> firms;
	private static Firm[] allFirms;
	public static Landscape landscape;
	private static String[] commonResourceConfig;

	public static void main(String[] args) {
		// Landscape initialization
		String filename = "conf/test1.conf";
		FileIO.loadParameters(filename);
		commonResourceConfig = new String[Globals.getN()];
		landscape  = new Landscape(0, new InfluenceMatrix(Globals.getInfluenceMatrix()));
		printLandscape();

		// Firm initialization
		int firmID = 0;
		firms = new Vector<Firm>();
		allFirms = new Firm[Globals.getNumFirms()];
		for (int i = 0; i < Globals.getNumFirmTypes(); i++) {
			for (int j = 0; j < Globals.getNumFirmsForType(i); j++) {
//				Firm f = new Firm(i, firmID, Globals.getInitResourcesForType(i),
//					Globals.getInnovationForType(i), Globals.getResourcesIncrementForType(i),
//					Globals.getSearchScopeForType(i), Globals.getSearchThresholdForType(i),
//					Globals.getSearchForType(i), Globals.getResourceDecisionForType(i),
//					Globals.getResourceThresholdForType(i));
				Firm f = new Firm(i, firmID, Globals.getInitResourcesForType(i),
//						sharedResIndexToUse, sharedOwnResIndex, isSharingOwnRes,
						Globals.getInnovationForType(i), Globals.getResourcesIncrementForType(i),
						Globals.getSearchScopeForType(i), Globals.getSearchThresholdForType(i),
						Globals.getSearchForType(i), Globals.getResourceDecisionForType(i),
						Globals.getResourceThresholdForType(i));
				firms.add(f);
				firmID++;
				System.out.println(f);
			}
		}
		Globals.setFirms(allFirms);
		//FINISHED

		summarizeCommonResourceConfig();
		Globals.generateRscComponents();

		// System.out.println("COMMON RES CONFIG:\t" + landscape.commonConfigToString());
		// System.out.println(commonConfigToString());
		// for (Firm f : firms) {
		// 	System.out.println(f.toString());
		// }

		/**
		 *  RUN ITERATIONS
		 */
		System.out.println(">>>>>>>>> firm make decisions");
		for (int t = 0; t < Globals.getIterations(); t++) {
			// System.out.print("ITERATION:\t" + t);

			for (Firm f : firms) {
				f.makeDecision();
				 System.out.println(f.toStringWithFitness(landscape));
			}
			summarizeCommonResourceConfig();

			Collections.sort(firms);
			System.out.println(">>>>>>>>> assign rankings: ");
			// assign rankings
			int currentRank = 1;
			double currentFitness = 1.0d;
			for (int i = 0; i < firms.size(); i++) {
				Firm f = (Firm)firms.get(i);
				double focalFitness = f.getFitness();
				System.out.println( "focalFitness:"+ focalFitness);
				if (currentFitness == focalFitness) {
					f.setRank(currentRank);
				} else {
					currentRank = i + 1; 
					f.setRank(currentRank);
					currentFitness = focalFitness;
				}
			}

			System.out.println(">>>>>>>>> results: ");
			// output results
			for (Firm f : firms) {
				// Globals.out.println(t + "\t" + f.toStringWithFitness(landscape));
				Globals.out.println(t + "\t" + f.toStringFull(landscape));
			}
			System.out.println(">>>>>>>>> end ");
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
	
	private static void printLandscape(){
		for (int i = 0; i < (int)(Math.pow(2, Globals.getN())); i++) {
			System.out.println(Location.getLocationStringFromInt(i) + "\t" + landscape.getFitness(Location.getLocationFromInt(i)));
		}
	}
}
