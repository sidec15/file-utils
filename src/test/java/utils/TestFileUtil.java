package utils;

import java.io.File;
import java.util.zip.ZipException;

import org.junit.Test;

import com.sdc.file.utils.Util;
import com.sdc.file.zip.UtilZip;
import com.sdc.file.*;
import static org.junit.Assert.*;

/**
 * TestFileUtil.java
 */

/**
 * @author simone.decristofaro 17/set/2014
 */
public class TestFileUtil {

	@Test
	public void testFileUtil() {
		// System.out.println(
		// textFileEquals("C:\\Users\\Simone\\workspace\\Ferrovie\\OutputData\\ATAC_codeseConFerrovie.txt",
		// "C:\\Users\\Simone\\Desktop\\ATAC_codeseConFerrovie.txt"));
		// System.out.println(Arrays.toString(readFile("ATAC_codese.txt")));
		// readFile("C:\\Users\\Simone\\workspace\\Ferrovie\\ATAC_anagservizioprogConFerrovie.txt");
		// System.out.println("Elaborating...");
		// boolean b
		// =textFileEquals("C:\\Users\\Simone\\Desktop\\HyperPath Transit\\trunk\\data\\ATAC\\ATAC_anagservizioprogConFerrovie.txt",
		// "C:\\Users\\Simone\\Desktop\\HyperPath Transit\\trunk\\data\\ATAC\\ATAC_anagservizioprog.txt");
		// boolean b
		// =textFileEquals("C:\\Users\\Simone\\Desktop\\ATAC_anagservizioprog.txt",
		// "C:\\Users\\Simone\\Desktop\\ATAC_anagservizioprogModificata.txt");
		// System.out.println(b);

		// try {
		// imageToByteArray("C:\\Users\\Simone\\Desktop\\Nuova Cartella\\Squall.JPG");
		// } catch (IOException e) {
		// e.printStackTrace();
		// }

		// System.out.println("size in mb of: C:\\Users\\Simone\\Desktop\\Nuova cartella :"
		// + getSizeMB("C:\\Users\\Simone\\Desktop\\Nuova cartella"));
		// System.out.println("size in mb of: C:\\Users\\Simone\\Desktop\\Nuova cartella :"
		// + getNumberOfFiles("C:\\Users\\Simone\\Desktop\\Nuova cartella"));

		// System.out.println(new
		// File("C:\\Users\\Simone\\Desktop\\Nuova cartella\\Nuova cartella2"));

		// File f = new File("C:\\MSOCache");
		// File f = new File("C:\\Nuova cartella");

		// String[] table;
		// try {
		// table = readFile("prova.csv");
		// System.out.println(FormatTable("test", table, ",", "#", 2));
		// } catch (IOException e) {
		// e.printStackTrace();
		// } catch (TextFormatException e) {
		// e.printStackTrace();
		// }

		// String
		// filePath="C:\\Users\\Simone\\Lavoro SISTeMA\\repo\\TDE\\branches\\GoogleTransit\\exec\\multiImport_output\\output_dval.csv";
		// boolean makeNewFile=true;
		// boolean fieldsInSameRowOfName=false;
		// boolean compact=false;
		// Table.FormatTablesInTxtFile(filePath, "$", ":", ',', '#', '"',
		// compact, makeNewFile, fieldsInSameRowOfName);
		// Table t=Table.getTableInFile("C:\\Users\\Simone\\Desktop\\new 2.txt",
		// "OPTIMIZATION", "$", ":", ',', '#','"',false);
		// System.out.println(t.toString());

		// String filePath="CommandLine.csv";
		// System.out.println(getFileEncoding(filePath));

		// Integer[] v= {1,1,1};
		// System.out.println("v: " + Arrays.toString(v));
		// Integer[] u= {1};
		// System.out.println("u: " + Arrays.toString(u));
		//
		// Object[][] v1=com.sdc.file.utils.Util.splitArray(v, u);
		// for (int i = 0; i < v1.length; i++) {
		// System.out.println("oc"+i+": "+ Arrays.toString(v1[i]));
		// }

		File f = null;
		String outputDirPath;
		try {
			outputDirPath = "/fileTest/output/";
			UtilZip.decompress(getClass().getResource("/fileTest/prova.zip").getPath(),getClass().getResource(outputDirPath).getPath());
			// check if file exists
			f = new File(TestFileUtil.class.getResource(outputDirPath).getPath());

			assertNotNull("OutPut file is null", f);
			assertTrue("OutPut directory does not exist", f.exists());
			assertTrue("OutPut directory does is empty", f.listFiles().length > 0);
			assertTrue("File test.txt does not exist", new File(f.getAbsolutePath() + "/test.txt").exists());
			assertTrue("File Allegria - Cirque du soleil.mp3 does not exist", new File(f.getAbsolutePath() + "/Allegria - Cirque du soleil.mp3").exists());

		} catch (ZipException e) {
			e.printStackTrace();
		} finally {
			Util.removeDirectoryContent(f);
			assertTrue("OutPut directory is not empty", f.listFiles().length == 0);
		}

	}

	
	public void testGTFS() {
		File f = null;
		String outputDirPath;
		try {
			outputDirPath = "/fileTest/output/";
			UtilZip.decompress(TestFileUtil.class.getResource("/fileTest/google_transit.zip").getPath(), TestFileUtil.class.getResource(outputDirPath).getPath());
			// check if file exists
			f = new File(TestFileUtil.class.getResource(outputDirPath).getPath());


		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Util.removeDirectoryContent(f);
			assertTrue("OutPut directory is not empty", f.listFiles().length == 0);
		}

	}
	
}
