package server.message;

import database.SDataBase;
import database.SDataBaseSection;

import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class MessageDataBase {
    private SDataBase dataBase;
    private SDataBaseSection senders;
    private SDataBaseSection recipients;

    public MessageDataBase(String dataBaseId) {
        dataBase = new SDataBase(Paths.get("").toAbsolutePath() +"/db", dataBaseId);
        senders = dataBase.getSection("senders");
        if (senders.isEmpty()) {
            dataBase.put("senders", new HashMap<>());
            senders = dataBase.getSection("senders");
        }
        recipients = dataBase.getSection("recipients");
        if (recipients.isEmpty()) {
            dataBase.put("recipients", new HashMap<>());
            recipients = dataBase.getSection("recipients");
        }
    }

    public void addMessage(Message message) {
        HashMap<String, Object> messageMap = new HashMap<>();
        messageMap.put("text", message.message);
        messageMap.put("sender", message.sender);
        messageMap.put("recipient", message.recipient);
        messageMap.put("sendTime", message.sendTime);

        Map<Object, Object> senderMap = senders.getMap(message.sender);
        if (senderMap == null) {
            senderMap = new HashMap<>();
        }
        senderMap.put(message.sendTime, messageMap);
        senders.put(message.sender, senderMap);

        Map<Object, Object> recipientMap = recipients.getMap(message.recipient);
        if (recipientMap == null) {
            recipientMap = new HashMap<>();
        }
        recipientMap.put(message.sendTime, messageMap);
        recipients.put(message.recipient, recipientMap);

        dataBase.saveQuietly();
    }

    public MessagePage getMessagesPage(String user, int page, int pageSize) {
        TreeMap<Long, Message> allMessages = new TreeMap<>(Collections.reverseOrder());

        Map<Object, Object> asSender = senders.getMap(user);
        if (asSender != null) {
            for (Object value : asSender.values()) {
                if (value instanceof HashMap) {
                    Message msg = messageFromMap((HashMap<?, ?>) value);
                    if (msg != null) {
                        allMessages.put(msg.sendTime, msg);
                    }
                }
            }
        }

        Map<Object, Object> asRecipient = recipients.getMap(user);
        if (asRecipient != null) {
            for (Object value : asRecipient.values()) {
                if (value instanceof HashMap) {
                    Message msg = messageFromMap((HashMap<?, ?>) value);
                    if (msg != null) {
                        allMessages.put(msg.sendTime, msg);
                    }
                }
            }
        }

        List<Message> messageList = new ArrayList<>(allMessages.values());
        int totalCount = messageList.size();
        int start = page * pageSize;
        int end = Math.min(start + pageSize, totalCount);

        ArrayList<Message> pageMessages = new ArrayList<>();
        if (start < totalCount) {
            pageMessages.addAll(messageList.subList(start, end));
        }

        return new MessagePage(pageMessages, totalCount, page, pageSize);
    }

    public MessagePage getConversationPage(String user1, String user2, int page, int pageSize) {
        TreeMap<Long, Message> sortedMessages = new TreeMap<>(Collections.reverseOrder());

        Map<Object, Object> user1Sent = senders.getMap(user1);
        if (user1Sent != null) {
            for (Object value : user1Sent.values()) {
                if (value instanceof HashMap) {
                    Message msg = messageFromMap((HashMap<?, ?>) value);
                    if (msg != null && msg.recipient.equals(user2)) {
                        sortedMessages.put(msg.sendTime, msg);
                    }
                }
            }
        }

        Map<Object, Object> user2Sent = senders.getMap(user2);
        if (user2Sent != null) {
            for (Object value : user2Sent.values()) {
                if (value instanceof HashMap) {
                    Message msg = messageFromMap((HashMap<?, ?>) value);
                    if (msg != null && msg.recipient.equals(user1)) {
                        sortedMessages.put(msg.sendTime, msg);
                    }
                }
            }
        }

        List<Message> messageList = new ArrayList<>(sortedMessages.values());
        int totalCount = messageList.size();
        int start = page * pageSize;
        int end = Math.min(start + pageSize, totalCount);

        ArrayList<Message> pageMessages = new ArrayList<>();
        if (start < totalCount) {
            pageMessages.addAll(messageList.subList(start, end));
        }

        return new MessagePage(pageMessages, totalCount, page, pageSize);
    }

    public int getMessageCount(String user) {
        int count = 0;
        Map<Object, Object> asSender = senders.getMap(user);
        if (asSender != null) count += asSender.size();
        Map<Object, Object> asRecipient = recipients.getMap(user);
        if (asRecipient != null) count += asRecipient.size();
        return count;
    }

    public ArrayList<Message> getLastMessages(String user, int count) {
        MessagePage page = getMessagesPage(user, 0, count);
        return page.getMessages();
    }

    public ArrayList<Message> getConversation(String user1, String user2, int limit) {
        MessagePage page = getConversationPage(user1, user2, 0, limit);
        return page.getMessages();
    }

    public void save() {
        dataBase.saveQuietly();
    }

    private Message messageFromMap(HashMap<?, ?> map) {
        try {
            String text = (String) map.get("text");
            String sender = (String) map.get("sender");
            String recipient = (String) map.get("recipient");
            long sendTime = (Long) map.get("sendTime");
            return new Message(text, sender, recipient, sendTime);
        } catch (Exception e) {
            return null;
        }
    }
}