package objects;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.util.Pair;
import util.Globals;
import util.Landscape;
import app.*;
import util.MersenneTwisterFast;

public class Firm implements Comparable<Firm> {

	private int firmID;

	// firm parameters
	private int firmType;
	private int initResources;
	private double innovation;
	private int resourcesIncrement;
	private int searchScope;
	private double searchThreshold;
	private double borrowOrSwitchingThreshold;
	private String search;
	private String resourceDecision;
	private double resourceThreshold;

	// [EDITED] shared resources firm attributes declaration
	private HashSet<Integer> sharedRscComponents;
	private boolean isSharingOwnRes;
	private int[] sharedResIndexToUse;
	private Set<Integer> nonControllableIndex;
	private int[][] sharedOwnResIndex;
	private static final int MAX_SHARED_INDEX = 2;

	private List<Pair<Firm, List<Integer>>> borrowedRsc;

	//private ArrayList<Product> products; // can we have overlapping resources for different products?  I think NO 
	private Boolean[] resources;
	private String[] resourceConfig; // array of "0", "1" or " "
	// private double fitness;
	private int rank;
	// NEED? dictionary of resource connections

	// random firm with of Globals.numResources resources
	public Firm() {
		setResources(Globals.getInitResourcesForType(0));
		setResourceConfig();
		// fitness = Simulation.landscape.getFitness(resourceConfig);
	}

	// [EDITED] constructor
	// assume one company only use the shared resource from another companies
	// those resources will be at the beginning of their own resource array
	public Firm(int aType, int id, int anInitResources, boolean isSharingOwnRes, double anInnovation,
				int anResourcesIncrement, int aSearchScope, double aSearchThreshold,
				String aSearch, String aResourceDecision, double aResourceThreshold) {
		System.out.println(anInitResources);
		firmType = aType;
		firmID = id;
		initResources = anInitResources;
		innovation = anInnovation;
		resourcesIncrement = anResourcesIncrement;
		searchScope = aSearchScope;
		searchThreshold = aSearchThreshold;
		search = aSearch;
		resourceDecision = aResourceDecision;
		resourceThreshold = aResourceThreshold;
		sharedRscComponents = new HashSet<>();

		this.nonControllableIndex = new HashSet<Integer>();

		setResources(initResources);
		setResourceConfig();
		if(isSharingOwnRes)
			shareRes();
	}
	// ==========
	public Firm(int aType, int id, int anInitResources, int[] sharedResIndexToUse,int[][] sharedOwnResIndex, boolean isSharingOwnRes, double anInnovation,
				int anResourcesIncrement, int aSearchScope, double aSearchThreshold,
				String aSearch, String aResourceDecision, double aResourceThreshold) {
		System.out.println(anInitResources);
		firmType = aType;
		firmID = id;
		initResources = anInitResources;
		innovation = anInnovation;
		resourcesIncrement = anResourcesIncrement;
		searchScope = aSearchScope;
		searchThreshold = aSearchThreshold;
		search = aSearch;
		resourceDecision = aResourceDecision;
		resourceThreshold = aResourceThreshold;

		this.sharedResIndexToUse = sharedResIndexToUse;
		this.nonControllableIndex = new HashSet<Integer>();
		this.sharedOwnResIndex = sharedOwnResIndex;

		setResources(initResources);
		setResourceConfig(initResources, sharedResIndexToUse);
		if(isSharingOwnRes)
			shareOwnRes();
	}
	// FINISHED

	public Firm(int aType, int id, int anInitResources, double anInnovation,
				int anResourcesIncrement, int aSearchScope, double aSearchThreshold,
				String aSearch, String aResourceDecision, double aResourceThreshold) {
		firmType = aType;
		firmID = id;
		initResources = anInitResources;
		innovation = anInnovation;
		resourcesIncrement = anResourcesIncrement;
		searchScope = aSearchScope;
		searchThreshold = aSearchThreshold;
		search = aSearch;
		resourceDecision = aResourceDecision;
		resourceThreshold = aResourceThreshold;

		setResources(initResources);
		setResourceConfig();
	}

	public Firm(int id) {
		firmID = id;
		setResources(Globals.getInitResourcesForType(0));
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
		resources = new Boolean[Globals.getN()];
		for (int i = 0; i < indices.length; i++) {
			resources[indices[i]] = true;
		}
		setResourceConfig();
		// Simulation.landscape.getFitness(resourceConfig);
	}

	// initialize firm resources
	private void setResources(int size) {
		resources = new Boolean[Globals.getN()];
		Arrays.fill(resources, false);
		int resourcesSet = 0;
		while (resourcesSet < size) {
			int r = Globals.rand.nextInt(Globals.getN());
			if (!resources[r]) {
				resourcesSet++;
				resources[r] = true;
			}
		}
	}


	// [EDITED] assign shared resources to use: by default only use one set of shared resources
	// =======
	private void assignSharedResToUse(){
		MersenneTwisterFast rnd = new MersenneTwisterFast();
		if(Globals.getSharedResourcesSize() == 0) return;
		int d = rnd.nextInt() % Globals.getSharedResourcesSize() + 1;
		sharedResIndexToUse = new int[]{d};
	}

	private void setResourceConfig(int size, int[] sharedResIndexToUse){
		if(sharedResIndexToUse == null){
			assignSharedResToUse();
		}
		setResourceConfig();
		int j = 0;
		if(sharedResIndexToUse != null){
			for(int i: sharedResIndexToUse){
				if(i >= Globals.getSharedResourcesSize()) continue;
				String[] temp = Globals.getSharedResources(i);
				for(String t: temp){
					if(temp.equals(" ")) continue;
					nonControllableIndex.add(i);
					resourceConfig[j] = t;
					resources[j] = true;
					j ++;
				}
			}
		}
	}

	private void initializeSharedOwnResIndex(){
		// maximum share 3 bundles
		MersenneTwisterFast rnd = new MersenneTwisterFast();
		int numOfSharedBundles = rnd.nextInt() % MAX_SHARED_INDEX + 1;
		System.out.println(numOfSharedBundles);
		sharedOwnResIndex = new int[numOfSharedBundles][2];
		System.out.println("Sharing "+rnd+" resource bundles");

		MersenneTwisterFast rnd2 = new MersenneTwisterFast();
		// allow overlaps in each bundle group
		for(int i = 0; i < numOfSharedBundles; i ++){
			int s = rnd2.nextInt()%resources.length;
			int e = rnd2.nextInt()%resources.length;
			while(e == s){e = rnd2.nextInt()%resources.length;}
			System.out.println("Sharing: "+s+" "+e);
			sharedOwnResIndex[i] = new int[]{Math.min(s, e), Math.max(s, e)};
		}
	}

	private void shareOwnRes(){
		if(sharedOwnResIndex == null){
			initializeSharedOwnResIndex();
		}
		if(isSharingOwnRes){
			for(int[] i: sharedOwnResIndex){
				// i[1]: end index; i[0]: start index
				String[] sharedBundle = new String[i[1]];
				for(int j = 0; j <  i[0]; j ++){
					sharedBundle[j] = " ";
				}
				for(int j = i[0]; j < i[1]; j++){
					sharedBundle[j] = resourceConfig[j];
				}
				Globals.addSharedResources(sharedBundle);
			}
		}
	}
	// FINISHED

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

	// EDITED: make decision - whether to use or not to use resources (NEED? whether to share or not to share the resources)
	public void makeResComponentDecision(){
//		double currentFitness = Simulation.landscape.getFitness(resourceConfig);
//		Firm[] firms = Globals.getFirms();
//		for(Firm f: firms){
//			// [QUESTION] how to write this? using how many resources from other firms, and how to combine them?
//			HashSet<Integer> sharedC = f.getSharedRscComponents();
//			for(int i = 0; i < Globals.getRscComponents().length - 1; i ++){
//				int r = Globals.getRscComponents()[i];
//				if(sharedC.contains(r)){
//					int nextR = Globals.getRscComponents()[i + 1];
//					String[] rscConfig = new String[nextR - r];
//					for(int j = 0; j < nextR; j ++){
//
//					}
//				}
//			}
//		}
//		syncResources();
//		shareRes();
	}



	public HashSet<Integer> getSharedRscComponents() {
		return sharedRscComponents;
	}

	public void setSharedRscComponents(HashSet<Integer> sharedRscComponents) {
		this.sharedRscComponents = sharedRscComponents;
	}

	// ====
	public void makeResDecision(){


		double currentFitness = Simulation.landscape.getFitness(resourceConfig);
		int optionSize = Globals.getSharedResourcesSize();
		String[] maxConf = resourceConfig;
		double maxFitness = 0;
		for(int i = 0; i < optionSize; i ++){
			String[] resConf = Globals.getSharedResources(i);
			String[] tempConf = new String[resourceConfig.length];
			System.arraycopy(tempConf, 0, resourceConfig, 0, resourceConfig.length);
			for(int t = 0; t < resConf.length; t ++){
				if(resConf[t].equals(" ")) continue;
				tempConf[t] = resConf[t];
			}
			double tempFitness = Simulation.landscape.getFitness(tempConf);
			if(tempFitness > maxFitness){
				maxFitness = tempFitness;
				maxConf = tempConf;
			}
		}
		if(maxFitness > currentFitness){
			resourceConfig = maxConf;
		}
		syncResources();
	}
	// FINISHED


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
		System.out.println("\n\nSearch Experiencial: ");
		searchExperiential();

		// if (Globals.getInnovation() >= Globals.rand.nextDouble()) {
		// 	dropResource();
		// } 
		// if (Globals.getInnovation() >= Globals.rand.nextDouble()) {
		// 	addResource();
		// } 

		System.out.println("\n\nInnovation: "+innovation);
		// Innovate by adding/removing resources, then decision making (absolute/normalize) to decide whether to adopt the changes
		if (innovation >= Globals.rand.nextDouble()) {
			String[] addResourceConfig = new String[Globals.getN()];
			System.arraycopy(considerAddResource(), 0, addResourceConfig, 0, addResourceConfig.length);
			String[] dropResourceConfig = new String[Globals.getN()];
			System.arraycopy(considerDropResource(), 0, dropResourceConfig, 0, dropResourceConfig.length);

			double currentFitness = Simulation.landscape.getFitness(resourceConfig);
			double addResourceUtility = Simulation.landscape.getFitness(addResourceConfig);
			double dropResourceUtility = Simulation.landscape.getFitness(dropResourceConfig);

			System.out.println("current: "+currentFitness+", add: "+addResourceUtility+", drop: "+dropResourceUtility );
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
			if (resourceDecision.equals("absolute")) {
				// if (addResourceUtility > dropResourceUtility) {
				// 	// add is better
				// 	if (addResourceUtility - currentFitness - Globals.getResourceThreshold() > 0) {
				// 		// AND it's a good move
				// 		System.arraycopy(addResourceConfig, 0, resourceConfig, 0, addResourceConfig.length);
				// 	}
				// } else {
				// 	// drop is better
				// 	if (dropResourceUtility - currentFitness - Globals.getResourceThreshold() > 0) {
				// 		// AND it's a good move
				// 		System.arraycopy(dropResourceConfig, 0, resourceConfig, 0, dropResourceConfig.length);
				// 	}
				// }
				// first consider if threshold has been met	by either add or drop


				if ((addResourceUtility - currentFitness - resourceThreshold > 0) || (dropResourceUtility - currentFitness - resourceThreshold > 0)) {
					if (addResourceUtility > dropResourceUtility) {
						System.arraycopy(addResourceConfig, 0, resourceConfig, 0, addResourceConfig.length);
					} else {
						System.arraycopy(dropResourceConfig, 0, resourceConfig, 0, dropResourceConfig.length);
					}
				} else { // now consider if dropResourceUtility is performance enhancing
					// [question] why cost is not included here?
					if (dropResourceUtility - currentFitness > 0) {
						System.arraycopy(dropResourceConfig, 0, resourceConfig, 0, dropResourceConfig.length);
					}
				}

			} else { // getResourceDecision() == "relative" **** ACTUALLY WE'RE NOT RUNNING THIS FOR NOW.  SO THIS PART HASN'T BEEN FULLY TESTED
				// [question]: should the cost be 'random' or should it be a portion of 'the current fitness level'?
				if ((addResourceUtility/(numCurrentResources + numResourcesToAdd)) - resourceThreshold > (dropResourceUtility/(numCurrentResources - 1)) + resourceThreshold) {
					// add is better 
					// currentFitness is out of numResources whereas addResourceUtility is out of (numResources + 1)
					// if ((addResourceUtility/(numCurrentResources + 1)) > (currentFitness/numCurrentResources) + Globals.getResourceThreshold()) {
					if ((addResourceUtility/(numCurrentResources + numResourcesToAdd)) > (currentFitness/numCurrentResources) + resourceThreshold) {
						System.arraycopy(addResourceConfig, 0, resourceConfig, 0, addResourceConfig.length);
					} // else  do nothing
				} else {
					// drop is better
					// currentFitness is out of numResources whereas addResourceUtility is out of (numResources + 1)
					if ((dropResourceUtility/(numCurrentResources - 1)) > (currentFitness/numCurrentResources) - resourceThreshold) {
						System.arraycopy(dropResourceConfig, 0, resourceConfig, 0, dropResourceConfig.length);
					} // else  do nothing
				}
			}
			syncResources(); // resets bool resources[]

			borrowOrSwitch();
		}
	}

	private boolean haveComponent(List<Integer> component, Firm f){
		Boolean[] rsc = f.getResources();
		for(int i: component){
			if(!rsc[i]) {
				return false;
			}
		}
		return true;
	}

	private boolean canBorrowComponent(List<Integer> component){
		for(int i: component){
			if(resources[i]) {
				return false;
			}
		}
		return true;
	}

	// [fixme]
	private void borrowOrSwitch(){
		// decide on which component to borrow
		List<List<Integer>> components = Globals.getRscComponents();
		List<Integer> indexOfComponentToBorrow = new ArrayList<Integer>();
		for(int i = 0; i < components.size(); i ++){
			if(canBorrowComponent(components.get(i))) indexOfComponentToBorrow.add(i);
		}
		MersenneTwisterFast rnd = new MersenneTwisterFast();
		int rIndex = Math.abs(rnd.nextInt())%indexOfComponentToBorrow.size();
		rIndex = indexOfComponentToBorrow.get(rIndex);

		List<Integer> componentToBorrow = components.get(rIndex);

		// decide on which firm to borrow
		List<Firm> firmCanLend = new ArrayList<>();
		for(Firm f: Globals.getFirms()){
			if(haveComponent(componentToBorrow, f)) firmCanLend.add(f);
		}
		int fIndex = Math.abs(rnd.nextInt())%firmCanLend.size();

		// decide whether to perform the borrowing or not
		borrowFromFirm(firmCanLend.get(fIndex), components.get(rIndex));

		syncResources();
	}

	// [fixme]
	private void borrowFromFirm(Firm f, List<Integer> rscIndex){

		borrowOrSwitchingThreshold

		borrowedRsc.add(new Pair<Firm, List<Integer>>(f, rscIndex));
	}

	public Boolean[] getResources() {
		return resources;
	}

	public void setResources(Boolean[] resources) {
		this.resources = resources;
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
		int numResourcesToAdd = Globals.rand.nextInt(Math.min(Globals.getN() - numCurrentResources + 1, resourcesIncrement)) + 1;

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
		if (resourceDecision.equals("absolute")) {
			if (addResourceUtility > currentFitness + resourceThreshold) {
				System.arraycopy(addResourceConfig, 0, resourceConfig, 0, addResourceConfig.length);
			} // else do nothing
		} else {
			// currentFitness is out of numResources whereas addResourceUtility is out of (numResources + 1)
			// if ((addResourceUtility/(numCurrentResources + 1)) > (currentFitness/numCurrentResources) + Globals.getResourceThreshold()) {
			if ((addResourceUtility/(numCurrentResources + numResourcesToAdd)) > (currentFitness/numCurrentResources) + resourceThreshold) {
				System.arraycopy(addResourceConfig, 0, resourceConfig, 0, addResourceConfig.length);
			} // else  do nothing
		}
		syncResources(); // resets bool resources[]

		// // [fixme] check previous firms if they are borrowing my dropped resources: if yes, they need to drop their component as well
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
		int numResourcesToAdd = Globals.rand.nextInt(Math.min(Globals.getN() - numCurrentResources + 1, resourcesIncrement)) + 1;

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
			if (resourceDecision.equals("absolute")) {
				if (dropResourceUtility < currentFitness - resourceThreshold) {
					System.arraycopy(dropResourceConfig, 0, resourceConfig, 0, dropResourceConfig.length);
				} // else do nothing
			} else {
				// currentFitness is out of numResources whereas addResourceUtility is out of (numResources + 1)
				if ((dropResourceUtility/(numCurrentResources - 1)) > (currentFitness/numCurrentResources) - resourceThreshold) {
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
		int numResourcesToChange = Globals.rand.nextInt(searchScope) + 1;
		// shouldn't always be long jumps, so can consider UP TO searchScope changes
		// int numResourcesToChange = Globals.getSearchScope() + 1;
		for(int i =0; i < searchConfig.length; i ++){
			System.out.print(searchConfig[i]+", ");
		}
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

		System.out.println("\n\nSearch Config: ");
		for(int i =0; i < searchConfig.length; i ++){
			System.out.print(searchConfig[i]+", ");
		}

		//System.out.println("SearchConfig: \n" + Globals.arrayToString(searchConfig));
		double searchUtility = Simulation.landscape.getFitness(searchConfig);
		System.out.println("searchUtility:"+searchUtility);
		// if (searchUtility > currentFitness) {
		// 		System.arraycopy(searchConfig, 0, resourceConfig, 0, searchConfig.length);
		// }  else {
		// 	// do nothing
		// }

		// ABSOLUTE VS. NORMALIZED DECISION MAKING --> here it doesn't make a difference as the number of resources is the same
		// [TODO] but how do we make it more costly for long jumps???
		System.out.printf("currentFitness:%.0f, searchUtility: %.0f, searchThreshold:%.0f", currentFitness, searchUtility, searchThreshold);
		if (resourceDecision.equals("absolute")) {
			if (searchUtility > currentFitness + searchThreshold) {
				System.arraycopy(searchConfig, 0, resourceConfig, 0, searchConfig.length);
			} // else do nothing
		} else {
			// currentFitness is out of numResources whereas addResourceUtility is out of (numResources + 1)
			// [NOTE] this is the same as ABSOLUTE except for multiplying of numResourcesToChange but the cost scale is a bit off
			if ((searchUtility/(numResources)) > (currentFitness/numResources) + numResourcesToChange*searchThreshold) {
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
		String retString = "firmType (" + firmType + ")\t";
		retString += "firmID (" + firmID + ")\t";
		retString += "initResources (" + initResources + ")\t";
		retString += "innovation (" + innovation + ")\t";
		retString += "resourcesIncrement (" + resourcesIncrement + ")\t";
		retString += "searchScope (" + searchScope + ")\t";
		retString += "searchThreshold (" + searchThreshold + ")\t";
		retString += "resourceDecision (" + resourceDecision + ")\t";
		retString += "resourceThreshold (" + resourceThreshold + ")\t" + getResourceConfigString();
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
