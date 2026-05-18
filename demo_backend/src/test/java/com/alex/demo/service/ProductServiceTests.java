package com.alex.demo.service;

import com.alex.demo.config.ValidationException;
import com.alex.demo.model.Product;
import com.alex.demo.model.ProductCreateDTO;
import com.alex.demo.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductServiceTests {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

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
    void testGetProducts() {
        List<Product> products = List.of(new Product(), new Product());
        when(productRepository.findAll()).thenReturn(products);
        List<Product> result = productService.getProducts();
        assertEquals(2, result.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testAddProduct() throws ValidationException {
        ProductCreateDTO productDTO = new ProductCreateDTO();
        productDTO.setName("Laptop");
        productDTO.setPrice(1500.0);

        Product savedProduct = new Product();
        savedProduct.setId(UUID.randomUUID());
        savedProduct.setName("Laptop");
        savedProduct.setPrice(1500.0);

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
        Product result = productService.addProduct(productDTO);

        assertNotNull(result.getId());
        assertEquals("Laptop", result.getName());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testUpdateProduct() throws ValidationException {
        UUID uuid = UUID.randomUUID();
        Product existingProduct = new Product();
        existingProduct.setId(uuid);
        existingProduct.setName("Laptop");
        existingProduct.setPrice(1500.0);

        Product updateData = new Product();
        updateData.setName("Gaming Laptop");
        updateData.setPrice(2000.0);

        when(productRepository.findById(uuid)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Product result = productService.updateProduct(uuid, updateData);

        assertEquals("Gaming Laptop", result.getName());
        assertEquals(2000.0, result.getPrice());
        verify(productRepository, times(1)).findById(uuid);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testUpdateProductNotFound() {
        UUID uuid = UUID.randomUUID();
        when(productRepository.findById(uuid)).thenReturn(Optional.empty());
        assertThrows(ValidationException.class, () -> productService.updateProduct(uuid, new Product()));
    }

    @Test
    void testDeleteProduct() {
        UUID uuid = UUID.randomUUID();
        doNothing().when(productRepository).deleteById(uuid);
        productService.deleteProduct(uuid);
        verify(productRepository, times(1)).deleteById(uuid);
    }
}