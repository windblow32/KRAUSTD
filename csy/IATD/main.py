# code for IATD by csy
import numpy as np
import csv

# hyper parameters setting
a_e, b_e, u_b, o_b, g_n, u_t, o_t, l, p, c = 0.05, 0.10, 0, 10, 0, 0, 100, 0.9, 0.3, 0.01
o = [0.1 for i in range(5499)]
bv = [0 for i in range(5499)]
attribute_index = 6
source_index = 0
object_index = 1
source_num = 55
object_num = 1000
source = []
object = []
attribute = []
related_oj = [[0 for i in range(source_num)] for j in range(115240)]

def print_hi(name):
    print(f'Hi, {name}')

def calculate_tv():
    # Calculate the denominatornumerator
    sum1 = 0
    # Calculate the numerator
    sum2 = 0
    j = 3
    tv = [0 for i in range(object_num)]
    for i in range(1, len(object)):
        if attribute[i] != '' and '.' not in attribute[i]:
            sum1 = sum1 + 1 / ((o[i] + bv[i] + g_n) ** 2)
            sum2 = sum2 + int(attribute[i]) * 1/((o[i] + bv[i] + g_n)**2)
        if i == len(object) - 1:
            result = (u_t / o_t + sum2) / (1 / o_t + sum1)
            tv[int(object[i]) - 1] = result
        if j != int(object[i]):
            j = int(object[i+1])
            result = (u_t*1/((o_t)**2)+sum2) / (1/((o_t)**2)+sum1)
            tv[int(object[i]) - 1] = result
            sum1 = 0
            sum2 = 0
    return tv

def caculate_distance(A, tv):
    print("len_tv", len(tv))
    print("tv=", tv)
    for i in range(1, len(o)):
        if object[i] != object[i-1]:
            start = i
        j = start
        while object[j] == object[start]:
            if attribute[i] == attribute[j] and attribute[i] != tv[int(object[start]) - 1]:
                A[int(source[i])-1][int(object[i])-1].append(int(source[j]))
            j = j + 1
            if j == len(o):
                j = 1
    return A

def calculate_Esv(os, oj, tv):
    A = [[[] for i in range(object_num)] for j in range(source_num)]
    A = caculate_distance(A, tv)
    for i in range(0, len(object)):
        sum = 0
        if A[int(source[i])-1][int(object[i])-1] != []:
            for j in range(len(A[int(source[i])-1][int(object[i])-1])):
                sum = sum + oj[i][int(source[j])-1]
            o[i] = l / len(A[int(source[i])-1][int(object[i])-1]) * sum + (1 - l) * os[i]
        else:
            o[i] = os[i]
    '''
    A, sum = caculate_A()
    for i in range(0, len(object)):
        if A[i] != 0:
            o[i] = l / A[i] * sum[i] + (1 - l) * os[i]
        else:
            o[i] = os[i]
    '''

def renew_os(os, oj, bv):
    # source_provide start from 1
    # and means the number of entities the source provide
    source_provide = [0 for i in range(source_num+1)]
    provide_object = [0 for i in range(object_num+1)]
    for i in range(1, len(object)):
        source_provide[int(source[i])] = source_provide[int(source[i])] + 1
        provide_object[int(object[i])] = provide_object[int(object[i])] + 1

    A = [0 for i in range(len(object))]
    sum = [0 for i in range(len(object))]  # calculate the sum of oj
    start = 1
    for i in range(1, len(object)):
        if object[i] != object[i - 1]:
            start = i
        j = start
        while object[j] == object[start]:
            if attribute[i] == attribute[j] and attribute[i] != tv[int(object[start]) - 1]:
                A[i] = A[i] + 1
                sum[i] = sum[i] + oj[i][j - start]
            j = j + 1
            if j == len(object):
                j = 1

    # Calculate the partial derivative for os
    e1 = [0 for i in range(len(object))]
    for i in range(1, len(object)):
        if attribute[i] != '' and '.' not in attribute[i]:
            e1[i] = (1-l)*(-((int(attribute[i])-tv[int(object[i])-1])**2)/((bv[i]+o[i]+g_n)**3)+1/(bv[i]+o[i]+g_n)) + (2*(1+a_e)/os[i]-2*b_e/(os[i]**3))/source_provide[int(source[i])]
    # print("es=", e1)

    # Calculate the partial derivative for oj
    e2 = [0 for i in range(len(object))]
    for i in range(1, len(object)):
        if attribute[i] != '' and '.' not in attribute[i]:
            e2[i] = l/A[i]*(-((int(attribute[i])-tv[int(object[i])-1])**2)/((bv[i]+o[i]+g_n)**3)+1/(bv[i]+o[i]+g_n))
            #print(l)
    # print("ej=", e2)

    # Calculate the partial derivative for bv
    e3 = [0 for i in range(len(object))]
    for i in range(1, len(object)):
        if attribute[i] != '' and '.' not in attribute[i]:
            e3[i] = -((int(attribute[i])-tv[int(object[i])-1])**2)/((bv[i]+o[i]+g_n)**3) + 1/(bv[i]+o[i]+g_n) + (bv[i]-u_b)/provide_object[int(object[i])]/(o_b**2)
    # print("eb=", e3)

    # calculate t and o(k+1)
    for i in range(1, len(object)):
        flag = 0
        t = 0
        # represent o(k+1)
        os_temp = 0
        while flag == 0:
            os_temp = os[i] - (p**t) * e1[i]
            if os_temp > 0.00001:
                os_temp = os_temp
            else:
                os_temp = 0.00001
            if L(i, os_temp, source_provide[int(source[i])], provide_object[int(object[i])])-L(i, os[i], source_provide[int(source[i])], provide_object[int(object[i])]) <= c * e1[i] *(os_temp-os[i]):
                os[i] = os_temp
                flag = 1
                for j in range(source_num):
                    oj[i][j] = oj[i][j] - (p**t) * e2[i]
                    if oj[i][j] > 0.00001:
                        oj[i][j] = oj[i][j]
                    else:
                        oj[i][j] = 0.00001
                bv[i] = bv[i] - (p ** t) * e3[i]
                # print("t=", t, "i=", i, "e[i]=", e[i], "os_temp=", (p**t) * e[i])
            else:
                t = t + 1
        # print("os_temp=",os_temp)
    # print("os=", os)
    a = []
    for i in range(len(object)):
        a.append(oj[i][0])
    # print("oj=", a)
    # print("bv=", bv)

def renew_osj(os, oj, tv):
    # source_provide start from 1
    # and means the number of entities the source provide
    source_provide = [0 for i in range(source_num + 1)]
    provide_object = [0 for i in range(object_num + 1)]
    for i in range(1, len(object)):
        source_provide[int(source[i])] = source_provide[int(source[i])] + 1
        provide_object[int(object[i])] = provide_object[int(object[i])] + 1

    A = [[[] for i in range(object_num)] for j in range(source_num)]
    A = caculate_distance(A, tv)

    # calculate the osj
    osj = [[0.1 for i in range(source_num+2)] for j in range(len(object))]
    for i in range(1, len(object)):
        osj[i][1] = os[i]
        for j in range(2, source_num+2):
            osj[i][j] = oj[i][j-2]
    # Calculate the partial derivative for os
    e1 = [0 for i in range(len(object))]
    for i in range(1, len(object)):
        if attribute[i] != '' and '.' not in attribute[i]:
            e1[i] = (1 - l) * (
                         -((int(attribute[i]) - tv[int(object[i]) - 1]) ** 2) / ((bv[i] + o[i] + g_n) ** 3) + 1 / (
                            bv[i] + o[i] + g_n)) + (2 * (1 + a_e) / os[i] - 2 * b_e / (os[i] ** 3)) / \
                    source_provide[int(source[i])]
    # print("es=", e1)

    # calculate the partial derivative for oj
    e2 = [0 for i in range(len(object))]
    for i in range(1, len(object)):
        if attribute[i] != '' and '.' not in attribute[i]:
            e2[i] = l * (-((int(attribute[i])-tv[int(object[i])-1])**2)/((bv[i-1]+o[i-1]+g_n)**3)) / len(A[int(source[i])-1][int(object[i])-1])
    # print("ej=", e2)

    # Calculate the partial derivative for bv
    e3 = [0 for i in range(len(object))]
    for i in range(1, len(object)):
        if attribute[i] != '' and '.' not in attribute[i]:
            e3[i] = -((int(attribute[i]) - tv[int(object[i]) - 1]) ** 2) / ((bv[i] + o[i] + g_n) ** 3) + 1 / (
                        bv[i] + o[i] + g_n) + (bv[i] - u_b) / provide_object[int(object[i])] / o_b
    # print("ebv=", e3)
    # calculate t and osj(k+1)
    for i in range(1, len(object)):
        flag = 0
        t = 0
        # represent osj(k+1)
        os_temp = [0 for i in range(source_num+2)]
        while flag == 0:
            # calculate the osj(k+1)
            os_temp[1] = osj[i][1] - (p ** t) * e1[i]
            for j in range(2, source_num + 2):
                os_temp[j] = osj[i][j] - (p ** t) * e2[i]
            for j in range(1, source_num + 2):
                if os_temp[j] > 0.00001:
                    os_temp[j] = os_temp[j]
                else:
                    os_temp[j] = 0.00001
            print(os_temp[j])
            temp = e1[i] * (os_temp[1] - osj[i][1])
            for j in range(2, source_num+2):
                temp = temp + e2[i] * (os_temp[j] - osj[i][j])
            print(L(i, os_temp, A, source_provide[int(source[i])], provide_object[int(object[i])])-L(i, osj[i], A, source_provide[int(source[i])], provide_object[int(object[i])]))
            if L(i, os_temp, A, source_provide[int(source[i])], provide_object[int(object[i])])-L(i, osj[i], A, source_provide[int(source[i])], provide_object[int(object[i])]) <= c * temp:
                for j in range(1, source_num+2):
                    osj[i][j] = os_temp[j]
                    oj[i][j-2] = osj[i][j]
                os[i] = osj[i][1]
                flag = 1
                bv[i] = bv[i] - (p ** t) * e3[i]
            else:
                t = t + 1
                print("t=", t)
        print("i=", i)

# index:the index of object, x:osj, num:source_provide, provide_object
def L(index, x, A, num1, num2):
    result = 0
    o_temp = x[1] * (1 - l)
    if len(A[int(source[index])-1][int(object[index])-1]) != 0:
        for i in range(len(A[int(source[index])-1][int(object[index])-1])):
            o_temp = o_temp + l * x[i] / len(A[int(source[index])-1][int(object[index])-1])
    if attribute[index] != '' and '.' not in attribute[index]:
        result = result + 0.5*((int(attribute[index])-tv[int(object[index])-1])**2)/((bv[index-1]+o_temp+g_n)**2)
        result = result + np.log(bv[index-1]+o_temp+g_n)
        result = result + (2*(1+a_e)*np.log(x[1]) + b_e/(x[1]**2))/num1
        result = result + 0.5*((bv[index-1]-u_b)**2)/o_b/num2
        result = result + 0.5*((tv[int(object[index])-1]-u_t)**2)/o_t/num2
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

if __name__ == '__main__':
    # read data
    with open("E:\\stock\\clean_stock\\alldataset_stock_sampled.csv", 'r', encoding='utf-8') as data:
        reader = csv.reader(data)
        index = 0
        for row in reader:
            index = index + 1
            source.append(row[source_index])
            object.append(row[object_index])
            attribute.append(row[attribute_index])
            if index == 5499:
                break
    print("object(len):", len(object))

    flag = 1
    m = 0
    t0 = [0 for i in range(2100)]
    while flag == 1:
        # calculate tv
        tv = calculate_tv()

        # calculate Esv
        oj = [[0.1 for i in range(source_num)] for j in range(len(object))]
        os = [0.1 for i in range(len(object))]
        calculate_Esv(os, oj, tv)
        print("?")

        # calculate os, oj, bv
        renew_osj(os, oj, tv)
        print("good")

        # print values
        m = m + 1
        print("m=", m)
        print("tv=", tv)
        flag = 0
        for i in range(2100):
            if (t0[i]-tv[i]) > 1 or (t0[i]-tv[i]) < -1:
                flag = 1
        t0 = tv
    print("tv(final)=", tv)

    print_hi('IATD!')

