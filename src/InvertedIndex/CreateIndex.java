package InvertedIndex;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import IKAnalyze.IKAnalyze;

public class CreateIndex {
    private static int[] doclen = null; //每个文档长度，每个词频平方和，index = id
    private ArrayList<SimpleList> postinglists = null;
    private ArrayList<VectorList> vectorlists = null; //documents tf向量
    static final String doctype = ".txt";
    public static File[] files = null;
    private int id = 0;
    private DocumentTF[] doctf = null;

    /**
     * 需要文档文件夹url
     * @param dirurl
     */
    public CreateIndex(String dirurl){
        postinglists = new ArrayList<SimpleList>();
        //	vectorlists = new ArrayList<VectorList>();
        File dir = new File(dirurl);
        if(!dir.exists())
            return;
        files = dir.listFiles();
        doclen = new int[files.length];
    }
    /**
     * DocumetTF 从index = 0开始,对应文档id
     * **********************
     * 弃用
     * **********************
     * @param tf
     */
    public void setDocumentTFSet(DocumentTF[] tf){
        doctf = new DocumentTF[tf.length - 1];
        for (int i = 0; i < tf.length - 1; i++)
            doctf[i] = tf[i + 1];

    }
    public String readDocument(File f){
        File file = f;
        try {
            BufferedInputStream bin = new BufferedInputStream(new FileInputStream(file));
            String txt = new String();
            int ch;
            byte[] b = new byte[65535];
            try {
                while((ch = bin.read(b)) != -1)
                    txt += new String(b, "utf-8");
                bin.close();
                return txt;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 建立倒排索引 invertedIndex
     * @param dirurl Documents文件夹
     */
    public void handleLists(){
        //	DocumentTF[] tfs = new DocumentTF[files.length]; //为计算各document tf
        for (int i = 1; i < files.length; i++){ //第0个文件.DS_Store
            ArrayList<String> tokens = null;
            if (id < 1){
                tokens = IKAnalyze.CNAnalyzerBArr(readDocument(files[i])); //解析文档tokens
                tokens.sort(new Sort());//自定义排序,短<长,相同长度以字符编码为准

                int[] templen = new int[2];
                addTemsToPostingList(postinglists, tokens, id++, templen); // i : 文档id
                doclen[id] = templen[0];  //第0个文档长度
                //	System.out.println("id:" + (id - 1)  + ", " + templen[0]);
                continue;
            }
            //先获取terms，排序且合并后加入postings list
            ArrayList<String> arr = IKAnalyze.CNAnalyzerBArr(readDocument(files[i]));
            arr.sort(new Sort());

            int[] templen = new int[2];
            addOrderedArray(postinglists, arr, templen);
            doclen[id] = templen[0];
            id++;
            //	System.out.println("id:" + (id - 1)  + ", " + templen[0]);
        }

        //打印倒排索引表
        for (int i = 0; i < postinglists.size(); i++){
            LinkedList<IdNode> ids = postinglists.get(i).getids();
            System.out.print(postinglists.get(i).getTerm() + ": \t");
            for (int j = 0; j < ids.size(); j++)
                System.out.print(ids.get(j) + ",\t");
            System.out.println();
        }

        //	test();

    }
    private ArrayList<SimpleList> backLists(){
        if (files == null)
            return null;
        handleLists();
        return postinglists;
    }
    void test(){
        int oldsize = postinglists.size();
        System.out.println("oldsize:" + postinglists.size());
        for (int i = 0; i < 2000; i++){
            System.out.println(i);
            Runtime rt = Runtime.getRuntime();
            for (int j = 0; j < oldsize; j++){
                postinglists.add(new SimpleList(postinglists.get(j).getTerm(), 0));
                for (int k = 0; k < 1000; k++)
                    postinglists.get(postinglists.size() - 1).addLastID(k);
            }
            System.out.println( "heap:" + (rt.maxMemory() - rt.totalMemory() + rt.freeMemory()) / Math.pow(2, 30) + "G");
        }
        System.out.println("newsize:" + postinglists.size());
    }
    /**
     * id>0 时调用,比较方法与Sort Comparator相匹配(长度>数字>字母>汉字)
     * 将terms插入有序postings list
     * @param source
     * @param elem
     */
    void addOrderedArray(ArrayList<SimpleList> source, ArrayList<String> elem, int[] templen){
        //	vectorlists.add(new VectorList(id)); //由前一条链表新增同样大小的向量
        templen[0] = 0;

        for (int e = 0, s = 0; e < elem.size(); e++){
            String eterm = elem.get(e);
            while(s < source.size()){
                while(s < source.size() && eterm.length() > source.get(s).getTerm().length())
                    s++;
                if (s >= source.size()){
                    while(e < elem.size()){
                        source.add(new SimpleList(elem.get(e ++), id));
                        //
                        //			vectorlists.get(id).addLastNode(elem.get(e - 1));
                        templen[1] = 1;
                        //
                        while(e > 0 && e < elem.size() && elem.get(e - 1).compareTo(elem.get(e)) == 0){ //elem出现了相同term,上一元素已加入source
                            source.get(s).addLastIdTFByOne(); //此id tf＋1
                            e++;
                            //
                            //			vectorlists.get(id).addLastTfByOne();
                            templen[1]++;
                            //
                        }
                        templen[0] += templen[1] * templen[1]; //求平方和
                        s++;
                    }
                    return;
                }
                //长度<=source.get(s)
                while(s < source.size() && eterm.compareTo(source.get(s).getTerm()) > 0 && eterm.length() == source.get(s).getTerm().length())
                    s++;
                if (s >= source.size()){
                    while(e < elem.size()){
                        source.add(new SimpleList(elem.get(e ++), id));
                        //
                        //		vectorlists.get(id).addLastNode(elem.get(e - 1));
                        templen[1] = 1;
                        //
                        while(e > 0 && e < elem.size() && elem.get(e - 1).compareTo(elem.get(e)) == 0){ //elem出现了相同term,上一元素已加入source
                            source.get(s).addLastIdTFByOne(); //此id tf＋1
                            e++;
                            //
                            //			vectorlists.get(id).addLastTfByOne();
                            templen[1]++;
                            //
                        }
                        templen[0] += templen[1] * templen[1];
                        s++;
                    }
                    return;
                }
                if (eterm.compareTo(source.get(s).getTerm()) == 0){
                    source.get(s).addLastID(id);
                    //
                    //		vectorlists.get(id).addLastNode(eterm);
                    templen[1] = 1;
                    //
                }
                else{
                    //		source.add(s ++, new SimpleList(eterm, id)); //长度比source.get(s)小，或长度相等但compare<0
                    source.add(s , new SimpleList(eterm, id)); //长度比source.get(s)小，或长度相等但compare<0
                    //
                    //		vectorlists.get(id).addLastNode(eterm);
                    templen[1] = 1;
                    //
                }
                break;
            }
            while(e < elem.size() - 1 && eterm.compareTo(elem.get(e + 1)) == 0){
                source.get(s).addLastIdTFByOne();
                e++;
                //
                //		vectorlists.get(id).addLastTfByOne();
                templen[1] ++;
                //
            }
            templen[0] += templen[1] * templen[1];
        }
    }
    /**
     * 在排好序的tokens中除去相同的terms，tf＋1
     * @param arr
     * @param d
     */
    void combineSameTerms(ArrayList<String> arr, DocumentTF d){
        for (int i = 0; i < arr.size() - 1; i++){
            if (arr.get(i).compareTo(arr.get(i + 1)) == 0){
                arr.remove(i + 1);
                d.addOne(arr.get(i));
                i--;
            }
        }
    }
    /**
     * 将第id号文档的terms 加入list
     * @param list posting lists
     * @param arr terms
     * @param id
     */
    void addTemsToPostingList(ArrayList<SimpleList>list, ArrayList<String> arr, int id, int[] templen){
        //	vectorlists.add(new VectorList(id)); //添加id号链表 id --- tfs
        templen[0] = 0;
        for (int i = 0; i < arr.size(); i++){
            list.add(new SimpleList(arr.get(i), id)); // 加入单链 term --- id
            //		vectorlists.get(vectorlists.size() - 1).addLastNode(arr.get(i));; //
            templen[1] = 1;
            while (i < arr.size() -1 && arr.get(i).compareTo(arr.get(i + 1)) == 0){
                list.get(list.size() - 1).addLastIdTFByOne();
                //			vectorlists.get(vectorlists.size() - 1).addTfByOne(vectorlists.get(vectorlists.size() - 1).getVector().size() - 1);;  //
                templen[1] ++;
                i++;
            }
            templen[0] += templen[1] * templen[1];
            //i--;
        }
    }
    /**
     * 返回建好的倒排索引表
     * @param dirurl
     * @return
     */
    public static ArrayList<SimpleList> getInvetedIndex(String dirurl){
        return new CreateIndex(dirurl).backLists();
    }
    /**
     * 在调用此函数前必须确定已建好倒排索引表
     * @return
     */
    public DocumentTF[] getDocumentTFs(){
        return doctf;
    }
    /**
     * 对创建对象返回该对象的倒排索引
     * @return
     */
    public ArrayList<SimpleList> getInvetedIndex(){
        handleLists();
        return postinglists;
    }
    /**
     * 请先调用getInvertedIndex
     * @return vectorlist[]
     */
    public ArrayList<VectorList> getVectorList(){
        return vectorlists;
    }
    public int getDocumentsetQuantity(){
        return id;
    }
    public int getDocumentLength(int id){
        return doclen[id];
    }
    public int[] getDocslen(){
        return doclen;
    }
}
class Sort implements Comparator<Object>{
    public int compare(Object o1, Object o2){
        String s1 = (String)o1;
        String s2 = (String)o2;
        if (s1.length() > s2.length())
            return 1;
        if (s1.length() < s2.length())
            return -1;

        if (s1.compareTo(s2) > 0)
            return 1;
        if (s1.compareTo(s2) < 0)
            return -1;
        return 0;
    }
}