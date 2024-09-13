package com.oneswap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class OneswapWorkerApplication {

	public static void main(String[] args) {
		SpringApplication.run(OneswapWorkerApplication.class, args);
	}

}
