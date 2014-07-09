/**
 * ImageFilter.java
 */
package com.sdc.file.filter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * @author Simone De Cristofaro
 * @created 07/feb/2012
 */
public class JpgFilter extends FileFilter{

	/* (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
	 */
	@Override
	public boolean accept(File file) {
		if (file.isDirectory()) return true;
		String fname = file.getName().toLowerCase();
	return fname.endsWith("jpg") || fname.endsWith("JPG");
	}

	/* (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#getDescription()
	 */
	@Override
	public String getDescription() {
		return "*.jpg";
	}

	
	
}
