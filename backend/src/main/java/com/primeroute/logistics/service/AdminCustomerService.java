package com.primeroute.logistics.service;

import com.primeroute.logistics.dto.*;
import com.primeroute.logistics.entity.*;
import com.primeroute.logistics.exception.ResourceNotFoundException;
import com.primeroute.logistics.repository.AddressRepository;
import com.primeroute.logistics.repository.OrderRepository;
import com.primeroute.logistics.repository.ShipmentRepository;
import com.primeroute.logistics.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminCustomerService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ShipmentRepository shipmentRepository;
    private final AddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminCustomerService(UserRepository userRepository,
                                OrderRepository orderRepository,
                                ShipmentRepository shipmentRepository,
                                AddressRepository addressRepository,
                                PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.shipmentRepository = shipmentRepository;
        this.addressRepository = addressRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<AdminCustomerDTO> getAllCustomers() {
        List<User> customers = userRepository.findByRoleOrderByCreatedAtDesc(Role.CUSTOMER);
        return customers.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AdminCustomerDetailDTO getCustomerById(Long id) {
        User customer = userRepository.findByIdAndRole(id, Role.CUSTOMER)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        AdminCustomerDetailDTO detail = new AdminCustomerDetailDTO();
        detail.setId(customer.getId());
        detail.setFullName(customer.getFullName());
        detail.setEmail(customer.getEmail());
        detail.setPhone(customer.getPhone() != null ? customer.getPhone() : "");
        detail.setCompanyName(customer.getCompanyName() != null ? customer.getCompanyName() : "");
        detail.setStatus(customer.getStatus() != null ? customer.getStatus() : "ACTIVE");
        detail.setCreatedAt(customer.getCreatedAt());
        detail.setLastPasswordChange(customer.getLastPasswordChange());

        long ordersCount = orderRepository.countByCustomer(customer);
        detail.setTotalOrders(ordersCount);

        Double spent = orderRepository.sumTotalSpentByCustomer(customer);
        detail.setTotalSpent(spent != null ? spent : 0.0);

        List<Shipment> shipments = shipmentRepository.findByOrder_Customer(customer);
        detail.setTotalShipments(shipments.size());

        long delivered = shipments.stream()
                .filter(s -> s.getStatus() == OrderStatus.DELIVERED)
                .count();
        detail.setDeliveredShipments(delivered);

        long active = shipments.stream()
                .filter(s -> s.getStatus() != OrderStatus.DELIVERED && s.getStatus() != OrderStatus.CANCELLED)
                .count();
        detail.setActiveShipments(active);

        // Real addresses
        List<Address> addresses = addressRepository.findByCustomer(customer);
        List<AddressResponse> addressResponses = addresses.stream()
                .map(this::convertAddressToResponse)
                .collect(Collectors.toList());
        detail.setAddresses(addressResponses);

        // Recent orders
        List<Order> recentOrders = orderRepository.findTop10ByCustomerOrderByCreatedAtDesc(customer);
        List<CustomerRecentOrderDTO> orderDTOs = recentOrders.stream()
                .map(this::convertOrderToRecentDTO)
                .collect(Collectors.toList());
        detail.setRecentOrders(orderDTOs);

        return detail;
    }

    public AdminCustomerDTO createCustomer(CreateCustomerRequest request) {
        String cleanEmail = request.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(cleanEmail)) {
            throw new IllegalArgumentException("A customer account with email '" + cleanEmail + "' already exists.");
        }

        User user = new User();
        user.setFullName(request.getFullName().trim());
        user.setEmail(cleanEmail);
        user.setPhone(request.getPhone().trim());
        user.setCompanyName(request.getCompanyName() != null && !request.getCompanyName().isBlank()
                ? request.getCompanyName().trim() : null);
        user.setRole(Role.CUSTOMER);
        user.setStatus("ACTIVE");
        user.setPassword(passwordEncoder.encode("Welcome@PrimeRoute2026"));

        User saved = userRepository.save(user);
        return convertToDTO(saved);
    }

    public AdminCustomerDTO updateCustomer(Long id, UpdateCustomerRequest request) {
        User customer = userRepository.findByIdAndRole(id, Role.CUSTOMER)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        String cleanEmail = request.getEmail().trim().toLowerCase();
        if (!customer.getEmail().equalsIgnoreCase(cleanEmail)) {
            if (userRepository.existsByEmail(cleanEmail)) {
                throw new IllegalArgumentException("Email '" + cleanEmail + "' is already in use by another account.");
            }
            customer.setEmail(cleanEmail);
        }

        customer.setFullName(request.getFullName().trim());
        customer.setPhone(request.getPhone().trim());
        customer.setCompanyName(request.getCompanyName() != null && !request.getCompanyName().isBlank()
                ? request.getCompanyName().trim() : null);

        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            customer.setStatus(request.getStatus().trim().toUpperCase());
        }

        User saved = userRepository.save(customer);
        return convertToDTO(saved);
    }

    public void deactivateCustomer(Long id) {
        User customer = userRepository.findByIdAndRole(id, Role.CUSTOMER)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        customer.setStatus("INACTIVE");
        userRepository.save(customer);
    }

    public AdminCustomerDTO toggleCustomerStatus(Long id) {
        User customer = userRepository.findByIdAndRole(id, Role.CUSTOMER)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        String current = customer.getStatus() != null ? customer.getStatus() : "ACTIVE";
        String next = "ACTIVE".equalsIgnoreCase(current) ? "INACTIVE" : "ACTIVE";
        customer.setStatus(next);
        User saved = userRepository.save(customer);
        return convertToDTO(saved);
    }

    private AdminCustomerDTO convertToDTO(User user) {
        long orders = orderRepository.countByCustomer(user);
        long shipments = shipmentRepository.countByOrder_Customer(user);
        Double spent = orderRepository.sumTotalSpentByCustomer(user);

        return new AdminCustomerDTO(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone() != null ? user.getPhone() : "",
                user.getCompanyName() != null ? user.getCompanyName() : "",
                user.getStatus() != null ? user.getStatus() : "ACTIVE",
                user.getCreatedAt(),
                orders,
                shipments,
                spent != null ? spent : 0.0
        );
    }

    private AddressResponse convertAddressToResponse(Address a) {
        AddressResponse r = new AddressResponse();
        r.setId(a.getId());
        r.setAddressType(a.getAddressType());
        r.setContactName(a.getContactName());
        r.setPhone(a.getPhone());
        r.setAddressLine1(a.getAddressLine1());
        r.setAddressLine2(a.getAddressLine2());
        r.setCity(a.getCity());
        r.setState(a.getState());
        r.setPostalCode(a.getPostalCode());
        r.setCountry(a.getCountry());
        r.setDefaultAddress(a.isDefaultAddress());
        r.setLatitude(a.getLatitude());
        r.setLongitude(a.getLongitude());
        return r;
    }

    private CustomerRecentOrderDTO convertOrderToRecentDTO(Order o) {
        String pickupCity = o.getPickupAddress() != null ? o.getPickupAddress().getCity() : "";
        String destCity = o.getDeliveryAddress() != null ? o.getDeliveryAddress().getCity() : "";
        return new CustomerRecentOrderDTO(
                o.getId(),
                o.getOrderNumber(),
                o.getStatus() != null ? o.getStatus().name() : "PENDING",
                o.getTotalAmount(),
                o.getCreatedAt(),
                pickupCity,
                destCity,
                o.getPackageDescription()
        );
    }
}
