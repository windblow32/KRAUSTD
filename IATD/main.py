import csv
import numpy as np
import random
import proprocess
import judge_attribute
import time
import os


a_e, b_e, u_b, o_b, g_c, l, p, c = 10, 10, 0, 10, 10, 0.9, 0.3, 0.01


# 计算实体所在列索引数据源个数、实体个数
# 计算需要用的source, attribute_index
# 读取source文件夹的所有文件
def find_index(source_tang):
    # 数据源文件夹
    source_path = 'E:/GitHub/KRAUSTD/CTD/' + source_tang + '/source'
    # 遍历source中的所有文件
    source_dirs = os.listdir(source_path)

    # object_index:在每个source文件的第一行找'entity'
    # source_num:查找source文件夹中带'source'的文件的个数
    # object_num:用数组查找(to_find_object_num)
    source_num, object_num = 0, 0
    source, attribute_index = ['source'], []
    to_find_object_num = []
    for source_dir in source_dirs:
        if 'source' in source_dir:
            source_num += 1
            with open(source_path + '/' + source_dir, 'r', encoding='utf-8') as data:
                reader = csv.reader(data)
                # k用来判断第几行，从0开始
                k = 0
                for row in reader:
                    if k == 0:
                        for j in range(len(row)):
                            if row[j] == 'entity':
                                object_index = j
                            if row[j] != 'entity' and row[j] != 'day' and j not in attribute_index:
                                attribute_index.append(j)
                    else:
                        if row[object_index] not in to_find_object_num:
                            to_find_object_num.append(row[object_index])
                            object_num += 1
                    k += 1
    for i in range(object_num):
        for j in range(source_num):
            source.append(j+1)
    return object_index, source_num, object_num, source, attribute_index


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
def sample_tv(tv, source_num, object_num, all_num, o, bv, source, object, attribute):
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

    # 计算公式7
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


def calculate_distance(A, tv, file, flag, source_num, object_num, all_num, o, bv, source, object, attribute):
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
        for i in range(0, len(o)):
            if object[i] != object[i - 1]:
                k += 1
                inter[object[i]] = k
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
        for i in range(len(A_temp)):
            k = 1
            for j in range(len(A_temp[i])):
                if A_temp[i][j] != ';':
                    if ':' in A_temp[i][j] or ';' in A_temp[i][j]:
                        if j == 0:
                            temp = []
                            if A_temp[i][j][-1] != ';':
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


def calculate_Esv(os, oj, tv, file, flag, source_num, object_num, all_num, o, bv, source, object, attribute):
    A = [[[] for i in range(object_num)] for j in range(source_num)]
    A, name = calculate_distance(A, tv, file, flag, source_num, object_num, all_num, o, bv, source, object, attribute)
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


def renew_osj(os, oj, tv, file, flag_a, source_num, object_num, all_num, o, bv, source, object, attribute):
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
    A, name = calculate_distance(A, tv, file, flag_a, source_num, object_num, all_num, o, bv, source, object, attribute)

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
    for i in range(1, len(object)):
        flag = 0
        t = 0
        os_temp = [0 for i in range(source_num+2)]
        while flag == 0:
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
            if L(tv, i, inter[object[i]], os_temp, A, source_provide[int(source[i])], provide_object[inter[object[i]]], source_num, object_num, all_num, o, bv, source, object, attribute)-L(tv, i, inter[object[i]], osj[i], A, source_provide[int(source[i])], provide_object[inter[object[i]]], source_num, object_num, all_num, o, bv, source, object, attribute) <= c * temp:
                for j in range(1, source_num+2):
                    osj[i][j] = os_temp[j]
                    oj[i][j-2] = osj[i][j]
                os[i] = osj[i][1]
                flag = 1
                bv[i] = bv[i] - (p ** t) * e3[i]
            else:
                t = t + 1


# index:the index of object, x:osj, num:source_provide, provide_object
def L(tv, ind, index, x, A, num1, num2, source_num, object_num, all_num, o, bv, source, object, attribute):
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


def top_k(object_num, all_num, o, bv, source, object, attribute):
    start = []
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
    # 把每个实体的桶按照声明的数量顺序排序
    for i in range(len(gama)):
        gama[i] = sorted(gama[i].items(), key=lambda x:x[1], reverse=True)
    for i in range(len(gama)):
        j = 0
        temp = []
        for key_value in gama[i]:
            if j < 2:
                temp.append(key_value[0])
            j += 1
        start.append(random.choice(temp))
    return start


def run():
    # 判断数据集
    file_to_read = r"E:\GitHub\KRAUSTD\CTD\log\Tri\IATD\sourceList 0.2_0.196_.txt"
    with open(str(file_to_read), 'r', encoding='utf-8') as data:
        reader = csv.reader(data)
        k = 0
        for row in reader:
            if '/' in row:
                source_tang = row
            if k == 0:
                flag_a = str(row[0])
            k += 1
    data.close()

    source_tang = "data/monitor0707"



    # 计算一些超参
    object_index, source_num, object_num, source, attribute_index = find_index(source_tang)
    all_num = source_num * object_num + 1

    o = [0.1 for i in range(all_num)]
    bv = [0 for i in range(all_num)]

    for attribute_i in attribute_index:
        object, object_no_repeat, attribute = ['entity'], [], ['attribute']
        object_source_sort, attribute_source_sort = [], []
        start = time.time()

        import os
        # 计算object attribute
        source_path = 'E:/GitHub/KRAUSTD/CTD/' + source_tang + '/source'
        source_dirs = os.listdir(source_path)
        for source_dir in source_dirs:
            if 'source' in source_dir:
                with open(source_path + '/' + source_dir, 'r', encoding='utf-8') as data:
                    reader = csv.reader(data)
                    # k用来判断第几行，从0开始
                    k = 0
                    for row in reader:
                        if k != 0:
                            object_source_sort.append(row[object_index])
                            attribute_source_sort.append(row[attribute_i])
                            if row[object_index] not in object_no_repeat:
                                object_no_repeat.append(row[object_index])
                        k += 1
        for i in range(object_num):
            for j in range(source_num):
                object.append(object_source_sort[i + j * object_num])
                attribute.append(attribute_source_sort[i + j * object_num])

        flag = 1
        m = 0
        t0 = [1000 for i in range(object_num)]
        os0 = [0.1 for i in range(len(object))]
        tv = top_k(object_num, all_num, o, bv, source, object, attribute)
        print(tv)
        while flag == 1:
            # calculate tv
            tv = sample_tv(tv, source_num, object_num, all_num, o, bv, source, object, attribute)

            # calculate Esv
            oj = [[0.1 for i in range(source_num)] for j in range(len(object))]
            os = [0.1 for i in range(len(object))]
            name = calculate_Esv(os, oj, tv, file_to_read, flag_a, source_num,object_num, all_num, o, bv, source, object, attribute)

            # calculate os, oj, bv
            renew_osj(os, oj, tv, file_to_read, flag_a, source_num, object_num, all_num, o, bv, source, object, attribute)

            # print values
            m = m + 1
            print("---------------------------------------")
            print("迭代轮数：", m)
            print("tv=", tv)

            # 计算gap
            flag = 0
            gap = 0
            for i in range(1, object_num):
                gap += np.abs(os0[i] - os[i])
                flag = 1
            print("gap:", gap)
            if gap < 1.2 or m > 10:
                flag = 0
            for i in range(len(tv)):
                t0[i] = tv[i]
            os0 = [os[i] for i in range(len(object))]
        k = 0
        out = []
        for i in range(len(tv)):
            if tv[i] != 0:
                k += 1
                out.append([object_no_repeat[i], tv[i]])
            else:
                tv[i] = 0
        print("---------------------------------------")
        print("tv(final)=", tv)
        print("name:", name)
        f = open("E:\GitHub\KRAUSTD\IATD\\" + name + "-" + str(attribute_i) +".csv", 'w', newline="")
        csv_writer = csv.writer(f)
        for row in range(len(out)):
            csv_writer.writerow(out[row])
        f.close()
        proprocess.process("E:\GitHub\KRAUSTD\IATD\\" + name + "-" + str(attribute_i) + ".csv", source_tang)


        # 把结果拼接之后输出
        end = time.time()
        print("time for IATD:", end - start)
        # here!
        # 输出结果
    out = [[1 for i in range(len(attribute_index)+2)] for _ in range(len(tv))]
    for attribute_i in attribute_index:
        f = open("E:\GitHub\KRAUSTD\IATD\\" + name + "-" + str(attribute_i) + "-truth.csv", 'r', newline="")
        reader = csv.reader(f)
        po = 0
        for row in reader:
            out[po][0] = row[0]
            out[po][attribute_i] = row[1]
            po += 1
        f.close()

        f = open("E:\GitHub\KRAUSTD\IATD\\" + name + "_truth.csv", 'w', newline="")
        csv_writer = csv.writer(f)
        for row in range(len(out)):
            csv_writer.writerow(out[row])
        f.close()


if __name__ == '__main__':
    run()
    # # 传递参数:source_num, object_num, o, bv, source, object
    #
    # # 计算一些超参
    # object_index, source_num, object_num, source, attribute_index = find_index("data/monitor0707")
    # all_num = source_num * object_num + 1
    #
    # o = [0.1 for i in range(all_num)]
    # bv = [0 for i in range(all_num)]
    # object_index += 1
    # for attribute_index in range(3, 5):
    #     object = []
    #     object2 = []
    #     attribute = []
    #     start = time.time()
    #     file_to_read = "E:\GitHub\KRAUSTD\CTD\log\Tri\IATD\sourceList 0.1_0.1_.txt"
    #     f = open(r"E:\GitHub\KRAUSTD\IATD\1.txt", 'r')
    #     flag_a = int(f.read()[-1])
    #     # read data
    #     # todo
    #     # object2:无重复的object
    #     with open("E:\\GitHub\\KRAUSTD\\IATD\\data\\monitor_ori.csv", 'r', encoding='utf-8') as data:
    #         reader = csv.reader(data)
    #         index = 0
    #         for row in reader:
    #             index = index + 1
    #             object.append(row[object_index])
    #             attribute.append(row[attribute_index])
    #             if index == all_num:
    #                 break
    #     object2 = list(set(object[1:]))
    #     for i in range(len(object2)):
    #         object2[i] = str(object2[i])
    #     for i in range(len(object2)):
    #         for j in range(i, len(object2)):
    #             if object2[i] > object2[j]:
    #                 temp = object2[i]
    #                 object2[i] = object2[j]
    #                 object2[j] = temp
    #     print(object)
    #     print(object2)
    #     flag = 1
    #     m = 0
    #     t0 = [1000 for i in range(object_num)]
    #     os0 = [0.1 for i in range(len(object))]
    #     tv = top_k(object_num, all_num, o, bv, source, object, attribute)
    #     print("tv_start:", tv)
    #     while flag == 1:
    #         # calculate tv
    #         tv = sample_tv(tv, source_num, object_num, all_num, o, bv, source, object, attribute)
    #
    #         # calculate Esv
    #         oj = [[0.1 for i in range(source_num)] for j in range(len(object))]
    #         os = [0.1 for i in range(len(object))]
    #         name = calculate_Esv(os, oj, tv, file_to_read, flag_a, source_num,object_num, all_num, o, bv, source, object, attribute)
    #
    #         # calculate os, oj, bv
    #         renew_osj(os, oj, tv, file_to_read, flag_a, source_num, object_num, all_num, o, bv, source, object, attribute)
    #
    #         # print values
    #         m = m + 1
    #         print("---------------------------------------")
    #         print("迭代轮数：", m)
    #         print("tv=", tv)
    #
    #         # 计算gap
    #         flag = 0
    #         gap = 0
    #         for i in range(1, object_num):
    #             gap += np.abs(os0[i] - os[i])
    #             flag = 1
    #         print("gap:", gap)
    #         if gap < 1.2 or m > 10:
    #             flag = 0
    #         for i in range(len(tv)):
    #             t0[i] = tv[i]
    #         os0 = [os[i] for i in range(len(object))]
    #     k = 0
    #     out = []
    #     for i in range(len(tv)):
    #         if tv[i] != 0:
    #             k += 1
    #             out.append([object2[i], tv[i]])
    #         else:
    #             tv[i] = 0
    #     print("---------------------------------------")
    #     print("tv(final)=", tv)
    #     print("name:", name)
    #     f = open("E:\GitHub\KRAUSTD\IATD\\" + name + "-" + str(attribute_index) + ".csv", 'w', newline="")
    #     csv_writer = csv.writer(f)
    #     for row in range(len(out)):
    #         csv_writer.writerow(out[row])
    #     f.close()
    #     proprocess.process("E:\GitHub\KRAUSTD\IATD\\" + name + "-" + str(attribute_index) + ".csv")
    #
    #     end = time.time()
    #     print("time for IATD:", end - start)
    #
    #
    # # 输出结果
    # out = [[0, 1, 0, 0, 0, 0, 1] for _ in range(len(tv))]
    # for attribute_index in range(3, 5):
    #     f = open("E:\GitHub\KRAUSTD\IATD\\" + name + "-" + str(attribute_index) + "_truth.csv", 'r', newline="")
    #     reader = csv.reader(f)
    #     po = 0
    #     for row in reader:
    #         out[po][0] = row[0]
    #         out[po][2] = row[1]
    #         po += 1
    #     f.close()
    # # todo
    # f = open("E:\\GitHub\\KRAUSTD\\IATD\\data\\monitor_ori_truth.csv")
    # reader = csv.reader(f)
    # po = 0
    # for row in reader:
    #     if po >= 0:
    #         out[po][1] = row[1]
    #         out[po][4] = row[4]
    #         out[po][5] = row[5]
    #     po += 1
    # f = open("E:\GitHub\KRAUSTD\IATD\\" + name + "_truth.csv", 'w', newline="")
    # csv_writer = csv.writer(f)
    # for row in range(len(out)):
    #     csv_writer.writerow(out[row])
    # f.close()
# time:0.17339348793029785+0.1566298007965088+0.10737729072570801+0.1598656177520752
# error_rate:0.35 0 0.05 0



