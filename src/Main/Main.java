package Main;

import goSearch.Search;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by john on 2017/6/30.
 */
public class Main {
    private static double time;
    private static int size;
    private static Search search = null;
    private static String[] keywords = null;
    public static void search(String key) {
        search = new Search(key);
        System.out.println("search over" + search.getResult().size());
        time = search.costTime();
        size = search.getSize();
        keywords = search.getKeywords();
    }

    public static ArrayList<JSPMessage> getResult(String key, int start, int end) {
        return search.getJSPMessage(start, end);
    }

    public static double costTime() {
        return time;
    }

    public static String[] getKeywords() {
        return keywords;
    }

    public static int getSize() {
        return size;
    }

    public static String getCorrectWord() {
        return search.getCorrectwords();
    }
}
