/**
 * COORDINATES_TYPE.java
 */
package com.sdc.file.structures;

import java.util.HashMap;

/**
 * @author Simone De Cristofaro
 * 27/nov/2012
 */
public enum COORDINATES_TYPE {

	METERS(0),
	DEGREE(1),
	INDETERMINATE(-1);
	
	private static final String metersString="meters";
	private static final String degreeString="degree";
	private static final String indeterminateString="indeterminate";
	private static HashMap<Integer, COORDINATES_TYPE> hashMap_request;
	static {
		hashMap_request=new HashMap<Integer, COORDINATES_TYPE>();
		for(COORDINATES_TYPE r: COORDINATES_TYPE.values())
			hashMap_request.put(r.value, r);
	}

	private int value;
	//CONSTRUCTORS
	private COORDINATES_TYPE(int value) {
		this.value=value;
	}
	
	//METHODS
	@Override
	public String toString() {
		if(this==COORDINATES_TYPE.METERS) return metersString;
		else if(this==COORDINATES_TYPE.DEGREE) return degreeString;
		return indeterminateString;
	}
	
	public static COORDINATES_TYPE fromString(String s) {
		if(s==null) return null;
		if(s.equals(metersString)) return COORDINATES_TYPE.METERS;
		else if(s.equals(degreeString)) return COORDINATES_TYPE.DEGREE;
		return COORDINATES_TYPE.INDETERMINATE;
	}
	
	
	public static COORDINATES_TYPE getFromInt(int i) {
		return hashMap_request.get(i);
	}	

	
	
}
