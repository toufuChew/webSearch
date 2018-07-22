package QueryCorrection;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import IKAnalyze.IKAnalyze;
/**
 * 生成训练集
 * @author chenqiu
 *
 */
public class Statistics {
    private static final String dict = "/Users/chenqiu/Desktop/dict.txt";
    private static final String big = "/Users/chenqiu/Downloads/big.txt";
    public Statistics(){
        File dictfile = new File(dict);
        if (dictfile.exists() && dictfile.length() != 0)
            return;
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        try {
            BufferedInputStream bin = new BufferedInputStream(new FileInputStream(new File(big)));
            byte[] b = new byte[65535];
            int ch;
            try {
                while((ch = bin.read(b)) != -1){
                    String txt = new String(b, "utf-8");
                    String[] arr = IKAnalyze.CNAnalyzerBStr(txt); //会过滤停用词,如and,the...
                    for (int i = 0; i < arr.length; i++) {
                        if (map.containsKey(arr[i]))
                            map.put(arr[i], map.get(arr[i]) + 1);
                        else
                            map.put(arr[i], 1);
                    }
                }
                Iterator<Map.Entry<String, Integer>> it = map.entrySet().iterator();
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dictfile)));
                while(it.hasNext()){
                    Map.Entry<String, Integer> entry = it.next();
                    bw.write(entry.getKey() + " " + entry.getValue() + "\n");
                }
                bw.flush();
                bw.close();
                System.out.println("已完成统计!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public static HashMap<String, Integer> words(){
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new BufferedInputStream(new FileInputStream(dict))));
            String row;
            HashMap<String, Integer> map = new HashMap<String, Integer>();
            try {
                while((row = br.readLine()) != null){
                    String[] s = row.split(" ");
                    map.put(s[0], Integer.valueOf(s[1]));
                }
                br.close();
                return map;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void main(String[] args){
        new Statistics();

    }
}
