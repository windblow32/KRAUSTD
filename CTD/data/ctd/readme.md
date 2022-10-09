#ctd使用方法
###数据集存放位置：
####weather在new weather文件夹下的各个参数，里面有source，sourceDA，allTruth和threetruth
#### camera数据集是camera0707，有和上述相同的文件结构
#### monitor数据集是monitor0707，有和上述相同的文件结构
####ctd运行时候需要存储中间结果。其中，source文件夹中的中间结果（在ctd迭代过程中计算的）存储在sourceNew中，sourceDA的中间结果存储在sourceNewDA中。此外，迭代真值也进行了保存，其中suource数据的迭代真值在result文件夹下，增强数据的迭代真值在DAresult下
#### 需要注意的是，camera，weather，monitor的sourceNew文件夹和sourceNewDA文件夹，存放在data/ctd/路径下统一管理，和原数据不在一起！

