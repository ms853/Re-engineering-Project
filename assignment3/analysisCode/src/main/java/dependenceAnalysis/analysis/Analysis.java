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

import dependenceAnalysis.util.cfg.CFGExtractor;
import dependenceAnalysis.util.cfg.Graph;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;

/**
 *
 *
 * Created by neilwalkinshaw on 19/10/2017.
 */
public abstract class Analysis {

    protected final Graph controlFlowGraph;

    protected final MethodNode mn;
    protected final ClassNode cn;

    public Analysis(ClassNode cn, MethodNode mn){
        Graph cfg = null;
        if(cn == null) {
            //This should only happen under testing conditions.
            this.cn = null;
            this.mn = null;
            this.controlFlowGraph = null;
            return;
        }
        try {
            cfg = CFGExtractor.getCFG(cn.name, mn);

        } catch (AnalyzerException e) {
            e.printStackTrace();
        }
        controlFlowGraph = cfg;
        this.mn = mn;
        this.cn = cn;
    }

    /**
     * Mainly a testability method - returns the control flow graph.
     * @return
     */
    public Graph getControlFlowGraph(){
        return controlFlowGraph;
    }

    /**
     * Create a new graph object that returns a Graph representation of the results of the analysis.
     * @return
     */
    public abstract Graph computeResult();



}
