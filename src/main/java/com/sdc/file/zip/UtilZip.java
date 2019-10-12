/**
 * Decomprimer.java
 */
package com.sdc.file.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipException;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;


/**
 * @author simone.decristofaro 17/set/2014
 */
public class UtilZip {

	private static final int BUFFER = 2048;

	/**
	 * Decompress an input zip file to the specified output file. If the file
	 * already exist then it will be overriden.
	 * 
	 * @param input input zip file
	 * @param output output direcotry
	 * @throws ZipException
	 */
	public static void decompress(String input, String output) throws ZipException {
		// controlli preliminari
		if (!input.endsWith(".zip"))
			throw new ZipException(input + " is not a zip file");
		else if (!new File(input).exists())
			throw new ZipException("Input file: " + input
					+ " doesn't exist");
		else if (output != null && !output.equals("") && !output.endsWith("\\") && !output.endsWith("/"))
			throw new ZipException(output + " is not a directory");

		File f=new File(output);
		if(!f.exists()) {
			f.mkdirs();
		}
		
		FileInputStream fis=null;
		ZipFile zif=null;
		try {
			zif = new ZipFile(input);
			ZipArchiveEntry entry;
			Enumeration<ZipArchiveEntry> entries = zif.getEntries();
			FileOutputStream fos;
			InputStream is;
			
			while (entries.hasMoreElements()) {
				int n;
				byte data[] = new byte[BUFFER];
				entry= entries.nextElement();
				// write the files to the disk
				fos = new FileOutputStream(output+entry.getName());
				is = zif.getInputStream(entry);
                while ((n = is.read(data)) != -1) {
                    if (n > 0) {
                        fos.write(data, 0, n);
                    }
                }				
				if(fos!=null) fos.close();
				if(is!=null) is.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(fis!=null) fis.close();
				if(zif!=null) zif.close();
			} catch (IOException e) {}
		}

	}

}
