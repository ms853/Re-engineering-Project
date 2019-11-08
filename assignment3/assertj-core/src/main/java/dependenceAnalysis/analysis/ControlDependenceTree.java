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

import dependenceAnalysis.util.cfg.Graph;
import dependenceAnalysis.util.cfg.Node;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by neilwalkinshaw on 19/10/2017.
 */
public class ControlDependenceTree extends Analysis {

    public ControlDependenceTree(ClassNode cn, MethodNode mn) {
        super(cn, mn);
    }

    /**
     * Return a graph representing the control dependence tree of the control
     * flow graph, which is stored in the controlFlowGraph class attribute
     * (this is inherited from the Analysis class).
     *
     * You may wish to use the post dominator tree code you implement to support
     * computing the Control Dependence Graph.
     *
     * @return
     */
    public Graph computeResult() {
        Graph controlDependenceTree = new Graph();
        Graph augmentedGraph = createAugmentedGraph();
        Graph postdomtree = computePostDomTree();
        try{
            //Compute dominance tree for CFG.

            Collection<Node> done = new HashSet<Node>();

            Node startNode = augmentedGraph.getEntry();

            for(Node cfgNode : augmentedGraph.getNodes()){
                for(Node successor : augmentedGraph.getSuccessors(cfgNode)){
                    //Check whether link between cfgNode and Successor is decision.
                    //Could also have done this by checking number of successors of cfgNode.
                    if(!(augmentedGraph.getSuccessors(cfgNode).size()>1))
                        continue;
                    //Check whether successor post-dominates cfgNode
                    if(postdomtree.getTransitiveSuccessors(successor).contains(cfgNode))
                        continue;
                    assert(!cfgNode.equals(successor));

                    if(cfgNode.equals(startNode))
                        continue;
                    //Get least common ancestor of cfgnode and successor
                    Node leastCommonAncestor = postdomtree.getLeastCommonAncestor(cfgNode,successor);
                    Collection<Node> path = new HashSet<Node>();
                    //Include cfgNode if cfgNode is least common ancestor.
                    if(leastCommonAncestor.equals(cfgNode)){
                        path.add(cfgNode);
                        path.addAll(getNodesOnPath(postdomtree,leastCommonAncestor,successor));
                    }
                    //Do not include cfgNode if cfgNode is not least common ancestor.
                    else{
                        path.addAll(getNodesOnPath(postdomtree,leastCommonAncestor,successor));
                        path.remove(cfgNode);
                    }
                    //Add control dependence edges between cfgNode and every node on path to successor.
                    for(Node target : path){
                        controlDependenceTree.addNode(cfgNode);
                        controlDependenceTree.addNode(target);
                        controlDependenceTree.addEdge(cfgNode, target);
                        done.add(target);
                    }
                }
            }

            //For any node that is not already control-dependent upon another node, add control dependence from Entry node.
            Node entry = controlFlowGraph.getEntry();
            controlDependenceTree.addNode(entry);
            done.add(entry);
            for(Node n : controlFlowGraph.getNodes()){
                if(!done.contains(n)) {
                    controlDependenceTree.addNode(n);
                    controlDependenceTree.addEdge(entry, n);
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return controlDependenceTree;
    }

    private Graph computePostDomTree() {
        PostDominatorTree dtg = new PostDominatorTree(cn,mn);
        Graph reversed = copyControlFlowGraph();
        reversed = dtg.reverseGraph(reversed);
        Graph pdt = dtg.computePostDominanceTree(reversed);
        return pdt;
    }


    private Graph createAugmentedGraph() {
        Graph augmentedGraph = copyControlFlowGraph();
        addStartNode(augmentedGraph);
        return augmentedGraph;
    }

    private Graph copyControlFlowGraph() {
        Graph newGraph = new Graph();
        for(Node n : controlFlowGraph.getNodes()){
            newGraph.addNode(n);
        }
        for(Node m : controlFlowGraph.getNodes()){
            for(Node n : controlFlowGraph.getSuccessors(m)){
                newGraph.addEdge(m,n);
            }
        }
        return newGraph;
    }

    private void addStartNode(Graph edges) {
        Node start = new Node("start");
        edges.addNode(start);
        edges.addEdge(start, edges.getEntry());
        edges.addEdge(start, edges.getExit());
    }

    private Collection<? extends Node> getNodesOnPath(Graph tree, Node from,
                                                      Node to) {
        Collection<Node> path = new HashSet<Node>();
        Node current = to;
        assert(tree.getTransitiveSuccessors(from).contains(to));
        while(!current.equals(from)){
            if(!current.equals(from))
                path.add(current);
            Collection<Node> predecessors = tree.getPredecessors(current);
            if(predecessors.isEmpty()){
                System.err.println("Failed to find parent node for "+current);
                break;
            }
            //Because we are walking up a tree, we know that each node can only have a single predecessor.
            assert(predecessors.size()==1);
            current = predecessors.iterator().next(); //take what should be the only node in the set.
        }
        assert(!path.contains(from));
        return path;
    }

}
