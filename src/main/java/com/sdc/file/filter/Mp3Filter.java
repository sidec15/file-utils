package com.sdc.file.filter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

	public class Mp3Filter extends FileFilter{
		
	public String getDescription() {
		return "*.mp3";
	}
	
	@Override
	public boolean accept(File file) {
		if (file.isDirectory()) return true;
		String fname = file.getName().toLowerCase();
	return fname.endsWith("mp3");
	}

}