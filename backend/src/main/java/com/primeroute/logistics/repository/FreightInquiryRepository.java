package com.primeroute.logistics.repository;

import com.primeroute.logistics.entity.FreightInquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FreightInquiryRepository extends JpaRepository<FreightInquiry, Long> {

    Optional<FreightInquiry> findByInquiryNumber(String inquiryNumber);

    List<FreightInquiry> findByEmailOrderByCreatedAtDesc(String email);

    List<FreightInquiry> findAllByOrderByCreatedAtDesc();

    boolean existsByInquiryNumber(String inquiryNumber);
}
