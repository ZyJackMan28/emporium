package com.emporium.item.serviceimpl;

import com.emporium.common.dto.CartDto;
import com.emporium.item.service.MerchandiseService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
@RunWith(SpringRunner.class)
@SpringBootTest
public class MerchandiseServiceImplTest {

    @Autowired
    private MerchandiseService service;

    @Test
    public void decreaseStock(){

        List<CartDto> list = Arrays.asList(new CartDto(2600242L, 2), new CartDto(2868393L, 2));
        service.decreaseStock(list);

    }
}