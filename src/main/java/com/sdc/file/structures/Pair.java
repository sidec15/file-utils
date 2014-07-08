/**
 * Pair.java
 */
package com.sdc.file.structures;

/**
 * @author Simone
 * 11/dic/2012
 */
public class Pair<T1,T2> {
	
	//ATTRIBUTES
	private T1 ele1;
	private T2 ele2;
	
	//CONSTRUCTORS
	
	
	/**
	 * @param ele1
	 * @param ele2
	 */
	public Pair(T1 ele1, T2 ele2) {
		super();
		this.ele1 = ele1;
		this.ele2 = ele2;
	}

	
	
	/**
	 * 
	 */
	public Pair() {
		super();
	}



	//METHODS
	
	/**
	 * @return the ele1
	 */
	public T1 getEle1() {
		return ele1;
	}

	/**
	 * @param ele1 the ele1 to set
	 */
	public void setEle1(T1 ele1) {
		this.ele1 = ele1;
	}

	/**
	 * @return the ele2
	 */
	public T2 getEle2() {
		return ele2;
	}

	/**
	 * @param ele2 the ele2 to set
	 */
	public void setEle2(T2 ele2) {
		this.ele2 = ele2;
	}



	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj==null || !obj.getClass().equals(getClass())) return false;
		
		@SuppressWarnings("rawtypes")
		Pair c= (Pair) obj;
		if((c.getEle1()==null || !(c.getEle1().getClass().equals(ele1.getClass()))) || (c.getEle2()==null || !(c.getEle2().getClass().equals(ele2.getClass()))) ) return false;
		
		
		return ele1.equals(c.getEle1()) && ele2.equals(c.getEle2());
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return super.toString();
	}
	
	public static void main(String[] args) {
		Pair<String, Integer> c1=new Pair<String, Integer>("Simone", 27);
		Pair<String, Integer> c2=new Pair<String, Integer>("Kiara", 26);
		Pair<Long, Long> c3=new Pair<Long, Long>(10l, 26l);
		Pair<Integer, Integer> c4=new Pair<Integer, Integer>(10, 26);
		Pair<String, Integer> c5=new Pair<String, Integer>("Simone", 27);
		
		System.out.println(c1.equals(c2));
		System.out.println(c1.equals(c5));
		System.out.println(c1.equals(c3));

		System.out.println("bye");

	}
	
}
