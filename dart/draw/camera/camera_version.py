import matplotlib.pyplot as plt
import numpy as np
from matplotlib.ticker import MultipleLocator

name = 'camera-迭代轮数-error-rate'
# 设置 x 轴最大最小刻度
path = "E:\GitHub\KRAUSTD\dart\picture\\"+name+".png"
plt.rcParams['font.sans-serif'] = ['Arial']  # 添加这条可以让图形显示中文
x_axis_data = [1, 2, 3, 4]
# all
y_all_ed_axis_data = [397.99, 398.1271, 396, 394]
y_all_rmse_axis_data = [17.799, 17.89, 17.71, 17.62]
y_all_error_rate_axis_data = [0.1283, 0.1275, 0.127, 0.127]
y_all_time_axis_data = [3.33, 3.667, 3.33, 3.25]

# nop
y_nop_error_rate_axis_data = [0.1285, 0.1282, 0.128, 0.128]
y_nop_ed_axis_data = [400.72, 400, 396.25285, 396.3138]
y_nop_rmse_axis_data = [17.921, 17.8048, 17.72097, 17.7203]
y_nop_time_axis_data = [5, 5.25, 5.25, 4.5]

# origin
# y_origin_error_rate_axis_data = [6.53, 14.2, 11.9, 12.8, 17.6]
# y_origin_ed_axis_data = [399.135, 399.135, 399.135, 399.135, 399.135]
# y_origin_rmse_axis_data = [6.53, 14.2, 11.9, 12.8, 17.6]
# y_origin_time_axis_data = [6.53, 14.2, 11.9, 12.8, 17.6]

# brm
y_brm_error_rate_axis_data = [0.13, 0.13, 0.13, 0.13]
y_brm_ed_axis_data = [387.26, 386.9, 386.5, 386.33]
y_brm_rmse_axis_data = [17.31253, 17.30269, 17.284805, 17.27720]
y_brm_time_axis_data = [4, 4.25, 5, 4.5]


y_warmstart_error_rate_axis_data = [0.152, 0.139, 0.134, 0.128]
y_warmstart_ed_axis_data = [416.87, 401, 400, 398.59]
y_warmstart_rmse_axis_data = [101, 20.879, 18.594, 17.83]
y_warmstart_time_axis_data = [5.75, 5.3, 5.5, 4.75]

ax1=plt.subplot(1, 1, 1)
ax1.set_ylim(15, 120)

my_x_ticks = np.arange(1, 5, 1)

plt.xticks(my_x_ticks,size=30)
plt.yticks(size=30)

# x轴数字
plt.xlabel('epoch', size=30)
# y轴数字
plt.ylabel('rmse', size=30)


# plot中参数的含义分别是横轴值，纵轴值，线的形状，颜色，透明度,线的宽度和标签
# plt.plot(x_axis_data, y_all_error_rate_axis_data, 'ro-', color='#4169E1', alpha=0.8, linewidth=2, label='all')
# plt.plot(x_axis_data, y_nop_error_rate_axis_data, 'ro-', color='#E14169', alpha=0.8, linewidth=2, label='no penalty')
# plt.plot(x_axis_data, y_brm_error_rate_axis_data, 'ro-', color='#6941E1', alpha=0.8, linewidth=2, label='BRM')
# plt.plot(x_axis_data, y_warmstart_error_rate_axis_data, 'ro-', color='#69E141', alpha=0.8, linewidth=2, label='no warmstart')

# plt.plot(x_axis_data, y_all_ed_axis_data, 'ro-', color='#4169E1', alpha=0.8, linewidth=2, label='all'),
# plt.plot(x_axis_data, y_nop_ed_axis_data, 'ro-', color='#E14169', alpha=0.8, linewidth=2, label='no penalty'),
# plt.plot(x_axis_data, y_brm_ed_axis_data, 'ro-', color='#6941E1', alpha=0.8, linewidth=2, label='BRM'),
# plt.plot(x_axis_data, y_warmstart_ed_axis_data, 'ro-', color='#69E141', alpha=0.8, linewidth=2, label='no warmstart'),

plt.plot(x_axis_data, y_all_rmse_axis_data, 'ro-', color='#4169E1', alpha=0.8, linewidth=2, label='all'),
plt.plot(x_axis_data, y_nop_rmse_axis_data, 'ro-', color='#E14169', alpha=0.8, linewidth=2, label='no penalty'),
plt.plot(x_axis_data, y_brm_rmse_axis_data, 'ro-', color='#6941E1', alpha=0.8, linewidth=2, label='BRM'),
plt.plot(x_axis_data, y_warmstart_rmse_axis_data, 'ro-', color='#69E141', alpha=0.8, linewidth=2, label='no warmstart'),

# plt.plot(x_axis_data, y_all_time_axis_data, 'ro-', color='#4169E1', alpha=0.8, linewidth=2, label='all'),
# plt.plot(x_axis_data, y_nop_time_axis_data, 'ro-', color='#E14169', alpha=0.8, linewidth=2, label='no penalty'),
# plt.plot(x_axis_data, y_brm_time_axis_data, 'ro-', color='#6941E1', alpha=0.8, linewidth=2, label='BRM'),
# plt.plot(x_axis_data, y_warmstart_time_axis_data, 'ro-', color='#69E141', alpha=0.8, linewidth=2, label='no warmstart'),
# 显示标签，如果不加这句，即使在plot中加了label='一些数字'的参数，最终还是不会显示标签
plt.legend(loc="upper right",fontsize=25)
plt.show()
plt.savefig(path, dpi=600)





