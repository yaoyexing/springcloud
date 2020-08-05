package com.demo.springcloud.cfgbeans;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.demo.myRule.MyIRule;
import com.netflix.loadbalancer.IRule;

@Configuration
public class ConfigBean {

	
	@Bean
	@LoadBalanced
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

//	@Bean
//	public IRule IRule() {
//		return new MyIRule();
//	}	
}
