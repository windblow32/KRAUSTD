package EMBDI.Experiment;

import org.junit.Test;

import java.util.regex.Pattern;

public class Write {
    @Test
    public void test(){
//        String str = " 4 5 6 7 8 9 11 12 13 14 15 16 18 20 22 28 29 30 31 32 33 36 37 39 41 42 43 46 48 49 50 51 54 ";
//        String[] data = str.split(" ",-1);
//        for(int i = 1;i<=55;i++){
//            int findSource = 0;
//            for(int j = 0;j< data.length;j++){
//                if(data[j].equals(String.valueOf(i))){
//                    findSource = 1;
//                    break;
//                }
//            }
//            if(findSource!=1){
//                System.out.println("不符合的数据源 :" + i);
//            }
//        }
//        System.out.println(55-data.length);

        boolean[] array = {true,false,true};
        System.out.println(getPartNum(array));



    }
    public int getPartNum(boolean[] array) {
        if (array == null) {
            return 0;
        }
        int num = 0;
        for (boolean bool : array) {
            num <<= 1;
            if (bool) {
                num += 1;
            }
        }
        return num;
    }
}
