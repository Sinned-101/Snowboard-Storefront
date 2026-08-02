/*
 * File: CartController.java
 * Description: Controller for shopping cart page requests
 * Author: Zach Christianson
 * Date Created: July 23, 2026
 * Last Updated: July 23, 2026
 */

package com.snowboardstorefront.controller;

import com.snowboardstorefront.dao.CartDAO;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Handles shopping cart requests for logged-in users
 */
@Controller
public class CartController {

    private final CartDAO cartDAO;

    /**
     * Initializes the controller with cart database operations
     *
     * @param cartDAO data access object for the cart and cart_items tables
     */
    public CartController(CartDAO cartDAO) {
        this.cartDAO = cartDAO;
    }

    /**
     * Displays the shopping cart page for the logged-in user
     *
     * @param session current user session
     * @param model stores cart data for the page
     * @return the cart template, or redirects to log-in if no user is logged in
     */
    @GetMapping("/cart")
    public String cartPage(HttpSession session, Model model) {

        // Get the logged-in user's ID from the session
        Object userIdObject = session.getAttribute("user_id");

        if (userIdObject == null) {
            return "redirect:/login";
        }

        int userId = (int) userIdObject;

        // Add the user's cart items and total to the page
        model.addAttribute("cartItems", cartDAO.findCartItemsByUserId(userId));
        model.addAttribute("cartTotal", cartDAO.calculateCartTotal(userId));

        return "cart";
    }

    /**
     * Adds a selected product to the logged-in user's cart
     *
     * @param productId product ID selected from the product details page
     * @param session current user session
     * @return redirect to the cart page after the product is added
     */
    @PostMapping("/cart/add")
    public String addToCart(
            @RequestParam int productId,
            HttpSession session) {

        // Get the logged-in user's ID from the session
        Object userIdObject = session.getAttribute("user_id");

        if (userIdObject == null) {
            return "redirect:/login";
        }

        int userId = (int) userIdObject;

        // Add the selected product to the user's cart
        cartDAO.addProductToCart(userId, productId);

        return "redirect:/cart";
    }

    /**
     * Removes a selected item from the logged-in user's cart
     *
     * @param cartItemId cart item ID selected from the cart page
     * @param session current user session
     * @return redirect to the cart page after the item is removed
     */
    @PostMapping("/cart/remove")
    public String removeFromCart(
            @RequestParam int cartItemId,
            HttpSession session) {

        // Get the logged-in user's ID from the session
        Object userIdObject = session.getAttribute("user_id");

        if (userIdObject == null) {
            return "redirect:/login";
        }

        int userId = (int) userIdObject;

        // Remove the selected item from the user's cart
        cartDAO.removeCartItem(userId, cartItemId);

        return "redirect:/cart";
    }

    /**
     * Updates the quantity of a selected cart item
     *
     * @param cartItemId cart item ID selected from the cart page
     * @param quantity new item quantity
     * @param session current user session
     * @return redirect to the cart page after the quantity is updated
     */
    @PostMapping("/cart/update")
    public String updateCartQuantity(
            @RequestParam int cartItemId,
            @RequestParam int quantity,
            HttpSession session) {

        // Get the logged-in user's ID from the session
        Object userIdObject = session.getAttribute("user_id");

        if (userIdObject == null) {
            return "redirect:/login";
        }

        int userId = (int) userIdObject;

        // Updates the selected item's quantity
        cartDAO.updateCartItemQuantity(userId, cartItemId, quantity);

        return "redirect:/cart";
    }
}
