package com.emporium.item.serviceimpl;

import com.emporium.common.enums.EnumsStatus;
import com.emporium.common.exception.EpException;
import com.emporium.item.mapper.SpecGroupMapper;
import com.emporium.item.mapper.SpecParamMapper;
import com.emporium.item.pojo.SpecGroup;
import com.emporium.item.pojo.SpecParam;
import com.emporium.item.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 *  根据商品分类查询组信息
 *  根据组信息查询商品信息
 * */

@Service
public class SpecificationServiceImpl implements SpecificationService {

    @Autowired
    private SpecGroupMapper specGroupMapper;
    @Autowired
    private SpecParamMapper specParamMapper;
    @Override
    public List<SpecGroup> queryGroupInfoByCid(Long cid) {
        /*
            SELECT * FROM 'tb_spec_group' where cid = ?
            单表查询，所以可以直接使用mybatis的
            根据SpecGroup非空字段查询
        * */
        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);
        List<SpecGroup> list = specGroupMapper.select(specGroup);
        //判断
        if(CollectionUtils.isEmpty(list)){
            throw new EpException(EnumsStatus.SPEC_GROUP_IS_NOT_FOUND);
        }
        return list;
    }

   /* @Override
    public List<SpecParam> queryParamInfoByGid(Long gid) {
        *//*根据组id查询还是单表
        * *//*
        SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);
        List<SpecParam> list = specParamMapper.select(specParam);
        //判断
        if(CollectionUtils.isEmpty(list)){
            //
            throw new EpException(EnumsStatus.SPEC_PARAM_IS_NOT_FOUND);
        }
        return list;
    }*/
    /*
    * 因为根据对象的非空查询，所传的参数有可能为null,
    * */
    @Override
    public List<SpecParam> queryParam(Long gid, Long cid, Boolean searching) {
        SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);
        specParam.setCid(cid);
        specParam.setSearching(searching);
        //因为根据非空字段查询
        List<SpecParam> specParams = specParamMapper.select(specParam);
        if (CollectionUtils.isEmpty(specParams)){
            throw new EpException(EnumsStatus.SPEC_PARAM_IS_NOT_FOUND);
        }
        return specParams;
    }

    @Override
    public List<SpecGroup> queryGroupAndSpecParamsByCid(Long cid) {
        //查询规格组
        List<SpecGroup> specGroups = queryGroupInfoByCid(cid);
        //还需要查询组类参数  规格组----规格参数  看表的关系  一对多
        //一个规格组---对多个规格参数
        /*for (SpecGroup specGroup : specGroups) {
            //通过groupId查询，一次性查询所有
        }*/
        //查询当前所有组下的参数信息
        List<SpecParam> specParams = queryParam(null, cid, null);
        //最优--map(k==groupId  v=specParam)
        Map<Long,List<SpecParam>> map = new HashMap<>();
        for (SpecParam param : specParams) {
            //判断所在规格参数的组是否存在
            if(!map.containsKey(param.getGroupId())) {
                //非--这个组id在map中不存在需要新增一个list
                map.put(param.getGroupId(), new ArrayList<>());
            }
            //不管有没有都添加到map里面
            map.get(param.getGroupId()).add(param);
        }
        //填充params到group
        for (SpecGroup specGroup : specGroups) {

            specGroup.setParams(map.get(specGroup.getId()));
        }
        //接下来就是把paramsList放入到specGroup
        //填充param,双重for效率低
        /*for (SpecGroup specGroup : specGroups) {
            for (SpecParam specParam : paramsList) {
                //只有当组id和
                if(specGroup.getId() == specParam.getGroupId()){

                }
            }
        }*/
        return specGroups;
    }
}
