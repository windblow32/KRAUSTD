package main.java.Embedding.EMBDI.GA;

import java.util.ArrayList;
import java.util.List;

public class Chromosome {
    public boolean[] gene;//基因序列
    private double score;//对应的函数得分

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    /**
     * @param size
     * fixme:用超参代替：随机生成基因序列
     */
    public Chromosome(int size) {
        if (size <= 0) {
            return;
        }
        initGeneSize(size);
        // fixme : warm start
//        for (int i = 0; i < size; i++) {
//            gene[i] = Math.random() >= 0.5;
//        }

        // warmStart 消融
        // p1 = 35, length = 6, 100011
        // monitor : 26 011010
        gene[0] = false;
        gene[1] = true;
        gene[2] = true;
        gene[3] = false;
        gene[4] = true;
        gene[5] = false;
        // p2 = 3, length = 3, 011
        gene[6] = false;
        gene[7] = true;
        gene[8] = true;
        // p3 = 6, length = 3, 110
        // monitor 4 100
        gene[9] = true;
        gene[10] = false;
        gene[11] = false;
        // p4 = 7, length = 3, 111
        // 3 011
        gene[12] = false;
        gene[13] = true;
        gene[14] = true;
        // p5 = 4, length = 3, 100
        // 7 111
        gene[15] = true;
        gene[16] = true;
        gene[17] = true;
        // p6 = 4, length = 3, 100
        // 3 011
        gene[18] = false;
        gene[19] = true;
        gene[20] = true;
        // p7 = 6, length = 3, 110
        // 5 101
        gene[21] = true;
        gene[22] = false;
        gene[23] = true;
        // p8 = 0, length = 1, 0
        gene[24] = false;
        // p9 = 1, length = 1, 1
        gene[25] = true;
        // p10 = 1, length = 1, 1
        gene[26] = true;
        // p11 = , length = 7, 1010000
        // 157 - 63 = 94 1011110
        gene[27] = true;
        gene[28] = false;
        gene[29] = true;
        gene[30] = true;
        gene[31] = true;
        gene[32] = true;
        gene[33] = false;
        // p12 = 2, length = 3, 010
        // 5 101
        gene[34] = true;
        gene[35] = false;
        gene[36] = true;


    }

    /**
     * 生成一个新基因
     */
    public Chromosome() {

    }

    /**
     * @param c
     * @return
     * @Description: 克隆基因
     */
    public static Chromosome clone(final Chromosome c) {
        if (c == null || c.gene == null) {
            return null;
        }
        Chromosome copy = new Chromosome();
        copy.initGeneSize(c.gene.length);
        for (int i = 0; i < c.gene.length; i++) {
            copy.gene[i] = c.gene[i];
        }
        return copy;
    }

    /**
     * @param size
     * @Description: 初始化基因长度
     */
    private void initGeneSize(int size) {
        if (size <= 0) {
            return;
        }
        gene = new boolean[size];
    }


    /**
     * @param p1
     * @param p2
     * @Description: 遗传产生下一代,交叉位置有几个固定点，需要根据超参分割位置确定
     */
    public static List<Chromosome> genetic(Chromosome p1, Chromosome p2) {
        if (p1 == null || p2 == null) { //染色体有一个为空，不产生下一代
            return null;
        }
        if (p1.gene == null || p2.gene == null) { //染色体有一个没有基因序列，不产生下一代
            return null;
        }
        if (p1.gene.length != p2.gene.length) { //染色体基因序列长度不同，不产生下一代
            return null;
        }
        Chromosome c1 = clone(p1);
        Chromosome c2 = clone(p2);
        //随机产生交叉互换位置
        int size = c1.gene.length;
        // fixme:min and max should change
        int a = ((int) (Math.random() * size)) % size;
        int b = ((int) (Math.random() * size)) % size;
        int min = Math.min(a, b);
        int max = Math.max(a, b);
        //对位置上的基因进行交叉互换
        boolean t;
        for (int i = min; i <= max; i++) {
            t = c1.gene[i];
            c1.gene[i] = c2.gene[i];
            c2.gene[i] = t;
        }
        List<Chromosome> list = new ArrayList<Chromosome>();
        list.add(c1);
        list.add(c2);
        return list;
    }

    /**
     * @param num
     * @Description: 基因num个位置发生变异
     */
    public void mutation(int num) {
        //允许变异
        int size = gene.length;
        for (int i = 0; i < num; i++) {
            //寻找变异位置
            int at = ((int) (Math.random() * size)) % size;
            //变异后的值
            boolean bool = !gene[at];
            gene[at] = bool;
        }
    }

    /**
     * @Description: 将基因转化为对应的数字
     */
    public int getNum() {
        if (gene == null) {
            return 0;
        }
        int num = 0;
        for (boolean bool : gene) {
            num <<= 1;
            if (bool) {
                num += 1;
            }
        }
        return num;
    }
}

