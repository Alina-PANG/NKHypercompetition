package util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Properties;

public class FileIO {
	public static void loadParameters(String configFile) {
		/*
		 *	int N = 20;
		 *	int numResources = 4;
		 *	int numFirms = 100;
		 *	String influenceMatrixFile = "inf/matrix12.txt";
		 *	int iterations = 100;
		 *  int adaptation = "resources";
		 *  String outfile = "output.txt";
		*/
		if (!configFile.equals("")) {
			Properties p = new Properties();
			try {
				p.load(new FileInputStream(configFile));
				// simulation parameters
				if (p.getProperty("N") != null) { Globals.setN(Integer.parseInt(p.getProperty("N"))); }
				if (p.getProperty("numResources") != null) { Globals.setNumResources(Integer.parseInt(p.getProperty("numResources"))); }
				if (p.getProperty("influenceMatrixFile") != null) { Globals.setInfluenceMatrix(p.getProperty("influenceMatrixFile")); }
				if (p.getProperty("numFirms") != null) { Globals.setNumFirms(Integer.parseInt(p.getProperty("numFirms"))); }
				if (p.getProperty("iterations") != null) { Globals.setIterations(Integer.parseInt(p.getProperty("iterations"))); }
				if (p.getProperty("adaptation") != null) { Globals.setAdaptation(p.getProperty("adaptation")); }
				if (p.getProperty("outfile") != null) { Globals.setOutfile(p.getProperty("outfile")); }
					else { Globals.setOutfile(""); }
					
			} catch (Exception e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
			} // END try..catch
		}  // end if confFile
	}
	
	public static void printParameters() {
		System.out.println("N: " + Globals.getN());
		System.out.println("numResources: " + Globals.getNumResources());
		System.out.println("numFirms: " + Globals.getNumFirms());
		System.out.println("iterations: " + Globals.getIterations());
		System.out.println("adaptation: " + Globals.getAdaptation());
		System.out.println("outfile: " + Globals.getOutfilename());
	}

	public static void main(String[] args) {
		// for test only
		loadParameters(args[0]);
		printParameters();
	}

}