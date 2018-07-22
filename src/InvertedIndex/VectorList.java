package InvertedIndex;

import java.util.ArrayList;
import java.util.LinkedList;

public class VectorList {
    private ArrayList<TermNode> vector = null;
    private int id;
    /**
     * id --- []
     * @param id
     */
    public VectorList(int id){
        this.id = id;
        vector = new ArrayList<TermNode>();
    }
    /**
     * id --- [term]
     * @param id
     * @param term
     */
    public VectorList(int id, String term){
        this.id = id;
        vector = new ArrayList<TermNode>();
        vector.add(new TermNode(term));
    }
    /**
     * 在最后新增维度
     *
     * @param term
     */
    public void addLastNode(String term){
        vector.add(new TermNode(term));
    }
    /**
     * 链表中相应的term tf＋1
     * @param index
     */
    public void addTfByOne(int index){
        vector.get(index).addtfByOne();
    }
    /**
     * 链表最后一个term tf＋1
     */
    public void addLastTfByOne(){
        vector.get(vector.size() - 1).addtfByOne();
    }
    public ArrayList<TermNode> getVector(){
        return vector;
    }
}
