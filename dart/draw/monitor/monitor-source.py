import matplotlib.pyplot as plt
import numpy as np
from matplotlib.ticker import MultipleLocator

name = 'monitor'

error_rate_path = "E:\GitHub\KRAUSTD\dart\picture\\" + name + "error_rate.png"
rmse_path = "E:\GitHub\KRAUSTD\dart\picture\\" + name + "rmse.png"
ed_path = "E:\GitHub\KRAUSTD\dart\picture\\" + name + " error distance.png"
time_path = "E:\GitHub\KRAUSTD\dart\picture\\" + name + " time.png"
plt.rcParams['font.sans-serif'] = ['Arial']  # 添加这条可以让图形显示中文
# data
x_axis_data = [2, 3, 4, 5]
# error rate
y_axis_data = [0.2857, 0.1657, 0.1714, 0.1457]
# log中
y_ed_axis_data = [5098.859801545857, 5098.900143079417, 5098.976808781849, 5098.932348351738]

y_rmse_axis_data = [21.27153, 21.27156, 21.27160, 21.27157]
y_time_axis_data = [2.583, 3.9583, 2.7917, 2.7083]

my_x_ticks = np.arange(2, 6, 1)
# x y size

plt.xticks(my_x_ticks, size=30)
plt.yticks(size=30)

ax1 = plt.subplot(1, 1, 1)
ax1.set_ylim(0.14, 0.3)  # 设置 x 轴最大最小刻度
# plot中参数的含义分别是横轴值，纵轴值，线的形状，颜色，透明度,线的宽度和标签
plt.plot(x_axis_data, y_axis_data, 'ro-', color='#4169E1', alpha=0.8, linewidth=2, label=name)
# 显示标签，如果不加这句，即使在plot中加了label='一些数字'的参数，最终还是不会显示标签
plt.legend(loc="upper right", fontsize=25)
# x轴数字
plt.xlabel('source coverage', size=30)
# y轴数字
# plt.ylabel('error rate', size=30)
# plt.savefig(error_rate_path, dpi=600)
# plt.show()

# # draw
# ax1 = plt.subplot(1, 1, 1)
# ax1.set_ylim(21.271, 21.272)
# plt.plot(x_axis_data, y_rmse_axis_data, 'ro-', color='#4169E1', alpha=0.8, linewidth=2, label=name),
# plt.ylabel('rmse', size=30)
# plt.savefig(rmse_path, dpi=600)
# plt.show()
#
# ax1 = plt.subplot(1, 1, 1)
# ax1.set_ylim(5097, 5100)
# plt.ylabel('error distance', size=20)
# plt.plot(x_axis_data, y_ed_axis_data, 'ro-', color='#4169E1', alpha=0.8, linewidth=2, label=name)
# plt.savefig(ed_path, dpi=600)
# plt.show()
#
ax1 = plt.subplot(1, 1, 1)
ax1.set_ylim(2, 5)
plt.ylabel('time', size=30)
plt.plot(x_axis_data, y_time_axis_data, 'ro-', color='#4169E1', alpha=0.8, linewidth=2, label=name)
plt.savefig(time_path, dpi=600)
plt.show()
#
# plt.show()
