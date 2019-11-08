import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ParseTraceToCSV {

	public static void parseToCSV() {
		
		BufferedReader br;
		String[] output = null;
		String tempStr = "";
		String traceFile = "";
		FileWriter fw1, fw2, fw3, fw4, fw5; 
		PrintWriter pw;
		
		//Hashmap for each trace file. 
		Map<String, Integer> traceMap1 = new HashMap<String, Integer>();
		Map<String, Integer> traceMap2 = new HashMap<String, Integer>();
		Map<String, Integer> traceMap3 = new HashMap<String, Integer>();
		Map<String, Integer> traceMap4 = new HashMap<String, Integer>();
		Map<String, Integer> traceMap5 = new HashMap<String, Integer>();
		
		try{
			for(int i = 0; i <= 4; i++) {
				if(i == 0) {
					traceFile = "/s_home/ms853/Documents/software-reengineering/assignment3/assignment3-ms853/dataFiles/traceObjectArrayAssert_extracting_Test.log";
					br = new BufferedReader(new FileReader(traceFile));
					while((tempStr = br.readLine()) != null) {
						output = tempStr.split(" ");
						if(traceMap1.containsKey(output[0])) {
							traceMap1.put(output[0], traceMap1.get(output[0]) + 1);
						}else{
							traceMap1.put(output[0], 1);
						}
					}
					//write to file 
					fw1 = new FileWriter("/s_home/ms853/Documents/software-reengineering/assignment3/assignment3-ms853/dataFiles/ObjectArrayAssert_extracting_Test.csv");
					pw = new PrintWriter(fw1);
					pw.println("Class Name, Number of Occurrences");
					for(Entry<String, Integer> e : traceMap1.entrySet()) {
						pw.println(e.getKey() + "," + e.getValue());
						pw.flush();
					}
					pw.close();
					
				}else if(i == 1){
					traceFile = "/s_home/ms853/Documents/software-reengineering/assignment3/assignment3-ms853/dataFiles/traceStandardRepresentation_toStringOf_Test.log";
					br = new BufferedReader(new FileReader(traceFile));
					while((tempStr = br.readLine()) != null) {
						output = tempStr.split(" ");
						if(traceMap2.containsKey(output[0])) {
							traceMap2.put(output[0], traceMap2.get(output[0]) + 1);
						}else{
							traceMap2.put(output[0], 1);
						}
					}
					fw2 = new FileWriter("/s_home/ms853/Documents/software-reengineering/assignment3/assignment3-ms853/dataFiles/StandardRepresentation_toStringOf_Test.csv");
					pw = new PrintWriter(fw2);
					pw.println("Class Name, Number of Occurrences");
					for(Entry<String, Integer> e : traceMap2.entrySet()) {
						pw.println(e.getKey() + "," + e.getValue());
						pw.flush();
					}
					pw.close();
					
				}else if(i == 2) {
					traceFile = "/s_home/ms853/Documents/software-reengineering/assignment3/assignment3-ms853/dataFiles/traceIterableAssert_extracting_Test.log";
					
					br = new BufferedReader(new FileReader(traceFile));
					while((tempStr = br.readLine()) != null) {
						output = tempStr.split(" ");
						if(traceMap3.containsKey(output[0])) {
							traceMap3.put(output[0], traceMap3.get(output[0]) + 1);
						}else{
							traceMap3.put(output[0], 1);
						}
					}
					fw3 = new FileWriter("/s_home/ms853/Documents/software-reengineering/assignment3/assignment3-ms853/dataFiles/IterableAssert_extracting_Test.csv");
					pw = new PrintWriter(fw3);
					pw.println("Class Name, Number of Occurrences");
					for(Entry<String, Integer> e : traceMap3.entrySet()) {
						pw.println(e.getKey() + "," + e.getValue());
						pw.flush();
					}
					pw.close();
					
				}else if(i == 3) {
					traceFile = "/s_home/ms853/Documents/software-reengineering/assignment3/assignment3-ms853/dataFiles/traceWithAssertions_delegation_Test.log";
					br = new BufferedReader(new FileReader(traceFile));
					while((tempStr = br.readLine()) != null) {
						output = tempStr.split(" ");
						if(traceMap4.containsKey(output[0])) {
							traceMap4.put(output[0], traceMap4.get(output[0]) + 1);
						}else{
							traceMap4.put(output[0], 1);
						}
					}
					
					fw4 = new FileWriter("/s_home/ms853/Documents/software-reengineering/assignment3/assignment3-ms853/dataFiles/WithAssertions_delegation_Test.csv");
					pw = new PrintWriter(fw4);
					pw.println("Class Name, Number of Occurrences");
					for(Entry<String, Integer> e : traceMap4.entrySet()) {
						pw.println(e.getKey() + "," + e.getValue());
						pw.flush();
					}
					pw.close();
					
				}
				else if(i == 4) {
					traceFile = "/s_home/ms853/Documents/software-reengineering/assignment3/assignment3-ms853/dataFiles/traceBDDAssertions_then_Test.log";
					br = new BufferedReader(new FileReader(traceFile));
					while((tempStr = br.readLine()) != null) {
						output = tempStr.split(" ");
						if(traceMap5.containsKey(output[0])) {
							traceMap5.put(output[0], traceMap5.get(output[0]) + 1);
						}else{
							traceMap5.put(output[0], 1);
						}
					}
					
					fw5 = new FileWriter("/s_home/ms853/Documents/software-reengineering/assignment3/assignment3-ms853/dataFiles/BDDAssertions_then_Test.csv");
					pw = new PrintWriter(fw5);
					pw.println("Class Name, Number of Occurrences");
					for(Entry<String, Integer> e : traceMap5.entrySet()) {
						pw.println(e.getKey() + "," + e.getValue());
						pw.flush();
					}
					pw.close();
					
				}
				
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		parseToCSV();
	}

}
