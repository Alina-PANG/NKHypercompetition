package util;

import java.util.ArrayList;

public class Bag <T> {
    private ArrayList<T> bag;

    public Bag(){
        bag = new ArrayList<T>();
    }

    public void add (T a) {
        this.bag.add(a);
    }

    public T randomPop () {
        MersenneTwisterFast rnd = new MersenneTwisterFast();
        return bag.get(rnd.nextInt(this.bag.size()));
    }

    public ArrayList<T> getBag() {
        return bag;
    }

    public void setBag(ArrayList<T> bag) {
        this.bag = bag;
    }
}
