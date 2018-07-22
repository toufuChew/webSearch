package WebCrawler;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.atomic.AtomicInteger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import HTMLParser.JsoupParser;

public class Crawler implements Runnable{
    URL mainurl = null;
    File htmlDoc = null; //存放html
    JsoupParser parser = null;
    public static final String docurl = "/Users/chenqiu/Desktop/database/";
    public static final String urldir = "/Users/chenqiu/Desktop/URL/";
    public static final String host = "http://www1.szu.edu.cn/board/view.asp?id=";
    public static final String home = "http://www1.szu.edu.cn/board/";
    public static final File[] files = new File(docurl).listFiles();
    public static final File[] urls = new File(urldir).listFiles();
    static final int threadsnumber = 4;  //控制线程数
    static final int threadsworks = 10000; //每个线程处理的页面
    /**
     * 爬取的id基数
     */
    public static int base = 330000;
    private static AtomicInteger id = null;
    /**
     *
     * @param mainurl
     * @param docurl 存放html文件夹
     */
    public Crawler(URL mainurl,String docurl){
        this.mainurl = mainurl;
        htmlDoc = new File(docurl);
        if (!htmlDoc.exists())
            htmlDoc.mkdirs();
    }
    /**
     * 爬取时同时解析，提高效率
     *
     * （时间过长，优化一下）
     * @param url 页面链接
     */
    private synchronized void crawling(URL url){
        System.out.println(url.toString());
        try {
            URLConnection conn = url.openConnection();
            BufferedInputStream bin = new BufferedInputStream(conn.getInputStream());
            String html = new String();
            int ch;
            byte[] b = new byte[65535]; //64kb
            boolean containbody = false;
            String regex = "tbcolor";
            while((ch = bin.read(b)) != -1){
                html += new String(b,"gb2312");
                //除去主页
				/* 浪费时间
				if (!containbody && html.contains("body")) {
					Document doc = Jsoup.parse(html);
					if (doc.getElementsByTag("body").attr("class").endsWith("6")) // 主页
						return;
					containbody = true;
				}
				*/
                if (!containbody) {
                    int index = html.indexOf(regex);
                    if (index != -1) {
                        if (html.charAt(index + regex.length()) == '6')
                            return;
                        containbody = true;
                    }
                }
            }
            File htmlfile = new File(htmlDoc.getAbsolutePath() + "/" + url.toString().substring(url.toString().lastIndexOf('=') + 1) + ".html");
			/*
			if (!htmlfile.exists())
				htmlfile.createNewFile();
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(htmlfile)));
			bw.write(html);
			bw.flush();
			*/
            JsoupParser.parserDirText(docurl, html, htmlfile.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    /**
     * 针对公文通做的爬取方法
     */
    public void run(){
        String initstr = "http://www1.szu.edu.cn/board/view.asp?id=";
        for(int i = 0; i < threadsworks; i++){
            try {
                synchronized(this){
                    crawling(new URL(initstr + id.incrementAndGet()));
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

    }
    /**
     * 外部调用接口，爬取+解析
     * 解析后的文档放于Document文件夹
     * @param docurl 存放html文件夹
     */
    public static void crawlPage(String docurl){
        System.out.println("Loading...");
        //	initbase(); 临时删除
        id = new AtomicInteger(base);
        for (int i = 0; i < threadsnumber; i++)
            new Thread(new Crawler(null, docurl)).start();
        while(Thread.activeCount() > 1)
            Thread.yield();
        System.out.println("Done!!!");
    }
    private static void initbase(){
        File[] files = new File(docurl).listFiles();
        File file = files[files.length - 1];
        String name = file.getName();
        base = Integer.valueOf(name.substring(0, name.indexOf('.')));
    }
    /**
     * 公文通
     * 补充新文档到文档库
     * @param lastId 当前文档库里最后一个文档(最大)id
     */
    private void Update(int lastId){
        int newId = newestId();
        if (lastId >= newId){
            System.out.println("已是最新文档库-");
            return ;
        }
        // 补充新的文档
        for (int i = 0; i < newId - lastId; i++)
            try {
                crawling(new URL(host + (lastId + i + 1)));
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        id = new AtomicInteger(newId);
        //id.set(newId); // 设成当前最新文档id
        System.out.println("文档库更新完毕！");
    }
    /**
     * 需要更新时调用
     */
    public void Update(){
        File[] files = new File(docurl).listFiles();
        Update(Integer.valueOf(files[files.length - 1].getName().substring(0, 6)));
    }
    /**
     * 最新新闻id
     * @return
     */
    public static int newestId(){
        URL homeurl;
        try {
            homeurl = new URL(home);
            try {
                URLConnection conn = homeurl.openConnection();
                BufferedInputStream bin = new BufferedInputStream(conn.getInputStream());
                String htm = new String();
                byte[] b = new byte[65535];
                int ch;
                while ((ch = bin.read(b)) != -1)
                    htm += new String(b, "gb2312");
                return JsoupParser.paserNewestId(htm);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    /**
     * 获取html文件夹路径
     * @return
     */
    public String getFilesURL(){
        return htmlDoc.getAbsolutePath();
    }
	/*public static void main(String []args){
		//抓取与解析HTML
		Crawler.crawlPage("/Users/chenqiu/Desktop/Crawler");
	}*/
}
