# Probability sampling
import csv
import random
import numpy as np
import slimfast
from sklearn import metrics
from sklearn.metrics import r2_score


stock = []
p1 = 60
p2 = 80
source_index = 0
object_index = 1
domain_index = 6
day_index = 18
attribute_index = 2


class Stock(object):
    class Struct(object):
        def __init__(self, source, object, attribute, domain, day, true_value):
            self.source = source
            self.object = object
            self.attribute = attribute
            self.domain = domain
            self.day = day
            self.true_value = true_value

    def make_stock(self, source, object, attribute, domain, day, true_value):
            return self.Struct(source, object, attribute, domain, day, true_value)

    def tostring(self, source, object, attribute, domain, day, true_value):
        source_str, object_str, attribute_str, domain_str, day_str, true_value_str = '', '', '', '', '', ''
        for i in range(len(source)):
            source_str += ('*' + source[i])
            object_str += ('*' + object[i])
            attribute_str += ('*' + attribute[i])
            domain_str += ('*' + domain[i])
            day_str += ('*' + day[i])
        for i in range(len(true_value)):
            true_value_str += true_value[i]
        return source_str + ' ' + object_str + ' ' + attribute_str + ' ' + domain_str + ' ' + day_str + ' ' + true_value_str


def read_data():
    data = []
    with open("E:\\stock\\clean_stock\\alldataset_stock_sampled.csv", 'r') as f:
        reader = csv.reader(f)
        for row in reader:
            data.append(row)
    source, object, attribute, domain, day = [], [], [], [], []
    true_value = []
    for i in range(1, len(data)-1):
        source.append(data[i][source_index])
        object.append(data[i][object_index])
        attribute.append(data[i][attribute_index])
        domain.append(data[i][domain_index])
        day.append(data[i][day_index])
        if data[i][object_index] != data[i+1][object_index]:
            my_stock = Stock()
            temp = my_stock.make_stock(source, object, attribute, domain, day, true_value)
            stock.append(temp)
            source, object, attribute, domain, day, true_value = [], [], [], [], [], []
    source, object, attribute, domain, day, true_value = [], [], [], [], [], []
    source.append(data[len(data)-1][source_index])
    object.append(data[len(data)-1][object_index])
    attribute.append(data[len(data)-1][attribute_index])
    domain.append(data[len(data)-1][domain_index])
    day.append(data[len(data)-1][day_index])
    my_stock = Stock()
    temp = my_stock.make_stock(source, object, attribute, domain, day, true_value)
    stock.append(temp)
    '''
    for i in range(len(camera)):
        my_camera = Camera()
        print(my_camera.tostring(camera[i].id, camera[i].website, camera[i].number, camera[i].image_resolution, camera[i].screen_size, camera[i].brand, camera[i].true_value))
    '''


def min(a, b):
    if a < b:
        return a
    else:
        return b


def wil_test(T, x, s1, s2):
    # slimfast
    y1, y2 = [], []
    for i in range(21):
        print("i", i)
        result1 = slimfast.model_DA1(T[i], s1, x[i], str(i+1), 1e-6)
        result2 = slimfast.model_DA1(T[i], s2, x[i], str(i+1), 1e-6)
        y1.append(result1)
        y2.append(result2)
    print(y1)
    print(y2)

    # wilcoxon test
    sign, ab, rank = [], [], []
    ab_rank = [0 for i in range(21)]
    for i in range(21):
        ab.append(np.abs(y1[i]-y2[i]))
        ab_rank[i] = ab[i]
        if y1[i] > y2[i]:
            sign.append(1)
        elif y1[i] < y2[i]:
            sign.append(-1)
        else:
            sign.append(0)
    ab_rank.sort()
    print(ab_rank)
    sum, num = 0, 0
    dic = {}
    for i in range(21):
        if i < 20 and ab_rank[i+1] != ab_rank[i]:
           sum += i
           num += 1
           dic[ab_rank[i]] = sum/num
           sum, num = 0, 0
        else:
            sum += i
            num += 1
        if i == 20:
            dic[ab_rank[i]] = sum / num
    R1, R2 = 0, 0
    for i in range(21):
        rank.append(dic[ab[i]])
        if sign[i] > 0:
            R1 += rank[i]
        elif sign[i] < 0:
            R2 += rank[i]
        else:
            R1 += 0.5 * rank[i]
            R2 += 0.5 * rank[i]
    print(R1, R2)
    T_w = min(R1, R2)
    z = (T_w - 1.5) / ((1.25)**0.5)
    return z, y1, y2


def active_augmentation():
    # 赋U的值
    U = [[0 for j in range(21)] for i in range(100)]
    j = 0
    for i in range(len(stock)):
        my_stock = Stock()
        U[j][int(stock[i].day[0])-1] = stock[i]
        j += 1
        if j == 100:
            j = 0
    print(len(U))

    decimation_rate = 0.2
    # 从U中选择一部分数据用于初次训练
    T = [[] for i in range(100)]
    T1 = [[] for i in range(21)]
    T0 = [[14, 87, 91, 109, 7, 139, 182, 101, 58, 112],
         [14, 87, 91, 109, 7, 139, 182, 101, 58, 112],
         [509, 419, 75, 358, 503, 291, 260, 239, 563, 14],
         [117, 569, 406, 109, 214, 179, 118, 74, 435, 310],
         [110, 484, 116, 503, 177, 314, 347, 357, 434, 70],
         [563, 374, 33, 92, 503, 317, 339, 271, 434, 90],
         [360, 141, 41, 560, 669, 318, 4, 523, 1022, 797],
         [182, 87, 274, 365, 341, 369, 33, 16, 745, 1512],
         [509, 435, 154, 31, 414, 219, 359, 413, 171, 234],
         [821, 334, 302, 278, 916, 141, 109, 72, 155, 348],
         [399, 171, 200, 77, 511, 269, 7, 9, 219, 264],
         [358, 105, 374, 39, 194, 66, 339, 482, 445, 266],
         [381, 448, 156, 366, 131, 379, 340, 313, 201, 463],
         [128, 640, 260, 544, 297, 29, 350, 176, 155, 229],
         [163, 606, 414, 555, 92, 304, 1190, 22, 348, 88],
         [109, 291, 427, 3, 41, 147, 542, 73, 414, 97],
         [11, 361, 258, 617, 159, 474, 258, 184, 31, 266],
         [328, 171, 279, 201, 182, 414, 580, 171, 214, 372],
         [514, 433, 799, 817, 549, 165, 1336, 743, 206, 3, 1033, 2645],
         [492, 671, 30, 409, 45, 1087, 351, 3, 105, 116],
         [529, 720, 481, 5, 307, 590, 219, 412, 498, 322]]

    j = 0
    for i in range(len(U)):
        for k in range(21):
            # 我们只要前10个对象，也就是说T是21*10
            if int(U[i][k].object[0]) <= 100:
                # 专家标注T
                # print("请输入第", i, "天的truth:")
                # stri = input()
                my_stock = Stock()
                U[i][k].true_value = str(T0[k][i])
                T1[int(U[i][k].day[0])-1].append(U[i][k])
    for i in range(10):
        T[i] = U[i]

    object_inter = {}
    for i in range(len(U)):
        object_inter[U[i][0].object[0]] = i
    # B = |T|
    B = len(T)
    print("B:", B)

    # U = U - T
    for i in range(10):
        U.remove(U[0])
    print("len(U):", len(U))

    b = 0
    ac, st = [], []

    source_i = []
    for i in range(55):
        source_i.append(i+1)
    while b <= 2*B and stock != []:
        confidence = []
        m = []
        index = []
        for i in range(len(U)):
            # 随机抽取10个源
            source_selected0 = random.sample(source_i, 10)
            source_selected1 = random.sample(source_i, 10)
            source_selected0.sort()
            source_selected1.sort()
            # wilxcon test
            # 把21天看作一个整体，对象相对应，也就是说每个对象在21天中都有一个实体
            # 抽两次源，21*2用wilxcon test，输出的z也就是这个对象（样本）的置信度
            # 输入:21天的T的集合,这个样本21的声明,两次抽取源的集合
            # 输出:该样本的置信度
            print("start wilxcon:", i, len(T1))
            z, m1, m2 = wil_test(T1, U[i], source_selected0, source_selected1)
            confidence.append(z)
            m.append(m1)

        print("confidence:", confidence)
        for i in range(len(U)):
            if confidence[i] < p1:
                ac.append(U[i])
                index.append(i)
            if confidence[i] > p2:
                for j in range(21):
                    U[i][j].true_value = m[i][j]
                st.append(U[i])
                T[object_inter[U[i][0].object[0]]] = U[i]
                index.append(i)
        print("ml")
        if ac == []:
            break

        for i in range(len(ac)):
            for j in range(21):
                # 专家标注T
                print("object:", ac[i][j].object[0], "day:", ac[i][j].day[0])
                stri = input()
                my_stock = Stock()
                ac[i][j].true_value = stri
                T1[int(ac[i][j].day[0]) - 1].append(ac[i][j])
            T[object_inter[ac[i][0].object[0]]] = ac[i]
        b += len(ac)
        ac = []
        for i in range(len(index)):
            U.remove(U[index[len(index)-i-1]])
    for i in range(len(U)):
        T[object_inter[U[i][0].object[0]]] = U[i]
    pre_truth, ground_truth = [], []
    for i in range(len(T)):
        print("len",i, len(T[i]))
    for i in range(21):
        for j in range(len(T)):
            print(j,i)
            print(j, i, T[j][i])
            if T[j][i].true_value != '':
                pre_truth.append(int(T[j][i].true_value))
            else:
                pre_truth.append(0)
    with open("E:\\stock\\clean_stock\\alltruthfile_stock_sampled.csv", 'r') as f:
        reader = csv.reader(f)
        for row in reader:
            if row[1] != '':
                ground_truth.append(int(row[1]))
            else:
                ground_truth.append(0)
    for i in range(len(pre_truth)):
        print(pre_truth[i], ground_truth[i])
    print("RMSE:", np.sqrt(metrics.mean_squared_error(np.array(ground_truth), np.array(pre_truth))))
    print("R2_square:", r2_score(np.array(ground_truth), np.array(pre_truth)))


def calculate():
    # 赋U的值
    U = [[0 for j in range(21)] for i in range(100)]
    j = 0
    for i in range(len(stock)):
        my_stock = Stock()
        U[j][int(stock[i].day[0]) - 1] = stock[i]
        j += 1
        if j == 100:
            j = 0
    print(len(U))

    decimation_rate = 0.2
    # 从U中选择一部分数据用于初次训练
    T = [[] for i in range(100)]
    T1 = [[] for i in range(21)]
    T0 = [[14, 87, 91, 109, 7, 139, 182, 101, 58, 112],
          [14, 87, 91, 109, 7, 139, 182, 101, 58, 112],
          [509, 419, 75, 358, 503, 291, 260, 239, 563, 14],
          [117, 569, 406, 109, 214, 179, 118, 74, 435, 310],
          [110, 484, 116, 503, 177, 314, 347, 357, 434, 70],
          [563, 374, 33, 92, 503, 317, 339, 271, 434, 90],
          [360, 141, 41, 560, 669, 318, 4, 523, 1022, 797],
          [182, 87, 274, 365, 341, 369, 33, 16, 745, 1512],
          [509, 435, 154, 31, 414, 219, 359, 413, 171, 234],
          [821, 334, 302, 278, 916, 141, 109, 72, 155, 348],
          [399, 171, 200, 77, 511, 269, 7, 9, 219, 264],
          [358, 105, 374, 39, 194, 66, 339, 482, 445, 266],
          [381, 448, 156, 366, 131, 379, 340, 313, 201, 463],
          [128, 640, 260, 544, 297, 29, 350, 176, 155, 229],
          [163, 606, 414, 555, 92, 304, 1190, 22, 348, 88],
          [109, 291, 427, 3, 41, 147, 542, 73, 414, 97],
          [11, 361, 258, 617, 159, 474, 258, 184, 31, 266],
          [328, 171, 279, 201, 182, 414, 580, 171, 214, 372],
          [514, 433, 799, 817, 549, 165, 1336, 743, 206, 3, 1033, 2645],
          [492, 671, 30, 409, 45, 1087, 351, 3, 105, 116],
          [529, 720, 481, 5, 307, 590, 219, 412, 498, 322]]
    for i in range(len(U)):
        for k in range(21):
            # 我们只要前10个对象，也就是说T是21*10
            if int(U[i][k].object[0]) <= 100:
                # 专家标注T
                # print("请输入第", i, "天的truth:")
                # stri = input()
                my_stock = Stock()
                U[i][k].true_value = str(T0[k][i])
                T1[int(U[i][k].day[0])-1].append(U[i][k])
    for i in range(10):
        T[i] = U[i]
    source_i = []
    for i in range(55):
        source_i.append(i + 1)
    y1 = []
    m = []
    for i in range(10):
        U.remove(U[0])
    for i in range(len(U)):
        # 随机抽取10个源
        source_selected0 = random.sample(source_i, 10)
        source_selected0.sort()
        for j in range(21):
            print("i", i)
            result1 = slimfast.model_DA1(T1[j], source_selected0, U[i][j], str(j + 1), 1e-6)
            y1.append(result1)
        m.append(y1)
    for i in range(len(U)):
        for j in range(21):
            U[i][j].true_value = m[i][j]
    pre_truth, ground_truth = [], []
    for i in range(21):
        for j in range(10):
            if T[j][i].true_value != '':
                pre_truth.append(int(T[j][i].true_value))
            else:
                pre_truth.append(0)
    for i in range(21):
        for j in range(len(U)):
            if U[j][i].true_value != '':
                pre_truth.append(int(U[j][i].true_value))
            else:
                pre_truth.append(0)
    with open("E:\\stock\\clean_stock\\alltruthfile_stock_sampled.csv", 'r') as f:
        reader = csv.reader(f)
        for row in reader:
            if row[1] != '':
                ground_truth.append(int(row[1]))
            else:
                ground_truth.append(0)
    for i in range(len(pre_truth)):
        print(pre_truth[i], ground_truth[i])
    print("RMSE:", np.sqrt(metrics.mean_squared_error(np.array(ground_truth), np.array(pre_truth))))
    print("R2_square:", r2_score(np.array(ground_truth), np.array(pre_truth)))


if __name__ == '__main__':
    read_data()
    # z = wil_test([125, 115, 130, 140, 140, 115, 140, 125, 140, 135], [110, 122, 125, 120, 140, 124, 123, 137, 135, 145])
    #active_augmentation()
    calculate()
