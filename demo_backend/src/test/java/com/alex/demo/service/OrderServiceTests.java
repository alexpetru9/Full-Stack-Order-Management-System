package com.alex.demo.service;

import com.alex.demo.config.ValidationException;
import com.alex.demo.model.Order;
import com.alex.demo.model.OrderCreateDTO;
import com.alex.demo.model.Person;
import com.alex.demo.repository.OrderRepository;
import com.alex.demo.repository.PersonRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OrderServiceTests {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private OrderService orderService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void testGetOrders() {
        List<Order> orders = List.of(new Order(), new Order());
        when(orderRepository.findAll()).thenReturn(orders);
        List<Order> result = orderService.getOrders();
        assertEquals(2, result.size());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void testAddOrder() throws ValidationException {
        UUID personId = UUID.randomUUID();
        OrderCreateDTO orderDTO = new OrderCreateDTO();
        orderDTO.setPersonId(personId);
        orderDTO.setOrderDate(LocalDate.now());

        Person fakePerson = new Person();
        fakePerson.setId(personId);

        Order savedOrder = new Order();
        savedOrder.setId(UUID.randomUUID());
        savedOrder.setOrderDate(orderDTO.getOrderDate());
        savedOrder.setPerson(fakePerson);

        when(personRepository.findById(personId)).thenReturn(Optional.of(fakePerson));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        Order result = orderService.addOrder(orderDTO);

        assertNotNull(result);
        assertEquals(savedOrder.getId(), result.getId());
        verify(personRepository, times(1)).findById(personId);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testUpdateOrder() throws ValidationException {
        UUID uuid = UUID.randomUUID();
        Order existingOrder = new Order();
        existingOrder.setId(uuid);
        existingOrder.setOrderDate(LocalDate.now().minusDays(1));

        Order updateData = new Order();
        updateData.setOrderDate(LocalDate.now());

        when(orderRepository.findById(uuid)).thenReturn(Optional.of(existingOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order result = orderService.updateOrder(uuid, updateData);

        assertEquals(LocalDate.now(), result.getOrderDate());
        verify(orderRepository, times(1)).findById(uuid);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testUpdateOrderNotFound() {
        UUID uuid = UUID.randomUUID();
        when(orderRepository.findById(uuid)).thenReturn(Optional.empty());
        assertThrows(ValidationException.class, () -> orderService.updateOrder(uuid, new Order()));
    }

    @Test
    void testDeleteOrder() {
        UUID uuid = UUID.randomUUID();
        doNothing().when(orderRepository).deleteById(uuid);
        orderService.deleteOrder(uuid);
        verify(orderRepository, times(1)).deleteById(uuid);
    }
}