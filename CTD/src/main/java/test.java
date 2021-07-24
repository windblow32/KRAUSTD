package main.java;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class test {

    public static void main(String[] args){
//        String str = "code < 12";
//        String str1 = "code";
//        String[] newstr = str.split(" ");
//        for(int i = 0;i< newstr.length;i++){
//            System.out.println(newstr[i]);
//        }

        String regex = "^[+-]?([0-9]*\\.?[0-9]+|[0-9]+\\.?[0-9]*)([eE][+-]?[0-9]+)?$";
        String input = "-12.2e-3";
        boolean ismatch = Pattern.matches(regex, input);
        System.out.println(Pattern.matches(regex, input));
        int[][] a = {{1,2},{3,4}};
        change(a);
        System.out.println(a[0][0]);

        String t = "1b 2 3 4";
        String[] t_split = t.split(" ");
        List<String> list = new ArrayList<>(Arrays.asList(t_split));
        System.out.println(list.get(0));
        System.out.println(list.get(1));
        System.out.println(list.get(2));
        System.out.println(list.get(3));

    }
    private static void change(int[][] a){
        a[0][0] = 10;
    }


}
