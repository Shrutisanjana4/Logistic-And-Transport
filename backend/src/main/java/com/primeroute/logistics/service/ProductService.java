package com.primeroute.logistics.service;

import com.primeroute.logistics.dto.ProductDTO;
import com.primeroute.logistics.dto.ProductDemandDTO;
import com.primeroute.logistics.entity.Product;
import com.primeroute.logistics.exception.ResourceNotFoundException;
import com.primeroute.logistics.repository.OrderItemRepository;
import com.primeroute.logistics.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;

    public ProductService(ProductRepository productRepository, OrderItemRepository orderItemRepository) {
        this.productRepository = productRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> getActiveCatalog() {
        return productRepository.findByActiveTrueOrderByNameAsc()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductDTO getProductDTOById(Long id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return mapToDTO(p);
    }

    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<ProductDemandDTO> getProductDemand() {
        List<Object[]> rawList = orderItemRepository.findAggregatedProductDemand();
        List<ProductDemandDTO> result = new ArrayList<>();

        for (Object[] row : rawList) {
            Long productId = ((Number) row[0]).longValue();
            String sku = (String) row[1];
            String name = (String) row[2];
            String category = (String) row[3];
            Long totalOrders = ((Number) row[4]).longValue();
            Long requestedQty = ((Number) row[5]).longValue();
            Double totalWeight = row[6] != null ? ((Number) row[6]).doubleValue() : 0.0;

            result.add(new ProductDemandDTO(productId, sku, name, category, totalOrders, requestedQty, totalWeight));
        }

        return result;
    }

    public ProductDTO mapToDTO(Product p) {
        return new ProductDTO(
                p.getId(),
                p.getSku(),
                p.getName(),
                p.getDescription(),
                p.getCategory(),
                p.getUnitWeight(),
                p.getUnitOfMeasure(),
                p.getUnitPrice(),
                p.getReorderLevel(),
                p.getActive()
        );
    }
}
