/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2017 the original author or authors.
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/*
 * This class will be responsible for analysing the tracing done at the first stage of the dynamic analysis. 
 * 
 * */
public class DynamicAnalysis {
	
	/*
	 * This method will compute the total number of occurrences of specific classes,
	 * invoked by a the test cases that I have traced.
	 * */ 
	private static Map<String, Integer> countNoOfOccurencesOfClasses() {

		Map<String, Integer> traceClasses = new HashMap<String, Integer>();
		String str;
		String[] output;
		String traceFile = "";
		
		
		try {
			BufferedReader reader;
			for (int i = 0; i <= 4; i++) {
				if (i == 0) {
					traceFile = "/s_home/ms853/Documents/software-reengineering/assignment3/assignment3-ms853/dataFiles/traceObjectArrayAssert_extracting_Test.log";
					//System.out.println("Trace file: " + traceFile);
					reader = new BufferedReader(new FileReader(traceFile)); 
				
					while ((str = reader.readLine()) != null) {
						output = str.split(" ");
						//if(output[0].equals(""))
						if(traceClasses.containsKey(output[0])) {
							traceClasses.put(output[0],  traceClasses.get(output[0]) + 1);
						}else{
							traceClasses.put(output[0], 1);
						}
					
					}
				} else if (i == 1) {
					traceFile = "/s_home/ms853/Documents/software-reengineering/assignment3/assignment3-ms853/dataFiles/traceStandardRepresentation_toStringOf_Test.log";
					//System.out.println("Trace file: " + traceFile);
					reader = new BufferedReader(new FileReader(traceFile));
					
					while ((str = reader.readLine()) != null) {
						output = str.split(" ");
						if(traceClasses.containsKey(output[0])) {
							traceClasses.put(output[0],  traceClasses.get(output[0]) + 1);
						}else{
							traceClasses.put(output[0], 1);
						}
					}
					
				} else if (i == 2) {
					traceFile = "/s_home/ms853/Documents/software-reengineering/assignment3/assignment3-ms853/dataFiles/traceIterableAssert_extracting_Test.log";
					//System.out.println("Trace file: " + traceFile);
					reader = new BufferedReader(new FileReader(traceFile));
					
					while ((str = reader.readLine()) != null) {
						output = str.split(" ");
						if(traceClasses.containsKey(output[0])) {
							traceClasses.put(output[0],  traceClasses.get(output[0]) + 1);
						}else{
							traceClasses.put(output[0], 1);
						}
					}
					
				}else if (i == 3) {
					traceFile = "/s_home/ms853/Documents/software-reengineering/assignment3/assignment3-ms853/dataFiles/traceWithAssertions_delegation_Test.log";
					//System.out.println("Trace file: " + traceFile);
					reader = new BufferedReader(new FileReader(traceFile));
					
					while ((str = reader.readLine()) != null) {
						output = str.split(" ");
						if(traceClasses.containsKey(output[0])) {
							traceClasses.put(output[0], traceClasses.get(output[0]) + 1);
						}else{
							traceClasses.put(output[0], 1);
						}
					}
				}
				else if (i == 4) {
					traceFile = "/s_home/ms853/Documents/software-reengineering/assignment3/assignment3-ms853/dataFiles/traceBDDAssertions_then_Test.log";
					//System.out.println("Trace file: " + traceFile);
					reader = new BufferedReader(new FileReader(traceFile));
					
					while ((str = reader.readLine()) != null) {
						output = str.split(" ");
						//System.out.println("ClassNames: " + output[0]);
						if(traceClasses.containsKey(output[0])) {
							traceClasses.put(output[0],  traceClasses.get(output[0]) + 1);
						}else{
							traceClasses.put(output[0], 1);
						}
					}
				}

			}
			
			for(Entry<String, Integer> cn : traceClasses.entrySet()) {
				System.out.println("className: " + cn.getKey() + " NumOccurance " + cn.getValue());
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			// System.out.println(ex.getMessage());
		}
		
		return traceClasses;
	}
	
	/*
	 * This method will check if the class is always used and will set a boolean flag to true/false 
	 * depending on that condition.
	 * */
	public static void checkIFClassAlwaysUsed() {
		String str = null;
		String traceFile = "";
		String[] output;
		BufferedReader br = null;
		boolean alwaysOccurs = false; //boolean variable to indicate whether a certain class always occurs.
		HashMap<String, Integer> map1 = new HashMap<String, Integer>();
		HashMap<String, Integer> map2 = new HashMap<String, Integer>();
		HashMap<String, Integer> map3 = new HashMap<String, Integer>();
		HashMap<String, Integer> map4 = new HashMap<String, Integer>();
		HashMap<String, Integer> map5 = new HashMap<String, Integer>();
		try{
			//Map will be used for comparison;
			Map<String, Integer> allClassesMap = countNoOfOccurencesOfClasses();
			
			for(int i = 0; i <= 4; i++) {
				if(i == 0) {
					traceFile = "/s_home/ms853/Documents/software-reengineering/assignment3/assignment3-ms853/dataFiles/traceObjectArrayAssert_extracting_Test.log";
					br = new BufferedReader(new FileReader(traceFile));
					while((str = br.readLine()) != null) {
						output = str.split(" ");
						if(map1.containsKey(output[0])) {
							map1.put(output[0], map1.get(output[0]) + 1);
						}else{
							map1.put(output[0], 1);
						}
					}
				}else if(i == 1) {
					traceFile = "/s_home/ms853/Documents/software-reengineering/assignment3/assignment3-ms853/dataFiles/traceStandardRepresentation_toStringOf_Test.log";
					while((str = br.readLine()) != null) {
						output = str.split(" ");
						if(map2.containsKey(output[0])) {
							map2.put(output[0], map2.get(output[0]) + 1);
						}else{
							map2.put(output[0], 1);
						}
					}
				}else if(i == 2) {
					traceFile = "/s_home/ms853/Documents/software-reengineering/assignment3/assignment3-ms853/dataFiles/traceIterableAssert_extracting_Test.log";
					while((str = br.readLine()) != null) {
						output = str.split(" ");
						if(map3.containsKey(output[0])) {
							map3.put(output[0], map3.get(output[0]) + 1);
						}else{
							map3.put(output[0], 1);
						}
					}
				}else if(i == 3) {
					traceFile = "/s_home/ms853/Documents/software-reengineering/assignment3/assignment3-ms853/dataFiles/traceWithAssertions_delegation_Test.log";
					while((str = br.readLine()) != null) {
						output = str.split(" ");
						if(map4.containsKey(output[0])) {
							map4.put(output[0], map4.get(output[0]) + 1);
						}else{
							map4.put(output[0], 1);
						}
					}
				}else if(i == 4) {
					traceFile = "/s_home/ms853/Documents/software-reengineering/assignment3/assignment3-ms853/dataFiles/traceBDDAssertions_then_Test.log";
					while((str = br.readLine()) != null) {
						output = str.split(" ");
						if(map5.containsKey(output[0])) {
							map5.put(output[0], map5.get(output[0]) + 1);
						}else{
							map5.put(output[0], 1);
						}
					}
				}
			}
			
			FileWriter fw = new FileWriter("/s_home/ms853/Documents/software-reengineering/assignment3/assignment3-ms853/dataFiles/classOccurrence.csv", true);
			BufferedWriter bw = new BufferedWriter(fw); // makes sure data is
														// efficiently written
														// to the file.
			PrintWriter pw = new PrintWriter(bw);
			pw.println("Class Name, Total Occurrences, Always Used");
			pw.flush();
			
			for(String key : allClassesMap.keySet()) {
				if(map1.containsKey(key) && map2.containsKey(key) && map3.containsKey(key) && map4.containsKey(key)
						&& map5.containsKey(key)) {
					alwaysOccurs = true;
				}else{
					alwaysOccurs = false;
				}
				String classAlwaysUsed = String.valueOf(alwaysOccurs);
				pw.println(key + ", " + allClassesMap.get(key) + ", " + classAlwaysUsed);
				pw.flush();
				
			}
			pw.close();
			
			
			
		}catch(Exception ex){
			System.out.println(ex.getMessage());
		}
	}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		countNoOfOccurencesOfClasses();
		checkIFClassAlwaysUsed();
	}

}
