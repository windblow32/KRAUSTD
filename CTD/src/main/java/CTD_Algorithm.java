package main.java;

import com.medallia.word2vec.Searcher;
import com.medallia.word2vec.Word2VecModel;
import main.java.Embedding.EMBDI.TripartiteNormalizeDistributeGraph.NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec;

import java.io.*;
import java.nio.Buffer;
import java.text.DecimalFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static java.lang.Double.NaN;

public class CTD_Algorithm {

    // 定义收敛区间,或者记录下两次error差距不超过百分之多少，就不追究了
    private final double min_error = 1;
    private final List<Double> weights = new ArrayList<>();
    private final List<String[]> processed_DC = new ArrayList<>();
    // 测试版本，embedding保存文件使用
    // flag标志矩阵数值是否改变，作用于distance函数中，模型是否重新训练
    public int flag = 0;
    // pass rmse to GA
    public double rmseForGA = 0;
    public String[][] calcTruth = null;

    public int CTD_sotaFlag = 0;
    // word2VecModel, change every time value update
    public Word2VecModel TriModel;
    public int v;
    // L行tuple
    private int L;
    // p种attr
    private int p;
    // num of source
    private int k;
    private List<String> attributes = new ArrayList<>();
    public double initFitnessScore;
    // 二维数组repaired table无法初始化，在算法中返回即可。

    public int isDA = 0;
    public int useTriModel = 0;

    public int time = 3;

    public static void main(String[] args) {
//        List<String> files = new ArrayList<>();
//        files.add("data/flight/flight-csv/flight0.CSV");
//        files.add("data/flight/flight-csv/flight0.CSV");
//        files.add("data/flight/flight-csv/flight0.CSV");
//        int k = 3;
//        List<String> DCs = new ArrayList<>();
//        DCs.add("scheduled_departure = 0");
//        DCs.add("actual_departure > scheduled_departure");
//        //        DCs.add("Tuple 1 gender = tuple 3 gender");
//        List<Double> w;
//        w = test.update(files, k, DCs, "FIVE");
//        for (double weight : w) {
//            System.out.println(weight);
//        }
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

    // todo

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
     * @param rmsePrinter         帮助GA打印rmse
     * @param r2_squarePrinter    帮助GA打印r2
     * @param fitScore            打印上次超参的适应度
     * @param extractedCTD_RMSE   打印上一次抽取数据的rmse
     * @return weight of source
     */
    public List<Double> update(
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
            double rmsePrinter,
            double r2_squarePrinter,
            double fitScore,
            double extractedCTD_RMSE,
            int isCBOW,
            int dim,
            int windowSize,
            int isDA,
            int useTriModel
    ) {
        this.isDA = isDA;
        this.v = version;
        this.k = k; // this.k = files.size();
        this.useTriModel = useTriModel;
        // double输出限制
        DecimalFormat df = new DecimalFormat("#.00");
        // use date to name file
        LocalTime time1 = LocalTime.now();
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("HH:mm:ss");
        String t1 = time1.format(formatter1);
        String[] data1 = t1.split(":");
        String insertT1 = data1[0] + data1[1] + data1[2];
        String logPath;
        if(isDA == 1){
            logPath = "log/Tri/weightCalcByVex/DA/logMin" + insertT1 + ".txt";

        }else{
            logPath = "log/Tri/weightCalcByVex/logMin" + insertT1 + ".txt";
        }
        File logFile = new File(logPath);
        try {
            logFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(logFile);
            PrintStream ps = new PrintStream(fos);
            System.setOut(ps);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // print information, if 0 then not in topk list
        System.out.println("GA printer : rmse = " + rmsePrinter);
        System.out.println("GA printer : r2 square = " + r2_squarePrinter);
        System.out.println("GA printer : last time fitScore = " + fitScore);
        System.out.println("GA printer : last time extracted data's rmse = " + extractedCTD_RMSE);

        // 解析DC,默认的DC格式为:
        // 值域约束 : zipcode < 12
        // 函数依赖 : attribute1 < attribute2
        for (String dc : DCs) {
            String[] split_DC = dc.split(" ");
            processed_DC.add(split_DC);
        }

        // todo:Initial the source weight W
        for (int wi = 0; wi < k; wi++) {
            weights.add(1.0 / k);
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
        // 初始化pre_result,别问我为啥初始化成0，因为他最后第一次赋值也没啥用，但是还不能空，就很离谱
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
            // finish todo 根据路径获取文件file,读取获得行数L,属性数p,填充attributes
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
                        System.out.println("越界 : "+y);
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
                // (25) finish todo : judge continuous data, continuous assure value[source][i][j] is double type to be parsed
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
                        // FIXME:应该修复为对于当前的数据集的行数，进行查找, p is waited to fix
                        for (int row = 0; row < p; row++) {
                            // 用于计算h函数的参数1，来自数据源t的第row行的j列属性
                            String parma1 = value[t][row][j];
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
                    }
                    // store in result temporarily
                    result[i][j] = v;
                }
            }
        }

        // finish todo:利用SimilarityUtils类，进行矩阵中字符串的比较
        SimilarityUtils sim = new SimilarityUtils();
        double error = 100;
        // 记录result文件的个数
        int times = 0;
        double goal = 0;
        while (error > min_error) {
            for (int i = 0; i < L; i++) {
                for (int j = 0; j < p; j++) {
                    int before_judge = 0;
                    // 直到找到一个不是空的value，然后判断是否是连续值
                    while (value[before_judge][i][j] == null) {
                        before_judge++;
                    }
                    boolean num_or_String = judge_number(value[before_judge][i][j]);
                    // (25) finish todo : judge continuous data, continuous assure value[source][i][j] is double type to be parsed
                    if (num_or_String) {
                        int source = 0; // 记录当前是第几个数据源
                        // 记录（25）中的分子
                        double sum_weighted_value = 0;
                        // 记录（25）中的分母
                        double sum_weight = 0;
                        for (double w1 : weights) {
                            if (!value[source][i][j].isEmpty()) {
                                sum_weighted_value +=
                                        w1 * Double.parseDouble(value[source][i][j]);
                            }
                            sum_weight += w1;
                            source++;
                        }
                        // store v[l][p]*
                        // fixme : sum_weight = 0
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
                            // FIXME:应该修复为对于当前的数据集的行数，进行查找, p is waited to fix
                            for (int row = 0; row < p; row++) {
                                // 用于计算h函数的参数1，来自数据源t的第row行的j列属性
                                String parma1 = value[t][row][j];
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
                        }
                        // store in result temporarily
                        result[i][j] = v;
                    }

                    // finish todo : judge satisfy (20) or (31)
                    boolean judge;
          /*
                    用于在judge部分应用不同的update方法！
                    1, 2, 3为single entity
                    1代表 code < 10;
                    2代表 code < zipcode
                    3代表前两种的混合情况, 如果flag被先后赋值1，2或者2，1,就将其赋值为3
                    4代表 multi entity
                     */
                    //                    int flag = 0;
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
                            // FIXME:当前开头不是属性开头时，默认是多实体间的DC,以tuple开头

                            if (!attributes.contains(dc[0])) {
                                //                                System.out.println("DC is not legal, the first word isn't an attribute!");
                                // FIXME:注意，这里认位多实体约束和单实体约束是分开的，不能混合使用！
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
                            sum += f_res;
                            // FIXME: 这里是否是DCs.size(),原文给的是整体约束DC的一个元素？
                            if (1 - sum - DCs.size() <= 0) {
                                judge = true;
                            } else {
                                System.out.println("(20) is not satisfied");
                                // 声明条件为假
                                judge = false;
                            }
                            // 如果result中出现 空值，说明所有数据源提供的都是空值，那就是空值
                            if (v1.equals("") || v2.equals("")) {
                                continue;
                            }
                            double v1_value = Double.parseDouble(v1);
                            double v2_value = Double.parseDouble(v2);
                            // judge 不成立并且v1，v2都是连续值时：
                            if (!judge && judge_number(v1) && judge_number(v2)) {
                                // todo : update v.lp according to (27)
                                // FIXME:采用(28)开始的公式对于上述中存在问题的数据进行捕获与修复
                                // 满足拉格朗日乘子为0前提下，求table X
                                switch (find_count) {
                                    case 2:
                                        // todo:(28),看先碰到哪个属性，避免再次碰到再修复。对于连续值进行的修复
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
                                        // todo:(29)
                                        if (v1_value < v2_value) {
                                            result[l][attr_index] = String.valueOf(df.format(v2_value));
                                        }
                                        break;
                                }
                            }
                        }
                    }
                    // else, data in result is the final v.lp

                }
            }
            // 至此,更新结束
//            CTD_sotaFlag = 1;
            try {
                writeValue(value);
                writeResult(result, times);
                // fixme : 2次退出
                // 等于0为3次，1为仅仅CTD自身
                if (times % time == 0 && times != 0) {
                    calcTruth = result;
                    return weights;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (times % time == 0 && times != 0) {
                calcTruth = result;
                return weights;
            }
            // update weight of kth source w.k by (17)
            double up = 0; // 分子
            double down; //分母
            // calculate up,必须分开计算，因为分子是三重循环的总和，定值
            // todo : matrix数值此后不改变，distance中模型训练一次保存后，不用改变了
            flag = 0;
            for (int s = 0; s < k; s++) {
                for (int l = 0; l < L; l++) {
                    for (int col = 0; col < p; col++) {
                        double r = distance(result[l][col], value[s][l][col], mode, times,
                                length, AttrDistributeLow, AttrDistributeHigh,
                                ValueDistributeLow, ValueDistributeHigh, TupleDistributeLow,
                                TupleDistributeHigh, dropSourceEdge, dropSampleEdge,isCBOW,dim,windowSize);
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
            // calculate wk
            for (int s = 0; s < k; s++) {
                down = 0;
                for (int l = 0; l < L; l++) {
                    for (int col = 0; col < p; col++) {
                        double r2 = distance(result[l][col], value[s][l][col], mode, times,
                                length, AttrDistributeLow, AttrDistributeHigh,
                                ValueDistributeLow, ValueDistributeHigh,
                                TupleDistributeLow, TupleDistributeHigh,
                                dropSourceEdge, dropSampleEdge,isCBOW,dim,windowSize);
                        if (r2 == -100) {
                            calcTruth = result;
                            return weights;
                        }
                        down += r2;

                    }
                }
                // calculate nature log
                double wk = Math.log1p(up / down);
                // fixme : weight无穷大,可能与数据缺失相关
                if (!Double.isFinite(wk) || Double.isNaN(wk)) {
                    weights.set(s, 3.0);
                }else{
                    weights.set(s, wk);
                }

            }
            flag = 0;
            // 计算距离用到result的路径，其中有times作为标志，因而在两次distance计算过后再增加times
            times++;
            // 重置error
            error = 0;
            for (int q = 0; q < L; q++) {
                for (int w = 0; w < p; w++) {
                    error += sim.levenshtein(result[q][w], pre_result[q][w]);
                }
            }
            System.out.println("error is " + error);
            pre_result = result;
            // 不用置信度了
//            if(isDA == 0){
//                goal = minGoal(result);
//                System.out.println("rmse : " + rmseForGA);
//                // fixme : goal上限
//                if (goal < 22) {
//                    break;
//                }
//            }
            if (times % time == 0 && times != 0) {
                break;
            }
        }
        if(isDA == 0){
            initFitnessScore = calcInitFitnessScore(times);
        }

        calcTruth = result;
        return weights;
    }

    /**
     * @param v1 待比较的第一个字符串
     * @param v2 待比较的另一个字符串
     * @return 相等返回1，否则返回2
     */
    private int h(String v1, String v2) {
        // FIXME:浮点比较直接等于是否有问题？因为一定存在相等的点！
        if (v1.equals(v2)) return 1;
        else return 0;
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
            // todo:update (35)
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
     * @param v1    字符串1，来自result
     * @param v2    字符串2，来自value
     * @param mode  表明使用三分图或者五分图进行distance计算
     * @param times 记录着result文件的个数(从0开始)，
     * @param isCBOW 为1则采用CBOW，否则v0采用sg
     * @param dim embedding 维度
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
                            int windowSize){
        try {
            if (v1.equals("") || v2.equals("")) {
                return 1;
            }
        } catch (NullPointerException e) {
            return 1;
        }

        List<String> fileList = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            // 注意和writeValue的路径相同
            int z = i + 1;
            String path;
            if(isDA == 0){
                path = "E:\\GitHub\\KRAUSTD\\CTD\\data\\stock100\\divideSourceNew\\source" + z + ".csv";
            }else{
                path = "E:\\GitHub\\KRAUSTD\\CTD\\data\\stock100\\divideSourceNewDA\\source" + z + ".csv";
            }
            fileList.add(path);
        }
        String resultPath;
        if(isDA == 0){
            resultPath =
                    "E:\\GitHub\\KRAUSTD\\CTD\\data\\stock100\\result\\result_" + v + "_" + times + ".csv";
        }else{
            resultPath =
                    "E:\\GitHub\\KRAUSTD\\CTD\\data\\stock100\\result\\DAresult\\result_" + v + "_" + times + ".csv";
        }
        String tempDAFilePath = "data/stock100/result/DAresult/tempDA.csv";
        if(isDA==0){
            // 拿到需要读入的数据
            String DAresultPath = "E:\\GitHub\\KRAUSTD\\CTD\\data\\stock100\\result\\DAresult\\result_" + v + "_" + times + ".csv";
            File DAresult = new File(DAresultPath);
            try {
                FileReader fr = new FileReader(DAresult);
                BufferedReader br = new BufferedReader(fr);
                String str;
                String[] data;
                int line = 0;
                // fixme : daResult 有attr，23+1=24
                while(line<24){
                    br.readLine();
                    line++;
                }
                // fixme : 增强的id
                List<String> daTupleList = new ArrayList<>();
                daTupleList.add("177");
                daTupleList.add("205");
                daTupleList.add("362");
                daTupleList.add("549");
                daTupleList.add("643");
                daTupleList.add("792");
                daTupleList.add("981");

                File resultFile = new File("E:\\GitHub\\KRAUSTD\\CTD\\data\\stock100\\result\\result_" + v + "_" + times + ".csv");
                FileReader resultFr = new FileReader(resultFile);
                BufferedReader resultBr = new BufferedReader(resultFr);
                // read attr
                str = resultBr.readLine();

                File DAFile = new File(tempDAFilePath);
                deleteWithPath(tempDAFilePath);
                DAFile.createNewFile();
                PrintStream ps = new PrintStream(DAFile);
                System.setOut(ps);
                // write attr
                System.out.println(str);

                // 从正常的result读取str输入到tempDA中，如果tuple在daTupleList中，就用这个替换

                while((str = resultBr.readLine())!=null){
                    data = str.split(",",-1);
                    if(daTupleList.contains(Math.round(Double.parseDouble(data[0])))){
                        // 需要被替换,因为元组是有序的，直接读取DAresult下一行就行
                        str = br.readLine();
                        System.out.println(str);
                    }else{
                        System.out.println(str);
                    }
                }
                ps.close();
                resultBr.close();
                resultFr.close();
                br.close();
                fr.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }



        if(isDA == 0){
            // 添加改动过的
            fileList.add(tempDAFilePath);
        }else{
            // 否则正常添加
            fileList.add(resultPath);
        }

        if (times % time == 0 && times != 0) {
            return -100;
        }
        // version
        // fixme
//        String walkPath = "data/stock100/walkList" + version + ".txt";
//        String modelPath = "model/Tri/stock100/total" + version + ".model";
//        String graphPath = "data/stock100/weightCalcByVex/graph/55SourceStockGraphMin.txt";

        if (mode.equals("FIVE")) {
            // fixme: 五分图没修改对应的类
            // 五分图
            NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec word2VecService = new NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec();
            // fixme : GA instead,利用遗传算法计算超参
            // todo GA()得到超参
//            word2VecService.train(fileList, graphPath, 20, 3, 60,20000,1,1,1,1,1,1);
//            return 1 - word2VecService.distance(v1, v2);
        } else if (mode.equals("THREE")) {
            // 三分图
            NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec word2VecService = new NormalizeDistributeSourceTripartiteEmbeddingViaWord2Vec();
            // GA第一次训练，构造图用
            File versionListFile = new File("log/Tri/CTD/version_list.txt");
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(versionListFile);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                List<Integer> versionList = (List<Integer>)objectInputStream.readObject();

                CTD_sotaFlag = versionList.get(versionList.size()-1);

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

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
                // version,length,6个参数
                LocalTime time = LocalTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                // 防止遗传算法因为图已经存储不能重新构造
                String t = time.format(formatter);
                String[] data = t.split(":");
                String insertT = data[0] + data[1] + data[2];
                String graphFilePath = "data/stock100/weightCalcByVex/graph/55SourceStockGraphMin" + v + "_" + insertT + ".txt";
                String modelPath = "model/Tri/stock100/weightCalcByVex/totalMin" + v + "_" + insertT + ".model";
                word2VecService.train(fileList, graphFilePath, 3, 3, length, 20000,
                        AttrDistributeLow, AttrDistributeHigh, ValueDistributeLow, ValueDistributeHigh,
                        TupleDistributeLow, TupleDistributeHigh, dropSourceEdge, dropSampleEdge,isCBOW,dim,windowSize);
                TriModel = word2VecService.trainWithLocalWalks(modelPath);
                // distanceUseSavedModel 计算的是相似度，需要处理
                return 1 - word2VecService.distanceUseSavedModel(TriModel, v1, v2);
            } else if ((CTD_sotaFlag == 1 && flag == 1) ||(useTriModel==1 && flag == 1)) {
                return 1 - word2VecService.distanceUseSavedModel(TriModel, v1, v2);
            }

        } else {
            // mode not support!
            System.out.println("mode isn't supported!");
        }
        return -1;
    }

    private void writeValue(String[][][] value) throws IOException {
//    String[] header = new String[] { "time", "place", "city", "good" };
        String[] header = new String[]{"sample", "change%", "last_trade_price", "open_price", "volumn", "today_high", "today_low", "previous_close", "52wk_H", "52wk_L"};
        String separator = ",";
        String sourcePath;
        for (int i = 0; i < k; i++) {
            int q = i + 1;
            if(isDA == 0){
                sourcePath = "E:\\GitHub\\KRAUSTD\\CTD\\data\\stock100\\divideSourceNew\\source" + q + ".csv";
            }else{
                sourcePath = "E:\\GitHub\\KRAUSTD\\CTD\\data\\stock100\\divideSourceNewDA\\source" + q + ".csv";
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
        //String[] header = new String[] { "time", "place", "city", "good" };
        String[] header = new String[]{"sample", "change%", "last_trade_price", "open_price", "volumn", "today_high", "today_low", "previous_close", "52wk_H", "52wk_L"};
        // sample,change%,last_trade_price,open_price,volumn,today_high,today_low,previous_close,52wk_H,52wk_L
        String separator = ",";
        String sourcePath;
        if(isDA == 0){
            sourcePath =
                    "E:\\GitHub\\KRAUSTD\\CTD\\data\\stock100\\result\\result_" + v + "_" + times + ".csv";
        }else{
            sourcePath =
                    "E:\\GitHub\\KRAUSTD\\CTD\\data\\stock100\\result\\DAresult\\result_" + v + "_" + times + ".csv";
        }


        PrintStream stream = null;
        try {
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
        } catch (FileNotFoundException e) {
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
     * todo:根据数据格式，修改partRMSE的fixme部分，然后根据需求改变minGoal的参数，
     * todo：最后修改error判定条件
     *
     * @return 数据信息量
     */
//    public double minGoal(String[][] result) {
//        // todo 更改文件编码，注意路径
//        String scoreFilePath = "data/stock100/divideSource/10scores.csv";
//        // fixme:规定样本总数是100个
//        double[] dataConfidence = new double[100];
//        try {
//            FileReader fd = new FileReader(scoreFilePath);
//            BufferedReader br = new BufferedReader(fd);
//            String str;
//            String[] data;
//            int row = 0;
////            str = br.readLine();
////            String[] first_line = str.split(",", -1);
//            while ((str = br.readLine()) != null) {
//                data = str.split(",", -1);
//                // 数据置信度存在第二格
//                dataConfidence[row] = Double.parseDouble(data[1]);
//                row++;
//            }
//            fd.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        // 获取最大最小置信度
//        double min = 0;
//        double max = 0;
//        for (int i = 0; i < 100; i++) {
//            double currentVal = dataConfidence[i];
//            if (currentVal > max) {
//                max = currentVal;
//            }
//            if (currentVal < min) {
//                min = currentVal;
//            }
//        }
//        // 重新计算置信度,归一化, 并获取部分和全部数据置信度sum
//        int unlabeledDataConfidenceSum = 0;
//        double ALDataConfidenceSum = 0;
//        for (int i = 0; i < 100; i++) {
//            dataConfidence[i] = (dataConfidence[i] - min) / (max - min);
//            // fixme:10个专家标注，10个主动获取
//            if (i >= 0 && i < 10) {
//                ALDataConfidenceSum += dataConfidence[i];
//            } else if (i > 20) {
//                unlabeledDataConfidenceSum += dataConfidence[i];
//            }
//
//        }
//
//        // fixme 未标注数据置信度,read file to get it!
//        // 未标注数据量
//        int unlabeledDataNum = 100 - 20;
//
//
//        // fixme: 主动学习数据量
//        double ALDataSize = 10;
//
//        double partDataInfoSum = ALDataSize - ALDataConfidenceSum + 20;
//        // 全部数据信息量
//        double allDataInfoSum = (ALDataSize - ALDataConfidenceSum) + unlabeledDataNum - unlabeledDataConfidenceSum + 20;
//        // 最小化目标还加一个所有数据RMSE（估计）+标注数量（已知）
//        //
//        //估计方式：所有数据RMSE/所有数据信息量=部分数据RMSE/部分数据信息量
//        // fixme:change部分数据RMSE
//        String extractFilePath = "data/stock100/divideSource/10samples.csv";
//        // todo : 换成DA的
//
//        List<Integer> extractSampleArray = new ArrayList<Integer>();
//        // fixme : 1st数据20个标注了的
//        String[][] extractCSYTruth = new String[20][10];
//        try {
//            FileReader fr = new FileReader(extractFilePath);
//            BufferedReader br = new BufferedReader(fr);
//            String str;
//            String[] data;
//
//            int row = 0;
//            while ((str = br.readLine()) != null) {
//                data = str.split(",", -1);
//                for (int i = 0; i < 10; i++) {
//                    extractCSYTruth[row][i] = data[i];
//                }
//                extractSampleArray.add(Integer.parseInt(data[0]));
//                row++;
//            }
//            fr.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        String[][] extractCTDTruth = new String[37][10];
//        int row = 0;
//        // L = 100;
//        for (int i = 0; i < 100; i++) {
//            int sample_id = (int) (Double.parseDouble(result[i][0]) * 10 + 5) / 10;
//            if (extractSampleArray.contains(sample_id)) {
//                // 第i行被抽取到了
//                // fixme: 样本一定要按照sample_id有序存放
//                for (int j = 0; j < 10; j++) {
//                    extractCTDTruth[row][j] = result[i][j];
//                }
//                row++;
//            }
//        }
//
//
//        double partRMSE = RMSE(extractCTDTruth, extractCSYTruth, 20, 10);
//        double allRMSE = (partRMSE / partDataInfoSum) * allDataInfoSum;
//        rmseForGA = allRMSE;
//        double minGoal = allRMSE + ALDataSize;
//        return minGoal;
//    }


    public double RMSE(String[][] calcTruth, String[][] goldenStandard, int D1, int D2) {

        double sum = 0;
        for (int i = 0; i < D1; i++) {
            for (int j = 0; j < D2; j++) {
                try {
                    if (calcTruth[i][j].equals("NaN")
                            || goldenStandard[i][j].equals("NaN")
                            || calcTruth[i][j].equals("")
                            || calcTruth[i][j] == null
                            || goldenStandard[i][j].equals("")
                            || goldenStandard[i][j] == null) {
                        sum += 0;
                    } else {
                        double v1 = Double.parseDouble(calcTruth[i][j]);
                        double v2 = Double.parseDouble(goldenStandard[i][j]);
                        sum += Math.pow(Math.abs(v1 - v2), 2) / (D1 * D2);
                    }
                } catch (NumberFormatException | NullPointerException e) {
                    // fixme 异常处理为0是否合理
                    sum += 0;
                }
            }
        }
        sum = Math.sqrt(sum);
        return sum;
    }


    // 计算初始适应度，内部包含global embedding生成
    public double calcInitFitnessScore(int t){
        Searcher search = TriModel.forSearch();
        String resultFilePath;
        int times = time - 1;
        if(isDA == 0){
            resultFilePath = "E:\\GitHub\\KRAUSTD\\CTD\\data\\stock100\\result\\result_" + v + "_" + times + ".csv";
        }else{
            resultFilePath = "E:\\GitHub\\KRAUSTD\\CTD\\data\\stock100\\result\\DAresult\\result_" + v + "_" + times + ".csv";
        }
        String truthFilePath;
        if(isDA == 0){
            truthFilePath = "data/stock100/100truth.csv";

        }else{
            // todo : add DA truth file
            // 专门提取da数据写成truth file
            truthFilePath = "data/stock100/DATruth/trueForDA.csv";
        }

        List<String> daTupleList = new ArrayList<>();
        daTupleList.add("177");
        daTupleList.add("205");
        daTupleList.add("362");
        daTupleList.add("549");
        daTupleList.add("643");
        daTupleList.add("792");
        daTupleList.add("981");

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
            // fixme : 10 attr
            int attrKind = 10;
            // fixme : 限制只读取前23行
            int usedLine = 0;
            while((str = brResult.readLine())!=null&&(strT = brTruth.readLine())!=null&&usedLine<23){
                data = str.split(",",-1);
                dataT = strT.split(",",-1);
                if(daTupleList.contains(dataT[0])){
                    continue;
                }
                // 按照da file对比
                // 维护一个文件存储每次增强的数据的sample id，然后遍历
                for(int a = 0;a<attrKind;a++){
                    try{
                        // 每次读取新单元格，重新初始化
                        List<List<Double>> listOfSingleWord = new ArrayList<>();
                        List<Double> s1List = search.getRawVector(data[a]);
                        // calc list
                        if(data[a].contains(" ")){
                            String[] singleWordArray = data[a].split(" ");
                            // add each single word split by " "
                            for(int s = 0;s<singleWordArray.length;s++){
                                listOfSingleWord.add(search.getRawVector(singleWordArray[s]));
                            }
                        }else {
                            listOfSingleWord.add(s1List);
                        }
                        // fixme : change global embedding
                        // calc pooling embedding并且拼接
                        List<Double> globalEmbedding = new ArrayList<>();
                        globalEmbedding.addAll(s1List);
                        globalEmbedding.addAll(meanPooling(listOfSingleWord));
                        // truth pooling
                        List<Double> s2List = search.getRawVector(dataT[a]);
                        List<List<Double>> listOfTruth = new ArrayList<>();
                        listOfTruth.add(s2List);
                        List<Double> truthEmbedding = new ArrayList<>();
                        truthEmbedding.addAll(s2List);
                        truthEmbedding.addAll(meanPooling(listOfTruth));
                        // end pooling and global embedding
                        // 欧氏距离
                        double totalSingleWord = 0;
                        int globalSize = globalEmbedding.size();
                        for(int i = 0;i<globalSize;i++){
                            totalSingleWord += Math.pow(Math.abs(globalEmbedding.get(i) - truthEmbedding.get(i)),2);
                        }
                        totalSingleWord = Math.sqrt(totalSingleWord);
                        totalGTDistance += totalSingleWord;
                    }catch (Searcher.UnknownWordException e){
                        totalGTDistance += 0;
                    }
                }
                usedLine++;
            }
            frResult.close();
            frTruth.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(isDA==0){
            // 数据增强
            double totalDADistance = 0;
            String DAFilePath = "E:\\GitHub\\KRAUSTD\\CTD\\data\\stock100\\result\\DAresult\\result_" + v + "_" + times + ".csv";
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
                // daFile读走前23行,剩下全是da
                int passLines = 23;
                for(int line = 0;line<passLines;line++){
                    brDA.readLine();
                }
                // 10 attr
                int attrKind = 10;
                // 从24行读取到最后
                int usedLine = 0;
                while(usedLine<7&&(strDA = brDA.readLine())!=null){

                    dataDA = strDA.split(",",-1);

                    do{
                        str = brResult.readLine();
                        data = str.split(",",-1);

                    }while(Math.abs(Double.parseDouble(data[0])-Double.parseDouble(dataDA[0]))>0.5);


                    if(!daTupleList.contains(data[0])){
                        continue;
                    }
                    for(int a = 0;a<attrKind;a++){
                        try{
                            // 每次读取新单元格，重新初始化
                            List<List<Double>> listOfSingleWord = new ArrayList<>();
                            List<Double> s1List = search.getRawVector(data[a]);
                            // calc list
                            if(data[a].contains(" ")){
                                String[] singleWordArray = data[a].split(" ");
                                // add each single word split by " "
                                for(int s = 0;s<singleWordArray.length;s++){
                                    listOfSingleWord.add(search.getRawVector(singleWordArray[s]));
                                }
                            }else {
                                listOfSingleWord.add(s1List);
                            }
                            // calc pooling embedding并且拼接
                            List<Double> globalEmbedding = new ArrayList<>();
                            globalEmbedding.addAll(s1List);
                            globalEmbedding.addAll(meanPooling(listOfSingleWord));
                            // truth pooling
                            List<Double> s2List = search.getRawVector(dataDA[a]);
                            List<List<Double>> listOfDA = new ArrayList<>();
                            listOfDA.add(s2List);
                            List<Double> DAEmbedding = new ArrayList<>();
                            DAEmbedding.addAll(s2List);
                            DAEmbedding.addAll(meanPooling(listOfDA));
                            // end pooling and global embedding
                            // 欧氏距离
                            double totalSingleWord = 0;
                            int globalSize = globalEmbedding.size();
                            for(int i = 0;i<globalSize;i++){
                                totalSingleWord += Math.pow(Math.abs(globalEmbedding.get(i) - DAEmbedding.get(i)),2);
                            }
                            totalSingleWord = Math.sqrt(totalSingleWord);
                            totalDADistance += totalSingleWord;
                        }catch (Searcher.UnknownWordException e){
                            totalDADistance += 0;
                        }
                    }
                    usedLine++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return totalDADistance + totalGTDistance;
        }
        else{
            return 0;
        }


    }

    /**
     * 平均池化输入的embedding
     * @param listOfSingleWord list of embedding to mean pooling
     */
    private List<Double> meanPooling(List<List<Double>> listOfSingleWord) {
        List<Double> result = new ArrayList<>();
        int size = listOfSingleWord.size();
        int poolWindow = 2;
        int currentIndex = 0;
        while(currentIndex+poolWindow<size){
            double pool = 0;
            for(int l = 0;l<size;l++){
                // 对于每个embedding
                List<Double> currentList = listOfSingleWord.get(l);
                for(int i = 0;i<poolWindow;i++){
                    // 每次取五个
                    pool += currentList.get(currentIndex + i);
                }
            }
            result.add(pool/(poolWindow*size));
            currentIndex++;
        }
        return result;
    }

    public double getRmseForGA() {
        return rmseForGA;
    }

}
