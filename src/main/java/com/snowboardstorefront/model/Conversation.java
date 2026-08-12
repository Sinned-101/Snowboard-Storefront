/*
 * File: Conversation.java
 * Description: Model class for customer and expert conversations
 * Author: Zach Christianson
 * Date Created: August 6, 2026
 * Last Updated: August 6, 2026
 */

package com.snowboardstorefront.model;

import java.sql.Timestamp;

/**
 * Represents a conversation between a customer and an expert
 */
public class Conversation {

    // Unique ID for the conversation
    private int conversationId;

    // Customer user ID connected to the conversation
    private int customerId;

    // Expert user ID connected to the conversation
    private int expertId;

    // Subject or topic of the conversation
    private String subject;

    // Date and time the conversation was created
    private Timestamp createdAt;

    // Date and time the conversation was last updated
    private Timestamp updatedAt;

    public Conversation() {
    }

    /**
     * Creates a conversation object with customer and expert conversation information
     *
     * @param conversationId unique conversation ID
     * @param customerId customer user ID connected to the conversation
     * @param expertId expert user ID connected to the conversation
     * @param subject subject or topic of the conversation
     * @param createdAt date and time the conversation was created
     * @param updatedAt date and time the conversation was last updated
     */
    public Conversation(int conversationId, int customerId, int expertId,
                        String subject, Timestamp createdAt, Timestamp updatedAt) {
        this.conversationId = conversationId;
        this.customerId = customerId;
        this.expertId = expertId;
        this.subject = subject;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getConversationId() {
        return conversationId;
    }

    public void setConversationId(int conversationId) {
        this.conversationId = conversationId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getExpertId() {
        return expertId;
    }

    public void setExpertId(int expertId) {
        this.expertId = expertId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
