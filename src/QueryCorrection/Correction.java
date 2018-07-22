package QueryCorrection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

public class Correction{
    private HashMap<String, Integer> map = null; //字典
    private static final String alphabet = "abcdefghijklmnopqrstuvwxyz";
    private static final double pow = 0.8;
    public Correction(){
        map = Statistics.words();
    }
    /**
     * 编辑距离为1的所有可能的词
     * @param word
     * @return
     */
    private ArrayList<String> editDist1(String word){
        ArrayList<String> words = new ArrayList<String>();
        BinaryWord[] binary = new BinaryWord[word.length()];
        //split
        for (int i = 0; i < word.length(); i++)
            binary[i] = new BinaryWord(word.substring(0, i), word.substring(i));
        //deletes
        for (int i = 0; i < binary.length; i++)
            if (binary[i].rw.length() > 1)
                words.add(binary[i].lw + binary[i].rw.substring(1));
            else
                words.add(binary[i].lw);
        //transposes
        for (int i = 0; i < binary.length; i++)
            if (binary[i].rw.length() > 1)
                words.add(binary[i].lw + binary[i].rw.charAt(1) + binary[i].rw.charAt(0) + binary[i].rw.substring(2));
        //replaces & inserts
        for (int i = 0; i < binary.length; i++)
            for (int j = 0; j < alphabet.length(); j++){
                words.add(binary[i].lw + alphabet.charAt(j) + binary[i].rw.substring(1));
                words.add(binary[i].lw + alphabet.charAt(j) + binary[i].rw); //inserts
            }
        //last inserts
        for (int i = 0; i < alphabet.length(); i++)
            words.add(word + alphabet.charAt(i));
        return words;
    }
    /**
     * 编辑距离为2
     * @param word
     * @return
     */
    private ArrayList<String> editDist2(String word){
        ArrayList<String> dist1 = editDist1(word);
        ArrayList<String> dist2 = new ArrayList<String>(); //只加入编辑距离为2且出现在字典中的词
        for (int i = 0; i < dist1.size(); i++){
            ArrayList<String> temp = editDist1(dist1.get(i));
            for (int j = 0; j < temp.size(); j++)
                if(map.containsKey(temp.get(j)))
                    dist2.add(temp.get(j));
        }

        return dist2;
    }
    /**
     * 获得最大概率的词
     * @param arr
     * @param islegal 是否都是合法单词
     * @return
     */
    public String maxPx(ArrayList<String> arr, boolean islegal){
        int frq = 0;
        String result = null;
        if (islegal){
            for (int i = 0; i < arr.size(); i++)
                if (frq < map.get(arr.get(i))){
                    frq = map.get(arr.get(i));
                    result = arr.get(i);
                }
        }
        else
            for (int i = 0; i < arr.size(); i++){
                if (map.containsKey(arr.get(i)))
                    if (frq < map.get(arr.get(i))){
                        frq = map.get(arr.get(i));
                        result = arr.get(i);
                    }
            }
        return result;
    }
    /**
     * 是否是合法单词(需要纠正)
     * @param word
     * @return
     */
    private boolean legalSpell(String word){
        if (map.containsKey(word))
            return true;
        return false;
    }
    /**
     * 英文词纠正
     * 纠正数：1
     * new Correction(String word).correct()
     * @param null
     * @return
     */
    public String correct(String wd){
        if (legalSpell(wd))
            return wd;
        String r1 = maxPx(editDist1(wd), false);
        int score1 = 0;
        if (r1 != null)
            score1 = map.get(r1);
        String r2 = maxPx(editDist2(wd), true);
        int score2 = 0;
        if (r2 != null)
            score2 = map.get(r2);
        if (score2 > score1 * pow)
            return r2;
        if (score2 == score1 * pow)
            return wd;
        return r1;
    }
    /**
     * 对多个词进行纠错
     * @param queryterm
     * @return false if no illegal word
     */
    public boolean correct(String[] queryterm){
        boolean hascorrect = false;
        //String regex = "[\\u4e00-\\u9fa5]"; //非中文
        String regex = "[\\u4e00-\\u9fa5]+";
        for (int i = 0; i < queryterm.length; i++){
            if (!queryterm[i].matches(regex)){
                String temp = correct(queryterm[i]);
                if (temp.compareTo(queryterm[i]) != 0){
                    hascorrect = true;
                    queryterm[i] = temp;
                }
            }
        }
        return hascorrect;
    }
    public static void main(String[] args){
        String[] query = {"calendar", "conclsion", "ture","canlendae", "conclsion", "ture","中文"};
        System.out.println(new Correction().correct(query));
        long st = System.currentTimeMillis();
        for (int i = 0; i < query.length; i++)
            System.out.println(query[i]);
        System.out.println((System.currentTimeMillis() - st)/ 1000.0 + "s");
    }
}

class BinaryWord{
    String lw = null;
    String rw = null;
    public BinaryWord(String lw, String rw){
        this.lw = lw;
        this.rw = rw;
    }
}