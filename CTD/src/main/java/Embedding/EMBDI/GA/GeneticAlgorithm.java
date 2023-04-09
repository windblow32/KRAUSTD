/**
 *@Description:
 */
package main.java.Embedding.EMBDI.GA;

import java.util.ArrayList;
import java.util.List;


public abstract class GeneticAlgorithm {
    private List<Chromosome> population = new ArrayList<Chromosome>();
    private int popSize = 10;//种群数量
    private int geneSize;//基因最大长度
    private int maxIterNum = 10;//最大迭代次数
    private double mutationRate = 0.1;//基因变异的概率
    private int maxMutationNum = 3;//最大变异步长

    public int generation;//当前遗传到第几代

    private double bestScore;//最好得分
    private double worstScore;//最坏得分
    private double totalScore;//总得分
    private double averageScore;//平均得分
    private String bestChro;

    private String x; //记录历史种群中最好的X值,String表示
    private double y; //记录历史种群中最好的Y值
    private int geneI;//x y所在代数
    // 存储历代rmse
    public List<Double> judgeFuncList = new ArrayList<>();
    public List<Integer> versionList = new ArrayList<>();

    public GeneticAlgorithm(int geneSize) {
        this.geneSize = geneSize;
    }

    public void calculate() {
        //初始化种群
        generation = 1;
        init();
        while (generation < maxIterNum) {
            //种群遗传
            evolve();
            print();
            generation++;
        }
    }

    /**
     * @Description: 输出结果
     */
    private void print() {
        System.out.println("--------------------------------");
        System.out.println("the generation is:" + generation);
        System.out.println("the best y is:" + bestScore);
        System.out.println("the worst fitness is:" + worstScore);
        System.out.println("the average fitness is:" + averageScore);
        System.out.println("the total fitness is:" + totalScore);
        System.out.println("geneI:" + geneI + "\tx:" + x + "\ty:" + y);
        System.out.println("best:" + geneI + "\tx:" + bestChro + "\ty:" + bestScore);
    }


    /**
     * @Description: 初始化种群
     */
    private void init() {
        // fixme : warm start
        for (int i = 0; i < popSize; i++) {
//            population = new ArrayList<Chromosome>();
            Chromosome chro = new Chromosome(geneSize);
            population.add(chro);
        }
        calculateScore();
    }

    /**
     * @Description:种群进行遗传
     */
    private void evolve() {
        List<Chromosome> childPopulation = new ArrayList<Chromosome>();
        //生成下一代种群
        while (childPopulation.size() < popSize) {
            Chromosome p1 = getParentChromosome();
            Chromosome p2 = getParentChromosome();
            List<Chromosome> children = Chromosome.genetic(p1, p2);
            if (children != null) {
                for (Chromosome chro : children) {
                    childPopulation.add(chro);
                }
            }
        }
        //新种群替换旧种群
        List<Chromosome> t = population;
        population = childPopulation;
        t.clear();
        t = null;
        //基因突变
        mutation();
        //计算新种群的适应度
        calculateScore();
    }

    /**
     * @return
     * @Description: 轮盘赌法选择可以遗传下一代的染色体
     */
    private Chromosome getParentChromosome (){
        double slice = Math.random() * totalScore;
        double sum = 0;
        for (Chromosome chro : population) {
            sum += chro.getScore();
            if (sum >= slice && chro.getScore() >= averageScore) {
                return chro;
            }
        }
        return null;
    }

    /**
     * @Description: 计算种群适应度
     */
    private void calculateScore() {
        setChromosomeScore(population.get(0));
        bestScore = population.get(0).getScore();
        worstScore = population.get(0).getScore();
        totalScore = 0;
        Chromosome chro;
        for (int i = 1;i<popSize;i++) {
            chro = population.get(i);
            setChromosomeScore(chro);
            System.out.println("gene : " + changeX(chro));
            System.out.println("score : " + chro.getScore());
            if (chro.getScore() >= bestScore) { //设置最好基因值
                bestScore = chro.getScore();
                bestChro = changeX(chro);
                if (y < bestScore) {
                    x = changeX(chro);
                    y = bestScore;
                    geneI = generation;
                }
            }
            if (chro.getScore() < worstScore) { //设置最坏基因值
                worstScore = chro.getScore();
            }
            totalScore += chro.getScore();
        }
        averageScore = totalScore / popSize;
        //因为精度问题导致的平均值大于最好值，将平均值设置成最好值
        averageScore = Math.min(averageScore, bestScore);
    }

    /**
     * 基因突变
     */
    private void mutation() {
        for (Chromosome chro : population) {
            if (Math.random() < mutationRate) { //发生基因突变
                int mutationNum = (int) (Math.random() * maxMutationNum);
                chro.mutation(mutationNum);
            }
        }
    }

    /**
     * @param chro
     * @Description: 设置染色体得分
     */
    private void setChromosomeScore(Chromosome chro) {
        if (chro == null) {
            return;
        }
        String x = changeX(chro);
        double y = calculateY(chro);
        chro.setScore(y);

    }

    /**
     * @param chro
     * @return
     * @Description: 将二进制转化为对应的X
     */
    public abstract String changeX(Chromosome chro);


    /**
     * @param chro
     * @return
     * @Description: 根据X计算Y值 Y=F(X)
     */
    public abstract double calculateY(Chromosome chro);

    public void setPopulation(List<Chromosome> population) {
        this.population = population;
    }

    public void setPopSize(int popSize) {
        this.popSize = popSize;
    }

    public void setGeneSize(int geneSize) {
        this.geneSize = geneSize;
    }

    public void setMaxIterNum(int maxIterNum) {
        this.maxIterNum = maxIterNum;
    }

    public void setMutationRate(double mutationRate) {
        this.mutationRate = mutationRate;
    }

    public void setMaxMutationNum(int maxMutationNum) {
        this.maxMutationNum = maxMutationNum;
    }

    public double getBestScore() {
        return bestScore;
    }

    public double getWorstScore() {
        return worstScore;
    }

    public double getTotalScore() {
        return totalScore;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public String getX() {
        return x;
    }
    public int getGeneI(){
        return geneI;
    }

    public double getY() {
        return y;
    }
}