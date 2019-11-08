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
package dependenceAnalysis.util.cfg;

import org.objectweb.asm.tree.*;
import org.objectweb.asm.util.Printer;

import java.util.HashMap;
import java.util.Map;


public class Node{
	
	
	private AbstractInsnNode instruction = null;	
	
	private static Map<Object, String> sIds = null;
	protected static int sNextId = 1;
	
	private String id = null;
	
	public Node(AbstractInsnNode node) {
		instruction = node;
		id = getId(node);
	}
	
	public Node(String id) {
		this.id = "\""+id+"\"";
	}
	
	public AbstractInsnNode getInstruction(){
		return instruction;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		if(instruction == null)
			return id;
		if (instruction instanceof LabelNode) {
            sb.append("LABEL");
        } else if (instruction instanceof LineNumberNode) {
            sb.append("LINENUMBER " + ((LineNumberNode) instruction).line);
        } else if (instruction instanceof FrameNode) {
            sb.append("FRAME");
        } else {
            int opcode = instruction.getOpcode();
            String[] OPCODES = Printer.OPCODES;
            if (opcode > 0 && opcode <= OPCODES.length) {
                sb.append(OPCODES[opcode]);
                if (instruction.getType() == AbstractInsnNode.METHOD_INSN) {
                    sb.append("(" + ((MethodInsnNode)instruction).name + ")");
                }
            }
        }
		sb.append(getId(instruction));
	        
		return "\""+sb.toString()+"\"";
	}
	

	private static String getId(Object object) {
	        if (sIds == null) {
	            sIds = new HashMap<Object,String>();
	        }
	        String id = sIds.get(object);
	        if (id == null) {
	            id = Integer.toString(sNextId++);
	            sIds.put(object, id);
	        }
	        return id;
	    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
}
