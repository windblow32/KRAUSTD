import csv
data = []
domain_index, source_index = 2, 0

with open("E:\\GitHub\\KRAUSTD\\dart\\weather\\30,50,35.0_ori.csv", 'r') as f:
    reader = csv.reader(f)
    for row in reader:
        data.append(row)
f.close()

domain = []
source = []
for i in range(1, len(data)):
    if data[i][domain_index] not in domain:
        domain.append(data[i][domain_index])
    if data[i][source_index] not in source:
        source.append(data[i][source_index])
print(domain)
# domain是竖着的
table = [[0 for _ in range(len(source)+1)] for _ in range(len(domain)+1)]
for i in range(1, len(domain)+1):
    table[i][0] = domain[i-1]
for j in range(1, len(source)+1):
    table[0][j] = source[j-1]


for i in range(1, len(data)):
    table[domain.index(data[i][domain_index])+1][source.index(data[i][source_index])+1] += 1
print(table)
data.append([0 for _ in range(8)])
for i in range(len(table)):
    data.append(table[i])
f = open("E:\\GitHub\\KRAUSTD\\dart\\weather\\30,50,35.0_ori.csv", 'w', newline="")
csv_writer = csv.writer(f)
for row in range(len(data)):
    csv_writer.writerow(data[row])
f.close()

