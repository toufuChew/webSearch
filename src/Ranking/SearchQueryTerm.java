package Ranking;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import InvertedIndex.CreateIndex;
import InvertedIndex.SimpleList;
import InvertedIndex.VectorList;
import QueryCorrection.Correction;
import WebCrawler.Crawler;
import WebUI.StemSentence;
/**
 * 实现检索功能
 * 有关权值计算参考Correlation
 * @author chenqiu
 *
 */
public class SearchQueryTerm {
    public String[] queryterm = null;
    private String[] correctQuery = null; //纠错后的queryterm
    private boolean needcorrect = false; //是否需要纠错
    private ArrayList<SimpleList> postinglists = null;
    private ArrayList<VectorList> vectorlists = null;
    private ArrayList<String> notExitsTerm = null; //query出现了未存在的词(未做处理)
    private ArrayList<SimpleList> matchList = null;
    public static int N; //文档集大小
    private String host = Crawler.host; //公文通主页域名
    private int base = Crawler.base;
    private String docurl = Crawler.docurl;//文档
    private int[] doclen = null; // 各文档长度平方和，用于向量归一化
    int newestId = 0;
    /**
     * 初始化，调用前确认文档解析已完成
     * @param dirurl 文档文件目录
     */
    public SearchQueryTerm(String dirurl){
        if (!new File(dirurl).exists())
            return;
        newestId = Crawler.newestId(); //受网络影响

        CreateIndex invertedIndex = null;
        System.out.println("正在建立倒排索引表...");
        long startTime = System.currentTimeMillis();
        invertedIndex = new CreateIndex(dirurl);
        postinglists = invertedIndex.getInvetedIndex();
        doclen = invertedIndex.getDocslen();
        N = invertedIndex.getDocumentsetQuantity();
        //
        //vectorlists = invertedIndex.getVectorList(); //ids - term
        //
        System.out.println("建立完成，用时：" + (System.currentTimeMillis() - startTime)/1000 + "s" );
    }
    /**
     * queryterm与postinglists匹配
     * 要求：queryterm已做排序
     * @return
     */
    public ArrayList<SimpleList> matchLists(){
        notExitsTerm = new ArrayList<String>();
        ArrayList<SimpleList> matched = new ArrayList<SimpleList>();
        for (int q = 0, p = 0; q < queryterm.length; q++){
            while(p < postinglists.size() && queryterm[q].length() > postinglists.get(p).getTerm().length()) //比较长度较快
                p++;
            if (p >= postinglists.size()){ //postinglists 未出现queryterm
                for (; q < queryterm.length; q++)
                    notExitsTerm.add(queryterm[q]);
                break;
            }
            //长度小于等于
            int tp = p;
            while(tp < postinglists.size() && queryterm[q].length() == postinglists.get(tp).getTerm().length() && queryterm[q].compareTo(postinglists.get(tp).getTerm()) != 0)
                tp++;
            if (tp >= postinglists.size() || queryterm[q].length() != postinglists.get(tp).getTerm().length())
                notExitsTerm.add(queryterm[q]);
            else{
                matched.add(postinglists.get(tp));
                //查询词中出现相同的term
                while(q < queryterm.length - 1 && queryterm[q].compareTo(queryterm[q + 1]) == 0) q++;
                p = tp;
            }
            p++;
        }
        return matched;
    }
    /**
     * 多次查询
     * 不再重建倒排索引
     * @param query
     * @return new ranking();
     */
    public ArrayList<Result> reRankingable(String query){
        queryterm = new HandleQuery(query).analyzeQuery();
        sortQueryterm(queryterm);
        long oldtime = System.currentTimeMillis();
        matchList = matchLists(); //同时修改了notExitsTerm
        System.out.println("匹配query token用时:" + (System.currentTimeMillis() - oldtime)/1000.0 + "s");
        return ranking();
    }
    public boolean getNeedCorrect(){
        return needcorrect;
    }
    public String getCorrectWords(){
        String wd = new String();
        for (int i = 0; i < correctQuery.length; i++)
            wd += correctQuery[i];
        return wd;
    }
    /**
     * 确认纠错后重新搜索结果
     * @return
     */
    public ArrayList<Result> researchBycorrect(){
        sortQueryterm(correctQuery);
        long oldtime = System.currentTimeMillis();
        matchList = matchLists(); //同时修改了notExitsTerm
        System.out.println("匹配query token用时:" + (System.currentTimeMillis() - oldtime)/1000.0 + "s");
        return ranking();
    }
    /**
     * 计算某文档与查询向量的相似度
     * @param docvector
     * @return double
     */
    public double getCorrelation(double[] querytf_idf, DocumentVector docvector, int id){
        double[] doctfs = Correlation.noramlizeDocumentTFs(docvector, doclen[id]);
        double value = 0;
        for (int i = 0; i < doctfs.length; i++)
            value += (querytf_idf[i] * doctfs[i]);
        return value;
    }

    private ArrayList<Result> ranking(){
        long oldtime = System.currentTimeMillis();
        ArrayList<DocumentVector> documentVectors = Correlation.doctermTFVector(postinglists, matchList);
        System.out.println("构造document向量用时:" + (System.currentTimeMillis() - oldtime)/1000.0 + "s");
        double[] querytf_idf = Correlation.queryTf_idf(postinglists, Correlation.querytermTFVector(queryterm, matchList), N);
        ArrayList<Result> webSearchRanking = new ArrayList<Result>();
        int[] index = new int[documentVectors.size()]; // 得分从高到底依次存放文档的id编号
        double[] scores = new double[documentVectors.size()]; // 各文档得分
        //	String[][] termSentences = new String[documentVectors.size()][]; //匹配词所在的句子
        File[] files = new File(docurl).listFiles(); //第0号.DS_Store配置文件忽略
        ArrayList<Result> temp = new ArrayList<Result>();
        oldtime = System.currentTimeMillis();

        long t0 = 0; //删掉
        long t1 = 0;

        for (int i = 0; i < documentVectors.size(); i++){

            long o1 = System.currentTimeMillis();
            scores[i] = getCorrelation(querytf_idf, documentVectors.get(i), documentVectors.get(i).getId()); //余弦相似度计算的得分
            t1 += System.currentTimeMillis() - o1;

            index[i] = documentVectors.get(i).getId();
            String name = files[index[i] + 1].getName();
            //	scores[i] = timelinessFactor(scores[i], newestId - Integer.valueOf(name.substring(0, name.indexOf('.')))); //加入时效因素后的得分

            long o = System.currentTimeMillis();
            //	if (i < 10)
            //		termSentences[i] = StemSentence.stemSentence(files[index[i] + 1], documentVectors.get(i).getTerms());
            t0 += System.currentTimeMillis() - o;

            temp.add(new Result(documentVectors.get(i).getTerms(), null, scores[i], index[i])); //结果
        }

        System.out.println("返回语句用时：" + t0/1000.0 + "s");
        System.out.println("计算余弦相似度用时：" + t1/1000.0 + "s");
        System.out.println("以上用时:" + (System.currentTimeMillis() - oldtime)/1000.0 + "s");
        //代替termSentences排序
        int[] sentenceIndex = new int[documentVectors.size()];
        for (int i = 0; i < sentenceIndex.length; i++)
            sentenceIndex[i] = i;
        //降序
        for (int i = 0; i < scores.length - 1; i++){
            int k = i;
            for (int j = i + 1; j < scores.length; j++)
                if (scores[k] < scores[j])
                    k = j;
            if (k != i){
                double d = scores[i];
                scores[i] = scores[k];
                scores[k] = d;

                int t = sentenceIndex[i];
                sentenceIndex[i] = sentenceIndex[k];
                sentenceIndex[k] = t;
            }
        }

        for(int i = 0; i < index.length; i++)
            try {
                String name = files[documentVectors.get(sentenceIndex[i]).getId() + 1].getName();
                temp.get(sentenceIndex[i]).setURL(new URL(host + name.substring(0, name.indexOf('.'))));
                webSearchRanking.add(temp.get(sentenceIndex[i]));
                //webSearchRanking.add(new URL(host + name.substring(0, name.indexOf('.'))));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        return webSearchRanking;
    }
    /**
     * 时效性影响
     * 公式：new_score = ω/(ω + 0.1x)*old_score
     * @param score
     * @param factor newestId - Id
     * @return
     */
    private double timelinessFactor(double score, int factor){
        double weights = 500;
        return weights/(weights + 0.1 * factor) * score;
    }

    /**
     * 与postinglists相同的排序
     * @param queryterm
     */
    void sortQueryterm(String[] queryterm){
        for (int i = 0; i < queryterm.length - 1; i++){
            int k = i;
            for (int j = k + 1; j < queryterm.length; j++)
                if (queryterm[k].length() > queryterm[j].length()){
                    k = j;
                    break;
                }
            if (k != i){
                String str = queryterm[i];
                queryterm[i] = queryterm[k];
                queryterm[k] = str;
            }
        }
        for (int i = 0; i < queryterm.length - 1; i++){
            int k = i;
            for (int j = k + 1; j < queryterm.length; j++){
                if (queryterm[k].length() != queryterm[j].length())
                    break;
                if (queryterm[k].compareTo(queryterm[j]) > 0)
                    k = j;
            }
            if (k != i){
                String str = queryterm[i];
                queryterm[i] = queryterm[k];
                queryterm[k] = str;
            }
        }
    }
    public String[] getQueryterm(){
        return queryterm;
    }
    public ArrayList<SimpleList> getPostinglists(){
        return postinglists;
    }
    public ArrayList<VectorList> getVectorlists(){
        return vectorlists;
    }
}
