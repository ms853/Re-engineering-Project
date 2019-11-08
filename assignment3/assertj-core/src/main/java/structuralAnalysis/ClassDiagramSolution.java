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


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by neilwalkinshaw on 24/10/2017.
 */
public class ClassDiagramSolution {

    protected Map<String,String> inheritance;
    protected Map<String,Set<String>> associations;

    //include classes in the JDK etc? Can produce crowded diagrams.
    protected boolean includeLibraryClasses = true;

    protected Set<String> allClassNames;

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
    public ClassDiagramSolution(String root, boolean includeLibs){
        this.includeLibraryClasses = includeLibs;
        File dir = new File(root);

        List<Class<?>> classes = processDirectory(dir,"");
        inheritance = new HashMap<String, String>();
        associations = new HashMap<String, Set<String>>();



        allClassNames = new HashSet<String>();
        for(Class cl : classes){
            allClassNames.add(cl.getName());
        }

        for(Class cl : classes){
            if(cl.isInterface())
                continue;
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

        for(String node : dotGraphClasses){
            dotGraph.append(node+ "[shape = box];\n");
        }

        dotGraph.append("}");
        fw.write(dotGraph.toString());
        fw.flush();
        fw.close();
    }


}
