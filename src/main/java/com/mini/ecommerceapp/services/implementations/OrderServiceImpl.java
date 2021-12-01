package com.mini.ecommerceapp.services.implementations;

import com.mini.ecommerceapp.controllers.RequestFormat.OrderRequest;
import com.mini.ecommerceapp.models.ClientUser;
import com.mini.ecommerceapp.models.Order;
import com.mini.ecommerceapp.models.VehicularSpace;
import com.mini.ecommerceapp.repository.OrderRepository;
import com.mini.ecommerceapp.security.securitycontext.IAuthentication;
import com.mini.ecommerceapp.services.ClientUserService;
import com.mini.ecommerceapp.services.OrderService;
import com.mini.ecommerceapp.services.VehicularSpaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    private final IAuthentication authentication;
    private final OrderRepository orderRepository;
    private final ClientUserService clientUserService;
    private final VehicularSpaceService vehicularSpaceService;

    @Autowired
    public OrderServiceImpl(IAuthentication authentication, OrderRepository orderRepository, ClientUserService clientUserService, VehicularSpaceService vehicularSpaceService) {
        this.authentication = authentication;
        this.orderRepository = orderRepository;
        this.clientUserService = clientUserService;
        this.vehicularSpaceService = vehicularSpaceService;
    }

    @Override
    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> getOrdersForUser() {
        return orderRepository.findByUser_Username(authentication.getAuthentication().getName());
    }

    @Override
    public Order getOrder(long id) {
        return orderRepository.getById(id);
    }

    @Override
    public Order addOrder(OrderRequest order) {
        ClientUser clientUser = clientUserService.getUser(authentication.getAuthentication().getName());
        VehicularSpace space = vehicularSpaceService.getVehicularSpace(order.getVehicularSpace().getId());
        space.setAvailableSlots(space.getAvailableSlots() - 1);
        order.setVehicularSpace(space);
        return orderRepository.save(new Order(clientUser, order));
    }

    @Override
    public void releaseResources(LocalDateTime dateTime) {
        List<Order> orders = orderRepository.findByStatusAndExpiry("Confirmed", dateTime);
        orders.forEach(order -> {
            VehicularSpace space = order.getItems();
            space.setAvailableSlots(space.getAvailableSlots() + 1);
            order.setStatus("Expired");
        });
    }
}