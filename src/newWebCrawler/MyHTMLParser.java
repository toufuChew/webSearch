package newWebCrawler;

import org.jsoup.Jsoup;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MyHTMLParser {

    private URLConnection urlConnection = null;
    private Connection connection = null;
    private String address = null;
    private URL url = null;
    private Document document = null;

    private static String getHost(String url) {
        if(url == "" || url.trim().equals("")) {
            return null;
        }
        String host = null;
        Pattern p = Pattern.compile("(?<=//|)((\\w)+\\.)+\\w+");
        Matcher matcher = p.matcher(url);
        if(matcher.find()) {
            host = matcher.group();
        }
        return host;
    }

    private static void JsoupHeader(Connection connection, String url) {
        connection.header("Host", getHost(url));
        connection.header("Proxy-Connection", "keep-alive");
        connection.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36");
        connection.header("Accept-Encoding", "gzip,deflate,sdch");
        connection.header("Accept-Language", "zh-CN,zh;q=0.8");
    }

    public static String getContent(String url) throws IOException{
        Connection connection = Jsoup.connect(url);
        JsoupHeader(connection, url);
        Document document = connection.get();
        return document.body().text();
    }

    public static URLMessage workByJsoup(String url, int depth) throws IOException {
        MyHTMLParser myHTMLParser = new MyHTMLParser(url);
        Document document = myHTMLParser.document;
        String html = document.toString();
        if(html == null || html.equals("")) {
            return null;
        }
        HashSet<String> hashSet = new HashSet<>();
        Element body = document.body();
        Elements elements = body.select("a");
        for(Element element : elements) {
            String nextURL = element.attr("href");
            if(nextURL != null && !nextURL.equals("") && nextURL.indexOf("http") == 0 && !nextURL.equals(url)) {
                hashSet.add(nextURL);
            }
        }
        return new URLMessage(depth, url, depth, hashSet, myHTMLParser.getTitle(), myHTMLParser.getTime());
    }

    public long getTime() {
        return urlConnection.getLastModified();
    }

    public String getAttr(String attr) {
        return document.attr(attr);
    }

    public String getTitle() {
        return document.title();
    }

    public MyHTMLParser(String address) throws IOException {
        this.address = address;
        url = new URL(address);
        urlConnection = url.openConnection();
        connection = Jsoup.connect(address);
        JsoupHeader(connection, address);
        document = connection.get();
    }
}
