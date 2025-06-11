package com.example.demo.controller;



import com.example.demo.model.Order;
import com.example.demo.model.ProductQuantity;
import com.example.demo.service.OrderServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderServiceImpl orderServiceImpl;

    public OrderController(OrderServiceImpl orderServiceImpl) {
        this.orderServiceImpl = orderServiceImpl;
    }

    @PostMapping
    public ResponseEntity<Order> create(@RequestParam String seatLetter,
                                        @RequestParam int seatNumber,
                                        @RequestParam(required = false) String email,
                                        @RequestBody(required = false) List<ProductQuantity> productQuantityList) {
        Order order = orderServiceImpl.createOrder(seatLetter, seatNumber,email, productQuantityList);
        return ResponseEntity.status(201).body(order);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Order> update(@PathVariable Long id,
                                        @RequestParam(required = false) String email,
                                        @RequestBody(required = false) List<ProductQuantity> productQuantityList) {
        Optional<Order> updatedOrder = orderServiceImpl.updateOrder(id, email, productQuantityList);
        return updatedOrder.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Order> cancel(@PathVariable Long id) {
        return orderServiceImpl.cancelOrder(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/finish")
    public ResponseEntity<Order> finish(@PathVariable Long id,
                                        @RequestParam String token,
                                        @RequestParam String gateway) {
        Optional<Order> orderOpt = orderServiceImpl.finishOrder(id, token, gateway);
        return orderOpt.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}
