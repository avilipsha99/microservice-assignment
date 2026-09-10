package com.example.order_service;

import com.example.order_service.client.NotificationClient;
import com.example.order_service.model.Order;
import com.example.order_service.model.OrderStatus;
import com.example.order_service.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @MockBean
    private NotificationClient notificationClient;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
    }

    @Test
    void createOrder_returnsCreated() throws Exception {
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerName\":\"John\",\"product\":\"Laptop\",\"quantity\":2}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerName").value("John"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void getOrder_returnsNotFound_whenOrderDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/orders/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllOrders_returnsEmptyList() throws Exception {
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void updateOrderStatus_returnsUpdatedOrder() throws Exception {
        Order order = new Order();
        order.setCustomerName("John");
        order.setProduct("Laptop");
        order.setQuantity(2);
        order.setStatus(OrderStatus.PENDING);
        Order saved = orderRepository.save(order);

        mockMvc.perform(patch("/api/orders/" + saved.getId() + "/status?status=CONFIRMED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }
}

