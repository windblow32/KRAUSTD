import numpy as np
import csv
import time

# 需要调整的参数：address, all_num, source_num, domain_num, object_num, max_claim, index
# 设置数据的参数
import glo

# tyf read!!!
# all_num:所有数据的数目（包括首行）
# sourcenum:数据源的数目，看ori文件结尾的table里有几个数据源
# domain_num:domain的数目，看ori文件结尾的table里有几个domain（如9.0中，1813-1830行为domain，domain_num = 18）
# object_number:实体的数目，看对应的truth文件有多少实体

all_num = 1810  # 所有数据的数目（包括首行）###
source_num = 15 ###
domain_num = 18 ###
object_number = 150####
max_claim = 3
truth_index = 1
name_index, domain_index, source_index, author_index = 1, 2, 0, 3
book_domain = []
domain = []
book_object = []
f0 = open("E:\GitHub\KRAUSTD\dart\hyperpara.txt", 'r', newline="")
glo._init()
print(author_index)
f0.close

# 设置参数
a, c, p = 1, 0.5, 0.5


def read_data():
    data = []
    with open("E:\\GitHub\\KRAUSTD\\dart\\weather\\15_30_9.0_ori.csv", 'r') as f:
        reader = csv.reader(f)
        for row in reader:
            data.append(row)
    return data


def data_process(book):
    source_all = []
    # 把源按照顺序转换成数字
    for i in range(1, all_num):
        if book[i][source_index] not in source_all:
            source_all.append(book[i][source_index])
            book[i][source_index] = len(source_all)
        else:
            for j in range(0, len(source_all)):
                if source_all[j] == book[i][source_index]:
                    book[i][source_index] = j + 1

    # 分domain
    for i in range(1, all_num):
        k = 0
        while k < len(book[i][domain_index]):
            name = ''
            while k < len(book[i][domain_index]) and book[i][domain_index][k] != ',':
                name = name + book[i][domain_index][k]
                k = k + 1
            if name not in domain:
                domain.append(name)
            k = k + 2
    print("domain:", domain)
    for i in range(1, all_num):
        k = 0
        temp = []
        while k < len(book[i][domain_index]):
            name = ''
            while k < len(book[i][domain_index]) and book[i][domain_index][k] != ',':
                name = name + book[i][domain_index][k]
                k = k + 1
            for j in range(0, domain_num):
                if domain[j] == name:
                    temp.append(j + 1)
            k = k + 2
        book_domain.append(temp)
        book_object.append(book[i][name_index])
    print("book domain:", book_domain)


# 计算某个source在某个domain的声明的数目
# o[domain][source]
# domain对应的数字是数组domain中的顺序
# source对应的数字是数据集中source的顺序
# return O_number
def calculate_number(o, book):
    for i in range(1, all_num):
        for j in range(1, domain_num + 1):
            for k in range(1, source_num + 1):
                if j in book_domain[i - 1] and k == int(book[i][source_index]):
                    o[j][k] += 1
    print("o:", o)
    return o


def calculate_infs(infs, book, file, flag):
    if flag == 0:
        Pr = [[0 for i in range(source_num + 1)] for j in range(domain_num + 1)]
        # Pr[domain][source]
        Pr = calculate_number(Pr, book)
        for i in range(1, all_num):
            for j in range(1, domain_num + 1):
                for k in range(1, source_num + 1):
                    if j in book_domain[i - 1] and k == int(book[i][source_index]):
                        Pr[j][k] += 1

        for k in range(1, all_num):
            for i in range(1, source_num + 1):
                for j in range(1, domain_num + 1):
                    for t in range(1, domain_num + 1):
                        if j in book_domain[k - 1] and t in book_domain[k - 1] and i == int(
                                book[k][source_index]) and j != t:
                            infs[i][j][t] += 1
        for i in range(1, source_num + 1):
            for j in range(1, domain_num + 1):
                for t in range(1, domain_num + 1):
                    if infs[i][j][t] != 0:
                        infs[i][j][t] = infs[i][j][t] / int(Pr[j][i])
        kk = 0
        with open(file, 'r') as embedding:
            for row in embedding:
                if kk == 0:
                    name = row
                kk += 1
    if flag == 1:
        print("book:::", book)
        kk = 0
        with open(file, 'r') as embedding:
            for row in embedding:
                if kk == 0:
                    name = row
                else:
                    word1, word2, word3, number = 0, '', '', ''
                    k = 0
                    for j in range(len(row)):
                        if row[j] == '&' and k == 1:
                            k += 1
                        if k == 1:
                            word2 += row[j]
                        if row[j] == '&' and k == 0:
                            word1 += int(row[j-1]) + 1
                            k += 1
                        if row[j] == ':':
                            k += 1
                        if k == 2 and row[j] != '&':
                            word3 += row[j]
                        if k == 3 and row[j] != ':':
                            number += row[j]
                    if word2 == 'Philips_Electronics':
                        word2 = 'Philips Electronics'
                    if word3 == 'Philips_Electronics':
                        word3 = 'Philips Electronics'
                    if word2 == 'iiyama_North_America_Inc':
                        word2 = 'iiyama North America'
                    if word3 == 'iiyama_North_America_Inc':
                        word3 = 'iiyama North America'
                    if word2 == 'Hannspree_Inc':
                        word2 = 'Hannspree'
                    if word3 == 'Hannspree_Inc':
                        word3 = 'Hannspree'
                    if word2 in domain and word3 in domain:
                        infs[word1][domain.index(word2)+1][domain.index(word3)+1] = float(number)
                kk += 1
    return infs, name


def one_step(book, book_domain, file, flag, theta):
    author_index = glo.get_value('author_index')
    # 算Pd(s)[domain][s]
    Pd = [[0 for i in range(source_num+1)]for j in range(domain_num+1)]
    for i in range(1, domain_num+1):
        for j in range(1, source_num+1):
            Pd[i][j] = book[i + all_num + 1][j]
    Pd_sum = [0 for _ in range(domain_num+1)]
    for i in range(1, domain_num+1):
        for j in range(1, source_num+1):
            Pd_sum[i] += int(Pd[i][j])
    for i in range(1, domain_num+1):
        for j in range(1, source_num+1):
            Pd[i][j] = int(Pd[i][j]) / Pd_sum[i]
    print("---Pd:", Pd)

    # 算rd(s)
    rd = [[0 for i in range(source_num+1)]for j in range(domain_num+1)]
    for i in range(1, domain_num+1):
        for j in range(1, source_num+1):
            rd[i][j] = (1 - (a * Pd[i][j] - 1) ** 2) ** 0.5

    # infs[source,k,i] source = 5 domain = 5
    infs = [[[0 for i in range(domain_num+1)]for j in range(domain_num+1)] for k in range(source_num+1)]
    infs, name = calculate_infs(infs, book, file, flag)
    #print("---infs:", infs)

    # 算ed(s)[d][s]
    ed = [[0.9 for i in range(source_num+1)]for j in range(domain_num+1)]
    temp = 0
    for i in range(1, domain_num+1):
        for j in range(1, source_num+1):
            for k in range(1, domain_num+1):
                if k != i:
                    temp = temp + rd[i][j] * infs[j][k][i]
            ed[i][j] = rd[i][j] + p * temp
            temp = 0
    #print("---ed:", ed)

    # 初始化tdrec=0.9 和tdsp=0.9
    tdrec = [[0.9 for i in range(source_num+1)]for j in range(domain_num+1)]
    tdsp = [[0.9 for i in range(source_num+1)]for j in range(domain_num+1)]

    # 算cs(v)
    cs = [[{} for _ in range(object_number + 1)] for _ in range(source_num+1)]  # 现根据字典定位v，确定一个cs(v)，需要s, o, v三个维度
    # 算每个对象对应的所有值以及源
    # value[s][object][name, author]
    value = [[[0 for k in range(max_claim + 1)] for _ in range(object_number + 1)] for _ in range(source_num+1)]
    j = 0
    for i in range(1, all_num):
        if book[i][name_index] != book[i - 1][name_index]:
            j = j + 1
            # 找到对应的源
            for m in range(1, source_num+1):
                if m == int(book[i][source_index]):
                    value[m][j][0] = book[i][name_index]
                    print("?:",book[i][name_index])
                    break
            print(name_index)
            temp = ''
            k = 0
            t = 0
            while k < len(book[i][author_index]):
                while k < len(book[i][author_index]) and book[i][author_index][k] != ';':
                    temp = temp + book[i][author_index][k]
                    k = k + 1
                t = t + 1
                value[m][j][t] = temp
                temp = ''
                k = k + 1
        else:
            for m in range(1, source_num+1):
                if m == int(book[i][source_index]):
                    value[m][j][0] = book[i][name_index]
                    break
            temp = ''
            k = 0
            t = 0
            while k < len(book[i][author_index]):
                while k < len(book[i][author_index]) and (book[i][author_index][k] != ';' and book[i][author_index][k] != '/' and book[i][author_index][k] != '|'):
                    temp = temp + book[i][author_index][k]
                    k = k + 1
                t = t + 1
                value[m][j][t] = temp
                temp = ''
                k = k + 1
    print("---value:")
    for i in range(1, source_num+1):
        print(value[i])
    print("------")

    # 计算某一个源对应的对象的值的个数 [source][object]
    value_num = [[0 for i in range(object_number+1)] for j in range(source_num+1)]
    for i in range(1, source_num+1):
        for j in range(1, object_number+1):
            num = -1
            for k in range(0, max_claim+1):
                if value[i][j][k] != 0:
                    num = num + 1
            if num > 0:
                value_num[i][j] = num
            else:
                value_num[i][j] = 0
    #print("---value_num:", value_num)
    # object_num:每个对象的值的个数（不重复）
    # object_num2:每个对象的值的个数（重复）
    object_num = [0 for i in range(object_number+1)]
    object_num2 = [0 for i in range(object_number+1)]
    object_temp = []
    for i in range(1, object_number+1):
        for j in range(1, source_num+1):
            for k in range(1, max_claim+1):
                if value[j][i][k] != 0:
                    if value[j][i][k] not in object_temp:
                        object_temp.append(value[j][i][k])
                        object_num[i - 1] = object_num[i - 1] + 1
        object_temp = []
    # i是用来遍历v的
    for i in range(1, source_num+1):
        for j in range(1, object_number+1):
            for k in range(1, max_claim+1):
                if value[i][j][k] != 0:
                    for t in range(1, source_num+1):
                        if value[i][j][k] in value[t][j] and value_num[t][j] != 0:
                            cs[t][j][value[i][j][k]] = (1 - object_num[j - 1] / value_num[t][j] / ((object_num[j - 1]) ** 2)) / \
                                             value_num[t][j]
                        else:
                            cs[t][j][value[i][j][k]] = 1 / ((object_num[j - 1]) ** 2)

    for j in range(1, object_number+1):
        for i in range(1, source_num+1):
            for k in range(1, max_claim+1):
                if value[i][j][k] != 0:
                    object_num2[j] = object_num2[j] + 1
    print("test")
    ov = [[[0.5 for i in range(max_claim+1)] for j in range(object_number+1)] for k in range(source_num+1)]
    P1 = [[[0 for i in range(max_claim+1)] for j in range(object_number+1)] for k in range(source_num+1)]
    P2 = [[[0 for i in range(max_claim+1)] for j in range(object_number+1)] for k in range(source_num+1)]
    P3 = [[[0 for i in range(max_claim+1)] for j in range(object_number+1)] for k in range(source_num+1)]
    t1_o = 0
    t1_sum = 0
    t2_o = 0
    t2_sum = 0

    l = 5
    while l > 0:
        time_start = time.time()
        print("test_start:", l)
        for i in range(1, source_num+1):
            print("i=", i, time.time() - time_start)
            for j in range(1, object_number+1):
                for k in range(1, max_claim+1):#可以减少
                    t = 1
                    z = 1
                    temp1 = 1
                    temp2 = 1
                    while t < j:
                        if book_object[z] != book_object[z - 1]:
                            t = t + 1
                        z = z + 1
                    temp_value = value[i][j][k]
                    for m in range(1, source_num+1):
                        if temp_value != 0:
                            cs_temp = cs[m][j][temp_value]
                            if temp_value in value[m][j]:
                                t = 0
                                while t < len(book_domain[z - 1]):
                                    var1 = ed[book_domain[z - 1][t]][m] * cs_temp
                                    var01 = tdrec[book_domain[z - 1][t]][m]
                                    temp1 = temp1 * (var01 ** var1)
                                    temp2 = temp2 * ((1 - var01) ** var1)
                                    t = t + 1
                            else:
                                t = 1
                                while t <= domain_num:
                                    if t not in book_domain[z - 1]:
                                        var2 = ed[t][m] * cs_temp
                                        var02 = tdsp[t][m]
                                        temp1 = temp1 * ((1 - var02) ** var2)
                                        temp2 = temp2 * (var02 ** var2)
                                    t = t + 1
                    P1[i][j][k] = temp1
                    P2[i][j][k] = temp2
        for j in range(1, object_number+1):
            for i in range(1, source_num+1):
                for k in range(1, max_claim+1):
                    value_temp = value[i][j][k]
                    if value_temp != 0:
                        temp = 0
                        for m in range(1, source_num+1):
                            for t in range(1, max_claim+1):
                                if value_temp == value[m][j][t]:
                                    temp = temp + 1
                        P3[i][j][k] = temp / object_num2[j]
        time_end = time.time()
        print("test_end:", l)
        print("test_time:", time_end - time_start)
        for i in range(1, source_num+1):
            for j in range(1, object_number+1):
                for k in range(1, max_claim+1):
                    if value[i][j][k] != 0:
                        ov[i][j][k] = (P1[i][j][k] / P2[i][j][k]) * (1 / P3[i][j][k] - 1)
                        ov[i][j][k] = 1 / (ov[i][j][k] + 1)
            #print(ov[i])
        for i in range(1, domain_num+1):
            for j in range(1, source_num+1):
                t = 0
                for k in range(1, all_num):
                    if book[k][name_index] != book[k - 1][name_index]:
                        t = t + 1
                    if i in book_domain[k - 1] and j == int(book[k][source_index]):
                        for m in range(1, max_claim+1):
                            if value[j][t][m] != 0:
                                t1_o = t1_o + ov[j][t][m]
                                t1_sum = t1_sum + 1
                    if i in book_domain[k - 1] and int(book[k][source_index]) not in value[j][t]:
                        for m in range(1, max_claim+1):
                            if value[int(book[k][source_index])][t][m] != 0:
                                t2_o = t2_o + ov[int(book[k][source_index])][t][m]
                                t2_sum = t2_sum + 1
                if t1_sum != 0:
                    tdrec[i][j] = t1_o / t1_sum
                else:
                    tdrec[i][j] = 0
                if t2_sum != 0:
                    tdsp[i][j] = t2_o / t2_sum
                else:
                    tdsp[i][j] = 0
            # print(tdrec)
            # print(tdsp)
        l = l - 1
    output = []
    already = []
    for i in range(1, object_number+1):
        print("-", i, "-", value[1][i][0], "\t", "\t", end=" ")
        flag = 0
        for j in range(1, source_num+1):
            for k in range(1, max_claim+1):
                if ov[j][i][k] > theta and value[j][i][k] != 0:
                    if [value[j][i][k]] not in already:
                        print(value[j][i][k], end="/")
                        already.append([value[j][i][k]])
                        #already.append([[value[j][i][k]]+[ov[j][i][k]]])
                        flag = 1
                    print("-", ov[j][i][k])
        print("")
        '''
        if flag == 0:
            print(ov[1][i], "\n", ov[2][i], "\n", ov[3][i], "\n", ov[4][i], "\n", ov[5][i])
        '''
        for j in range(len(value)):
            if value[j][i][0] != 0:
                output_name = value[j][i][0]
        output.append([output_name]+[already]+[1])
        already = []
    name = name[:-1]
    print(output)
    print(name)
    f = open("E:\GitHub\KRAUSTD\dart\\"+name+"_truth_pro.csv", 'w', newline="")
    csv_writer = csv.writer(f)
    for row in range(len(output)):
        csv_writer.writerow(output[row])
    return output, name


def score(a, b):
    truth = []
    with open('C:\\Users\\陈思莹\\Desktop\\data\\weather\\weather_truth_sam.csv', 'r', encoding='utf-8') as f:
        reader = csv.reader(f)
        for row in reader:
            truth.append(row[truth_index])
    print("truth:", truth, "\n", a)
    pre_top, pre_down = 0, 0
    for i in range(len(a)):
        for j in range(len(a[i][1])):
            if a[i][1][j] != 0:
                pre_down += 1
                if a[i][1][j] in truth[i]:
                    pre_top += 1
    pre = pre_top/pre_down
    print("precision:", pre_top, pre_down, pre)
    print("error:", 1-pre)
    '''
    recall_top, recall_down = 0, 0
    for i in range(len(truth)):
        temp = truth[i][2]#[1:-1].split(',')
        for j in range(len(temp)):
            temp[j] = temp[j].strip()
            temp[j] = temp[j].strip('\'')
        for j in range(len(temp)):
            recall_down += 1
            if temp[j] in a[i][1]:
                recall_top += 1
    recall = recall_top/recall_down
    print("recall:", recall_top, recall_down, recall)
    print("F1:", 2*recall*pre/(recall+pre))
    print(a, "\n",truth)
    '''


def run(file, flag, theta):
    start = time.time()
    book = read_data()
    data_process(book)
    print("data:", book)
    a, name = one_step(book, book_domain, file, flag, theta)
    end = time.time()
    print("time(Dart_pro):", end - start, "(s)")
    # score(a, 1)
    return name

# monitor
# time:6.2026448249816895+6.20264482498168956.0247533321380615+6.818145513534546+6.14521312713623056.0247533321380615+6.818145513534546+6.1452131271362305
# error:0.15000000000000002+0.04761904761904767+0.09090909090909094+0.2857142857142857

# camera
# time:9.950549840927124+7.560710668563843
# error:0.125+0.3783783783783784
