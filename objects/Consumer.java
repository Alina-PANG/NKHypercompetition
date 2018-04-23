package objects;

import java.util.*;
import util.Globals;

public class Consumer {
	private boolean[] needs; 
	private ArrayList<Product> adopted;
	
	public Consumer() {
		// initialize need
		needs = new boolean[Globals.N];
		int needSet = 0;
		while (needSet < Globals.numNeeds) {
			int r = Globals.rand.nextInt(Globals.N);
			if (!needs[r]) {
				needSet++;
				needs[r] = true;
			}
		}
		
		// initialize empty set of adopted products
		adopted = new ArrayList<Product>();
	}
	
	
	
	public String toString() {
		String retString = "";
		for (int i = 0; i < needs.length; i++) {
			if (needs[i]) {
				retString += "1";
			} else {
				retString += "0";
			}
		}
		return retString;
	}
	
	public static void main(String[] args) {
		Consumer c = new Consumer();
		System.out.println(c.toString());
	}
}
