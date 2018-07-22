package Main;

public class JSPMessage {
    private String url = null;
    private String title = null;
    private String content = null;

    public JSPMessage(String url, String title, String content) {
        this.url = url;
        this.title = title;
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "JSPMessage{" +
                "url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
