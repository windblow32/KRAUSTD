import dart_pro
import proprocess
import os
import glo
import csv

if __name__ == '__main__':

    glo._init()
    f = open(r'E:/GitHub/KRAUSTD/CTD/data/dart/weather/version.txt', 'r')
    flag = int(f.read()[-1])
    f.close()

    # 读取多个真值文件，对应proprocess里边的output文件
    # 然后把多个属性对应的真值文件合并成为同一个文件"_truth.csv" /53行

    file_to_read = r"E:\GitHub\KRAUSTD\CTD\log\Tri\DART\DART_connection.txt"
    with open(str(file_to_read), 'r', encoding='utf-8') as data:
        reader = csv.reader(data)
        for row in reader:
            if '/' in row[0]:
                source_tang = row[0]
    data.close()
    # source_tang = 'data/monitor0707'
    print("source_:", source_tang)

    attribute_index = []
    with open("E:/GitHub/KRAUSTD/dart/" + source_tang + "_ori.csv", 'r', encoding='utf-8') as data:
        reader = csv.reader(data)
        # k用来判断第几行，从0开始
        k = 0
        for row in reader:
            if k == 0:
                for j in range(len(row)):
                    if row[j] != 'source' and row[j] != 'entity' and row[j] != 'day' and j not in attribute_index:
                        attribute_index.append(j)
            k += 1

    for attribute_i in attribute_index:
        glo.set_value('author_index', attribute_i)
        file = dart_pro.run(r'E:\GitHub\KRAUSTD\CTD\log\Tri\DART\DART_connection.txt', flag, 0.5, source_tang)
        proprocess.process("E:\GitHub\KRAUSTD\dart\\" + file + "_truth_pro.csv", source_tang)

        if os.path.isfile("E:\GitHub\KRAUSTD\dart\\" + file + "_truthv" + str(attribute_i) + ".csv"):
            os.remove("E:\GitHub\KRAUSTD\dart\\" + file + "_truthv" + str(attribute_i) + ".csv")
        os.rename("E:\GitHub\KRAUSTD\dart\output.csv", "E:\GitHub\KRAUSTD\dart\\" + file + "_truthv" + str(attribute_i) + ".csv")

    result = []
    for attribute_i in attribute_index:
        with open("E:\\GitHub\\KRAUSTD\\dart\\" + file + "_truthv" + str(attribute_i) + ".csv", 'r') as f0:
            k = 0
            reader = csv.reader(f0)
            for row in reader:
                if len(result) <= k:
                    result.append([row[0]])
                result[k].append(row[1])
                k += 1

    for i in range(len(result)):
        result[i].append(1)

    f = open("E:\\GitHub\\KRAUSTD\\dart\\" + file + "_truth.csv", 'w', newline="")
    csv_writer = csv.writer(f)
    for row in range(len(result)):
        csv_writer.writerow(result[row])
    print(str(file))
    f.close()
