/**
 * FileUtilsAzureBlobStorageAzureBlobStorage.java
 */
package utils;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.google.common.io.Files;
import com.sdc.file.utils.FileUtilsAzureBlobStorage;

/**
 * @author Simone De Cristofaro
 * Jan 21, 2020
 */
public class FileUtilsAzureBlobStorageTest {

    private static final String BASE_OUTPUT = "target/test/file-utils/";
    private static final String BASE_INPUT = "/file-utils"; 
    
    @Test
    public void testJoinResourcesPath() {
        
        String path1 = "path1";
        String path2 = "path2";
        assertThat(FileUtilsAzureBlobStorage.joinResourcesPath(path1, path2), is(equalTo("path1/path2")));
    }
    
    @Test
    public void testJoinFiles() {
        String path1 = null;
        String path2 = "leaf.txt";
        String result = FileUtilsAzureBlobStorage.joinFiles(path1, path2);
        assertThat(result, is(equalTo(path2)));

        path1 = "root";
        path2 = null;
        result = FileUtilsAzureBlobStorage.joinFiles(path1, path2);
        assertThat(result, is(equalTo(path1)));
        
        path1 = "root";
        path2 = "leaf.txt";
        result = FileUtilsAzureBlobStorage.joinFiles(path1, path2);
        assertThat(result.length(), is(equalTo(path1.length() + path2.length() + FileUtilsAzureBlobStorage.FILE_SEPARATOR.length())));
        
        path1 = "root" + FileUtilsAzureBlobStorage.FILE_SEPARATOR + FileUtilsAzureBlobStorage.FILE_SEPARATOR;
        path2 = "branch" + FileUtilsAzureBlobStorage.FILE_SEPARATOR;
        result = FileUtilsAzureBlobStorage.joinFiles(path1, path2);
        assertThat(result.length(), is(equalTo(path1.length() + path2.length() + FileUtilsAzureBlobStorage.FILE_SEPARATOR.length() - 3 * FileUtilsAzureBlobStorage.FILE_SEPARATOR.length())));
        
    }
    
    @Test
    public void testJoinUrls() {
        String path1 = null;
        String path2 = "leaf.txt";
        String result = FileUtilsAzureBlobStorage.joinUrls(path1, path2);
        assertThat(result, is(equalTo(path2)));

        path1 = "root";
        path2 = null;
        result = FileUtilsAzureBlobStorage.joinUrls(path1, path2);
        assertThat(result, is(equalTo(path1)));
        
        path1 = "root";
        path2 = "leaf.txt";
        result = FileUtilsAzureBlobStorage.joinUrls(path1, path2);
        assertThat(result.length(), is(equalTo(path1.length() + path2.length() + FileUtilsAzureBlobStorage.URL_SEPARATOR.length())));
        
        path1 = "root" + FileUtilsAzureBlobStorage.URL_SEPARATOR + FileUtilsAzureBlobStorage.URL_SEPARATOR;
        path2 = "branch" + FileUtilsAzureBlobStorage.URL_SEPARATOR;
        result = FileUtilsAzureBlobStorage.joinUrls(path1, path2);
        assertThat(result.length(), is(equalTo(path1.length() + path2.length() + FileUtilsAzureBlobStorage.URL_SEPARATOR.length() - 3 * FileUtilsAzureBlobStorage.URL_SEPARATOR.length())));
        
    }
    
    @Test
    public void testReadFile() throws URISyntaxException, FileNotFoundException, IOException {
        
        File file = TestResourceManager.getResourceFile(getClass(), BASE_INPUT + "/link.csv");
        
        List<String> content = FileUtilsAzureBlobStorage.readFile(file);
        assertThat(content.size(), is(equalTo(5)));
        
        content = FileUtilsAzureBlobStorage.readFile(new FileInputStream(file));
        assertThat(content.size(), is(equalTo(5)));
    }
    
    @Test
    public void testReadCsv() throws URISyntaxException, FileNotFoundException, IOException {
        
        File file = TestResourceManager.getResourceFile(getClass(), BASE_INPUT + "/link.csv");
        
        List<Integer> content = FileUtilsAzureBlobStorage.readCsv(file, null, (s, c) -> s.split(FileUtilsAzureBlobStorage.CSV_SEPARATOR).length);
        
        assertThat(content.size(), is(equalTo(4)));
    }
 
    @Test
    public void testReadCsvWithContext() throws URISyntaxException, FileNotFoundException, IOException {
        
        File file = TestResourceManager.getResourceFile(getClass(), BASE_INPUT + "/link.csv");
        
        LinkReaderContext context = new LinkReaderContext(4, 1);
        
        List<Integer> content = FileUtilsAzureBlobStorage.readCsv(file, context, (s, c) -> {
            
            int noLoadedLink = ++c.counter;
            double perc = (double) noLoadedLink / c.noLinks * 100;
            if (perc > c.previousPrintedPerc + c.percStep) {
                double newPrint = c.previousPrintedPerc + c.percStep;
                c.previousPrintedPerc = newPrint;
                System.out.println(String.format("Loaded %s%% of links: %d / %d", c.df.format(newPrint), noLoadedLink, c.noLinks));
            }   

            
            return s.split(FileUtilsAzureBlobStorage.CSV_SEPARATOR).length;
        });
        
        assertThat(content.size(), is(equalTo(4)));
    }
 
    @Test
    public void testReadCsvInHadoopFileSystem() throws URISyntaxException, IOException {
        
        File inputFile = TestResourceManager.getResourceFile(getClass(), BASE_INPUT + "/link.csv");
        
        List<String> values = FileUtilsAzureBlobStorage.readCsv(inputFile.toURI().toString(), null, (s, c) -> s);
        
        assertThat(values, hasSize(4));
    }
    
    @Test
    public void testAddTrailingSlash() {
        
        String filePath = "file";
        assertThat(FileUtilsAzureBlobStorage.addTrailingSlash(filePath), is(equalTo("file/")));
    }
    
    @Test
    public void testAddTrailingFileSeparator() {
        
        String filePath = "file";
        assertThat(FileUtilsAzureBlobStorage.addTrailingFileSeparator(filePath), is(equalTo("file" + File.separator)));
    }
    
    @Test
    public void testRemoveTrailingFileSeparator() {
        
        String filePath = "file" + File.separator;
        assertThat(FileUtilsAzureBlobStorage.removeTrailingFileSeparator(filePath), is(equalTo("file")));
    }
    
    @Test
    public void testCountLines() throws Throwable {
        
        File inputFile = TestResourceManager.getResourceFile(getClass(), BASE_INPUT + "/link.csv");
        assertThat(FileUtilsAzureBlobStorage.countLines(inputFile), is(equalTo(5)));
    }
    
    @Test
    public void testWriteFile() throws IOException {
        
        List<String> fileContent = Arrays.asList("l1", "l2");
        String filePath = BASE_OUTPUT + "test-write";
        
        FileUtilsAzureBlobStorage.writeFile(fileContent, filePath);
        
        File actual = new File(filePath);
        assertThat(actual.exists(), is(true));
        
        List<String> actualContent = Files.readLines(actual, Charset.defaultCharset());
        
        assertThat(actualContent, is(equalTo(fileContent)));
        
    }
    
    @Test
    public void testDeleteIfExists() throws IOException {
        
        String pathname = BASE_OUTPUT + "test-delete";
        File file = new File(pathname);
        FileUtils.writeLines(file, Arrays.asList("ciao"));
        
        FileUtilsAzureBlobStorage.deleteIfExists(pathname);
        
        assertThat(new File(pathname).exists(), is(false));
        
    }
    
    @Test
    public void testDate() {
        String s = "2014-12-04T14:40:00Z";
        
        DateTimeFormatter dtf = DateTimeFormatter.ISO_DATE_TIME;
        
        LocalDateTime ldt = LocalDateTime.parse(s, dtf);
        System.out.println(ldt);
        
        ZonedDateTime zdt = ZonedDateTime.parse(s, dtf);
        System.out.println(zdt);
        
        System.out.println(zdt.toEpochSecond());
        System.out.println(ZonedDateTime.of(ldt, ZoneId.of("UTC")).toEpochSecond());
        
        
    }
    
    
    private static class LinkReaderContext{
        int counter;
        double previousPrintedPerc;
        double percStep;
        int noLinks;
        DecimalFormat df;
        /**
         * @param percStep
         * @param noLinks
         */
        public LinkReaderContext(int noLinks, double percStep) {

            super();
            this.percStep = percStep;
            this.noLinks = noLinks;
            df = new DecimalFormat("#.00");
        }
        
        
        
    }
    
    
}
