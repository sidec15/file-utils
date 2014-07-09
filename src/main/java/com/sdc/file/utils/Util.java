package com.sdc.file.utils;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.nio.channels.FileChannel;
import java.util.*;

import javax.imageio.ImageIO;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.mozilla.universalchardet.UniversalDetector;
import org.w3c.dom.Document;

import com.sdc.file.exception.TableException;
import com.sdc.file.exception.TextFormatException;
import com.sdc.file.structures.Table;


public class Util {
	
	private static String NEWLINE=System.getProperty("line.separator");
	
	@SuppressWarnings("unused")
	public static void main(String []args) throws TextFormatException, IOException, TableException {
		
		if (false) {
			
//		System.out.println( textFileEquals("C:\\Users\\Simone\\workspace\\Ferrovie\\OutputData\\ATAC_codeseConFerrovie.txt",
//				"C:\\Users\\Simone\\Desktop\\ATAC_codeseConFerrovie.txt"));
//		System.out.println(Arrays.toString(readFile("ATAC_codese.txt")));
//		readFile("C:\\Users\\Simone\\workspace\\Ferrovie\\ATAC_anagservizioprogConFerrovie.txt");
//		System.out.println("Elaborating...");
//		boolean b =textFileEquals("C:\\Users\\Simone\\Desktop\\HyperPath Transit\\trunk\\data\\ATAC\\ATAC_anagservizioprogConFerrovie.txt",
//				"C:\\Users\\Simone\\Desktop\\HyperPath Transit\\trunk\\data\\ATAC\\ATAC_anagservizioprog.txt");
//		boolean b =textFileEquals("C:\\Users\\Simone\\Desktop\\ATAC_anagservizioprog.txt",
//				"C:\\Users\\Simone\\Desktop\\ATAC_anagservizioprogModificata.txt");
//		System.out.println(b);
		
//		try {
//			imageToByteArray("C:\\Users\\Simone\\Desktop\\Nuova Cartella\\Squall.JPG");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
//		System.out.println("size in mb of: C:\\Users\\Simone\\Desktop\\Nuova cartella :" + getSizeMB("C:\\Users\\Simone\\Desktop\\Nuova cartella"));
//		System.out.println("size in mb of: C:\\Users\\Simone\\Desktop\\Nuova cartella :" + getNumberOfFiles("C:\\Users\\Simone\\Desktop\\Nuova cartella"));

//		System.out.println(new File("C:\\Users\\Simone\\Desktop\\Nuova cartella\\Nuova cartella2"));
	
//		File f = new File("C:\\MSOCache");
		//File f = new File("C:\\Nuova cartella");

//		String[] table;
//		try {
//			table = readFile("prova.csv");
//			System.out.println(FormatTable("test", table, ",", "#", 2));
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (TextFormatException e) {
//			e.printStackTrace();
//		}
		
//		String filePath="C:\\Users\\Simone\\Lavoro SISTeMA\\repo\\TDE\\branches\\GoogleTransit\\exec\\multiImport_output\\output_dval.csv";
//		boolean makeNewFile=true;
//		boolean fieldsInSameRowOfName=false;
//		boolean compact=false;
//		Table.FormatTablesInTxtFile(filePath, "$", ":", ',', '#', '"', compact, makeNewFile, fieldsInSameRowOfName);
//		Table t=Table.getTableInFile("C:\\Users\\Simone\\Desktop\\new 2.txt", "OPTIMIZATION", "$", ":", ',', '#','"',false);
//		System.out.println(t.toString());
		
//		String filePath="CommandLine.csv";
//		System.out.println(getFileEncoding(filePath));
		
		Integer[] v= {1,1,1};
		System.out.println("v: " + Arrays.toString(v));
		Integer[] u= {1};
		System.out.println("u: " + Arrays.toString(u));

		Object[][] v1=com.sdc.file.utils.Util.splitArray(v, u);
		for (int i = 0; i < v1.length; i++) {
			System.out.println("oc"+i+": "+ Arrays.toString(v1[i]));
		}
		
		}
	}
	
	/**
	 * Return <i>true</i> if a text file with path f1 is equal to a text file with path f2.
	 * @param f1 Path of first text file
	 * @param f2 Path of second text file
	 * @return  <i>true</i> if a text file with path f1 is equal to a text file with path f2,
	 * <i>false</i> otherwise
	 * @throws IOException 
	 */
	public static boolean textFileEquals(String f1,String f2) throws IOException {
		String []firstFile=readFile(f1);
		
		FileReader f=null;
		BufferedReader reader=null;
		int i=0;

		try{
			f=new FileReader(f2);
			reader=new BufferedReader(f);
			String text_line=reader.readLine();
			while(text_line!=null) {
				if(!text_line.equals(firstFile[i]))
					return false;
				i++;
				text_line=reader.readLine();
			}
		}catch(FileNotFoundException e){
			System.out.println("File non trovato");
		}catch(IOException e){
			System.out.println("Errore in lettura");
		}catch(Throwable e){
			System.out.println("Riga: "+i);
			System.out.println("Errore generico");
			e.printStackTrace();
		}finally{
			try{
				f.close();
				reader.close();
			}catch(IOException e){
				System.out.println("Errore in chiusura");
			}
		}

		return true;
	}
	
	/**
	 * Return <i>true</i> if v1 is equal to v2.
	 * @param v1 Object Array
	 * @param v2 Object Array
	 * @return <i>true</i> if v1 is equal to v2, <i>false</i> otherwise
	 */
	public static boolean uguali(Object []v1,Object[]v2) {
		if(v1.length!=v2.length) return false;
		for(int i=0;i<v1.length;i++)
			if(!v1[i].equals(v2[i])) return false;
	return true;
	}
	
	
	//****************READ FILES METHODS*****************************
	/**
	 * Make the content of the text file with path <code>filePath</code>
	 * in a String array, begun from the row <code>firstRow</code>
	 * @param filePath Path of file to read
	 * @param firstRow The first row (on base 1) from which read
	 * @return The content of a text file
	 * @throws IOException 
	 * @throws Throwable 
	 */
	public static String[] readFileFromRow(String filePath,int firstRow) throws IOException  {
		
		InputStreamReader f=null;
		BufferedReader reader=null;
		List<String> list_toReturn=null;
		String []toReturn=null;
		int i=0;
		String encoding=null;
		
		try{
			encoding=Util.getFileEncoding(filePath);
			if (encoding!=null)
				f=new InputStreamReader(new FileInputStream(filePath), encoding);
			else
				f=new InputStreamReader(new FileInputStream(filePath));
			reader=new BufferedReader(f);
			list_toReturn=new ArrayList<String>();
			String text_line=reader.readLine();
			
			while(text_line!=null && (i+1)<firstRow) {
				text_line=reader.readLine();
				i++;
			}
			
			while(text_line!=null) {
				list_toReturn.add(text_line);
				text_line=reader.readLine();
				i++;
			}
			toReturn=new String[list_toReturn.size()];
			list_toReturn.toArray(toReturn);
		}catch(FileNotFoundException e){
			System.out.println("File non trovato");
			throw e;
		}catch(IOException e){
			System.out.println("Errore in lettura");
			throw e;
		}finally{
			try{
				if(f!=null)
					f.close();
				if(reader!=null)
					reader.close();
			}catch(IOException e){
				System.out.println("Errore in chiusura");
			}
		}
		
		return toReturn;

	}

	/**
	 * Make the content of the text file with path <code>filePath</code>
	 * in a String array
	 * @param filePath Path of file to read
	 * @return The content of a text file
	 * @throws IOException 
	 */
	public static String[] readFile(String filePath) throws IOException {
		InputStreamReader f=null;
		BufferedReader reader=null;
		List<String> list_toReturn=null;
		String []toReturn=null;
		int i=0;
		String encoding=null;
		
		try{
			encoding=Util.getFileEncoding(filePath);
			if (encoding!=null)
				f=new InputStreamReader(new FileInputStream(filePath), encoding);
			else
				f=new InputStreamReader(new FileInputStream(filePath));
			reader=new BufferedReader(f);
			list_toReturn=new ArrayList<String>();
			String text_line=reader.readLine();
			while(text_line!=null) {
				list_toReturn.add(text_line);
				text_line=reader.readLine();
				i++;
			}
			toReturn=new String[list_toReturn.size()];
			list_toReturn.toArray(toReturn);
		}catch(FileNotFoundException e){
			System.out.println("File non trovato");
			throw e;
		}catch(IOException e){
			System.out.println("Errore in lettura");
			throw e;
		}finally{
			try{
				if(f!=null)
					f.close();
				if(reader!=null)
					reader.close();
			}catch(IOException e){
				System.out.println("Errore in chiusura");
			}
		}
		
		return toReturn;

	}

	/**
	 * Make the content of the text file with path <code>filePath</code>
	 * in a String square matrix based on a specific separator
	 * @param filePath Path of file to read
	 * @param separator field separator
	 * @param fieldsNumber Number of field (colunm). Must be a positive not null integer
	 * @return The content of a text file
	 * @throws IOException 
	 */
	public static String[][] readFile(String filePath,String separator,int fieldsNumber) throws IOException {
		InputStreamReader f=null;
		BufferedReader reader=null;
		List<String[]> list_toReturn=null;
		List<String> tmp_List=null;
		String [][]toReturn=null;
		String tmp_strings[];
		int i=0;
		String encoding=null;
		
		try{
			encoding=Util.getFileEncoding(filePath);
			if (encoding!=null)
				f=new InputStreamReader(new FileInputStream(filePath), encoding);
			else
				f=new InputStreamReader(new FileInputStream(filePath));
			reader=new BufferedReader(f);
			list_toReturn=new ArrayList<String[]>();
			tmp_List=new ArrayList<String>();
			String text_line=reader.readLine();
			String tmp_Strings[]=null;
			String tmp_String2[]=null;
			while(text_line!=null) {
				tmp_Strings=text_line.split(separator);
				if(fieldsNumber>0 && tmp_Strings.length>0) {
					if(tmp_Strings.length<fieldsNumber) {
						tmp_String2=new String[fieldsNumber];
						for(int k=0;k<tmp_Strings.length;k++)
							tmp_String2[k]=tmp_Strings[k];
						for(int k=tmp_Strings.length;k<fieldsNumber;k++)
							tmp_String2[k]="";
						tmp_Strings=tmp_String2;
					}
					for(int j=0;j<tmp_Strings.length;j++)
						tmp_List.add(tmp_Strings[j]);
					tmp_strings=new String[tmp_List.size()];
					list_toReturn.add(tmp_List.toArray(tmp_strings));
					tmp_List.clear();
					i++;
				}
				text_line=reader.readLine();
			}
			toReturn=new String[list_toReturn.size()][];
			
			for(int h=0;h<i;h++) {
				toReturn[h]=list_toReturn.get(h);
				
//				for(int k=0;k<toReturn[h].length;k++)
//					toReturn[h][k]=list_toReturn.get(h)[k];
				
			}

		}catch(FileNotFoundException e){
			System.out.println("File non trovato");
			throw e;
		}catch(IOException e){
			System.out.println("Errore in lettura");
			throw e;
		}finally{
			try{
				if(f!=null)
					f.close();
				if(reader!=null)
					reader.close();
			}catch(IOException e){
				System.out.println("Errore in chiusura");
			}
		}
		
		return toReturn;

	}

	
	/**
	 * Make the content of the text file with path <code>filePath</code>
	 * in a String array
	 * @param filePath Path of file to read
	 * @param numberOfRowToRead The number of row to read from the file
	 * @return The content of a text file
	 * @throws IOException 
	 */
	public static String[] readFile(String filePath,int numberOfRowToRead) throws IOException {
		InputStreamReader f=null;
		BufferedReader reader=null;
		String []toReturn=null;
		int i=0;
		String encoding=null;
		
		try{
			encoding=Util.getFileEncoding(filePath);
			if (encoding!=null)
				f=new InputStreamReader(new FileInputStream(filePath), encoding);
			else
				f=new InputStreamReader(new FileInputStream(filePath));
			reader=new BufferedReader(f);
			toReturn=new String[numberOfRowToRead];
			String text_line=reader.readLine();
			while(text_line!=null && i<numberOfRowToRead) {
				toReturn[i]=text_line;
				text_line=reader.readLine();
				i++;
			}
		}catch(FileNotFoundException e){
			System.out.println("File non trovato");
			throw e;
		}catch(IOException e){
			System.out.println("Errore in lettura");
			throw e;
		}finally{
			try{
				if(f!=null)
					f.close();
				if(reader!=null)
					reader.close();
			}catch(IOException e){
				System.out.println("Errore in chiusura");
			}
		}
		
		return toReturn;

	}
	//**************** END READ FILES METHODS*****************************

	//******************* WRITE FILES METHODS*****************************
	/**
	 * Write the content of the String array <code>v</code> in a text file
	 * with path <code>filePath</code>
	 * @param filePath Path of destination file
	 * @param v String array to print in the file
	 * @param append <i>true</i> for append mode
	 * @throws IOException 
	 */
	public static void writeFile(String filePath, String[] v,boolean append) throws IOException {
		
		File file = new File(filePath);
		if (file.exists() && append==false)
			file.delete();
		
		FileWriter f=null;
		PrintWriter writer=null;
		int riga=0;
		try{
			f=new FileWriter(filePath);
			writer=new PrintWriter(f);
			for(int i=0;i<v.length;i++)
				writer.println(v[i]);
		}catch(IOException e){
			System.out.println("Errore in scrittura");
			throw e;
		}finally{
			try{
				f.close();
				writer.close();
			}catch(IOException e){
				System.out.println("Errore in chiusura");
			}
		}
	}
	
	/**
	 * Write the content of a single String in a text file
	 * with path <code>filePath</code>
	 * @param filePath Path of destination file
	 * @param s String to print in the file
	 * @param append <i>true</i> for append mode
	 * @throws IOException 
	 */
	public static void writeFile(String filePath, String s,boolean append) throws IOException {
		
		File file = new File(filePath);
		if (file.exists() && append==false)
			file.delete();
		
		FileWriter f=null;
		PrintWriter writer=null;
		int riga=0;
		try{
			f=new FileWriter(filePath);
			writer=new PrintWriter(f);
				writer.println(s);
		}catch(IOException e){
			System.out.println("Errore in scrittura");
			throw e;
		}finally{
			try{
				f.close();
				writer.close();
			}catch(IOException e){
				System.out.println("Errore in chiusura");
			}
		}
	}
	//******************* WRITE FILES METHODS*****************************

	/**
	 * Copy a file with path <code>sourceFile</code> to a new file with path
	 * <code>destinationFile</code> 
	 * @param sourceFile Path of file to copy
	 * @param destinationFile Path of destination file
	 * @throws IOException
	 */
	public static void copyFile(String sourceFile,String destinationFile) throws IOException{
		copyFile(new File(sourceFile), new File(destinationFile));
	}
	
	/**
	 * Copy a file with path <code>sourceFile</code> to a new file with path
	 * <code>destinationFile</code> 
	 * @param sourceFile Path of file to copy
	 * @param destinationFile Path of destination file
	 * @throws IOException
	 */
	public static void copyFile(File sourceFile,File destinationFile) throws IOException{
		FileChannel source=null;
		FileChannel dest=null;
		try {
			source= new FileInputStream(sourceFile).getChannel();
			dest = new FileOutputStream(destinationFile).getChannel();
			source.transferTo(0, source.size(), dest);
		} catch (IOException e) {
			throw e;
		}finally {
			if(source!=null) source.close();
			if(dest!=null) dest.close();
		}
	}
	
	/**
	 * Makes in a String array the names of files and folders contained in the directory
	 * with absolute path: <code>path</code>.
	 * @param path The path of the directory of wich print the content
	 * @return the names of files and folders contained in a directory
	 */
	public static String[] getNamesOfFilesInDirectory(String path) {
		File cartella = new File(path);
		File []fileArray=cartella.listFiles();
		String []fileNames = new String[fileArray.length];
		for(int i=0;i<fileNames.length;i++)
			fileNames[i]=fileArray[i].getName();
		return fileNames;

	}

	/**
	 * Makes in a String array the absolute paths of files and folders contained in the directory
	 * with absolute path: <code>path</code>.
	 * @param path The path of the directory of wich print the content
	 * @return the names of files and folders contained in a directory
	 */
	public static String[] getPathsOfFilesInDirectory(String path) {
		File cartella = new File(path);
		File []fileArray=cartella.listFiles();
		String []filePaths = new String[fileArray.length];
		for(int i=0;i<filePaths.length;i++)
			filePaths[i]=fileArray[i].getPath();
		return filePaths;

	}

	/**
	 * Get the files contained in a directory.
	 * @param path The path of the directory of wich print the content
	 * @return the names of files and folders contained in a directory
	 */
	public static File[] getFilesInDirectory(String path) {
		File cartella = new File(path);
		return cartella.listFiles();
	}

	
	/**
	 * Return the last modified file between that containd in the array <code>files</code>
	 * @param files Array of files to check
	 * @return The last modified file
	 */
	public static File lastModifiedFile(File[] files) {
		
		if (files==null || files.length==0)
			return null;
		
		int index=0;
		long lastModify=0;
		long tmp_long=0;
		
		for(int i=0;i<files.length;i++) {
			tmp_long=files[i].lastModified();
			
			if(tmp_long>lastModify) {
				lastModify=tmp_long;
				index=i;
			}
		}
		
		return files[index];
	}

	/**
	 * Return the last modified file between that containd in the directory <code>directoryPath</code>
	 * @param directoryPath Path of the directory
	 * @return The last modified file in the specified directory
	 */
	public static File lastModifiedFile(String directoryPath) {
		
		File[] files = new File(directoryPath).listFiles();
		return lastModifiedFile(files);
		
	}

	/**
	 * Return the extension of the <i>File</i>. If the <i>File</i> is a <i>directory</i> return an empty string.
	 * @param File
	 * @return The extension of the <i>File</i>
	 */
	public static String getExtention(File file) {
		
		String name;
		
		if(file.isDirectory()) return "";
		
		name=file.getName();
		
		return name.substring(name.lastIndexOf(".")+1,name.length());
		
	}

	/**
	 * Return the extension of the <i>File</i>. If the <i>File</i> is a <i>directory</i> return an empty string.
	 * @param filePath Absolute path of the file
	 * @return
	 */
	public static String getExtention(String filePath) {
		
		return getExtention(new File(filePath));
		
	}
	
	/**
	 * Return the name of the <i>File</i> without the extension.
	 * If <i>File</i> is a directory return its name.
	 * @param file
	 * @return The name of the FTPFile without the extension
	 */
	public static String getNameWithoutExtension(File file) {
		
		if (file.isDirectory()) return file.getName();
		
		String extension=getExtention(file);
		String fileName=file.getName();
		
		return fileName.substring(0,fileName.length()-extension.length()-1);
		
	}

	/**
	 * Return the name of the <i>File</i> without the extension.
	 * If <i>File</i> is a directory return its name.
	 * @param filePath Absolute path of the file
	 * @return
	 */
	public static String getNameWithoutExtension(String filePath) {
		
		return getNameWithoutExtension(new File(filePath));
		
	}

	/**
	 * Return the absolute path of the <i>File</i> without the extension.
	 * If <i>File</i> is a directory return its name.
	 * @param file
	 * @return The name of the FTPFile without the extension
	 */
	public static String getPathWithoutExtension(File file) {
		
		if (file.isDirectory()) return file.getPath();
		
		String extension=getExtention(file);
		String fileName=file.getPath();
		
		return fileName.substring(0,fileName.length()-extension.length()-1);
		
	}

	/**
	 * Return the absolute path of the <i>File</i> without the extension.
	 * If <i>File</i> is a directory return its name.
	 * @param filePath Absolute path of the file
	 * @return
	 */
	public static String getPathWithoutExtension(String filePath) {
		
		return getPathWithoutExtension(new File(filePath));
		
	}
	
	/**
	 * Return the absolute path of the father directory of the current file/directory.
	 * If the current file not exist or there isn't a father director of this file, return <code>null</code>.
	 * @param filePath
	 * @return The absolute path of the father directory of the current file/directory
	 */
	public static String getFatherDirectory(String filePath) {
		File f = new File(filePath);
		if (!f.exists()) return null;
		
		int endIndex=filePath.lastIndexOf(File.separator);
		if (endIndex<=0) return null;
		if(endIndex==2) endIndex++;//for the root such as --> C:
		return filePath.substring(0,endIndex);
	}
	
	/**
	 * Return the absolute path of the father directory of the current file/directory.
	 * If the current file not exist or there isn't a father director of this file, return <code>null</code>.
	 * @param file
	 * @return The absolute path of the father directory of the current file/directory
	 */
	public static String getFatherDirectory(File file) {
		if (!file.exists()) return null;
		return getFatherDirectory(file.getAbsolutePath());
	}

	/**
	 * Create a jpg file from a <code>RenderedImage</code>
	 * @param renderedImage
	 * @param filePath
	 * @throws IOException
	 */
	public static void writeJPG(RenderedImage renderedImage,String filePath) throws IOException {
		ImageIO.write(renderedImage, "jpg", new File(filePath));

	}

	public static byte[] imageToByteArray(String filePath) throws IOException {
		
		File fnew=new File(filePath);
		BufferedImage originalImage=ImageIO.read(fnew);
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		ImageIO.write(originalImage, getExtention(fnew), baos );
		
		return baos.toByteArray();


	}

	/**
	 * Return the length in byte of the specified file. If is a <i>directory</i> return its size
	 * including its subdirectory.
	 * @param filePath Absolute path of the directory
	 * @return The size in byte of the specified file/directory
	 */
	public static double getSizeKB(String filePath) {
		File f = new File(filePath);
		//base
		if (!f.isDirectory()) return ((double)f.length())/1024;
		//the file is a directory
		//recursion
		double size=0.0;
		File[] directoryContent= f.listFiles();
		if (directoryContent!=null) {
			for (int i=0; i<directoryContent.length;i++)
				size+= getSizeKB(directoryContent[i].getAbsolutePath());
		}
		return size;
	}
	
	/**
	 * Return the length in byte of the specified file. If is a <i>directory</i> return its size
	 * including its subdirectory.
	 * @param filePath Absolute path of the directory
	 * @return The size in byte of the specified file/directory
	 */
	public static double getSizeMB(String filePath) {
		File f = new File(filePath);
		//base
		if (!f.isDirectory()) return ((double)f.length())/(1024*1024);
		//the file is a directory
		//recursion
		double size=0.0;
		File[] directoryContent= f.listFiles();
		if (directoryContent!=null) {
			for (int i=0; i<directoryContent.length;i++)
				size+= getSizeMB(directoryContent[i].getAbsolutePath());
		}
		return size;
	}

	/**
	 * Return the number of all the files contained in the <i>directory</i> 
	 * including that of its subdirectory. If the file is not a directory return 1.
	 * @param filePath Absolute path of the directory
	 * @return The number of all the files contained in the <i>directory</i>
	 */
	public static int getNumberOfFiles(String filePath) {
		File f = new File(filePath);
		//base
		if (!f.isDirectory()) return 1;
		//the file is a directory
		//recursion
		int counter=0;
		File[] directoryContent= f.listFiles();
		if (directoryContent!=null) {
			for (int i=0; i<directoryContent.length;i++)
				counter+= getNumberOfFiles(directoryContent[i].getAbsolutePath());
		}
		return counter;
	}
	
	/**
	 * Format a text table addind the right number of space to the table columns
	 * @param tableName Tha name of the table
	 * @param myTable The table as a string array
	 * @param fieldSep Fields separator
	 * @param commentIdentifier Comment identifier
	 * @param CommentedRowBehaviour 0 = commented row not considered, 1 = try to format commented rows, 2 = delete commented rows
	 * @param regex Regular expression to remove. If formed by several consecutives field separator (i.e. ",,,,,")
	 * @return A string representing the formatted table
	 * @throws TextFormatException
	 */
	public static String FormatTable(String tableName,String[] myTable,String fieldSep,String commentIdentifier,int CommentedRowBehaviour,String regex) throws TextFormatException {
		
		boolean wrongNumberOfFields=false;
		int j=0;
		boolean isCommented=false;
		
		int nCol=0;
		int nRow=myTable.length;
		
		//get fields table
		int i=0;
		while(i<nRow && myTable[i].startsWith(commentIdentifier))
			i++;
		
		if(i>=nRow) throw new TextFormatException("No fields in table: "+ tableName);
		
		String[] tmp_filedsName=myTable[i].split(fieldSep,-1);
		
		for(int h=0;h<tmp_filedsName.length;h++) {
			if(tmp_filedsName[h].equals(""))
				break;
			else
				nCol++;
		}
		
		String[]  filedsName=Arrays.copyOfRange(tmp_filedsName, 0, nCol);
		
		ArrayList<String[]> list_formattedTable=new ArrayList<String[]>();
		int k=0;
		
		list_formattedTable.add(new String[nCol]);
		for (j=0;j<nCol;j++)
			list_formattedTable.get(k)[j]=filedsName[j].trim();
		
		//get rows of table
		HashMap<Integer,String> hash_commentedRow=new HashMap<Integer, String>();
		i++;
		
		if(i>=nRow) throw new TextFormatException("No rows in table: "+ tableName);
		
		//first row of the table
		String[] rowContent=myTable[i].split(fieldSep,-1);
		isCommented=myTable[i].startsWith(commentIdentifier);
		if(rowContent.length!=nCol) {
			
			wrongNumberOfFields=false;
			
			if(rowContent.length<nCol) 
				wrongNumberOfFields=true;
			else{
			
				for(int h=nCol;h<rowContent.length;h++) {
					if(!rowContent[h].equals("")) {
						wrongNumberOfFields=true;
						break;
					}
				}
			}
			
			if(wrongNumberOfFields && !isCommented) throw new TextFormatException("Wrong number of fields at row: "+ i +". \n Number of fields: " + nCol +". \n Row content: " + myTable[i]);
		}
		
		//first row is ok
		k++;
		if(!isCommented || (CommentedRowBehaviour==1 && !wrongNumberOfFields)) {
			//(tryToFormatCommentedRow && m[i].length==nCol) --> If false is a custom comment
			list_formattedTable.add(new String[nCol]);
			for (j=0;j<nCol;j++)
				list_formattedTable.get(k)[j]=rowContent[j].trim();
		}else if(CommentedRowBehaviour==2) {//delete commented row
			k--;
		}else list_formattedTable.add(new String[] {myTable[i]});
		
		if(isCommented && CommentedRowBehaviour!=2) hash_commentedRow.put(k,myTable[i]);
		
		//get the other rows
		i++;
		while(i<nRow) {
			
			rowContent=myTable[i].split(fieldSep,-1);
			isCommented=myTable[i].startsWith(commentIdentifier);
			if(rowContent.length!=nCol) {
				
				wrongNumberOfFields=false;
				
				if(rowContent.length<nCol) 
					wrongNumberOfFields=true;
				else{
				
					for(int h=nCol;h<rowContent.length;h++) {
						if(!rowContent[h].equals("")) {
							wrongNumberOfFields=true;
							break;
						}
					}
				}
				
				if(wrongNumberOfFields && !isCommented) throw new TextFormatException("Wrong number of fields at row: "+ i +". \n Number of fields: " + nCol +". \n Row content: " + myTable[i]);
			}

			//Row is ok
			k++;
			if(!isCommented || (CommentedRowBehaviour==1 && !wrongNumberOfFields)) {
				//(tryToFormatCommentedRow && m[i].length==nCol) --> If false is a custom comment
				list_formattedTable.add(new String[nCol]);
				for (j=0;j<nCol;j++)
					list_formattedTable.get(k)[j]=rowContent[j].trim();
			}else if(CommentedRowBehaviour==2) {//delete commented row
				k--;
			}else list_formattedTable.add(new String[] {myTable[i]});

			if(isCommented && CommentedRowBehaviour!=2) hash_commentedRow.put(k, myTable[i]);

			i++;
		}
		
		
		//now format table columns
		String[][]m= list_formattedTable.toArray(new String[0][]);
		for(j=0;j<nCol;j++) {
			formatTableColumn(m,j,nCol,CommentedRowBehaviour==1,hash_commentedRow);
		}
		
		
		StringBuilder sb = new StringBuilder();
		
		for(i=0;i<=k;i++) {
			for(j=0;j<m[i].length;j++)
				sb.append(m[i][j] + fieldSep);
			sb.delete(sb.length()-1, sb.length());
			sb.append("\n");
		}
		sb.delete(sb.length()-1, sb.length());
		
		return sb.toString();
	}
	
	/**
	 * Format a single column of a table adding the right number of space to each column
	 * @param m String matrix
	 * @param j Column index
	 * @param j Number of column
	 * @param commentIdentifier The comment identifier
	 * @param tryToFormatCommentedRow True if try to format also the commented rows
	 */
	private static void formatTableColumn(String m[][],int j,int nCol, boolean tryToFormatCommentedRow,HashMap<Integer, String> commentedRows) {
		
		StringBuilder sb=null;
		int nRow=m.length;
		int maxLength=0;
		boolean isCommented=false;
		for(int i=0;i<nRow;i++) {
			isCommented=commentedRows.containsKey(i);
			if(!isCommented || (tryToFormatCommentedRow && m[i].length==nCol))
				//(tryToFormatCommentedRow && m[i].length==nCol) --> If false is a custom comment
				if(m[i][j].length()>maxLength) maxLength=m[i][j].length();
		}
		
		//format the table column
		int currentLength=0;
		
		for(int i=0;i<nRow;i++) {
			isCommented=commentedRows.containsKey(i);
			if(!isCommented || (tryToFormatCommentedRow && m[i].length==nCol)) {
				//(tryToFormatCommentedRow && m[i].length==nCol) --> If false is a custom comment
				currentLength=m[i][j].length();
				sb=new StringBuilder(m[i][j]);
				for(int k=0;k<maxLength-currentLength;k++)
					sb.append(" ");
				m[i][j]=sb.toString();
			}
		}
		
	}

	/**
	 * Write the table in the specified file
	 * @param table
	 * @param filePath
	 * @param tablePrefix
	 * @param tableSuffix
	 * @param fieldSeparator
	 * @throws TableException
	 * @throws IOException
	 */
	public static void createTable(Table table, String filePath,String tablePrefix,String tableSuffix,char fieldSeparator,char textQualifier) throws TableException, IOException{
		
		writeFile(filePath, table.toString(tablePrefix,tableSuffix,fieldSeparator,textQualifier), false);
		
	}

	/**
	 * If the parse fail return null
	 * @param s
	 * @return
	 */
	public static Integer integerTryParse(String s) {
		try {
			return new Integer(Integer.parseInt(s));
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * Try to recognize the encoding of the file
	 * <a href="https://code.google.com/p/juniversalchardet/">https://code.google.com/p/juniversalchardet/</a>
	 * @param filePath
	 * @return
	 */
	public static String getFileEncoding(String filePath) {
	    byte[] buf = new byte[4096];
	    String fileName = filePath;
	    java.io.FileInputStream fis=null;
	    UniversalDetector detector=null;
	    String encoding=null;
	    
		try {
			fis = new java.io.FileInputStream(fileName);

		    // (1)
		    detector = new UniversalDetector(null);
	
		    // (2)
		    int nread;
		    while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
		      detector.handleData(buf, 0, nread);
		    }
		    // (3)
		    detector.dataEnd();
	
		    // (4)
		    encoding = detector.getDetectedCharset();
		    
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
		    // (5)
		    if (detector!=null) detector.reset();
				try {
				    if (fis!=null)
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		
		return encoding;

	}


	/**
	 * Transform an xml document to a string
	 * @param doc
	 * @return
	 * @throws TransformerException 
	 */
	public static String xmlDocToString(Document doc) throws TransformerException {
		//xml to string
		StringWriter sw = new StringWriter();
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.transform(new DOMSource(doc), new StreamResult(sw));
        return sw.toString();
	}

	/**
	  Remove a directory and all of its contents.
	  The results of executing File.delete() on a File object
	  that represents a directory seems to be platform
	  dependent. This method removes the directory
	  and all of its contents.

	  @return true if the complete directory was removed, false if it could not be.
	  If false is returned then some of the files in the directory may have been removed.

	 */
	public static boolean removeDirectory(File directory) {

		// System.out.println("removeDirectory " + directory);

		if (directory == null)
			return false;
		if (!directory.exists())
			return true;
		if (!directory.isDirectory())
			return false;

		String[] list = directory.list();

		// Some JVMs return null for File.list() when the
		// directory is empty.
		if (list != null) {
			for (int i = 0; i < list.length; i++) {
				File entry = new File(directory, list[i]);

				//        System.out.println("\tremoving entry " + entry);

				if (entry.isDirectory())
				{
					if (!removeDirectory(entry))
						return false;
				}
				else
				{
					if (!entry.delete())
						return false;
				}
			}
		}

		return directory.delete();
	}
	
	/**
	  Remove the contents of a directory.
	  The results of executing File.delete() on a File object
	  that represents a directory seems to be platform
	  dependent. This method removes the directory
	  and all of its contents.

	  @return true if the complete directory was removed, false if it could not be.
	  If false is returned then some of the files in the directory may have been removed.

	 */
	public static boolean removeDirectoryContent(File directory) {

		// System.out.println("removeDirectory " + directory);

		if (directory == null)
			return false;
		if (!directory.exists())
			return true;
		if (!directory.isDirectory())
			return false;

		String[] list = directory.list();

		// Some JVMs return null for File.list() when the
		// directory is empty.
		if (list != null) {
			for (int i = 0; i < list.length; i++) {
				File entry = new File(directory, list[i]);

				//        System.out.println("\tremoving entry " + entry);

				if (entry.isDirectory())
				{
					if (!removeDirectory(entry))
						return false;
				}
				else
				{
					if (!entry.delete())
						return false;
				}
			}
		}

		return true;
	}


	/**
	 * Split Array <code>v</code> according to <code>u</code> occurences in <code>v</code>
	 * @param v
	 * @param u
	 * @return
	 */
	public static Object[][] splitArray(Object[] v, Object[] u) {
		Object[][] toReturn=null;
		if (v == null || u==null || v.length==0 || u.length==0 || u.length>v.length) {
			if(v!=null) {
				toReturn=new Object[1][v.length];
				for (int i = 0; i < v.length; i++) {
					toReturn[0][i]=v[i];
				}				
			}
		}

		
		int[] occurences=subArrayIndexOf(v, u);
		
		if(occurences==null) {
			toReturn=new Object[1][v.length];
			for (int i = 0; i < v.length; i++) {
				toReturn[0][i]=v[i];
			}
		}else {
			int nPart=occurences.length;
			if(occurences[0]==0) nPart--;
			boolean occurenceAtTheEnd=true;
			if(occurences[occurences.length-1]!=v.length-u.length) {
				nPart++;
				occurenceAtTheEnd=false;
			}
			//List<Object> list_obj=new ArrayList<Object>();
			Object[] tmp_obj=null; 
			toReturn=new Object[nPart][];
			int counter=0;
			int firstIndex=0;
			int lastIndex=0;
			for (int i = 0; i < occurences.length; i++) {
				if(occurences[i]-firstIndex>0) {
					tmp_obj=new Object[occurences[i]-firstIndex];
					for (int j = firstIndex; j < occurences[i]; j++)
						tmp_obj[j-firstIndex]=v[j];
					toReturn[counter]=tmp_obj;
					counter++;
				}
				firstIndex=occurences[i]+u.length;
			}
			//get last part
			lastIndex=occurenceAtTheEnd?v.length-u.length:v.length;
			if(lastIndex-firstIndex<=0) {
				tmp_obj=new Object[lastIndex-firstIndex];
				for (int j = firstIndex; j < lastIndex; j++)
					tmp_obj[j-firstIndex]=v[j];
				toReturn[counter]=tmp_obj;
			}			
		}
		
		
		return toReturn;

		
	}



	/**
	 * check if v2 is contained in v1 and return the indexes
	 * of its occurence. The index return is the index in v1 of the first element of v2.
	 * If v1 is null or empty or v2 is null or empty or is longer than v1 return null.
	 * If no occurence of v2 is in v1 then return null.
	 * @param v1 Array in which search
	 * @param v2 Array to search
	 * @return 
	 */
	public static int[] subArrayIndexOf(Object[] v1,Object[] v2){
		if (v1 == null || v2==null || v1.length==0 || v2.length==0 || v2.length>v1.length) return null;
		int[] toReturn=null;
		List<Integer> list_occurrences=new ArrayList<Integer>();
		int i=0;
		int index=0;
		while(i<v1.length) {
			if(v1[i].equals(v2[0])) {//found a feasible occurence
				//freeze i
				int j=i;
				int k=0;
				index=i;
				j++;
				k++;
				while(k<v2.length) {
					if(j>=v1.length || !(v2[k].equals(v1[j]))) {
						if(j>=v1.length && v2.length==j-index) {//check if the occurence is at the end
							//do nothing
						}else {
							index=-1;
							j++;							
						}
						break;
					}
					j++;
					k++;
				}
				if (index>=0)
					list_occurrences.add(index);
			}
			i++;
		}
		
		if(list_occurrences.size()>0) {
			toReturn=new int[list_occurrences.size()];
			i=0;
			for (Iterator<Integer> iterator = list_occurrences.iterator(); iterator.hasNext();) {
				Integer integer = (Integer) iterator.next();
				toReturn[i]=integer;
				i++;
			}
		}
		return toReturn;
	}


	/**
	 * Split Array <code>v</code> according to <code>u</code> occurences in <code>v</code>
	 * @param v
	 * @param u
	 * @return
	 */
	public static byte[][] splitArray(byte[] v, byte[] u) {
		byte[][] toReturn=null;
		if (v == null || u==null || v.length==0 || u.length==0 || u.length>v.length) {
			if(v!=null) {
				toReturn=new byte[1][v.length];
				for (int i = 0; i < v.length; i++) {
					toReturn[0][i]=v[i];
				}				
			}
		}

		
		int[] occurences=subArrayIndexOf(v, u);
		
		if(occurences==null) {
			toReturn=new byte[1][v.length];
			for (int i = 0; i < v.length; i++) {
				toReturn[0][i]=v[i];
			}
		}else {
			int nPart=occurences.length;
			if(occurences[0]==0) nPart--;
			boolean occurenceAtTheEnd=true;
			if(occurences[occurences.length-1]!=v.length-u.length) {
				nPart++;
				occurenceAtTheEnd=false;
			}
			//List<Object> list_obj=new ArrayList<Object>();
			byte[] tmp_obj=null; 
			toReturn=new byte[nPart][];
			int counter=0;
			int firstIndex=0;
			int lastIndex=0;
			for (int i = 0; i < occurences.length; i++) {
				if(occurences[i]-firstIndex>0) {
					tmp_obj=new byte[occurences[i]-firstIndex];
					for (int j = firstIndex; j < occurences[i]; j++)
						tmp_obj[j-firstIndex]=v[j];
					toReturn[counter]=tmp_obj;
					counter++;
				}
				firstIndex=occurences[i]+u.length;
			}
			//get last part
			lastIndex=occurenceAtTheEnd?v.length-u.length:v.length;
			if(lastIndex-firstIndex<=0) {
				tmp_obj=new byte[lastIndex-firstIndex];
				for (int j = firstIndex; j < lastIndex; j++)
					tmp_obj[j-firstIndex]=v[j];
				toReturn[counter]=tmp_obj;
			}			
		}
		
		
		return toReturn;

		
	}



	/**
	 * check if v2 is contained in v1 and return the indexes
	 * of its occurence. The index return is the index in v1 of the first element of v2.
	 * If v1 is null or empty or v2 is null or empty or is longer than v1 return null.
	 * If no occurence of v2 is in v1 then return null.
	 * @param v1 Array in which search
	 * @param v2 Array to search
	 * @return 
	 */
	public static int[] subArrayIndexOf(byte[] v1,byte[] v2){
		if (v1 == null || v2==null || v1.length==0 || v2.length==0 || v2.length>v1.length) return null;
		int[] toReturn=null;
		List<Integer> list_occurrences=new ArrayList<Integer>();
		int i=0;
		int index=0;
		while(i<v1.length) {
			if(v1[i]==v2[0]) {//found a feasible occurence
				//freeze i
				int j=i;
				int k=0;
				index=i;
				j++;
				k++;
				while(k<v2.length) {
					if(j>=v1.length || !( v2[k]== v1[j])) {
						if(j>=v1.length && v2.length==j-index) {//check if the occurence is at the end
							//do nothing
						}else {
							index=-1;
							j++;							
						}
						break;
					}
					j++;
					k++;
				}
				if (index>=0)
					list_occurrences.add(index);
			}
			i++;
		}
		
		if(list_occurrences.size()>0) {
			toReturn=new int[list_occurrences.size()];
			i=0;
			for (Iterator<Integer> iterator = list_occurrences.iterator(); iterator.hasNext();) {
				Integer integer = (Integer) iterator.next();
				toReturn[i]=integer;
				i++;
			}
		}
		return toReturn;
	}

	/**
	 * Return a list of current runnin process.
	 * It's only for windows platform
	 * @return
	 */
	public static List<String> getRunningProcesses() {
		List<String> processes = new ArrayList<String>();
		try {
			String line;
			Process p = Runtime.getRuntime().exec("tasklist.exe /fo csv /nh");
			BufferedReader input = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			while ((line = input.readLine()) != null) {
				if (!line.trim().equals("")) {
					// keep only the process name
					processes.add(line);
				}

			}
			input.close();
		} catch (Exception err) {
			err.printStackTrace();
		}
		return processes;
	}

}
