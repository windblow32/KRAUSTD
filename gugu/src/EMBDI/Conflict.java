package EMBDI;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Conflict {
    public int row_size;
    public int col_size;
    private final List<Double> conflict = new ArrayList<>();
    public List<Double> calcConflict(List<String> fileList){

        try {
            // 读取最后一个，是真值文件
            FileReader fr = new FileReader(fileList.get(fileList.size()-1));
            BufferedReader br = new BufferedReader(fr);
            String str;
            String[] data = null;
            // 记录总行数
            row_size = 0;
            while((str=br.readLine())!=null){
                data = str.split(",");
                row_size++;
            }
            col_size = data.length;
            String[][] truth = new String[row_size][col_size];
            // 重新置0准备赋值,采用新的变量
            int row = 0;
            // 赋值得到truth数组
            FileReader truthFr = new FileReader(fileList.get(fileList.size()-1));
            BufferedReader truthBr = new BufferedReader(truthFr);
            while((str=truthBr.readLine())!=null){
                data = str.split(",");
                System.arraycopy(data, 0, truth[row], 0, col_size);
                row++;
            }
            // 对于其余元组进行赋值到矩阵中
            // String[][] value = new String[row_size][data.length];

            for(int i = 0;i<fileList.size()-1;i++){
                // 第i个文件
                String file = fileList.get(i);
                FileReader newFr = new FileReader(file);
                BufferedReader newBr = new BufferedReader(newFr);
                row = 0;
                // 针对每个文件计算其conflict数量
                int sumConflict = 0;
                while((str=newBr.readLine())!=null){
                    data = str.split(",");
                    // System.arraycopy(data, 0, value[row], 0, data.length);
                    for(int s = 0;s< col_size;s++){
                        if(!(data[s].equals(truth[row][s]))){
                            sumConflict++;
                        }
                    }
                    row++;
                }
                conflict.add((double)sumConflict/(row_size*col_size));
            }
            return conflict;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getRow_size(){
        return row_size;
    }
    public int getCol_size(){
        return col_size;
    }
}
