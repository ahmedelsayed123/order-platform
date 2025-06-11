package com.example.demo.service;

import com.example.demo.model.Order;
import com.example.demo.model.ProductQuantity;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing orders.
 */
public interface OrderService {

    /**
     * Creates a new order with the specified seat details.
     *
     * @param seatLetter        the letter of the seat
     * @param seatNumber        the number of the seat
     * @param buyerEmail        the email of the buyer
     * @param productQuantities the list of products and their quantities to update in the order
     * @return the created order
     */
    Order createOrder(String seatLetter, int seatNumber, String buyerEmail, List<ProductQuantity> productQuantities);

    /**
     * Cancels an order by its ID.
     *
     * @param orderId the ID of the order to cancel
     * @return an {@link Optional} containing the canceled order, or empty if the order was not found
     */
    Optional<Order> cancelOrder(Long orderId);

    /**
     * Updates an order with new buyer details and product information.
     *
     * @param orderId           the ID of the order to update
     * @param buyerEmail        the email of the buyer
     * @param productQuantities the list of products and their quantities to update in the order
     * @return an {@link Optional} containing the updated order, or empty if the order was not found
     */
    Optional<Order> updateOrder(Long orderId, String buyerEmail, List<ProductQuantity> productQuantities);

    /**
     * Finalizes an order by processing payment and updating its status.
     *
     * @param orderId the ID of the order to finalize
     * @param token   the payment token
     * @param gateway the payment gateway
     * @return an {@link Optional} containing the finalized order, or empty if the order was not found
     */
    Optional<Order> finishOrder(Long orderId, String token, String gateway);
}