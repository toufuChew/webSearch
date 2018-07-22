package newWebCrawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class getHelpOfOther {
    private static final int Num = 3;

    private static String changeFormat(String link) {
        if(link != null) {
            link = link.replaceAll(" ", "");
            link = link.replaceAll("\t", "");
            link = link.replaceAll("[\\t\\n\\r]", "");

        }
        return link;
    }

    private static String findLink(String html) {
        Pattern pattern = Pattern.compile("\"http?\'?(.*?)\"?\'?\"", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(html);
        if(matcher.find()) {
            String link = matcher.group(0);
            return link.substring(link.indexOf("\"") + 1, link.lastIndexOf("\""));
        }
        return null;
    }

    public static ArrayList<String> work(String key) {
        ArrayList<String> arrayList = new ArrayList<>();
        int id = 1;
        int Page = 0;
        while(Page < 3) {
            String url = "http://www.baidu.com/s?wd=" + key + "&pn=" + (Page * 10);
            Page++;
            try {
                Document document = Jsoup.connect(url).get();
                for(int i = 0; i < 10; ++i) {
                    String html = document.getElementById(String.valueOf(id)).toString();
                    html = changeFormat(html);
                    String href = findLink(html);
                    if(href != null) {
                        arrayList.add(href);
                        ++id;
                    } else {
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return arrayList;
    }
}

