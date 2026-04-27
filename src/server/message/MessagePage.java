package server.message;

import java.util.ArrayList;

public class MessagePage {
    private final ArrayList<Message> messages;
    private final int totalCount;
    private final int page;
    private final int pageSize;

    public MessagePage(ArrayList<Message> messages, int totalCount, int page, int pageSize) {
        this.messages = messages;
        this.totalCount = totalCount;
        this.page = page;
        this.pageSize = pageSize;
    }

    public ArrayList<Message> getMessages() { return messages; }
    public int getTotalCount() { return totalCount; }
    public int getPage() { return page; }
    public int getPageSize() { return pageSize; }
    public int getTotalPages() { return (int) Math.ceil((double) totalCount / pageSize); }
    public boolean hasNext() { return (page + 1) * pageSize < totalCount; }
    public boolean hasPrev() { return page > 0; }
}