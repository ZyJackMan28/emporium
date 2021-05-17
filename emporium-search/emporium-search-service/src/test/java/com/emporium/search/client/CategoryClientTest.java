package com.emporium.search.client;

import com.emporium.item.pojo.Category;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CategoryClientTest {
    @Autowired
    private CategoryClient categoryClient;

    @Test
    public void queryCategoryByIds(){
        List<Category> categories = categoryClient.queryCategoryByIds(Arrays.asList(74L,75L));
        //断言查出2个否则报错
        Assert.assertEquals(2,categories.size());
        for (Category category : categories) {
            System.out.println("category : " + category);
        }

    }


}