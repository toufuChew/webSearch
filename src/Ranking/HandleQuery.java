package Ranking;
import IKAnalyze.IKAnalyze;

/**
 * 可对查询语句做修正处理
 * @author chenqiu
 *
 */
public class HandleQuery {
    public String query = null;
    public HandleQuery(String query){
        this.query = query;
    }
    public String[] analyzeQuery(){
        return IKAnalyze.CNAnalyzerBStr(query);
    }
}
