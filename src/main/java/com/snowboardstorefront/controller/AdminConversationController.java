/*
 * File: AdminConversationController.java
 * Description: Controller for admin conversation overview page
 * Author: Dennis Feldbruegge
 * Date Created: August 17, 2026
 * Last Updated: August 17, 2026
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

// Shows all customer-expert conversations to the admin and allows reassigning experts
@Controller
public class AdminConversationController {

    private final MessageDAO messageDAO;
    private final UserDAO userDAO;

    // Spring injects MessageDAO and UserDAO through the constructor
    public AdminConversationController(MessageDAO messageDAO, UserDAO userDAO) {
        this.messageDAO = messageDAO;
        this.userDAO = userDAO;
    }

    // Shows all conversations with a dropdown to reassign the expert on each one
    @GetMapping("/admin/conversations")
    public String adminConversationsPage(HttpSession session, Model model) {

        // Only admins can access this page
        String role = (String) session.getAttribute("role");
        if (!"admin".equals(role)) {
            return "redirect:/login";
        }

        // Fetch all conversations and all experts so the reassign dropdown is populated
        model.addAttribute("conversations", messageDAO.findAllConversationsForAdmin());
        model.addAttribute("experts", userDAO.findAllExperts());

        return "admin-conversations";
    }

    // Reassigns a conversation to a different expert
    @PostMapping("/admin/conversations/{conversationId}/reassign")
    public String reassignConversation(
            HttpSession session,
            @PathVariable int conversationId,
            @RequestParam int expertId) {

        // Only admins can reassign conversations
        String role = (String) session.getAttribute("role");
        if (!"admin".equals(role)) {
            return "redirect:/login";
        }

        messageDAO.reassignConversation(conversationId, expertId);

        return "redirect:/admin/conversations";
    }
}
