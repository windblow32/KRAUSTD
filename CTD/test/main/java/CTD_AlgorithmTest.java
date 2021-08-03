package main.java;

import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class CTD_AlgorithmTest extends TestCase {
    @Test
    public void testUpdate() {
        CTD_Algorithm test = new CTD_Algorithm();
        List<String> files = new ArrayList<>();
        files.add("data/sourceDataCSV/sourceData1.CSV");
        files.add("data/sourceDataCSV/sourceData2.CSV");
        files.add("data/sourceDataCSV/sourceData3.CSV");

        int k = 3;
        List<String> DCs = new ArrayList<>();
        DCs.add("time > 0");
        DCs.add("city > place");
//        DCs.add("Tuple 1 gender = tuple 3 gender");
        List<Double> w;
        w = test.update(files, k, DCs,"FIVE");
        for (double weight : w) {
            System.out.println(weight);
        }
    }
}