package objects;

import java.util.*;
import util.Globals;
import util.Landscape;
import app.*;

public class Firm implements Comparable<Firm> {

	private int firmID;
	private ArrayList<Product> products; // can we have overlapping resources for different products?  I think NO 
	private boolean[] resources; 
	private String[] resourceConfig; // array of "0", "1" or " "
	// private double fitness;
	private int rank;

	// random firm with of Globals.numResources resources
	public Firm() {
		setResources(Globals.getInitResources());
		setResourceConfig();
		// fitness = Simulation.landscape.getFitness(resourceConfig);
	}
	
	public Firm(int id) {
		firmID = id;
		setResources(Globals.getInitResources());
		setResourceConfig();
		// fitness = Simulation.landscape.getFitness(resourceConfig);
	}

	// new firm with n resources
	public Firm(int id, int numResources) { 
		firmID = id;
		setResources(numResources);
		setResourceConfig();
		// Simulation.landscape.getFitness(resourceConfig);
	}
	
	// new firm with specific resources
	public Firm(int id, int[] indices) {
		firmID = id;
		resources = new boolean[Globals.getN()];
		for (int i = 0; i < indices.length; i++) {
			resources[indices[i]] = true;
		}
		setResourceConfig();
		// Simulation.landscape.getFitness(resourceConfig);
	}
	
	// initialize firm resources
	private void setResources(int size) {
		resources = new boolean[Globals.getN()];
		int resourcesSet = 0;
		while (resourcesSet < size) {
			int r = Globals.rand.nextInt(Globals.getN());
			if (!resources[r]) {
				resourcesSet++;
				resources[r] = true;
			}
		}
	}
	
	// NO NEED FITNESS IS ALWAYS CALCULATED ON THE FLY
	// public void initFitness() {
	// 	fitness = Simulation.landscape.getFitness(resourceConfig);
	// 	System.out.println("init: " + firmID + "\t" + this.getResourceConfigString() + "\t" + fitness);
	// }

	// randomly set initial configuration of resources
	private void setResourceConfig() {
		resourceConfig = new String[Globals.getN()];
		for (int i = 0; i < resourceConfig.length; i++) {
			if (resources[i]) {
				resourceConfig[i] = Integer.toString(Globals.rand.nextInt(2));
			} else {
				resourceConfig[i] = " ";
			}
		}
	}
	
	
	public boolean isValidResources(int idx) {
		return resources[idx];
	}


	public void makeDecision() { // with digitization
		// addResource with prob then search -- i.e., search always happends 
		if (Globals.getDigitization() >= Globals.rand.nextDouble()) {
			for (int i = 0; i < Globals.getResourcesIncrement(); i++) {
				addResource();
			}
		} 
		if (Globals.getSearch().equals("experiential")) {
			searchExperiential();
		} else {
			searchExhaustive();
		}
	}

	public void addResource() {
		//double addResourceUtility = 0.0d;
		double currentFitness = Simulation.landscape.getFitness(resourceConfig);
		// System.out.println(firmID + "\t" + getResourceConfigString() + "\t" + currentFitness + "\tmaking decision");
		
		// get current number of resources available to the firm
		int numResources = 0;
		for (int i = 0; i < resources.length; i++) {
			if (resources[i]) { numResources++; }
		}
		//System.out.println("ResourceConfig: \n" + Globals.arrayToString(resourceConfig));

		// add resource config
		String[] addResourceConfig = new String[Globals.getN()];
		System.arraycopy(resourceConfig, 0, addResourceConfig, 0, resourceConfig.length);

		try {
			int resourceToAdd = Globals.rand.nextInt(Globals.getN() - numResources);
			int count = 0;
			for (int i = 0; i < resources.length; i++) {
				if (!resources[i]) {
					if (count == resourceToAdd) {
						// ADD RESOURCE WITH RANDOM SETTING 
						// !! change to setting with higher utility?
						addResourceConfig[i] = Integer.toString(Globals.rand.nextInt(2)); 
						break;
					}
					count++;
				}
			}

			//System.out.println("SearchConfig: \n" + Globals.arrayToString(searchConfig));
			double addResourceUtility = Simulation.landscape.getFitness(addResourceConfig);

			if (addResourceUtility > currentFitness) {
				System.arraycopy(addResourceConfig, 0, resourceConfig, 0, addResourceConfig.length);
			} // else  do nothing

		} catch (java.lang.IllegalArgumentException ex) {
			// do nothing
		}
	}

	public void searchExperiential() { // search one-off changes in existing resources
		//double addResourceUtility = 0.0d;
		double currentFitness = Simulation.landscape.getFitness(resourceConfig);
		// System.out.println(firmID + "\t" + getResourceConfigString() + "\t" + currentFitness + "\tmaking decision");
		
		// get current number of resources available to the firm
		int numResources = 0;
		for (int i = 0; i < resources.length; i++) {
			if (resources[i]) { numResources++; }
		}
		//System.out.println("ResourceConfig: \n" + Globals.arrayToString(resourceConfig));
		
		// search config
		String[] searchConfig = new String[Globals.getN()];
		System.arraycopy(resourceConfig, 0, searchConfig, 0, resourceConfig.length);
		int resourceToChange = Globals.rand.nextInt(numResources); 
		int count = 0;
		for (int i = 0; i < resources.length; i++) {
			if (resources[i]) {
				if (count == resourceToChange) {
					if (resourceConfig[i].equals("0")) { 
						searchConfig[i] = "1";
						break;
					} else {
						searchConfig[i] = "0";
						break;
					}
				}
				count++;
			}
		}

		//System.out.println("SearchConfig: \n" + Globals.arrayToString(searchConfig));
		double searchUtility = Simulation.landscape.getFitness(searchConfig);

		if (searchUtility > currentFitness) {
				System.arraycopy(searchConfig, 0, resourceConfig, 0, searchConfig.length);
		}  else {
			// do nothing
		}
	}

	public void searchExhaustive() { // search one-off changes in existing resources
		//double addResourceUtility = 0.0d;
		double currentFitness = Simulation.landscape.getFitness(resourceConfig);
		// System.out.println(firmID + "\t" + getResourceConfigString() + "\t" + currentFitness + "\tmaking decision");
		
		// get current number of resources available to the firm
		int numResources = 0;
		for (int i = 0; i < resources.length; i++) {
			if (resources[i]) { numResources++; }
		}
		//System.out.println("ResourceConfig: \n" + Globals.arrayToString(resourceConfig));
		
		// search config
		String[] bestSearchConfig = new String[Globals.getN()];
		double bestAlternative = 0.0d;
		for (int i = 0; i < resources.length; i++) {
			String[] searchConfig = new String[Globals.getN()];
			System.arraycopy(resourceConfig, 0, searchConfig, 0, resourceConfig.length);

			if (resources[i]) {
				if (resourceConfig[i].equals("0")) { 
					searchConfig[i] = "1";
				} else {
					searchConfig[i] = "0";
				}
				if (Simulation.landscape.getFitness(searchConfig) > bestAlternative) {
					System.arraycopy(searchConfig, 0, bestSearchConfig, 0, searchConfig.length);
					bestAlternative = Simulation.landscape.getFitness(searchConfig);
				}
			}
		}

		//System.out.println("SearchConfig: \n" + Globals.arrayToString(searchConfig));
		double bestSearchUtility = Simulation.landscape.getFitness(bestSearchConfig);

		if (bestSearchUtility > currentFitness) {
				System.arraycopy(bestSearchConfig, 0, resourceConfig, 0, bestSearchConfig.length);
		}  else {
			// do nothing
		}
	}


	public String getResourceConfigAt(int idx) {
		return resourceConfig[idx];
	}
	
	private String getResourcesString() {
		String retString = "";
		for (int i = 0; i < resources.length; i++) {
			if (resources[i]) {
				retString += "1";
			} else {
				retString += "0";
			}
		}		
		return retString;
	}
	
	private String getResourceConfigString() {
		String retString = "";
		for (int i = 0; i < resourceConfig.length; i++) {
			if (resourceConfig[i].equals(" ")) {
				retString += "-";
			} else {
				retString += resourceConfig[i];
			}
		}		
		return retString;
		
	}

	private String getResourceConfigTabDelimited() {
		String retString = "";
		for (int i = 0; i < resourceConfig.length; i++) {
			if (resourceConfig[i].equals(" ")) {
				if (i == 0) {
					retString += "-";					
				} else {
					retString += "\t-";
				}

			} else {
				if (i == 0) {
					retString += resourceConfig[i];
				} else {
					retString += "\t" + resourceConfig[i];
				}
			}
		}		
		return retString;
		
	}

	public double getFitness() {
		return Simulation.landscape.getFitness(resourceConfig);
	}

	public void setRank(int aRank) {
		rank = aRank;
	}

	public int getRank() {
		return rank;
	}

	public int compareTo(Firm compareFirm) {
		double compareFitness = ((Firm)compareFirm).getFitness(); 
		double thisFitness = this.getFitness();
		if(thisFitness < compareFitness) {
			return 1;
		} else if(compareFitness < thisFitness) {
			return -1;
		} else {
			return 0;		
		}
	}	


	public String toString() {
		String retString = firmID + "\t" + getResourceConfigString();
		return retString;
	}

	public String toStringWithFitness(Landscape l) {
		//System.out.println(getResourceConfigString());
		String retString = firmID + "\t" + getResourceConfigString() + "\t" + l.getFitness(resourceConfig);
		return retString;
	}

	public String toStringFull(Landscape l) {
		//System.out.println(getResourceConfigString());
		String retString = Globals.getOutfilename() + "\t" + firmID + "\t" + rank + "\t" + getResourceConfigTabDelimited() + "\t" + l.getFitness(resourceConfig);
		return retString;
	}

	public static void main(String[] args) {
		// Firm f = new Firm();
		// System.out.println(f.toString());
		//f.makeDecision();

	}
}
