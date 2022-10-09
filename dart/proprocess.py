import csv


def process(file1):
    with open(file1) as f:
        f_csv = csv.reader(f)
        data = []
        #headers = next(f_csv)
        for row in f_csv:
            a, b, c = row
            #print(b)
            labelsWord = []
            spaceindex = []
            flag = 1
            for iword in b:
                if(flag == 0):
                    flag = 1
                    continue
                if(iword != '[' and iword != ']' and iword != '\''):
                    if(iword==','):
                        labelsWord += ';'
                        flag = 0
                    else:
                        labelsWord += iword
            strcell = ''
            for i2word in labelsWord:
                strcell+=i2word
            data.append([a, 1, strcell, 1, 1])
    f.close()
    # output:按照source排序的没有冗余字符发真值文件（只有一个属性）
    f1 = open(r"E:\GitHub\KRAUSTD\dart\output.csv", 'w', encoding='utf-8', newline="")
    csv_write = csv.writer(f1)
    f2 = open("E:\\GitHub\\KRAUSTD\\CTD\\data\\new_weather\\15_30_9.0\\source\\source1.csv", 'r', encoding='utf-8', newline="")
    reader = csv.reader(f2)
    for row in reader:
        for i in range(len(data)):
            if data[i][0] == row[0]:
                csv_write.writerow(data[i])
    f1.close()
    f2.close()
