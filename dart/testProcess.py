import subprocess

# Java命令及其参数
java_cmd = ['java', '-cp', r'E:\GitHub\KRAUSTD\CTD\test\main\java\Embedding\EMBDI\Bayes\DART_Bayes.java', 'com.example.main.java.Embedding.EMBDI.Bayes']

# 运行Java程序并捕获输出
process = subprocess.Popen(java_cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
stdout, stderr = process.communicate()
a = 1
a = a + 1


