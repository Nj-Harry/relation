package com.tzauto.dao;

import com.tzauto.entity.LotInfo;
import com.tzauto.entity.RelationEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Administrator on 2019/8/16.
 */

public interface MainMapping {

    List<RelationEntity> getAll();

    void delete(int id);

    Integer add(RelationEntity relationEntity);

    void update(RelationEntity relationEntity);

    List<RelationEntity> query(RelationEntity relationEntity);

    String queryUser(String name, String passWord);




    void addData(@Param("lot") String lot, @Param("item2") String item2, @Param("item4") String item4, @Param("item5") String item5, @Param("item6") String item6, @Param("isMain") String isMain);
}
