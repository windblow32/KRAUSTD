import csv


data = []

with open('E:\\GitHub\\KRAUSTD\\CTD\\data\\monitor0707\\source\\source3.csv', 'r', encoding='ascii') as f:
    print(f.read())
f.close()

with open('E:\\GitHub\\KRAUSTD\\CTD\\data\\monitor0707\\source\\source3.csv', 'r') as f:
    csv_reader = csv.reader(f)
    for row in csv_reader:
        temp0 = [[] for _ in range(50)]
        print(row)
        num = 0
        for i in range(len(row)):
            if '\n' in row[i]:
                temp = row[i].split('\n')
                temp0[0].append(temp[0])
                num += 1
                for j in range(len(temp)-1):
                    temp0[num] = [temp[j+1]]
                    num += 1
            else:
                temp0[num].append(row[i])
        for i in range(len(temp0)):
            if temp0[i] != []:
                data.append(temp0[i])
    f.close()
    print(data)
    for i in range(len(data)):
        for j in range(len(data[i])):
            if ',' in data[i][j]:
                temp = data[i][j].split(',')
                data[i].remove(data[i][j])
                data[i] += temp
with open('source3.1.csv', 'w', newline='', encoding='utf-8') as d0:
    csv_writer = csv.writer(d0)
    for i in range(len(data)):
        csv_writer.writerow(data[i])
d0.close()
