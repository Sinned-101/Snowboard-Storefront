/*
 * File: MessageController.java
 * Description: Controller for customer and expert messaging requests
 * Author: Zach Christianson
 * Date Created: August 9, 2026
 * Last Updated: August 9, 2026
 */

package com.snowboardstorefront.controller;

import com.snowboardstorefront.dao.MessageDAO;
import com.snowboardstorefront.dao.UserDAO;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Handles requests for viewing and sending customer and expert messages
 */
@Controller
public class MessageController {

    private final MessageDAO messageDAO;
    private final UserDAO userDAO;

    public MessageController(MessageDAO messageDAO , UserDAO userDAO) {
        this.messageDAO = messageDAO;
        this.userDAO = userDAO;
    }

    /**
     * Displays the messaging page for the logged-in user
     *
     * @param session current user session
     * @param model model used to send conversation information to the page
     * @return messages page or redirect to login
     */
    @GetMapping("/messages")
    public String messagesPage(HttpSession session, Model model) {
        Object userIdObject = session.getAttribute("user_id");
        Object roleObject = session.getAttribute("role");

        if (userIdObject == null || roleObject == null) {
            return "redirect:/login";
        }

        int userId = (int) userIdObject;
        String role = roleObject.toString();

        if ("expert".equals(role)) {
            model.addAttribute("conversations", messageDAO.findConversationsByExpertId(userId));
        } else {
            model.addAttribute("conversations", messageDAO.findConversationsByCustomerId(userId));
        }

        model.addAttribute("role", role);

        return "messages";
    }

    /**
     * Displays the selected conversation and its messages for the logged-in user
     *
     * @param conversationId ID of the selected conversation
     * @param session current user session
     * @param model model used to send message information to the page
     * @return conversation details page or redirect to login
     */
    @GetMapping("/messages/{conversationId}")
    public String conversationDetails(@PathVariable int conversationId,
                                      HttpSession session,
                                      Model model) {
        Object userIdObject = session.getAttribute("user_id");

        if (userIdObject == null) {
            return "redirect:/login";
        }

        int userId = (int) userIdObject;
        String role = (String) session.getAttribute("role");

        // Admins can view any conversation - other users can only view their own
        boolean isAdmin = "admin".equals(role);
        if (!isAdmin && !messageDAO.userCanAccessConversation(userId, conversationId)) {
            return "redirect:/messages";
        }

        model.addAttribute("messages", messageDAO.findMessagesByConversationId(conversationId));
        model.addAttribute("conversationId", conversationId);
        model.addAttribute("currentUserId", userId);
        model.addAttribute("isAdmin", isAdmin);

        return "conversation-details";
    }

    /**
     * Saves a reply to the selected conversation
     *
     * @param conversationId ID of the conversation being replied to
     * @param body message text entered by the user
     * @param session current user session
     * @return selected conversation page or redirect to login
     */
    @PostMapping("/messages/reply")
    public String replyToConversation(@RequestParam int conversationId,
                                      @RequestParam String body,
                                      HttpSession session) {
        Object userIdObject = session.getAttribute("user_id");

        if (userIdObject == null) {
            return "redirect:/login";
        }

        int senderId = (int) userIdObject;
        String role = (String) session.getAttribute("role");

        // Admins can reply to any conversation
        boolean isAdmin = "admin".equals(role);
        if (!isAdmin && !messageDAO.userCanAccessConversation(senderId, conversationId)) {
            return "redirect:/messages";
        }

        if (body == null || body.isBlank()) {
            return "redirect:/messages/" + conversationId;
        }

        messageDAO.sendMessage(conversationId, senderId, body);

        return "redirect:/messages/" + conversationId;
    }

    /**
     * Starts a conversation with an expert or adds a message to an existing conversation
     *
     * @param subject subject entered by the customer
     * @param body message text entered by the customer
     * @param session current user session
     * @return selected conversation page, messages page, or redirect to login
     */
    @PostMapping("/messages/start")
    public String startConversation(@RequestParam String subject,
                                    @RequestParam String body,
                                    HttpSession session) {
        Object userIdObject = session.getAttribute("user_id");

        if (userIdObject == null) {
            return "redirect:/login";
        }

        int customerId = (int) userIdObject;
        // Assign to the expert with the fewest conversations for even distribution
        int expertId = messageDAO.findExpertWithFewestConversations();

        if (expertId == 0 || subject.isBlank() || body.isBlank()) {
            return "redirect:/messages";
        }

        int conversationId = messageDAO.findConversationIdByCustomerAndExpert(customerId, expertId);

        if (conversationId == 0) {
            conversationId = messageDAO.createConversation(customerId, expertId, subject);
        }

        if (conversationId == 0) {
            return "redirect:/messages";
        }

        messageDAO.sendMessage(conversationId, customerId, body);

        return "redirect:/messages/" + conversationId;
    }
}
