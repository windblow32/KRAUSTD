data0 = []
import csv
file = "1"
with open("E:\\GitHub\\KRAUSTD\\dart\\" + file + "_truthv1.csv", 'r') as f0:
    reader = csv.reader(f0)
    for row in reader:
        data0.append(row)
data1 = []
with open("E:\\GitHub\\KRAUSTD\\dart\\" + file + "_truthv2.csv", 'r') as f1:
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

f = open("E:\GitHub\KRAUSTD\dart\\" + file + "_truth.csv", 'w', newline="")
csv_writer = csv.writer(f)
for row in range(len(result)):
    csv_writer.writerow(result[row])