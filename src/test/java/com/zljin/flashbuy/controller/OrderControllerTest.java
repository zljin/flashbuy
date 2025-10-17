package com.zljin.flashbuy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zljin.flashbuy.model.dto.CreateOrderDTO;
import com.zljin.flashbuy.model.vo.OrderVO;
import com.zljin.flashbuy.service.OrderInfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OrderInfoService orderInfoService;

    @InjectMocks
    private OrderController orderController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void createOrder_Success() throws Exception {
        // Given
        CreateOrderDTO createOrderDTO = new CreateOrderDTO();
        createOrderDTO.setItemId("1234");
        createOrderDTO.setPromoId("12");
        createOrderDTO.setAmount(2);

        OrderVO orderVO = new OrderVO();
        orderVO.setId("ORDER_123");

        when(orderInfoService.createOrder(anyString(), anyString(), anyInt()))
                .thenReturn(orderVO);

        // When & Then
        mockMvc.perform(post("/order/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOrderDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value("ORDER_123"));
    }

}