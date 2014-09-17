/**
 * DecomprimerException.java
 */
package com.sdc.file.exception;

/**
 * @author simone.decristofaro
 * 17/set/2014
 */
public class ZipException extends Exception {
	public ZipException(String m) {super(m);};
	public ZipException() {super();}
	public ZipException(String message,Throwable innerException) {
		super(message, innerException);
	}

}
