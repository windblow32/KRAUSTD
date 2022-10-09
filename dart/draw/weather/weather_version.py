import matplotlib.pyplot as plt
import numpy as np
from matplotlib.ticker import MultipleLocator

name = 'weather'
# 设置 x 轴最大最小刻度
path = "E:\GitHub\KRAUSTD\dart\picture\\" +name+ ".png"
plt.rcParams['font.sans-serif'] = ['Arial']  # 添加这条可以让图形显示中文
x_axis_data = [1, 2, 3, 4]
# all
y_all_ed_axis_data = [183.47,  184.7, 183.58, 183]
y_all_rmse_axis_data = [8.18, 8.13, 7.91, 7.83]
y_all_error_rate_axis_data = [0.0044, 0.0044, 0.0044, 0.0044]
y_all_time_axis_data = [449, 448, 434, 425]


# nop
y_nop_error_rate_axis_data = [0.0044, 0.0044, 0.0044, 0.0044]
y_nop_ed_axis_data = [187, 188, 186, 185]
y_nop_rmse_axis_data = [21.666, 21.65, 21.6444, 21.656]
y_nop_time_axis_data = [480.5, 515, 513, 515]

# origin
# y_origin_error_rate_axis_data = [6.53, 14.2, 11.9, 12.8, 17.6]
# y_origin_ed_axis_data = [399.135, 399.135, 399.135, 399.135, 399.135]
# y_origin_rmse_axis_data = [6.53, 14.2, 11.9, 12.8, 17.6]
# y_origin_time_axis_data = [6.53, 14.2, 11.9, 12.8, 17.6]

# brm
y_brm_error_rate_axis_data = [0.232, 0.232, 0.232, 0.232]
y_brm_ed_axis_data = [128, 124, 129, 123]
y_brm_rmse_axis_data = [5., 21.81453590084, 21.63404779652, 21.620381702473]
y_brm_time_axis_data = [486, 475, 435, 435]



ax1=plt.subplot(1, 1, 1)
ax1.set_ylim(400, 600)

my_x_ticks = np.arange(1, 5, 1)

plt.xticks(my_x_ticks,size=30)
plt.yticks(size=30)

# x轴数字
plt.xlabel('epoch', size=30)
# y轴数字
plt.ylabel('time', size=30)


# plot中参数的含义分别是横轴值，纵轴值，线的形状，颜色，透明度,线的宽度和标签
# plt.plot(x_axis_data, y_all_error_rate_axis_data, 'ro-', color='#4169E1', alpha=0.8, linewidth=2, label='all')
# plt.plot(x_axis_data, y_nop_error_rate_axis_data, 'ro-', color='#E14169', alpha=0.8, linewidth=2, label='no penalty')
# plt.plot(x_axis_data, y_brm_error_rate_axis_data, 'ro-', color='#6941E1', alpha=0.8, linewidth=2, label='BRM')

# plt.plot(x_axis_data, y_all_ed_axis_data, 'ro-', color='#4169E1', alpha=0.8, linewidth=2, label='all'),
# plt.plot(x_axis_data, y_nop_ed_axis_data, 'ro-', color='#E14169', alpha=0.8, linewidth=2, label='no penalty'),
# plt.plot(x_axis_data, y_brm_ed_axis_data, 'ro-', color='#6941E1', alpha=0.8, linewidth=2, label='BRM'),

# plt.plot(x_axis_data, y_all_rmse_axis_data, 'ro-', color='#4169E1', alpha=0.8, linewidth=2, label='all'),
# plt.plot(x_axis_data, y_nop_rmse_axis_data, 'ro-', color='#E14169', alpha=0.8, linewidth=2, label='no penalty'),
# plt.plot(x_axis_data, y_brm_rmse_axis_data, 'ro-', color='#6941E1', alpha=0.8, linewidth=2, label='BRM'),
# plt.plot(x_axis_data, y_warmstart_rmse_axis_data, 'ro-', color='#69E141', alpha=0.8, linewidth=2, label='no warmstart'),

plt.plot(x_axis_data, y_all_time_axis_data, 'ro-', color='#4169E1', alpha=0.8, linewidth=2, label='all'),
plt.plot(x_axis_data, y_nop_time_axis_data, 'ro-', color='#E14169', alpha=0.8, linewidth=2, label='no penalty'),
plt.plot(x_axis_data, y_brm_time_axis_data, 'ro-', color='#6941E1', alpha=0.8, linewidth=2, label='BRM'),
# plt.plot(x_axis_data, y_warmstart_time_axis_data, 'ro-', color='#69E141', alpha=0.8, linewidth=2, label='no warmstart'),
# 显示标签，如果不加这句，即使在plot中加了label='一些数字'的参数，最终还是不会显示标签
plt.legend(loc="upper right",fontsize=20)
plt.show()
plt.savefig(path, dpi=600)

