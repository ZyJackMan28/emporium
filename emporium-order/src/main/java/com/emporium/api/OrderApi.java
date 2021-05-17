package com.emporium.api;

import com.emporium.dto.OrderDto;
import com.emporium.pojo.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

public interface OrderApi {
    @PostMapping
    public ResponseEntity<List<Long>> createOrder(@RequestParam("seck") String seck, @RequestBody @Valid OrderDto orderDto);

}
