package com.primeroute.logistics.repository;

import com.primeroute.logistics.entity.Order;
import com.primeroute.logistics.entity.OrderItem;
import com.primeroute.logistics.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrder(Order order);
    List<OrderItem> findByProduct(Product product);

    @Query("SELECT oi.product.id, oi.product.sku, oi.product.name, oi.product.category, COUNT(DISTINCT oi.order.id), SUM(oi.quantity), SUM(oi.totalWeight) " +
           "FROM OrderItem oi " +
           "GROUP BY oi.product.id, oi.product.sku, oi.product.name, oi.product.category " +
           "ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> findAggregatedProductDemand();
}
