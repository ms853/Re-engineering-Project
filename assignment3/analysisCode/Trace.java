

import java.util.logging.*;

import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.*;


/*
This aspect class will be responsible for logging trace files for the following classes that I have identified based on the results of the static analysis.
The following classes I will be performing this tracing are: 

org.assertj.core.api.AbstractIterableAssert
org.assertj.core.internal.Arrays - AbstractEnumerableAssert_hasSameSizeAs_with_Array_Test.java
org.assertj.core.internal.Iterables
org.assertj.core.api.AbstractObjectArrayAssert
org.assertj.core.api.AtomicReferenceArrayAssert


The logger should record the class, method name and its parameters, as well as the stack depth of each method call. 
*/

aspect Trace{

        private static Logger logger = Logger.getLogger("Tracing");

        public Trace(){

        try{
                FileHandler handler=new FileHandler("trace.log",false);

                logger.addHandler(handler);

                handler.setFormatter(new Formatter(){

                        public String format(LogRecord record){
                                return record.getMessage()+"\n";
                                }
                                });
                        }

        catch(Exception e){}

        }
        
        pointcut traceMethods() : (execution(* *(..))&& !cflow(within(Trace)) && !within(org.junit..*));
        
        

        before(): traceMethods(){
                Signature sig = thisJoinPointStaticPart.getSignature();
                String sourceName = thisJoinPointStaticPart.getSourceLocation().getWithinType().getCanonicalName();
                
                //Get the parameters of the methods. 
                String[] parameters = ((CodeSignature) thisJoinPointStaticPart.getSignature()).getParameterNames();
                StackTraceElement[] stackDepth = Thread.currentThread().getStackTrace(); //array for getting the stack depth.
                int realStackDepth = stackDepth.length -1;
                
                StringBuilder paramSb = new StringBuilder();
                String stack_depth = Integer.toString(stackDepth.length);
                
                for(int i = 0; i < parameters.length; i++) {
                	paramSb.append(parameters[i] + " ");
                	
                }
                
                
                
                Logger.getLogger("Tracing").log(
                        Level.INFO,
                        //sig.getDeclaringType().getSimpleName().toString() + " " + 
                        sourceName + " " + sig.getName()
                        + " " + paramSb.toString().replaceAll(","," ") //replace all commas
                        + " " + realStackDepth
                );
        }
}
