package main.java.Embedding.EMBDI.GA;

import main.java.Embedding.EMBDI.TripartiteNormalizeDistributeGraph.NormalizeDistributeRunInGA;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import main.java.Embedding.EMBDI.TripartiteGraphWithSource.RunInGA;
import static java.lang.System.arraycopy;
import static java.lang.System.err;

/**
 *@Description:
 */

public class GeneticAlgorithmTest extends GeneticAlgorithm{

    public static final int NUM = 1 << 24;
    public int version = 0;
    public List<String> fileList = new ArrayList<>();

    // 分批转化成不同的超参，带入评价函数中

    String p1 = "length";
    int p1_length = 6;
    String p2 = "AttrDistributeLow";
    String p3 = "AttrDistributeHigh";
    String p4 = "ValueDistributeLow";
    String p5 = "ValueDistributeHigh";
    String p6 = "TupleDistributeLow";
    String p7 = "TupleDistributeHigh";
    int p2_length = 3;

    public GeneticAlgorithmTest() {
        // fixme (fixed) change length of chro
        super(24);
    }

    @Override
    /**
     * @Description: x的显示表示，将chro转换为用户需要的显示信息，只在print时候和calc时候调用，返回类型是double
     * 7个参数，一个length，6个low和high
     */
    public String changeX(Chromosome chro) {
        // TODO Auto-generated method stub
        boolean[] parameter1 = new boolean[p1_length];
        boolean[] parameter2 = new boolean[p2_length];
        boolean[] parameter3 = new boolean[p2_length];
        boolean[] parameter4 = new boolean[p2_length];
        boolean[] parameter5 = new boolean[p2_length];
        boolean[] parameter6 = new boolean[p2_length];
        boolean[] parameter7 = new boolean[p2_length];
        arraycopy(chro.gene,0,parameter1,0,p1_length);
        arraycopy(chro.gene,p1_length,parameter2,0,p2_length);
        arraycopy(chro.gene,p1_length,parameter3,0,p2_length);
        arraycopy(chro.gene,p1_length,parameter4,0,p2_length);
        arraycopy(chro.gene,p1_length,parameter5,0,p2_length);
        arraycopy(chro.gene,p1_length,parameter6,0,p2_length);
        arraycopy(chro.gene,p1_length,parameter7,0,p2_length);
        return p1 + ": " + String.valueOf(getPartNum(parameter1))
                + p2 + ": " + String.valueOf(getPartNum(parameter2))
                + p3 + ": " + String.valueOf(getPartNum(parameter3))
                + p4 + ": " + String.valueOf(getPartNum(parameter4))
                + p5 + ": " + String.valueOf(getPartNum(parameter5))
                + p6 + ": " + String.valueOf(getPartNum(parameter6))
                + p7 + ": " + String.valueOf(getPartNum(parameter7));


        // return String.valueOf((1.0 * chro.getNum() / NUM) * 100 + 6);
    }


    @Override
    /**
     * @Description: 设计评价函数,将changeX的参数带入模型中训练，根据训练结果计算估价函数
     * todo
     */
    public double calculateY(Chromosome chro) {
        // TODO Auto-generated method stub
        boolean[] parameter1 = new boolean[p1_length];
        boolean[] parameter2 = new boolean[p2_length];
        boolean[] parameter3 = new boolean[p2_length];
        boolean[] parameter4 = new boolean[p2_length];
        boolean[] parameter5 = new boolean[p2_length];
        boolean[] parameter6 = new boolean[p2_length];
        boolean[] parameter7 = new boolean[p2_length];
        arraycopy(chro.gene,0,parameter1,0,p1_length);
        arraycopy(chro.gene,p1_length,parameter2,0,p2_length);
        arraycopy(chro.gene,p1_length,parameter3,0,p2_length);
        arraycopy(chro.gene,p1_length,parameter4,0,p2_length);
        arraycopy(chro.gene,p1_length,parameter5,0,p2_length);
        arraycopy(chro.gene,p1_length,parameter6,0,p2_length);
        arraycopy(chro.gene,p1_length,parameter7,0,p2_length);
        NormalizeDistributeRunInGA obj = new NormalizeDistributeRunInGA();
        // trans what
        // fixme : parse x into several parts
        int length=getPartNum(parameter1);
        int useNum=20000;
        int AttrDistributeLow = getPartNum(parameter2);
        int AttrDistributeHigh = getPartNum(parameter3);
        int ValueDistributeLow = getPartNum(parameter4);
        int ValueDistributeHigh = getPartNum(parameter5);
        int TupleDistributeLow = getPartNum(parameter6);
        int TupleDistributeHigh = getPartNum(parameter7);
        // length, 三分/5分, sampleNum

        initialFileList();

        obj.trainWithPath(version,length,AttrDistributeLow,
        AttrDistributeHigh,
        ValueDistributeLow,
        ValueDistributeHigh,
        TupleDistributeLow,
        TupleDistributeHigh);
        // todo 根据参数返回结果，计算评价函数
        // 评价函数实现：
        double score = valueFunction();
        return 0;
//        return 100 - Math.log(Double.parseDouble(x));
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
    // 测试运行使用！！！
    @Test
    public void test() {
        GeneticAlgorithmTest test = new GeneticAlgorithmTest();
        test.calculate();
        test.getGeneI();
    }

    private double valueFunction(){

        return 0;
    }

    public void initialFileList(){

    }

}