/**
 * TableException.java
 */
package com.sdc.file.exception;

/**
 * @author Simone De Cristofaro
 * @created 10/ott/2012
 */
public class TableException extends Exception{
	
	/**
     * 
     */
    private static final long serialVersionUID = 1L;
    public TableException(String m) {super(m);};
	public TableException() {super();}
	public TableException(String message,Throwable innerException) {
		super(message, innerException);
	}
}
