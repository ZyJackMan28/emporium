package com.emporium.page.serviceimpl;

import com.emporium.page.service.PageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
@RunWith(SpringRunner.class)
@SpringBootTest
public class PageServiceImplTest {

    @Autowired
    private PageService pageService;
    @Test
    public void createHtml() {
        pageService.createHtml(141L);
    }
}