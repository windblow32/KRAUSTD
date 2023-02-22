#  判断数据集属性的类别
#  类别包括：
#  多值 0
#  numeric 1
#  single-word string 2
#  1-to-5-word string 3
#  5-to-10-word string 4
#  long string(>10 words) 5


import re
import csv


def judge_multi(str):
    if ';' in str:
        return True
    else:
        return False


def judge_number(str):
    regInt = '^0$|^[1-9]\d*$'  # 不接受09这样的为整数
    regFloat = '^0\.\d+$|^[1-9]\d*\.\d+$'
    regIntOrFloat = regInt + '|' + regFloat  # 整数或小数
    patternIntOrFloat = re.compile(regIntOrFloat)
    if patternIntOrFloat.search(str):
        return True
    if re.search(patternIntOrFloat, str):
        return True
    if re.search(regIntOrFloat, str):
        return True
    else:
        return False


def judge_length(str):
    if str == '':
        return 0
    length = len(str.split(' '))
    return length


def judge_attribute(str):
    if judge_multi(str):
        print(str, ":多值")
        return 0
    elif judge_number(str):
        print(str, ":numeric")
        return 1
    elif judge_length(str) == 1:
        print(str, ":single-word string")
        return 2
    elif judge_length(str) > 1 and judge_length(str) < 6:
        print(str, ":1-to-5-word string")
        return 3
    elif judge_length(str) > 5 and judge_length(str) < 11:
        print(str, ":5-to-10-word string")
        return 4
    elif judge_length(str) > 10:
        print(str, ":long string")
        return 5