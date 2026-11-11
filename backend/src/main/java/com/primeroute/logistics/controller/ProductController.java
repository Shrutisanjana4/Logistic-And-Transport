package com.primeroute.logistics.controller;

import com.primeroute.logistics.dto.ApiResponse;
import com.primeroute.logistics.dto.ProductDTO;
import com.primeroute.logistics.dto.ProductDemandDTO;
import com.primeroute.logistics.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Public / Customer catalog
    @GetMapping("/api/products")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getActiveCatalog() {
        List<ProductDTO> catalog = productService.getActiveCatalog();
        return ResponseEntity.ok(new ApiResponse<>(true, "Active product catalog retrieved successfully", catalog));
    }

    @GetMapping("/api/products/{id}")
    public ResponseEntity<ApiResponse<ProductDTO>> getProduct(@PathVariable Long id) {
        ProductDTO dto = productService.getProductDTOById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Product retrieved successfully", dto));
    }

    // Admin catalog & demand endpoints
    @GetMapping("/api/admin/products")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getAllProductsForAdmin() {
        List<ProductDTO> all = productService.getAllProducts();
        return ResponseEntity.ok(new ApiResponse<>(true, "Admin product catalog retrieved successfully", all));
    }

    @GetMapping("/api/admin/products/demand")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<ProductDemandDTO>>> getCustomerDemand() {
        List<ProductDemandDTO> demand = productService.getProductDemand();
        return ResponseEntity.ok(new ApiResponse<>(true, "Customer product demand retrieved successfully", demand));
    }
}
