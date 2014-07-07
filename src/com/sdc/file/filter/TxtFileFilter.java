/**
 * TxtFileFilter.java
 */
package com.sdc.file.filter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * @author Simone De Cristofaro
 * @created 06/dic/2011
 */
public class TxtFileFilter extends FileFilter {
	
	public String getDescription() {
		return "*.txt";
	}
	
	@Override
	public boolean accept(File file) {
		if (file.isDirectory()) return true;
		String fname = file.getName().toLowerCase();
	return fname.endsWith("txt");
	}
}
