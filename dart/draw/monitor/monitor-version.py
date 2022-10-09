import matplotlib.pyplot as plt
import numpy as np
from matplotlib.ticker import MultipleLocator

name = 'monitor'
# 设置 x 轴最大最小刻度
path = "E:\GitHub\KRAUSTD\dart\picture\\"+name+".png"
plt.rcParams['font.sans-serif'] = ['Arial']  # 添加这条可以让图形显示中文
x_axis_data = [1, 2, 3, 4]
# all
y_all_ed_axis_data = [5074.636, 5062.99, 5059.30, 5059.43]
y_all_rmse_axis_data = [21.387, 21.18, 21.16, 21.16]
y_all_error_rate_axis_data = [0.15857, 0.1514, 0.1457, 0.1457]
y_all_time_axis_data = [2.75, 2.5, 2.5, 2.5]


# nop
y_nop_error_rate_axis_data = [0.235785, 0.235757, 0.235728, 0.235714]
y_nop_ed_axis_data = [5212.8449, 5212.6729, 5212.411268, 5212.75]
y_nop_rmse_axis_data = [21.666, 21.65, 21.6444, 21.656]
y_nop_time_axis_data = [3, 2.75, 4.25, 4.25]

# origin
# y_origin_error_rate_axis_data = [6.53, 14.2, 11.9, 12.8, 17.6]
# y_origin_ed_axis_data = [399.135, 399.135, 399.135, 399.135, 399.135]
# y_origin_rmse_axis_data = [6.53, 14.2, 11.9, 12.8, 17.6]
# y_origin_time_axis_data = [6.53, 14.2, 11.9, 12.8, 17.6]

# brm
y_brm_error_rate_axis_data = [0.1657142857142857, 0.1657142857142857, 0.1657142857142857, 0.1657142857142857]
y_brm_ed_axis_data = [5098.938436582024, 5098.919691329752, 5098.900386713427, 5099.07635]
y_brm_rmse_axis_data = [21.7941968761, 21.81453590084, 21.63404779652, 21.620381702473]
y_brm_time_axis_data = [4.5, 3.75, 3.5, 3.5]


y_warmstart_error_rate_axis_data = [0.2385, 0.2374, 0.2378, 0.2367]
y_warmstart_ed_axis_data = [6185, 5485, 5617, 5306]
y_warmstart_rmse_axis_data = [1988, 21.879, 21.594, 21.83]
y_warmstart_time_axis_data = [2.85, 2.75, 2.55, 2.5]

ax1=plt.subplot(1, 1, 1)
ax1.set_ylim(0.14, 0.4)

my_x_ticks = np.arange(1, 5, 1)

plt.xticks(my_x_ticks,size=30)
plt.yticks(size=30)

# x轴数字
plt.xlabel('epoch', size=30)
# y轴数字
plt.ylabel('error rate', size=30)


# plot中参数的含义分别是横轴值，纵轴值，线的形状，颜色，透明度,线的宽度和标签
plt.plot(x_axis_data, y_all_error_rate_axis_data, 'ro-', color='#4169E1', alpha=0.8, linewidth=2, label='all')
plt.plot(x_axis_data, y_nop_error_rate_axis_data, 'ro-', color='#E14169', alpha=0.8, linewidth=2, label='no penalty')
plt.plot(x_axis_data, y_brm_error_rate_axis_data, 'ro-', color='#6941E1', alpha=0.8, linewidth=2, label='BRM')
plt.plot(x_axis_data, y_warmstart_error_rate_axis_data, 'ro-', color='#69E141', alpha=0.8, linewidth=2, label='no warmstart')

# plt.plot(x_axis_data, y_all_ed_axis_data, 'ro-', color='#4169E1', alpha=0.8, linewidth=2, label='all'),
# plt.plot(x_axis_data, y_nop_ed_axis_data, 'ro-', color='#E14169', alpha=0.8, linewidth=2, label='no penalty'),
# plt.plot(x_axis_data, y_brm_ed_axis_data, 'ro-', color='#6941E1', alpha=0.8, linewidth=2, label='BRM'),
# plt.plot(x_axis_data, y_warmstart_ed_axis_data, 'ro-', color='#69E141', alpha=0.8, linewidth=2, label='no warmstart'),

# plt.plot(x_axis_data, y_all_rmse_axis_data, 'ro-', color='#4169E1', alpha=0.8, linewidth=2, label='all'),
# plt.plot(x_axis_data, y_nop_rmse_axis_data, 'ro-', color='#E14169', alpha=0.8, linewidth=2, label='no penalty'),
# plt.plot(x_axis_data, y_brm_rmse_axis_data, 'ro-', color='#6941E1', alpha=0.8, linewidth=2, label='BRM'),
# plt.plot(x_axis_data, y_warmstart_rmse_axis_data, 'ro-', color='#69E141', alpha=0.8, linewidth=2, label='no warmstart'),

# plt.plot(x_axis_data, y_all_time_axis_data, 'ro-', color='#4169E1', alpha=0.8, linewidth=2, label='all'),
# plt.plot(x_axis_data, y_nop_time_axis_data, 'ro-', color='#E14169', alpha=0.8, linewidth=2, label='no penalty'),
# plt.plot(x_axis_data, y_brm_time_axis_data, 'ro-', color='#6941E1', alpha=0.8, linewidth=2, label='BRM'),
# plt.plot(x_axis_data, y_warmstart_time_axis_data, 'ro-', color='#69E141', alpha=0.8, linewidth=2, label='no warmstart'),
# 显示标签，如果不加这句，即使在plot中加了label='一些数字'的参数，最终还是不会显示标签
plt.legend(loc="upper right",fontsize=20)
plt.show()
plt.savefig(path, dpi=600)

