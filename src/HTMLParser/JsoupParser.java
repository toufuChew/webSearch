package HTMLParser;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class JsoupParser implements Runnable{
    private File dir = null; //存放解析文档
    String srcurl = null; //html文档路径
    public JsoupParser(String file_loc, String srcurl){
        File file = new File(file_loc);
        if(!file.exists() || !file.isDirectory())
            return ;
        dir = file;
        this.srcurl = srcurl;
    }
    /**
     * 解析HTML获取文本内容
     * 并创建为document
     * @param direct Documents目录
     * @param html
     * @param name document name 最好是该文本在文件夹的序号
     */
    public static void parserDirText(String direct, String html, String name){
        File f = new File(direct);
        if (!f.exists())
            f.mkdir();
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(direct + "/" + name.substring(0, name.indexOf('.')) + ".txt")));
            Document doc = Jsoup.parse(html);
            try {
                bw.write(doc.body().text());
                bw.flush();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    /**
     * 解析所有HMTL获取文档
     */
    public void run(){
        while(srcurl == null || dir == null) Thread.yield();
        File htmls = new File(srcurl);
        File[] files = htmls.listFiles();
        for(File f : files){
            try {
                BufferedInputStream bin = new BufferedInputStream(new FileInputStream(f));
                String html = new String();
                byte[] b = new byte[1024];
                int ch;
                try {
                    while((ch = bin.read(b)) != -1)
                        html += new String(b, "gb2312");
                    Document doc = Jsoup.parse(html);
                    String name = f.getName();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dir + "/" + name.substring(0, name.indexOf('.')) + ".txt")));
                    bw.write(doc.body().text());
                    bw.flush();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 获取到当前公文通最新的文档id
     * @param htm
     * @return int 公文通id
     */
    public static int paserNewestId(String htm){
        Document doc = Jsoup.parse(htm);
        Elements elements = doc.select("tr:has(a)");
        ArrayList<String> news = new ArrayList<String>();
        for (Element element : elements){
            Elements links = element.select("a[href]");
            for (Element link : links){
                String str = link.attr("href");
                if (str.startsWith("view"))
                    news.add(str.replaceAll(".*[^\\d](?=(\\d+))", ""));
            }
            //System.out.println(element.select("a[href]"));
        }
        int index = 0;
        for (int i = index + 1; i < news.size(); i++)
            if (news.get(index).compareTo(news.get(i)) < 0)
                index = i;
        return Integer.valueOf(news.get(index));
    }
    public static void main(String []args){
        String s = "view.asp?id=fdsffdsfs2324";
        Pattern pattern = Pattern.compile(".*[^\\d](?=(\\d+))");
        Matcher matcher = pattern.matcher(s);
        //System.out.println(matcher.group());
        System.out.println(s.matches("(?=(\\d+))"));

    }
}
