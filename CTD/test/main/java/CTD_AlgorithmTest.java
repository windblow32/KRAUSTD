package main.java;

import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class CTD_AlgorithmTest extends TestCase {
    public int sourceNum = 55;
    @Test
    public void testUpdate() {
        String str = "1,2,,3,4,5,6";
        String[] data = str.split(",",-1);
        System.out.println(data);
//        CTD_Algorithm test = new CTD_Algorithm();
//        List<String> files = new ArrayList<>();
//        for(int i = 0;i<sourceNum;i++){
//            int temp = i + 1;
//            String filePath = "data/stock100/divideSource/source" + temp + ".csv";
//            files.add(filePath);
//        }
//
//        int k = 3;
//        List<String> DCs = new ArrayList<>();
//        DCs.add("52wk_H > 52wk_L");
////        DCs.add("Tuple 1 gender = tuple 3 gender");
//        List<Double> w;
////        w = test.update(files, k, DCs,"THREE");
////        for (double weight : w) {
////            System.out.println(weight);
////        }
    }
}