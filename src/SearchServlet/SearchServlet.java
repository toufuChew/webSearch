package SearchServlet;


import Main.JSPMessage;
import Main.Main;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.util.Version;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class SearchServlet extends HttpServlet{

    private static final int maxMessageNum = 10;

    public SearchServlet() {
        super();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String key = request.getParameter("key");
        System.out.println("key:" + key);
        if(key != null && key.equals("") == false) {
            String pageString = request.getParameter("p");
            String hasSearch = request.getParameter("has");
            if(hasSearch != null && hasSearch.equals("T")) {
                Main.search(key);
            }
            int p = pageString == null ? 1 : Integer.parseInt(pageString);
            int size = Main.getSize();
            int totalPage = size / maxMessageNum + (size % maxMessageNum == 0 ? 0 : 1);
            int leftNum = (p - 1) * maxMessageNum;
            int rightNum = min(p * maxMessageNum, size);
            int messageNum = rightNum - leftNum;
            Page page = new Page(p, totalPage, messageNum, size, leftNum, rightNum,
                    leftNum != 0, rightNum != size);

            ArrayList<JSPMessage> messageList = getMessageList(key, leftNum, rightNum);
            request.setAttribute("key", key);
            request.setAttribute("messageList", messageList);
            request.setAttribute("page", page);
            request.setAttribute("time", Main.costTime());
            System.out.println(Main.getCorrectWord());
            request.setAttribute("correct", Main.getCorrectWord());
            request.getRequestDispatcher("result.jsp").forward(request, response);
        } else {
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
    }

    private int min(int a, int b) {
        return a >= b ? b : a;
    }

    public ArrayList<JSPMessage> getMessageList(String key, int start, int end) {
        try {
            ArrayList<JSPMessage> messageList = Main.getResult(key, start, end);
//            String[] fields = {"news_title"};
//            Analyzer analyzer = new StandardAnalyzer();
//            MultiFieldQueryParser parser = new MultiFieldQueryParser(fields, analyzer);
//            Query query = parser.parse(key);
//            QueryScorer scorer = new QueryScorer(query, fields[0]);
//            SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<span style=\"color:red;\">", "</span>");
//            Highlighter highlighter = new Highlighter(formatter, scorer);
//            for(MyMessage myMessage : messageList) {
//                Fragmenter fragmenter = new SimpleSpanFragmenter(scorer);
//                highlighter.setTextFragmenter(fragmenter);
//            }

//            TermQuery termQuery = new TermQuery(new Term("field", key));
            String[] keywords = Main.getKeywords();
            for(int i = 0; i < messageList.size(); ++i) {
                JSPMessage jspMessage = messageList.get(i);
//                TokenStream tokenStreamContent = new StandardAnalyzer()
//                        .tokenStream("field", new StringReader(jspMessage.getContent()));
//                TokenStream tokenStreamTitle = new StandardAnalyzer()
//                        .tokenStream("field", new StringReader(jspMessage.getTitle()));
//                QueryScorer queryScorer = new QueryScorer(termQuery);
//                Highlighter highlighter = new Highlighter(queryScorer);
//                highlighter.setTextFragmenter(new SimpleSpanFragmenter(queryScorer));
//                String title = highlighter.getBestFragment(tokenStreamTitle, jspMessage.getTitle());
////                System.out.println("title:" + myMessage.getTitle());
//                String content = highlighter.getBestFragment(tokenStreamContent, jspMessage.getContent());
                String title = jspMessage.getTitle();
                String content = jspMessage.getContent();
                for (int j = 0; j < keywords.length; ++j) {
                    content = content.replaceAll(keywords[j], "<B>" + keywords[j] + "</B>");
                    title = title.replaceAll(keywords[j], "<B>" + keywords[j] + "</B>");
                }
                jspMessage.setContent(content);
                jspMessage.setTitle(title);
//                System.out.println(myMessage);
                messageList.set(i, jspMessage);
            }
            return messageList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
