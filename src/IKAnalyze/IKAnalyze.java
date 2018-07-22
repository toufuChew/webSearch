package IKAnalyze;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class IKAnalyze {
    private static IKAnalyzer analyzer = new IKAnalyzer();
    public static String[] CNAnalyzerBStr(String sentence){

        StringReader reader = new StringReader(sentence); //
        TokenStream tokenStream = analyzer.tokenStream("", reader);
        CharTermAttribute charTermAttribute = tokenStream.getAttribute(CharTermAttribute.class); //词元文本属性
        OffsetAttribute offsetAttribute = tokenStream.addAttribute(OffsetAttribute.class); //词元位移属性
        String tokens = new String();
        try {
            tokenStream.reset();
            while(tokenStream.incrementToken()){
                int startOffset = offsetAttribute.startOffset();
                int endOffset = offsetAttribute.endOffset();
                if (endOffset - startOffset > 1)
                    tokens += charTermAttribute.toString() + " ";
            }
            tokenStream.close();
            return tokens.split(" ");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 去间停用词，转换大小写
     * @param sentence
     * @return
     */
    public static ArrayList<String> CNAnalyzerBArr(String sentence){
        StringReader reader = new StringReader(sentence);
        TokenStream tokenStream = analyzer.tokenStream("", reader);
        CharTermAttribute charTermAttribute = tokenStream.getAttribute(CharTermAttribute.class);
        OffsetAttribute offsetAttribute = tokenStream.addAttribute(OffsetAttribute.class);
        ArrayList<String> tokens = new ArrayList<String>();
        try {
            tokenStream.reset();
            while(tokenStream.incrementToken()){
                int startOffset = offsetAttribute.startOffset();
                int endOffset = offsetAttribute.endOffset();
                if (endOffset - startOffset > 1)
                    tokens.add(charTermAttribute.toString());
            }
            tokenStream.close();
            return tokens;
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            reader.close();
        }
        return null;
    }
    public static void main(String[] args){
        String[] s = IKAnalyze.CNAnalyzerBStr("深圳大学English深圳- SHENZHEN University, is a beatiful colleage!");
        for (int i = 0; i < s.length; i++)
            System.out.print(s[i] + " ");
    }
}
