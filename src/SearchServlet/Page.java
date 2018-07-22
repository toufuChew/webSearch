package SearchServlet;

public class Page {
    private int page = 1; //当前页数
    private int totalPage = 0; //总页数
    private int messageNum = 0; //当前页url数
    private int totalMessageNum = 0; //总url数
    private int startNum = 0; //当前页url的第一个索引位置
    private int endNum = 0;
    private boolean hasPrevPage = false;
    private boolean hasNextPage = false;

    public Page(int page, int totalPage, int messageNum, int totalMessageNum,
                int startNum, int endNum, boolean hasPrevPage, boolean hasNextPage) {
        this.page = page;
        this.totalPage = totalPage;
        this.messageNum = messageNum;
        this.totalMessageNum = totalMessageNum;
        this.startNum = startNum;
        this.endNum = endNum;
        this.hasPrevPage = hasPrevPage;
        this.hasNextPage = hasNextPage;
    }

    public int getPage() {
        return page;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public int getMessageNum() {
        return messageNum;
    }

    public int getTotalMessageNum() {
        return totalMessageNum;
    }

    public int getStartNum() {
        return startNum;
    }

    public int getEndNum() {
        return endNum;
    }

    public boolean isHasPrevPage() {
        return hasPrevPage;
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }
}
