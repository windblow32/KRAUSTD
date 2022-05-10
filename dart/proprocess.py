import csv


def process(file1):
    f = open("output.csv", 'w', encoding='utf-8', newline="")#output.csv为输出文件
    csv_write = csv.writer(f)
    with open(file1) as f:
        f_csv = csv.reader(f)
        #headers = next(f_csv)
        for row in f_csv:
            a, b = row
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
            csv_write.writerow([a, strcell])
    f.close()

