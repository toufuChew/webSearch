package newWebCrawler;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

public class Crawler{
    private static final String[] mainurl = {"http://www.qq.com", "http://news.baidu.com",
            "http://www.zhihu.com", "http://www.taobao.com", "http://www.itjuzi.com",
            "http://www.dianping.com", "http://www.meituan.com", "http://36kr.com/",
            "http://www.youku.com", "http://www.chinahr.com", "http://www.autohome.com.cn",
            "http://sz.58.com/", "http://sj.qq.com/", "http://www.guahao.com/",
            "http://www.ctrip.com", "http://www.12306.cn", "http://sports.sina.com.cn/",
            "http://sports.qq.com/", "http://sports.sohu.com/", "http://bbs.hupu.com/"};
    private String docurl = "D:\\crawler\\content"; //文档位置
    private String indexurl = "D:\\crawler\\index"; //索引位置
    private File htmlDoc = null;
    private File indexDoc = null;
    private AtomicInteger id = new AtomicInteger(0);
    private boolean finish = false;
    private TaskQueue taskQueue = null;
    private int nowThreadNum = 0;
    private static int ThreadNum = 20;
    private static int maxDepth = 10;
    private int taskNum = 0;
    //    private TextArray textArray = null;
    private HashSet<String> visited = null;
    private static Crawler crawler = new Crawler();

    public static Crawler getCrawler() {
        return crawler;
    }

//    public TextArray getTextArray() {
//        return textArray;
//    }

    private Crawler() {
        htmlDoc = new File(docurl);
        if(!htmlDoc.exists() || !htmlDoc.isDirectory()) {
            htmlDoc.mkdir();
        }
        indexDoc = new File(indexurl);
        if(!indexDoc.exists() || !indexDoc.isDirectory()) {
            indexDoc.mkdir();
        }
        System.out.println(htmlDoc.exists());
        System.out.println(indexDoc.exists());
        taskQueue = TaskQueue.getTaskQueue();
//        textArray = new TextArray();
        visited = new HashSet<>();
        addInitialURL();
    }

    public void howMuchThread() {
        while(true) {
            try {
                Thread.sleep(10000);
                System.out.println("ThreadNum:" + ThreadNum);
                System.out.println("queue:" + taskQueue.getQueueSize());
                System.out.println("waitQueue:" + taskQueue.getWaitQueueSize());
                synchronized (taskQueue) {
                    taskQueue.addTaskFormWaitQueue();
                    taskQueue.notifyAll();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void addInitialURL() {
        for(int i = 0; i < mainurl.length; ++i) {
            try {
                taskQueue.addTask(MyHTMLParser.workByJsoup(mainurl[i], 0));
            } catch (IOException e) {
//                e.printStackTrace();
            }
        }
    }

    private void downloadHTML(String html, int id) {
        try {
            File file = new File(htmlDoc, id + ".txt");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] buffer = html.getBytes();
            fileOutputStream.write(buffer, 0, buffer.length);
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void downloadINDEX(String index, int id) {
        try {
            File file = new File(indexDoc, id + ".json");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] buffer = index.getBytes();
            fileOutputStream.write(buffer, 0, buffer.length);
            fileOutputStream.close();
        } catch(IOException e) {
//            e.printStackTrace();
        }
    }

    private void download(String html, String index, int id) {
        downloadHTML(html, id);
        downloadINDEX(index, id);
    }

    private void crawling() {
        while(!finish) {
            URLMessage urlMessage = null;
            while(!finish) {
                urlMessage = taskQueue.getURLMessage();
                if(urlMessage == null) {
                    synchronized (taskQueue) {
                        try {
                            --nowThreadNum;
                            taskQueue.wait();
                            ++nowThreadNum;
                            continue;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            }
            if(finish) return;
            if(urlMessage.getDepth() >= maxDepth) continue;
            System.out.println("find:" + urlMessage.getUrl());
            HashSet<String> children = urlMessage.getChildren();
//            synchronized (textArray) {
//                textArray.add(new TextContent(urlMessage.getUrl(), urlMessage.getTitle(), urlMessage.getTime()));
//                downloadHTML(MyHTMLParser.getContent(urlMessage.getUrl()), id.incrementAndGet());
//            }
            try {
                download(MyHTMLParser.getContent(urlMessage.getUrl()), MyMessageAndJson.MyMessageToJson(urlMessage.URLMessageToMyMessage()), id.incrementAndGet());
                int depth = urlMessage.getDepth();
                for(String child : children) {
                    URLMessage childURLMessage = MyHTMLParser.workByJsoup(child, depth + 1);
                    taskQueue.addTask(childURLMessage);
                }
            } catch (IOException e) {
                new Thread(() -> crawling()).start();
//                e.printStackTrace();
            }
        }
    }

    public void work() {
        taskNum = 0;
        nowThreadNum = ThreadNum;
        System.out.println(taskQueue.getQueueSize());
        System.out.println(taskQueue.getWaitQueueSize());
        new Thread(() -> howMuchThread()).start();
        for(int i = 0; i < Crawler.ThreadNum; ++i) {
            new Thread(() -> crawling()).start();
        }
    }

    public void requestHelpOfOther(String key) {
        ArrayList<String> arrayList = getHelpOfOther.work(key);
        for(String url : arrayList) {
//            synchronized (textArray) {
//                MyHTMLParser myHTMLParser = new MyHTMLParser(url);
//                textArray.add(new TextContent(url, myHTMLParser.getTitle(), myHTMLParser.getTime()));
//                downloadHTML(url, id.incrementAndGet());
//            }
            try {
                MyHTMLParser myHTMLParser = new MyHTMLParser(url);
                MyMessage myMessage = new MyMessage(url, myHTMLParser.getTitle(), myHTMLParser.getTime());
                download(myMessage.getUrl(), MyMessageAndJson.MyMessageToJson(myMessage), id.incrementAndGet());
            } catch (IOException e) {
//                e.printStackTrace();
            }
        }
    }
}

