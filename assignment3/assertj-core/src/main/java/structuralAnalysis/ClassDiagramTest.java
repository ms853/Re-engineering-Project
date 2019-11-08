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
package structuralAnalysis;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.plaf.synth.SynthSpinnerUI;

/**
 * Created by neilwalkinshaw on 24/10/2017.
 * 
 * This class was created to generate class diagrams based on metrics 
 * -I have chosen in the static analysis. 
 */
public class ClassDiagramTest {

	 protected Map<String,String> inheritance;
	    protected Map<String,Set<String>> associations;

	    //include classes in the JDK etc? Can produce crowded diagrams.
	    protected static boolean includeLibraryClasses = false;

	    protected Set<String> allClassNames;
	    static HashMap<String, Integer> classOccursMap = new HashMap<String, Integer>(); //hashmap for class occurrence.
	    static HashMap<String, Integer> locMap = new HashMap<String, Integer>(); //hashmap for lines of code.
	    static HashMap<String, Integer> wmcMap = new HashMap<String, Integer>(); //hashmap for weighted method count
	    
	   // @SuppressWarnings("null")
		public static void main(String[] a) throws IOException {
	    	
	    	String fileName1, fileName2, fileName3; 
	    	fileName1 = "/s_home/ms853/Documents/software-reengineering/assignment3/assignment3-ms853/dataFiles/LinesOfCode.csv";
	    	fileName2 = "/s_home/ms853/Documents/software-reengineering/assignment3/assignment3-ms853/dataFiles/classOccurrence.csv";
	    	fileName3 = "/s_home/ms853/Documents/software-reengineering/assignment3/assignment3-ms853/dataFiles/weightedMethodCount.csv";
	    	BufferedReader reader;
	    	BufferedReader reader2 = null;
	    	BufferedReader reader3 = null;
	    	//BufferedReader reader3 = null;
	    	reader = new BufferedReader(new FileReader(fileName1));
			String[] output;
			String line = "";
			String headerLine = "";
			headerLine = reader.readLine(); //to skip the first header csv file. 
			System.out.println(headerLine);
			while ((line = reader.readLine()) != null) {
				output = line.split(",");	
				locMap.put(output[0], Integer.parseInt(output[1]));
			
			}
			reader.close();
			
			reader2 = new BufferedReader(new FileReader(fileName2));
			String[] output1;
			String line1 = "";
			String headerLine1 = "";
			headerLine1 = reader2.readLine(); //to skip the first header csv file. 
			System.out.println(headerLine1);
			while ((line1 = reader2.readLine()) != null) {
				output1 = line1.split(",");	
				classOccursMap.put(output1[0], Integer.parseInt(output1[1].substring(1)));
			
			}
			reader2.close();
			
			reader3 = new BufferedReader(new FileReader(fileName3));
			String[] output2;
			String line2 = "";
			String headerLine2 = "";
			headerLine2 = reader3.readLine(); //to skip the first header csv file. 
			System.out.println(headerLine2);
			while ((line2 = reader3.readLine()) != null) {
				output2 = line2.split(",");	
				wmcMap.put(output2[0], Integer.parseInt(output2[1]));
			
			}
			reader3.close();
			
		 	//String for writing out the class diagram
	    	String cdFile = "/s_home/ms853/Documents/software-reengineering/assignment3/assignment3-ms853/dataFiles/classDiagram.dot";	
	    	//the targetClasses object is responsible for processing all the classes in the target-classes directorys
			ClassDiagramTest targetClasses = new ClassDiagramTest("target/classes", includeLibraryClasses);
			File dotFile = new File(cdFile);
			targetClasses.writeDot(dotFile); //write out the class diagram dot file for class occurrences & Lines of Code.
	    }

	    public static List<Class<?>> processDirectory(File directory, String pkgname) {

	        ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
	        String prefix = pkgname+".";
	        if(pkgname.equals(""))
	            prefix = "";

	        // Get the list of the files contained in the package
	        String[] files = directory.list();
	        for (int i = 0; i < files.length; i++) {
	            String fileName = files[i];
	            String className = null;

	            // we are only interested in .class files
	            if (fileName.endsWith(".class")) {
	                // removes the .class extension
	                className = prefix+fileName.substring(0, fileName.length() - 6);
	            }


	            if (className != null) {
	                Class loaded = loadClass(className);
	                if(loaded!=null)
	                    classes.add(loaded);
	            }

	            //If the file is a directory recursively class this method.
	            File subdir = new File(directory, fileName);
	            if (subdir.isDirectory()) {

	                classes.addAll(processDirectory(subdir, prefix + fileName));
	            }
	        }
	        return classes;
	    }

	    private static Class<?> loadClass(String className) {
	        try {
	            return Class.forName(className);
	        }
	        catch (ClassNotFoundException e) {
	            throw new RuntimeException("Unexpected ClassNotFoundException loading class '" + className + "'");
	        }
	        catch (Error e){
	            return null;
	        }
	    }


	    /**
	     * Instantiating the class will populate the inheritance and association relations.
	     * @param root
	     */
	    public ClassDiagramTest(String root, boolean includeLibs){
	        this.includeLibraryClasses = includeLibs;
	        File dir = new File(root);

	        List<Class<?>> classes = processDirectory(dir,"");
	        inheritance = new HashMap<String, String>();
	        associations = new HashMap<String, Set<String>>();



	        allClassNames = new HashSet<String>();
	        for(Class cl : classes){
	        	//a condition to exclude any classes that are not in the org.assertj.core package.
	        	if(cl.getName().startsWith("org.assertj.core")) {
	        		allClassNames.add(cl.getName());
	        	}
	        }

	        for(Class cl : classes){
	        	
	            if(cl.isInterface())
	                continue;
	            if(cl.getName().startsWith("org.assertj.core")) {
	            	
	            	inheritance.put(cl.getName(),cl.getSuperclass().getName());
		            Set<String> fields = new HashSet<String>();
		            for(Field fld : cl.getDeclaredFields()){
		                //Do not want to include associations to primitive types such as ints or doubles.
		                if(!fld.getType().isPrimitive()) {
		                    fields.add(fld.getType().getName());
		                }
		            }
		            associations.put(cl.getName(),fields);
	            }
	            
	        }
	    }

	    /**
	     * Write out the class diagram to a specified file.
	     * @param target
	     */
	    public void writeDot(File target) throws IOException {
	        BufferedWriter fw = new BufferedWriter(new FileWriter(target));
	        StringBuffer dotGraph = new StringBuffer();
	        Collection<String> dotGraphClasses = new HashSet<String>(); //need this to specify that shape of each class should be a square.
	        dotGraph.append("digraph classDiagram{\n" +
	                "graph [splines=ortho]\n\n");

	        //Add inheritance relations
	        for(String childClass : inheritance.keySet()){
	            String from = "\""+childClass +"\"";
	            dotGraphClasses.add(from);
	            String to = "\""+inheritance.get(childClass)+"\"";
	            if(!includeLibraryClasses){
	                if(!allClassNames.contains(inheritance.get(childClass)))
	                    continue;
	            }
	            dotGraphClasses.add(to);
	            dotGraph.append(from+ " -> "+to+"[arrowhead = onormal];\n");
	        }

	        //Add associations
	        for(String cls : associations.keySet()){
	            Set<String> fields = associations.get(cls);
	            for(String field : fields) {
	                String from = "\""+cls +"\"";
	                dotGraphClasses.add(from);
	                String to = "\""+field+"\"";
	                if(!includeLibraryClasses){
	                    if(!allClassNames.contains(field))
	                        continue;
	                }
	                dotGraphClasses.add(to);
	                dotGraph.append(from + " -> " +to + "[arrowhead = diamond];\n");
	            }
	        }

	        /*
	         * Firstly, I iterate through the hashmap and within that iteration,
	         * I iterate over the collection of dotGraphClasses.
		        * Here I check for every node in the collection, 
		        * if that the set of keys in the hashmap consists of that node. 
		        * And I scale the class diagram according to the condition.
		        * */
			for(Entry<String, Integer> w : classOccursMap.entrySet()) {	
				for(String node : dotGraphClasses){	
			        	if(node.contains(w.getKey()) && (w.getValue() >= 500)) {
			        		dotGraph.append(node+ "[shape = box height = 10 width = 10 fillcolor=purple style=filled];\n");
			        	}
			        }
			}
			
			//Alter class diagram based on the lines of code.
			for(Entry<String, Integer> lc : locMap.entrySet()) {
				//some refactoring  to the key so it can be compared with the node. 
				String className ="org.assertj.core" + lc.getKey().substring(1, lc.getKey().length() -5).replaceAll("/", ".");
				
				for(String node : dotGraphClasses){
			       // if lines of code is greater or equal to 100 then modify the classes.
			        	if(node.contains(className) && (lc.getValue() >= 100)) {
			        		dotGraph.append(node+ "[shape = circle height = 5 width = 5] [color=green fillcolor=blue style=filled];\n"); //scale the dot-graph object by changing the shape.
			        		if(lc.getValue() >=300) { 
			        			dotGraph.append(node+ "[shape = doublecircle height = 7 width = 7] [color=black fillcolor=red style=filled];\n"); //scale 
			        			System.out.println("LOOOK ------ " + node);
			        		}
			        	}
			        		
			        }
			}
			
			//This part of the code alters the structure of the class diagram based on the weighted 
			for(Entry<String, Integer> wc : wmcMap.entrySet()) {
				//some refactoring to the key so it can be compared with the node. 
				String className = wc.getKey().replaceAll("/", ".");
				for(String node : dotGraphClasses){
			        	if(node.contains(className) && (wc.getValue() >= 100)) {
			        		dotGraph.append(node+ "[shape = box height = 5 width = 5] [color=green fillcolor=orange style=filled];\n");
			        	//	System.out.println("LOOOK ------ " + node);
			        	}
			        		
			        }
			}
	        
	        dotGraph.append("}");
	        fw.write(dotGraph.toString());
	        fw.flush();
	        fw.close();
	        //System.out.println(dotGraph.toString());
	    }

}
