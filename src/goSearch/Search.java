package goSearch;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import InvertedIndex.CreateIndex;
import InvertedIndex.SimpleList;
import Main.JSPMessage;
import QueryCorrection.Correction;
import Ranking.*;
import WebCrawler.Crawler;
import WebUI.StemSentence;
import newWebCrawler.MyMessage;

public class Search {
    private static final SearchQueryTerm search = index.getSearchQueryTerm();
    private static final File files[] = Crawler.files;
    private ArrayList<Result> result = null; //结果，每次检索都会被覆盖
    private long time; //检索时间
    private int size; //结果大小
    private int[] urlID = null;
    private String[] keywords = null;
    private String correctwords = null;
    private boolean need;
    private String[] corr;
    public Search(){}
    public Search(String query){ //初始化
        long startTime = System.currentTimeMillis();
        result = goSearch(query);
        keywords = new HandleQuery(query).analyzeQuery();
        corr = new String[keywords.length];
        for (int i = 0; i < corr.length; i++)
            corr[i] = keywords[i];
        need = new Correction().correct(corr);//英文词纠错

        time = System.currentTimeMillis() - startTime;
        size = result.size();
        urlID = new int[result.size()];
        for (int i = 0; i < urlID.length; i++){
            String name = files[result.get(i).getId() + 1].getName();
            urlID[i] = Integer.valueOf(name.substring(0, name.indexOf('.')));
        }
    }
    public String[] getKeywords(){
        return keywords;
    }
    public String getCorrectwords(){
        if (need) {
            correctwords = new String();
            for (int i = 0; i < corr.length; i++)
                correctwords += (corr[i] + " ");
            return correctwords;
        }
        return null;
    }
    private ArrayList<Result> goSearch(String query){ //输入关键字检索
        return search.reRankingable(query);
    }
    //下面的方法要求先完成检索后再调用
    public double costTime(){
        return time/1000.0;
    }
    public int getSize(){
        return size;
    }
    /**
     * 初始化一定数量的结果
     * 若要返回前10个,则start = 0, end = 10
     * @param start 起始位置,0开始
     * @param end 末尾
     * @return
     */
    public ArrayList<Result> getSomeOfResult(int start, int end){
        if (end > result.size())
            end = result.size();
        StemSentence.addSentences(start, end, result);
        StemSentence.addTitle(start, end, result, urlID);
        StemSentence.addURL(start, end, result, urlID);
        return result;
    }
    /**
     * 只返回 (end - start)个结果
     * @param start
     * @param end
     * @return
     */
    public ArrayList<JSPMessage> getJSPMessage(int start, int end){
        getSomeOfResult(start, end);
        ArrayList<JSPMessage> jspMessages = new ArrayList<JSPMessage>();
        for (int i = 0; i < end - start; i++){
            Result node = result.get(start + i);
            String content = new String();
            for (int j = 0; j < node.getTermSentences().length; j++)
                content += node.getTermSentences()[j];
            jspMessages.add(new JSPMessage(node.getURL().toString(), node.getTitle(), content));
        }
        return jspMessages;
    }
    public ArrayList<Result> getResult(){
        return result;
    }
    public static void main(String[] args){
        //	Crawler.crawlPage("/Users/chenqiu/Desktop/Crawler"); //爬取并解析网页
        //	new Crawler(null, "/Users/chenqiu/Desktop/Crawler").Update(); //更新文档库
        //	result();
        while(true){
            System.out.println("搜索：");
            String query = new Scanner(System.in).next();
            Search search = new Search(query);
			/*
			ArrayList<Result> result = search.getSomeOfResult(0, 10);
			for (int i = 0; i < 10; i++){
				System.out.print(result.get(i).getURL().toString() + " " + result.get(i).getTitle() + "\n");
				for (int j = 0; j < result.get(i).getTermSentences().length; j++)
					System.out.print(result.get(i).getTermSentences()[j] + ", ");
				System.out.println();
			}
			*/
            ArrayList<JSPMessage> jspMessages = search.getJSPMessage(0, 10);
            for (int i = 0; i < jspMessages.size(); i++){
                System.out.println("url:" + jspMessages.get(i).getUrl() + ", title:" + jspMessages.get(i).getTitle() + ", content:" + jspMessages.get(i).getContent());
            }
        }

    }
    /**
     * 最终结果
     */
    public static void result(){
        Runtime rt = Runtime.getRuntime();
        SearchQueryTerm st = new SearchQueryTerm(Crawler.docurl);
        System.out.println( (rt.totalMemory() - rt.freeMemory()) / Math.pow(2, 30) + "G");
        System.out.println("搜索：");
        while(true){
            ArrayList<Result> arr = null;
            String query = new Scanner(System.in).next();
            long startTime = System.currentTimeMillis();
            arr = st.reRankingable(query);

            if (arr.size() <= 10)
                StemSentence.addSentences(0, arr.size(), arr);
            else{
                StemSentence.addSentences(0, 10, arr);
                StemSentence.addSentences(10, arr.size(), arr);
            }
            for (int i = 0; i < arr.size(); i++){
                System.out.print(arr.get(i).getURL().toString() + ", ");
                if (arr.get(i).getTermSentences() != null)
                    for (int j = 0; j < arr.get(i).getTermSentences().length; j++)
                        System.out.print( arr.get(i).getTermSentences()[j] + ", ");
                System.out.println();
            }
            System.out.println("搜索用时：" + (System.currentTimeMillis() - startTime) / 1000.0 + "s" + "\n文档数：" + arr.size());
        }
    }
    /**
     * 测试matched
     */
    public static void testMatched(){
        SearchQueryTerm st = new SearchQueryTerm(Crawler.docurl);
        for (int i = 0; i < st.queryterm.length; i++)
            System.out.print(st.queryterm[i] + " ");
        System.out.println();
        ArrayList<SimpleList> arr = st.matchLists();
        for (int i = 0; i < arr.size(); i++)
            System.out.println(arr.get(i).getTerm() + arr.get(i).getids());
    }
    /**
     * 测试查询tf向量
     */
    public static void testQueryVector(){
        SearchQueryTerm st = new SearchQueryTerm(Crawler.docurl);
        //int[] queryvector = Correlation.querytermTFVector(st.getPostinglists(), st.queryterm, st.matchLists());
        int[] queryvector = Correlation.querytermTFVector(st.queryterm, st.matchLists());
        for (int i = 0; i < queryvector.length; i++)
            System.out.println(queryvector[i] + " ");
    }
    /**
     * 测试文档tf向量
     */
    public static void testDocumentVector(){
        SearchQueryTerm st = new SearchQueryTerm(Crawler.docurl);
        ArrayList<DocumentVector> arr = Correlation.doctermTFVector(st.getPostinglists(), st.matchLists());
		/*
		 * id:-[x1][x2][]...[xn]
		 *
		 */
        for (int i = 0; i < arr.size(); i++)
            System.out.println(arr.get(i).getId() + "-" + arr.get(i).getTFVector());
    }
}
