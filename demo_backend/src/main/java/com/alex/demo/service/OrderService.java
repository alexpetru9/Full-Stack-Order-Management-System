package com.alex.demo.service;

import com.alex.demo.config.ValidationException;
import com.alex.demo.model.Order;
import com.alex.demo.model.OrderCreateDTO;
import com.alex.demo.model.Person;
import com.alex.demo.repository.OrderRepository;
import com.alex.demo.repository.PersonRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final PersonRepository personRepository;

    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    public Order addOrder(OrderCreateDTO orderDTO) throws ValidationException {
        /// ////////////////////////////////
        /// edge cases - bad updates prevention for 1:M
        /// we check if the person actually exists in the database before assigning the order
        Person existingPerson = personRepository.findById(orderDTO.getPersonId())
                .orElseThrow(() -> new ValidationException("The person with id " + orderDTO.getPersonId() + " does not exist. Cannot create order."));
        /// ////////////////////////////////

        Order order = new Order();

        order.setOrderDate(orderDTO.getOrderDate());
        order.setPerson(existingPerson);

        return orderRepository.save(order);
    }

    public Order updateOrder(UUID uuid, Order order) throws ValidationException {
        Optional<Order> orderOptional = orderRepository.findById(uuid);

        if(orderOptional.isEmpty()) {
            throw new ValidationException("Order with id " + uuid + " not found");
        }
        Order existingOrder = orderOptional.get();

        existingOrder.setOrderDate(order.getOrderDate());

        // If the update includes a person change, we update it
        if (order.getPerson() != null) {
            existingOrder.setPerson(order.getPerson());
        }

        return orderRepository.save(existingOrder);
    }

    public Order updateOrder2(UUID uuid, Order order) throws ValidationException {
        return orderRepository
                .findById(uuid)
                .map(existingOrder -> {
                    existingOrder.setOrderDate(order.getOrderDate());
                    if (order.getPerson() != null) {
                        existingOrder.setPerson(order.getPerson());
                    }
                    return orderRepository.save(existingOrder);
                })
                .orElseThrow(
                        () -> new ValidationException("Order with id " + uuid + " not found")
                );
    }

    public void deleteOrder(UUID uuid) {
        orderRepository.deleteById(uuid);
    }

    public Order getOrderById(UUID uuid) {
        return orderRepository.findById(uuid).orElseThrow(
                () -> new IllegalStateException("Order with id " + uuid + " not found"));
    }

    /// ///////////////////
    public Order patchOrder(UUID uuid, Order orderUpdates) throws ValidationException {
        Order existingOrder = orderRepository.findById(uuid)
                .orElseThrow(() -> new ValidationException("Order with id " + uuid + " not found"));

        if (orderUpdates.getOrderDate() != null) {
            existingOrder.setOrderDate(orderUpdates.getOrderDate());
        }
        if (orderUpdates.getPerson() != null) {
            existingOrder.setPerson(orderUpdates.getPerson());
        }

        return orderRepository.save(existingOrder);
    }
    /// /////////////////
}