package WebUI;

import java.util.ArrayList;

public class DocTerms {
    private int id;
    private ArrayList<String> terms;
    public DocTerms(int id){
        this.id = id;
        terms = new ArrayList<String>();
    }
    public void addOneTermLast(String term){
        terms.add(term);
    }
    public int getId(){
        return id;
    }
    public ArrayList<String> getTerms(){
        return terms;
    }
}
