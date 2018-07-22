package newWebCrawler;


public class MyMessage {
    private String url = null;
    private String title = null;
    private long time = 0;

    public MyMessage(String url, String title, long time) {
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

    @Override
    public String toString() {
        return "MyMessage{" +
                "url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", time=" + time +
                '}';
    }

}

