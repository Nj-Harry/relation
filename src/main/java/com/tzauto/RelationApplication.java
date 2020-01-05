package com.tzauto;

import com.tzauto.javafxSupport.AbstractJavaFxApplicationSupport;
import com.tzauto.view.LoginView;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(value = "com.tzauto.dao")
//SFCZ4_ZDCVL  表单编号
public class RelationApplication extends AbstractJavaFxApplicationSupport {

	public static void main(String[] args) {
		launch(RelationApplication.class, LoginView.class, new SplashScreenCofig(), args);
	}

}
