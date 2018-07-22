package InvertedIndex;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * ****************
 * 弃用
 * ****************
 * @author chenqiu
 *
 */
@Deprecated
public class DocumentTF {
    private HashMap<String, Integer> termfrequence;
    public DocumentTF(){
        termfrequence = new HashMap<String, Integer>();
    }
    public void addOne(String term){
        termfrequence.put(term, termfrequence.get(term) + 1);
    }
    public void initAllOne(ArrayList<String> arr){
        for(int i = 0; i < arr.size(); i++)
            termfrequence.put(arr.get(i), 1);
    }
    public HashMap<String, Integer> getTermFrequence(){
        return termfrequence;
    }
}
