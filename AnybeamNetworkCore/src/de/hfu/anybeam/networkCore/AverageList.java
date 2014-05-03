package de.hfu.anybeam.networkCore;

import java.util.Vector;

public class AverageList extends Vector<Double> {

	private static final long serialVersionUID = 7801680903484273521L;
	private final int MAX_SIZE;

	public AverageList(int maxSize) {
		this.MAX_SIZE = maxSize < 1 ? 1 : maxSize;
	}

	@Override
	public synchronized boolean add(Double t) {
		if(this.size()+1 > this.MAX_SIZE) {
			 this.remove(0);
		}
		
		return super.add(t);
	}
	
	public synchronized double getAverage() {
		double sum = 0;
		
		for(Double d : this) {
			sum += d;
		}
		
		return sum/this.size();
	}

}