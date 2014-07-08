/**
 * Point.java
 */
package com.sdc.file.structures;


/**
 * @author Simone De Cristofaro
 * @created 21/ott/2012
 */
public class Stop {

	private String code;
	private double pxco;
	private double pyco;
	
	
	
	/**
	 * @param code
	 * @param pxco
	 * @param pyco
	 */
	public Stop(String code, double pxco, double pyco) {
		super();
		this.code = code;
		this.pxco = pxco;
		this.pyco = pyco;
	}
	/**
	 * @param pxco
	 * @param pyco
	 */
	public Stop(double pxco, double pyco) {
		this(null, pxco, pyco);
	}
	/**
	 * @return the pxco
	 */
	public double getPxco() {
		return pxco;
	}
	/**
	 * @param pxco the pxco to set
	 */
	public void setPxco(double pxco) {
		this.pxco = pxco;
	}
	/**
	 * @return the pyco
	 */
	public double getPyco() {
		return pyco;
	}
	/**
	 * @param pyco the pyco to set
	 */
	public void setPyco(double pyco) {
		this.pyco = pyco;
	};
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return code + ": " + pxco + ", " + pyco;
	}

	//STATIC METHODS
	/**
	 * Return the distance [m] between the two stops, according to the specified coordinate type
	 * @param s1
	 * @param s2
	 * @param cType
	 * @return
	 */
	public static double distance(Stop s1,Stop s2,COORDINATES_TYPE cType) {
		return com.sdc.file.support.Support.getDistance(s1.getPxco(), s1.getPyco(), s2.getPxco(), s2.getPyco(), cType);
	}
	


	
}
