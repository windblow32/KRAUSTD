from matplotlib import pyplot as plt
import numpy as np
from scipy.interpolate import make_interp_spline
fig = plt.figure(figsize=(10,7),dpi=100)
plt.rcParams['font.size'] = 13

rmse_x = np.array([
    14,
    38,
    84,
    114
])

btree_y = np.array([
    369.7,
    805.2,
    1637.1,
    2164.5
])

multi_x = np.array([
    11.7,
    33.5,
    106,
    208

])

multi_y = np.array([
    90.3,
    100.7,
    132.6,
    213.6

])
plt.xlabel("size/Mb")
plt.ylabel("memory usage/Mb")
plt.annotate('only use linear combination', xy=(350, 241.6), xytext=(500, 241.6), arrowprops=dict(arrowstyle='->'))

# # kraska,multi
# plt.annotate('(200, 384)', xy=(200, 384), xytext=(200, 384))
# plt.annotate('(350, 241.6)', xy=(350, 241.6), xytext=(350, 231.6))
# plt.annotate('(762, 341)', xy=(762, 341), xytext=(762, 341))
# plt.annotate('(1515, 384)', xy=(1515, 384), xytext=(1515, 384))
# plt.annotate('(1945, 384)', xy=(1945, 384), xytext=(1945, 384))

# auto
# plt.annotate('(7, 230)', xy=(7, 230), xytext=(7, 230))
# plt.annotate('(700, 273)', xy=(700, 273), xytext=(700, 273))
# plt.annotate('(1515, 337)', xy=(1515, 337), xytext=(1515, 337))
# plt.annotate('(1945, 300)', xy=(1945, 300), xytext=(1945, 300))

# btree
plt.annotate('(14, 369.7)', xy=(14, 369.7), xytext=(14, 369.7))
plt.annotate('(38, 805.2)', xy=(38, 805.2), xytext=(38, 805.2))
plt.annotate('(84, 1637.1)', xy=(84, 1637.1), xytext=(84, 1637.1))
plt.annotate('(114, 2164.5)', xy=(114, 2164.5), xytext=(114, 2164.5))

#multi
plt.annotate('(11.7,90.3)', xy=(11.7,90.3), xytext=(11.7,90.3))
plt.annotate('(33.5, 100.7)', xy=(33.5, 100.7), xytext=(33.5, 100.7))
plt.annotate('(106, 132.6)', xy=(106, 132.6), xytext=(106, 132.6))


# kraska_x_smooth = np.linspace(kraska_x.min(),kraska_x.max(),300)
# auto_x_smooth = np.linspace(auto_x.min(),auto_x.max(),300)
# plt.plot(kraska_x, kraska_y, label='kraska,multi')
# plt.plot(kraska_x, kraska_y, 'om')
plt.plot(btree_x, btree_y, label='btree')
plt.plot(btree_x, btree_y, 'og')
plt.plot(multi_x, multi_y, label='multi-items')
plt.plot(multi_x, multi_y, 'or')
plt.legend()

# kraska_x_smooth = np.linspace(kraska_x.min(),kraska_x.max(),300)
#
#
# kraska_y_smooth = make_interp_spline(kraska_x,kraska_y)(kraska_x_smooth)
# plt.plot(kraska_x_smooth, kraska_y_smooth)
#
# auto_x_smooth = np.linspace(auto_x.min(),auto_x.max(),300)
#
#
# auto_y_smooth = make_interp_spline(auto_x,auto_y)(auto_x_smooth)
# plt.plot(auto_x_smooth, auto_y_smooth)
plt.savefig("E:/draw_picture/kraska,multi,btree,multi/memory.png")
plt.show()
