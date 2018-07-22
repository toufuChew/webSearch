package WebUI;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Stack;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.TitleTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import Ranking.Result;
import WebCrawler.Crawler;
import goSearch.Search;

import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.TitleTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class Window {
    public static JFrame frame = null;
    public static JButton button = null;
    public static JTextField textfield = null;
    public static Container mainPane = null;
    public static JScrollPane scrollpane = null;
    public static JPanel wrappanel = null;
    public static LinkedList<JEditorPane> editPane = null;
    public static JEditorPane currEditpane = null;
    public static JButton backButton = null;
    public static JButton forwardButton = null;
    static JButton home = null;
    static JMenuBar bar;
    static JMenu menu;
    static JMenu fileMenu;
    static Hashtable<JButton, Integer> blank_list = null; //tab按钮列表
    static JButton add_blank = null; //new窗口按钮
    public static JPanel blankPanel = null; //tab容器
    static int pageNum = 1;
    public static void InitWindow(){
        frame = new JFrame("HTTPEdge");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainPane = frame.getContentPane();
        mainPane.setLayout(new BorderLayout());
        textfield = new JTextField(24);
        editPane = new LinkedList<JEditorPane>();
        currEditpane = new JEditorPane(); //初始化EditroPane列表
        currEditpane.setEditable(false);
        editPane.add(currEditpane); //第一个页面
        scrollpane = new JScrollPane(currEditpane); //当前的editpane显示出来
        scrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
//		wrappanel = new JPanel();
//		wrappanel.setPreferredSize(new Dimension(1024,738));
        scrollpane.setPreferredSize(new Dimension(1024,738));
        //mainPane.add(scrollpane, BorderLayout.CENTER);
//		wrappanel.add(scrollpane);
        mainPane.add(scrollpane, BorderLayout.CENTER);
        bar = new JMenuBar();
        menu = new JMenu("Histroy");
        fileMenu = new JMenu("File");
        OpenFile.openFileListener();
        bar.add(fileMenu);
        bar.add(menu);
        bar.add(new JMenu("Help"));
        JPanel np = new JPanel(new BorderLayout()); //np 包括menu,textfield,tab
        np.add(bar, BorderLayout.NORTH);
        FlowLayout templayout = new FlowLayout();
        templayout.setHgap(0);
        JPanel upperPanel = new JPanel(templayout);
        button = new JButton(new ImageIcon("/Users/chenqiu/Desktop/cache/refresh.png"));
        button.setPreferredSize(new Dimension(25,25));
        backButton = new JButton();
        forwardButton = new JButton();
        home = new JButton(new ImageIcon("/Users/chenqiu/Desktop/cache/home.png"));
        backButton.add(new JLabel(new ImageIcon("/Users/chenqiu/Desktop/cache/back.png")));
        forwardButton.add(new JLabel(new ImageIcon("/Users/chenqiu/Desktop/cache/forward.png")));
        backButton.setPreferredSize(new Dimension(43, 26));
        forwardButton.setPreferredSize(new Dimension(43, 26));
        home.setPreferredSize(new Dimension(26,26));
        HTMLView viewer = new HTMLView();
        button.addActionListener(viewer);
        backButton.addActionListener(viewer);
        home.addActionListener(viewer);
        forwardButton.addActionListener(viewer);
        textfield.addKeyListener(new KeyListener(){
            public void keyTyped(KeyEvent e){
                if (e.getKeyChar() == KeyEvent.VK_ENTER && textfield.getText().compareTo("") != 0)
                    viewer.serverPage();
            }
            public void keyPressed(KeyEvent e){}
            public void keyReleased(KeyEvent e){}
        });
        upperPanel.add(backButton);
        upperPanel.add(forwardButton);
        upperPanel.add(home);
        upperPanel.add(textfield);
        upperPanel.add(button);
        add_blank = new JButton(new ImageIcon("/Users/chenqiu/Desktop/cache/add.png"));
        add_blank.setPreferredSize(new Dimension(25,25));
        NewTab tabAction = new NewTab();
        add_blank.addActionListener(tabAction); //new按钮的响应
        JPanel combine = new JPanel(new BorderLayout());
        combine.add(upperPanel, BorderLayout.WEST);
        templayout.setAlignment(FlowLayout.RIGHT);
        JPanel bp = new JPanel(templayout);
        bp.add(add_blank);
        combine.add(bp, BorderLayout.CENTER);
        np.add(combine, BorderLayout.CENTER);

        blank_list = new Hashtable<JButton, Integer>(); //初始化窗口列表
        JButton blank1 = new JButton("BLANK");
        blank1.addActionListener(tabAction); //添加第一个窗口响应
        blank_list.put(blank1, pageNum ++); //blank1加入按键列表
        FlowLayout blankLayout = new FlowLayout(FlowLayout.LEFT);
        blankLayout.setHgap(-15);
        blankPanel = new JPanel(blankLayout);
        blankPanel.add(blank1); //blank1按键加入blankPanel
        np.add(blankPanel, BorderLayout.SOUTH);
        mainPane.add(np, BorderLayout.NORTH);
        frame.setSize(700, 480);
        frame.setVisible(true);
    }
    public static void main(String[] args){
        //new Crawler(null, "/Users/chenqiu/Desktop/Crawler").Update(); //更新文档库
        new Search();
        new Thread(new Runnable(){
            public void run(){
                Window.InitWindow();
            }
        }).start();

    }
}
/**
 *
 * HTMLView构造http请求，建立与服务器的链接并下载html文件，解析显示到窗口
 * @author chenqiu
 *
 */
class HTMLView implements ActionListener{
    private URL currurl = null; //当前页面的URL
    JEditorPane editPane = null;
    private Hashtable<String, URL> url_list = null;
    private Stack<URL> forwardstack = null; //记录前进的页面,含当前页面
    private Stack<URL> backstack = null; //记录返回页面
    private URL preurl = null;
    JMenu menu = null;
    HashMap<URL, String> cookieMap = null; //保存cookie
    int backcount;
    public HTMLView(){
        while(Window.textfield == null);
        while(Window.editPane == null);
        forwardstack = new Stack<URL>();
        backstack = new Stack<URL>();
        editPane = Window.currEditpane;	//显示网页的窗口
        url_list = new Hashtable<String, URL>();	//存放历史记录与url的映射
        menu = Window.menu;	//历史记录菜单栏
        cookieMap = CookieMap.cookieMap; //cookie列表
        backcount = 0;
    }
    void backPage() throws IOException{ //返回
        if (forwardstack.size() <= 1) //前进栈空间小于1，即到了最开始的页面
            return; //不做操作
        backstack.push(forwardstack.pop()); //返回时将前进栈的栈顶页面弹出，并压入返回栈
        currurl = forwardstack.peek(); //前进栈顶就是返回后的页面
        editPane.setPage(currurl); //显示
        backcount ++;	//表示返回的次数，当在返回的页面直接继续访问时，要将前面的浏览的页面清除，所以backcount记录要清除的栈顶元素数
    }
    void forwardPage() throws IOException{ //
        if (backstack.isEmpty()) //返回栈为空，即到了最前面
            return;
        currurl = backstack.pop();
        forwardstack.push(currurl);
        editPane.setPage(currurl);
        backcount --; //返回次数减一
    }
    public void actionPerformed(ActionEvent e){
        if (e.getSource() == Window.backButton){
            try {
                backPage();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return;
        }
        if (e.getSource() == Window.forwardButton){
            try {
                forwardPage();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return;
        }
        if (e.getSource() == Window.home)
            try {
                loadPage(new URL("http://www.chan.com"));
                return;
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            }
        serverPage();
    }
    public void serverPage(){

        String str = Window.textfield.getText(); //地址栏
        if (str.compareTo("") == 0) return;

        Search search = new Search(str);
        ArrayList<Result> result = search.getResult();

        String htm = "<!DOCTYPE html><html><head><meta charset=\"utf-8\"><title>";
        htm += str;
        htm += "</title></head><body><div style=\"width: 100%; height: 100%; font-size: 14px\"><div style=\"height: 100%\">";
        if (result.size() > 0){
            htm += "<div style=\"color: #808080; font-size: small\"><span>About ";
            htm += search.getResult().size();
            htm += " results (";
            htm += search.costTime();
            htm += " seconds)</span></div>";
            for (int i = 0; i < result.size(); i++){
                htm += "<ul style=\"list-style: none;\"><li style=\"display: block;\"><a href=\"";
                htm += result.get(i).getURL().toString();
                htm += "\">深圳大学公文通</a></li><li>";
                String[] sentence = result.get(i).getTermSentences();
                for (int j = 0; j < sentence.length; j++){
                    if (j%2 == 0)
                        htm += "</br>";
                    htm += sentence[j];
                }
                htm += "</li></ul>";
            }
        }
        else{
            htm += "<span style=\"color:red;margin:4px 0 0 4px;\">抱歉，未找到相关信息</span>";
        }
        htm += "</div></div></body></html>";
        File file = new File("/Users/chenqiu/Desktop/soyso.html");
        if (!file.exists())
            try {
                file.createNewFile();
            } catch (IOException e4) {
                e4.printStackTrace();
            }
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            try {
                bw.write(htm);
                bw.flush();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (FileNotFoundException e3) {
            e3.printStackTrace();
        }

        try {
            loadPage(new URL("file:/Users/chenqiu/Desktop/soyso.html"));
            while(backcount > 0){ //返回了n次，再次点击链接时移除前n个记录
                backstack.pop();
                backcount --;
            }
        } catch (MalformedURLException e2) {
            e2.printStackTrace();
        }
        editPane.addHyperlinkListener(new HyperlinkListener(){
            public void hyperlinkUpdate(HyperlinkEvent e){
                if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
                    try {
                        loadPage(e.getURL());
                        Window.textfield.setText(e.getURL().toString());
                        while(backcount > 0){ //返回了n次，再次点击链接时移除前n个记录
                            backstack.pop();
                            backcount --;
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
            }
        });
        editPane.setEditable(false);
    }
    public void loadPage(URL url) throws MalformedURLException{
        try {
            editPane.setPage("file:/Users/chenqiu/Desktop/soyiso.html");
            editPane.setPage(url); //setPage()解析html
            forwardstack.push(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
class NewTab implements ActionListener{
    public void actionPerformed(ActionEvent e){
        Hashtable<JButton, Integer> blank_list = Window.blank_list;
        LinkedList<JEditorPane> editPane = Window.editPane;
        if (e.getSource() == Window.add_blank){ //new按钮响应，添加窗口按键
            JButton newPageButton = new JButton("BLANK");
            newPageButton.addActionListener(this); //tab按键响应
            blank_list.put(newPageButton, Window.pageNum ++);
            Window.blankPanel.add(newPageButton);
            Window.blankPanel.validate();
            JEditorPane neweditpane = new JEditorPane();
            neweditpane.setEditable(false);
            neweditpane.validate(); //重构窗口栏，否则无法显示新加入的窗口按键
            editPane.add(neweditpane);
            Window.mainPane.validate();
            return;
        }
        if (Window.currEditpane == editPane.get(blank_list.get(e.getSource()) - 1))
            return;

        Window.scrollpane.remove(0); //remove当前正在显示的panel
        Window.currEditpane = editPane.get(blank_list.get(e.getSource()) - 1);
        Window.currEditpane.validate();
        Window.scrollpane.add(Window.currEditpane); //重新显示editPane链表里对应的页面
        try {
            Window.currEditpane.setPage("http://www.baidu.com");
            System.out.println(Window.currEditpane);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        Window.scrollpane.validate();  //重构panel
        Window.scrollpane.repaint();
        Window.mainPane.validate();
        Window.mainPane.repaint();

    }
}

class AddHistory implements History, HTMLParser{
    URL currurl = null;
    JEditorPane editPane = null;
    public AddHistory(URL currurl, JEditorPane editPane){
        this.currurl = currurl;
        this.editPane = editPane;
    }
    /**
     * Parameters:
     * url_list哈希表的String存放的是域名和对应页面的title
     * _url是存放在本地的页面的URL
     */
    public JMenuItem addHistory(Hashtable<String, URL> url_list, URL url){
        String his_url = url.toString().replaceFirst("http://", "");
        //避免多次重复加入
        if (url_list.containsKey(his_url))
            return null;
        url_list.put(his_url, currurl);  //将当前页面加入哈希表
        JMenuItem item = new JMenuItem(his_url + " " +titleParser());	//历史记录加入history菜单
        item.addActionListener(new ActionListener() { //添加菜单响应
            public void actionPerformed(ActionEvent e) {
                try {
                    editPane.setPage(url_list.get(e.getActionCommand().split(" ")[0])); //e[0]就是his_url，即URL
                    ((AbstractButton) Window.blankPanel.getComponent(Window.editPane.indexOf(Window.currEditpane))).setText(titleParser()); //更新窗口名字
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        return item;
    }
    public String titleParser() {
        try{
            Parser parser = new Parser(currurl.toString());
            parser.setEncoding("utf-8"); //避免乱码
            NodeClassFilter titleFilter = new NodeClassFilter(TitleTag.class);
            NodeList titleList = parser.extractAllNodesThatMatch(titleFilter);

            for (int i = 0; i < titleList.size();){
                TitleTag title = (TitleTag) titleList.elementAt(i);
                return title.getTitle();
            }
        }catch(ParserException e){
        }
        return "";
    }
}
class CookieMap{
    static HashMap<URL, String>cookieMap = new HashMap<URL,String>();
}
class OpenFile{
    static void openFileListener(){
        JMenuItem item = new JMenuItem("Open File...");
        Window.fileMenu.add(item);
        item.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if (e.getActionCommand().compareTo("Open File...") == 0){
                    JFileChooser chooser = new JFileChooser();
                    chooser.setDialogTitle("请选择文本文件");
                    chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                    int source = chooser.showOpenDialog(null);
                    if (source == JFileChooser.APPROVE_OPTION)
                        try {
                            Window.currEditpane.setPage(new URL("file://" + chooser.getSelectedFile()));
                        } catch (MalformedURLException e1) {} catch (IOException e1) {}
                }
            }
        });
    }
}
interface History {
    public JMenuItem addHistory(Hashtable<String, URL> url_list, URL url);
}
interface HTMLParser{
    public String titleParser();
}
