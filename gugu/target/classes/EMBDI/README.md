## EMBDI包中外部四个文件是基础三分图的源文件，可以针对单一数据源生成EMBDI
#### （1）GenerateTripartiteGraph用于构建三分图
#### （2）GenerateRandomWalk实现随机游走的功能
#### （3）Meta_Algorithm_for_EMBDI对于上述两个文件进行了包装
#### （4）Word2VecService通过maven加入了word2vex模块，并利用之前包装好的函数，生成数据，带入训练得到word2vecModel
#### 三分图结构为Ri，a(i), A(i),即：元组，值，属性