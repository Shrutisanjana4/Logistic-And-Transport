package com.primeroute.logistics.repository;

import com.primeroute.logistics.entity.Faq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FaqRepository extends JpaRepository<Faq, Long> {

    List<Faq> findByIsActiveTrueOrderByDisplayOrderAsc();

    List<Faq> findByCategoryIgnoreCaseAndIsActiveTrueOrderByDisplayOrderAsc(String category);

    @Query("SELECT DISTINCT f.category FROM Faq f WHERE f.isActive = true ORDER BY f.category ASC")
    List<String> findDistinctActiveCategories();
}
