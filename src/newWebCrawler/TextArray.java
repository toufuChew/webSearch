package newWebCrawler;

import java.util.ArrayList;

public class TextArray {
    ArrayList<TextContent> arrayList = null;
    public TextArray() {
        arrayList = new ArrayList<>();
    }
    public void add(TextContent textContent) {
        arrayList.add(textContent);
    }
    public TextContent get(int id) {
        return arrayList.get(id);
    }
}
