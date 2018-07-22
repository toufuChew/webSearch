package WebUI;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import InvertedIndex.SimpleList;
import Ranking.Result;
import WebCrawler.Crawler;
public class StemSentence {
    private static File[] files = Crawler.files;
    private static File[] urls = Crawler.urls;
    /**
     * 给返回结果补上关键字所在的句子
     * 注：
     * 若补充结果>20则启动线程
     * 下标：start 到 end - 1
     * @param start
     * @param end 不大于返回结果大小
     * @param webResult
     */
    public static void addSentences(int start, int end, ArrayList<Result> webResult){
        if (end - start > 20)
            new Thread(new Runnable() {
                public void run() {
                    for (int i = start; i < end; i++)
                        webResult.get(i).setSentences(
                                stemSentence(files[webResult.get(i).getId() + 1], webResult.get(i).getTerms()));
                }
            }).start();
        else
            for (int i = start; i < end; i++)
                webResult.get(i).setSentences(
                        stemSentence(files[webResult.get(i).getId() + 1], webResult.get(i).getTerms()));
    }
    public static void addURL(int start, int end, ArrayList<Result> webResult, int[] urlId){
        if (end - start > 20)
            new Thread(new Runnable(){
                public void run(){
                    for (int i = start; i < end; i++)
                        try {
                            webResult.get(i).setURL(new URL(parseURL(urlId[i])));
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                }
            }).start();
        else
            for (int i = start; i < end; i++)
                try {
                    webResult.get(i).setURL(new URL(parseURL(urlId[i])));
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
    }
    public static void addTitle(int start, int end, ArrayList<Result> webResult, int[] urlId){
        if (end - start > 20)
            new Thread(new Runnable(){
                public void run(){
                    for (int i = start; i < end; i++)
                        webResult.get(i).setTitle(parseTitle(urlId[i]));
                }
            }).start();
        else
            for (int i = start; i < end; i++)
                webResult.get(i).setTitle(parseTitle(urlId[i]));
    }
    public static String parseTitle(int id){
        JsonParser jsonParser = new JsonParser();
        try {
            File title = null;
            for (int i = 0; i < urls.length; i++)
                if (Integer.valueOf(urls[i].getName().substring(0, urls[i].getName().indexOf('.'))) == id){
                    title = urls[i];
                    break;
                }
            JsonObject jsonObject = (JsonObject) jsonParser
                    .parse(new FileReader(title));
            return jsonObject.get("title").getAsString();
        } catch (JsonIOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String parseURL(int id){
        JsonParser jsonParser = new JsonParser();
        try {
            File url = null;
            for (int i = 0; i < urls.length; i++)
                if (Integer.valueOf(urls[i].getName().substring(0, urls[i].getName().indexOf('.'))) == id){
                    url = urls[i];
                    break;
                }
            JsonObject jsonObject = (JsonObject) jsonParser
                    .parse(new FileReader(url));
            return jsonObject.get("url").getAsString();
        } catch (JsonIOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 取出匹配词所在的句子
     * @param file
     * @param terms
     * @return
     */
    public static String[] stemSentence(File file, ArrayList<String> terms){
        String[] sentences = new String[terms.size()];
        try {
            BufferedInputStream bin = new BufferedInputStream(new FileInputStream(file));
            BufferedReader br = new BufferedReader(new InputStreamReader(bin));
            String s;
            StringBuffer sb = new StringBuffer();
            String txt;
            try {
                while((s = br.readLine()) != null)
                    sb.append(s);
                txt = sb.toString().trim();
                for (int i = 0; i < sentences.length; i++){
                    String t = terms.get(i);
                    if (!t.matches("[\\u4e00-\\u9fa5]+")){ //非中文
                        sentences[i] = new String(t);
                        continue;
                    }
                    //			System.out.println("t: " + t);
                    //			System.out.println(txt);
                    int tindex = txt.indexOf(t);
                    //			System.out.println("index: " + tindex);
                    int pindex = tindex + 1;
                    while(pindex > tindex)
                        pindex = new Random().nextInt(10);
                    //			System.out.println("pindex: " + pindex);
                    String str = "";
                    if (pindex != tindex)
                        str = "...";
                    sentences[i] = new String(str + txt.substring(tindex - pindex , tindex + t.length() + pindex));
                }
                br.close();
                return sentences;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

}