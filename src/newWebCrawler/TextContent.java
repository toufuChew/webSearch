package newWebCrawler;


public class TextContent {
    private String url = null;
    private String title = null;
    private long time = 0;
    public TextContent(String url, String title, long time) {
        this.url = url;
        this.title = title;
        this.time = time;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public long getTime() {
        return time;
    }
}

