package getRealData;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

/**
 * Created by chenqiu on 7/10/17.
 */
public class getRealData {
    private static String index = "/Users/chenqiu/Desktop/URL";
    private static String data = "/Users/chenqiu/Desktop/database";
    private static HashMap<String, Integer> hashMap;
    public static void main(String args[]) {
        hashMap = new HashMap<>();
        try {
            File indexD = new File(index);
            File dataD = new File(data);
            JsonParser jsonParser = new JsonParser();
            for(int i = 1; i < 26163; ++i) {
                File index = new File(indexD, i + ".json");
                JsonObject jsonObject = (JsonObject) jsonParser.parse(
                        new FileReader(index.getAbsoluteFile())
                );
                String url = jsonObject.get("url").getAsString();
                if(url.endsWith("/")) {
                    url = url.substring(0, url.length() - 1);
                }
                if(!hashMap.containsKey(url)) {
                    hashMap.put(url, i);
                } else {
                    index.delete();
                    index = new File(dataD, i + ".txt");
                    index.delete();
                }
            }
            System.out.println(hashMap.size());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
