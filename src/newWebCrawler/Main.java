package newWebCrawler;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jdk.nashorn.internal.parser.JSONParser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;

public class Main {
    public static void main(String args[]) {
//        Crawler crawler = Crawler.getCrawler();
//        crawler.work();

        HashSet<String> hashSet = new HashSet<>();
        try {
            for(int i = 1; i < 26163; ++i) {
                JsonParser jsonParser = new JsonParser();
                JsonObject jsonObject = (JsonObject) jsonParser.parse(new FileReader("D:\\crawler" +
                        "\\index\\" + i + ".json"));
                String url = jsonObject.get("url").getAsString();
                if(url.endsWith("/")) {
                    url.substring(0, url.length() - 1);
                }
                hashSet.add(url);
            }
            System.out.println("readDataNum:" + hashSet.size());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

//        JsonParser jsonParser = new JsonParser();
//        try {
//            JsonObject jsonObject = (JsonObject) jsonParser.parse(
//                    new FileReader("D:\\crawler\\index\\7.json")
//            );
//            String title = jsonObject.get("title").getAsString();
//            System.out.println(title);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        URLMessage urlMessage1 = new URLMessage(1, null, 0, null, null, 0);
//        URLMessage urlMessage2 = new URLMessage(5, null, 0, null, null, 0);
//        TreeSet<URLMessage> treeSet = new TreeSet<>();
//        treeSet.add(urlMessage1);
//        treeSet.add(urlMessage2);
//        System.out.println(treeSet.pollFirst().getScore());
    }
}
