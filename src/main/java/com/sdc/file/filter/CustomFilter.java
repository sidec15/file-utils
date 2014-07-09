/**
 * CustomFilter.java
 */
package com.sdc.file.filter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import com.sdc.file.utils.Util;


/**
 * @author Simone De Cristofaro
 * @created 26/lug/2012
 */
public class CustomFilter extends FileFilter{
	
	//ATTRIBUTES
	String[] extention;
	
	//CONSTRUCTORS
	/**
	 * Filter custom extension
	 * @param availablesExtension Array containing the extention to filter
	 */
	public CustomFilter(String[] availablesExtension) {
		super();
		extention=availablesExtension;
	}
	
	

	/* (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
	 */
	@Override
	public boolean accept(File file) {
		if (file.isDirectory()) return true;
		if (extention==null) return true;
		String ext=Util.getExtention(file).toLowerCase();
		for(int i=0;i<extention.length;i++)
			if(ext.equals(extention[i])) return true;
		return false;
	}

	/* (non-Javadoc)
	 * @see javax.swing.filechooser.FileFilter#getDescription()
	 */
	@Override
	public String getDescription() {
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<extention.length;i++)
			sb.append("*.").append(extention[i]).append(", ");
		sb.delete(sb.length()-2, sb.length());
		return sb.toString();
	}
	
}

