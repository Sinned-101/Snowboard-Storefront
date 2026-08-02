/*
 * File: CheckoutController.java
 * Description: Controller for displaying checkout pages and creating customer orders
 * Author: Zach Christianson
 * Date Created: July 26, 2026
 * Last Updated: July 26, 2026
 */

package com.snowboardstorefront.controller;

import com.snowboardstorefront.dao.CartDAO;
import com.snowboardstorefront.dao.OrderDAO;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import com.snowboardstorefront.dao.ProfileDAO;

/**
 * Handles requests for reviewing the cart and placing customer orders
 */
@Controller
public class CheckoutController {

    private final CartDAO cartDAO;
    private final OrderDAO orderDAO;
    private final ProfileDAO profileDAO;

    public CheckoutController(CartDAO cartDAO, OrderDAO orderDAO, ProfileDAO profileDAO) {
        this.cartDAO = cartDAO;
        this.orderDAO = orderDAO;
        this.profileDAO = profileDAO;
    }

    /**
     * Displays the checkout page for the logged-in user
     *
     * @param session current user session
     * @param model model used to send cart information to the checkout page
     * @return checkout page or redirect to log-in
     */
    @GetMapping("/checkout")
    public String checkoutPage(HttpSession session, Model model) {
        Object userIdObject = session.getAttribute("user_id");

        if (userIdObject == null) {
            return "redirect:/login";
        }

        int userId = (int) userIdObject;

        model.addAttribute("profile", profileDAO.findByUserId(userId));
        model.addAttribute("cartItems", cartDAO.findCartItemsByUserId(userId));
        model.addAttribute("cartTotal", cartDAO.calculateCartTotal(userId));

        return "checkout";
    }

    /**
     * Places an order using the logged-in user's cart
     *
     * @param session current user session
     * @param model model used to send confirmation information to the page
     * @return order confirmation page or redirect to cart
     */
    @PostMapping("/checkout/place-order")
    public String placeOrder(HttpSession session, Model model) {
        Object userIdObject = session.getAttribute("user_id");

        if (userIdObject == null) {
            return "redirect:/login";
        }

        int userId = (int) userIdObject;

        // Creates the order from the user's current cart
        int orderId = orderDAO.placeOrder(userId);

        if (orderId == 0) {
            return "redirect:/cart";
        }

        model.addAttribute("orderId", orderId);

        return "order-confirmation";
    }
}
