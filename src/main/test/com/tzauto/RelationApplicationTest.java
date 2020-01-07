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
        RelationEntity relationEntity = new RelationEntity(51, "F0AP009", "1", "F6.xml", null);
        List<RelationEntity> all = mainMapping.getAll();

    }
}
