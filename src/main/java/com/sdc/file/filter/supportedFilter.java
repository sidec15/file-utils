package com.sdc.file.filter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

	public class supportedFilter extends FileFilter{
		
	public String getDescription() {
		return "*.mp3, *.wav";
	}
	
	@Override
	public boolean accept(File file) {
		if (file.isDirectory()) return true;
		String fname = file.getName().toLowerCase();
	return fname.endsWith("mp3") || fname.endsWith("wav");
	}

}