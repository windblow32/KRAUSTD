package main.java;

import main.java.Embedding.EMBDI.GA.GAImplTest;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Init {

    @Test
    public void initAll(){
        initVersionList();
        initRmseFile();
    }

    @Test
    public void initRmseFile(){
        List<Double> list = new ArrayList<Double>();
        list.add(0.0);
        String rmseStoreFile = "data/monitor0707/rmseFile.txt";
        File storeRmseList = new File(rmseStoreFile);

        try {
            storeRmseList.createNewFile();
            FileOutputStream fos = new FileOutputStream(storeRmseList);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(list);
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void initVersionList(){
        List<Integer> versionList = new ArrayList<Integer>();
        versionList.add(0);
        File versionFile = new File("data/monitor0707/version.txt");
        try {
            versionFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(versionFile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(versionList);
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testVersion(){
        String version_list_path = "data/dart/monitor/version.txt";
        File version_list_file = new File(version_list_path);

        try {
            FileInputStream inputVersion = new FileInputStream(version_list_file);
            ObjectInputStream objectVersion = new ObjectInputStream(inputVersion);
            List<Integer> versionList = (List<Integer>)objectVersion.readObject();
            int a = 0;

        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

    }
    @Test
    public void changesource(){
        GAImplTest ga = new GAImplTest();
        for(int i = 2;i<=5;i++){
            ga.sourceNum = i;
            ga.test();
        }

    }
}
