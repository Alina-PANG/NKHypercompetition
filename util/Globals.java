package util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.IOException;

public class Globals {
	/*
	 * Default values 
	 */
	public static PrintWriter out;
	public static long runID = System.currentTimeMillis(); // need?
	public static MersenneTwisterFast rand = new MersenneTwisterFast(runID);

	private static int N = 16;
	private static int initResources = 3;
	private static int numFirms = 1;
	private static int numNeeds = 10;
	private static int numConsumers = 10;
	private static String influenceMatrixFile = "inf/matrix16-3.txt";
	private static int iterations = 3;
	//public static String adaptation = "resources";
	private static double innovation = 0.0d;
	private static int resourcesIncrement = 1;
	private static String search = "experiential";
	private static String resourceDecision = "absolute";
	private static double resourceThreshold = 0.05d;
	private static double searchThreshold = 0.02d;
	private static int searchScope = 1;
	private static String outfilename = "testing.txt";

	/* setters */

	public static void setN(int n) {
		N = n;
	}

	public static void setInitResources(int n) {
		initResources = n;
	}

	public static void setNumFirms(int n) {
		numFirms = n;
	}

	public static void setNumNeeds(int n) {
		numNeeds = n;
	}

	public static void setNumConsumers(int n) {
		numConsumers = n;
	}

	public static void setInfluenceMatrix(String matrix) {
		influenceMatrixFile = "inf/" + matrix + ".txt";
	}

	public static void setIterations(int n) {
		iterations = n;
	}

	// public static void setAdaptation(String adapt) {
	// 	adaptation = adapt;
	// }

	public static void setResourceDecision(String decision) {
	    if (decision.equals("abs") || decision.equals("absolute") ) {
	    	resourceDecision = "absolute";
	    } else if (decision.equals("rel") || decision.equals("relative") ) {
	    	resourceDecision = "relative";
	    } else {
	    	System.err.println("INCORRECT PARAMETER ERROR: resourceDecision (" + decision + ")must either be \"abs\" (absolute) or \"rel\" (relative)");
	    	System.exit(0);
	    } 
	}

	public static void setInnovation(double d) {
		if ((d < 0.0d) || (d > 1.0d)) {
	    	System.err.println("INCORRECT PARAMETER ERROR: innovation (" + d + ") must either be between 0 and 1 (inclusive)");
	    	System.exit(0);
	} else {
			innovation = d;
		}
	}

	public static void setResourceThreshold(double d) {
		if ((d < 0.0d) || (d > 1.0d)) {
	    	System.err.println("INCORRECT PARAMETER ERROR: resourceThreshold (" + d + ") must either be between 0 and 1 (inclusive)");
	    	System.exit(0);
	} else {
			resourceThreshold = d;
		}
	}

	public static void setSearchThreshold(double d) {
		if ((d < 0.0d) || (d > 1.0d)) {
	    	System.err.println("INCORRECT PARAMETER ERROR: searchThreshold (" + d + ") must either be between 0 and 1 (inclusive)");
	    	System.exit(0);
	} else {
			searchThreshold = d;
		}
	}

	public static void setResourcesIncrement(int n) {
		resourcesIncrement = n;
	}

	public static void setSearch(String s) {
	    if (s.equals("experiential") || s.equals("exhaustive") ) {
	    	search = s;
	    } else {
	    	System.err.println("INCORRECT PARAMETER ERROR: search must either be \"experiential\" or \"rel\" (relative)");
	    	System.exit(0);
	    } 
	}

	public static void setSearchScope(int n) {
		searchScope = n;
	}

	public static void setOutfile(String file) {
		outfilename = "out/" + file;
		try {
			if (outfilename.equals("")) {
				// System.out.println("setting STDOUT");
				out = new PrintWriter(System.out, true);
			} else {
				out = new PrintWriter(new FileOutputStream(outfilename, true), true);
			}
		} catch (IOException io) {
			System.err.println(io.getMessage());
			io.printStackTrace();
		}

	}
	/* END setters */

	/* getters */
	public static int getN() {
		return N;
	}

	public static int getInitResources() {
		return initResources;
	}

	public static int getNumFirms() {
		return numFirms;
	}

	public static int getNumNeeds() {
		return numNeeds;
	}

	public static int getNumConsumers() {
		return numConsumers;
	}

	public static String getInfluenceMatrix() {
		return influenceMatrixFile;
	}

	public static int getIterations() {
		return iterations;
	}

	// public static String getAdaptation() {
	// 	return adaptation;
	// }

	public static double getInnovation() {
		return innovation;
	}

	public static int getResourcesIncrement() {
		return resourcesIncrement;
	}

	public static String getSearch() {
		return search;
	}

	public static int getSearchScope() {
		return searchScope;
	}

	public static String getResourceDecision() {
		return resourceDecision;
	}

	public static double getResourceThreshold() {
		return resourceThreshold;
	}

	public static double getSearchThreshold() {
		return searchThreshold;
	}

	public static String getOutfilename() {
		return "out/" + outfilename;
	}

	/* END getters */

	public static void setInfMatFile(String filename) {
		influenceMatrixFile = "inf/" + filename + ".txt";
	}

	public static String arrayToString(String[] array) {
		String retString = "";
		for (int i = 0; i < array.length; i++) { 
			retString += array[i];
		}
		return retString;
	}
	public static String arrayToString(int[] array) {
		String retString = "";
		for (int i = 0; i < array.length; i++) {
			retString += array[i];
		}
		return retString;
	}

	public static String arrayToString(boolean[] array) {
		String retString = "";
		for (int i = 0; i < array.length; i++) {
			if (array[i]) {
				retString += "T";
			} else {
				retString += "F";
			}
		}
		return retString;
	}
	
}
