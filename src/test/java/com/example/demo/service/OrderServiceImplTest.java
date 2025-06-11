package com.example.demo.service;

import com.example.demo.model.Order;
import com.example.demo.model.Product;
import com.example.demo.model.ProductQuantity;
import com.example.demo.model.enums.OrderStatus;
import com.example.demo.paymentGateway.PaymentGatewayService;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.orderUtils.OrderHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepo;

    @Mock
    private ProductRepository productRepo;

    @Mock
    private PaymentGatewayService paymentGateway;

    @Mock
    private OrderHelper orderHelper;


    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateOrder() {
        List<ProductQuantity> productQuantities = List.of(new ProductQuantity());

        Order order = new Order();
        when(orderHelper.initializeOrder("A", 1)).thenReturn(order);
        when(orderRepo.save(order)).thenReturn(order);

        Order result = orderService.createOrder("A", 1,"buyer@example.com",productQuantities);

        assertNotNull(result);
        verify(orderHelper, times(1)).initializeOrder("A", 1);
        verify(orderRepo, times(1)).save(order);
    }

    @Test
    void testCancelOrder() {
        Order order = new Order();
        order.setStatus(OrderStatus.OPEN);
        order.setProductQuantities(List.of(createProductQuantity(1L, 2)));

        when(orderRepo.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepo.save(order)).thenReturn(order);
        when(productRepo.findById(anyLong())).thenReturn(Optional.of(createProduct(1L, "Product A", BigDecimal.valueOf(50), 10)));


        // Mock the behavior of updateOrderStatus to change the status
        doAnswer(invocation -> {
            Order argOrder = invocation.getArgument(0);
            argOrder.setStatus(OrderStatus.CANCELED);
            return null;
        }).when(orderHelper).updateOrderStatus(order, OrderStatus.CANCELED);

        Optional<Order> result = orderService.cancelOrder(1L);

        assertTrue(result.isPresent());
        assertEquals(OrderStatus.CANCELED, result.get().getStatus());
        verify(orderHelper, times(1)).updateOrderStatus(order, OrderStatus.CANCELED);
        verify(orderRepo, times(1)).findById(1L);
        verify(orderRepo, times(1)).save(order);
        verify(productRepo, times(1)).findById(anyLong());
        verify(productRepo, times(1)).save(any(Product.class));
        assertEquals(12, productRepo.findById(1L).get().getStock()); // Ensure stock is restored
    }

    @Test
    void testUpdateOrder() {
        Order order = new Order();
        List<ProductQuantity> productQuantities = List.of(new ProductQuantity());
        BigDecimal total = BigDecimal.valueOf(100);

        when(orderRepo.findById(1L)).thenReturn(Optional.of(order));
        when(orderHelper.calculateTotalPriceAndUpdateStock(productQuantities, productRepo)).thenReturn(total);
        when(orderRepo.save(order)).thenReturn(order);

        Optional<Order> result = orderService.updateOrder(1L, "buyer@example.com", productQuantities);

        assertTrue(result.isPresent());
        verify(orderRepo, times(1)).findById(1L);
        verify(orderHelper, times(1)).calculateTotalPriceAndUpdateStock(productQuantities, productRepo);
        verify(orderHelper, times(1)).updateOrderDetails(order, "buyer@example.com", total, productQuantities);
        verify(orderRepo, times(1)).save(order);
    }

    @Test
    void testFinishOrder() {
        Order order = new Order();
        when(orderRepo.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepo.save(order)).thenReturn(order);

        Optional<Order> result = orderService.finishOrder(1L, "token", "gateway");

        assertTrue(result.isPresent());
        verify(orderRepo, times(1)).findById(1L);
        verify(orderHelper, times(1)).processPayment(order, "token", "gateway", paymentGateway);
        verify(orderHelper, times(1)).updateOrderStatus(order, OrderStatus.FINISHED);
        verify(orderRepo, times(1)).save(order);
    }

    @Test
    void testUpdateOrder_SuccessfulPurchase() {
        Order order = new Order();
        order.setTotalPrice(BigDecimal.valueOf(100));
        List<ProductQuantity> productQuantities = List.of(createProductQuantity(1L, 2));
        BigDecimal total = BigDecimal.valueOf(100);

        when(orderRepo.findById(1L)).thenReturn(Optional.of(order));
        when(orderHelper.calculateTotalPriceAndUpdateStock(productQuantities, productRepo)).thenReturn(total);
        when(orderRepo.save(order)).thenReturn(order);

        Optional<Order> result = orderService.updateOrder(1L, "buyer@example.com", productQuantities);

        assertTrue(result.isPresent());
        assertEquals(total, result.get().getTotalPrice());
        verify(orderRepo, times(1)).findById(1L);
        verify(orderHelper, times(1)).calculateTotalPriceAndUpdateStock(productQuantities, productRepo);
        verify(orderHelper, times(1)).updateOrderDetails(order, "buyer@example.com", total, productQuantities);
        verify(orderRepo, times(1)).save(order);
    }

    @Test
    void testUpdateOrder_InsufficientStock() {
        Order order = new Order();
        List<ProductQuantity> productQuantities = List.of(createProductQuantity(1L, 20));

        when(orderRepo.findById(1L)).thenReturn(Optional.of(order));
        when(orderHelper.calculateTotalPriceAndUpdateStock(productQuantities, productRepo))
                .thenThrow(new IllegalStateException("Insufficient stock for product ID: 1"));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            orderService.updateOrder(1L, "buyer@example.com", productQuantities);
        });

        assertEquals("Insufficient stock for product ID: 1", exception.getMessage());
        verify(orderRepo, times(1)).findById(1L);
        verify(orderHelper, times(1)).calculateTotalPriceAndUpdateStock(productQuantities, productRepo);
        verify(orderRepo, never()).save(order);
    }

    @Test
    void testUpdateOrder_InvalidProductId() {
        Order order = new Order();
        List<ProductQuantity> productQuantities = List.of(createProductQuantity(99L, 2));

        when(orderRepo.findById(1L)).thenReturn(Optional.of(order));

        when(orderHelper.calculateTotalPriceAndUpdateStock(productQuantities, productRepo))
                .thenThrow(new IllegalStateException("Product not found for ID: 99"));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            orderService.updateOrder(1L, "buyer@example.com", productQuantities);
        });

        assertEquals("Product not found for ID: 99", exception.getMessage());
        verify(orderRepo, times(1)).findById(1L);
        verify(orderHelper, times(1)).calculateTotalPriceAndUpdateStock(productQuantities, productRepo);
        verify(orderRepo, never()).save(order);
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
}