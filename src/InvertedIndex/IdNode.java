package InvertedIndex;

public class IdNode {
    private int id; //文档id
    private int termfrequence; //词项频率
    /**
     * termfrequence初始设为1
     */
    public IdNode(int id){
        this.id = id;
        this.termfrequence = 1;
    }
    public void addTermfrequenceOne(){
        this.termfrequence ++;
    }
    public String toString(){
        return "[" + id +":" + termfrequence + "]";
    }
    public int getId(){
        return id;
    }
    public int getTermfrequence(){
        return termfrequence;
    }
}
