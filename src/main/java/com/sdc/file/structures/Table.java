/**
 * Table.java
 */
package com.sdc.file.structures;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.postgis.PGgeometry;

import com.csvreader.CsvReader;
import com.sdc.file.exception.TableException;
import com.sdc.file.exception.TextFormatException;
import com.sdc.file.utils.FileUtils;


/**
 * @author Simone De Cristofaro
 * @created 10/ott/2012
 */
public class Table implements Iterable<Object[]>{
	private static String NEWLINE=System.getProperty("line.separator");
	private static final SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	//ATTRIBUTES
	private String name;
	private int nRows;
	private int nRowsIncludeComment;
	private int nFields;
	private List<Object[]> content;
	private String fields[];
	private HashMap<String, Integer> coll_fields;
	/**
	 * key: progressive index; value:index in arraylist "content"
	 */
	private HashMap<Integer,Integer> coll_nonCommentedRows;
	/**
	 * key: index in arraylist "content"; value:progressive index
	 */
	private HashMap<Integer,Integer> coll_commentedRows;
	private char commentIdentifier;
	
	//CONSTRUCTORS
	public Table() {coll_fields=null;coll_commentedRows=null;coll_nonCommentedRows=null;commentIdentifier='#';}
	
	/**
	 * 
	 * @param name
	 * @param fields
	 * @throws TableException
	 */
	public Table(String name,String fields[]) throws TableException {
		this(name,fields,10,'#');
	}

	public Table(String name,String fields[], ArrayList<Object[]> content,char commentIdentifier) throws TableException {
		super();
		createFromList(name, fields, content, commentIdentifier);
	}

	public Table(String name,String fields[], int initialCapacity,char comentIdentifier) throws TableException {
		super();
		this.name = name;
		this.fields=fields;
		this.nFields=this.fields.length;
		this.commentIdentifier=comentIdentifier;
		
		coll_fields=new HashMap<String, Integer>();
		for(int i=0;i<nFields;i++)
			coll_fields.put(fields[i], i);
		
		this.content=new ArrayList<Object[]>(initialCapacity);
	}

	public Table(String name,String fields[], Object[][] content,char commentIdentifier) throws TableException {
		super();
		
		if(content==null) throw new TableException("Empty table content!");
		
		ArrayList<Object[]> tmp_content=new ArrayList<Object[]>(content.length);
		for(int i=0;i<content.length;i++)
			tmp_content.add(content[i]);
		
		createFromList(name, fields, tmp_content, commentIdentifier);
		
	}
	
	/**
	 * Get a Table from a {@link com.csvreader.CsvReader CsvReader}
	 * @param name
	 * @param csvReader
	 * @param getComments if <code>true</code> put in the table also the commented rows
	 * @throws TableException
	 * @throws IOException
	 */
	public Table(String name,CsvReader csvReader,boolean getComments) throws TableException, IOException {
		

		this.name = name;

		String regex="("+ csvReader.getDelimiter() + "+)$";

		if(!getComments)
			csvReader.setUseComments(true);
		this.commentIdentifier=csvReader.getComment();
		
		csvReader.readHeaders();
		this.nFields=csvReader.getHeaderCount();
		this.fields=csvReader.getHeaders();
		
		int i=1;
		this.content=new ArrayList<Object[]>();
		Object[] tmp_rowcontent=null;		
		
		while(csvReader.readRecord()) {
			
			if(csvReader.getRawRecord().startsWith(""+commentIdentifier)) {//Commented row
				this.content.add(new Object[]{csvReader.getRawRecord().replaceAll(regex, "")});			
			}else {
				if (csvReader.getColumnCount()>nFields)
					throw new TableException("Wrong number of fields at row: " + i);
				tmp_rowcontent=new Object[nFields];
				for(int j=0;j<nFields;j++)
					tmp_rowcontent[j]=csvReader.get(csvReader.getHeader(j));

				this.content.add(tmp_rowcontent);			
			}			
			i++;
		}
		
		finalizeConstructor();
		
	}
	
	//METHODS
	public boolean isEmpty() {
	    return nRows == 0;
	}
	
	private void handleCommentedRows() throws TableException{
		//get non commented rows
		Object[] tmp_row=null;
		int i=0;
		
		int nonCommentRowCounter=0;
		int commentRowCounter=0;
		coll_nonCommentedRows=new HashMap<Integer, Integer>(content.size());
		coll_commentedRows=new HashMap<Integer, Integer>();
		
		Iterator<Object[]> it=content.iterator();
		while(it.hasNext()) {
			tmp_row=it.next();
			if(tmp_row.length>0) {
				if(tmp_row[0]  instanceof String && ((String)tmp_row[0]).startsWith(""+commentIdentifier)  ) {
					coll_commentedRows.put(i, commentRowCounter);
					commentRowCounter++;
				}else {
					coll_nonCommentedRows.put(nonCommentRowCounter, i);
					nonCommentRowCounter++;					
				}
			}else
				throw new TableException("Empty record as row: " + 0);
			
			i++;
		}
		
		if(content!=null) {
			this.nRows=nonCommentRowCounter;
			this.nRowsIncludeComment=nonCommentRowCounter+commentRowCounter;
		}
	}
	
	private void finalizeConstructor() throws TableException{
		
		handleCommentedRows();
		
		coll_fields=new HashMap<String, Integer>();
		for(int j=0;j<nFields;j++)
			coll_fields.put(fields[j], j);

	}
	
	private void createFromList(String name,String fields[], List<Object[]> content,char commentIdentifier) throws TableException {
		this.name = name;
		this.content = content;
		this.fields=fields;
		this.commentIdentifier=commentIdentifier;
		this.nFields=this.fields.length;

		finalizeConstructor();

	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param includeComment if <code>true</code> consider also the commented rows
	 * @return the nRecord
	 */
	public int getnRows(boolean includeComment) {
		if(includeComment) return nRowsIncludeComment;
		return nRows;
	}

	/**
	 * @return the number of record not including the commented rws
	 */
	public int getnRows() {
		return getnRows(false);
	}
	
	public int size() {
	    return nRows;
	}
	
	/**
	 * @return the nField
	 */
	public int getnFields() {
		return nFields;
	}

	/**
	 * @param includeComment if <code>true</code> include the commented rows
	 * @return the content
	 */
	public Object[][] getContent(boolean includeComment) {
		Object[][] toReturn=null;
		if(includeComment) {
			toReturn=new Object[nRowsIncludeComment][nFields];
			for(int i=0;i<toReturn.length;i++)
				toReturn[i]=content.get(i);
		}else {
			int counter=0;
			toReturn=new Object[nRows][nFields];
			for(int i=0;i<nRows;i++)
				if(!coll_commentedRows.containsKey(i)) {
					toReturn[counter]=content.get(i);								
					counter++;
				}
		}
		
		return toReturn;
	}

	/**
	 * @return the content
	 */
	public Object[][] getContent() {
		return getContent(false);
	}
	
	/**
	 * @return the field
	 */
	public String[] getFields() {
		return fields;
	}

	/**
	 * @param fieldsName Name of the field
	 * @param row Index of the row (starting from 0)
	 * @return the value of the specified field at the specified row
	 */
	public Object get(String fieldsName,int row) {
		if(row<0 || row>=nRows || !coll_fields.containsKey(fieldsName)) return null;
		return content.get(coll_nonCommentedRows.get(row))[coll_fields.get(fieldsName)];
	}
	
	public int getColumnIndex(String columnName) {
	    return coll_fields.get(columnName);
	}
	
	/**
	 * @param fieldsIndex Index of the field (starting from 0)
	 * @param row Index of the row (starting from 0)
	 * @return the value of the specified field at the specified row
	 */
	public Object get(int fieldsIndex,int row) {
		if(row<0 || row>=nRows || fieldsIndex<0 || fieldsIndex>nFields ) return null;
		return content.get(coll_nonCommentedRows.get(row))[fieldsIndex];
	}
	
	/**
	 * Add a row to the table
	 * @param row
	 * @throws TableException
	 */
	public void addRow(Object[] row) throws TableException {
		if(row==null) throw new TableException("Bad input row");
		if(! (row[0] instanceof String && ((String)row[0]).startsWith(""+commentIdentifier))  ) {//non commented row
			coll_nonCommentedRows.put(nRows, nRowsIncludeComment);
			nRows++;
			if(row.length!=nFields) throw new TableException("Wrong number of fields");
		}else {
			coll_commentedRows.put(nRowsIncludeComment, nRowsIncludeComment-nRows);
		}
		content.add(row);
		nRowsIncludeComment++;
	}
	
	/**
	 * Remove the specied (non commented) row
	 * @param i Index of the row in the table
	 * @throws TableException
	 */
	public void removeRow(int i) throws TableException {
		if(i<0 || i >nRows) throw new TableException("Wrong record index");
		content.remove(coll_nonCommentedRows.get(i));
		
		handleCommentedRows();
	}
	
	/**
	 * Remove all the element from the table
	 */
	public void clear() {
		nRows=0;
		nRowsIncludeComment=0;
		content.clear();
		coll_commentedRows.clear();
		coll_nonCommentedRows.clear();
	}
	
	/**
	 * Clear the table and set the content
	 * @param data The new content of the table
	 * @throws TableException 
	 */
	public void setContent(Object[][] data) throws TableException {
		if(data==null) throw new TableException("Bad input (empty data)!");
		
		ArrayList<Object[]> tmp_content= new ArrayList<Object[]>(data.length);
		
		for(int i=0;i<data.length;i++)
			tmp_content.add(data[i]);
		
		createFromList(name, fields, content, commentIdentifier);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj==null || !obj.getClass().equals(Table.class)) return false;
		
		Table table=(Table)obj;
		
		if(!name.equals(table.getName()) || nRowsIncludeComment!= table.getnRows(true) || nFields!=table.getnFields())
			return false;
		
		Object tableContent[][]=table.getContent(true);
		
		for(int i=0;i<nRowsIncludeComment;i++)
			for(int j=0;j<content.get(i).length;j++)
				if(!content.get(i)[j].equals(tableContent[i][j])) return false;
		
		return true;
	}

	/**
	 * Remove the field separetors presents in the commented row
	 * @param fieldSep
	 */
	public void depurateComment(char fieldSep) {
		String regex="("+ fieldSep + "+)$";

		Iterator<Entry<Integer, Integer>> it = coll_commentedRows.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<Integer, Integer> pairs = (Map.Entry<Integer, Integer>)it.next();
	        
	        content.set(pairs.getKey(),new Object[] {((String)(content.get(pairs.getKey())[0])).replaceAll(regex, "")});
	        
	        it.remove(); // avoids a ConcurrentModificationException
	    }		
	}
	
	/**
	 * Get the {@link Iterator} to loop over the data
	 * @return {@link Iterator}
	 */
	public Iterator<Object[]> iterator(){
		return content.iterator();
	}
	
	//******************************************************************************************************************************
	//TOSTRING
	//******************************************************************************************************************************


	public String toString(String tablePrefix,String tableSuffux,char fieldSeparator,char textQualifier,boolean compact) {
		if(compact)
			return toStringCompact(tablePrefix, tableSuffux, fieldSeparator, textQualifier);
		else
			return toStringExpanded(tablePrefix, tableSuffux, fieldSeparator, textQualifier);
	}
	
	public String toString(String tablePrefix,String tableSuffux,char fieldSeparator,char textQualifier) {
		return toStringExpanded(tablePrefix, tableSuffux, fieldSeparator, textQualifier);
	}
	
	private String toStringCompact(String tablePrefix,String tableSuffux,char fieldSeparator,char textQualifier) {
		String value=null;
		String aux_value=null;		
		StringBuilder sb=new StringBuilder(tablePrefix+ name + tableSuffux +NEWLINE);
		
		for(int j=0;j<fields.length;j++) {
			sb.append(fields[j]);
			sb.append(fieldSeparator);
		}
			
		sb.delete(sb.length()-1, sb.length());
		sb.append(NEWLINE);
		
		for(int i=0;i<nRowsIncludeComment;i++) {
			if(!coll_commentedRows.containsKey(i)) {
				for(int j=0;j<nFields;j++) {
					if(content.get(i)[j] instanceof Calendar)
						value=sdf.format(((Calendar) content.get(i)[j]).getTime() );
					else if (content.get(i)[j] instanceof Date)
						value=sdf.format(((Date) content.get(i)[j]));
					else if(content.get(i)[j]  instanceof String) {
						aux_value=(String) content.get(i)[j];
						if(aux_value.contains(""+fieldSeparator)) {
							aux_value=textQualifier+aux_value+textQualifier;
						}
						value=aux_value;					
					}else
						value=content.get(i)[j].toString();

					sb.append(value);
					sb.append(fieldSeparator);
				}
				sb.delete(sb.length()-1, sb.length());
			}else {
				sb.append(content.get(i)[0]);
			}
			sb.append(NEWLINE);				
		}

		return sb.toString();
	}

	private String toStringExpanded(String tablePrefix,String tableSuffux,char fieldSeparator,char textQualifier) {
		int[] maxLengthOfFields=new int[nFields];
		String value=null;
		String aux_value=null;
		
		for(int j=0;j<nFields;j++)
			if(fields[j].length()>maxLengthOfFields[j]) maxLengthOfFields[j]=fields[j].length();
		
		for(int i=0;i<nRowsIncludeComment;i++)
			if(!coll_commentedRows.containsKey(i)) {
				for(int j=0;j<nFields;j++) {
					if(content.get(i)[j] instanceof Calendar)
						value=sdf.format(((Calendar) content.get(i)[j]).getTime() );
					else if (content.get(i)[j] instanceof Date)
						value=sdf.format(((Date) content.get(i)[j]));
					else if(content.get(i)[j]  instanceof String) {
						aux_value=(String) content.get(i)[j];
						if(aux_value.contains(""+fieldSeparator)) {
							aux_value=textQualifier+aux_value+textQualifier;
						}
						value=aux_value;					
					}else
						value=content.get(i)[j].toString();
						
					if(value.length()>maxLengthOfFields[j]) maxLengthOfFields[j]=value.length();
				}				
			}
			
		
		StringBuilder sb=new StringBuilder(tablePrefix+ name + tableSuffux +NEWLINE);
		
		for(int j=0;j<fields.length;j++) {
			sb.append(fields[j]);
			for(int k=0;k<maxLengthOfFields[j]-fields[j].length();k++)
				sb.append(" ");
			sb.append(fieldSeparator);
		}
			
		sb.delete(sb.length()-1, sb.length());
		sb.append(NEWLINE);
		
		for(int i=0;i<nRowsIncludeComment;i++) {
			if(!coll_commentedRows.containsKey(i)) {
				for(int j=0;j<nFields;j++) {
					if(content.get(i)[j] instanceof Calendar)
						value=sdf.format(((Calendar) content.get(i)[j]).getTime() );
					else if (content.get(i)[j] instanceof Date)
						value=sdf.format(((Date) content.get(i)[j]));
					else if(content.get(i)[j]  instanceof String) {
						aux_value=(String) content.get(i)[j];
						if(aux_value.contains(""+fieldSeparator)) {
							aux_value=textQualifier+aux_value+textQualifier;
						}
						value=aux_value;					
					}else
						value=content.get(i)[j].toString();

					sb.append(value);
					if(j<nFields-1)
						for(int k=0;k<maxLengthOfFields[j]-value.length();k++)
							sb.append(" ");
					sb.append(fieldSeparator);
				}
				sb.delete(sb.length()-1, sb.length());
			}else {
				sb.append(content.get(i)[0]);
			}
			sb.append(NEWLINE);				
		}

		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.toStringExpanded("","",',','"');
	}	
	
	//******************************************************************************************************************************
	//******************************************************************************************************************************
	//******************************************************************************************************************************

	/**
	 * @return a string that represent an insert query of the entire table
	 */
	public String getInsertQuery() {
		if(content.size()==0) return "";
		String value=null;
		StringBuilder sb=new StringBuilder("INSERT INTO " + name +" (");
		for(int i=0;i<nFields;i++)
			sb.append(fields[i]+", ");
		sb.delete(sb.length()-2, sb.length());
		sb.append(")\n VALUES \n(");
		for(int i=0;i<nRowsIncludeComment;i++) {
			if(!coll_commentedRows.containsKey(i)) {
				for(int j=0;j<nFields;j++) {
					if(content.get(i)[j] instanceof String)
						value="'"+content.get(i)[j]+"'";
					else if (content.get(i)[j] instanceof PGgeometry)
						value="ST_GeomFromText('"+ ((PGgeometry) (content.get(i)[j])).getGeometry().getTypeString() +((PGgeometry) (content.get(i)[j])).getGeometry().getValue() +"', "+ ((PGgeometry) (content.get(i)[j])).getGeometry().getSrid() +")";
					else if(content.get(i)[j] instanceof Calendar)
						value="'"+sdf.format(((Calendar) content.get(i)[j]).getTime() )+"'";
					else if (content.get(i)[j] instanceof Date)
						value="'"+sdf.format(((Date) content.get(i)[j]))+"'";
					else
						value=content.get(i)[j].toString();
					
					sb.append(value+", ");
				}
				sb.delete(sb.length()-2, sb.length());
				sb.append("),\n(");				
			}
		}
		sb.delete(sb.length()-3, sb.length());
		sb.append(";");
		return sb.toString();
	}
	
	public Object[] getRandomRecord() {
		return content.get(com.sdc.file.support.Support.getRandomInt(0, nRows-1));
	}

	/**
	 * @param fieldsIndex
	 * @return a random element of the specified field
	 */
	public Object getRandomElement(int fieldsIndex) {
		if(fieldsIndex<0 || fieldsIndex>nFields ) return null;
		return content.get(com.sdc.file.support.Support.getRandomInt(0, nRows-1))[fieldsIndex];
	}

	/**
	 * @param fieldsName
	 * @return a random element of the specified field
	 */
	public Object getRandomElement(String fieldsName) {
		if(!coll_fields.containsKey(fieldsName)) return null;
		return content.get(com.sdc.file.support.Support.getRandomInt(0, nRows-1))[coll_fields.get(fieldsName)];
	}

	//STATIC METHOD
	/**
	 * Read the specified file and return the specified table
	 * @param filePath File absolute path
	 * @param tableName Name of the table returned
	 * @param tablePrefix Table prefix (e.g.: '$')
	 * @param tableSuffix Table prefix (e.g.: ':')
	 * @param fieldSeparator Field separator (the default value is: ',')
	 * @param commentIdentifier Comment identifier (the default value is: '#')
	 * @param textQualifier Text qualifier (the default value is: '"')
	 * @param fieldsInSameStringOfName If true the name of  the fields of the table are in the same string
	 * of the table name, if false the fields name are in the following string
	 * @return The table requested
	 * @throws TableException 
	 * @throws IOException 
	 */
	public static Table getTableInFile(String filePath,String tableName,String tablePrefix,String tableSuffix,
			char fieldSeparator,char commentIdentifier,char textQualifier, boolean fieldsInSameStringOfName) throws TableException, IOException{
							
		if(tableName==null || tableName.equals(""))
			throw new TableException("Bad input format. Check the name of the table");
		
		InputStreamReader fr=null;
		BufferedReader reader=null;
		
		if (tablePrefix==null) tablePrefix="";
		if (tableSuffix==null) tableSuffix="";
		if (fieldSeparator==Character.MIN_VALUE) fieldSeparator=',';
		if (commentIdentifier==Character.MIN_VALUE) commentIdentifier='#';
		if (textQualifier==Character.MIN_VALUE) textQualifier='"';

		String tmp_tableName=tablePrefix + tableName + tableSuffix;
		StringBuilder sb=new StringBuilder();
		
		try{
		    
		    String encoding = FileUtils.getFileEncoding(filePath);
            if (encoding != null)
                fr = new InputStreamReader(new FileInputStream(filePath), encoding);
            else
                fr = new InputStreamReader(new FileInputStream(filePath));
			reader= new BufferedReader(fr);
			String text_line=reader.readLine();
			
			//find the table
			while(text_line!=null && !text_line.startsWith(tmp_tableName)) {
				text_line=reader.readLine();
			}
			
			if(text_line==null)
				return new Table(); // empty table
			
			if(fieldsInSameStringOfName) {
				text_line=text_line.substring(tmp_tableName.length());
				if(text_line==null || text_line.equals(""))
					throw new TableException("Fields of table not found");
			}else {
				text_line=reader.readLine();
				while(text_line.startsWith(""+commentIdentifier)) {
					text_line=reader.readLine();
				}
				if(text_line==null || text_line.equals(""))
					throw new TableException("Fields of table not found");
			}
			sb.append(text_line).append(NEWLINE);
						
			text_line=reader.readLine();
			while(text_line!=null && !text_line.equals("")) {				
				sb.append(text_line).append(NEWLINE);
				text_line=reader.readLine();
			}
			
			if(sb.length()==0)
				throw new TableException("Empty table: " + tableName);
						
			return Table.parse(sb.toString(), tableName,fieldSeparator,commentIdentifier,textQualifier);
			
		}catch(Exception e){
			throw new TableException(e.getMessage(),e);
		}finally{
			try{
				if(fr!=null)
					fr.close();
				if(reader!=null)
					reader.close();
			}catch(IOException e){
				System.out.println("Errore in chiusura");
			}
		}
			
	}
	
	

	/**
	 * Get a {@link Table Table} from the String (that must not include table name)
	 * @param s
	 * @param tableName Name of the table to get
	 * @param fieldSeparator
	 * @param commentIdentifier
	 * @param textQualifier
	 * @return a {@link Table Table} from the String (that must not include table name)
	 * @throws TableException
	 * @throws IOException
	 */
	public static Table parse(String s,String tableName ,char fieldSeparator,char commentIdentifier,char textQualifier) throws TableException, IOException {
		CsvReader csvReader=CsvReader.parse(s);
		
		//check table validity
		csvReader.setDelimiter(fieldSeparator);
		csvReader.setComment(commentIdentifier);
		csvReader.setTextQualifier(textQualifier);
		csvReader.setUseTextQualifier(true);			
		
		return new Table(tableName,csvReader,true);

	}

	
	/**
	 * @param s
	 * @param tableName Name of the table to get
	 * @return a {@link Table Table} from the String (that must not include table name)
	 * @throws TableException
	 * @throws IOException
	 */
	public static Table parse(String s,String tableName) throws TableException, IOException {
		CsvReader csvReader=CsvReader.parse(s);
		
		//check table validity
		csvReader.setDelimiter(',');
		csvReader.setComment('#');
		csvReader.setTextQualifier('"');
		csvReader.setUseTextQualifier(true);			
		
		return new Table(tableName,csvReader,true);

	}
	
	
	
	/**
	 * Format the tables contained in a text field
	 * @param filePath File containing the tables. Available file extention: csv, txt, net
	 * @param tablePrefix 
	 * @param tableSuffix
	 * @param fieldSep
	 * @param commentIdentifier
	 * @param textQualifier
	 * @param compact If <code>true</code> remove the redoundant whitespace
	 * @param makeNewFile true of create a new file, otherwise overwrite the input file
	 * @param fieldsInSameRowOfName
	 * @throws TextFormatException
	 * @throws IOException
	 * @throws TableException 
	 */
	public static void FormatTablesInTxtFile(String filePath,String tablePrefix,String tableSuffix,char fieldSep,char commentIdentifier,char textQualifier,boolean compact ,boolean makeNewFile, boolean fieldsInSameRowOfName) throws TextFormatException, IOException, TableException {
		
		int i=0;
		StringBuilder sb=new StringBuilder();
		String text_line=null;
		String tableName=null;
		String formattedTable=null;
		int nRow=0;
		int nFields=0;
		int nEl=0;
		String contentToWrite=null;
		StringBuilder sb_table=null;
		
		String regex="("+ fieldSep + "+)$";
		Pattern p = Pattern.compile(regex);
		
		InputStreamReader f=null;
		BufferedReader reader=null;

		try{
            String encoding = FileUtils.getFileEncoding(filePath);
            if(encoding != null)
                f = new InputStreamReader(new FileInputStream(filePath), encoding);
            else
                f = new InputStreamReader(new FileInputStream(filePath));
                
			reader=new BufferedReader(f);

			text_line=reader.readLine();
			while(text_line!=null) {
				
				if (text_line.startsWith(tablePrefix)) {
					//check if there is a table
					tableName=(text_line.replace(tablePrefix, "")).replaceAll(regex, "");
										
					if(tableName.contains(tableSuffix)) {
						//table found						
						sb_table=new StringBuilder("");
						
						if(fieldsInSameRowOfName) {
							tableName=tableName.split(tableSuffix)[0];
							text_line=text_line.substring((tablePrefix+tableName+tableSuffix).length());
							if(text_line==null || text_line.equals(""))
								throw new TableException("Fields of table not found");
						}else {
							text_line=reader.readLine();
							while(text_line.startsWith(""+commentIdentifier)) {
								text_line=reader.readLine();
							}
							if(text_line==null || text_line.equals(""))
								throw new TableException("Fields of table not found");
							tableName=tableName.replace(tableSuffix, "");
						}
						nFields=text_line.split(""+fieldSep).length;

						sb_table.append(text_line).append(NEWLINE);

						text_line=reader.readLine();
						
						nRow=0;
						while(text_line!=null && !text_line.equals("")) {
							if(text_line.matches("^"+regex)) {
								nEl=text_line.split(""+fieldSep,-1).length;
								if(nEl<nFields)
									break;
								for(i=nEl-1;i>=nFields;i--) {
									text_line=text_line.substring(0, text_line.length()-1);
								}
							}else if(p.matcher(text_line).find()){
								nEl=text_line.split(""+fieldSep,-1).length;
								for(i=nEl-1;i>=nFields;i--) {
									text_line=text_line.substring(0, text_line.length()-1);
								}
							}
							sb_table.append(text_line).append(NEWLINE);

							text_line=reader.readLine();
							nRow++;
						}
						//check the table
						if(nRow>0) {//the table is not empty
							//format table
							formattedTable=Table.parse(sb_table.toString(),tableName, fieldSep, commentIdentifier, textQualifier).toString(tablePrefix,tableSuffix,fieldSep,textQualifier,compact);
							sb.append(formattedTable);
						}
					}	
				}
				
				if(text_line!=null) {
					sb.append(text_line.replaceAll(regex, "")).append(NEWLINE);
					text_line=reader.readLine();
				}
				
			}
			
			if(sb.length()>1) sb.delete(sb.length()-1, sb.length());
			
			contentToWrite= sb.toString();
			
			//write the output
			if(makeNewFile) {
				//create a new file
				String now=new SimpleDateFormat("yyyyMMddHHmm").format(Calendar.getInstance().getTime());
				String ext=FileUtils.getExtention(filePath);
				FileUtils.writeFile(filePath.substring(0,filePath.length()-4) + "_" + now + "." + ext, contentToWrite, false);
			}else {
				//overwrite the file
				FileUtils.writeFile(filePath, contentToWrite, false);
			}
			
			
		} catch(Exception e){
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
		
		
	}

	
}
