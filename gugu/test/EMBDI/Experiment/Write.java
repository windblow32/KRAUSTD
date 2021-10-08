package EMBDI.Experiment;

import org.junit.Test;

import java.util.regex.Pattern;

public class Write {
    @Test
    public void test(String args){
        String str = "/data/truth.csv";
        String regex = ".truth.";
        System.out.println(Pattern.matches(regex,str));
    }
}
