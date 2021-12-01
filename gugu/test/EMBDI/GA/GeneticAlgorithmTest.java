package EMBDI.GA;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import EMBDI.TripartiteGraphWithSource.RunInGA;
import static java.lang.System.arraycopy;
import static java.lang.System.err;

/**
 *@Description:
 */

public class GeneticAlgorithmTest extends GeneticAlgorithm{

    public static final int NUM = 1 << 24;
    public int version = 0;

    // 分批转化成不同的超参，带入评价函数中
    String p1 = "length";
    String p2 = "sampleNum";

    public GeneticAlgorithmTest() {
        // todo change length of chro
        super(24);
    }

    @Override
    /**
     * @Description: x的显示表示，将chro转换为用户需要的显示信息，只在print时候和calc时候调用，返回类型是double
     * todo
     */
    public String changeX(Chromosome chro) {
        // TODO Auto-generated method stub
        boolean[] parameter1 = new boolean[p1.length()];
        boolean[] parameter2 = new boolean[p2.length()];
        arraycopy(chro.gene,0,parameter1,0,p1.length());
        arraycopy(chro.gene,p1.length(),parameter2,0,p2.length());
        return p1 + ": " + String.valueOf(getPartNum(parameter1))
                + p2 + ": " + String.valueOf(getPartNum(parameter2));

        // return String.valueOf((1.0 * chro.getNum() / NUM) * 100 + 6);
    }


    @Override
    /**
     * @Description: 设计评价函数,将changeX的参数带入模型中训练，根据训练结果计算估价函数
     * todo
     */
    public double calculateY(String x) {
        // TODO Auto-generated method stub
        String[] parameter = x.split(": ",-1);
        RunInGA obj = new RunInGA();
        // trans what
        // fixme : parse x into several parts
        int length=10;
        int useNum=100;
        // length, 三分/5分, sampleNum
        obj.trainWithPath(length, useNum,version);
        // todo 根据参数返回结果，计算评价函数
        // 评价函数实现：
        double score = valueFunction();
        return 100 - Math.log(Double.parseDouble(x));
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
    @Test
    public void test() {
        GeneticAlgorithmTest test = new GeneticAlgorithmTest();
        test.calculate();
    }

    private double valueFunction(){

        return 0;
    }

}