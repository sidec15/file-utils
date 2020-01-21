/**
 * AzureBlobStorageFileUtils.java
 */
package com.sdc.file.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.BiFunction;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Simone De Cristofaro
 * Jan 21, 2020
 */
public class FileUtilsAzureBlobStorage {

    private static final Logger LOG = LoggerFactory.getLogger(FileUtils.class);
    
    public static final NumberFormat NF = NumberFormat.getInstance();
    public static final Charset UTF8 = Charset.forName("utf8");
    public static final String EMPTY = "";
    public static final String CSV_SEPARATOR = ",";
    public static final String LINESTRING_POINTS_SEPARATOR = CSV_SEPARATOR + "\\s{0,1}";
    public static final String NEW_LINE = System.getProperty("line.separator");
    public static final String CSV_SEPARATOR_TOKEN = ",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
    
    public static final int BYTE_CONVERSION_FACTOR = 1024;    
    public static final String SLASH = "/";
    public static final String URL_SEPARATOR = SLASH;
    public static final String FILE_SEPARATOR = File.separator;
    public static final String RESOURCE_FILE_SEPARATOR = "/";
    
    private FileUtilsAzureBlobStorage() {}
    
    
    public static String joinUrls(String path1, String path2) {
        return join(path1, path2, URL_SEPARATOR);
    }
    
    public static String joinFiles(String path1, String path2) {
        Optional<String> checkResOp = jaseInputCheckForJoin(path1, path2);
        if(checkResOp.isPresent())
            return checkResOp.get();
        
        return Paths.get(path1, path2).toString();
    }
    
    public static String joinResourcesPath(String path1, String path2) {
        return join(path1, path2, RESOURCE_FILE_SEPARATOR);
    }

    
    private static String join(String path1, String path2, String separator) {
        Optional<String> checkResOp = jaseInputCheckForJoin(path1, path2);
        if(checkResOp.isPresent())
            return checkResOp.get();
        
        String cleanedPath1 = cleanPath(path1, separator);
        String cleanedPath2 = cleanPath(path2, separator);
        
        return cleanedPath1 + separator + cleanedPath2;
        
    }

    private static String cleanPath(String path, String separator) {

        String cleanedPath = path;
        while(cleanedPath.endsWith(separator))
            cleanedPath = cleanedPath.substring(0, cleanedPath.length() - separator.length());
        return cleanedPath;
    }

    private static Optional<String> jaseInputCheckForJoin(String path1, String path2) {

        if(path1 == null) {
            if(path2 == null)
                return null;
            return Optional.of(path2);
        }
        else if(path2 == null) {
            return Optional.of(path1);
        }
        
        return Optional.empty();
    }
    
    /**
     * @param filePath
     * @return String
     */
    public static String addTrailingSlash(String filePath) {
    
        return addTrailingSuffix(filePath, SLASH);
    }
    
    /**
     * @param filePath
     * @return String
     */
    public static String addTrailingFileSeparator(String filePath) {
    
        return addTrailingSuffix(filePath, File.separator);
    }
    
    /**
     * @param filePath
     * @return String
     */
    public static String removeTrailingFileSeparator(String filePath) {
    
        return removeTrailingSuffix(filePath, File.separator);
    }
    
    
    /**
     * 
     * @param filePath
     * @param suffix
     * @return String
     */
    public static String addTrailingSuffix(String filePath, String suffix) {
    
        if(!filePath.endsWith(suffix)){
            filePath += suffix;
        }
        return filePath;
    }
    
    /**
     * 
     * @param filePath
     * @param suffix
     * @return String
     */
    public static String removeTrailingSuffix(String filePath, String suffix) {
    
        if(filePath.endsWith(suffix)){
            filePath = filePath.substring(0, filePath.length() - suffix.length());
        }
        return filePath;
    }

    public static int countLines(File file) throws IOException {
        InputStream is = new BufferedInputStream(new FileInputStream(file));
        try {
            byte[] c = new byte[1024];
            int count = 0;
            int readChars = 0;
            boolean empty = true;
            while ((readChars = is.read(c)) != -1) {
                empty = false;
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
            }
            return (count == 0 && !empty) ? 1 : count + 1;
        } finally {
            is.close();
        }
    }

    
    public static FileSystem getHadoopFileSystem(String uri) {
        return getHadoopFileSystem(new Configuration(), getContainerFromAzureUri(uri));
    }
    
    public static FileSystem getHadoopFileSystem(Configuration configuration, String container) {
    
        // get the hadoop file system interface to manage the storage
        FileSystem hdfs = null;
        try {
    
            if (container != null) {
                URI containerUri = new URI(container);
                if(LOG.isDebugEnabled())
                    LOG.debug(String.format("Container URI: %s", containerUri));
                hdfs = FileSystem.get(containerUri, configuration);
            }
            else
                hdfs = FileSystem.get(configuration);
    
        }
        catch (IOException | URISyntaxException e) {
            throw new IllegalStateException("Error retreiving Hadoop file system", e);
        }
        return hdfs;
    }

    public static String getContainerFromAzureUri(String uri) {
        
        String container = null;
        
        if(uri != null && !uri.isEmpty() 
                && (uri.startsWith("wasbs://") || uri.startsWith("wasb://"))) {
            
            String targetEndToken = ".blob.core.windows.net/";
            
            int beginIndex = 0;
            int endIndex = uri.indexOf(targetEndToken);
            
            if(endIndex > 0) {
                endIndex = endIndex + targetEndToken.length();
                container = uri.substring(beginIndex, endIndex);
            }            
        }
    
        
        return container;
    }

    public static void writeFile(List<String> fileContent, String filePath) {

        FileSystem hdfs = getHadoopFileSystem(filePath);

        BufferedOutputStream bos = null;
        try {
            if (LOG.isDebugEnabled())
                LOG.debug(String.format("Creating file: %s", filePath));
            FSDataOutputStream output = hdfs.create(new Path(filePath));
            bos = new BufferedOutputStream(output);
            for (String row : fileContent) {
                bos.write(String.format("%s%s", row, NEW_LINE).getBytes(UTF8));
            }
        }
        catch (IOException e) {
            throw new IllegalStateException("Error deleting file on hadoop file system", e);
        }
        finally {
            if (bos != null)
                try {
                    bos.close();
                }
                catch (IOException e) {
                    throw new IllegalStateException("Error closing buffer output stream persisting simple file on hadoop file system", e);
                }
        }
    }
    
    public static <E, C> List<E> readCsv(File file, C context, BiFunction<String, C, E> transformer) throws IOException{
        
        return readCsv(new FileInputStream(file), context, transformer);

    }
    
    public static <E, C> List<E> readCsv(InputStream is, C context, BiFunction<String, C, E> transformer) throws IOException{
        
        return readFile(is, context, transformer, true);

    }
    

    public static List<String> readFile(InputStream is) throws IOException{
        
        return readFile(is, null, (s, c) -> s, false);

    }
    
    public static List<String> readFile(File file) throws IOException{
        
        return readFile(openStreamFromHadoopFileSystem(file.getAbsolutePath()), null, (s, c) -> s, false);

    }

    
    public static <E, C> List<E> readFile(InputStream is, C context, BiFunction<String, C, E> transformer, boolean skipFirstLine) throws IOException{
        
        List<E> elements = new ArrayList<>();

        try (Scanner sc = new Scanner(is, "UTF-8")) {
            if (skipFirstLine && sc.hasNextLine())
                sc.next(); // skip the header
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (!line.equals("")) {
                    E el = transformer.apply(line, context);
                    if(el != null)
                        elements.add(el);
                }
            }

            // note that Scanner suppresses exceptions
            if (sc.ioException() != null) {
                throw sc.ioException();
            }
        }

        return elements;
    }

    /**
     * 
     * @param filePath
     * @return <code>true</code> if file has been succesfully deleted or it doesn't exist
     */
    public static boolean deleteIfExists(String filePath) {
//        File f = new File(filePath);
//        if(f.exists())
//            try {
//                org.apache.commons.io.FileUtils.forceDelete(f);
//                return true;
//            }
//            catch (IOException e) {
//                LOG.warn(String.format("Error deleting file: %s", f.getAbsolutePath()), e);
//                return false;
//            }
//        return true;
        
        FileSystem hdfs = getHadoopFileSystem(filePath);
        if(exists(filePath, hdfs)) {
            return deleteExistingFileInHadoopFS(filePath, hdfs);
        }else
            return true;
        
    }

    
    public static <E, C> List<E> readCsv(String filePath, C context, BiFunction<String, C, E> transformer) throws IOException, URISyntaxException{
        
        return readFile(openStreamFromHadoopFileSystem(filePath), context, transformer, true);

    }

    
    public static InputStream openStreamFromHadoopFileSystem(String filePath) throws IOException {

        Path path = new Path(filePath);
        FileSystem hdfs = getHadoopFileSystem(new Configuration(), getContainerFromAzureUri(filePath));
        InputStream is = hdfs.open(path);
        
        
        
        return is;
    }

    
    private static boolean deleteExistingFileInHadoopFS(String filePath, FileSystem hdfs) {
    
        if(LOG.isDebugEnabled())
            LOG.debug(String.format("HDFS working directory: %s", hdfs.getWorkingDirectory()));
    
        // delete output file if it already exists
        try {
            LOG.info(String.format("Deleting file: %s", filePath));
            hdfs.delete(new Path(filePath), true);
            return true;
        }
        catch (IOException e) {
            LOG.warn(String.format("Error deleting file: %s", filePath), e);
            return false;
        }
    }

    public static boolean exists(String filePath, FileSystem hdfs) {
        try {
            return hdfs.exists(new Path(filePath));
        }
        catch (IllegalArgumentException | IOException e) {
            LOG.error(String.format("Error checking for existence of file: %s", filePath), e);
            return false;
        }
    }
    
    public static boolean exists(String filePath) {
        return exists(filePath, getHadoopFileSystem(filePath));
    }

    
}
