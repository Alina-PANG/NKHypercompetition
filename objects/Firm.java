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
	// NEED? dictionary of resource connections

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
	
	// NO NEED; FITNESS IS ALWAYS CALCULATED ON THE FLY
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


	public void makeDecision() { // with innovation
		// addResource with prob then search -- i.e., search always happens 
		// how about drop resources?  -- for now, use the same probability (but independently drawn) to also drop

		/* Jan 17 2019: searchExhaustive no longer implemented */
		/*
		if (Globals.getSearch().equals("experiential")) {
			searchExperiential();
		} else {
			searchExhaustive();
		}
		*/

		// [TODO] search first then consider add OR drop depending on which is better
		searchExperiential();

		// if (Globals.getInnovation() >= Globals.rand.nextDouble()) {
		// 	dropResource();
		// } 
		// if (Globals.getInnovation() >= Globals.rand.nextDouble()) {
		// 	addResource();
		// } 
		if (Globals.getInnovation() >= Globals.rand.nextDouble()) {
			String[] addResourceConfig = new String[Globals.getN()];
			System.arraycopy(considerAddResource(), 0, addResourceConfig, 0, addResourceConfig.length);
			String[] dropResourceConfig = new String[Globals.getN()];
			System.arraycopy(considerDropResource(), 0, dropResourceConfig, 0, dropResourceConfig.length);

			double currentFitness = Simulation.landscape.getFitness(resourceConfig);
			double addResourceUtility = Simulation.landscape.getFitness(addResourceConfig);
			double dropResourceUtility = Simulation.landscape.getFitness(dropResourceConfig);

			int numCurrentResources = 0;
			for (int i = 0; i < resources.length; i++) {
				if (resources[i]) { 
					numCurrentResources++; 
				}
			}

			int numResourcesToAdd = 0; 
			for (int i = 0; i < addResourceConfig.length; i++) {
				if (!addResourceConfig[i].equals(" ")) { 
					numResourcesToAdd++; 
				}
			}
			numResourcesToAdd = numResourcesToAdd - numCurrentResources;

			int numResourcesToDrop = 0; 
			for (int i = 0; i < dropResourceConfig.length; i++) {
				if (!dropResourceConfig[i].equals(" ")) { 
					numResourcesToDrop++; 
				}
			}
			numResourcesToDrop = numCurrentResources - numResourcesToDrop;



			// ABSOLUTE VS. NORMALIZED DECISION MAKING
			if (Globals.getResourceDecision().equals("absolute")) {
				if (addResourceUtility - Globals.getResourceThreshold() > dropResourceUtility + Globals.getResourceThreshold()) {
					// add is better
					if (addResourceUtility - currentFitness - Globals.getResourceThreshold() > 0) {
						// AND it's a good move
						System.arraycopy(addResourceConfig, 0, resourceConfig, 0, addResourceConfig.length);
					}
				} else {
					// drop is better
					if (dropResourceUtility - currentFitness + Globals.getResourceThreshold() > 0) {
						// AND it's a good move
						System.arraycopy(dropResourceConfig, 0, resourceConfig, 0, dropResourceConfig.length);
					}
				}
			} else { // getResourceDecision() == "relative" **** ACTUALLY WE'RE NOT RUNNING THIS FOR NOW.  SO THIS PART HASN'T BEEN FULLY TESTED
				if ((addResourceUtility/(numCurrentResources + numResourcesToAdd)) - Globals.getResourceThreshold() > (dropResourceUtility/(numCurrentResources - 1)) + Globals.getResourceThreshold()) {
					// add is better 
					// currentFitness is out of numResources whereas addResourceUtility is out of (numResources + 1)
					// if ((addResourceUtility/(numCurrentResources + 1)) > (currentFitness/numCurrentResources) + Globals.getResourceThreshold()) {
					if ((addResourceUtility/(numCurrentResources + numResourcesToAdd)) > (currentFitness/numCurrentResources) + Globals.getResourceThreshold()) {	
						System.arraycopy(addResourceConfig, 0, resourceConfig, 0, addResourceConfig.length);
					} // else  do nothing
				} else {
					// drop is better
					// currentFitness is out of numResources whereas addResourceUtility is out of (numResources + 1)
					if ((dropResourceUtility/(numCurrentResources - 1)) > (currentFitness/numCurrentResources) - Globals.getResourceThreshold()) {
						System.arraycopy(dropResourceConfig, 0, resourceConfig, 0, dropResourceConfig.length);
					} // else  do nothing
				}
			}
			syncResources(); // resets bool resources[] 
		} 
	}

	private void addResource() {
		//double addResourceUtility = 0.0d;
		double currentFitness = Simulation.landscape.getFitness(resourceConfig);
		// System.out.println(firmID + "\t" + getResourceConfigString() + "\t" + currentFitness + "\tmaking decision");
		
		// get current number of resources available to the firm
		int numCurrentResources = 0;
		for (int i = 0; i < resources.length; i++) {
			if (resources[i]) { 
				numCurrentResources++; 
			}
		}

		// add resource config: create copy of current resourceConfig
		String[] addResourceConfig = new String[Globals.getN()];
		System.arraycopy(resourceConfig, 0, addResourceConfig, 0, resourceConfig.length);

		// need to pick 
		int numResourcesToAdd = Globals.rand.nextInt(Math.min(Globals.getN() - numCurrentResources + 1, Globals.getResourcesIncrement())) + 1;

		// create copy of resources so that we can update 
		boolean[] resourcesCopy = new boolean[Globals.getN()];
		System.arraycopy(resources, 0, resourcesCopy, 0, resources.length);

		for (int j = 0; j < numResourcesToAdd; j++) {
			try {
				int resourceToAdd = Globals.rand.nextInt(Globals.getN() - numCurrentResources - j);
				int count = 0;
				for (int i = 0; i < resourcesCopy.length; i++) {
					if (!resourcesCopy[i]) {
						if (count == resourceToAdd) {
							// ADD RESOURCE WITH RANDOM SETTING 
							// !! change to setting with higher utility?
							addResourceConfig[i] = Integer.toString(Globals.rand.nextInt(2)); 
							resourcesCopy[i] = true;
							break;
						}
						count++;
					}
				}
			} catch (java.lang.IllegalArgumentException ex) {
				// do nothing
			}
		}

		double addResourceUtility = Simulation.landscape.getFitness(addResourceConfig);

		// ABSOLUTE VS. NORMALIZED DECISION MAKING
		if (Globals.getResourceDecision().equals("absolute")) {
			if (addResourceUtility > currentFitness + Globals.getResourceThreshold()) {
				System.arraycopy(addResourceConfig, 0, resourceConfig, 0, addResourceConfig.length);
			} // else do nothing
		} else {
			// currentFitness is out of numResources whereas addResourceUtility is out of (numResources + 1)
			// if ((addResourceUtility/(numCurrentResources + 1)) > (currentFitness/numCurrentResources) + Globals.getResourceThreshold()) {
			if ((addResourceUtility/(numCurrentResources + numResourcesToAdd)) > (currentFitness/numCurrentResources) + Globals.getResourceThreshold()) {	
				System.arraycopy(addResourceConfig, 0, resourceConfig, 0, addResourceConfig.length);
			} // else  do nothing
		}
		syncResources(); // resets bool resources[] 
	}

	private String[] considerAddResource() {
		//double addResourceUtility = 0.0d;
		// double currentFitness = Simulation.landscape.getFitness(resourceConfig);
		// System.out.println(firmID + "\t" + getResourceConfigString() + "\t" + currentFitness + "\tmaking decision");
		
		// get current number of resources available to the firm
		int numCurrentResources = 0;
		for (int i = 0; i < resources.length; i++) {
			if (resources[i]) { 
				numCurrentResources++; 
			}
		}

		// add resource config: create copy of current resourceConfig
		String[] addResourceConfig = new String[Globals.getN()];
		System.arraycopy(resourceConfig, 0, addResourceConfig, 0, resourceConfig.length);

		// need to pick 
		int numResourcesToAdd = Globals.rand.nextInt(Math.min(Globals.getN() - numCurrentResources + 1, Globals.getResourcesIncrement())) + 1;

		// create copy of resources so that we can update 
		boolean[] resourcesCopy = new boolean[Globals.getN()];
		System.arraycopy(resources, 0, resourcesCopy, 0, resources.length);

		for (int j = 0; j < numResourcesToAdd; j++) {
			try {
				int resourceToAdd = Globals.rand.nextInt(Globals.getN() - numCurrentResources - j);
				int count = 0;
				for (int i = 0; i < resourcesCopy.length; i++) {
					if (!resourcesCopy[i]) {
						if (count == resourceToAdd) {
							// ADD RESOURCE WITH RANDOM SETTING 
							// !! change to setting with higher utility?
							addResourceConfig[i] = Integer.toString(Globals.rand.nextInt(2)); 
							resourcesCopy[i] = true;
							break;
						}
						count++;
					}
				}
			} catch (java.lang.IllegalArgumentException ex) {
				// do nothing
			}
		}
		return addResourceConfig;
	}

	/* TODO */
	private void dropResource() {
		// FOR NOW WE'LL ONLY CONSIDER DROPPING 1 RESOURCE AT A TIME AND ONLY WHEN numCurrentResources > 2
		//double addResourceUtility = 0.0d;
		double currentFitness = Simulation.landscape.getFitness(resourceConfig);
		// System.out.println(firmID + "\t" + getResourceConfigString() + "\t" + currentFitness + "\tmaking decision");
		
		// get current number of resources available to the firm
		int numCurrentResources = 0;
		for (int i = 0; i < resources.length; i++) {
			if (resources[i]) { 
				numCurrentResources++; 
			}
		}
		// drop resource config: copy of current resourceConfig
		String[] dropResourceConfig = new String[Globals.getN()];
		System.arraycopy(resourceConfig, 0, dropResourceConfig, 0, resourceConfig.length);

		// System.out.println("numCurrentResources:" + numCurrentResources);
		// if a firm has only 1 (last) resource, it cannot drop it.  
		if (numCurrentResources > 1) {

			// [TODO] CURRENTLY ONLY DROPPING 1 RESOURCE AT A TIME -- CHANGE TO UP TO ResourceThreshold?
			int resourceToDrop = Globals.rand.nextInt(numCurrentResources);
			int count = 0;
			for (int i = 0; i < resources.length; i++) {
				if (resources[i]) {
					if (count == resourceToDrop) {
						// ADD RESOURCE WITH RANDOM SETTING 
						// !! change to setting with higher utility?
						dropResourceConfig[i] = " ";
						break;
					}
					count++;
				}
			}
			
			double dropResourceUtility = Simulation.landscape.getFitness(dropResourceConfig);

			// ABSOLUTE VS. NORMALIZED DECISION MAKING
			if (Globals.getResourceDecision().equals("absolute")) {
				if (dropResourceUtility < currentFitness - Globals.getResourceThreshold()) {
					System.arraycopy(dropResourceConfig, 0, resourceConfig, 0, dropResourceConfig.length);
				} // else do nothing
			} else {
				// currentFitness is out of numResources whereas addResourceUtility is out of (numResources + 1)
				if ((dropResourceUtility/(numCurrentResources - 1)) > (currentFitness/numCurrentResources) - Globals.getResourceThreshold()) {
					System.arraycopy(dropResourceConfig, 0, resourceConfig, 0, dropResourceConfig.length);
				} // else  do nothing
			}

			syncResources(); // resets bool resources[] 
		}

	}

	private String[] considerDropResource() {
		// FOR NOW WE'LL ONLY CONSIDER DROPPING 1 RESOURCE AT A TIME AND ONLY WHEN numCurrentResources > 2
		//double addResourceUtility = 0.0d;
		double currentFitness = Simulation.landscape.getFitness(resourceConfig);
		// System.out.println(firmID + "\t" + getResourceConfigString() + "\t" + currentFitness + "\tmaking decision");
		
		// get current number of resources available to the firm
		int numCurrentResources = 0;
		for (int i = 0; i < resources.length; i++) {
			if (resources[i]) { 
				numCurrentResources++; 
			}
		}
		// drop resource config: copy of current resourceConfig
		String[] dropResourceConfig = new String[Globals.getN()];
		System.arraycopy(resourceConfig, 0, dropResourceConfig, 0, resourceConfig.length);

		// System.out.println("numCurrentResources:" + numCurrentResources);
		// if a firm has only 1 (last) resource, it cannot drop it.  
		if (numCurrentResources > 1) {

			// [TODO] CURRENTLY ONLY DROPPING 1 RESOURCE AT A TIME -- CHANGE TO UP TO ResourceThreshold?
			int resourceToDrop = Globals.rand.nextInt(numCurrentResources);
			int count = 0;
			for (int i = 0; i < resources.length; i++) {
				if (resources[i]) {
					if (count == resourceToDrop) {
						// ADD RESOURCE WITH RANDOM SETTING 
						// !! change to setting with higher utility?
						dropResourceConfig[i] = " ";
						break;
					}
					count++;
				}
			}			
		}
		return dropResourceConfig;
	}

	private void searchExperiential() { // search one-off changes in existing resources
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
		
		// create copy of resources so that we can update 
		boolean[] resourcesCopy = new boolean[Globals.getN()];
		System.arraycopy(resources, 0, resourcesCopy, 0, resources.length);

		// determine how many resrouces to change.
		int numResourcesToChange = Globals.rand.nextInt(Globals.getSearchScope()) + 1;
		// shouldn't always be long jumps, so can consider UP TO searchScope changes
		// int numResourcesToChange = Globals.getSearchScope() + 1;
		
		// numResources could be smaller than numResourcesToChange so we need to cap it at numResources
		for (int j = 0; j < Math.min(numResourcesToChange, numResources); j++) {
			int resourceToChange = Globals.rand.nextInt(numResources); 
			int count = 0;
			for (int i = 0; i < resources.length; i++) {
				if (resourcesCopy[i]) {
					if (count == resourceToChange) {
						resourcesCopy[i] = false; // this way we know the current resource has been changed and won't be changed again
						numResources--;
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

		}

		//System.out.println("SearchConfig: \n" + Globals.arrayToString(searchConfig));
		double searchUtility = Simulation.landscape.getFitness(searchConfig);

		// if (searchUtility > currentFitness) {
		// 		System.arraycopy(searchConfig, 0, resourceConfig, 0, searchConfig.length);
		// }  else {
		// 	// do nothing
		// }

		// ABSOLUTE VS. NORMALIZED DECISION MAKING --> here it doesn't make a difference as the number of resources is the same
		// [TODO] but how do we make it more costly for long jumps???
		if (Globals.getResourceDecision().equals("absolute")) {
			if (searchUtility > currentFitness + Globals.getSearchThreshold()) {
				System.arraycopy(searchConfig, 0, resourceConfig, 0, searchConfig.length);
			} // else do nothing
		} else {
			// currentFitness is out of numResources whereas addResourceUtility is out of (numResources + 1)
			// [NOTE] this is the same as ABSOLUTE except for multiplying of numResourcesToChange but the cost scale is a bit off
			if ((searchUtility/(numResources)) > (currentFitness/numResources) + numResourcesToChange*Globals.getSearchThreshold()) {
				System.arraycopy(searchConfig, 0, resourceConfig, 0, searchConfig.length);
			} // else  do nothing
		}
		// syncResources(); // resets bool resources[] -- NO NEED FOR SEARCH AS RESOURCECONFIG DOES NOT CHANGE


	}

	/* TODO
		- implement searchScope so that long jumps are possible.  
		- For now, we'll implement searchScope = 1 or 2 but if we need >3 then we'll likely need a more general approach with recursion
		- Jan 17, 2019: We'll not implement searchExhaustive --> unrealistic
	 */
	/*
	private void searchExhaustive() { // search one-off changes in existing resources
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
		
		if (Globals.getSearchScope() == 1) {
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
		} else if (Globals.getSearchScope() == 2) {
			for (int i = 0; i > resources.length; i++) {
				String[] searchConfig = new String[Globals.getN()];
				System.arraycopy(resourceConfig, 0, searchConfig, 0, resourceConfig.length);
				if (resources[i]) {
					if (resourceConfig[i].equals("0")) { 
						searchConfig[i] = "1";
					} else {
						searchConfig[i] = "0";
					}
					for (int j = 0; j > resources.length; j++) {
						if (resources[j]) {
							if (resourceConfig[j].equals("0")) { 
								searchConfig[j] = "1";
							} else {
								searchConfig[j] = "0";
							}
						}
						if (Simulation.landscape.getFitness(searchConfig) > bestAlternative) {
							System.arraycopy(searchConfig, 0, bestSearchConfig, 0, searchConfig.length);
							bestAlternative = Simulation.landscape.getFitness(searchConfig);
						}
					}
				} 
			}

		} else {
			// this shouldn't happen 
	    	System.err.println("INCORRECT PARAMETER ERROR: searchScope must either be 1 or 2 (for now)");
	    	System.exit(0);
		}

	
		//System.out.println("SearchConfig: \n" + Globals.arrayToString(searchConfig));
		double bestSearchUtility = Simulation.landscape.getFitness(bestSearchConfig);

		if (bestSearchUtility > currentFitness) {
				System.arraycopy(bestSearchConfig, 0, resourceConfig, 0, bestSearchConfig.length);
		}  else {
			// do nothing
		}
	}
	*/

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

	public double getFitness(Landscape l) {
		return l.getFitness(resourceConfig);
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

	private void syncResources() {
		for (int i = 0; i < resourceConfig.length; i++) {
			if (resourceConfig[i].equals(" ")) {
				resources[i] = false;
			} else {
				resources[i] = true;
			}
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

	/* for testing only */
	public static void main(String[] args) {
		// Firm f = new Firm();
		// System.out.println(f.toString());
		//f.makeDecision();
	}
}
