### 最终需要的数据就是source和sourceDA两个文件夹中的数据
### 
### sourceNew是对标注数据进行实体对齐之后产生的
### 其中sourceNew有56个标注实体
### sourceDA是增强后的数据，有94个增强实体
### 数据结构上，标注实体在前，增强实体在后，
### 理论上共有56+94 = 150实体
### temp中是所有的增强前的数据
### 需要将temp中的元组，添加到sourceNew元组的后面,最终形成了source中的完整初始数据