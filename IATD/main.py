import csv
import numpy as np
import random
import proprocess
import time

all_num = 95
# hyper parameters setting
a_e, b_e, u_b, o_b, g_c, l, p, c = 10, 10, 0, 10, -10, 0.9, 0.3, 0.01
o = [0.1 for i in range(all_num)]
bv = [0 for i in range(all_num)]


o = [0.1 for i in range(all_num)]
bv = [0 for i in range(all_num)]
#bv = [int(np.random.normal(loc=0, scale=10, size=1)) for i in range(5499)]
attribute_index = 4
source_index = 1
object_index = 0
source_num = 5
object_num = 20
truth_index = 2
source = []
object = []
object2 = []
attribute = []
related_oj = [[0 for i in range(source_num)] for j in range(115240)]


# 计算逻辑回归函数
def h(t):
    return np.exp(t)/(1+np.exp(t))


# derta函数，相等为1，不等为0
def derta(a, b):
    if a == b:
        return 1
    else:
        return 0


# 计算真值，对于每一个对象，需要计算该对象所有声明为真的概率（14）
# 真值的
# 首先需要计算条件概率
#    第一步：计算gama（每个对象不同声明的个数）
#    第二步：计算条件概率（7）
#    第三步：计算条件概率的乘积
#    第四步：计算后验概率（某个对象的某个声明的个数占所有声明数的比值）
#    第五步：计算先验概率
# 输出最大条件概率对应的声明
def sample_tv(tv):
    p_con = [0 for _ in range(all_num)]
    gama = [{} for _ in range(object_num)]
    j = 0
    k = -1
    for i in range(1, len(object)):
        if j != int(object[i]):
            j = int(object[i])
            k += 1
            if k != -1 and attribute[i] != '':
                if attribute[i] not in gama[k]:
                    gama[k][attribute[i]] = 1
                else:
                    gama[k][attribute[i]] += 1
        else:
            if attribute[i] != '':
                if attribute[i] not in gama[k]:
                    gama[k][attribute[i]] = 1
                else:
                    gama[k][attribute[i]] += 1
    print("gama", gama)

    k = -1
    j = 0
    for i in range(1, len(object)):
        if int(object[i]) != j:
            k += 1
            j = int(object[i])
        if attribute[i] != '':
            if len(gama[k]) - 1 == 0:
                p_con[i] = 1
            else:
                p_con[i] = h(-o[i] + bv[i] + g_c) ** derta((attribute[i]), tv[k]) * ((1 - h(-o[i] + bv[i] + g_c))/(len(gama[k]) - 1)) ** (1-derta((attribute[i]), tv[k]))
    #print("p_con:", p_con)
    k = -1
    j = 0
    p_mul = [{} for _ in range(object_num)]
    for i in range(1, len(object)):
        if int(object[i]) != j:
            k += 1
            j = int(object[i])
        if attribute[i] != '':
            if attribute[i] not in p_mul[k]:
                p_mul[k][attribute[i]] = p_con[i]
            else:
                p_mul[k][attribute[i]] *= p_con[i]
    p_post = [{} for _ in range(object_num)]
    for i in range(len(gama)):
        key_sum = 0
        for key in gama[i]:
            key_sum += gama[i][key]
        for key in gama[i]:
            p_post[i][key] = gama[i][key]/key_sum
    print("p_mul:", p_mul)
    print("p_post:", p_post)
    k = -1
    j = 0
    p_rec = [{} for _ in range(object_num)]
    for i in range(object_num):
        max_p = 0
        max_t = 0
        for key in p_mul[i]:
            if max_p < p_mul[i][key] * p_post[i][key]:
                max_p = p_mul[i][key] * p_post[i][key]
                max_t = (key)
        tv[i] = max_t
    return tv


def calculate_distance(A, tv, file, flag):
    if flag == 0:
        row_num = 0
        with open(str(file), 'r', encoding='utf-8') as data:
            reader = csv.reader(data)
            for row in reader:
                if row_num == 0:
                    name = row[0]
                row_num += 1
        k = -1
        for i in range(1, len(o)):
            if object[i] != object[i-1]:
                start = i
                k += 1
            j = start
            while object[j] == object[start]:
                if i != j and attribute[i] == attribute[j] and attribute[i] != tv[k]:
                    A[int(source[i])-1][k].append(int(source[j]))
                j = j + 1
                if j == len(o):
                    j = 1
    else:
        inter = {}
        k = -1
        for i in range(1, len(o)):
            if object[i] != object[i - 1]:
                k += 1
                inter[object[i]] = k
        f = open("tv.csv", 'w', newline="")
        csv_writer = csv.writer(f)
        for row in range(len(tv)):
            csv_writer.writerow([tv[row]])
        f.close()
        #passs()
        A_temp = []
        row_num = 0
        with open(str(file), 'r', encoding='utf-8') as data:
            reader = csv.reader(data)
            for row in reader:
                if row_num == 0:
                    name = row[0]
                else:
                    A_temp.append(row)
                row_num += 1
        A_temp1 = []
        for i in range(len(A_temp)):
            k = 1
            for j in range(len(A_temp[i])):
                if A_temp[i][j] != ';':
                    if ':' in A_temp[i][j] or ';' in A_temp[i][j]:
                        if j == 0:
                            temp = []
                            temp.append(int(A_temp[i][j][-1]))
                        else:
                            A[i][inter[object[k]]] = temp
                            k += 1
                            temp = []
                            temp.append(int(A_temp[i][j][-1]))
                    else:
                        temp.append(int(A_temp[i][j]))
                else:
                    A[i][inter[object[k]]] = temp
    return A, name


def calculate_Esv(os, oj, tv, file, flag):
    A = [[[] for i in range(object_num)] for j in range(source_num)]
    A, name = calculate_distance(A, tv, file, flag)
    inter = {}
    k = -1
    for i in range(1, len(o)):
        if object[i] != object[i - 1]:
            k += 1
            inter[object[i]] = k
    for i in range(1, len(object)):
        sum = 0
        if A[int(source[i])-1][inter[object[i]]] != []:
            for j in range(len(A[int(source[i])-1][inter[object[i]]])):
                sum = sum + oj[i][A[int(source[i])-1][inter[object[i]]][j]-1]
            o[i] = l / len(A[int(source[i])-1][inter[object[i]]]) * sum + (1 - l) * os[i]
        else:
            o[i] = os[i]
    return name


def renew_osj(os, oj, tv, file, flag_a):
    inter = {}
    k = -1
    for i in range(1, len(o)):
        if object[i] != object[i - 1]:
            k += 1
            inter[object[i]] = k
    # source_provide start from 1
    # and means the number of entities the source provide
    source_provide = [0 for i in range(source_num + 1)]
    provide_object = [0 for i in range(object_num)]
    for i in range(1, len(object)):
        source_provide[int(source[i])] = source_provide[int(source[i])] + 1
        provide_object[inter[object[i]]] = provide_object[inter[object[i]]] + 1

    A = [[[] for i in range(object_num)] for j in range(source_num)]
    A, name = calculate_distance(A, tv, file, flag_a)

    # calculate the osj
    osj = [[0.1 for i in range(source_num+2)] for j in range(len(object))]
    for i in range(1, len(object)):
        osj[i][1] = os[i]
        for j in range(2, source_num+2):
            osj[i][j] = oj[i][j-2]
    # Calculate the partial derivative for os
    e1 = [0 for i in range(len(object))]
    k = -1
    for i in range(1, len(object)):
        if int(object[i]) != j:
            k += 1
            j = int(object[i])
        if attribute[i] != '' and len(A[int(source[i])-1][inter[object[i]]]) > 0:
            e1[i] = (l - 1) * (h(-o[i] + bv[i] + g_c) - derta((attribute[i]), tv[k])) + (2 * (1 + a_e) / os[i] - 2 * b_e / (os[i] ** 3)) / \
                    source_provide[int(source[i])]
        if len(A[int(source[i]) - 1][inter[object[i]]]) == 0 and attribute[i] != '':
            e1[i] = -(h(-o[i] + bv[i] + g_c) - derta((attribute[i]), tv[k])) + (2 * (1 + a_e) / os[i] - 2 * b_e / (os[i] ** 3)) / \
                    source_provide[int(source[i])]

    # print("es=", e1)

    # calculate the partial derivative for oj
    e2 = [0 for i in range(len(object))]
    k = -1
    for i in range(1, len(object)):
        if int(object[i]) != j:
            k += 1
            j = int(object[i])
        if attribute[i] != '' and len(A[int(source[i])-1][inter[object[i]]]) > 0:
            e2[i] = -l * (h(-o[i] + bv[i] + g_c) - derta((attribute[i]), tv[k])) / len(A[int(source[i])-1][inter[object[i]]])
        if len(A[int(source[i])-1][inter[object[i]]]) == 0:
            e2[i] = 0
    # print("ej=", e2)

    # Calculate the partial derivative for bv
    e3 = [0 for i in range(len(object))]
    for i in range(1, len(object)):
        if attribute[i] != '':
            e3[i] = h(-o[i] + bv[i] + g_c) - derta((attribute[i]), tv[k]) + (bv[i] - u_b) / provide_object[inter[object[i]]] / o_b
    # print("ebv=", e3)
    # calculate t and osj(k+1)
    aaa = 0
    for i in range(1, len(object)):
        flag = 0
        t = 0
        # represent osj(k+1)
        os_temp = [0 for i in range(source_num+2)]
        while flag == 0:
            # calculate the osj(k+1)
            # print("i:", i, "e:", e1[i], e2[i])
            os_temp[1] = osj[i][1] - (p ** t) * e1[i]
            for j in range(2, source_num + 2):
                os_temp[j] = osj[i][j] - (p ** t) * e2[i]
            for j in range(1, source_num + 2):
                if os_temp[j] > 0.00001:
                    os_temp[j] = os_temp[j]
                else:
                    os_temp[j] = 0.00001
            temp = e1[i] * (os_temp[1] - osj[i][1])
            for j in range(2, source_num+2):
                temp = temp + e2[i] * (os_temp[j] - osj[i][j])
            # print("c*temp", c*temp)
            # print("差：   ", L(i, os_temp, A, source_provide[int(source[i])], provide_object[int(object[i])])-L(i, osj[i], A, source_provide[int(source[i])], provide_object[int(object[i])]))
            if L(i, inter[object[i]], os_temp, A, source_provide[int(source[i])], provide_object[inter[object[i]]])-L(i, inter[object[i]], osj[i], A, source_provide[int(source[i])], provide_object[inter[object[i]]]) <= c * temp:
                # print("ll")
                for j in range(1, source_num+2):
                    osj[i][j] = os_temp[j]
                    oj[i][j-2] = osj[i][j]
                os[i] = osj[i][1]
                flag = 1
                bv[i] = bv[i] - (p ** t) * e3[i]
            else:
                t = t + 1
            if aaa == 10000:
                # print(str(i/len(object)*100)+' %')
                aaa = 0
            aaa += 1


# index:the index of object, x:osj, num:source_provide, provide_object
def L(ind, index, x, A, num1, num2):
    result = 0
    o_temp = x[1] * (1 - l)
    if len(A[int(source[ind])-1][index]) != 0:
        for i in range(len(A[int(source[ind])-1][index])):
            o_temp = o_temp + l * x[A[int(source[ind])-1][index][i]+1] / len(A[int(source[ind])-1][index])
    else:
        o_temp = x[1]
    if attribute[ind] != '':
        if h(-o_temp+bv[ind] + g_c) > 1e-10:
            result = result - derta((attribute[ind]), tv[index]) * np.log(h(-o_temp+bv[ind] + g_c))
        else:
            result = result - derta((attribute[ind]), tv[index]) * np.log(1e-10)
        result = result - (1 - derta((attribute[ind]), tv[index])) * np.log(1 - h(-o_temp+bv[ind] + g_c))
        result = result + (2*(1+a_e)*np.log(x[1]) + b_e/(x[1]**2))/num1
        result = result + 0.5*((bv[ind-1]-u_b)**2)/o_b/num2
    return result


# calculate correlation
def sim(i, j):
    source_i = source[i]
    source_j = source[j]
    flag = 0
    w1 = 0
    w2 = 0
    w12 = 0
    for k in range(1, len(object)):
        if object[i] != object[i-1]:
            flag = 0
        if source[k] == source_i:
            if int(attribute[k]) != tv[int(object[k])]:
                w1 = w1 + 1
                flag = flag + 1
        if source[k] == source_j:
            if int(attribute[k]) != tv[int(object[k])]:
                w2 = w2 + 1
                flag = flag + 1
        if flag == 2:
            w12 = w12 + 1
            flag = 0
    result = w12 / (w1 + w2 + w12)
    return result


def top_k():
    start = []
    gama = [{} for _ in range(object_num)]
    j = 0
    k = -1
    for i in range(1, len(object)):
        if j != int(object[i]):
            j = int(object[i])
            if k != -1 and attribute[i] != '':
                if attribute[i] not in gama[k]:
                    gama[k][attribute[i]] = 1
                else:
                    gama[k][attribute[i]] += 1
            k += 1
        else:
            if attribute[i] != '':
                if attribute[i] not in gama[k]:
                    gama[k][attribute[i]] = 1
                else:
                    gama[k][attribute[i]] += 1
    print("gama:", gama)
    for i in range(len(gama)):
        gama[i] = sorted(gama[i].items(), key=lambda x:x[1], reverse=True)
    print("gama:", gama)
    for i in range(len(gama)):
        j = 0
        temp = []
        for key in gama[i]:
            if j < 1:
                temp.append(key)
            j += 1
        start.append(random.choice(temp))
    return start


if __name__ == '__main__':
    start = time.time()
    file_to_read = "E:\GitHub\KRAUSTD\CTD\log\Tri\IATD\sourceList 0.2_0.196_.txt"
    f = open("1.txt", 'r')
    flag_a = int(f.read()[-1])
    # read data
    with open("monitor_original.csv", 'r', encoding='utf-8') as data:
        reader = csv.reader(data)
        index = 0
        for row in reader:
            index = index + 1
            source.append(row[source_index])
            object.append(row[object_index])
            attribute.append(row[attribute_index])
            if index == all_num:
                break
    object2 = list(set(object[1:]))
    for i in range(len(object2)):
        object2[i] = int(object2[i])
    for i in range(len(object2)):
        for j in range(i, len(object2)):
            if object2[i] > object2[j]:
                temp = object2[i]
                object2[i] = object2[j]
                object2[j] = temp

    print("source:", source)
    print("attribute:", attribute)
    print("object:", object2)

    flag = 1
    m = 0
    t0 = [1000 for i in range(object_num)]
    tv = top_k()
    print("tv_start:", tv)
    while flag == 1:
        # calculate tv
        tv = sample_tv(tv)

        # calculate Esv
        oj = [[0.1 for i in range(source_num)] for j in range(len(object))]

        # 这里可以更换成embedding的值
        os = [0.1 for i in range(len(object))]
        name = calculate_Esv(os, oj, tv, file_to_read, flag_a)
        print("?")

        # calculate os, oj, bv
        renew_osj(os, oj, tv, file_to_read, flag_a)
        print("good")

        # print values
        m = m + 1
        print("m=", m)
        print("tv=", tv)
        flag = 0
        min_gap = 10000000
        for i in range(object_num):
            if (os[i] - os[i]) >= 1 or (os[i] - os[i]) <= -1:
                if np.abs(os[i] - os[i]) < min_gap:
                    min_gap = np.abs(t0[i] - tv[i])
                flag = 1
        print("gap:", min_gap)
        if min_gap < 1.2:
            flag = 0
        for i in range(len(tv)):
            t0[i] = tv[i]
    k = 0
    out = []
    for i in range(len(tv)):
        if tv[i] != 0:
            #tv[i] = tv[i] + average[k]
            k += 1
            out.append([object2[i], tv[i]])
        else:
            tv[i] = 0
    print("tv(final)=", tv)
    print("name:", name)
    f = open(name+".csv", 'w', newline="")
    csv_writer = csv.writer(f)
    for row in range(len(out)):
        csv_writer.writerow(out[row])
    f.close()
    proprocess.process(name + ".csv")

    end = time.time()
    print("time for IATD:", end - start)


    truth = []
    with open("monitor_truth.csv", 'r', encoding='utf-8') as data:
        reader = csv.reader(data)
        index = 0
        for row in reader:
            temp = row[truth_index].split(',')
            for j in range(len(temp)):
                temp[j] = temp[j].strip()
                temp[j] = temp[j].strip('[')
                temp[j] = temp[j].strip(']')
                temp[j] = temp[j].strip('\'')
            truth.append(temp)
    error_num, total_num = 0, 0
    for i in range(len(tv)):
        temp = tv[i].split(',')
        for j in range(len(temp)):
            if temp[j] not in truth[i]:
                error_num += 1
        total_num += len(temp)
    print("error_rate:", error_num/total_num, error_num, total_num)
# time:0.17339348793029785+0.1566298007965088+0.10737729072570801+0.1598656177520752
# error_rate:0.35 0 0.05 0
