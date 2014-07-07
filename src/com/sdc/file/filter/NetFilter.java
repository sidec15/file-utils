/**
 * NetFilter.java
 */
package com.sdc.file.filter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * @author Simone De Cristofaro
 * @created 26/lug/2012
 */
public class NetFilter extends FileFilter{

	/* (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
	 */
	@Override
	public boolean accept(File file) {
		if (file.isDirectory()) return true;
		String fname = file.getName().toLowerCase();
	return fname.endsWith("net");
	}

	/* (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#getDescription()
	 */
	@Override
	public String getDescription() {
		return "*.net";
	}

	
	
}
