package app;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import objects.*;
import util.*;

public class Simulation {
	private static Vector<Firm> firms;
	public static Landscape landscape;
	private static String[] commonResourceConfig;

	private static void printLandsacpe(){
		System.out.println("\n***** Simulation.java: Landscape ******");
		 for (int i = 0; i < (int)(Math.pow(2, Globals.getN())); i++) {
			 System.out.println(Location.getLocationStringFromInt(i) + "\t" + landscape.getFitness(Location.getLocationFromInt(i)));
		 }
	}
	
	public static void main(String[] args) {
		// LANDSCAPE INITIALIZATION
		String filename = "conf/test1.conf";
//		FileIO.loadParameters(args[0]);
		FileIO.loadParameters(filename);
		commonResourceConfig = new String[Globals.getN()];
		landscape  = new Landscape(0, new InfluenceMatrix(Globals.getInfluenceMatrix()));
//		printLandsacpe();

		// COMPONENT INITIALIZATION
		Globals.setComponents();

		// FIRM INITIALIZATION
		int firmID = 0;
		firms = new Vector<Firm>();

		for (int i = 0; i < Globals.getNumFirmTypes(); i++) {
			for (int j = 0; j < Globals.getNumFirmsForType(i); j++) {
				firms.add(new Firm(i, firmID, Globals.getInitResourcesForType(i), 
					Globals.getInnovationForType(i), Globals.getResourcesIncrementForType(i), 
					Globals.getSearchScopeForType(i), Globals.getSearchThresholdForType(i), 
					Globals.getSearchForType(i), Globals.getResourceDecisionForType(i), 
					Globals.getResourceThresholdForType(i),

					Globals.getComponentBorrowingInnovationForType(i),
					Globals.getComponentBorrowingThresholdForType(i),

					Globals.getComponentSwitchingInnovationForType(i),
					Globals.getComponentSwitchingThresholdForType(i),

					Globals.getComponentLendingInnovationForType(i),
					Globals.getComponentLendingThresholdForType(i)));
				firmID++;
			}
		}

		summarizeCommonResourceConfig();

		/**
		 *  RUN ITERATIONS
		 */
		for (int t = 0; t < Globals.getIterations(); t++) {
			Globals.refreshLendingFirms(); // edited: clean up the lending firm list -> maintaining cost & benefit for each period - NPV & PV issue?
			System.out.println("====== Changing/Adding/Dropping Resources");
			for (Firm f : firms) {
				f.makeDecision();
			}
			System.out.println("====== Lending");
			for (Firm f : firms) {
				f.componentOperations(2);
			}
			Globals.printSharingFirms();
			System.out.println("====== Switching");
			for (Firm f : firms) {
				f.componentOperations(1);
			}
			System.out.println("====== Borrowing");
			for (Firm f : firms) {
				f.componentOperations(0);
			}

			summarizeCommonResourceConfig();

			Collections.sort(firms);

			// assign rankings
			int currentRank = 1;
			double currentFitness = 1.0d;
			for (int i = 0; i < firms.size(); i++) {
				Firm f = (Firm)firms.get(i);
				double focalFitness = f.getFitness();
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
		System.out.println("\n***** Simulation.java: SUMMARIZING COMMON RESOURCE CONFIG:\t" + landscape.commonConfigToString());
	}
	
	private static String commonConfigToString() {
		String retString = "";
		for (int i = 0; i < commonResourceConfig.length; i++) {
			retString += commonResourceConfig[i];
		}
		return retString;
	}
	
	
}
