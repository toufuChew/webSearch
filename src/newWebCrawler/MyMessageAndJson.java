package newWebCrawler;

import com.google.gson.Gson;

public class MyMessageAndJson {

    private static Gson gson = new Gson();

    public static String MyMessageToJson(MyMessage myMessage) {
        return gson.toJson(myMessage);
    }

    public static MyMessage JsonToMyMessage(String JsonObject) {
        return gson.fromJson(JsonObject, MyMessage.class);
    }

//    public static void main(String args[]) {
//        MyMessage myMessage = new MyMessage("123", "123");
//        String JsonObject = MyMessageToJson(myMessage);
//        System.out.println("Json:" + JsonObject);
//        myMessage = JsonToMyMessage(JsonObject);
//        System.out.println("MyMessage:" + myMessage);
//    }

}
