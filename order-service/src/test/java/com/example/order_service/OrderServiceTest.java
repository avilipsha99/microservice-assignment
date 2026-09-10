package com.example.order_service;

import com.example.order_service.service.OrderService;

import com.example.order_service.client.NotificationClient;
import com.example.order_service.dto.OrderRequest;
import com.example.order_service.exception.OrderNotFoundException;
import com.example.order_service.model.Order;
import com.example.order_service.model.OrderStatus;
import com.example.order_service.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private NotificationClient notificationClient;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_savesAndReturnsOrder() {
        OrderRequest request = new OrderRequest();
        request.setCustomerName("John");
        request.setProduct("Laptop");
        request.setQuantity(2);

        Order savedOrder = new Order();
        savedOrder.setCustomerName("John");
        savedOrder.setProduct("Laptop");
        savedOrder.setQuantity(2);

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        Order result = orderService.createOrder(request);

        assertNotNull(result);
        assertEquals("John", result.getCustomerName());
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(notificationClient, times(1)).notifyOrderCreated(any());
    }

    @Test
    void getOrderById_returnsOrder_whenExists() {
        Order order = new Order();
        order.setCustomerName("John");

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Order result = orderService.getOrderById(1L);

        assertNotNull(result);
        assertEquals("John", result.getCustomerName());
    }

    @Test
    void getOrderById_throwsException_whenNotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.getOrderById(99L));
    }

    @Test
    void updateOrderStatus_updatesAndReturnsOrder() {
        Order order = new Order();
        order.setCustomerName("John");

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.updateOrderStatus(1L, OrderStatus.CONFIRMED);

        assertEquals(OrderStatus.CONFIRMED, result.getStatus());
        verify(orderRepository, times(1)).save(order);
    }
}

