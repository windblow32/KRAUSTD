package main.java.Embedding.EMBDI.GA;


import com.medallia.word2vec.Searcher;
import com.medallia.word2vec.Word2VecModel;
import main.java.CTD_Algorithm;
import main.java.Embedding.EMBDI.TripartiteNormalizeDistributeGraph.NormalizeDistributeRunInGA;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import main.java.Embedding.EMBDI.TripartiteGraphWithSource.RunInGA;
import static java.lang.System.arraycopy;
import static java.lang.System.err;

/**
 *@Description:
 */

public class GAImpl extends GeneticAlgorithm{

    public static final int NUM = 1 << 26;
    public int version;

    // 分批转化成不同的超参，带入评价函数中

    public String p1 = "length";
    public int p1_length = 6;
    public String p2 = "AttrDistributeLow";
    public String p3 = "AttrDistributeHigh";
    public String p4 = "ValueDistributeLow";
    public String p5 = "ValueDistributeHigh";
    public String p6 = "TupleDistributeLow";
    public String p7 = "TupleDistributeHigh";
    public String p8 = "DropSourceEdge";
    public String p9 = "DropSampleEdge";
    public int p8_length = 1;
    public int p2_length = 3;
    public int sourceNum = 55;
    // k是质优度的超参
    public int k = 7;
    // 存储Topk
    public List<Double> rmseList = new ArrayList<>();
    public double minRMSE = 10000;

    public GAImpl() {
        // fixme (fixed) change length of chro
        super(26);
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
        boolean[] parameter8 = new boolean[p8_length];
        boolean[] parameter9 = new boolean[p8_length];
        arraycopy(chro.gene,0,parameter1,0,p1_length);
        arraycopy(chro.gene,p1_length,parameter2,0,p2_length);
        arraycopy(chro.gene,p1_length*2,parameter3,0,p2_length);
        arraycopy(chro.gene,p1_length*3,parameter4,0,p2_length);
        arraycopy(chro.gene,p1_length*4,parameter5,0,p2_length);
        arraycopy(chro.gene,p1_length*5,parameter6,0,p2_length);
        arraycopy(chro.gene,p1_length*6,parameter7,0,p2_length);
        arraycopy(chro.gene,p1_length*7,parameter8,0,p8_length);
        arraycopy(chro.gene,p1_length*7+p8_length,parameter9,0,p8_length);
        return p1 + ": " + String.valueOf(getPartNum(parameter1))
                + p2 + ": " + String.valueOf(getPartNum(parameter2))
                + p3 + ": " + String.valueOf(getPartNum(parameter3))
                + p4 + ": " + String.valueOf(getPartNum(parameter4))
                + p5 + ": " + String.valueOf(getPartNum(parameter5))
                + p6 + ": " + String.valueOf(getPartNum(parameter6))
                + p7 + ": " + String.valueOf(getPartNum(parameter7))
                + p8 + ": " + String.valueOf(getPartNum(parameter8))
                + p9 + ": " + String.valueOf(getPartNum(parameter9));



        // return String.valueOf((1.0 * chro.getNum() / NUM) * 100 + 6);
    }


    @Override
    /**
     * @Description: 设计评价函数,将changeX的参数带入模型中训练，根据训练结果计算估价函数
     * todo
     */
    public double calculateY(Chromosome chro) {

        // fixme : 父类的public变量，子类直接使用，数值会随着父类变化吗
        version = generation;

        // TODO Auto-generated method stub
        boolean[] parameter1 = new boolean[p1_length];
        boolean[] parameter2 = new boolean[p2_length];
        boolean[] parameter3 = new boolean[p2_length];
        boolean[] parameter4 = new boolean[p2_length];
        boolean[] parameter5 = new boolean[p2_length];
        boolean[] parameter6 = new boolean[p2_length];
        boolean[] parameter7 = new boolean[p2_length];
        boolean[] parameter8 = new boolean[p8_length];
        boolean[] parameter9 = new boolean[p8_length];
        arraycopy(chro.gene,0,parameter1,0,p1_length);
        arraycopy(chro.gene,p1_length,parameter2,0,p2_length);
        arraycopy(chro.gene,p1_length*2,parameter3,0,p2_length);
        arraycopy(chro.gene,p1_length*3,parameter4,0,p2_length);
        arraycopy(chro.gene,p1_length*4,parameter5,0,p2_length);
        arraycopy(chro.gene,p1_length*5,parameter6,0,p2_length);
        arraycopy(chro.gene,p1_length*6,parameter7,0,p2_length);
        arraycopy(chro.gene,p1_length*7,parameter8,0,p8_length);
        arraycopy(chro.gene,p1_length*7+p8_length,parameter9,0,p8_length);
//        NormalizeDistributeRunInGA obj = new NormalizeDistributeRunInGA();
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
        int dropSourceEdge = getPartNum(parameter8);
        int dropSampleEdge = getPartNum(parameter9);
//        obj.trainWithPath(version,length,AttrDistributeLow,
//                AttrDistributeHigh,
//                ValueDistributeLow,
//                ValueDistributeHigh,
//                TupleDistributeLow,
//                TupleDistributeHigh);
        CTD_Algorithm CtdService = new CTD_Algorithm();
        // 数据集列表
        List<String> fileList = new ArrayList<>();
        fileList = initialFileList();
        // 否定约束
        List<String> DCs = new ArrayList<>();
        DCs = initialDC();
        // CTD返回的source weight
        List<Double> weightList = new ArrayList<>();
        weightList = CtdService.update(version,fileList,sourceNum,DCs,"THREE",length,AttrDistributeLow,
                AttrDistributeHigh,
                ValueDistributeLow,
                ValueDistributeHigh,
                TupleDistributeLow,
                TupleDistributeHigh,
                dropSourceEdge,
                dropSampleEdge);

        String[][] calcTruth =  CtdService.getCalcTruth();
        // calcTruth和真值求RMSE
        // golden standard读取
        int D1 = CtdService.getD1();
        int D2 = CtdService.getD2();
        String[][] goldenStandard = readGoldStandard(D1,D2);
        double RMSEScore = RMSE(calcTruth,goldenStandard,D1,D2);
        if(rmseList.size()<k){
            rmseList.add(RMSEScore);
            if(RMSEScore<minRMSE){
                minRMSE = RMSEScore;
            }
        }
        else {
            // 保留TopK最小的
            rmseList.add(RMSEScore);
            rmseList.remove(Collections.max(rmseList));
            // 升序,rmse小的，返回的index小，评估的分数就小，适应度下降，不合理
//            Collections.sort(rmseList);
            // 降序，rmse小的，index大，评估分数大，适应度高
            Collections.reverse(rmseList);
            Word2VecModel m = CtdService.getTriModel();
            try{
                int rank = rmseList.indexOf(RMSEScore);
                int Qi = rank/(k+1);
                int B_sum = 0;
                for(int s1 = 0;s1<sourceNum;s1++){
                    for(int s2 = s1 + 1;s2<sourceNum;s2++){
                        // s1与s2的weight
                        String sourceP = "source_"+s1;
                        String sourceQ = "source_"+s2;
                        double detaSimilarity = Math.abs(distanceUseSavedModel(m,sourceP,sourceQ));
                        double detaWeight = Math.abs(weightList.get(s1)-weightList.get(s2));
                        B_sum += Math.abs(detaSimilarity-detaWeight);
                    }
                }
                return (double)Qi/B_sum;

            }catch (NullPointerException e){
                // 被抛弃了，排序很低
                return 0;
            }
        }

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
//    GeneticAlgorithmTest test = new GeneticAlgorithmTest();
//        test.calculate();
//        test.getGeneI();

    private double valueFunction(){

        return 0;
    }

    public List<String> initialFileList(){
        List<String> fileList = new ArrayList<>();
        for (int i = 0; i < sourceNum; i++) {
            int temp = i + 1;
            String filePath = "data/stock100/divideSource/source" + temp + ".csv";
            fileList.add(filePath);
        }
        // 真值添加
        return fileList;
    }
    public List<String> initialDC(){
        List<String> DCs = new ArrayList<>();
        DCs.add("52wk_H > 52wk_L");
        return DCs;
    }
    public String[][] readGoldStandard(int D1, int D2){
        String[][] goldenStandard = new String[D1][D2];
        String goldenStandardPath = "data/stock100/100truth.csv";
        try {
            FileReader fr = new FileReader(goldenStandardPath);
            BufferedReader br = new BufferedReader(fr);
            String str = null;
            String[] data = null;
            int row = 0;
            br.readLine();
            while ((str = br.readLine())!=null){
                // data长度不足
                data = str.split(",",-1);
                System.arraycopy(data,0,goldenStandard[row],0,D2);
                row++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return goldenStandard;

    }
    public double RMSE(String[][] calcTruth, String[][] goldenStandard,int D1,int D2){
        double sum = 0;
        for(int i = 0;i<D1;i++){
            for(int j = 0;j<D2;j++){
                try{
                    double v1 = Double.parseDouble(calcTruth[i][j]);
                    double v2 = Double.parseDouble(goldenStandard[i][j]);
                    sum += Math.pow(Math.abs(v1-v2),2);
                }catch (NumberFormatException|NullPointerException e){
                    // fixme 异常处理为0是否合理
                    sum += 0;
                }
            }
        }
        sum = sum/(D1*D2);
        sum = Math.sqrt(sum);
        return sum;
    }
    public double distanceUseSavedModel(Word2VecModel model, String s1, String s2){
        Searcher search = model.forSearch();
        double d = 0;
        try {
            d = search.cosineDistance(s1, s2);
            List<Double> s1List = search.getRawVector(s1);
            List<Double> s2List = search.getRawVector(s2);
            double total1 = 0;
            for(double s:s1List){
                total1 += s*s;
            }
            double model1 = Math.sqrt(total1);
            double total2 = 0;
            for(double s : s2List){
                total2 += s*s;
            }
            double model2 = Math.sqrt(total2);
            return d/(model1*model2);
        } catch (Searcher.UnknownWordException e) {
//            e.printStackTrace();
            System.out.println("word not find");
            return 0;
        }

    }

}