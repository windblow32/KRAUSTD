import csv


def process(file1, source_tang):
    data = []
    with open(file1) as f:
        f_csv = csv.reader(f)
        # headers = next(f_csv)
        for row in f_csv:
            a, b = row
            labelsWord = []
            flag = 1
            for iword in b:
                if (flag == 0):
                    flag = 1
                    continue
                if (iword != '[' and iword != ']' and iword != '\''):
                    if (iword == ','):
                        labelsWord += ';'
                        flag = 0
                    else:
                        labelsWord += iword
            strcell = ''
            for i2word in labelsWord:
                strcell += i2word
            data.append([a, strcell])
    f.close()
    f1 = open(file1[:-4] + "-truth.csv", 'w', encoding='utf-8', newline="")
    csv_write = csv.writer(f1)
    entity = []
    f2 = open("E:/GitHub/KRAUSTD/CTD/" + source_tang + "/source/source1.csv", 'r', encoding='utf-8', newline="")
    reader = csv.reader(f2)
    for row in reader:
        for i in range(len(data)):
            if data[i][0] == row[0] and row[0] not in entity:
                csv_write.writerow(data[i])
                entity.append(row[0])
    f1.close()
    f2.close()
