package com.allinpay.achilles.achilles;

import com.allinpay.achilles.controller.QuickPayController;
import com.allinpay.achilles.core.UUIDUtil;
import com.allinpay.achilles.domian.*;
import com.allinpay.achilles.service.MerchantConfigService;
import com.allinpay.achilles.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AchillesApplicationTests {

	@Autowired
	private UserService service;
	@Autowired
	private MerchantConfigService configService;

	@Autowired
	private QuickPayController controller;

	@Test
	public void contextLoads() {
	}

}
