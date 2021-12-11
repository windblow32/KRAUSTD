import numpy as np
import csv
import math


stock = []
truth = []
source_num = 10
object_num = 100

source_index = 0
object_index = 1
attribute_index = 2
day_index = 4
truth_index = 1

all_num = 5499
domain = 3

w_s = [1 for i in range(source_num)]

# 读取数据，把整个数据集存在stock里面，真值存在truth里面
def read_data():
    #with open("E:\\stock\\clean_stock\\alldataset_stock_sampled.csv", 'r') as f:
    #    reader = csv.reader(f)
    #    for row in reader:
    #        stock.append(row)
    with open("E:\\stock\\clean_stock\\alltruthfile_stock_sampled.csv", 'r') as f:
        reader = csv.reader(f)
        for row in reader:
            truth.append(row)
    return truth

# 相同为1
def distance(a, b):
    if a == b:
        return 1
    else:
        return 0

def model(w_k, s):
    l = 0
    while True:
        # 计算概率每个值为真的概率
        value = [{} for i in range(object_num)]
        index_for_value = 0
        p = [[] for i in range(object_num)]
        p0 = [[] for i in range(object_num)]
        start = 1
        old_start = 1
        object_n = 1
        coefficient = [0 for i in range(source_num)]
        coefficient_k = 0
        sum = 0
        for i in range(1, all_num):
            # 找到这个对象的第一个声明
            if stock[i][object_index] != stock[i-1][object_index]:
                start = i
            # 转换到下一个对象后，归一化上一个对象的概率，求和清零
            if old_start != start:
                for j in range(len(value[object_n-1])):
                    p[object_n-1][j] /= sum
                object_n += 1
                sum = 0
                old_start = start
                index_for_value = 0
            # 找到这个对象声明的所有的值，用哈希表存储
            if stock[i][attribute_index] not in value[object_n-1].keys():
                # 把这个声明加入哈希表中
                value[object_n-1][stock[i][attribute_index]] = index_for_value
                # 遍历这个对象的所有源的声明，来计算这个值可信度
                j = start
                exp = 0
                # 公式4
                while stock[start][object_index] == stock[j][object_index]:
                    if distance(stock[i][attribute_index], stock[j][attribute_index]) == 1:
                        if stock[j][attribute_index] == truth[object_n-1][truth_index]:
                            coefficient[int(stock[j][source_index])-1] += 1
                            if stock[j][domain] != '' and '.' not in stock[j][domain]:
                                coefficient_k += int(stock[j][domain])
                        if stock[j][domain] != '' and '.' not in stock[j][domain]:
                            exp += (w_s[int(stock[j][source_index])-1] + w_k * int(stock[j][domain]))
                        else:
                            exp += w_s[int(stock[j][source_index])-1]
                    j += 1
                temp = np.exp(exp)
                sum += temp
                p[object_n-1].append(temp)
                p0[object_n-1].append(exp)
                index_for_value += 1
        # 归一化最后一个对象
        for j in range(len(value[object_n - 1])):
            p[object_n - 1][j] /= sum
        print("p")
        '''
        print("p=", p)
        print("p0=", p0)
        print("value=", value)
        print("coe=", coefficient, coefficient_k)
        '''
        old_l = l
        # 计算似然函数
        l = 0
        for i in range(object_num):
            l += p0[i][value[i][truth[i][truth_index]]]

        # 计算偏导数
        alpha = 0.01
        for i in range(source_num):
            b, d = 0, 0
            for j in range(source_num):
                if j != i:
                    b += coefficient[j] * w_s[j]
                    d += 3 * w_s[j]
            b += coefficient_k * w_k
            d += coefficient_k * w_k
            w_s[i] = w_s[i] - alpha * (coefficient[i] * d - b * 3) / ((3 * w_s[i] + d)**2)
        b, d = 0, 0
        for j in range(source_num):
            b += coefficient[j] * w_s[j]
            d += 3 * w_s[j]
        w_k = w_k - alpha * (coefficient_k * d - b * coefficient_k) / ((coefficient_k * w_k + d) ** 2)
        if abs(old_l - l) < 0.16:
            break
    return p, value

# input:选中的数据源，对应的天数(string)，w_k
# output:选中的数据源对应的w_s和w_k
def model_DA1(T, s, x, day, w_k):
    # stock用来存储T的属性，大小为T的大小*55，属性：source, object, attribute, domain, day
    # truth用来存储T的真值，大小为T的大小
    stock = []
    truth = []
    for i in range(len(T)):
        for j in range(len(T[i].source)):
            temp = []
            temp.append(T[i].source[j])
            temp.append(T[i].object[j])
            temp.append(T[i].attribute[j])
            if T[i].domain[j] != '' and '.' not in T[i].domain[j]:
                temp.append(int(T[i].domain[j])/1e8)
            else:
                temp.append(0)
            temp.append(T[i].day[j])
            stock.append(temp)
        truth.append(T[i].true_value)

    # s_inter:我们抽取的源对应的索引，比如说原来源的序号是33是被抽取的第4个源，s_inter[33] = 4
    s_inter = {}
    for i in range(len(s)):
        s_inter[s[i]] = i + 1

    # l是似然值
    l = 0
    # 以下的while循环用来计算参数ws和wk，当l的值收敛的时候循环停止
    while True:

        # value:存储每个对象的所有值，大小是T的大小（也就是对象的数目），每个值存储的是一个字典，字典是值对应的序列号
        #       比如对于第一个对象x有三个声明100，101，105，那么value[0][100] = 0, value[0][101] = 1, value[0][105] = 2
        # index_for_value:用来存储现在的值在该对象中的索引值
        # p:用来存储每个值为真的概率（就是公式4中的概率），大小是T的大小（也就是对象的数目），每个值存储的是该对象对应所有的值为真的概率
        #   比如对于第一个对象x有三个声明100，101，105，为真的概率分别为0.1，0.2，0.7，那么p[0][0] = 0.1,p[0][0] = 0.2,p[0][0] = 0.7
        # p0:用于记录公式4中的分子部分，结构与p完全一致，便于计算偏导数
        value = [{} for i in range(len(T))]
        index_for_value = 0
        p = [[] for i in range(len(T))]
        p0 = [[] for i in range(len(T))]

        # start:该对象的第一个声明对应的索引
        # old_start:检测start什么时候发生变化
        # object_n:用于记录现在遍历到了第几个对象
        # coefficient:用于记录参数ws和wk的系数，，大小是T的大小，每个值里头有11个值，分别是10个ws和1个wk，用于计算后边偏导数
        # sum:用于计算公式4的分母
        start = 1
        old_start = -1
        object_n = 1
        coefficient = [[] for i in range(len(T))]
        sum = 0
        print(len(stock))
        # 这个循环用来计算对于每个对象的所有值为真的概率（根据公式4计算）
        for i in range(0, len(stock)):
            # 第一步要找到对应的天数（理论上，这个天数一定会对应上的）
            if stock[i][day_index] == day:
                # 找到这个对象的第一个声明
                if int(stock[i][source_index]) == s[0]:
                    start = i
                # 转换到下一个对象后，归一化上一个对象的概率，求和清零
                if old_start == -1 and int(stock[i][source_index]) == s[0]:
                    old_start = start
                if old_start != -1 and old_start != start:
                    for j in range(len(value[object_n - 1])):
                        p[object_n - 1][j] /= sum
                    object_n += 1
                    sum = 0
                    old_start = start
                    index_for_value = 0
                # 找到这个对象声明的所有的值，用哈希表存储
                if int(stock[i][source_index]) in s and stock[i][attribute_index] not in value[object_n - 1].keys():
                    # 把这个声明加入哈希表中
                    value[object_n - 1][stock[i][attribute_index]] = index_for_value
                    # 遍历这个对象的所有输入源的声明，来计算这个值可信度
                    j = start
                    exp = 0
                    coe = [0 for i in range(11)]
                    # 公式4
                    # 保证声明相同且源在抽取的10个源中
                    while j < len(stock) and stock[start][object_index] == stock[j][object_index]:
                        if int(stock[j][source_index]) in s:
                            if distance(stock[i][attribute_index], stock[j][attribute_index]) == 1:
                                # 计算系数(源系数加一，domain系数加domain的值)
                                print(int(stock[j][source_index]))
                                coe[s_inter[int(stock[j][source_index])] - 1] += 1
                                if stock[j][domain] != 0:
                                    coe[10] += int(stock[j][domain])
                                # 计算概率值，如果domain的值为空的话，当作0计算
                                if stock[j][domain] != 0:
                                    exp += (w_s[s_inter[int(stock[j][source_index])] - 1] + w_k * int(stock[j][domain]))
                                else:
                                    exp += w_s[s_inter[int(stock[j][source_index])] - 1]
                        j += 1
                    temp = np.exp(exp)
                    print("exp:", exp, "temp:", temp)
                    sum += temp
                    p[object_n - 1].append(temp)
                    p0[object_n - 1].append(exp)
                    coefficient[object_n-1].append(coe)
                    index_for_value += 1
        # 归一化最后一个对象
        print(len(value))
        for j in range(len(value[object_n - 1])):
            p[object_n - 1][j] /= sum

        print("the end for calculate p")
        for i in range(len(p0)):
            print(i, p0[i])
        for i in range(len(value)):
            print(value[i])

        old_l = l
        # 计算似然函数
        l = 0
        for i in range(len(T)):
            if truth[i] in value[i].keys():
                if p[i][value[i][truth[i]]] > 0:
                    l += math.log(p[i][value[i][truth[i]]])
        print("cie", coefficient)
        print(s)
        # 计算偏导数
        alpha = 0.01
        for i in range(source_num):
            temp = 0
            for j in range(len(T)):
                if truth[j] in value[j].keys():
                    temp += (coefficient[j][value[j][truth[j]]][i] - p[j][value[j][truth[j]]])
            w_s[i] = w_s[i] - alpha * temp
        for i in range(len(T)):
            temp = 0
            if truth[i] in value[i].keys():
                temp += coefficient[i][value[i][truth[i]]][10]
                temp1 = 0
                temp2 = 0
                for j in range(len(value[i])):
                    temp1 += coefficient[i][j][10] * p0[i][j]
                    temp2 += p0[i][j]
                temp -= temp1 / temp2
            print("temp:", temp)
            w_k = w_k - alpha * temp
        print("迭代", old_l-l)
        if abs(old_l - l) < 100:
            break


    # 根据上面算出的w，找到最大概率的值作为预测值输出
    value = {}
    index_for_value = 0
    sum = 0
    p = []
    for i in range(len(x.source)):
        # 找到这个对象声明的所有的值，用哈希表存储
        if int(x.source[i]) in s and x.attribute[i] not in value.keys():
            # 把这个声明加入哈希表中
            value[x.attribute[i]] = index_for_value
            # 遍历这个对象的所有输入源的声明，来计算这个值可信度
            j = 0
            exp = 0
            # 公式4
            while j < len(x.source):
                if int(x.source[j]) in s:
                    if distance(x.attribute[i], x.attribute[j]) == 1:
                        if x.domain[j] and '.' not in x.domain[j]:
                            print(x.source[j])
                            exp += (w_s[s_inter[int(x.source[j])] - 1] + w_k * int(x.domain[j]))
                        else:
                            exp += w_s[s_inter[int(x.source[j])] - 1]
                        print(exp)
                j += 1
            temp = np.exp(exp)
            sum += temp
            p.append(temp)
            index_for_value += 1
    for i in range(len(value)):
        p[i] /= sum
    print(p,"end")

    max = 0
    max_index = 0
    for i in range(len(value)):
        if p[i] > max and x.attribute[i] != '':
            max = p[i]
            max_index = i
    return int(x.attribute[max_index])