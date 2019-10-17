package util;

import objects.Firm;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Globals {
	/*
	 * Default values 
	 */
	public static PrintWriter out;
	public static long runID = System.currentTimeMillis(); // need?
	public static MersenneTwisterFast rand = new MersenneTwisterFast(runID);

	/** SIMULATION PARAMETERS */
	private static int N = 16;
	private static String outfilename = "testing.txt";
	private static String influenceMatrixFile = "inf/matrix16-3.txt";
	private static int iterations = 3;
	private static int numFirmTypes;
	private static List<String[]> sharedResources;

	// not used
	private static int numNeeds = 10;
	private static int numConsumers = 10;

	/** FIRM PARAMETERS */
	// private static int initResources = 3;
	private static int[] numFirms; 
	private static int[] initResources;
	// private static int numFirms = 1;
	//public static String adaptation = "resources";
	private static double[] innovation; // = 0.0d;
	private static int[] resourcesIncrement; // = 1;
	private static String[] search; // = "experiential";
	private static String[] resourceDecision; // = "absolute";
	private static double[] resourceThreshold; // = 0.05d;
	private static double[] searchThreshold; // = 0.02d;
	private static int[] searchScope; // = 1;

//	// [EDITED] manage shared resources from all firms
	private static int[] rscComponents;
	private static int numRscComponent;
	private static Firm[] firms;


	private static void generateNumRscComponent(){
//		initResources = new int[]{1,2,12,1,1,1,2,1,1,1};
		MersenneTwisterFast rnd = new MersenneTwisterFast();
		numRscComponent = Math.abs(rnd.nextInt()) % (initResources.length/2); // [QUESTION] should I set min - max? randomize or user input?
	}

	public static void generateRscComponents(){
		generateNumRscComponent();
		if(numRscComponent == 0) return;
		System.out.println("##### Number of Components: " + numRscComponent);
		rscComponents = new int[numRscComponent + 1];
		rscComponents[0] = 0;
		MersenneTwisterFast rnd = new MersenneTwisterFast();
		int left = initResources.length;
		for(int i = 1; i < rscComponents.length - 1; i ++){
			int t = Math.abs(rnd.nextInt());
			t  = t % (left - (numRscComponent + 1 - i)) + 1;
			left -= t;
			rscComponents[i] = t;
		}
		rscComponents[rscComponents.length - 1] = left;
		// calculate the cumulative sum
		for(int i = 1; i < rscComponents.length; i ++){
			rscComponents[i] += rscComponents[i - 1];
		}
		printRscComponent();
	}

	private static void printRscComponent(){
		for(int c: rscComponents){
			System.out.print(c+", ");
		}
		System.out.println();
	}

	public static int[] getRscComponents() {
		return rscComponents;
	}

	public static void setRscComponents(int[] rscComponents) {
		Globals.rscComponents = rscComponents;
	}

	public static Firm[] getFirms() {
		return firms;
	}

	public static void setFirms(Firm[] firms) {
		Globals.firms = firms;
	}

	// ======
	public static void addSharedResources(String[] resources){
		if(sharedResources == null) sharedResources = new ArrayList<String[]>();
		sharedResources.add(resources);
	}
	public static String[] getSharedResources(int index){
		if(sharedResources == null) sharedResources = new ArrayList<String[]>();
		return sharedResources.get(index);
	}
	public static int getSharedResourcesSize(){
		if(sharedResources == null) sharedResources = new ArrayList<String[]>();
		return sharedResources.size();
	}

	// FINISHED

	public static List<String[]> getSharedResources() {
		return sharedResources;
	}

	public static void setSharedResources(List<String[]> sharedResources) {
		Globals.sharedResources = sharedResources;
	}



	/* setters */
	/** SIMULATION PARAMETERS */
	public static void setN(int n) {
		N = n;
	}

	public static void setInfluenceMatrix(String matrix) {
		influenceMatrixFile = "inf/" + matrix + ".txt";
	}

	public static void setIterations(int n) {
		iterations = n;
	}

	public static void setOutfile(String outfilename) {
		try {
			if (outfilename.equals("")) {
				// System.out.println("setting STDOUT");
				out = new PrintWriter(System.out, true);
			} else {
				File f = new File(outfilename);
				out = new PrintWriter(new FileOutputStream(outfilename, true), true);
			}
		} catch (IOException io) {
			System.err.println(io.getMessage());
			io.printStackTrace();
		}

	}

	/** FIRM PARAMETERS */

	public static void setParameters(String fullParameterString) {
		//#firms=50,5,1,1,1,0,abs,0.2;50,15,1,1,1,0,abs,0.2
		//#firms=numFirms,initResources,innovation,resourcesIncrement,searchScope,searchThreshold,resourceDecision,resourceThreshold
		// 1.  Parse by type (delimiter = ;)
		StringTokenizer typeTokenizer = new StringTokenizer(fullParameterString, ";");
		numFirmTypes = typeTokenizer.countTokens();
		// initialize firm parameter arrays
		numFirms = new int[numFirmTypes]; 
		initResources = new int[numFirmTypes];
		innovation = new double[numFirmTypes]; // = 0.0d;
		resourcesIncrement = new int[numFirmTypes]; // = 1;
		searchScope = new int[numFirmTypes]; // = 1;
		searchThreshold = new double[numFirmTypes]; // = 0.02d;
		search = new String[numFirmTypes]; // = "experiential";
		resourceDecision = new String[numFirmTypes]; // = "absolute";
		resourceThreshold = new double[numFirmTypes]; // = 0.05d;

		int firmTypeNum = 0;
		while (typeTokenizer.hasMoreTokens()) {
			// 2. Parse by parameter (delimiter = ,)
			StringTokenizer firmParameterTokenizer = new StringTokenizer(typeTokenizer.nextToken().trim(), ",");
			if (firmParameterTokenizer.countTokens() == 8) { // HARD CODED 8 parameters numFirms,initResources,innovation,resourcesIncrement,searchScope,searchThreshold,resourceDecision,resourceThreshold; #9 is search (fixed at "experiential")
				numFirms[firmTypeNum] = Integer.parseInt(firmParameterTokenizer.nextToken().trim());
				initResources[firmTypeNum] = Integer.parseInt(firmParameterTokenizer.nextToken().trim());
				innovation[firmTypeNum] = Double.parseDouble(firmParameterTokenizer.nextToken().trim());
				resourcesIncrement[firmTypeNum] = Integer.parseInt(firmParameterTokenizer.nextToken().trim());
				searchScope[firmTypeNum] = Integer.parseInt(firmParameterTokenizer.nextToken().trim());
				searchThreshold[firmTypeNum] = Double.parseDouble(firmParameterTokenizer.nextToken().trim());
				search[firmTypeNum] = "experiential";
				resourceDecision[firmTypeNum] = firmParameterTokenizer.nextToken().trim();
				resourceThreshold[firmTypeNum] = Double.parseDouble(firmParameterTokenizer.nextToken().trim());

		    } else {
		    	System.err.println("INCORRECT PARAMETER COUNT ERROR: each firm type must have 8 comma-delimited parameters (numFirms,initResources,innovation,resourcesIncrement,searchScope,searchThreshold,resourceDecision,resourceThreshold)");
		    	System.exit(0);
			}
			firmTypeNum++;
		}
	}
	

	/** OLD: no heterogeneity of firm types 
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
	*/
	/* END setters */

	/* getters */
	public static int getN() {
		return N;
	}

	public static String getInfluenceMatrix() {
		return influenceMatrixFile;
	}

	public static int getIterations() {
		return iterations;
	}

	public static String getOutfilename() {
		return "out/" + outfilename;
	}


	public static int getNumFirms() {
		int totalFirms = 0;
		for (int i = 0; i < numFirms.length; i++) {
			totalFirms += numFirms[i];
		}
		return totalFirms;
	}

	public static int getNumFirmTypes() {
		return numFirmTypes;
	}

	public static int getNumFirmsForType(int i) {
		return numFirms[i];
	}

	public static int getInitResourcesForType(int i) {
		return initResources[i];
	}

	public static int getNumNeeds() {
		return numNeeds;
	}

	public static int getNumConsumers() {
		return numConsumers;
	}

	// public static String getAdaptation() {
	// 	return adaptation;
	// }

	public static double getInnovationForType(int i) {
		return innovation[i];
	}

	public static int getResourcesIncrementForType(int i) {
		return resourcesIncrement[i];
	}

	public static String getSearchForType(int i) {
		return search[i];
	}

	public static int getSearchScopeForType(int i) {
		return searchScope[i];
	}

	public static String getResourceDecisionForType(int i) {
		return resourceDecision[i];
	}

	public static double getResourceThresholdForType(int i) {
		return resourceThreshold[i];
	}

	public static double getSearchThresholdForType(int i) {
		return searchThreshold[i];
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
