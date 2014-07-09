/**
 * CsvFilter.java
 */
package com.sdc.file.filter;

import java.io.File;

import javax.swing.filechooser.FileFilter;


/**
 * @author Simone De Cristofaro
 * @created 07/ott/2012
 */
public class CsvFilter extends FileFilter {
	
	public String getDescription() {
		return "*.csv";
	}
	
	@Override
	public boolean accept(File file) {
		if (file.isDirectory()) return true;
		String fname = file.getName().toLowerCase();
	return fname.endsWith("csv");
	}
}
