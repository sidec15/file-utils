package com.sdc.file.filter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

	public class WavFilter extends FileFilter{
		
	public String getDescription() {
		return "*.wav";
	}
	
	@Override
	public boolean accept(File file) {
		if (file.isDirectory()) return true;
		String fname = file.getName().toLowerCase();
	return fname.endsWith("wav");
	}

}