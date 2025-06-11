package com.example.demo.service.orderUtils;

import com.example.demo.model.BuyerInfo;
import com.example.demo.model.Order;

import com.example.demo.model.Product;
import com.example.demo.model.ProductQuantity;
import com.example.demo.model.enums.OrderStatus;
import com.example.demo.model.enums.PaymentStatus;
import com.example.demo.paymentGateway.PaymentGatewayService;
import com.example.demo.repository.ProductQuantityRepo;
import com.example.demo.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderHelperTest {

    @Mock
    private ProductRepository productRepo;


    @Mock
    private PaymentGatewayService paymentGateway;

    private OrderHelper orderHelper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderHelper = new OrderHelper();
    }

    @Test
    void testInitializeOrder() {
        Order order = orderHelper.initializeOrder("A", 1);

        assertNotNull(order);
        assertEquals(OrderStatus.OPEN, order.getStatus());
        assertEquals(BigDecimal.ZERO, order.getTotalPrice());
        assertNotNull(order.getBuyerInfo());
        assertEquals("A", order.getBuyerInfo().getSeatLetter());
        assertEquals(1, order.getBuyerInfo().getSeatNumber());
    }

    @Test
    void testUpdateOrderStatus() {
        Order order = new Order();
        order.setStatus(OrderStatus.OPEN);

        orderHelper.updateOrderStatus(order, OrderStatus.CANCELED);

        assertEquals(OrderStatus.CANCELED, order.getStatus());
    }

    @Test
    void testCalculateTotalPriceAndUpdateStock() {
        Product product = createProduct(1L, "Product A", BigDecimal.valueOf(50), 10);
        ProductQuantity productQuantity = createProductQuantity(1L, 2);
        List<ProductQuantity> productQuantities = List.of(productQuantity);

        when(productRepo.findById(1L)).thenReturn(java.util.Optional.of(product));
        doAnswer(invocation -> {
            Product p = invocation.getArgument(0);
            assertEquals(8, p.getStock()); // Ensure stock is updated correctly
            return null;
        }).when(productRepo).save(product);

        BigDecimal total = orderHelper.calculateTotalPriceAndUpdateStock(productQuantities, productRepo);

        assertEquals(BigDecimal.valueOf(100), total);
        assertEquals(8, product.getStock());
        verify(productRepo, times(1)).findById(1L);
        verify(productRepo, times(1)).save(product);
    }

    Product createProduct(Long id, String name, BigDecimal price, int stock) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(price);
        product.setStock(stock);
        return product;
    }

    ProductQuantity createProductQuantity(Long productId, int quantity) {
        ProductQuantity productQuantity = new ProductQuantity();
        productQuantity.setProductId(productId);
        productQuantity.setQuantity(quantity);
        return productQuantity;
    }

    @Test
    void testCalculateTotalPriceAndUpdateStock_InsufficientStock() {
        Product product = createProduct(1L, "Product A", BigDecimal.valueOf(50), 1);
        ProductQuantity productQuantity = createProductQuantity(1L, 2);
        List<ProductQuantity> productQuantities = List.of(productQuantity);

        when(productRepo.findById(1L)).thenReturn(java.util.Optional.of(product));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            orderHelper.calculateTotalPriceAndUpdateStock(productQuantities, productRepo);
        });

        assertEquals("Insufficient stock for product ID: 1", exception.getMessage());
        verify(productRepo, times(1)).findById(1L);
        verify(productRepo, never()).save(product);
    }

    @Test
    void testUpdateOrderDetails() {
        Order order = new Order();
        BuyerInfo buyerInfo = new BuyerInfo();
        order.setBuyerInfo(buyerInfo);
        order.setProductQuantities(new ArrayList<ProductQuantity>());
        List<ProductQuantity> productQuantities = List.of(createProductQuantity(1L, 2));
        BigDecimal total = BigDecimal.valueOf(100);

        orderHelper.updateOrderDetails(order, "buyer@example.com", total, productQuantities);

        assertEquals("buyer@example.com", order.getBuyerInfo().getBuyerEmail());
        assertEquals(productQuantities, order.getProductQuantities());
        assertEquals(total, order.getTotalPrice());
    }

    @Test
    void testProcessPayment() {
        Order order = new Order();
        when(paymentGateway.processPayment("gateway")).thenReturn(PaymentStatus.PAID);

        orderHelper.processPayment(order, "token", "gateway", paymentGateway);

        assertNotNull(order.getPaymentInfo());
        assertEquals("token", order.getPaymentInfo().getCardToken());
        assertEquals("gateway", order.getPaymentInfo().getPaymentGateway());
        assertEquals(PaymentStatus.PAID, order.getPaymentInfo().getPaymentStatus());
        assertNotNull(order.getPaymentInfo().getPaymentDate());
        verify(paymentGateway, times(1)).processPayment("gateway");
    }
}