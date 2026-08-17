/*
 * File: Message.java
 * Description: Model class for messages sent between customers and experts
 * Author: Zach Christianson
 * Date Created: August 6, 2026
 * Last Updated: August 6, 2026
 */

package com.snowboardstorefront.model;

import java.sql.Timestamp;

/**
 * Represents one message inside a customer and expert conversation
 */
public class Message {

    // Unique ID for the message
    private int messageId;

    // Conversation ID connected to the message
    private int conversationId;

    // User ID of the person who sent the message
    private int senderId;

    // Text content of the message
    private String body;

    // Tracks whether the message has been read
    private boolean read;

    // Date and time the message was sent
    private Timestamp sentAt;

    public Message() {
    }

    /**
     * Creates a message object for a conversation
     *
     * @param messageId unique message ID
     * @param conversationId conversation ID connected to the message
     * @param senderId user ID of the person who sent the message
     * @param body text content of the message
     * @param read whether the message has been read
     * @param sentAt date and time the message was sent
     */
    public Message(int messageId, int conversationId, int senderId,
                   String body, boolean read, Timestamp sentAt) {
        this.messageId = messageId;
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.body = body;
        this.read = read;
        this.sentAt = sentAt;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getConversationId() {
        return conversationId;
    }

    public void setConversationId(int conversationId) {
        this.conversationId = conversationId;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public Timestamp getSentAt() {
        return sentAt;
    }

    public void setSentAt(Timestamp sentAt) {
        this.sentAt = sentAt;
    }

    // Sender username - populated via JOIN when fetching messages for display
    private String senderUsername;

    public String getSenderUsername() { return senderUsername; }
    public void setSenderUsername(String senderUsername) { this.senderUsername = senderUsername; }
}
