package InvertedIndex;

public class TermNode {
    private String term = null;
    private int tf;
    /**
     * tf初始值设为1
     * @param term
     */
    public TermNode(String term){
        this.term = term;
        this.tf = 1;
    }
    public void addtfByOne(){
        tf++;
    }
    public String getTerm(){
        return term;
    }
    public int getTf(){
        return tf;
    }
}
