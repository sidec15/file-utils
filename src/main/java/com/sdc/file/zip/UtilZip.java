/**
 * Decomprimer.java
 */
package com.sdc.file.zip;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.*;


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
	 * @throws DecomprimerException
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

		BufferedOutputStream dest =null;
		FileInputStream fis=null;
		ZipInputStream zis=null;
		try {
			dest = null;
			fis = new FileInputStream(input);
			zis = new ZipInputStream(
					new BufferedInputStream(fis));
			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null) {
				int count;
				byte data[] = new byte[BUFFER];
				// write the files to the disk
				FileOutputStream fos = new FileOutputStream(output+entry.getName());
				dest = new BufferedOutputStream(fos, BUFFER);
				while ((count = zis.read(data, 0, BUFFER)) != -1) {
					dest.write(data, 0, count);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				dest.flush();
				dest.close();
				zis.close();
			} catch (IOException e) {}
		}

	}

}
