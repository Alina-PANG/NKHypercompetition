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
		setResources(Globals.numResources);
		setResourceConfig();
		// fitness = Simulation.landscape.getFitness(resourceConfig);
	}
	
	public Firm(int id) {
		firmID = id;
		setResources(Globals.numResources);
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
		resources = new boolean[Globals.N];
		for (int i = 0; i < indices.length; i++) {
			resources[indices[i]] = true;
		}
		setResourceConfig();
		// Simulation.landscape.getFitness(resourceConfig);
	}
	
	// initialize firm resources
	private void setResources(int size) {
		resources = new boolean[Globals.N];
		int resourcesSet = 0;
		while (resourcesSet < size) {
			int r = Globals.rand.nextInt(Globals.N);
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
		resourceConfig = new String[Globals.N];
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

	public void makeDecision() {
		// choices: 1. search (experiential); 2. acquire new resource
		
		//double addResourceUtility = 0.0d;
		double currentFitness = Simulation.landscape.getFitness(resourceConfig);
		// System.out.println(firmID + "\t" + getResourceConfigString() + "\t" + currentFitness + "\tmaking decision");
		int numResources = 0;
		for (int i = 0; i < resources.length; i++) {
			if (resources[i]) { numResources++; }
		}
		//System.out.println("ResourceConfig: \n" + Globals.arrayToString(resourceConfig));
		
		// search config
		String[] searchConfig = new String[Globals.N];
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

		// add resource config
		String[] addResourceConfig = new String[Globals.N];
		System.arraycopy(resourceConfig, 0, addResourceConfig, 0, resourceConfig.length);
		int resourceToAdd = Globals.rand.nextInt(Globals.N - numResources);
		count = 0;
		for (int i = 0; i < resources.length; i++) {
			if (!resources[i]) {
				if (count == resourceToAdd) {
					addResourceConfig[i] = Integer.toString(Globals.rand.nextInt(2));
					break;
				}
				count++;
			}
		}

		//System.out.println("SearchConfig: \n" + Globals.arrayToString(searchConfig));
		double searchUtility = Simulation.landscape.getFitness(searchConfig);
		double addResourceUtility = Simulation.landscape.getFitness(addResourceConfig);

		if (Globals.adaptation.equals("search")) {
			if (searchUtility > currentFitness) {
					System.arraycopy(searchConfig, 0, resourceConfig, 0, searchConfig.length);
			}  else {
				// do nothing
			}
		} else if (Globals.adaptation.equals("resources")) {
			if (searchUtility > currentFitness) {
				if (addResourceUtility > searchUtility) {
					System.arraycopy(addResourceConfig, 0, resourceConfig, 0, addResourceConfig.length);
				} else {
					System.arraycopy(searchConfig, 0, resourceConfig, 0, searchConfig.length);
				}
			} else {
				if (addResourceUtility > currentFitness) {
					System.arraycopy(addResourceConfig, 0, resourceConfig, 0, addResourceConfig.length);
				} else {
					// do nothing
				}
			}
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
		String retString = Globals.outfilename + "\t" + firmID + "\t" + rank + "\t" + getResourceConfigTabDelimited() + "\t" + l.getFitness(resourceConfig);
		return retString;
	}

	public static void main(String[] args) {
		// Firm f = new Firm();
		// System.out.println(f.toString());
		//f.makeDecision();

	}
}
