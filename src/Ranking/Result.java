package Ranking;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
/**
 * 存放结果数据
 * @author chenqiu
 *
 */
public class Result {
    private ArrayList<String> terms = null; //该文档出现的查询词
    private String[] termsentences = null; //
    private URL url = null;
    private double score = 0;
    private int id;
    private String title = null;
    /**
     *
     * @param terms
     * @param termsentences
     * @param socre
     */
    public Result(ArrayList<String> terms, String[] termsentences, double score, int id){
        this.terms = terms;
        this.termsentences = termsentences;
        this.score = score;
        this.id = id;
    }
    public void setSentences(String[] sentences){
        this.termsentences = sentences;
    }
    public void setURL(URL url){
        this.url = url;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public URL getURL(){
        return url;
    }
    public String[] getTermSentences(){
        return termsentences;
    }
    public double getScore(){
        return score;
    }
    public ArrayList<String> getTerms(){
        return terms;
    }
    public int getId(){
        return id;
    }
    public String getTitle(){
        return title;
    }
}
