import dart_pro
import proprocess
import os

f = open(r'E:\GitHub\KRAUSTD\CTD\data\dart\monitor\version.txt', 'r')
flag = int(f.read()[-1])

file = dart_pro.run(r'E:\GitHub\KRAUSTD\CTD\log\Tri\DART\monitor\DART_connection.txt', flag)
proprocess.process(file + "_truth_pro.csv")
if os.path.isfile(file + "_truth.csv"):
    os.remove(file + "_truth.csv")
os.rename("output.csv", file + "_truth.csv")