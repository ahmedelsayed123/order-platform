package com.example.demo.controller;

import com.example.demo.model.Order;
import com.example.demo.service.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderControllerTest {

    @Mock
    private OrderServiceImpl orderServiceImpl;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreate() {
        Order order = new Order();
        when(orderServiceImpl.createOrder("A", 1, "buyer@example.com", List.of())).thenReturn(order);

        ResponseEntity<Order> result = orderController.create("A", 1, "buyer@example.com", List.of());

        assertNotNull(result);
        verify(orderServiceImpl, times(1)).createOrder("A", 1, "buyer@example.com", List.of());
    }

    @Test
    void testUpdateOrderFound() {
        Order order = new Order();
        when(orderServiceImpl.updateOrder(1L, "buyer@example.com", List.of())).thenReturn(Optional.of(order));

        ResponseEntity<Order> response = orderController.update(1L, "buyer@example.com", List.of());

        assertEquals(ResponseEntity.ok(order), response);
        verify(orderServiceImpl, times(1)).updateOrder(1L, "buyer@example.com", List.of());
    }

    @Test
    void testUpdateOrderNotFound() {
        when(orderServiceImpl.updateOrder(1L, "buyer@example.com", List.of())).thenReturn(Optional.empty());

        ResponseEntity<Order> response = orderController.update(1L, "buyer@example.com", List.of());

        assertEquals(ResponseEntity.notFound().build(), response);
        verify(orderServiceImpl, times(1)).updateOrder(1L, "buyer@example.com", List.of());
    }

    @Test
    void testCancelOrderFound() {
        Order order = new Order();
        when(orderServiceImpl.cancelOrder(1L)).thenReturn(Optional.of(order));

        ResponseEntity<Order> response = orderController.cancel(1L);

        assertEquals(ResponseEntity.ok(order), response);
        verify(orderServiceImpl, times(1)).cancelOrder(1L);
    }

    @Test
    void testCancelOrderNotFound() {
        when(orderServiceImpl.cancelOrder(1L)).thenReturn(Optional.empty());

        ResponseEntity<Order> response = orderController.cancel(1L);

        assertEquals(ResponseEntity.notFound().build(), response);
        verify(orderServiceImpl, times(1)).cancelOrder(1L);
    }

    @Test
    void testFinishOrderFound() {
        Order order = new Order();
        when(orderServiceImpl.finishOrder(1L, "token", "gateway")).thenReturn(Optional.of(order));

        ResponseEntity<Order> response = orderController.finish(1L, "token", "gateway");

        assertEquals(ResponseEntity.ok(order), response);
        verify(orderServiceImpl, times(1)).finishOrder(1L, "token", "gateway");
    }

    @Test
    void testFinishOrderNotFound() {
        when(orderServiceImpl.finishOrder(1L, "token", "gateway")).thenReturn(Optional.empty());

        ResponseEntity<Order> response = orderController.finish(1L, "token", "gateway");

        assertEquals(ResponseEntity.notFound().build(), response);
        verify(orderServiceImpl, times(1)).finishOrder(1L, "token", "gateway");
    }
}