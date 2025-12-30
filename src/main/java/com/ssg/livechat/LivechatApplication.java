package com.ssg.livechat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync // 비동기 기능 활성화
@SpringBootApplication
public class LivechatApplication {

	public static void main(String[] args) {
		SpringApplication.run(LivechatApplication.class, args);
	}

}
