package newWebCrawler;

import java.util.HashSet;

public class URLMessage implements Comparable<URLMessage>{
    private double score = 0;
    private String url = null;
    private int depth = 0;
    private HashSet<String> children = null;
    private String title = null;
    private long time = 0;

    public URLMessage(double score, String url, int depth, HashSet<String> children, String title, long time) {
        this.score = score;
        this.url = url;
        this.depth = depth;
        this.children = children;
        this.title = title;
        this.time = time;
    }

    public double getScore() {
        return score;
    }

    public String getUrl() {
        return url;
    }

    public int getDepth() {
        return depth;
    }

    public HashSet<String> getChildren() {
        return children;
    }

    public String getTitle() {
        return title;
    }

    public long getTime() {
        return time;
    }

    @Override
    public int compareTo(URLMessage o) {
        if(score - o.score >= 0) {
            return 1;
        }
        return -1;
    }

    public MyMessage URLMessageToMyMessage() {
        return new MyMessage(url, title, time);
    }
}
