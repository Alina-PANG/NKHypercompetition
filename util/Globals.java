package util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.IOException;

public class Globals {
	/*
	 * Default values 
	 */
	public static int N = 20;
	public static long runID = 0l; //System.currentTimeMillis(); // need?
	public static MersenneTwisterFast rand = new MersenneTwisterFast(runID);
	public static int numResources = 4;
	public static int numNeeds = 10;
	public static int numFirms = 100;
	public static int numConsumers = 10;
	public static String influenceMatrixFile = "inf/matrix12.txt";
	public static int iterations = 100;
	public static String adaptation = "resources";
	public static String outfilename = "output.txt";

	public static PrintWriter out;

	/* setters */

	public static void setN(int n) {
		N = n;
	}

	public static void setNumResources(int n) {
		numResources = n;
	}

	public static void setNumFirms(int n) {
		numFirms = n;
	}

	public static void setInfluenceMatrix(String matrix) {
		influenceMatrixFile = "inf/" + matrix + ".txt";
	}

	public static void setIterations(int n) {
		iterations = n;
	}

	public static void setAdaptation(String adapt) {
		adaptation = adapt;
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

	public static int getNumResources() {
		return numResources;
	}

	public static int getNumFirms() {
		return numFirms;
	}

	public static String getInfluenceMatrix() {
		return influenceMatrixFile;
	}

	public static int getIterations() {
		return iterations;
	}

	public static String getAdaptation() {
		return adaptation;
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
