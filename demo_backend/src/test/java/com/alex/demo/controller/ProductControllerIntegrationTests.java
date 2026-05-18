package com.alex.demo.controller;

import com.alex.demo.model.Product;
import com.alex.demo.model.Person;
import com.alex.demo.repository.ProductRepository;
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
public class ProductControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private String authToken;

    private static final String FIXTURE_PATH = "src/test/resources/fixtures/";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws Exception {
        productRepository.deleteAll();
        personRepository.deleteAll();

        Person admin = new Person();
        admin.setName("Admin");
        admin.setEmail("admin@test.com");
        admin.setRole("ADMIN");
        admin.setPassword("Password123!");
        admin.setAge(30);
        personRepository.save(admin);

        authToken = jwtUtil.createToken(admin);

        productRepository.flush();
        seedDatabase();
    }

    private void seedDatabase() throws Exception {
        String seedDataJson = loadFixture("product_seed.json");
        List<Product> products = objectMapper.readValue(seedDataJson, new TypeReference<>() {});
        productRepository.saveAll(products);
    }

    @Test
    void testGetProducts() throws Exception {
        mockMvc.perform(get("/product")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].name", Matchers.containsInAnyOrder("Laptop", "Mouse")))
                .andExpect(jsonPath("$[*].price", Matchers.containsInAnyOrder(1500.0, 50.0)));
    }

    @Test
    void testAddProduct_ValidPayload() throws Exception {
        String validProductJson = loadFixture("valid_product.json");

        mockMvc.perform(post("/product")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validProductJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Monitor"))
                .andExpect(jsonPath("$.price").value(300.0));
    }

    @Test
    void testAddProduct_InvalidPayload() throws Exception {
        String invalidProductJson = loadFixture("invalid_product.json");

        mockMvc.perform(post("/product")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidProductJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Product name cannot be empty!"))
                .andExpect(jsonPath("$.price").value("Product price is required!"));
    }

    private String loadFixture(String fileName) throws IOException {
        return Files.readString(Paths.get(FIXTURE_PATH + fileName));
    }
}