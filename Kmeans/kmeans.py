import matplotlib.pyplot as plt
from sklearn.cluster import KMeans
import numpy as np

data = np.random.rand(100,3)
print(data.size)
estimator = KMeans(n_clusters=3, random_state=9)
estimator.fit(data)
label_pred = estimator.labels_
zero = []
one = []
two = []
for i in range(100):
    if label_pred[i] == 0:
        zero.append(data[i])
    if label_pred[i] == 1:
        one.append(data[i])
    if label_pred[i] == 2:
        two.append(data[i])


print(zero)
print(one)
print(two)

centroids = estimator.cluster_centers_
inertia = estimator.inertia_
print(label_pred)
print(centroids)
print(inertia)