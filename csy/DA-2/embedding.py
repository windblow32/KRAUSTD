import numpy as np
import random
import time

max_features = 50
em_vector = 300


# input:a: vector1, vector:dimension
def mold(a, vector):
    sum = 0
    for i in range(vector):
        sum += a[i] ** 2
    return sum ** 0.5


# 距离是余弦距离
# input:a: vector1, b: vector2, vector:dimension
def distance(a, b, vector):
    sum = 0
    for i in range(vector):
        sum += a[i] * b[i]
    sum = sum / mold(a, vector) / mold(b, vector)
    return 1 - sum


def load_glove(word):
    EMBEDDING_FILE = "D:\\glove.6B\\glove.42B.300d.txt"
    with open(EMBEDDING_FILE, 'r', encoding='utf-8') as f:
        # 用于将embedding的每行的第一个元素word和后面为float类型的词向量分离出来。
        # *表示后面的参数按顺序作为一个元组传进函数
        # ** 表示将调用函数时，有等号的部分作为字典传入。
        def get_coefs(word, *arr): return word, np.asarray(arr, dtype='float32')

        # 将所以的word作为key，numpy数组作为value放入字典
        start = time.time()
        embeddings_index = dict(get_coefs(*o.split(" ")) for o in open(EMBEDDING_FILE, encoding='utf-8'))
        end = time.time()
        print("time for load:", end - start)
    if word not in embeddings_index.keys():
        return word

    # 查询最小距离
    min_distance = distance(embeddings_index[word], embeddings_index[","], em_vector)
    min_word = ","

    # 计算判断距离
    b = random.sample(embeddings_index.keys(), 1)[0]
    judge_distance = distance(embeddings_index[word], embeddings_index[b], 10)
    for i in range(99):
        b = random.sample(embeddings_index.keys(), 1)[0]
        if distance(embeddings_index[word], embeddings_index[b], 10) < judge_distance:
            judge_distance = distance(embeddings_index[word], embeddings_index[b], 10)
    for key, value in embeddings_index.items():
        if distance(embeddings_index[word], embeddings_index[key], 10) < judge_distance:
            if distance(embeddings_index[word], value, em_vector) < min_distance and key != word:
                min_word = key
                min_distance = distance(embeddings_index[word], value, em_vector)
    return min_word
