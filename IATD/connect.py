import csv
import numpy as np
import random
import proprocess
import time
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
        # tv[i] = tv[i] + average[k]
        k += 1
        out.append([object2[i], tv[i]])
    else:
        tv[i] = 0
print("tv(final)=", tv)
print("name:", name)
f = open(name + ".csv", 'w', newline="")
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
print("error_rate:", error_num / total_num, error_num, total_num)
