package de.hfu.anybeam.networkCore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

/**
 * A {@link Vector} subclass providing functionality to calculate a average value over all values included in this list.
 * Only the last max values are stored, all others are disposed.
 * @author chrwuer
 * @since 1.0
 * @version 1.0
 */
class AverageList extends Vector<Double> {
	
	private static final long serialVersionUID = 7801680903484273521L;
	
	//The max size of the list
	private final int MAX_SIZE;

	/**
	 * Creates a new {@link AverageList} object with the given max number of elements.
	 * @param maxSize the max number of elements in this list
	 */
	public AverageList(int maxSize) {
		this.MAX_SIZE = maxSize < 1 ? 1 : maxSize;
	}

	@Override
	public synchronized boolean add(Double t) {
		//Remove a element if there would be more than the max
		if(this.size()+1 > this.MAX_SIZE) {
			 this.remove(0);
		}
		
		//Add the new value
		return super.add(t);
	}
	
	/**
	 * Calculates the average value of all elements in this list.
	 * @return the calculated average
	 */
	public synchronized double getAverage() {
		List<Double> copy = new ArrayList<>(this);
		Collections.sort(copy);
		
		if(copy.size() == 0)
			return 0;
		
		return copy.get(copy.size()/2);
	}

}