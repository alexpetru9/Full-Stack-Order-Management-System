package com.alex.demo.controller;

import com.alex.demo.config.ValidationException;
import com.alex.demo.model.Order;
import com.alex.demo.model.OrderCreateDTO;
import com.alex.demo.service.OrderService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@CrossOrigin
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/order")
    public List<Order> getOrders() {
        return orderService.getOrders();
    }

    @GetMapping("/order/{uuid}")
    public Order getOrderById(@PathVariable UUID uuid) {
        return orderService.getOrderById(uuid);
    }

    @PostMapping("/order")
    public Order addOrder(
            @Valid @RequestBody OrderCreateDTO orderDTO
    ) throws ValidationException {
        return orderService.addOrder(orderDTO);
    }

    @PutMapping("/order/{uuid}")
    public Order updateOrder(@PathVariable UUID uuid,
                             @RequestBody Order order)
            throws ValidationException {
        return orderService.updateOrder(uuid, order);
    }

    @DeleteMapping("/order/{uuid}")
    public void deleteOrder(@PathVariable UUID uuid) {
        orderService.deleteOrder(uuid);
    }

    /// ///////////////////////////
    @PatchMapping("/order/{uuid}")
    public Order patchOrder(@PathVariable UUID uuid, @RequestBody Order orderUpdates) throws ValidationException {
        return orderService.patchOrder(uuid, orderUpdates);
    }
}