This project seeks to build a demonstration to enhance the semantic awareness of State Of The Art truth discovery algorithms using representation learning. We build a Semi-supervised Semantic-aware Truth Discovery(S3TD) framework. Given observations from multiple sources, the goal of S3TD is to resolve conflicts among observations of the same real-world entities. A data integration pipeline often contains multiple steps : data transformation, schema alignment, entity resolution, truth discovery, etc. Despite the success of semantic representation on schema alignment and entity resolution, traditional truth discovery algorithms, mostly considering textual value as categories, tend to ignore semantic ambiguity. With semi-supervised representation refinement, we can incorporate semantic similarity into truth discovery algorithms. Currently, we demonstrate S3TD with three public dataset(weather,  camera and monitor) and three SOTA algorithm(IATD, DART, CTD).
## Guide
All versions of our algorithm is in test.main.java. As to weather dataset, we present many versions of our algorithm in test.main.java.ctd.weather. Take test.main.java.ctd.weather.all for example, users can simply execute GAImpl_all.java to check the result of our whole algorithm. If you want to change dataset, please change the variable "dataPath" in row 28. The result will be shown in "dataPath/result". Other versions of our algorithm has the same file structure. DART part can refer to test.main.java.dart and IATD part can refer to test.main.java.iatd.
## Dependencies
jdk version 1.8.0_251
org.junit.Test. Tested on version 4.13.1
com.medallia.word2vec:word2vecjava_2.11. Tested on version 1.0.1
other maven dependencies, please refer to our pom.xml file
## Platforms
Our algorithm has been tested on Windows 10

