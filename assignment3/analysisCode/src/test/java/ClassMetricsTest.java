import org.junit.Before;
import org.junit.Test;

import java.io.File;

/**
 * Created by neilwalkinshaw on 24/10/2017.
 */
public class ClassMetricsTest {

    public final String analysisClass = "/java/awt/geom/Area.class";
    public final String targetCSV = "outputs" + File.separator+ "Area.csv";

    /**
     * If a CSVFile exists from a previous test, delete it.
     *
     * We do not clean this file up *after* running the test because you might want to inspect it /
     * analyse it with Excel etc.
     *
     */
    @Before
    public void cleanUpAndPrepare(){
        File csvFile = new File(targetCSV);
        if(csvFile.exists()){
            csvFile.delete();
        }
        File outputDir = new File("outputs");
        if(!outputDir.exists()){
            outputDir.mkdir();
        }
    }

    /**
     * Run the main method.
     * @throws Exception
     */
    @Test
    public void main() throws Exception {
        ClassMetrics.main(new String[]{analysisClass,targetCSV});
    }

}