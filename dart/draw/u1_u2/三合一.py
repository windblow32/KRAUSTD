import matplotlib.pyplot as plt
import numpy as np
from matplotlib.ticker import MultipleLocator

name = '三合一'
ax1=plt.subplot(1, 1, 1)
# error rate
# ax1.set_ylim(0, 0.02)						# 设置 x 轴最大最小刻度
# ed
# ax1.set_ylim(40, 190)						# 设置 x 轴最大最小刻度
# time
ax1.set_ylim(0, 50)
error_rate_path = "E:\GitHub\KRAUSTD\dart\picture\\"+name+" error_rate.png"
rmse_path = "E:\GitHub\KRAUSTD\dart\picture\\"+name+" rmse.png"
ed_path = "E:\GitHub\KRAUSTD\dart\picture\\"+name+" error distance.png"
time_path = "E:\GitHub\KRAUSTD\dart\picture\\"+name+" time.png"
plt.rcParams['font.sans-serif'] = ['SimHei']  # 添加这条可以让图形显示中文
x_axis_data = [5, 15, 25, 35]
# error rate
y_axis_data = [0.0133, 0.0093, 0.0061, 0.0071]
# rmse
y_rmse_axis_data = [3.41, 3.44, 2.97, 3.38]
# ed
y_ed_axis_data = [114, 133, 114, 144]
# time
y_time_axis_data = [27.36, 41.56, 18.17, 15.5]
# 大小设置
my_x_ticks = np.arange(0, 45, 5)
plt.xticks(my_x_ticks, size=15)
plt.yticks(size=15)
# 30 error rate
# plt.plot(x_axis_data, y_axis_data, 'ro-', color='#B9E141', alpha=0.8, linewidth=2, label='30_50')
# plt.plot(x_axis_data, y_rmse_axis_data, 'ro-', color='#B9E141', alpha=0.8, linewidth=2, label='30_50')
# plt.plot(x_axis_data, y_ed_axis_data, 'ro-', color='#B9E141', alpha=0.8, linewidth=2, label='30_50')
plt.plot(x_axis_data, y_time_axis_data, 'ro-', color='#B9E141', alpha=0.8, linewidth=2, label='30_50')

# 20
x_axis_data = [3.5, 10.5, 17.5, 24.5, 31.5]
y_axis_data = [0.0060, 0.0074, 0.0051, 0.0034, 0.0058]
y_rmse_axis_data = [3, 2.28, 1.83, 2.24, 2.97]
y_ed_axis_data = [95, 72, 58, 71, 94]
y_time_axis_data = [27.54, 17.71, 16.96, 14.13, 16.75]

# plt.plot(x_axis_data, y_axis_data, 'ro-', color='#E16941', alpha=0.8, linewidth=2, label='20_35')
# plt.plot(x_axis_data, y_rmse_axis_data, 'ro-', color='#E16941', alpha=0.8, linewidth=2, label='20_35')
# plt.plot(x_axis_data, y_ed_axis_data, 'ro-', color='#E16941', alpha=0.8, linewidth=2, label='20_35')
plt.plot(x_axis_data, y_time_axis_data, 'ro-', color='#E16941', alpha=0.8, linewidth=2, label='20_35')

# 15
x_axis_data = [3, 9, 15, 21, 27]
# error rate
y_axis_data = [0.0053, 0.004, 0.0057, 0.0061, 0.0066]
# y_rmse_axis_data = [3.31, 1.935, 1.899, 2.67, 3.43]
y_ed_axis_data = [94, 53, 52, 73, 94]
y_time_axis_data = [7.29, 14, 6.42, 20.375, 13.42]

# plot中参数的含义分别是横轴值，纵轴值，线的形状，颜色，透明度,线的宽度和标签
# plt.plot(x_axis_data, y_axis_data, 'ro-', color='#4169E1', alpha=0.8, linewidth=2, label='15_30')
# plt.plot(x_axis_data, y_rmse_axis_data, 'ro-', color='#4169E1', alpha=0.8, linewidth=2, label='15_30')
# plt.plot(x_axis_data, y_ed_axis_data, 'ro-', color='#4169E1', alpha=0.8, linewidth=2, label='15_30')
plt.plot(x_axis_data, y_time_axis_data, 'ro-', color='#4169E1', alpha=0.8, linewidth=2, label='15_30')
# 显示标签，如果不加这句，即使在plot中加了label='一些数字'的参数，最终还是不会显示标签
plt.legend(loc="upper right")
# x轴数字
plt.xlabel('label per', size=15)
# y轴数字
# plt.ylabel('error rate')
# plt.ylabel('error distance', size=15)
plt.ylabel('time', size=15)
# plt.savefig(error_rate_path, dpi=600)
# plt.savefig(rmse_path, dpi=600)
# plt.savefig(ed_path, dpi=600)
plt.savefig(time_path, dpi=600)
plt.show()


