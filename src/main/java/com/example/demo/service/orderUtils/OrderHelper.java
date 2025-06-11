package com.example.demo.service.orderUtils;

import com.example.demo.model.BuyerInfo;
import com.example.demo.model.Order;
import com.example.demo.model.PaymentInfo;
import com.example.demo.model.Product;
import com.example.demo.model.ProductQuantity;
import com.example.demo.model.enums.OrderStatus;
import com.example.demo.model.enums.PaymentStatus;
import com.example.demo.paymentGateway.PaymentGatewayService;
import com.example.demo.repository.ProductRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class OrderHelper {


    public Order initializeOrder(String seatLetter, int seatNumber) {
        Order order = new Order();
        BuyerInfo buyerInfo = new BuyerInfo();
        buyerInfo.setSeatLetter(seatLetter);
        buyerInfo.setSeatNumber(seatNumber);
        order.setBuyerInfo(buyerInfo);
        order.setStatus(OrderStatus.OPEN);
        order.setTotalPrice(BigDecimal.ZERO);
        order.setProductQuantities(new ArrayList<>());
        return order;
    }

    public void updateOrderStatus(Order order, OrderStatus status) {
        order.setStatus(status);
    }


    public BigDecimal calculateTotalPriceAndUpdateStock(List<ProductQuantity> productQuantities, ProductRepository productRepo) {
        BigDecimal total = BigDecimal.ZERO;
        if (productQuantities != null && !productQuantities.isEmpty()) {
            for (ProductQuantity productQuantity : productQuantities) {
                Product product = fetchAndValidateProduct(productQuantity, productRepo);
                total = total.add(product.getPrice().multiply(BigDecimal.valueOf(productQuantity.getQuantity())));
                updateProductStock(product, productQuantity.getQuantity(), productRepo);
            }
        }
        return total;
    }

    private Product fetchAndValidateProduct(ProductQuantity productQuantity, ProductRepository productRepo) {
        Product product = productRepo.findById(productQuantity.getProductId())
                .orElseThrow(() -> new IllegalStateException("Product not found for ID: " + productQuantity.getProductId()));

        if (product.getStock() < productQuantity.getQuantity()) {
            throw new IllegalStateException("Insufficient stock for product ID: " + productQuantity.getProductId());
        }

        return product;
    }

    private void updateProductStock(Product product, Integer quantity, ProductRepository productRepo) {
        product.setStock(product.getStock() - quantity);
        productRepo.save(product);
    }


    public void updateOrderDetails(Order order, String buyerEmail, BigDecimal total, List<ProductQuantity> productQuantityList) {
        order.getBuyerInfo().setBuyerEmail(buyerEmail);
        BuyerInfo buyerInfo = order.getBuyerInfo();
        if (buyerInfo == null) {
            buyerInfo = new BuyerInfo();
            order.setBuyerInfo(buyerInfo);
        }
        buyerInfo.setBuyerEmail(buyerEmail);
        if (productQuantityList != null)
            order.getProductQuantities().addAll(productQuantityList);
        order.setTotalPrice(total);
    }

    public void processPayment(Order order, String token, String gateway, PaymentGatewayService paymentGateway) {
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setCardToken(token);
        paymentInfo.setPaymentGateway(gateway);
        PaymentStatus result = paymentGateway.processPayment(gateway);
        paymentInfo.setPaymentStatus(result);
        paymentInfo.setPaymentDate(LocalDateTime.now());
        order.setPaymentInfo(paymentInfo);
    }
}