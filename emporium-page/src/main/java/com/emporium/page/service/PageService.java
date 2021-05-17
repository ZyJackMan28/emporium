package com.emporium.page.service;

import java.util.Map;

public interface PageService {

   Map<String, Object> loadModel(Long spuId);

   //创建静态页
   void createHtml(Long spu);

    void deleteHtml(Long spuId);
}
