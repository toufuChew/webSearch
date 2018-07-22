package goSearch;

import Ranking.SearchQueryTerm;

/**
 * Created by chenqiu on 7/10/17.
 */
public class index {
    private static final SearchQueryTerm search = new SearchQueryTerm("/Users/chenqiu/Desktop/database"); //建立的索引，所有的搜索都要依靠他
    public static SearchQueryTerm getSearchQueryTerm() {
        return search;
    }
    public static void main(String args[]) {
        while(true) {

        }
    }
}
