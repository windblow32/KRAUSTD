import dart_pro
import proprocess
import os
import glo
import csv

if __name__ == '__main__':

    glo._init()
    f = open(r'E:\GitHub\KRAUSTD\CTD\data\dart\weather\version.txt', 'r')
    flag = int(f.read()[-1])

    # 读取多个真值文件，对应proprocess里边的output文件
    # 然后把多个属性对应的真值文件合并成为同一个文件"_truth.csv" /53行

    # 第一个待计算超参的index
    glo.set_value('author_index', 3)
    file = dart_pro.run(r'E:\GitHub\KRAUSTD\CTD\log\Tri\DART\weather\DART_connection.txt', flag, 0.5)
    proprocess.process("E:\GitHub\KRAUSTD\dart\\"+ file + "_truth_pro.csv")

    if os.path.isfile("E:\GitHub\KRAUSTD\dart\\"+ file + "_truthv1.csv"):
        os.remove("E:\GitHub\KRAUSTD\dart\\"+ file + "_truthv1.csv")
    os.rename("E:\GitHub\KRAUSTD\dart\output.csv", "E:\GitHub\KRAUSTD\dart\\"+ file + "_truthv1.csv")

    # 第二个待计算超参的index
    glo.set_value('author_index', 4)
    file = dart_pro.run(r'E:\GitHub\KRAUSTD\CTD\log\Tri\DART\weather\DART_connection.txt', flag, 0.8)
    proprocess.process("E:\GitHub\KRAUSTD\dart\\"+ file + "_truth_pro.csv")
    if os.path.isfile("E:\GitHub\KRAUSTD\dart\\"+ file + "_truthv2.csv"):
        os.remove("E:\GitHub\KRAUSTD\dart\\"+ file + "_truthv2.csv")
    os.rename("E:\GitHub\KRAUSTD\dart\output.csv", "E:\GitHub\KRAUSTD\dart\\"+ file + "_truthv2.csv")

    data0 = []
    with open("E:\\GitHub\\KRAUSTD\\dart\\"+ file + "_truthv1.csv", 'r') as f0:
        reader = csv.reader(f0)
        for row in reader:
            data0.append(row)
    data1 = []
    with open("E:\\GitHub\\KRAUSTD\\dart\\"+ file + "_truthv2.csv", 'r') as f1:
        reader = csv.reader(f1)
        for row in reader:
            data1.append(row)
    f0.close()
    f1.close()


    result = [[] for _ in range(len(data0))]
    for i in range(len(data0)):
        result[i].append(data0[i][0])
        result[i].append(data0[i][1])
        result[i].append(data0[i][2])
        result[i].append(data1[i][2])
        result[i].append(data1[i][3])
        result[i].append(data1[i][3])

    f = open("E:\\GitHub\\KRAUSTD\\dart\\" + file + "_truth.csv", 'w', newline="")
    csv_writer = csv.writer(f)
    for row in range(len(result)):
        csv_writer.writerow(result[row])
    f.close()
