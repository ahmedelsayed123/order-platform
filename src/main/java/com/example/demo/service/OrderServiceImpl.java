package com.example.demo.service;

import com.example.demo.model.Order;
import com.example.demo.model.Product;
import com.example.demo.model.ProductQuantity;
import com.example.demo.model.enums.OrderStatus;
import com.example.demo.paymentGateway.PaymentGatewayService;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.orderUtils.OrderHelper;
import jakarta.persistence.OptimisticLockException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepo;
    private final ProductRepository productRepo;
    private final PaymentGatewayService paymentGateway;
    private final OrderHelper orderHelper;

    public OrderServiceImpl(OrderRepository orderRepo, ProductRepository productRepo, PaymentGatewayService paymentGateway, OrderHelper orderHelper) {
        this.orderRepo = orderRepo;
        this.productRepo = productRepo;
        this.paymentGateway = paymentGateway;
        this.orderHelper = orderHelper;
    }

    @Override
    public Order createOrder(String seatLetter, int seatNumber, String buyerEmail ,List<ProductQuantity> productQuantities) {
        Order order = orderHelper.initializeOrder(seatLetter, seatNumber);

        BigDecimal total = orderHelper.calculateTotalPriceAndUpdateStock(productQuantities, productRepo);

        orderHelper.updateOrderDetails(order, buyerEmail, total, productQuantities);
        return orderRepo.save(order);
    }

    @Override
    @Transactional
    public Optional<Order> cancelOrder(Long orderId) {
        Optional<Order> optionalOrder = orderRepo.findById(orderId);
        if (optionalOrder.isEmpty()) {
            return Optional.empty();
        }

        Order order = optionalOrder.get();
        orderHelper.updateOrderStatus(order, OrderStatus.CANCELED);

        // Restore stock for associated products
        for (ProductQuantity productQuantity : order.getProductQuantities()) {
            Product product = productRepo.findById(productQuantity.getProductId())
                    .orElseThrow(() -> new IllegalStateException("Product not found for ID: " + productQuantity.getProductId()));
            product.setStock(product.getStock() + productQuantity.getQuantity());
            productRepo.save(product);
        }

        return Optional.of(orderRepo.save(order));
    }

    @Override
    @Transactional
    @Retryable(
            retryFor = OptimisticLockException.class,
            backoff = @Backoff(delay = 300)
    )
    public Optional<Order> updateOrder(Long orderId, String buyerEmail, List<ProductQuantity> productQuantityList) {
        Optional<Order> optionalOrder = orderRepo.findById(orderId);
        if (optionalOrder.isEmpty()) {
            return Optional.empty();
        }

        Order order = optionalOrder.get();

        BigDecimal total = orderHelper.calculateTotalPriceAndUpdateStock(productQuantityList, productRepo);


        orderHelper.updateOrderDetails(order, buyerEmail, total, productQuantityList);

        return Optional.of(orderRepo.save(order));
    }

    @Override
    @Transactional
    public Optional<Order> finishOrder(Long orderId, String token, String gateway) {
        Optional<Order> optionalOrder = orderRepo.findById(orderId);
        if (optionalOrder.isEmpty()) {
            return Optional.empty();
        }

        Order order = optionalOrder.get();
        orderHelper.processPayment(order, token, gateway, paymentGateway);
        orderHelper.updateOrderStatus(order, OrderStatus.FINISHED);

        return Optional.of(orderRepo.save(order));
    }
}