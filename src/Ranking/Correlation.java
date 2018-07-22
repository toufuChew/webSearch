package Ranking;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import InvertedIndex.DocumentTF;
import InvertedIndex.IdNode;
import InvertedIndex.SimpleList;
import InvertedIndex.TermNode;
import InvertedIndex.VectorList;
import WebCrawler.Crawler;

public class Correlation {
    private static int[] dfs = null;
    private static File[] docs = new File(Crawler.docurl).listFiles();
    /**
     * 匹配查询词，返回int[] tf向量
     * @param postinglists
     * @param queryterm
     * @param matched
     * @return
     */
    public static int[] querytermTFVector(String[] queryterm, ArrayList<SimpleList> matched){
		/*
		int[] vector = initArray(postinglists.size());
		//matched也是有序的
		for (int i = 0, j = 0; i < matched.size(); i++){
			String mterm = matched.get(i).getTerm();
			for (; j < postinglists.size(); j++){
				if (mterm.compareTo(postinglists.get(j).getTerm()) == 0){
					for (int k = i; k < queryterm.length; k++)
						if (queryterm[k].compareTo(mterm) == 0)
							vector[j]++;
					break;
				}
			}
		}
		*/
        int[] vector = initArray(matched.size());
        for (int i = 0, j = 0; i < matched.size(); i++){
            while(j < queryterm.length && matched.get(i).getTerm().compareTo(queryterm[j]) != 0)
                j++;
            while(j < queryterm.length && matched.get(i).getTerm().compareTo(queryterm[j]) == 0){
                vector[i]++;
                j++;
            }
        }
	/*
		System.out.print("[");
		for (int i = 0; i < vector.length; i++)
			System.out.print(vector[i] + ", ");
		System.out.println("]");
	*/
        return vector;
    }
    /**
     * 匹配的各文档tf向量
     *
     * @param postinglists
     * @return
     */
    public static ArrayList<DocumentVector> doctermTFVector(ArrayList<SimpleList> postinglists, ArrayList<SimpleList> matched){

		/*
		ArrayList<DocumentVector> vectorArray = new ArrayList<DocumentVector>();
		for (int i = 0; i < matched.size(); i++){
			LinkedList<IdNode> list = matched.get(i).getids();
			Iterator<IdNode> it = list.iterator();
			if (i > 0)
				while (it.hasNext()) {
					IdNode node = it.next();
					boolean hasId = false;
					for (int j = 0; j < vectorArray.size(); j++)
						if (vectorArray.get(j).getId() == node.getId()){
							hasId = true;
							vectorArray.get(j).addOneTerm(matched.get(i).getTerm());
							vectorArray.get(j).addOneTF(node.getTermfrequence());
							break;
						}
					if (!hasId)
						vectorArray.add(new DocumentVector(node.getId(), matched.get(i).getTerm(), node.getTermfrequence(), matched.size()));
				}
			else
				while (it.hasNext()){
					IdNode node = it.next();
					vectorArray.add(new DocumentVector(node.getId(), matched.get(i).getTerm(), node.getTermfrequence(), matched.size()));
				}
		}
		//构造tf
		for (int i = 0; i < vectorArray.size(); i++){
			ArrayList<String> terms = vectorArray.get(i).getTerms();
			for (int j = 0, k = 0; k < matched.size(); k++){

				if (j < terms.size() && terms.get(j).compareTo(matched.get(k).getTerm()) == 0){
					vectorArray.get(i).addOneDimension(k, vectorArray.get(i).getMatchedTF(j));
					j++;
				}
				else
					vectorArray.get(i).addOneDimension(k, 0);
			}
		}
		*/

        HashMap<Integer, TF_Term> map = new HashMap<Integer, TF_Term>();
        for (int i = 0; i < matched.size(); i++){
            String listterm = matched.get(i).getTerm();
            Iterator<IdNode> it = matched.get(i).getids().iterator();
            if (i < 1)
                while(it.hasNext()){
                    IdNode node = it.next();
                    //LinkedList<Integer> tfs = new LinkedList<Integer>();
                    TF_Term tf_term = new TF_Term();
                    tf_term.addTFLast(node.getTermfrequence());
                    tf_term.addterm(listterm);
                    //tfs.add(node.getTermfrequence());
                    //map.put(node.getId(), tfs);
                    map.put(node.getId(), tf_term);
                }
            else
                while(it.hasNext()){
                    IdNode node = it.next();
                    if (map.containsKey(node.getId())){
                        //	LinkedList<Integer> tfs = map.get(node.getId());
                        TF_Term tf_term = map.get(node.getId());
                        if (tf_term.tfsSize() - 1 < i){
                            //for (int j = tfs.size(); j < i; j++)
                            //tfs.add(0);
                            tf_term.insertZeroTF(i);
                            //tfs.add(node.getTermfrequence());
                            tf_term.addTFLast(node.getTermfrequence());
                            tf_term.addterm(listterm);
                            //map.put(node.getId(), tfs);
                            map.put(node.getId(), tf_term);
                        }

                        else {
                            //tfs.add(node.getTermfrequence());
                            //map.put(node.getId(), tfs);
                            tf_term.addTFLast(node.getTermfrequence());
                            tf_term.addterm(listterm);
                            map.put(node.getId(), tf_term);
                        }
                    }
                    else{
                        //	LinkedList<Integer> tfs = new LinkedList<Integer>();
                        //	for (int j = 0; j < i - 1; j++)
                        //		tfs.add(0);
                        TF_Term tf_term = new TF_Term();
                        tf_term.insertZeroTF(i);
                        //	tfs.add(node.getTermfrequence());
                        tf_term.addTFLast(node.getTermfrequence());
                        tf_term.addterm(listterm);
                        //	map.put(node.getId(), tfs);
                        map.put(node.getId(), tf_term);
                    }
                }
        }

        ArrayList<DocumentVector> vectorArray = new ArrayList<DocumentVector>();
        //	Iterator<Map.Entry<Integer, LinkedList<Integer>>> it = map.entrySet().iterator();
        Iterator<Map.Entry<Integer, TF_Term>> it = map.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<Integer, TF_Term> entry = it.next();
            //	System.out.println(entry.getValue().terms);
            vectorArray.add(new DocumentVector(entry.getKey(), entry.getValue().tfs, entry.getValue().terms));
        }

        return vectorArray;

    }
    private static int[] initArray(int size){
        int[] d = new int[size];
        for (int i = 0; i < size; i++)
            d[i] = 0;
        return d;
    }
    private static double[] initDoubleArray(int size){
        double[] d = new double[size];
        for (int i = 0; i < size; i++)
            d[i] = 0;
        return d;
    }
    /**
     * df向量,所有文档及查询tf向量共用
     * @param postinglists
     * @return
     */
    private static int[] df(ArrayList<SimpleList> postinglists){
        dfs = initArray(postinglists.size());
        for (int i = 0; i < postinglists.size(); i++){
			/*
			Iterator<IdNode> it = postinglists.get(i).getids().iterator();
			while(it.hasNext()){
				IdNode node = it.next();
				dfs[i] ++;
			}
			*/
            dfs[i] = postinglists.get(i).getids().size();
        }
        return dfs;
    }
    private static double[] queryIdf(int[] df, int N){
        double[] idfs = initDoubleArray(df.length);
        for (int i = 0; i < df.length; i++)
            idfs[i] = Math.log10((N * 1.0)/df[i]);
        return idfs;
    }
    /**
     * tf-idf查询向量
     * 已进行归一化
     * @param tf
     * @param N 文档集总数
     * @return
     */
    public static double[] queryTf_idf(ArrayList<SimpleList> postinglists,int []tf, int N){
        double[] tf_idfs = queryIdf(df(postinglists), N); //idf
        double vlen = 0;
        for (int i = 0; i < tf.length; i++){
            tf_idfs[i] = tf_idfs[i] * tf[i];
            vlen += tf_idfs[i] * tf_idfs[i];
        }
        vlen = Math.pow(vlen, 0.5);
        //归一化
        for (int i = 0; i < tf.length; i++)
            tf_idfs[i] /= vlen;
        return tf_idfs;
    }
    /**
     * 某文档归一化后的tf向量
     * @param v
     * @param vectorlist id-terms
     * @return
     */
    public static double[] noramlizeDocumentTFs(DocumentVector v, int doclen){
        double[] d = new double[v.getTFVector().length];
        double vlen = doclen;
        //	System.out.print("[");
        for (int i = 0; i < v.getTFVector().length; i++){
            //int tf = v.getTFVector().get(i);
            int tf = v.getTFValue(i);
            //		vlen += tf * tf;
            d[i] = tf;

            //	System.out.print(tf + " ,");

        }
        //	System.out.println("]");

		/*for (int i = 0; i < vectorlist.getVector().size(); i++){
			vlen += Math.pow(vectorlist.getVector().get(i).getTf(), 2);
		}*/

        vlen = Math.pow(vlen, 0.5);
        for (int i = 0; i < d.length; i++)
            d[i] /= vlen;
        return d;
    }
}

class TF_Term{
    LinkedList<Integer> tfs = null;
    ArrayList<String> terms = null;
    public TF_Term(){
        tfs = new LinkedList<Integer>();
        terms = new ArrayList<String>();
    }
    /**
     * 在前面补上0
     * @param end
     */
    public void insertZeroTF(int end){
        for (int i = tfs.size(); i < end; i++)
            tfs.add(0);
    }
    public void addterm(String term){
        terms.add(term);
    }
    /**
     * 加到向量末尾
     * @param tf
     */
    public void addTFLast(int tf){
        tfs.add(tf);
    }
    public int tfsSize(){
        return tfs.size();
    }
}
