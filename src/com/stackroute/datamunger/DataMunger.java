package com.stackroute.datamunger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class DataMunger {

	public void main(String[] args) {
		
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter String: ");
		String queryString = scan.nextLine();
		parseQuery(queryString);
		scan.close();
	}

	/*
	 * we are creating multiple methods, each of them are responsible for extracting
	 * a specific part of the query. However, the problem statement requires us to
	 * print all elements of the parsed queries. Hence, to reduce the complexity, we
	 * are using the parseQuery() method. From inside this method, we are calling
	 * all the methods together, so that we can call this method only from main()
	 * method to print the entire output in console
	 */
	public void parseQuery(String queryString) {
		// call the methods
		getSplitStrings(queryString);
		getFile(queryString);
		getBaseQuery(queryString);
		getConditionsPartQuery(queryString);
		getConditions(queryString);
		getLogicalOperators(queryString);
		getFields(queryString);
		getOrderByFields(queryString);
		getGroupByFields(queryString);
		getAggregateFunctions(queryString);
	}

	/*
	 * this method will split the query string based on space into an array of words
	 * and display it on console
	 */
	public String[] getSplitStrings(String queryString) {
		
		String[] splits = null;
		if(queryString.isEmpty()) {
			 return null;
		}else {
			String string = queryString.toLowerCase();
			splits = string.split(" ");
		}		
		return splits;
	}

	/*
	 * extract the name of the file from the query. File name can be found after a
	 * space after "from" clause. 
	 * Note:
	 * -----
	 * CSV file can contain a field that contains from as a part of the column name. 
	 * For eg: from_date,from_hrs etc.
	 * 
	 * Please consider this while extracting the file name in this method.
	 */
	public String getFile(String queryString) {
		
		String filename=null;
		if(queryString.isEmpty()) {
			return null;
		}else {
			filename = queryString.split("from")[1].split("\\s+")[1].trim();
			}
		
		return filename.toLowerCase();
	}

	/*
	 * This method is used to extract the baseQuery from the query string. BaseQuery
	 * contains from the beginning of the query till the where clause
	 * 
	 * Note:
	 * ------- 
	 * 1. the query might not contain where clause but contain order by or
	 *    group by clause 
	 * 2. the query might not contain where, order by or group by clause 
	 * 3. the query might not contain where, but can contain both group by
	 *    and order by clause
	 */
	public String getBaseQuery(String queryString) {
		
		String base=null;
		if(queryString.isEmpty()) {
			return null;
		}else {
			base = queryString.split("where|group|order")[0];
		}
			
		return base;
	}

	/*
	 * This method is used to extract the conditions part from the query string. The
	 * conditions part contains starting from where keyword till the next keyword,
	 * which is either group by or order by clause. In case of absence of both group
	 * by and order by clause, it will contain till the end of the query string.
	 * Note: 
	 * ----- 
	 * 1. The field name or value in the condition can contain keywords
	 * as a substring. 
	 * For eg: from_city,job_order_no,group_no etc. 
	 * 2. The query might not contain where clause at all.
	 */
	public String getConditionsPartQuery(String queryString) {
		
		String partquery = "";
		if(queryString!=null) {
			if(queryString.contains("where")) {
			partquery = queryString.split("where")[1].split("group by|order by")[0].toLowerCase();
			}else {
				return null;
			}
		}else {
			return null;
		}
		
		return partquery;
	}

	/*
	 * This method will extract condition(s) from the query string. The query can
	 * contain one or multiple conditions. In case of multiple conditions, the
	 * conditions will be separated by AND/OR keywords. 
	 * for eg: 
	 * Input: select city,winner,player_match from ipl.csv where season > 2014 and city
	 * ='Bangalore'
	 * 
	 * This method will return a string array ["season > 2014","city ='Bangalore'"]
	 * and print the array
	 * 
	 * Note: 
	 * ----- 
	 * 1. The field name or value in the condition can contain keywords
	 * as a substring. 
	 * For eg: from_city,job_order_no,group_no etc. 
	 * 2. The query might not contain where clause at all.
	 */
	public String[] getConditions(String queryString) {

		
		String[] conditions = null;
		if(queryString==null) {
			return null;
		}else { 
			String s = queryString.toLowerCase();
			if(s.contains("where")) {
			conditions = s.split("where")[1].split("group by|order by")[0].split("and|or ");
			for(int i=0;i<conditions.length;i++) {
				conditions[i]=conditions[i].trim();
			}
			}else {
				return null;
			}
		}
		
		return conditions;
	}

	/*
	 * This method will extract logical operators(AND/OR) from the query string. The
	 * extracted logical operators will be stored in a String array which will be
	 * returned by the method and the same will be printed 
	 * Note: 
	 * ------- 
	 * 1. AND/OR keyword will exist in the query only if where conditions exists and it
	 * contains multiple conditions. 
	 * 2. AND/OR can exist as a substring in the conditions as well. 
	 * For eg: name='Alexander',color='Red' etc. 
	 * Please consider these as well when extracting the logical operators.
	 * 
	 */
	public String[] getLogicalOperators(String queryString) {
		
		String Cons = getConditionsPartQuery(queryString);
		String[] logical = null;
		if(Cons!=null) {
			Cons = Cons.trim();
			String[] c = Cons.split(" ");
			ArrayList<String> list = new ArrayList<String>();
			for (int i=0;i<c.length;i++) {
				if(c[i].equals("and") || c[i].equals("or")){
					list.add(c[i]);
				}
			}
			logical = list.toArray(new String[list.size()]);
		}else {
			return null;
		}		
		
		return logical;

	}

	/*
	 * This method will extract the fields to be selected from the query string. The
	 * query string can have multiple fields separated by comma. The extracted
	 * fields will be stored in a String array which is to be printed in console as
	 * well as to be returned by the method
	 * 
	 * Note: 
	 * ------ 
	 * 1. The field name or value in the condition can contain keywords
	 * as a substring. 
	 * For eg: from_city,job_order_no,group_no etc. 
	 * 2. The field name can contain '*'
	 * 
	 */
	public String[] getFields(String queryString) {
		
		String[] fields = null;
		if(queryString.isEmpty()) {
			 return null;
		}else {
			fields = queryString.split("select")[1].split("from")[0].trim().split(",");
		}		
		return fields;
	}

	/*
	 * This method extracts the order by fields from the query string. 
	 * Note: 
	 * ------
	 * 1. The query string can contain more than one order by fields. 
	 * 2. The query string might not contain order by clause at all. 
	 * 3. The field names,condition values might contain "order" as a substring. 
	 * For eg:order_number,job_order 
	 * Consider this while extracting the order by fields
	 */
	public String[] getOrderByFields(String queryString) {
		
		String[] orderby = null;
		if(queryString.isEmpty() || queryString.contains("group by")) {
			return null;
		}else {
			orderby = queryString.split("order by")[1].trim().split(",");
			}
		
		return orderby;
	}

	/*
	 * This method extracts the group by fields from the query string. 
	 * Note: 
	 * ------
	 * 1. The query string can contain more than one group by fields. 
	 * 2. The query string might not contain group by clause at all. 
	 * 3. The field names,condition values might contain "group" as a substring. 
	 * For eg: newsgroup_name
	 * 
	 * Consider this while extracting the group by fields
	 */
	public String[] getGroupByFields(String queryString) {
		
		String[] groupby = null;
		if(queryString.isEmpty() || queryString.contains("order by")) {
			groupby= null;
			
		}else {
			groupby = queryString.split("group by")[1].trim().split(",");
		}
		
		return groupby;
	}

	/*
	 * This method extracts the aggregate functions from the query string. 
	 * Note:
	 * ------ 
	 * 1. aggregate functions will start with "sum"/"count"/"min"/"max"/"avg"
	 * followed by "(" 
	 * 2. The field names might contain"sum"/"count"/"min"/"max"/"avg" as a substring.
	 * For eg: account_number,consumed_qty,nominee_name
	 * 
	 * Consider this while extracting the aggregate functions
	 */
	public String[] getAggregateFunctions(String queryString) {
		
		String[] aggregate = null;
		if(queryString.isEmpty()) {
			return null;
		}else {
			String field = queryString.substring(queryString.indexOf("select"), queryString.indexOf("from")).replace("select ", "").trim();
			if(field.contains("*")){
				return null;
			}else {
				aggregate = field.split(",");
			}
		}
		return aggregate;
	}

}