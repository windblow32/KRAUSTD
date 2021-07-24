package main.java;


import java.util.HashMap;

public class MyUDF  {
    /**
     * UDF Evaluate接口
     * "tyf 1" 与"tyf1"
     * UDF在记录层面上是一对一，字段上是一对一或多对一。 Evaluate方法在每条记录上被调用一次，输入为一个或多个字段，输出为一个字段
     */
    public Double evaluate(String a, String b) {
        // TODO: 请按需要修改参数和返回值，并在这里实现你自己的逻辑
        if(a==null || b==null)
            return 0.0;
        String[] temp1 =a.split(",");
        String[] temp2 =b.split(",");
        if (temp1==null || temp2==null) {
            return 0.0;
        }
        HashMap<String, Double> map1=new HashMap<String, Double>();
        HashMap<String, Double> map2=new HashMap<String, Double>();
        for(String temp:temp1)
        {
            String[] t =temp.split(":");
            map1.put(t[0], Double.parseDouble(t[1]));
        }
        for(String temp:temp2)
        {
            String[] t =temp.split(":");
            map2.put(t[0], Double.parseDouble(t[1]));
        }
        double fen_zi=0;
        double fen_mu1=0;
        for(String i:map1.keySet())
        {
            double value=map1.get(i);
            if (map2.get(i)!=null) {
                fen_zi+=value*map2.get(i);
            }
            fen_mu1+=value*value;
        }
        double fenmu2=0;
        for(double i:map2.values())
        {
            fenmu2+=i*i;
        }
        double fenmu=Math.sqrt(fen_mu1)*Math.sqrt(fenmu2);
        return fen_zi/fenmu;
    }
    public static void main(String[] args) {
//        String a="12:500,14:105,30:100";
//        String b="12:500,14:100,30:300";
//        String c = "12:500,14:100,30:200";
//        String d = "12:50,14:100,30:200";
        String a = "12";
        String b = "1";
        String c = "2";
        String d = "122";
        // 都是2*1矩阵
        int L = 2;
        int p = 1;
        String[][] a1 = {{a},{b}};
        String[][] a2 = {{c},{d}};
        MyUDF myUDF=new MyUDF();
        double error = 0;
        for(int i = 0;i<L;i++){
            for(int j = 0;j<p;j++){
                error += 1-myUDF.evaluate(a1[i][j],a2[i][j]);
            }
        }
        System.out.println(error);
    }
}