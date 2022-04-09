package main.java.Kmean;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class KMeans
{
    //聚类的数目
    final static int ClassCount = 3;
    //样本数目（测试集），tuple数量
    final static int InstanceNumber = 100;
    //样本属性数目（测试）,embedding维度
    final static int FieldCount = 100;

    //设置异常点阈值参数（每一类初始的最小数目为InstanceNumber/ClassCount^t）
    final static double t = 2.0;

    //存放数据的矩阵
    private float[][] data;
    //每个类的均值中心
    private float[][] classData;
    //噪声集合索引
    private ArrayList<Integer> noises;
    //存放每次变换结果的矩阵
    private ArrayList<ArrayList<Integer>> result;

    public KMeans()
    {
        //最后一位用来储存结果
        data = new float[InstanceNumber][FieldCount+1];
        classData = new float[ClassCount][FieldCount];
        result = new ArrayList<ArrayList<Integer>>(ClassCount);
        noises = new ArrayList<Integer>();
    }

    public void readData(String TrainDataFile)
    {
        FileReader fr = null;
        BufferedReader br = null;
        try
        {
            fr = new FileReader(TrainDataFile);
            br = new BufferedReader(fr);
            //存放数据的临时变量
            String lineData = null;
            String[] splitData = null;
            int line = 0;
            while( br.ready())
            {
                lineData = br.readLine();
                System.out.println(lineData);
                splitData = lineData.split(",");
                for(int i = 0 ; i < splitData.length ;i++)
                {
                    data[line][i] = Float.parseFloat(splitData[i]);
                }
                line++;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(null != br)
            {
                try
                {
                    br.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            if(null != fr)
            {
                try
                {
                    fr.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public void cluster()
    {
        //数据归一化
        normalize();

        //标记是否需要重新找初始点
        boolean needUpdataInitials = true;

        //找初始点的迭代次数
        int times = 1;

        //找初始点
        while(needUpdataInitials)
        {
            needUpdataInitials = false;
            result.clear();
            System.out.println("Find Initials Iteration"+(times++)+"time(s)");

            //一次找初始点的尝试和根据初始点的分类
            findInitials();
            firstClassify();

            for(int i = 0;i < result.size();i++)
            {
                if(result.get(i).size() < InstanceNumber/Math.pow(ClassCount,t))
                {
                    needUpdataInitials = true;
                    noises.addAll(result.get(i));
                }
            }
        }

        Adjust();
    }

    /**
     * 数据归一化
     * @author coshaho
     */
    private void normalize()
    {
        // 计算数据每个维度最大值max
        float[] max = new float[FieldCount];
        for(int i = 0;i < InstanceNumber;i++)
        {
            for(int j = 0;j < FieldCount;j++)
            {
                if(data[i][j] > max[j])
                {
                    max[j] = data[i][j];
                }
            }
        }

        // 每个维度归一化值=原始值/max
        for(int i = 0;i < InstanceNumber;i++)
        {
            for(int j = 0;j < FieldCount;j++)
            {
                data[i][j] = data[i][j]/max[j];
            }
        }
    }

    /**
     * 寻找初始聚类中心
     * @author coshaho
     */
    private void findInitials()
    {
        int i, j, a, b;
        i = j = a = b = 0;
        float maxDis = 0;
        int alreadyCls = 2;

        // 选取距离最远的两个点a,b作为聚类中心点
        ArrayList<Integer> initials = new ArrayList<Integer>();
        for (; i < InstanceNumber; i++)
        {
            // 噪声点不参与计算
            if (noises.contains(i))
            {
                continue;
            }
            j = i + 1;
            for (; j < InstanceNumber; j++)
            {
                // 噪声点不参与计算
                if (noises.contains(j))
                {
                    continue;
                }
                float newDis = calDis(data[i], data[j]);
                if (maxDis < newDis)
                {
                    a = i;
                    b = j;
                    maxDis = newDis;
                }
            }
        }

        // initials添加初始聚类中心点序号a,b
        initials.add(a);
        initials.add(b);

        // classData添加聚类中心点data[a],data[b]
        classData[0] = data[a];
        classData[1] = data[b];

        // 新增两个聚类，并添加聚类成员
        ArrayList<Integer> resultOne = new ArrayList<Integer>();
        ArrayList<Integer> resultTwo = new ArrayList<Integer>();
        resultOne.add(a);
        resultTwo.add(b);
        result.add(resultOne);
        result.add(resultTwo);

        // 1、计算剩下每个点x与其他点的最小距离l，并记录Map<x,l>
        // 2、选取Map<x,l>中的最大l，并以对应的点x作为新的聚类中心
        while (alreadyCls < ClassCount)
        {
            i = j = 0;
            float maxMin = 0;
            int newClass = -1;

            for (; i < InstanceNumber; i++)
            {
                float min = 0;
                float newMin = 0;
                if (initials.contains(i))
                {
                    continue;
                }
                if (noises.contains(i))
                {
                    continue;
                }
                for (j = 0; j < alreadyCls; j++)
                {
                    newMin = calDis(data[i], classData[j]);
                    if (min == 0 || newMin < min)
                    {
                        min = newMin;
                    }
                }
                if (min > maxMin)
                {
                    maxMin = min;
                    newClass = i;
                }
            }

            // initials添加新的聚类中心点序号newClass
            initials.add(newClass);

            // classData添加新的聚类中心点data[newClass]
            classData[alreadyCls++] = data[newClass];

            // 新增一个聚类，并添加成员
            ArrayList<Integer> rslt = new ArrayList<Integer>();
            rslt.add(newClass);
            result.add(rslt);
        }
    }

    /**
     * 首次聚类分配
     * 点x到哪个聚类中心点最近，则划分到哪个聚类
     * @author coshaho
     */
    public void firstClassify()
    {
        for (int i = 0; i < InstanceNumber; i++)
        {
            float min = 0f;
            int clsId = -1;
            for (int j = 0; j < classData.length; j++)
            {
                // 欧式距离
                float newMin = calDis(classData[j], data[i]);
                if (clsId == -1 || newMin < min)
                {
                    clsId = j;
                    min = newMin;
                }
            }

            if (!result.get(clsId).contains(i))
            {
                result.get(clsId).add(i);
            }
        }
    }

    // 迭代分类，直到各个类的数据不再变化
    public void Adjust()
    {
        // 记录是否发生变化
        boolean change = true;

        // 循环的次数
        int times = 1;
        while (change)
        {
            // 复位
            change = false;
            System.out.println("Adjust Iteration" + (times++) + "time(s)");

            // 重新计算每个类的均值
            for (int i = 0; i < ClassCount; i++)
            {
                // 原有的数据
                ArrayList<Integer> cls = result.get(i);

                // 新的均值
                float[] newMean = new float[FieldCount];

                // 计算均值
                for (Integer index : cls)
                {
                    for (int j = 0; j < FieldCount; j++)
                        newMean[j] += data[index][j];
                }
                for (int j = 0; j < FieldCount; j++)
                {
                    newMean[j] /= cls.size();
                }
                if (!compareMean(newMean, classData[i]))
                {
                    classData[i] = newMean;
                    change = true;
                }
            }
            // 清空之前的数据
            for (ArrayList<Integer> cls : result)
            {
                cls.clear();
            }

            // 重新分配
            for (int i = 0; i < InstanceNumber; i++)
            {
                float min = 0f;
                int clsId = -1;
                for (int j = 0; j < classData.length; j++)
                {
                    float newMin = calDis(classData[j], data[i]);
                    if (clsId == -1 || newMin < min)
                    {
                        clsId = j;
                        min = newMin;
                    }
                }
                data[i][FieldCount] = clsId;
                result.get(clsId).add(i);
            }
        }
    }

    /**
     * 计算a样本和b样本的欧式距离作为不相似度
     *
     * @param aVector 样本a
     * @param bVector 样本b
     * @return 欧式距离长度
     */
    private float calDis(float[] aVector, float[] bVector) {
        double dis = 0;
        int i = 0;
        /* 最后一个数据在训练集中为结果，所以不考虑 */
        for (; i < aVector.length; i++)
            dis += Math.pow(bVector[i] - aVector[i], 2);
        dis = Math.pow(dis, 0.5);
        return (float) dis;
    }

    /**
     * 判断两个均值向量是否相等
     *
     * @param a 向量a
     * @param b 向量b
     * @return
     */
    private boolean compareMean(float[] a, float[] b) {
        if (a.length != b.length)
            return false;
        for (int i = 0; i < a.length; i++) {
            if (a[i] > 0 && b[i] > 0 && a[i] != b[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * 将结果输出到一个文件中
     *
     * @param fileName
     */
    public void printResult(String fileName)
    {
        FileWriter fw = null;
        BufferedWriter bw = null;
        try
        {
            fw = new FileWriter(fileName);
            bw = new BufferedWriter(fw);
            // 写入文件
            for (int i = 0; i < InstanceNumber; i++)
            {
                bw.write(String.valueOf(data[i][FieldCount]).substring(0, 1));
                bw.newLine();
            }

            // 统计每类的数目，打印到控制台
            for (int i = 0; i < ClassCount; i++)
            {
                System.out.println("第" + (i + 1) + "类数目: "
                        + result.get(i).size());
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {

            // 关闭资源
            if (bw != null)
            {
                try
                {
                    bw.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            if (fw != null)
            {
                try
                {
                    fw.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
