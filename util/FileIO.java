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
				Globals.setN(Integer.parseInt(p.getProperty("N")));
				Globals.setNumResources(Integer.parseInt(p.getProperty("numResources")));
				Globals.setInfluenceMatrix(p.getProperty("influenceMatrixFile"));
				Globals.setNumFirms(Integer.parseInt(p.getProperty("numFirms")));
				Globals.setIterations(Integer.parseInt(p.getProperty("iterations")));
				Globals.setAdaptation(p.getProperty("adaptation"));
				if (p.getProperty("outfile") == null) {
					Globals.setOutfile("");
				} else {
					Globals.setOutfile(p.getProperty("outfile"));
				}
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