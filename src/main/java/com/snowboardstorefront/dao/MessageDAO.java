/*
 * File: MessageDAO.java
 * Description: Data access class for customer and expert conversations and messages
 * Author: Zach Christianson
 * Date Created: August 9, 2026
 * Last Updated: August 9, 2026
 */

package com.snowboardstorefront.dao;

import com.snowboardstorefront.model.Conversation;
import com.snowboardstorefront.model.Message;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Handles database operations for conversations and messages
 */
@Repository
public class MessageDAO {

    private final JdbcTemplate jdbcTemplate;

    public MessageDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Gets all conversations for a customer
     *
     * @param customerId logged-in customer's user ID
     * @return list of the customer's conversations
     */
    public List<Conversation> findConversationsByCustomerId(int customerId) {
        String sql = """
                SELECT conversation_id, customer_id, expert_id, subject, created_at, updated_at
                FROM conversation
                WHERE customer_id = ?
                ORDER BY updated_at DESC
                """;

        return jdbcTemplate.query(sql, (resultSet, rowNum) -> new Conversation(
                resultSet.getInt("conversation_id"),
                resultSet.getInt("customer_id"),
                resultSet.getInt("expert_id"),
                resultSet.getString("subject"),
                resultSet.getTimestamp("created_at"),
                resultSet.getTimestamp("updated_at")
        ), customerId);
    }

    /**
     * Gets all conversations assigned to an expert
     *
     * @param expertId logged-in expert's user ID
     * @return list of the expert's conversations
     */
    public List<Conversation> findConversationsByExpertId(int expertId) {
        String sql = """
                SELECT conversation_id, customer_id, expert_id, subject, created_at, updated_at
                FROM conversation
                WHERE expert_id = ?
                ORDER BY updated_at DESC
                """;

        return jdbcTemplate.query(sql, (resultSet, rowNum) -> new Conversation(
                resultSet.getInt("conversation_id"),
                resultSet.getInt("customer_id"),
                resultSet.getInt("expert_id"),
                resultSet.getString("subject"),
                resultSet.getTimestamp("created_at"),
                resultSet.getTimestamp("updated_at")
        ), expertId);
    }

    /**
     * Gets all messages in a selected conversation
     *
     * @param conversationId conversation ID selected by the user
     * @return list of messages in the conversation
     */
    public List<Message> findMessagesByConversationId(int conversationId) {
        String sql = """
                SELECT message_id, conversation_id, sender_id, body, is_read, sent_at
                FROM message
                WHERE conversation_id = ?
                ORDER BY sent_at
                """;

        return jdbcTemplate.query(sql, (resultSet, rowNum) -> new Message(
                resultSet.getInt("message_id"),
                resultSet.getInt("conversation_id"),
                resultSet.getInt("sender_id"),
                resultSet.getString("body"),
                resultSet.getBoolean("is_read"),
                resultSet.getTimestamp("sent_at")
        ), conversationId);
    }

    /**
     * Finds an existing conversation between a customer and expert
     *
     * @param customerId customer user ID
     * @param expertId expert user ID
     * @return conversation ID, or 0 if no conversation is found
     */
    public int findConversationIdByCustomerAndExpert(int customerId, int expertId) {
        String sql = """
            SELECT conversation_id
            FROM conversation
            WHERE customer_id = ? AND expert_id = ?
            LIMIT 1
            """;

        try {
            Integer conversationId = jdbcTemplate.queryForObject(sql, Integer.class, customerId, expertId);
            return conversationId == null ? 0 : conversationId;
        } catch (Exception exception) {
            return 0;
        }
    }

    /**
     * Checks whether a user is part of a selected conversation
     *
     * @param userId logged-in user's ID
     * @param conversationId selected conversation ID
     * @return true if the user is part of the conversation, otherwise false
     */
    public boolean userCanAccessConversation(int userId, int conversationId) {
        String sql = """
            SELECT COUNT(*)
            FROM conversation
            WHERE conversation_id = ?
              AND (customer_id = ? OR expert_id = ?)
            """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, conversationId, userId, userId);

        return count != null && count > 0;
    }

    /**
     * Creates a new conversation between a customer and an expert
     *
     * @param customerId customer user ID
     * @param expertId expert user ID
     * @param subject subject entered by the customer
     * @return newly created conversation ID
     */
    public int createConversation(int customerId, int expertId, String subject) {
        String sql = """
            INSERT INTO conversation (customer_id, expert_id, subject)
            VALUES (?, ?, ?)
            """;

        jdbcTemplate.update(sql, customerId, expertId, subject);

        Integer conversationId = jdbcTemplate.queryForObject(
                "SELECT LAST_INSERT_ID()",
                Integer.class
        );

        return conversationId == null ? 0 : conversationId;
    }

    /**
     * Adds a new message to a conversation
     *
     * @param conversationId conversation ID connected to the message
     * @param senderId user ID of the person sending the message
     * @param body message text entered by the sender
     */
    public void sendMessage(int conversationId, int senderId, String body) {
        String sql = """
            INSERT INTO message (conversation_id, sender_id, body)
            VALUES (?, ?, ?)
            """;

        jdbcTemplate.update(sql, conversationId, senderId, body);

        updateConversationTime(conversationId);
    }

    /**
     * Counts all conversations in the database
     *
     * @return total number of conversations
     */
    public int countAllConversations() {
        String sql = """
            SELECT COUNT(*)
            FROM conversation
            """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);

        return count == null ? 0 : count;
    }

    /**
     * Counts all messages in the database
     *
     * @return total number of messages
     */
    public int countAllMessages() {
        String sql = """
            SELECT COUNT(*)
            FROM message
            """;

        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);

        return count == null ? 0 : count;
    }

    /**
     * Updates the conversation timestamp after a new message is sent
     *
     * @param conversationId conversation ID to update
     */
    public void updateConversationTime(int conversationId) {
        String sql = """
            UPDATE conversation
            SET updated_at = CURRENT_TIMESTAMP
            WHERE conversation_id = ?
            """;

        jdbcTemplate.update(sql, conversationId);
    }
}
