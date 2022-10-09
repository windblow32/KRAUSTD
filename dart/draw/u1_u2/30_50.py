import matplotlib.pyplot as plt
import numpy as np
from matplotlib.ticker import MultipleLocator

name = '30_50'
ax1=plt.subplot(1, 1, 1)
ax1.set_ylim(0, 0.02)						# 设置 x 轴最大最小刻度
error_rate_path = "E:\GitHub\KRAUSTD\dart\picture\\"+name+"error_rate.png"
rmse_path = "E:\GitHub\KRAUSTD\dart\picture\\"+name+"rmse.png"
plt.rcParams['font.sans-serif'] = ['SimHei']  # 添加这条可以让图形显示中文
x_axis_data = [5, 15, 25, 35]
y_axis_data = [0.0133, 0.0093, 0.0061, 0.0071]

# y_rmse_axis_data = [114, 133, 114, 144]
y_rmse_axis_data = [3.41, 3.44, 2.97, 3.38]
# x plot
# plt.xlim(0,30)
# x_major_locator = MultipleLocator(3)
# ax=plt.gca()
# ax.xaxis.set_major_locator(x_major_locator)
my_x_ticks = np.arange(5, 45, 10)

plt.xticks(my_x_ticks)
# plot中参数的含义分别是横轴值，纵轴值，线的形状，颜色，透明度,线的宽度和标签
plt.plot(x_axis_data, y_axis_data, 'ro-', color='#4169E1', alpha=0.8, linewidth=2, label=name),
# 显示标签，如果不加这句，即使在plot中加了label='一些数字'的参数，最终还是不会显示标签
plt.legend(loc="upper right")
# x轴数字
plt.xlabel('label per')
# y轴数字
plt.ylabel('error rate')
plt.savefig(error_rate_path, dpi=600)

ax1=plt.subplot(1, 1, 1)
ax1.set_ylim(1, 4)
plt.plot(x_axis_data, y_rmse_axis_data, 'ro-', color='#4169E1', alpha=0.8, linewidth=2, label=name),
plt.ylabel('rmse')
plt.savefig(rmse_path, dpi=600)
plt.show()

plt.show()
