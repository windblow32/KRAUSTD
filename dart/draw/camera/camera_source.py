import matplotlib.pyplot as plt
import numpy as np
from matplotlib.ticker import MultipleLocator

name = 'camera'
ax1 = plt.subplot(1, 1, 1)
ax1.set_ylim(0, 0.2)  # 设置 x 轴最大最小刻度
error_rate_path = "E:\GitHub\KRAUSTD\dart\picture\\" + name + "error_rate.png"
rmse_path = "E:\GitHub\KRAUSTD\dart\picture\\" + name + "rmse.png"
ed_path = "E:\GitHub\KRAUSTD\dart\picture\\" + name + " error distance.png"
time_path = "E:\GitHub\KRAUSTD\dart\picture\\" + name + " time.png"
plt.rcParams['font.sans-serif'] = ['Arial']  # 添加这条可以让图形显示中文
# data
x_axis_data = [2, 3, 4, 5, 6]
# error rate
y_axis_data = [0.046, 0.136, 0.134, 0.134, 0.128]
# log中
y_ed_axis_data = [146, 318, 265, 287, 394]
#
y_rmse_axis_data = [6.53, 14.2, 11.9, 12.8, 17.6]
y_time_axis_data = [1.17, 2.25, 1.42, 1.47, 2.46]
# x plot
# plt.xlim(0,30)
# x_major_locator = MultipleLocator(3)
# ax=plt.gca()
# ax.xaxis.set_major_locator(x_major_locator)
my_x_ticks = np.arange(2, 7, 1)
# x y size

plt.xticks(my_x_ticks, size=30)
plt.yticks(size=30)
# plot中参数的含义分别是横轴值，纵轴值，线的形状，颜色，透明度,线的宽度和标签
plt.plot(x_axis_data, y_axis_data, 'ro-', color='#4169E1', alpha=0.8, linewidth=2, label=name)
# 显示标签，如果不加这句，即使在plot中加了label='一些数字'的参数，最终还是不会显示标签
plt.legend(loc="upper right", fontsize=25)
# x轴数字
plt.xlabel('source coverage', size=30)
# y轴数字
plt.ylabel('error rate', size=30)
plt.savefig(error_rate_path, dpi=600)
plt.show()

# draw

# ax1 = plt.subplot(1, 1, 1)
# ax1.set_ylim(1, 25)
# plt.xlabel('source coverage', size=30)
# plt.xticks(my_x_ticks, size=30)
# plt.yticks(size=30)
# plt.ylabel('rmse', size=30)
# plt.plot(x_axis_data, y_rmse_axis_data, 'ro-', color='#4169E1', alpha=0.8, linewidth=2, label=name),
# plt.legend(loc="upper right", fontsize=25)
# plt.savefig(rmse_path, dpi=600)
# plt.show()


# plt.legend(loc="upper right", fontsize=25)
# ax1 = plt.subplot(1, 1, 1)
# ax1.set_ylim(100, 500)
# plt.xticks(my_x_ticks, size=30)
# plt.yticks(size=30)
# plt.xlabel('source coverage', size=30)
# plt.ylabel('error distance', size=30)
# plt.plot(x_axis_data, y_ed_axis_data, 'ro-', color='#4169E1', alpha=0.8, linewidth=2, label=name)
# plt.legend(loc="upper right", fontsize=25)
# plt.savefig(ed_path, dpi=600)
# plt.show()

# plt.legend(loc="upper right", fontsize=25)
# ax1 = plt.subplot(1, 1, 1)
# ax1.set_ylim(1, 4)
# plt.xticks(my_x_ticks, size=30)
# plt.yticks(size=30)
# plt.xlabel('source coverage', size=30)
# plt.ylabel('time', size=30)
# plt.plot(x_axis_data, y_time_axis_data, 'ro-', color='#4169E1', alpha=0.8, linewidth=2, label=name)
# plt.legend(loc="upper right", fontsize=25)
# plt.savefig(time_path, dpi=600)
# plt.show()
