package main.java.embdiTest.CTD.embdi_glove;


import com.google.common.collect.ImmutableList;
import com.medallia.word2vec.Searcher;
import com.medallia.word2vec.Word2VecModel;
import main.java.Embedding.EMBDI.SourceEmbedding.SourceEmbeddingViaWord2Vec;
import main.java.Embedding.EMBDI.TripartiteNormalizeDistributeGraph.NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec;
import main.java.SimilarityUtils;

import java.io.*;
import java.text.DecimalFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static java.lang.System.exit;

public class CTD_Algorithm_embdi_glove {

    // 定义收敛区间,或者记录下两次error差距不超过百分之多少，就不追究了
    public int version = 1;
    // attention : change source
    public String dataPath;
    public int existDA;
    private final double min_error = 1;
    // fixme : 属性数
    public int D2;
    // fixme : change source , biaozhushu
    public int biaozhushu;
    private final List<Double> weights = new ArrayList<>();
    private final List<String[]> processed_DC = new ArrayList<>();
    // 测试版本，embedding保存文件使用
    // flag标志矩阵数值是否改变，作用于distance函数中，模型是否重新训练
    public int flag = 0;
    // pass rmse to GA
    public double rmseForGA = 0;
    public String[][] calcTruth = null;
    // 如果是0，就运行最原始ctd算法
    public int CTD_sotaFlag = 1;
    // word2VecModel, change every time value update
    public Word2VecModel TriModel;
    public int v;
    public double initFitnessScore;
    public int isDA = 0;
    // fixme : 原始ctd把下列参数置为0
    public int useTriModel = 1;
    // fixme : ctd迭代次数，属于先验知识
    public int time = 5;
    // L行tuple
    private int L;
    // 二维数组repaired table无法初始化，在算法中返回即可。
    // p种attr
    private int p;
    // num of source
    private int k;
    private List<String> attributes = new ArrayList<>();
    // fixme : change source da num
    private int zengqiangshu;
    public String timeBeforeWalk;
    public String timeAfterWalk;
    public String timeAfterTrain;
    public String timeAfterDis1;
    public String timeAfterDis2;
    public List<Integer> versionList;
    private final List<Integer> numList = new ArrayList<>();
    public PrintStream out = System.out;
    private void initNumList(){
        if(dataPath.equals("data/monitor0707")){
            numList.add(4);
            numList.add(5);
            numList.add(6);
        }else if(dataPath.equals("data/camera0707")){
            numList.add(4);
            numList.add(2);
        }
    }
    public static boolean deleteWithPath(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            // file not exist
            System.out.println("safe, graphFile is not exist");
            return false;
        } else {
            if (file.exists() && file.isFile()) {
                // file exist
                if (file.delete()) {
                    System.out.println("delete graph succeed");
                    return true;
                } else {
                    System.out.println("graph delete failed");
                    return false;
                }
            } else {
                System.out.println("input graphPath error!");
                return false;
            }
        }
    }

    /**
     * 把图表示学习的参数和CTD算法的参数放一起了
     *
     * @param version             遗传算法代数
     * @param files               读取文件的列表，data from K sources
     * @param k                   数据源个数
     * @param DCs                 a piece of a set of ∑ of DCs    (fai)
     * @param mode                运行模式，三分图/五分图。实验直接设置为“THREE”即可，不需要特质的五分图了
     * @param length              游走长度
     * @param AttrDistributeLow   属性正态分布均值
     * @param AttrDistributeHigh  属性正态分布标准差
     * @param ValueDistributeLow  属性值正态分布均值
     * @param ValueDistributeHigh 属性值正态分布标准差
     * @param TupleDistributeLow  样本正态分布均值
     * @param TupleDistributeHigh 样本正态分布标准差
     * @param dropSourceEdge      是否drop和SOURCE连接的边，取值含义看图构建部分
     * @param dropSampleEdge      是否drop和SAMPLE连接的边
    //     * @param rmsePrinter         帮助GA打印rmse
    //     * @param r2_squarePrinter    帮助GA打印r2
    //     * @param fitScore            打印上次超参的适应度
    //     * @param extractedCTD_RMSE   打印上一次抽取数据的rmse
     * @return weight of source
     */
    public List<Double> update(
            int biaozhushu,
            int zengqiangshu,
            String dataPath,
            int existDA,
            int version,
            List<String> files,
            int k,
            List<String> DCs,
            String mode,
            int length,
            int AttrDistributeLow,
            int AttrDistributeHigh,
            int ValueDistributeLow,
            int ValueDistributeHigh,
            int TupleDistributeLow,
            int TupleDistributeHigh,
            int dropSourceEdge,
            int dropSampleEdge,
            int isCBOW,
            int dim,
            int windowSize,
            List<Integer> versionList,
            int isDA,
            int useTriModel,
            Map<String, float[]> fastText
    ) {
        this.biaozhushu = biaozhushu;
        this.zengqiangshu = zengqiangshu;
        this.existDA = existDA;
        this.dataPath = dataPath;
        // fixme : brm
        this.version = version;
        this.isDA = isDA;
        this.v = version;
        this.k = k; // this.k = files.size();
        this.useTriModel = useTriModel;
        this.versionList = versionList;
        // fixme : change source 替换成sourceDA中任意一个文件path
        String daTF = dataPath + "/sourceDA/source1.csv";
        File fileTF = new File(daTF);
        if(fileTF.exists()){
            initDATupleList(daTF);
        }
        initNumList();

        // double输出限制
        DecimalFormat df = new DecimalFormat("#.00");

        for (String dc : DCs) {
            String[] split_DC = dc.split(" ");
            processed_DC.add(split_DC);
        }

        for (int wi = 0; wi < k; wi++) {
            weights.add(1.0);
        }
        // 获取L,p,attributes，用于初始化value，result，pre_result数组
        String first_file = files.get(0);
        try {
            FileReader fd = new FileReader(first_file);
            BufferedReader br = new BufferedReader(fd);
            String str;
            // 先读取一行，作为属性
            str = br.readLine();
            String[] first_line = str.split(",", -1);
            p = first_line.length;
            D2 = p;
            List<String> temp = new ArrayList<>(Arrays.asList(first_line));
            attributes = new ArrayList<>(temp);
            int row = 0; //记录行数L
            // 余下的都是元组了
            while (br.readLine() != null) {
                row++;
            }
            L = row;
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Initialize
        // 下方初始化
        String[][][] value = new String[k][L][p];
        // value后循环初始化
        String[][] result = new String[L][p];
        // 一定需要初始化，否则evaluate返回0
        String[][] pre_result = new String[L][p];
        // 初始化pre_result
        // 用于循环的变量就随便起了233
        for (int x = 0; x < L; x++) {
            for (int c = 0; c < p; c++) {
                pre_result[x][c] = "0";
            }
        }

        // 将数据存入value数组
        int y = 0;
        // 初始化value
        for (String fileAddress : files) {
            // 根据路径获取文件file,读取获得行数L,属性数p,填充attributes
            try {
                FileReader fd = new FileReader(fileAddress);
                BufferedReader br = new BufferedReader(fd);
                String str;
                // 先读取一行，作为属性
                str = br.readLine();
                String[] first_line = str.split(",", -1);
                if (p != first_line.length) {
                    System.out.println(fileAddress + " 属性数量不匹配");
                }

                List<String> temp = new ArrayList<>(Arrays.asList(first_line));
                if (!attributes.equals(temp)) {
                    System.out.println(fileAddress + " 属性名称不匹配");
                }
                int row = 0; //记录行数L
                // 余下的都是元组了
                while ((str = br.readLine()) != null) {
                    String[] data = str.split(",", -1);
                    try {
                        System.arraycopy(data, 0, value[y][row], 0, data.length);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println("越界 : " + y);
                        System.out.println(row);
                    }
                    row++;
                }
                if (L != row) {
                    System.out.println(fileAddress + " 元组数量不匹配");
                }
                y++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 初始化result
        for (int i = 0; i < L; i++) {
            for (int j = 0; j < p; j++) {
                int before_judge = 0;
                // 直到找到一个不是空的value，然后判断是否是连续值
                while (value[before_judge][i][j] == null) {
                    before_judge++;
                }
                boolean num_or_String = judge_number(value[before_judge][i][j]);
                // (25) finished : judge continuous data, continuous assure value[source][i][j] is double type to be parsed
                if (num_or_String) {
                    int source = 0; // 记录当前是第几个数据源
                    // 记录（25）中的分子
                    double sum_weighted_value = 0;
                    // 记录（25）中的分母
                    double sum_weight = 0;
                    for (double w1 : weights) {
                        try {
                            if (!value[source][i][j].isEmpty()) {
                                sum_weighted_value +=
                                        w1 * Double.parseDouble(value[source][i][j]);
                            }
                            sum_weight += w1;
                        } catch (NullPointerException e) {
                            sum_weight += 0;
                        }

                        source++;
                    }
                    // store v[l][p]*
                    result[i][j] = String.valueOf(sum_weighted_value / sum_weight);
                } else {
                    // finish todo : judge categorical data, categorical is defined as String attr
                    // double[] temp = new double[k];
                    // max
                    double max = 0;
                    // 修复后v的取值，只可能是现有的第j列中的一个
                    String v = null;
                    for (int t = 0; t < k; t++) {
                        // 对当前lpk（t）进行存储，h函数的参数2
                        //                            String temp = value[t][i][j];
                        // 应该修复为对于当前的数据集的行数，进行查找, p is waited to fix
                        for (int row = 0; row < L; row++) {
                            // 用于计算h函数的参数1，来自数据源t的第row行的j列属性
                            String parma1 = value[t][row][j];
                            int num = 0; //当前序号,数据源标记,相当于公式中的k
                            double sum_categorical = 0; //  暂时保存总和
                            // 这里weight的size应该和k相等
                            for (double w2 : weights) {
                                try{
                                    sum_categorical += w2 * h(parma1, value[num][i][j]);
                                }catch (ArrayIndexOutOfBoundsException e4){
                                    System.out.println(num);
                                    System.out.println(i);
                                    System.out.println(j);
                                    exit(500);
                                }
                                num++;
                            }
                            // 更新max
                            if (sum_categorical > max) {
                                max = sum_categorical;
                                v = parma1;
                            }
                        }
                    }
                    // store in result temporarily
                    result[i][j] = v;
                }
            }
        }

        // 利用SimilarityUtils类，进行矩阵中字符串的比较
        SimilarityUtils sim = new SimilarityUtils();
        double error = 100;
        // 记录result文件的个数
        int times = 0;
        double goal = 0;
        while (times<time){
            for (int i = 0; i < L; i++) {
                for (int j = 0; j < p; j++) {
                    int before_judge = 0;
                    // 直到找到一个不是空的value，然后判断是否是连续值
                    while (value[before_judge][i][j] == null) {
                        before_judge++;
                    }
                    boolean num_or_String = judge_number(value[before_judge][i][j]);
                    // (25) judge continuous data, continuous assure value[source][i][j] is double type to be parsed
                    if (num_or_String) {
                        int source = 0; // 记录当前是第几个数据源
                        // 记录（25）中的分子
                        double sum_weighted_value = 0;
                        // 记录（25）中的分母
                        double sum_weight = 0;
                        for (double w1 : weights) {
                            try {
                                if (!value[source][i][j].isEmpty() || !value[source][i][j].equals("")) {
                                    sum_weighted_value +=
                                            w1 * Double.parseDouble(value[source][i][j]);
                                }
                            } catch (NullPointerException e) {
                                int a = 0;
                            }

                            sum_weight += w1;
                            source++;
                        }
                        // store v[l][p]*
                        result[i][j] = String.valueOf(sum_weighted_value / sum_weight);
                    } else {
                        // judge categorical data, categorical is defined as String attr
                        // double[] temp = new double[k];
                        // max
                        double max = 0;
                        // 修复后v的取值，只可能是现有的第j列中的一个
                        String v = null;
                        for (int t = 0; t < k; t++) {
                            // 对当前lpk（t）进行存储，h函数的参数2
                            //                            String temp = value[t][i][j];
                            // 应该修复为对于当前的数据集的行数，进行查找, p is waited to fix
                            // 用于计算h函数的参数1，来自数据源t的第row行的j列属性
                            String parma1 = value[t][i][j];
                            int num = 0; //当前序号,数据源标记,相当于公式中的k
                            double sum_categorical = 0; //  暂时保存总和
                            // 这里weight的size应该和k相等
                            for (double w2 : weights) {
                                sum_categorical += w2 * h(parma1, value[num][i][j]);
                                num++;
                            }
                            // 更新max
                            if (sum_categorical > max) {
                                max = sum_categorical;
                                v = parma1;
                            }
                        }
                        // store in result temporarily
                        result[i][j] = v;
                    }

                    // judge satisfy (20) or (31)
                    boolean judge;
                    /*
                    用于在judge部分应用不同的update方法！
                    1, 2, 3为single entity
                    1代表 code < 10;
                    2代表 code < zipcode
                    3代表前两种的混合情况, 如果flag被先后赋值1，2或者2，1,就将其赋值为3
                    4代表 multi entity
                     */
                    // int flag = 0;
                    int find_count;
                    // 用于发现multi entity，跳出循环标志
                    int multi_flag = 0;
                    for (int l = 0; l < L; l++) {
                        if (multi_flag == 1) {
                            break;
                        }
                        int sum = 0; //记录每个元组经过f计算后的数值
                        for (String[] dc : processed_DC) {
                            // 记录需要查找的属性数量
                            // 1代表 code < 10 类型的DC
                            find_count = 1;
                            // attribute judge
                            // 当前开头不是属性开头时，默认是多实体间的DC,以tuple开头
                            if (!attributes.contains(dc[0])) {
                                // 注意，这里认为多实体约束和单实体约束是分开的，不能混合使用！
                                multi_flag = 1;
                                // 完全托付给外部了
                                int index = processed_DC.indexOf(dc);
                                List<String[]> multi_entity_dc = new ArrayList<>();
                                for (int now = index; now < processed_DC.size(); now++) {
                                    multi_entity_dc.add(processed_DC.get(now));
                                }
                                multi_entity(multi_entity_dc, result, value);
                                break;
                            }
                            if (attributes.contains(dc[2])) {
                                // the third word is also attribute, such as "avg < total"
                                find_count = 2;
                            }
                            // 将指定了的find_sort放入列表中

                            int attr_index = 0; // 记录第一个属性的列index
                            int attr_index2 = 0; // 记录第二个属性的列index
                            int index = 0; // 记录当前index
                            // 找对应的attr
                            for (String attr : attributes) {
                                if (attr.equals(dc[0])) {
                                    attr_index = index;
                                }
                                if (find_count == 2 && attr.equals(dc[2])) {
                                    attr_index2 = index;
                                }
                                index++;
                            }
                            // find value:
                            String v1;
                            String v2;
                            v1 = result[l][attr_index];
                            if (find_count == 1) {
                                // example : code < 10
                                v2 = dc[2];
                            } else {
                                // example : code < zipcode
                                v2 = result[l][attr_index2];
                            }
                            // sign
                            String option = dc[1];
                            // calc constraint f:
                            int f_res = f_constraints(v1, v2, option, "single");
                            sum += -f_res;
                            if (1 - sum - DCs.size() <= 0) {
                                judge = true;
                            } else {
                                // attention : 是否确实违反
                                System.out.println("(20) is not satisfied");
                                // 声明条件为假
                                judge = false;
                            }
                            if (v1 == null || v2 == null) {
                                exit(-15);
                            }
                            // 如果result中出现 空值，说明所有数据源提供的都是空值，那就是空值
                            if (v1.equals("") || v2.equals("")) {
                                continue;
                            }

                            // judge 不成立并且v1，v2都是连续值时：
                            if (!judge && judge_number(v1) && judge_number(v2)) {
                                double v1_value = Double.parseDouble(v1);
                                double v2_value = Double.parseDouble(v2);
                                // update v.lp according to (27)
                                // 采用(28)开始的公式对于上述中存在问题的数据进行捕获与修复
                                // 满足拉格朗日乘子为0前提下，求table X
                                switch (find_count) {
                                    case 2:
                                        // (28),看先碰到哪个属性，避免再次碰到再修复。对于连续值进行的修复
                                        double sum_28_up = 0;
                                        for (int sor = 0; sor < k; sor++) {

                                            double p1 = 0;
                                            double p2 = 0;
                                            try {
                                                if (value[sor][l][attr_index2].equals("")) {
                                                    p1 = 0;
                                                } else {
                                                    p1 = Double.parseDouble(value[sor][l][attr_index2]);
                                                }
                                            } catch (NullPointerException e) {
                                                p1 = 0;
                                            }
                                            try {
                                                if (value[sor][l][attr_index].equals("")) {
                                                    p2 = 0;
                                                } else {
                                                    p2 = Double.parseDouble(value[sor][l][attr_index]);
                                                }
                                            } catch (NullPointerException e) {
                                                p2 = 0;
                                            }
                                            sum_28_up +=
                                                    weights.get(sor) *
                                                            (
                                                                    p1 + p2
                                                            );
                                        }
                                        double sum_28_down = 0;
                                        for (double wk : weights) {
                                            sum_28_down += wk;
                                        }
                                        if (sum_28_down == 0) {
                                            sum_28_down = 1;
                                        }
                                        if (attr_index > attr_index2) {
                                            // n 小，n先遇到被修复
                                            result[l][attr_index2] =
                                                    String.valueOf(df.format(sum_28_up / (sum_28_down * 2)));
                                        } else if (attr_index < attr_index2) {
                                            result[l][attr_index] =
                                                    String.valueOf(df.format(sum_28_up / (sum_28_down * 2)));
                                        }
                                        break;
                                    case 1:
                                        // (29)
                                        if (v1_value < v2_value) {
                                            result[l][attr_index] = String.valueOf(df.format(v2_value));
                                        }
                                        break;
                                }
                            }
                        }
                    }
                }
            }
            // 至此,更新结束
            try {
                writeValue(value);
                writeResult(result, times);
                //
                // 1为仅仅CTD自身
//                if (times % time == 0 && times != 0) {
//                    calcTruth = result;
//                    return weights;
//                }
            } catch (IOException e) {
                e.printStackTrace();
            }
//            if (times % time == 0 && times != 0) {
//                calcTruth = result;
//                return weights;
//            }
            // update weight of kth source w.k by (17)
            double up = 0; // 分子
            double down; //分母
            // calculate up,必须分开计算，因为分子是三重循环的总和，定值
            // finished : matrix数值此后不改变，distance中模型训练一次保存后，不用改变了
            flag = 0;
            for (int s = 0; s < k; s++) {
                for (int l = 0; l < L; l++) {
                    for (int col = 0; col < p; col++) {
                        double r = distance(result[l][col], value[s][l][col], mode, times,
                                length, AttrDistributeLow, AttrDistributeHigh,
                                ValueDistributeLow, ValueDistributeHigh, TupleDistributeLow,
                                TupleDistributeHigh, dropSourceEdge, dropSampleEdge, isCBOW, dim, windowSize);
                        if (r == -100) {
                            calcTruth = result;
                            return weights;
                        }
                        up += r;
                        // flag = 1代表数值不变
                        flag = 1;
                    }
                }
            }
            // after calc distance time
            LocalTime now = LocalTime.now();
            DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("HH:mm:ss");
            timeAfterDis1 = now.format(formatter1);
            // calculate wk
            for (int s = 0; s < k; s++) {
                down = 0;
                for (int l = 0; l < L; l++) {
                    for (int col = 0; col < p; col++) {
                        double r2 = distance(result[l][col], value[s][l][col], mode, times,
                                length, AttrDistributeLow, AttrDistributeHigh,
                                ValueDistributeLow, ValueDistributeHigh,
                                TupleDistributeLow, TupleDistributeHigh,
                                dropSourceEdge, dropSampleEdge, isCBOW, dim, windowSize);
                        if (r2 == -100) {
                            calcTruth = result;
                            return weights;
                        }
                        down += r2;
                    }
                }
                // calculate nature log
                double wk = Math.log1p(up / down);
                // weight无穷大,可能与数据缺失相关
                if (!Double.isFinite(wk) || Double.isNaN(wk)) {
                    weights.set(s, 3.0);
                } else {
                    weights.set(s, wk);
                }
            }
            timeAfterDis2 = now.format(formatter1);
            flag = 0;
            // 计算距离用到result的路径，其中有times作为标志，因而在两次distance计算过后再增加times
            times++;
            // 重置error
            error = 0;
            for (int q = 0; q < L; q++) {
                for (int w = 0; w < p; w++) {
                    // attention : 数字类型用rel-diff
                    if(numList.contains(w)){
                        // numerical data use abs normal
                        error += sim.abs_normal(result[q][w],pre_result[q][w]);
                    }else error += sim.levenshtein(result[q][w], pre_result[q][w]);
                }
            }
            System.out.println("error is " + error);
            System.out.println("distanceSum is " + up);
            pre_result = result;

        }
        // attention : if only ctd, dont need under
        if (isDA == 0) {
            initFitnessScore = calcInitFitnessScore(times, fastText);
        }

        calcTruth = result;
        return weights;
    }

    /**
     * @param v1 待比较的第一个字符串
     * @param v2 待比较的另一个字符串
     * @return 相等返回1，否则返回0
     */
    private int h(String v1, String v2) {
        if (v1 == null || v2 == null || v1.equals("") || v2.equals("")) {
            return 0;
        }
        if (judge_number(v1) && judge_number(v2)) {
            double value1 = Double.parseDouble(v1);
            double value2 = Double.parseDouble(v2);
            if (Math.abs((value2 - value1)) < 0.001) {
                return 1;
            } else return 0;
        } else {
            if (v1.equals(v2)) return 1;
            else return 0;
        }

    }

    /**
     * 根据不同的sgn函数以及不同的反转运算符，计算函数f的数值
     *
     * @param v1     v(im)
     * @param v2     r(v(im))
     * @param option signal, should be inverse
     * @param type   "single" or "multi"
     * @return function f
     */
    private int f_constraints(String v1, String v2, String option, String type) {
        if (v1 == null || v2 == null || v1.equals("") || v2.equals("")) {
            return 0;
        }
        if (judge_number(v1) && judge_number(v2)) {
            double p1 = Double.parseDouble(v1);
            double p2 = Double.parseDouble(v2);
            switch (option) {
                case ">":
                    // inverse option is <=
                    return -sgn(p1 - p2);
                case "<":
                    // >=
                    return sgn(p1 - p2);
                case "=":
                    // !=
                    return sgn(Math.pow(p1 - p2, 2));
                case "!=":
                    return -sgn(Math.pow(p1 - p2, 2));
                case ">=":
                    return sgn(Math.pow(p1 - p2, 2)) - sgn(p1 - p2) - 1;
                case "<=":
                    return sgn(Math.pow(p1 - p2, 2)) + sgn(p1 - p2) - 1;
            }
        } else {
            // 输入的是字符串
            switch (option) {
                case "=":
                    if (v1.equals(v2)) {
                        return 0;
                    } else return 1;
                case "!=":
                    if (v1.equals(v2)) {
                        return 1;
                    } else return 0;
            }
        }

        return 1;
    }

    private int sgn(double x) {
        if (x > 0) {
            return 1;
        } else if (x == 0) {
            return 0;
        } else {
            return -1;
        }
    }

    /**
     * 注意用到的数据都是来自数据集成后的result数组中的
     *
     * @param pro_DC 输入的约束，对于多实体，规定约束形式如下: Tuple i attr(p) > tuple j attr(q)
     *               dc[1]代表第i个元组, dc[2]代表属性p，dc[3]是比较符号 ,dc[5]是第j个元组，dc[6]是属性q
     * @param result 修复后的value集合
     */
    private void multi_entity(
            List<String[]> pro_DC,
            String[][] result,
            String[][][] value
    ) {
        int sum = 0; // 记录总体的加和
        boolean judge;
        String v1 = null;
        String v2 = null;
        int row_v2 = 0;
        int row_v1 = 0;
        int attr1_index = -1;
        int attr2_index = -1;
        String option_sign = null;
        for (String[] dc : pro_DC) {
            row_v1 = Integer.parseInt(dc[1]);
            row_v2 = Integer.parseInt(dc[5]);
            if (row_v1 > L || row_v2 > L) {
                System.out.println("该元组不存在");
            }
            String attr1 = dc[2];
            String attr2 = dc[6];
            // 对于String类型的option，操作符仅有"="和"!="
            option_sign = dc[3];
            attr1_index = -1;
            attr2_index = -1;

            for (String attribute : attributes) {
                if (attribute.equals(attr1)) {
                    attr1_index = attributes.indexOf(attr1);
                }
                if (attribute.equals(attr2)) {
                    attr2_index = attributes.indexOf(attr2);
                }
            }
            if (attr1_index == -1 || attr2_index == -1) {
                System.out.println("DC 输入的属性不符合要求");
            }
            v1 = result[row_v1][attr1_index];
            v2 = result[row_v2][attr2_index];
            int res = f_constraints(v1, v2, option_sign, "multi");
            sum += res;
        }
        if (1 - sum - pro_DC.size() <= 0) {
            //            System.out.println("(31) is satisfied");
            judge = true;
        } else {
            System.out.println("(31) is not satisfied");
            // 声明条件为假
            judge = false;
        }
        if (
                option_sign != null &&
                        !judge &&
                        !judge_number(v1) &&
                        !judge_number(v2) &&
                        option_sign.equals("=")
        ) {
            // finished : update (35)
            double multi_sum1 = 0;
            for (int s1 = 0; s1 < k; s1++) {
                multi_sum1 +=
                        weights.get(s1) *
                                (
                                        h(result[row_v1][attr2_index], value[s1][row_v1][attr2_index]) +
                                                h(result[row_v1][attr2_index], value[s1][row_v2][attr2_index])
                                );
            }
            double multi_sum2 = 0;
            for (int s2 = 0; s2 < k; s2++) {
                multi_sum2 +=
                        weights.get(s2) *
                                (
                                        h(result[row_v2][attr2_index], value[s2][row_v1][attr2_index]) +
                                                h(result[row_v2][attr2_index], value[s2][row_v2][attr2_index])
                                );
            }
            if (multi_sum1 > multi_sum2) {
                result[row_v2][attr2_index] = result[row_v1][attr1_index];
            } else {
                result[row_v1][attr1_index] = result[row_v2][attr2_index];
            }
        } else if (option_sign == null) {
            System.out.println("option 为空");
        }
    }

    private boolean judge_number(String str) {
        String regex =
                "^[+-]?([0-9]*\\.?[0-9]+|[0-9]+\\.?[0-9]*)([eE][+-]?[0-9]+)?$";
        return Pattern.matches(regex, str);
    }

    /**
     * 利用embedding计算两个字符串之间的距离，引入EMBDI
     *
     * @param v1         字符串1，来自result
     * @param v2         字符串2，来自value
     * @param mode       表明使用三分图或者五分图进行distance计算
     * @param times      记录着result文件的个数(从0开始)，
     * @param isCBOW     为1则采用CBOW，否则v0采用sg
     * @param dim        embedding 维度
     * @param windowSize 窗口大小
     * @return 返回两个字符串的欧式距离
     */
    private double distance(String v1, String v2, String mode, int times,
                            int length, int AttrDistributeLow,
                            int AttrDistributeHigh,
                            int ValueDistributeLow,
                            int ValueDistributeHigh,
                            int TupleDistributeLow,
                            int TupleDistributeHigh,
                            int dropSourceEdge,
                            int dropSampleEdge,
                            int isCBOW,
                            int dim,
                            int windowSize) {
        try {
            if (v1.equals("") || v2.equals("")) {
                return 1;
            }
        } catch (NullPointerException e) {
            return 1;
        }

        List<String> fileList = new ArrayList<>();

        // fixme : ctd-BRM_al
//        for (int i = 0; i < k; i++) {
//            // 注意和writeValue的路径相同
//            int z = i + 1;
//            String path;
//            path = "data/ctd/monitor/sourceNew/source" + z + ".csv";
//            fileList.add(path);
//        }
//        String resultPath;
//        resultPath = "data/ctd/monitor/result/result_" + v + "_" + times + ".csv";
//        fileList.add(resultPath);


        // 以下全是原来的
        for (int i = 0; i < k; i++) {
            // 注意和writeValue的路径相同
            int z = i + 1;
            String path;

            if(isDA == 0){
                path = dataPath + "/sourceNew/source" + z + ".csv";
            }else{
                path = dataPath + "/sourceNewDA/source" + z + ".csv";
            }
            fileList.add(path);
        }
        String resultPath;
        if(isDA == 0){
            // fixme : change source
            resultPath =
                    dataPath + "/result/result_" + v + "_" + times + ".csv";
        }else{
            resultPath =
                    dataPath + "/result/DAresult/result_" + 1 + "_" + times + ".csv";
        }
        // attention : tempDA path
        String tempDAFilePath = dataPath + "/result/DAresult/tempDA.csv";
        if (isDA == 0&&existDA==1&&flag==0) {
            // 拿到需要读入的数据
            // fixme : change source
            String DAresultPath = dataPath + "/result/DAresult/result_" + 1 + "_" + times + ".csv";
            File DAresult = new File(DAresultPath);
            try {
                FileReader fr = new FileReader(DAresult);
                BufferedReader br = new BufferedReader(fr);
                String str;
                String[] data;
                int line = 0;
                // attention : daResult 有attr
                while (line < zengqiangshu + 1) {
                    br.readLine();
                    line++;
                }

                File resultFile = new File(dataPath + "/result/result_" + v + "_" + times + ".csv");

                FileReader resultFr = new FileReader(resultFile);
                BufferedReader resultBr = new BufferedReader(resultFr);
                // read attr
                str = resultBr.readLine();

                File DAFile = new File(tempDAFilePath);
                DAFile.createNewFile();

                PrintStream ps = new PrintStream(DAFile);
                System.setOut(ps);
                // write attr
                System.out.println(str);
                // 从正常的result读取str输入到tempDA中，如果tuple在daTupleList中，就用这个替换
                while ((str = resultBr.readLine()) != null) {
                    data = str.split(",", -1);
                    if (daTupleList.contains(data[0])) {
                        // 需要被替换,因为元组是有序的，直接读取DAresult下一行就行
                        str = br.readLine();
                        System.out.println(str);
                    } else {
                        System.out.println(str);
                    }
                }
                resultBr.close();
                resultFr.close();
                br.close();
                fr.close();
                ps.close();
                System.setOut(out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(isDA == 0&&existDA==1){
            // 添加改动过的
            fileList.add(tempDAFilePath);
        }
        else{
            // 否则正常添加
            fileList.add(resultPath);
        }
        if (times % time == 0 && times != 0) {
            return -100;
        }
        if (mode.equals("THREE")) {
            // fixme : ctd-BRM_al
            if(version == 5){
                // 三分图
                SourceEmbeddingViaWord2Vec word2VecService = new SourceEmbeddingViaWord2Vec();

                if (CTD_sotaFlag == 0 && useTriModel == 0) {
                    int value1 = (int)Double.parseDouble(v1);
                    int value2 = (int)Double.parseDouble(v2);
                    if (value1 == value2) {
                        return 0;
                    } else return 1;
                } else if ((CTD_sotaFlag == 1 && flag == 0) ||(useTriModel==1 && flag == 0)){

                    String graphPath = "data/stock100/weightCalcByVex/graph/55SourceStockGraphMin.txt";
                    deleteWithPath(graphPath);
                    // GA传参数过来，CTD训练得到结果
                    // version.txt,length,6个参数
                    LocalTime time = LocalTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                    // 防止遗传算法因为图已经存储不能重新构造
                    String t = time.format(formatter);
                    String[] data = t.split(":");
                    String insertT = data[0] + data[1] + data[2];
                    String graphFilePath = "data/stock100/weightCalcByVex/graph/55SourceStockGraphMin" + v + "_" + insertT + ".txt";
                    // fixme : change source model path
                    String modelPath = "model/Tri/CTD/monitor/totalMin" + v + "_" + insertT + ".model";
                    word2VecService.train(fileList, graphFilePath, 3, 3, length);
                    TriModel = word2VecService.trainWithLocalWalks(modelPath);
                    // distanceUseSavedModel 计算的是相似度，需要处理
                    return 1 - word2VecService.distanceUseSavedModel(TriModel, v1, v2);
                } else if ((CTD_sotaFlag == 1 && flag == 1) ||(useTriModel==1 && flag == 1)) {
                    return 1 - word2VecService.distanceUseSavedModel(TriModel, v1, v2);
                }
            }else{
                // 三分图
                NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec word2VecService = new NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec();
                CTD_sotaFlag = versionList.get(versionList.size() - 1);
                // fixme : only ctd
//                CTD_sotaFlag = 0;
//                useTriModel = 0;
                if (CTD_sotaFlag == 0 && useTriModel == 0) {
                    if (judge_number(v1) && judge_number(v2)) {
                        int value1 = (int) Double.parseDouble(v1);
                        int value2 = (int) Double.parseDouble(v2);
                        if (value1 == value2) {
                            return 0;
                        } else return 1;
                    } else {
                        if (v1.equals(v2)) {
                            return 0;
                        } else return 1;
                    }

                } else if ((CTD_sotaFlag == 1 && flag == 0) || (useTriModel == 1 && flag == 0)) {

                    String graphPath = "data/stock100/weightCalcByVex/graph/55SourceStockGraphMin.txt";
                    deleteWithPath(graphPath);
                    // GA传参数过来，CTD训练得到结果
                    // version.txt,length,6个参数
                    LocalTime time = LocalTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                    // 防止遗传算法因为图已经存储不能重新构造
                    String t = time.format(formatter);
                    String[] data = t.split(":");
                    String insertT = data[0] + data[1] + data[2];
                    String graphFilePath = "data/stock100/weightCalcByVex/graph/55SourceStockGraphMin" + v + "_" + insertT + ".txt";
//                String modelPath = "model/Tri/CTD/weather/totalMin" + v + "_" + insertT + ".model";
                    // fixme : change source model path
                    String modelPath = "model/Tri/CTD/monitor/totalMin" + v + "_" + insertT + ".model";
                    // attention : debug, record time
                    LocalTime now = LocalTime.now();
                    DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("HH:mm:ss");
                    timeBeforeWalk = now.format(formatter1);
                    word2VecService.train(fileList, graphFilePath, 3, 3, length, 20000,
                            AttrDistributeLow, AttrDistributeHigh, ValueDistributeLow, ValueDistributeHigh,
                            TupleDistributeLow, TupleDistributeHigh, dropSourceEdge, dropSampleEdge, isCBOW, dim, windowSize);
                    now = LocalTime.now();
                    timeAfterWalk = now.format(formatter1);

                    TriModel = word2VecService.trainWithLocalWalks(modelPath);
                    now = LocalTime.now();
                    timeAfterTrain = now.format(formatter1);
                    // 恢复输出流
                    System.setOut(out);
                    // distanceUseSavedModel 计算的是相似度，需要处理
                    double res = 1 - word2VecService.distanceUseSavedModel(TriModel, v1, v2);
                    if(res > 0.65) return 1;
                    return res;
                } else if ((CTD_sotaFlag == 1 && flag == 1) || (useTriModel == 1 && flag == 1)) {
                    double res = 1 - word2VecService.distanceUseSavedModel(TriModel, v1, v2);
                    if(res > 0.65) return 1;
                    return res;
                }
            }




        } else {
            // mode not support!
            System.out.println("mode isn't supported!");
        }
        return -1;
    }

    private void writeValue(String[][][] value) throws IOException {
//    String[] header = new String[] { "time", "place", "city", "good" };
        // String[] header = new String[]{"sample", "change%", "last_trade_price", "open_price", "volumn", "today_high", "today_low", "previous_close", "52wk_H", "52wk_L"};
        // fixme : 更换整个数据集时，change source attr
        // entity,brand,screen_type,supported_aspect_ratio,response_time(ms),screen_size_diagonal,day
        String[] header = new String[]{"entity", "brand", "screen_type", "supported_aspect_ratio", "response_time(ms)", "screen_size_diagonal", "day"};
//        entity,Domain,Conditions,Humidity,day
//        String[] header = new String[]{"entity", "Domain", "Conditions", "Humidity", "day"};
        // camera
//        String[] header = new String[]{"entity", "brand", "num", "condition", "day"};
        String separator = ",";
        String sourcePath;
        for (int i = 0; i < k; i++) {
            int q = i + 1;
            if (isDA == 0) {
                // ctd
                // sourcePath = "E:\\GitHub\\KRAUSTD\\CTD\\data\\stock100\\divideSourceNew\\source" + q + ".csv";
                // weather
//                sourcePath = "data/ctd/weather/sourceNew/source" + q + ".csv";
                // monitor
                sourcePath = dataPath + "/sourceNew/source" + q + ".csv";
            } else {
                // ctd
//                sourcePath = "E:\\GitHub\\KRAUSTD\\CTD\\data\\stock100\\divideSourceNewDA\\source" + q + ".csv";
                // weather
//                sourcePath = "data/ctd/weather/sourceNewDA/source" + q + ".csv";
                sourcePath = dataPath + "/sourceNewDA/source" + q + ".csv";

            }

            PrintStream stream = new PrintStream(sourcePath);

            for (int e = 0; e < header.length - 1; e++) {
                //                bw.write(header[e] + separator);
                stream.print(header[e] + separator);
            }
            //            bw.write(header[header.length-1] + "\n");
            stream.print(header[header.length - 1] + "\n");
            for (int b = 0; b < L; b++) {
                int j;
                for (j = 0; j < p - 1; j++) {
                    // 除了最后一个的直接输入
                    //                    bw.write(value[i][k][j] + separator);
                    stream.print(value[i][b][j] + separator);
                }
                //                bw.write(value[i][k][j] + "\n");
                stream.print(value[i][b][j] + "\n");
            }
            //            bw.close();
            stream.close();
        }
    }

    private void writeResult(String[][] result, int times) {
        // attention : header数组存储了每个属性的名字，按照数据集中source文件夹下任意一个文件的第一行填写就行
        // stock
//        String[] header = new String[]{"sample", "change%", "last_trade_price", "open_price", "volumn", "today_high", "today_low", "previous_close", "52wk_H", "52wk_L"};
        // monitor
        String[] header = new String[]{"entity", "brand", "screen_type", "supported_aspect_ratio", "response_time(ms)", "screen_size_diagonal", "day"};
        // weather|
        // entity,Domain,Conditions,Humidity,day
//        String[] header = new String[]{"entity", "brand", "num", "condition", "day"};
//        String[] header = new String[]{"entity", "Domain", "Conditions", "Humidity", "day"};

//        String[] header = new String[]{"entity", "Domain", "Conditions", "Humidity", "day"};
        String separator = ",";
        String sourcePath;
//        if (isDA == 0) {
//            // ctd
////            sourcePath =
////                    "E:\\GitHub\\KRAUSTD\\CTD\\data\\stock100\\result\\result_" + v + "_" + times + ".csv";
//            // monitor
//            sourcePath = "data/ctd/weather/result/result_" + v + "_" + times + ".csv";
//        } else {
//            // ctd
////            sourcePath =
////                    "E:\\GitHub\\KRAUSTD\\CTD\\data\\stock100\\result\\DAresult\\result_" + v + "_" + times + ".csv";
//            // monitor
//            sourcePath = "data/ctd/weather/result/DAresult/result_" + 1 + "_" + times + ".csv";
//
//        }
        if(isDA == 0){
            // ctd
//            sourcePath =
//                    "E:\\GitHub\\KRAUSTD\\CTD\\data\\stock100\\result\\result_" + v + "_" + times + ".csv";
            // monitor
            // attention : result path, use monitor/camera 替换 weather
            sourcePath = dataPath + "/result/result_" + v + "_" + times + ".csv";
        }else{
            // ctd
//            sourcePath =
//                    "E:\\GitHub\\KRAUSTD\\CTD\\data\\stock100\\result\\DAresult\\result_" + v + "_" + times + ".csv";
            // monitor
            sourcePath = dataPath + "/result/DAresult/result_" + 1 + "_" + times + ".csv";
        }


        PrintStream stream = null;
        try {
            File file = new File(sourcePath);
            file.createNewFile();
            stream = new PrintStream(sourcePath);
            // 覆盖写入
            for (int e = 0; e < header.length - 1; e++) {
                stream.print(header[e] + separator);
            }
            stream.print(header[header.length - 1] + "\n");
            for (int b = 0; b < L; b++) {
                int j;
                for (j = 0; j < p - 1; j++) {
                    // 除了最后一个的直接输入
                    //                    bw.write(value[i][k][j] + separator);
                    stream.print(result[b][j] + separator);
                }
                //                bw.write(value[i][k][j] + "\n");
                stream.print(result[b][j] + "\n");
            }
            stream.close();
            //            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[][] getCalcTruth() {
        return calcTruth;
    }

    public int getD1() {
        return L;
    }

    public int getD2() {
        return p;
    }

    public Word2VecModel getTriModel() {
        return TriModel;
    }

    /**
     * 部分数据信息量=∑_AL数据（1-数据置信度） +kmeans数据量
     * 所有数据信息量=∑_(AL数据+未标注数据)〖（1-数据置信度）〗 +kmeans数据量
     * 根据数据格式，修改partRMSE的fixme部分，然后根据需求改变minGoal的参数，
     * 最后修改error判定条件
     *
     * @return 数据信息量
     */


    public List<String> daTupleList = new ArrayList<>();
    public List<String> initDATupleList(String daFilePath){
        List<String> res = new ArrayList<>();
        File f = new File(daFilePath);
        try {
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            br.readLine();
            String str;
            String[] data;
            while((str = br.readLine())!=null){
                data = str.split(",",-1);
                res.add(data[0]);
            }
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }
    // 计算初始适应度，内部包含global embedding生成
    public double calcInitFitnessScore(int times, Map<String, float[]> fastText) {
        times = times - 1;
        Searcher search = TriModel.forSearch();
        String resultFilePath;

        resultFilePath = dataPath + "/result/result_" + v + "_" + times + ".csv";
        String truthFilePath;
        // attention : change source
//        truthFilePath = "data/ctd/monitor/monitor_truth.csv";
        truthFilePath = dataPath + "/threetruth.CSV";
        // find embedding
        File resultFile = new File(resultFilePath);
        File truthFile = new File(truthFilePath);
        // calcResult and golden standard
        double totalGTDistance = 0;
        try {
            // result
            FileReader frResult = new FileReader(resultFile);
            BufferedReader brResult = new BufferedReader(frResult);
            String str;
            String[] data;
            // truth
            FileReader frTruth = new FileReader(truthFile);
            BufferedReader brTruth = new BufferedReader(frTruth);
            String strT;
            String[] dataT;
            // 读走属性行
            brResult.readLine();
            brTruth.readLine();

            // finished : 限制只读取前biaozhushu行
            int usedLine = 0;
            while ((str = brResult.readLine()) != null && (strT = brTruth.readLine()) != null && usedLine < biaozhushu) {
                data = str.split(",", -1);
                dataT = strT.split(",", -1);
                if (daTupleList.contains(dataT[0])) {
                    continue;
                }
                // 按照da file对比
                // 维护一个文件存储每次增强的数据的sample id，然后遍历
                totalGTDistance = embdi_glove(data, dataT,search,fastText);
                usedLine++;
            }
            frResult.close();
            frTruth.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (isDA == 0&&existDA==1) {
            // 数据增强
            double totalDADistance = 0;
            String DAFilePath = dataPath + "/result/DAresult/result_" + 1 + "_" + times + ".csv";
            // find embedding
            File DAFile = new File(DAFilePath);
            try {
                // result
                FileReader frResult = new FileReader(resultFile);
                BufferedReader brResult = new BufferedReader(frResult);
                String str;
                String[] data;
                // truth
                FileReader frDA = new FileReader(DAFile);
                BufferedReader brDA = new BufferedReader(frDA);
                String strDA;
                String[] dataDA;

                // 读走属性行
                brResult.readLine();
                brDA.readLine();
                // daFile读走前biaozhushu行,剩下是da
                int passLines = biaozhushu;
                for (int line = 0; line < passLines; line++) {
                    brDA.readLine();
                }
                // 6 attr
                int attrKind = D2;
                // 5
                int usedLine = 0;
                while ((strDA = brDA.readLine()) != null) {

                    dataDA = strDA.split(",", -1);
                    double e1 = 0;
                    double e2 = 0;
                    int numFlag = 0;
                    do {
                        str = brResult.readLine();
                        data = str.split(",", -1);

                        try{
                            e1 = Double.parseDouble(data[0]);
                            e2 = Double.parseDouble(dataDA[0]);
                            if(Math.abs(e1-e2) > 0.5){
                                numFlag = 1;
                            }
                        }catch(NumberFormatException e58){
                            // string类型，不能被解析
                            if(data[0].equals(dataDA[0])){
                                numFlag = 1;
                            }
                        }
                    } while (numFlag == 0);


                    if (!daTupleList.contains(data[0])) {
                        continue;
                    }
                    totalDADistance = embdi_glove(data,dataDA,search,fastText);
                    usedLine++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return totalDADistance + totalGTDistance;
        } else {
            return totalGTDistance;
        }

    }

    /**
     * 平均池化输入的embedding
     *
     * @param listOfSingleWord list of embedding to mean pooling
     */
    private List<Float> meanPooling(List<List<Float>> listOfSingleWord) {
        List<Float> result = new ArrayList<>();
        int size = listOfSingleWord.size();
        if(size==1){
            return listOfSingleWord.get(0);
        }
        if(size==2){
            List<Float> list1 = listOfSingleWord.get(0);
            List<Float> list2 = listOfSingleWord.get(1);
            List<Float> res = new ArrayList<>();
            for(int i = 0;i<list1.size();i++){
                res.add(0.5f*(list1.get(i) + list2.get(i)));
            }
            return res;
        }
        int poolWindow = 2;
        int currentIndex = 0;
        try{
            int vectorSize = listOfSingleWord.get(0).size();
            int resultSize = vectorSize / poolWindow;

            List<Float> resultVector = new ArrayList<>();
            for (int i = 0; i < resultSize; i++) {
                float sum = 0.0f;
                for (List<Float> vector : listOfSingleWord) {
                    for (int j = i * poolWindow; j < (i + 1) * poolWindow; j++) {
                        sum += vector.get(j);
                    }
                }
                float average = sum / (listOfSingleWord.size() * poolWindow);
                resultVector.add(average);
            }

            return resultVector;
        }
        catch(IndexOutOfBoundsException e32){
            exit(-32);
        }

        return result;
    }

    public double getRmseForGA() {
        return rmseForGA;
    }

    public List<Float> array2list(float[] array){
        int embdi_length = 300;
        List<Float> res = new ArrayList<>();
        try{
            for(float n: array){
                res.add(n);
            }
            return res;
        }catch (NullPointerException e){
            for(int i = 0;i<embdi_length;i++){
                res.add(+0.0f);
            }
            return res;
        }
    }

    private List<Float> double2float(ImmutableList<Double> temp){
        List<Float> newList = new ArrayList<>();
        for(double n : temp){
            newList.add((float)n);
        }
        return newList;
    }

    private double embdi_glove(String[] data, String[] dataT, Searcher search, Map<String, float[]> fastText){
        double totalDistance = 0;
        for (int a = 0; a < D2; a++) {
            try {
                // 每次读取新单元格，重新初始化
                List<List<Float>> listOfSingleWord = new ArrayList<>();
                // calc list
                List<List<Float>> fastGe = new ArrayList<>();
                List<Float> globalEmbedding = new ArrayList<>();
                if (data[a].contains(" ")) {
                    String[] singleWordArray = data[a].split(" ");
                    // add each single word split by " "
                    for (String value : singleWordArray) {
                        listOfSingleWord.add(double2float(search.getRawVector(value)));
                        fastGe.add(array2list(fastText.get(value)));
                    }
                    globalEmbedding.addAll(meanPooling(listOfSingleWord));
                    globalEmbedding.addAll(meanPooling(fastGe));
                } else {
                    globalEmbedding.addAll(double2float(search.getRawVector(data[a])));
                    globalEmbedding.addAll(array2list(fastText.get(data[a])));
                }

                // truth pooling
                List<Float> truthEmbedding = new ArrayList<>();
                truthEmbedding.addAll(double2float(search.getRawVector(dataT[a])));
                // attention: fastText
                truthEmbedding.addAll(array2list(fastText.get(dataT[a])));
                // end of embdi

                // end pooling and global embedding
                // 欧氏距离
                double totalSingleWord = 0;
                int globalSize = globalEmbedding.size();
                for (int i = 0; i < globalSize-1; i++) {
                    try{
                        totalSingleWord += Math.pow(Math.abs(globalEmbedding.get(i) - truthEmbedding.get(i)), 2);
                    }catch (IndexOutOfBoundsException ignored){}
                }
                totalSingleWord = Math.sqrt(totalSingleWord);
                totalDistance += totalSingleWord;
            } catch (Searcher.UnknownWordException e) {
                totalDistance += 0;
            }catch (ArrayIndexOutOfBoundsException e1){
                exit(-12);
            }
        }
        return totalDistance;
    }

    private double embdi(String[] data, String[] dataT, Searcher search){
        double totalDistance = 0;
        for (int a = 0; a < D2; a++) {
            try {
                // 每次读取新单元格，重新初始化
                List<List<Float>> listOfSingleWord = new ArrayList<>();
                // calc list
                List<Float> globalEmbedding = new ArrayList<>();
                if (data[a].contains(" ")) {
                    String[] singleWordArray = data[a].split(" ");
                    // add each single word split by " "
                    for (String value : singleWordArray) {
                        listOfSingleWord.add(double2float(search.getRawVector(value)));
                    }
                    globalEmbedding.addAll(meanPooling(listOfSingleWord));
                } else {
                    globalEmbedding.addAll(double2float(search.getRawVector(data[a])));
                }

                // truth pooling
                List<Float> truthEmbedding = new ArrayList<>();
                truthEmbedding.addAll(double2float(search.getRawVector(dataT[a])));
                // end of embdi

                // end pooling and global embedding
                // 欧氏距离
                double totalSingleWord = 0;
                int globalSize = globalEmbedding.size();
                for (int i = 0; i < globalSize-1; i++) {
                    try{
                        totalSingleWord += Math.pow(Math.abs(globalEmbedding.get(i) - truthEmbedding.get(i)), 2);
                    }catch (IndexOutOfBoundsException ignored){}
                }
                totalSingleWord = Math.sqrt(totalSingleWord);
                totalDistance += totalSingleWord;
            } catch (Searcher.UnknownWordException e) {
                totalDistance += 0;
            }catch (ArrayIndexOutOfBoundsException e1){
                exit(-12);
            }
        }
        return totalDistance;
    }

    private double glove(String[] data, String[] dataT, Searcher search, Map<String, float[]> fastText){
        double totalDistance = 0;
        for (int a = 0; a < D2; a++) {
            try {
                // 每次读取新单元格，重新初始化
                List<List<Float>> listOfSingleWord = new ArrayList<>();
                // calc list
                List<List<Float>> fastGe = new ArrayList<>();
                List<Float> globalEmbedding = new ArrayList<>();
                if (data[a].contains(" ")) {
                    String[] singleWordArray = data[a].split(" ");
                    // add each single word split by " "
                    for (String value : singleWordArray) {
                        listOfSingleWord.add(double2float(search.getRawVector(value)));
                        fastGe.add(array2list(fastText.get(value)));
                    }
                    globalEmbedding.addAll(meanPooling(fastGe));
                } else {
                    globalEmbedding.addAll(array2list(fastText.get(data[a])));
                }

                // truth pooling
                List<Float> truthEmbedding = new ArrayList<>();
                // attention: fastText
                truthEmbedding.addAll(array2list(fastText.get(dataT[a])));
                // end of embdi

                // end pooling and global embedding
                // 欧氏距离
                double totalSingleWord = 0;
                int globalSize = globalEmbedding.size();
                for (int i = 0; i < globalSize-1; i++) {
                    try{
                        totalSingleWord += Math.pow(Math.abs(globalEmbedding.get(i) - truthEmbedding.get(i)), 2);
                    }catch (IndexOutOfBoundsException ignored){}
                }
                totalSingleWord = Math.sqrt(totalSingleWord);
                totalDistance += totalSingleWord;
            } catch (Searcher.UnknownWordException e) {
                totalDistance += 0;
            }catch (ArrayIndexOutOfBoundsException e1){
                exit(-12);
            }
        }
        return totalDistance;
    }

    private double embdi_turl(String[] data, String[] dataT, Searcher search, Map<String, float[]> fastText){
        double totalDistance = 0;
        for (int a = 0; a < D2; a++) {
            try {
                // 每次读取新单元格，重新初始化
                List<List<Float>> listOfSingleWord = new ArrayList<>();
                // calc list
                List<List<Float>> fastGe = new ArrayList<>();
                List<Float> globalEmbedding = new ArrayList<>();
                if (data[a].contains(" ")) {
                    String[] singleWordArray = data[a].split(" ");
                    // add each single word split by " "
                    for (String value : singleWordArray) {
                        listOfSingleWord.add(double2float(search.getRawVector(value)));
                        fastGe.add(array2list(fastText.get(value)));
                    }
                    globalEmbedding.addAll(meanPooling(listOfSingleWord));
                    globalEmbedding.addAll(meanPooling(fastGe));
                } else {
                    globalEmbedding.addAll(double2float(search.getRawVector(data[a])));
                    globalEmbedding.addAll(array2list(fastText.get(data[a])));
                }

                // truth pooling
                List<Float> truthEmbedding = new ArrayList<>();
                truthEmbedding.addAll(double2float(search.getRawVector(dataT[a])));
                // attention: fastText
                truthEmbedding.addAll(array2list(fastText.get(dataT[a])));
                // end of embdi

                // end pooling and global embedding
                // 欧氏距离
                double totalSingleWord = 0;
                int globalSize = globalEmbedding.size();
                for (int i = 0; i < globalSize-1; i++) {
                    try{
                        totalSingleWord += Math.pow(Math.abs(globalEmbedding.get(i) - truthEmbedding.get(i)), 2);
                    }catch (IndexOutOfBoundsException ignored){}
                }
                totalSingleWord = Math.sqrt(totalSingleWord);
                totalDistance += totalSingleWord;
            } catch (Searcher.UnknownWordException e) {
                totalDistance += 0;
            }catch (ArrayIndexOutOfBoundsException e1){
                exit(-12);
            }
        }
        return totalDistance;
    }



}

