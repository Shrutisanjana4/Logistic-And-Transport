package com.primeroute.logistics.repository;

import com.primeroute.logistics.entity.Address;
import com.primeroute.logistics.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {

    List<Address> findByCustomer(User customer);

    Optional<Address> findByIdAndCustomer(Long id, User customer);
}