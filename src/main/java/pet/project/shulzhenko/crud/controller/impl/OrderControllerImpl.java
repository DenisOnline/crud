package pet.project.shulzhenko.crud.controller.impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pet.project.shulzhenko.crud.controller.OrderApi;
import pet.project.shulzhenko.crud.dto.request.OrderDtoRequest;
import pet.project.shulzhenko.crud.dto.request.UpdateStatusDtoRequest;
import pet.project.shulzhenko.crud.dto.response.OrderDtoResponse;
import pet.project.shulzhenko.crud.security.CustomUserDetails;
import pet.project.shulzhenko.crud.service.OrderService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderControllerImpl implements OrderApi {

    private final OrderService orderService;

    @Override
    @PostMapping
    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    public ResponseEntity<OrderDtoResponse> creatingOrder(
            @Valid @RequestBody OrderDtoRequest orderDto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        OrderDtoResponse creatingOrder = orderService.createOrder(orderDto, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(creatingOrder);
    }

    @Override
    @GetMapping
    public ResponseEntity<Page<OrderDtoResponse>> userOrdersList (
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @ParameterObject Pageable pageable
    ) {
        Page<OrderDtoResponse> orderDtoList = orderService.userOrdersList(userDetails.user().getId(), pageable);
        return ResponseEntity.ok(orderDtoList);
    }

    @Override
    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<Page<OrderDtoResponse>> getAllOrders(@ParameterObject Pageable pageable) {
        Page<OrderDtoResponse> allOrders = orderService.getAllOrders(pageable);
        return ResponseEntity.ok(allOrders);
    }

    @Override
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<OrderDtoResponse> updateStatusOrder(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateStatusDtoRequest status
    ) {
        OrderDtoResponse orderDto = orderService.updateStatusOrder(id, status);
        return ResponseEntity.ok(orderDto);
    }

    @Override
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN') or @orderService.isOrderOwner(#id, principal)")
    public ResponseEntity<Void> deleteOrder(@PathVariable UUID id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}