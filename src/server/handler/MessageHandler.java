package server.handler;

import network.Packet;
import network.protocol.*;
import server.Server;
import server.message.Message;
import server.message.MessageDataBase;
import server.message.MessagePage;

public class MessageHandler {
    private final Server server;
    private final MessageDataBase messageDB;

    public MessageHandler(Server server) {
        this.server = server;
        this.messageDB = new MessageDataBase("messages");
    }

    public void handleMessageSend(Packet packet, String ip, int port) {
        MessageSendPacket msgPacket = new MessageSendPacket(packet);
        String recipient = msgPacket.getRecipient();
        String messageText = msgPacket.getMessage();

        String sender = server.getSessionManager().findUsernameByAddress(ip, port);
        if (sender == null) {
            System.out.println("[ERROR] Unknown sender: " + ip + ":" + port);
            return;
        }

        if (sender.equals(recipient)) {
            System.out.println("[ERROR] " + sender + " tried to send message to self");
            return;
        }

        Message message = new Message(messageText, sender, recipient);
        messageDB.addMessage(message);

        System.out.println("[MESSAGE] " + sender + " -> " + recipient + ": " + messageText);

        if (server.getSessionManager().isOnline(recipient)) {
            MessagePacket response = new MessagePacket(message);
            server.getSessionManager().sendToUser(recipient, response.getPacket());
        }

        MessagePacket confirmation = new MessagePacket(message);
        server.getPeer().send(confirmation.getPacket(), ip, port);
    }

    public void handleMessageHistoryRequest(Packet packet, String ip, int port) {
        MessageHistoryRequestPacket request = new MessageHistoryRequestPacket(packet);
        String username = request.getUsername();
        int page = request.getPage();
        int pageSize = request.getPageSize();
        String conversationWith = request.getConversationWith();

        if (pageSize <= 0 || pageSize > 200) pageSize = 50;
        if (page < 0) page = 0;

        MessagePage messagePage;
        if (conversationWith != null && !conversationWith.isEmpty()) {
            messagePage = messageDB.getConversationPage(username, conversationWith, page, pageSize);
        } else {
            messagePage = messageDB.getMessagesPage(username, page, pageSize);
        }

        boolean hasMore = messagePage.hasNext();
        MessageChunkPacket chunkPacket = new MessageChunkPacket(
                messagePage.getMessages(),
                messagePage.getTotalCount(),
                page,
                hasMore
        );

        server.getPeer().send(chunkPacket.getPacket(), ip, port);
        System.out.println("[HISTORY] " + username + " requested page " + page + " of " +
                messagePage.getTotalPages() + " (" + messagePage.getMessages().size() + " messages)");
    }

    public void handleMessagePoolRequest(Packet packet, String ip, int port) {
        MessagePoolRequestPacket req = new MessagePoolRequestPacket(packet);
        String username = req.getUsername();

        MessagePage page = messageDB.getMessagesPage(username, 0, 50);
        MessageChunkPacket chunkPacket = new MessageChunkPacket(
                page.getMessages(),
                page.getTotalCount(),
                0,
                page.hasNext()
        );
        server.getPeer().send(chunkPacket.getPacket(), ip, port);
        System.out.println("[HISTORY] " + username + " requested " + page.getMessages().size() + " messages");
    }
}