
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
import dependenceAnalysis.analysis.ProgramDependenceGraph;
import dependenceAnalysis.util.cfg.CFGExtractor;
import dependenceAnalysis.util.cfg.Graph;
import dependenceAnalysis.util.cfg.Node;
import structuralAnalysis.ClassDiagramSolution;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

/**
 * This class will write a single CSV file of metrics for a class.
 *
 *
 * Created by neilwalkinshaw on 24/10/2017.
 */

// This class is used to perform some static analysis on the test-classes of
// assertj-core.
public class ClassMetrics {

	/**
	 * First argument is the class name, e.g. /java/awt/geom/Area.class" The
	 * second argument is the name of the target csv file, e.g.
	 * "classMetrics.csv"
	 * 
	 * @param args
	 * @throws IOException
	 */

	public static void main(String[] args) throws IOException {

		// Read in the classes using the processDirecory method
		File dir = new File("target/classes"); // point to the class files in
												// the assertJ directory.
		List<Class<?>> listOfClasses = ClassDiagramSolution.processDirectory(dir, "");

		List<String> record = new ArrayList<String>();
		String str = "";

		HashMap<String, Integer> methodWeightedCount = new HashMap<String, Integer>();

		// CSV Printer and value.
		FileWriter fw = new FileWriter("classMetrics.csv");
		FileWriter fw2 = new FileWriter("weightedMethodCount.csv");
		FileWriter fw3 = new FileWriter("methodCohesiveness.csv");

		PrintWriter pw = new PrintWriter(fw2);

		CSVPrinter csvPrinter = new CSVPrinter(fw, CSVFormat.EXCEL);

		CSVPrinter csvPrinter2 = new CSVPrinter(fw3, CSVFormat.EXCEL);
		pw.println("Class Name, Weighted Method Count");
		csvPrinter2.printRecord("Method Name", "Method Tightness");

		record.add("Method");
		record.add("Nodes");
		record.add("Cyclomatic Complexity");
		csvPrinter.printRecord(record);

		String className = "";

		for (Class<?> c : listOfClasses) {
			int wmcCount = 0;

			ClassNode cn = new ClassNode(Opcodes.ASM4); // creates a class node
														// every time it
														// iterates.

			str = c.getName();

			className = str;
			className = "/" + className.replace(".", "/") + ".class";

			if (className.startsWith("/org/assertj/core/")) {
				// point to the class files in the assertJ directory

				InputStream in = CFGExtractor.class.getResourceAsStream(className);
				ClassReader classReader = new ClassReader(in);
				classReader.accept(cn, 0);
				//System.out.println(className);

				for (MethodNode mn : (List<MethodNode>) cn.methods) {
					record = new ArrayList<String>();
					
					double computeTightnessResult = 0.0;
					ProgramDependenceGraph pd = new ProgramDependenceGraph(cn, mn);
					
					pd.computeResult(); //build the control flow graph. 
					int numNodes = -1;
					int cyclomaticComplexity = -1; // both values default to -1
													// if
													// they cannot be computed.

					try {
						Graph cfg = CFGExtractor.getCFG(cn.name, mn);
						numNodes = getNodeCount(cfg); // the the total number of
														// nodes for the control
														// flow graph
						cyclomaticComplexity = getCyclomaticComplexity(cfg); // compute
																				// the
																				// cyclomatic
																				// complexity
																				// for
																				// all
																				// the
																				// nodes.
						// totalNumNodes = numNodes;
						// Here I am calculating the total weighted method count
						// based on the total of the cyclomatic complexity for a
						// given class.
						wmcCount += cyclomaticComplexity;
						// totalNumNodes = (double) numNodes;
						// here I perform some slice based metrics by computing
						// the tightness,
						// -based on a given given threshold.
						if (numNodes > 150) {

							computeTightnessResult = pd.computeTightness();
							// computeTightnessResult =
							// computeTightness/numNodes;

							System.out.println("Look at Tightness Result: " + computeTightnessResult);
							// Here I am printing the value of the compute along
							// with the method name.
							csvPrinter2.printRecord(cn.name + "." + mn.name, computeTightnessResult);
						}

					} catch (AnalyzerException e) {
						e.printStackTrace();
					}

					// Here I am storing the wmc value for each class into the
					// hashmap.
					methodWeightedCount.put(cn.name, wmcCount);
					// Write the method details and metrics to the CSV record.
					record.add(cn.name + "." + mn.name); // Add method
																	// signature
																	// in first
																	// column.
					record.add(Integer.toString(numNodes));
					record.add(Integer.toString(cyclomaticComplexity));
					csvPrinter.printRecord(record);

					// System.out.println(record.toString());

				}
			}

		}

		// loop through the values of the hashmap and write the class and value
		// to the csv file.
		for (Entry<String, Integer> m : methodWeightedCount.entrySet()) {

			pw.println(m.getKey() + "," + m.getValue());
			pw.flush();
		}

		pw.close();
		csvPrinter.close();
		csvPrinter2.close();
	}

	/**
	 * Returns the number of nodes in the CFG.
	 * 
	 * @param cfg
	 * @return
	 */
	private static int getNodeCount(Graph cfg) {
		return cfg.getNodes().size();
	}

	/**
	 * Returns the Cyclomatic Complexity by counting the number of branches 
	 * 
	 * @param cfg
	 * @return
	 */
	private static int getCyclomaticComplexity(Graph cfg) {
		int branchCount = 0;
		for (Node n : cfg.getNodes()) {
			if (cfg.getSuccessors(n).size() > 1) {
				branchCount++;
			}
		}
		return branchCount + 1;
	}

}
