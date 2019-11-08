package fileComparison;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Created by neilwalkinshaw on 08/11/2017.
 */
public class FileComparatorTest {

    String from = "src/test/java/ClassMetricsTest.java";

    String to = "src/test/java/StructuralAnalysis/ClassDiagramSolutionTest.java";

    @Test
    public void IndividualFileComparisonTest(){
        File fromFile = new File(from);
        File toFile = new File(to);
        FileComparator fc = new FileComparator(fromFile,toFile);
        boolean[][] scores = fc.detailedCompare();
        try {
            TablePrinter.printRelations(scores,new File("outputs/TestComparison.csv"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}