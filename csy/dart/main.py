import numpy as np
import csv
import time

source = ['aa', 'airtravelcenter', 'allegiantair', 'boston', 'businesstravellogue', 'CO', 'den', 'dfw', 'flightarrival', 'flightaware', 'flightexplorer', 'flights', 'flightstats', 'flightview', 'flightwise', 'flylouisville', 'flytecomm', 'foxbusiness', 'gofox', 'helloflight', 'iad', 'ifly', 'mco', 'mia', 'myrateplan', 'mytripandmore', 'orbitz', 'ord', 'panynj', 'phl', 'quicktrip', 'sfo', 'travelocity', 'ua', 'usatoday', 'weather', 'world-flight-tracker', 'wunderground']
source_0 = ['dangdang', 'xinhua', 'bookuu', 'yueyue', 'jiangtu']
domain = ['literature', 'Study', 'Computer science', 'Children', 'Science']
book = []
book_domain = []

all_num = 44
source_num = 5
domain_num = 5

source_index = 2
domain_index = 1

# 设置参数
a = 1
c = 0.5
p = 0.5

def read_data(book):
    # 把数据存成numpy数组
    with open("E:\\BOOK\\bookdata.csv", 'r') as f:
        reader = csv.reader(f)
        for row in reader:
            book.append(row)
    book = np.array(book)

def data_process(book, book_domain):
    print(book)
    # 把源按照顺序转换成数字
    for i in range(1, all_num):
        for j in range(0, source_num):
            if source_0[j] == book[i][source_index]:
                book[i][source_index] = int(j + 1)
    print(book)

    # 分domain
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
    print(book_domain)

def calculate_infs(infs):
    Pr = [[0 for i in range(source_num + 1)] for j in range(domain_num + 1)]
    # Pr[domain][source]
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
    return infs

def one_step(book, book_domain):
    # 算Pd(s)[domain][s]
    Pd = [[0 for i in range(source_num+1)]for j in range(domain_num+1)]
    for i in range(1, domain_num+1):
        for j in range(1, source_num+1):
            Pd[j][i] = book[i + 45][j]
    Pd_sum = [0, 0, 0, 0, 0, 0]
    for i in range(1, domain_num+1):
        for j in range(1, source_num+1):
            Pd_sum[i] += int(Pd[j][i])
    for i in range(1, domain_num+1):
        for j in range(1, source_num+1):
            Pd[j][i] = int(Pd[j][i]) / Pd_sum[i]

    # 算rd(s)
    rd = [[0 for i in range(source_num+1)]for j in range(domain_num+1)]
    for i in range(1, domain_num+1):
        for j in range(1, source_num+1):
            rd[i][j] = (1 - (a * Pd[i][j] - 1) ** 2) ** 0.5

    # infs[source,k,i] source = 5 domain = 5
    infs = [[[0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0], [0, 0, 0, 0, 0, 0],
             [0, 0, 0, 0, 0, 0]] for i in range(source_num+1)]
    infs = calculate_infs(infs)

    # 算ed(s)[d][s]
    ed = [[0.9 for i in range(source_num+1)]for j in range(domain_num+1)]
    temp = 0
    for i in range(1, source_num+1):
        for j in range(1, domain_num+1):
            for k in range(1, domain_num+1):
                if k != i:
                    temp = temp + rd[i][j] * infs[j][k][i]
            ed[i][j] = rd[i][j] + p * temp
            temp = 0

    # 初始化tdrec=0.9 和tdsp=0.9
    tdrec = [[0.9 for i in range(source_num+1)]for j in range(domain_num+1)]
    tdsp = [[0.9 for i in range(source_num+1)]for j in range(domain_num+1)]

    # 算cs(v)
    cs = [[[[0, 0, 0, 0, 0, 0] for i in range(5)] for j in range(13)] for k in range(6)]  # 现根据value定位v[v,source]
    vo = [1 for i in range(13)]  # o的数目
    vo_start = [1 for i in range(1194)]
    start = 1
    # 算每个对象对应的所有值以及源
    value = [[[0, 0, 0, 0, 0] for i in range(13)] for j in range(6)]
    kl = [0, 0, 0, 0, 0]
    j = 0
    for i in range(1, 44):
        if book[i][0] != book[i - 1][0]:
            j = j + 1
            # 找到对应的源
            for m in range(1, 6):
                if m == int(book[i][2]):
                    value[m][j][0] = book[i][0]
                    break
            temp = ''
            k = 0
            t = 0
            while k < len(book[i][3]):
                while k < len(book[i][3]) and book[i][3][k] != ',':
                    temp = temp + book[i][3][k]
                    k = k + 1
                t = t + 1
                value[m][j][t] = temp
                temp = ''
                k = k + 2
        else:
            for m in range(1, 6):
                if m == int(book[i][2]):
                    value[m][j][0] = book[i][0]
                    break
            temp = ''
            k = 0
            t = 0
            while k < len(book[i][3]):
                while k < len(book[i][3]) and book[i][3][k] != ',':
                    temp = temp + book[i][3][k]
                    k = k + 1
                t = t + 1
                value[m][j][t] = temp
                temp = ''
                k = k + 2
    # print("value")
    # for i in range(1, 6):
    #    print(value[i])

    # 计算某一个源对应的对象的值的个数 [source][object]
    value_num = [[0 for i in range(13)] for j in range(6)]
    for i in range(1, 6):
        for j in range(1, 13):
            num = -1
            for k in range(0, 5):
                if value[i][j][k] != 0:
                    num = num + 1
            if num > 0:
                value_num[i][j] = num
            else:
                value_num[i][j] = 0

    # object_num:每个对象的值的个数（不重复）
    # object_num2:每个对象的值的个数（重复）
    object_num = [0 for i in range(13)]
    object_num2 = [0 for i in range(13)]
    object_temp = []
    for i in range(1, 13):
        for j in range(1, 6):
            for k in range(1, 5):
                if value[j][i][k] != 0:
                    if value[j][i][k] not in object_temp:
                        object_temp.append(value[j][i][k])
                        object_num[i - 1] = object_num[i - 1] + 1
        object_temp = []

    # i是用来遍历v的
    for i in range(1, 6):
        for j in range(1, 13):
            for k in range(1, 5):
                if value[i][j][k] != 0:
                    for t in range(1, 6):
                        if value[i][j][k] in value[t][j]:
                            cs[i][j][k][t] = (1 - object_num[j - 1] / value_num[t][j] / ((object_num[j - 1]) ** 2)) / \
                                             value_num[t][j]
                        else:
                            cs[i][j][k][t] = 1 / ((object_num[j - 1]) ** 2)
    # for i in range(1, 6):
    #    print(cs[i])

    for j in range(1, 13):
        for i in range(1, 6):
            for k in range(1, 5):
                if value[i][j][k] != 0:
                    object_num2[j] = object_num2[j] + 1

    # 算cs(v)
    ov = [[[0.5 for i in range(5)] for j in range(13)] for k in range(6)]
    P1 = [[[0 for i in range(5)] for j in range(13)] for k in range(6)]
    P2 = [[[0 for i in range(5)] for j in range(13)] for k in range(6)]
    P3 = [[[0 for i in range(5)] for j in range(13)] for k in range(6)]
    t1_o = 0
    t1_sum = 0
    t2_o = 0
    t2_sum = 0
    l = 50
    while l > 0:
        for i in range(1, 6):
            for j in range(1, 13):
                for k in range(1, 5):
                    t = 1
                    z = 1
                    temp1 = 1
                    temp2 = 1
                    while t < j:
                        if book_domain[z] != book_domain[z - 1]:
                            t = t + 1
                        z = z + 1
                    for m in range(1, 6):
                        if value[i][j][k] in value[m][j]:
                            t = 0
                            while t < len(book_domain[z - 1]):
                                temp1 = temp1 * ((tdrec[book_domain[z - 1][t]][m]) ** (
                                            ed[book_domain[z - 1][t]][m] * cs[i][j][k][m]))
                                temp2 = temp2 * ((1 - tdrec[book_domain[z - 1][t]][m]) ** (
                                            ed[book_domain[z - 1][t]][m] * cs[i][j][k][m]))
                                t = t + 1
                        else:
                            t = 1
                            while t <= 5:
                                if t not in book_domain[z - 1]:
                                    temp1 = temp1 * ((1 - tdsp[t][m]) ** (ed[t][m] * cs[i][j][k][m]))
                                    temp2 = temp2 * ((tdsp[t][m]) ** (ed[t][m] * cs[i][j][k][m]))
                                t = t + 1
                    P1[i][j][k] = temp1
                    P2[i][j][k] = temp2

        for j in range(1, 13):
            for i in range(1, 6):
                for k in range(1, 5):
                    if value[i][j][k] != 0:
                        temp = 0
                        for m in range(1, 6):
                            for t in range(1, 5):
                                if value[i][j][k] == value[m][j][t]:
                                    temp = temp + 1
                        P3[i][j][k] = temp / object_num2[j]

        print("P3")
        for i in range(1, 6):
            print(P3[i])

        print("ov")
        for i in range(1, 6):
            for j in range(1, 13):
                for k in range(1, 5):
                    if value[i][j][k] != 0:
                        ov[i][j][k] = (P1[i][j][k] / P2[i][j][k]) * (1 / P3[i][j][k] - 1)
                        ov[i][j][k] = 1 / (ov[i][j][k] + 1)
            print(ov[i])
        for i in range(1, 6):
            for j in range(1, 6):
                t = 0
                for k in range(1, 44):
                    if book[k][0] != book[k - 1][0]:
                        t = t + 1
                    if i in book_domain[k - 1] and j == int(book[k][2]):
                        for m in range(1, 5):
                            if value[j][t][m] != 0:
                                t1_o = t1_o + ov[j][t][m]
                                t1_sum = t1_sum + 1

                    if i in book_domain[k - 1] and int(book[k][2]) not in value[j][t]:
                        for m in range(1, 5):
                            if value[int(book[k][2])][t][m] != 0:
                                t2_o = t2_o + ov[int(book[k][2])][t][m]
                                t2_sum = t2_sum + 1
                tdrec[i][j] = t1_o / t1_sum
                tdsp[i][j] = t2_o / t2_sum
            # print(tdrec)
            # print(tdsp)
        l = l - 1

    print("P1")
    for i in range(1, 6):
        print(P1[i])

    print("P2")
    for i in range(1, 6):
        print(P2[i])

    already = []
    for i in range(1, 13):
        print("-", i, "-", value[1][i][0], "\t", "\t", end=" ")
        flag = 0
        for j in range(1, 6):
            for k in range(1, 5):
                if ov[j][i][k] > 0.5 and value[j][i][k] != 0:
                    if value[j][i][k] not in already:
                        print(value[j][i][k], end="/")
                        already.append(value[j][i][k])
                        flag = 1
                    # print("-", ov[j][i][k])
        print("")
        if flag == 0:
            print(ov[1][i], "\n", ov[2][i], "\n", ov[3][i], "\n", ov[4][i], "\n", ov[5][i])
        already = []

if __name__ == '__main__':
    start = time.perf_counter()
    read_data(book)
    data_process(book, book_domain)
    one_step(book, book_domain)
    end = time.perf_counter()
    print("time(Dart):", end - start, "(s)")


'''
# 把登记口按照asc码转换成数字
flight2 = flight1
row = 1
temp0 = ''
for i in range(1, 26983):
    if flight1[i][4]:
        flight2[row][0] = row
        flight2[row][1] = flight1[i][1]
        flight2[row][2] = flight1[i][2]
        flight2[row][3] = flight1[i][3]
        flight2[row][4] = flight1[i][4]
        row = row + 1
# print(row)
# print(flight2)
print(flight2[1962][3])

# 把航班号转换成数字
object0 = [1 for i in range(1194)]
object = 1
for i in range(1, 10487):
    if flight2[i][2] == flight2[i+1][2]:
        object0[object] = flight2[i][2]
        flight2[i][2] = object
    else:
        flight2[i][2] = object
        object = object + 1
# for i in range(1, 10487):
#     print(flight2[i])

# 转换时间，按照冒号转换
temp1 = ''
temp2 = ''
for i in range(1, 10487):
    if flight2[i][3]:
        temp = flight2[i][3]
        for j in range(len(temp)):
            if (temp[j] == ':'):
                break;
        k = j + 1
        j = j - 1
        while k < len(temp) and ord(temp[k]) >= 48 and ord(temp[k]) <= 57:
            temp1 = temp1 + temp[k]
            k = k + 1
        while j >= 0 and ord(temp[j]) >= 48 and ord(temp[j]) <= 57:
            temp2 = temp2 + temp[j]
            j = j - 1
        temp2 = temp2[::-1]
        flight2[i][3] = temp2 + temp1
        temp1 = ''
        temp2 = ''
#for i in range(1, 10487):
#    print(flight2[i])
print(flight2[1962][3])


# 分domain（根据登机口有没有字母，存在第0列，有字母为1，没有为2）
flag = 0
for i in range(1, 10487):
    if flight2[i][4]:
        temp = flight2[i][4]
        for j in range(len(temp)):
            if ord(temp[j]) < 48 or ord(temp[j]) > 57:
                flag = 1
    if flag == 1:
        flight2[i][0] = 1
    else:
        flight2[i][0] = 2
    flag = 0
for i in range(1, 10487):
    print(flight2[i])
'''

'''
for i in range(1, 3):
    # domain
    for j in range(1,39):
        for k in range(1, 10487):
            if (flight2[k][0] == str(i)) and (flight2[k][1] == str(j)):
                Pd[i][j] = Pd[i][j] + 1

Pd_sum = [0, 0, 0]
for i in range(1, 3):
    for j in range(1, 39):
        Pd_sum[i] = Pd_sum[i] + Pd[i][j]
for i in range(1, 3):
    for j in range(1, 39):
        Pd[i][j] = float(Pd[i][j]) / Pd_sum[i]
'''

'''
# 算rd(s)
rd = [[0 for i in range(39)], [0 for i in range(39)], [0 for i in range(39)]]
for i in range(1, 3):
    for j in range(1,39):
        rd[i][j] = (1 - (a * Pd[i][j] - 1) ** 2) ** 0.5
    print(rd[i])

# infs[source,k,i]
infs = [[[0, 0, 0], [0, 0, 0], [0, 0, 0]] for i in range(39)]
for i in range(1, 39):
    for j in range(1, 3):
        for t in range(1, 3):
            if j != t:
                for k in range(1, 10487):
                    if flight2[k][0] == j and flight2[k][0] == t:
                        infs[i][j][t] = infs[i][j][t] + 1
infs_sum = [[0, 0, 0] for i in range(39)]
for i in range(1, 39):
    for j in range(1, 3):
        for t in range(1, 3):
            infs_sum[i][j] = infs_sum[i][j] + infs[i][j][t]
        print(infs_sum[i][j])
'''

'''
#算ed(s)[d][s]
ed = [[0 for i in range(39)], [0 for i in range(39)], [0 for i in range(39)]]
temp = 0
for i in range(1, 3):
    for j in range(1,39):
        for k in range(1, 3):
            if k != i:
                temp = temp + rd[i][j] * infs[j][k][i]
        ed[i][j] = rd[i][j] + p * temp
        temp = 0
    #print(ed[i])
print(flight2)

# 初始化tdrec=0.9 和tdsp=0.9
tdrec = [[0.9 for i in range(39)], [0.9 for i in range(39)], [0.9 for i in range(39)]]
tdsp = [[0.9 for i in range(39)], [0.9 for i in range(39)], [0.9 for i in range(39)]]
'''

'''
# 算cs(v)
cs = [[0 for i in range(39)]for j in range(10487)]
vo = [1 for i in range(1194)]# o的数目
vo_start = [1 for i in range(1194)]
print(flight2[10486][2])
start = 1
for i in range(1, 10486):
    if int(flight2[i][2]) == int(flight2[i+1][2]):
        vo[int(flight2[i][2])] = vo[int(flight2[i][2])] + 1
        vo_start[int(flight2[i][2])] = start
    else:
        vo_start[int(flight2[i][2])+1] = i + 1#
        start = i + 1
for i in range(1, 10487):
    start = vo_start[int(flight2[i][2])]#该对象的第一个相同对象的行数(参数是第二列)
    if int(flight2[i][2]) <= 1192:
        end = vo_start[int(flight2[i][2])+1]
    else:
        end = 10486
    for j in range(start, end):
        if flight2[i][2] == flight2[j][2]:
            cs[i][int(flight2[j][1])] = 1 - 1 / (vo[int(flight2[i][2])]**2) * vo[int(flight2[i][2])]
        else:
            cs[i][int(flight2[j][1])] = 1 / (vo[int(flight2[i][2])]**2)
'''

'''
df = pd.read_csv("E:\\clean_flight\\2011-12-01-new.csv")
dff = df.drop(columns=['Column3', 'Column6', 'Column7', 'Column8'], axis=1)
dff.to_csv("E:\\clean_flight\\2011-12-01-new08.csv", encoding="utf-8")
'''