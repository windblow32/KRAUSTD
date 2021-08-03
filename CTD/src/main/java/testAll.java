package main.java;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class testAll {

    public static void main(String[] args) {
        String regex = "^[+-]?([0-9]*\\.?[0-9]+|[0-9]+\\.?[0-9]*)([eE][+-]?[0-9]+)?$";
//        String input = "-12.2e-3";
        String a = "1";
        boolean ismatch = Pattern.matches(regex, a);
        try {
            write();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void change(int[][] a) {
        a[0][0] = 10;
    }

    // 要读读取的csv文件的 headr
    // fixme:替换成自己的属性
//    private final String FILE_NAME = "H:\\source\\gitRep\\BigdataStudy\\Kylin\\Kylin\\data\\dept.csv";
    // 注意 : withSkipHeaderRecord 这里要设置跳过 header, 否则会吧 header 当成第一行记录
    // 如果默认分隔符不是 , 则需要手动设置

//    CSVFormat csvFormat = CSVFormat.DEFAULT
//            .withHeader(header)
//            // 设置分隔符
//            .withDelimiter(',');

    /**
     * 写入分为覆盖写入和续写，此处需要覆盖写入，所以采取PrintStream，如需续写，请使用File及BufferWriter进行
     * 文件操作，详情见下方注释代码
     * @throws IOException
     */
    public static void write() throws IOException {
        int m = 2;
        int p = 2;
        int q = 4;

        String[][][] value  =new String[m][p][q];
        for(int i = 0;i<m;i++){
            for(int j = 0;j<p;j++){
                for(int k = 0;k<q;k++){
                    value[i][j][k] = "0";
                }
            }
        }
        String[] header = new String[]{"time", "place", "city", "good"};
        String separator = ",";
        for(int i = 0;i<m;i++){
            String sourcePath = "E:\\GitHub\\ICDE2021\\CTD\\data\\Temp\\source" + i + ".csv";
//            File file = new File(sourcePath);
//            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));

            PrintStream stream = new PrintStream(sourcePath);

            for(int e = 0;e<header.length-1;e++){
//                bw.write(header[e] + separator);
                stream.print(header[e] + separator);
            }
//            bw.write(header[header.length-1] + "\n");
            stream.print(header[header.length-1] + "\n");
            for(int k = 0;k<p;k++){
                int j;
                for(j = 0;j < q-1;j++){
                    // 除了最后一个的直接输入
//                    bw.write(value[i][k][j] + separator);
                    stream.print(value[i][k][j] + separator);
                }
//                bw.write(value[i][k][j] + "\n");
                stream.print(value[i][k][j] + "\n");
            }
//            bw.close();

        }


    }





}
