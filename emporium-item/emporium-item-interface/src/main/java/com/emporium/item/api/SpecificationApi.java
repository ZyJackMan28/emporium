package com.emporium.item.api;

import com.emporium.item.pojo.SpecGroup;
import com.emporium.item.pojo.SpecParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface SpecificationApi {
    @GetMapping("spec/params")
    List<SpecParam> queryParam(@RequestParam(value = "gid", required = false) Long gid
            , @RequestParam(value = "cid",required = false) Long cid, @RequestParam(value = "searching",required = false) Boolean searching);

    @GetMapping("spec/group")
    List<SpecGroup> queryGroupAndSpecParamsByCid(@RequestParam("cid") Long cid);
}
