package com.tzauto;

import com.tzauto.dao.MainMapping;
import com.tzauto.entity.RelationEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RelationApplicationTest {
    @Resource
    private MainMapping mainMapping;
    @Test
    public void test(){
        RelationEntity relationEntity = new RelationEntity(null, "F0AP00A", "43", "F6.xml", null);
        List<RelationEntity> all = mainMapping.getAll();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for(RelationEntity one : all){

            String time = dateFormat.format(one.getLastModifyTime());
            System.out.println("料号:"+one.getMaterialNumber()+",原时间格式:"+one.getLastModifyTime()+",时间:"+time);
        }
    }
}
