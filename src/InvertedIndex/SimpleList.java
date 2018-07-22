package InvertedIndex;

import java.util.ArrayList;
import java.util.LinkedList;

public class SimpleList {
    private String term;
    private LinkedList<IdNode> node;
    public SimpleList(String term, int id){
        this.term = term;
        this.node = new LinkedList<IdNode>();
        this.node.add(new IdNode(id));
    }
    public void addLastID(int id){
        node.add(new IdNode(id));
    }
    public String getTerm(){
        return term;
    }
    /**
     * 使链末尾id的term frequency ＋ 1
     */
    public void addLastIdTFByOne(){
        node.getLast().addTermfrequenceOne();
        //	node.get(node.size() - 1).addTermfrequenceOne();
    }
    /**
     * 非拷贝
     * @return
     */
    public LinkedList<IdNode> getids(){
        return node;
    }
}
