/*
 * File: AdminOrderController.java
 * Description: Controller for admin order management - view all delivery orders and mark as shipped
 * Author: Dennis Feldbruegge
 * Date Created: August 16, 2026
 * Last Updated: August 16, 2026
 */

package com.snowboardstorefront.controller;

import com.snowboardstorefront.dao.OrderDAO;
import com.snowboardstorefront.dao.ProductDAO;
import com.snowboardstorefront.dao.UserDAO;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

// Handles admin order management - listing, shipping, cancelling, and creating orders for customers
@Controller
public class AdminOrderController {

    private final OrderDAO orderDAO;
    private final UserDAO userDAO;
    private final ProductDAO productDAO;

    // Spring injects all three DAOs through the constructor
    public AdminOrderController(OrderDAO orderDAO, UserDAO userDAO, ProductDAO productDAO) {
        this.orderDAO = orderDAO;
        this.userDAO = userDAO;
        this.productDAO = productDAO;
    }

    // Shows all delivery orders across all customers
    @GetMapping("/admin/orders")
    public String adminOrdersPage(HttpSession session, Model model) {

        // Only admins can access this page
        String role = (String) session.getAttribute("role");
        if (!"admin".equals(role)) {
            return "redirect:/login";
        }

        // Fetch all delivery orders and pass them to the template
        model.addAttribute("orders", orderDAO.findAllDeliveryOrders());

        return "admin-orders";
    }

    // Marks a pending delivery order as shipped and records the tracking number
    @PostMapping("/admin/orders/{orderId}/ship")
    public String markOrderShipped(
            HttpSession session,
            @PathVariable int orderId,
            @RequestParam String trackingNumber) {

        // Only admins can mark orders as shipped
        String role = (String) session.getAttribute("role");
        if (!"admin".equals(role)) {
            return "redirect:/login";
        }

        orderDAO.markOrderShipped(orderId, trackingNumber);

        // Redirect back to the order management page after shipping
        return "redirect:/admin/orders";
    }

    // Marks a pending in-store pickup order as collected by the customer
    @PostMapping("/admin/orders/{orderId}/pickup")
    public String markOrderPickedUp(
            HttpSession session,
            @PathVariable int orderId) {

        // Only admins can mark orders as picked up
        String role = (String) session.getAttribute("role");
        if (!"admin".equals(role)) {
            return "redirect:/login";
        }

        orderDAO.markOrderPickedUp(orderId);

        return "redirect:/admin/orders";
    }

    // Cancels a pending delivery or pickup order - shipped and in-store orders cannot be cancelled
    @PostMapping("/admin/orders/{orderId}/cancel")
    public String cancelOrder(
            HttpSession session,
            @PathVariable int orderId) {

        String role = (String) session.getAttribute("role");
        if (!"admin".equals(role)) {
            return "redirect:/login";
        }

        orderDAO.cancelOrder(orderId);

        return "redirect:/admin/orders";
    }

    // Updates the tracking number on a shipped delivery order without changing its status
    @PostMapping("/admin/orders/{orderId}/tracking")
    public String updateTrackingNumber(
            HttpSession session,
            @PathVariable int orderId,
            @RequestParam String trackingNumber) {

        String role = (String) session.getAttribute("role");
        if (!"admin".equals(role)) {
            return "redirect:/login";
        }

        orderDAO.updateTrackingNumber(orderId, trackingNumber);

        return "redirect:/admin/orders";
    }

    // Reverses a completed pickup order back to pending so it becomes active and editable again
    @PostMapping("/admin/orders/{orderId}/unpickup")
    public String reverseOrderPickup(
            HttpSession session,
            @PathVariable int orderId) {

        String role = (String) session.getAttribute("role");
        if (!"admin".equals(role)) {
            return "redirect:/login";
        }

        orderDAO.reverseOrderPickup(orderId);

        return "redirect:/admin/orders";
    }

    // Reverses a shipped delivery order back to pending so it becomes active and editable again
    @PostMapping("/admin/orders/{orderId}/unship")
    public String reverseOrderShipment(
            HttpSession session,
            @PathVariable int orderId) {

        String role = (String) session.getAttribute("role");
        if (!"admin".equals(role)) {
            return "redirect:/login";
        }

        orderDAO.reverseOrderShipment(orderId);

        return "redirect:/admin/orders";
    }

    // Shows the form for creating a new order on behalf of a customer
    @GetMapping("/admin/orders/create")
    public String showCreateOrderForm(HttpSession session, Model model) {

        String role = (String) session.getAttribute("role");
        if (!"admin".equals(role)) {
            return "redirect:/login";
        }

        // Load all customers and products to populate the form dropdowns and product table
        model.addAttribute("customers", userDAO.findAllCustomers());
        model.addAttribute("products", productDAO.findAllProducts());

        return "admin-order-create";
    }

    // Receives the create order form and places the order for the selected customer
    @PostMapping("/admin/orders/create")
    public String createOrderForCustomer(
            HttpSession session,
            @RequestParam int customerId,
            @RequestParam String channel,
            @RequestParam(required = false, name = "shipping_address") String shippingAddress,
            @RequestParam Map<String, String> allParams) {

        String role = (String) session.getAttribute("role");
        if (!"admin".equals(role)) {
            return "redirect:/login";
        }

        // Extract product quantities from form params that start with "qty_"
        // For example qty_3 = 2 means 2 units of product ID 3
        Map<Integer, Integer> productQuantities = new HashMap<>();
        for (Map.Entry<String, String> param : allParams.entrySet()) {
            if (param.getKey().startsWith("qty_")) {
                int productId = Integer.parseInt(param.getKey().substring(4));
                int quantity = Integer.parseInt(param.getValue());
                if (quantity > 0) {
                    productQuantities.put(productId, quantity);
                }
            }
        }

        // Redirect back to the form if no products were selected
        if (productQuantities.isEmpty()) {
            return "redirect:/admin/orders/create";
        }

        orderDAO.createOrderForCustomer(customerId, channel, shippingAddress, productQuantities);

        return "redirect:/admin/orders";
    }
}
