# Random Emerging & Swapping

import csv
import numpy as np
import embedding
import time

data = []
camera = []
p1 = 0.2
p2 = 0.8
id_index = 0
website_index = 1
number_index = 2
image_resolution_index = 3
screen_size_index = 4
brand_index = 5
attribute_index = 5
B = [0, 1/2, 1/3, 2/3, 1/4, 3/4, 1/5, 2/5, 3/5, 4/5, 1/7, 2/7, 3/7, 4/7, 5/7, 6/7, 1]
B_num = [0, 2, 3, 3, 4, 4, 5, 5, 5, 5, 7, 7, 7, 7, 7, 7, 1]


class Camera(object):
    class Struct(object):
        def __init__(self, id, website, number, image_resolution, screen_size, brand, true_value):
            self.id = id
            self.website = website
            self.number = number
            self.image_resolution = image_resolution
            self.screen_size = screen_size
            self.brand = brand
            self.true_value = true_value

    def make_camera(self, id, website, number, image_resolution, screen_size, brand, true_value):
            return self.Struct(id, website, number, image_resolution, screen_size, brand, true_value)

    def tostring(self, id, website, number, image_resolution, screen_size, brand, true_value):
        website_str, number_str, image_resolution_str, screen_size_str, brand_str, true_value_str = '', '', '', '', '', ''
        for i in range(len(website)):
            website_str += ('*' + website[i])
            number_str += ('*' + number[i])
            image_resolution_str += ('*' + image_resolution[i])
            screen_size_str += ('*' + screen_size[i])
            brand_str += ('*' + brand[i])
        for i in range(len(true_value)):
            true_value_str += true_value[i]
        return id + ' ' + website_str + ' ' + number_str + ' ' + image_resolution_str + ' ' + screen_size_str + ' ' + brand_str + ' ' + true_value_str


def read_data():
    with open("E:\\camera\\camera_csy.csv", 'r') as f:
        reader = csv.reader(f)
        for row in reader:
            data.append(row)
    website = []
    number = []
    image_resolution, screen_size, brand = [], [], []
    true_value = []
    for i in range(1, len(data)-1):
        if data[i][id_index] != data[i+1][id_index]:
            my_camera = Camera()
            id = data[i][id_index]
            temp = my_camera.make_camera(id, website, number, image_resolution, screen_size, brand, true_value)
            camera.append(temp)
            website, number, image_resolution, screen_size, brand, true_value = [], [], [], [], [] ,[]
        website.append(data[i][website_index])
        number.append(data[i][number_index])
        image_resolution.append(data[i][image_resolution_index])
        screen_size.append(data[i][screen_size_index])
        brand.append(data[i][brand_index])
    '''
    for i in range(len(camera)):
        my_camera = Camera()
        print(my_camera.tostring(camera[i].id, camera[i].website, camera[i].number, camera[i].image_resolution, camera[i].screen_size, camera[i].brand, camera[i].true_value))
    '''


# 根据多值上限舍入，这里的上限为7
def rounding(a):
    discrepancy = 1
    result = 0
    # 找最近的分数
    for i in range(len(B)):
        if np.abs(B[i] - a) < discrepancy:
            discrepancy = np.abs(B[i] - a)
            result = i
    return B[result] * B_num[result], B_num[result] - B[result] * B_num[result]


# 根据属性值embedding替换词
def swap(word):
    return embedding.load_glove(word)


def ran_emer_swap(data0, data1, data2):
    A0, A1, A2 = data0, data1, data2
    # TODO:迭代次数的确定
    k = 1
    for i in range(k):
        new_A0, new_A1, new_A2 = [], [], []
        # TODO:pre_defined_size的大小确定
        pre_defined_size = 1
        while len(new_A0) < pre_defined_size:
            lamda = np.random.beta(0.2, 0.2)
            lamda_a, lamda_b = rounding(lamda)
            m = np.random.randint(0, len(data0))
            n = np.random.randint(0, len(data0))
            sample01 = data0[m]
            sample02 = data0[n]
            sample11 = data1[m]
            sample12 = data1[n]
            sample21 = data2[m]
            sample22 = data2[n]
            sample22 = swap(sample22.lower())
            sample0, sample1, sample2 = [], [], []
            for i in range(int(lamda_a)):
                sample0.append(sample01)
                sample1.append(sample11)
                sample2.append(sample21)
            for i in range(int(lamda_b)):
                sample0.append(sample02)
                sample1.append(sample12)
                sample2.append(sample22)
            new_A0.append(sample0)
            new_A1.append(sample1)
            new_A2.append(sample2)
        A0 = A0 + new_A0
        A1 = A1 + new_A1
        A2 = A2 + new_A2
    return A0, A1, A2, len(A0), len(A1), len(A2)


if __name__ == '__main__':
    print(embedding.distance([1,2],[2,3],2))
    read_data()
    data0, data1, data2 = [], [], []
    id = []
    image_resolution, screen_size, brand = [], [], []
    start = time.time()

    for i in range(1, len(data)-1):
        if data[i][id_index] != data[i+1][id_index]:
            data0.append(data[i][image_resolution_index])
            data1.append(data[i][screen_size_index])
            data2.append(data[i][brand_index])
            a, b, c, d, e, f = ran_emer_swap(data0, data1, data2)
            image_resolution += a
            screen_size += b
            brand += c
            data0, data1, data2 = [], [], []
            for j in range(d):
                id.append(data[i][id_index])
        else:
            data0.append(data[i][image_resolution_index])
            data1.append(data[i][screen_size_index])
            data2.append(data[i][brand_index])
    entity = [[0 for i in range(6)] for j in range(len(id))]
    for i in range(len(id)):
        entity[i][0] = id[i]
        entity[i][3] = image_resolution[i]
        entity[i][4] = screen_size[i]
        entity[i][5] = brand[i]

    begin = time.time()
    print("embedding for one all:", begin-start, "s")
    print(swap("canon".lower()))
    end = time.time()
    print("embedding for one word:", end-begin, "s")

    f = open('E:\\camera\\camera_csy_DA3.csv', 'w', encoding='utf-8', newline="")
    csv_writer = csv.writer(f)
    for row in range(len(entity)):
        csv_writer.writerow(entity[row])