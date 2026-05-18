package com.alex.demo.controller;

import com.alex.demo.model.Order;
import com.alex.demo.model.Person;
import com.alex.demo.repository.OrderRepository;
import com.alex.demo.repository.PersonRepository;
import com.alex.demo.util.JwtUtil;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class OrderControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String FIXTURE_PATH = "src/test/resources/fixtures/";
    private String authToken;
    private Person testBuyer;

    @BeforeEach
    void setUp() throws Exception {
        orderRepository.deleteAll();
        personRepository.deleteAll();

        Person p = new Person();
        p.setName("Admin Buyer");
        p.setEmail("admin_test@test.com");
        p.setAge(25);
        p.setPassword("StrongPass1!");
        p.setRole("ADMIN");
        this.testBuyer = personRepository.save(p);

        this.authToken = jwtUtil.createToken(this.testBuyer);

        seedDatabase(this.testBuyer);
    }

    private void seedDatabase(Person buyer) throws Exception {
        String seedDataJson = loadFixture("order_seed.json");
        List<Order> orders = objectMapper.readValue(seedDataJson, new TypeReference<>() {});

        for (Order o : orders) {
            o.setPerson(buyer);
        }
        orderRepository.saveAll(orders);
    }

    @Test
    void testGetOrders() throws Exception {
        mockMvc.perform(get("/order")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].orderDate", Matchers.containsInAnyOrder("2024-03-15", "2024-04-20")));
    }

    @Test
    void testAddOrder_ValidPayload() throws Exception {
        String validOrderJson = loadFixture("valid_order.json");
        validOrderJson = validOrderJson.replace("123e4567-e89b-12d3-a456-426614174000", testBuyer.getId().toString());

        mockMvc.perform(post("/order")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validOrderJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void testAddOrder_InvalidPayload() throws Exception {
        String invalidOrderJson = loadFixture("invalid_order.json");

        mockMvc.perform(post("/order")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidOrderJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.orderDate").value("Order date is required!"))
                .andExpect(jsonPath("$.personId").value("The order must be linked to a user (Person ID is required)!"));
    }

    private String loadFixture(String fileName) throws IOException {
        return Files.readString(Paths.get(FIXTURE_PATH + fileName));
    }
}