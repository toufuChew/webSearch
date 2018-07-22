package Ranking;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * V(id):[x1,x2,x3,...]
 * @author chenqiu
 *
 */
public class DocumentVector {
    private int id;
    //	private ArrayList<Integer> tf;
    private int[] tf;
    private ArrayList<String> terms = null;
    private LinkedList<Integer> tfs = null; //与terms对应,中间结果
    /**
     * v()
     * @param id
     */
    public DocumentVector(int id, String term, int tf,int size){
        this.id = id;
        //	this.tf = new ArrayList<Integer>();
        this.tf = new int[size];
        this.terms = new ArrayList<String>();
        terms.add(term);
        this.tfs = new LinkedList<Integer>();
        tfs.add(tf);
    }
    public DocumentVector(int id, LinkedList<Integer> tfs, ArrayList<String> terms){
        this.id = id;
        this.tf = new int[tfs.size()];
        for (int i = 0; i < tf.length; i++)
            this.tf[i] = tfs.get(i);
        this.terms = terms;
    }
	/*
	*//**
     * (...,...)
     * @param tf
     * @param id
     *//*
	public DocumentVector(int tf, int id){
		this.id = id;
		this.tf = new ArrayList<Integer>();
		this.tf.add(tf);
	}
	*/
    /**
     * v(x1,x2,x3,...,tf)
     * or
     * v(tf)
     * @param tf
     */
	/*
	public void addOneDimension(int tf){
		this.tf.add(tf);
	}
	*/
    public void addOneDimension(int index, int tf){
        this.tf[index] = tf;
    }
    public void addOneTerm(String term){
        terms.add(term);
    }
    public void addOneTF(Integer tf){
        tfs.add(tf);
    }
    public int getId(){
        return id;
    }
    public int[] getTFVector(){
        return tf;
    }
    /**
     * 获取tf向量某一维度值
     * @param index
     * @return
     */
    public int getTFValue(int index){
        return tf[index];
    }
    public ArrayList<String> getTerms(){
        return terms;
    }
    /**
     * DocumentVector的节点里terms对应的tf值
     * @param index
     * @return
     */
    public int getMatchedTF(int index){
        return tfs.get(index);
    }
}
