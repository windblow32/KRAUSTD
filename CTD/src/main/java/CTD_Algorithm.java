package main.java;

import com.medallia.word2vec.Searcher;
import main.java.Embedding.EMBDI.SourceEmbedding.SourceEmbeddingViaWord2Vec;
import main.java.Embedding.EMBDI.TripartiteGraphWithSource.SourceTripartiteEmbeddingViaWord2Vec;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class CTD_Algorithm {
    // 定义收敛区间
    private final double min_error = 1;
    private final List<Double> weights = new ArrayList<>();
    private final List<String[]> processed_DC = new ArrayList<>();
    // L行tuple
    private int L;
    // p种attr
    private int p;
    // num of source
    private int k;
    private List<String> attributes = new ArrayList<>();
    // 二维数组repaired table无法初始化，在算法中返回即可。

    public static void main(String[] args) {
        CTD_Algorithm test = new CTD_Algorithm();
        List<String> files = new ArrayList<>();
        files.add("data/flight/flight-csv/flight0.CSV");
        files.add("data/flight/flight-csv/flight0.CSV");
        files.add("data/flight/flight-csv/flight0.CSV");
        int k = 3;
        List<String> DCs = new ArrayList<>();
        DCs.add("scheduled_departure = 0");
        DCs.add("actual_departure > scheduled_departure");
//        DCs.add("Tuple 1 gender = tuple 3 gender");
        List<Double> w;
        w = test.update(files, k, DCs,"FIVE");
        for (double weight : w) {
            System.out.println(weight);
        }
    }

    /**
     * @param files 读取文件的列表，data from K sources
     * @param k     数据源个数
     * @param DCs   a piece of a set of ∑ of DCs    (fai)
     * @return the repaired table
     */
    public List<Double> update(List<String> files, int k, List<String> DCs, String mode) {
        this.k = k; // this.k = files.size();

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
            String[] first_line = str.split(",");
            p = first_line.length;
            List<String> temp = new ArrayList<>(Arrays.asList(first_line));
            attributes = new ArrayList<>(temp);
            int row = 0;//记录行数L
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
                String[] first_line = str.split(",");
                if (p != first_line.length) {
                    System.out.println(fileAddress + " 属性数量不匹配");
                }

                List<String> temp = new ArrayList<>(Arrays.asList(first_line));
                if (!attributes.equals(temp)) {
                    System.out.println(fileAddress + " 属性名称不匹配");
                }
                int row = 0;//记录行数L
                // 余下的都是元组了
                while ((str = br.readLine()) != null) {
                    String[] data = str.split(",");
                    System.arraycopy(data, 0, value[y][row], 0, data.length);
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
                    int source = 0;// 记录当前是第几个数据源
                    // 记录（25）中的分子
                    double sum_weighted_value = 0;
                    // 记录（25）中的分母
                    double sum_weight = 0;
                    for (double w1 : weights) {
                        if(!value[source][i][j].isEmpty()){
                            sum_weighted_value += w1 * Double.parseDouble(value[source][i][j]);
                        }
                        sum_weight += w1;
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
                            int num = 0;//当前序号,数据源标记,相当于公式中的k
                            double sum_categorical = 0;//  暂时保存总和
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
        double error = 10;
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
                        int source = 0;// 记录当前是第几个数据源
                        // 记录（25）中的分子
                        double sum_weighted_value = 0;
                        // 记录（25）中的分母
                        double sum_weight = 0;
                        for (double w1 : weights) {
                            if(!value[source][i][j].isEmpty()){
                                sum_weighted_value += w1 * Double.parseDouble(value[source][i][j]);
                            }
                            sum_weight += w1;
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
                                int num = 0;//当前序号,数据源标记,相当于公式中的k
                                double sum_categorical = 0;//  暂时保存总和
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
                        int sum = 0;//记录每个元组经过f计算后的数值
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

                            int attr_index = 0;// 记录第一个属性的列index
                            int attr_index2 = 0;// 记录第二个属性的列index
                            int index = 0;// 记录当前index
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
                            // 如果result中出现空值，说明所有数据源提供的都是空值，那就是空值
                            if(v1.equals("")||v2.equals("")){
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
                                            sum_28_up += weights.get(sor) * (Double.parseDouble(value[sor][l][attr_index2]) + Double.parseDouble(value[sor][l][attr_index]));
                                        }
                                        double sum_28_down = 0;
                                        for (double wk : weights) {
                                            sum_28_down += wk;
                                        }
                                        if (attr_index > attr_index2) {
                                            // n 小，n先遇到被修复
                                            result[l][attr_index2] = String.valueOf(sum_28_up / (sum_28_down * 2));
                                        } else if (attr_index < attr_index2) {
                                            result[l][attr_index] = String.valueOf(sum_28_up / (sum_28_down * 2));
                                        }
                                        break;
                                    case 1:
                                        // todo:(29)
                                        if (v1_value < v2_value) {
                                            result[l][attr_index] = String.valueOf(v2_value);
                                        }
                                        break;
                                }

                            }
                        }


                    }

                    // else, data in result is the final v.lp

                }
                // update weight of kth source w.k by (17)
                double up = 0;// 分子
                double down; //分母
                // calculate up,必须分开计算，因为分子是三重循环的总和，定值
                for (int s = 0; s < k; s++) {
                    for (int l = 0; l < L; l++) {
                        for (int col = 0; col < p; col++) {
                            // FIXME:distance always return 1, 导致weight仅更新一次，陷入了死循环
                            up += distance(value, result[l][col], value[s][l][col],mode);
                        }
                    }
                }
                // calculate wk
                for (int s = 0; s < k; s++) {
                    down = 0;
                    for (int l = 0; l < L; l++) {
                        for (int col = 0; col < p; col++) {
                            down += distance(value, result[l][col], value[s][l][col],mode);
                        }
                    }
                    // calculate nature log
                    double wk = Math.log1p(up / down);
                    weights.set(s, wk);
                }
            }

            // 重置error
            error = 0;
            for (int q = 0; q < L; q++) {
                for (int w = 0; w < p; w++) {
                    error += sim.levenshtein(result[q][w], pre_result[q][w]);
                }
            }
            System.out.println("error is " + error);
            pre_result = result;
        }
        return weights;
    }

    // todo

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
        if(v1==null||v2==null){
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
    private void multi_entity(List<String[]> pro_DC, String[][] result, String[][][] value) {
        int sum = 0;// 记录总体的加和
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
        if (option_sign != null && !judge && !judge_number(v1) && !judge_number(v2) && option_sign.equals("=")) {
            // todo:update (35)
            double multi_sum1 = 0;
            for (int s1 = 0; s1 < k; s1++) {
                multi_sum1 += weights.get(s1) * (h(result[row_v1][attr2_index], value[s1][row_v1][attr2_index]) + h(result[row_v1][attr2_index], value[s1][row_v2][attr2_index]));
            }
            double multi_sum2 = 0;
            for (int s2 = 0; s2 < k; s2++) {
                multi_sum2 += weights.get(s2) * (h(result[row_v2][attr2_index], value[s2][row_v1][attr2_index]) + h(result[row_v2][attr2_index], value[s2][row_v2][attr2_index]));
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
        String regex = "^[+-]?([0-9]*\\.?[0-9]+|[0-9]+\\.?[0-9]*)([eE][+-]?[0-9]+)?$";
        return Pattern.matches(regex, str);
    }

    /**
     * 利用embedding计算两个字符串之间的距离，引入EMBDI
     *
     * @param v1 字符串1，来自result
     * @param v2 字符串2，来自value
     * @return 返回两个字符串的欧式距离
     */
    private double distance(String[][][] value, String v1, String v2,String mode) {

//        if(mode.equals("FIVE")){
//            // 五分图
//            SourceEmbeddingViaWord2Vec word2VecService = new SourceEmbeddingViaWord2Vec();
//            word2VecService.train(fileList,20,3,3);
//            return 1 - word2VecService.distance(v1,v2);
//        }
//        else if(mode.equals("THREE")){
//            // 三分图
//            SourceTripartiteEmbeddingViaWord2Vec word2vecService = new SourceTripartiteEmbeddingViaWord2Vec();
//            word2vecService.train(fileList,20,3,3);
//            return 1 - word2vecService.distance(v1,v2);
//        }
//        else{
//            // mode not support!
//            System.out.println("mode isn't supported!");
//        }
        return -1;
    }
}
