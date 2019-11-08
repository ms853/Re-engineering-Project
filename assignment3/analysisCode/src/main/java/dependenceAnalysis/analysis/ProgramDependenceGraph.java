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
package dependenceAnalysis.analysis;

import br.usp.each.saeg.asm.defuse.Variable;
import dependenceAnalysis.util.cfg.CFGExtractor;
import dependenceAnalysis.util.cfg.Graph;
import dependenceAnalysis.util.cfg.Node;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by neilwalkinshaw on 19/10/2017.
 */
public class ProgramDependenceGraph extends Analysis {

    Graph dataDependencies;
    Graph controlDependencies;
    Graph combined;

    public ProgramDependenceGraph(ClassNode cn, MethodNode mn) {
        super(cn, mn);
        dataDependencies = new Graph();
    }

    /**
     * Return a graph representing the Program Dependence Graph of the control
     * flow graph, which is stored in the controlFlowGraph class attribute
     * (this is inherited from the Analysis class).
     *
     * You may wish to use the class that computes the Control Dependence Tree to
     * obtain the control dependences, and may wish to use the code in the
     * dependenceAnalysis.analysis.DataFlowAnalysis class to obtain the data dependences.
     * 
     * @return
     */
    public Graph computeResult() {
        computeDataDependenceEdges();
        computeControlDependencies();
        computeCombinedControlDataDependence();
        return combined;
    }

    private void computeCombinedControlDataDependence() {
        combined = new Graph();
        for(Node n : controlFlowGraph.getNodes()){
            combined.addNode(n);
        }
        for(Node n : controlFlowGraph.getNodes()){
            for(Node o : controlDependencies.getSuccessors(n)){
                combined.addEdge(n,o);
            }
            for(Node o : dataDependencies.getSuccessors(n)){
                combined.addEdge(n,o);
            }
        }
    }

    private void computeControlDependencies() {
        ControlDependenceTree cdt = new ControlDependenceTree(cn,mn);
        controlDependencies = cdt.computeResult();
    }

    /**
     * Compute data dependences between cfg nodes.
     * @return
     */
    protected void computeDataDependenceEdges(){
        //Instantiate our DataFlowAnalysis, which we will use to compute defs and uses for instructions.
        DataFlowAnalysis dfa = new DataFlowAnalysis();
        try {
            //Iterate through all of the cfg instructions
            for(Node n : controlFlowGraph.getNodes()) {
                dataDependencies.addNode(n);
                //For each instruction...
                Collection<Node> defs = new HashSet<Node>();
                //... define a set (defs) that define variables that are used at this node.
                if (n.getInstruction() == null)
                    continue;
                //For each variable that is used at this node...
                for (Variable use : dfa.usedBy(cn.name, mn, n.getInstruction())) {
                    // Search backwards through the graph for all nearest defs of that variable
                    defs.addAll(bfs(dfa, controlFlowGraph, n, use, new HashSet<Object>()));
                }


                //For each definition of a variable, create a data dependence edge.
                for (Node def : defs) {
                    if (def.equals(n))
                        continue;
                    dataDependencies.addNode(def);
                    dataDependencies.addEdge(def, n);
                }
            }

        }catch (AnalyzerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    /**
     * A breadth-first search backwards through a control flow graph.
     * @param dfa - Data flow analysis for the cfg in question
     * @param cfg - CFG we are searching through.
     * @param n - Node in the CFG we are commencing our search from.
     * @param use - Variable we are considering.
     * @param done - Set of nodes explored as part of search so far
     * @return
     * @throws AnalyzerException
     */
    private Collection<Node> bfs(DataFlowAnalysis dfa, Graph cfg, Node n, Variable use, Collection<Object> done) throws AnalyzerException {
        // Create an empty set of nodes that will eventually contain the nodes we are interested in.
        Collection<Node> defs = new HashSet<Node>();
        //For all immediate predecessors in the graph of n:
        for(Node pred : cfg.getPredecessors(n)){
            //Skip loops.
            if(done.contains(pred))
                continue;
            //Keep track of which nodes we have have already visited to avoid visiting them again.
            done.add(pred);
            //skip null instructions (entry and exit)
            if(pred.getInstruction()== null)
                continue;
            // If the set of variables that is defined at this instruction contains the variable we are interested in (use)
            if(dfa.definedBy(cn.name, mn, pred.getInstruction()).contains(use)){
                //Add this node to the set of defs. We don't need to look at any of this node's predecessors.
                defs.add(pred);
                continue;
            }
            //Otherwise
            else{
                //Check this nodes predecessors and recurse. Add the defs that are eventually found further down the line.
                defs.addAll(bfs(dfa,cfg,pred,use,done));
            }

        }
        return defs;
    }

    /**
     * Compute the set of nodes that belong to a backward slice, computed from a given
     * node in the program dependence graph.
     *
     * @param node
     * @return
     */
    public Set<Node> backwardSlice(Node node){
        if(combined == null)
            this.computeResult();
        HashSet<Node> slice = new HashSet<Node>();
        slice.add(node);
        slice.addAll(transitivePredecessors(node,slice));
        return slice;
    }

    private Set<Node> transitivePredecessors(Node m, Set<Node> done){
        Set<Node> preds = new HashSet<Node>();
        for(Node n : combined.getPredecessors(m)){
            if(!done.contains(n)) {
                preds.add(n);
                done.add(n);
                preds.addAll(transitivePredecessors(n, done));
            }

        }
        return preds;
    }

    Collection<Node> getOutputNodes(){
        Collection<Node> criteria = new HashSet<Node>();
        for(Node n : dataDependencies.getNodes()){
            if(dataDependencies.getSuccessors(n).isEmpty())
                criteria.add(n);
        }
        return criteria;
    }

    /**
     * Compute the Tightness slice-based metric. The proportion of nodes in a control flow graph that occur
     * in every possible slice of that control flow graph.
     * @return
     */
    public double computeTightness(){
        Collection<Node> criteria = getOutputNodes();
        Collection<Node> inAllSlices = new HashSet<Node>();
        inAllSlices.addAll(controlFlowGraph.getNodes());
        for(Node n : criteria){
            Collection<Node> slice = backwardSlice(n);
            inAllSlices.retainAll(slice);
        }
        return (double)inAllSlices.size()/((double)controlFlowGraph.getNodes().size());
    }

    /**
     * Compute the Overlap slice-based metric: How many statements per output-slice are unique to that slice?
     * Output slices are computed by computing backward slices from the set of nodes with incoming data dependencies,
     * but with no outgoing data dependencies.
     * @return
     */
    public double computeOverlap(){
        Collection<Node> criteria = getOutputNodes();
        Collection<Collection<Node>> slices = new HashSet<Collection<Node>>();
        for(Node n : criteria){
            Collection<Node> slice = backwardSlice(n);
            slices.add(slice);
        }
        double uniqueTotal = 0D;
        for(Collection<Node> slice : slices){
            double uniqueForSlice = 0D;
            Collection<Collection<Node>> otherSlices = new HashSet<Collection<Node>>();
            otherSlices.addAll(slices);
            otherSlices.remove(slice);
            for(Node n : slice){
                boolean unique = true;
                for(Collection<Node> otherSlice : otherSlices){
                    if(otherSlice.contains(n))
                        unique = false;
                }
                if(unique)
                    uniqueForSlice++;
            }
            uniqueTotal +=uniqueForSlice;
        }

        return uniqueTotal/slices.size();
    }
    
    public static void main(String[] args) throws IOException{
    	String className = "/org/assertj/core/api/ListAssert.class";
    	
    	ClassNode cn = new ClassNode(Opcodes.ASM4);
    	InputStream in = CFGExtractor.class.getResourceAsStream(className);
		ClassReader classReader = new ClassReader(in);
		classReader.accept(cn, 0);
		
		for (MethodNode mn : (List<MethodNode>) cn.methods) {
			ProgramDependenceGraph pgd = new ProgramDependenceGraph(cn, mn);
			pgd.computeResult();
			System.out.println(mn.name +", "+pgd.computeTightness());
		}
    	
    }
}