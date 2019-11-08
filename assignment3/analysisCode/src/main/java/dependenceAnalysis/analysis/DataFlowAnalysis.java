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

import br.usp.each.saeg.asm.defuse.DefUseAnalyzer;
import br.usp.each.saeg.asm.defuse.DefUseFrame;
import br.usp.each.saeg.asm.defuse.Variable;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;

import java.util.Collection;

/**
 * Created by neilwalkinshaw on 21/10/2016.
 */
public class DataFlowAnalysis {

    /**
     * Return the collection of variables that are used by the specified statement.
     * @param owner
     * @param mn
     * @param statement
     * @return
     * @throws AnalyzerException
     */
    public static Collection<Variable> usedBy(String owner, MethodNode mn, AbstractInsnNode statement) throws AnalyzerException {
        DefUseAnalyzer analyzer = new DefUseAnalyzer();
        analyzer.analyze(owner, mn);
        DefUseFrame[] frames = analyzer.getDefUseFrames();
        int index = mn.instructions.indexOf(statement);
        return frames[index].getUses();
    }

    /**
     * Return the collection of variables that are defined by the specified statement.
     * @param owner
     * @param mn
     * @param statement
     * @return
     * @throws AnalyzerException
     */
    public static Collection<Variable> definedBy(String owner, MethodNode mn, AbstractInsnNode statement) throws AnalyzerException {
        DefUseAnalyzer analyzer = new DefUseAnalyzer();
        analyzer.analyze(owner, mn);

        DefUseFrame[] frames = analyzer.getDefUseFrames();
        int index = mn.instructions.indexOf(statement);
        return frames[index].getDefinitions();
    }

}
