package com.mini.ecommerceapp.services.implementations;

import com.mini.ecommerceapp.dto.OrderDTO;
import com.mini.ecommerceapp.exceptions.ResourceNotAvailableException;
import com.mini.ecommerceapp.exceptions.ResourceNotFoundException;
import com.mini.ecommerceapp.models.Order;
import com.mini.ecommerceapp.models.Status;
import com.mini.ecommerceapp.models.VehicularSpace;
import com.mini.ecommerceapp.repository.OrderRepository;
import com.mini.ecommerceapp.services.OrderService;
import com.mini.ecommerceapp.services.VehicularSpaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import static com.mini.ecommerceapp.utils.Constants.ORDER_NOT_FOUND;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final VehicularSpaceService vehicularSpaceService;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, VehicularSpaceService vehicularSpaceService) {
        this.orderRepository = orderRepository;
        this.vehicularSpaceService = vehicularSpaceService;
    }

    @Override
    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> getOrdersForUser(String username) {
        return orderRepository.findByUserId(username);
    }

    @Override
    public Order getOrder(long id) {
        return orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(ORDER_NOT_FOUND));
    }

    @Override
    public Order addOrder(OrderDTO order, String username) {
        VehicularSpace space = vehicularSpaceService.getVehicularSpace(order.getVehicularSpace().getId());
        long count = orderRepository.countOrdersByItems_IdAndStartLessThanEqualAndStartLessThanAndStatus(space.getId(), order.getStartTimeStamp(), order.getEndTimeStamp(), Status.CONFIRMED);
        if (space.getTotalSlots() == count) {
            throw new ResourceNotAvailableException("Space Booked full");
        }
        order.setVehicularSpace(space);
        return orderRepository.save(new Order(username, order));
    }

    @Override
    public Map<Long, Long> getOrderCount(LocalDateTime startTime, LocalDateTime endTime) {
        return orderRepository.countOrdersMap(startTime, endTime, -1);
    }

    @Override
    public Map<Long, Long> getOrderCountForParkingSpace(LocalDateTime startTime, LocalDateTime endTime, long id) {
        return orderRepository.countOrdersMap(startTime, endTime, id);
    }

    @Override
    public Order updateStatus(long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(ORDER_NOT_FOUND));
        LocalDateTime dateTime = order.getExpiry();
        LocalDateTime now = LocalDateTime.now();
        long diff = ChronoUnit.MINUTES.between(dateTime, now);
        if (now.isAfter(dateTime) && diff > 5) {
            double extra = diff * order.getItems().getPrice() / 60;
            order.setExtraCharges(extra);
            order.setFinalCharge(order.getFinalCharge() + extra);
        }
        order.setEndTime(now);
        order.setStatus(Status.EXPIRED);
        return orderRepository.save(order);
    }
}
