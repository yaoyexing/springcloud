package com.demo.springcloud.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.demo.springcloud.entities.Dept;
import com.demo.springcloud.service.DeptService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
public class DeptController {

	@Autowired
	public DeptService deptService;

	@Autowired
	private DiscoveryClient client;

	@RequestMapping(value = "/dept/add", method = RequestMethod.POST)
	public boolean add(@RequestBody Dept dept) {
		return deptService.add(dept);
	}

	@RequestMapping(value = "/dept/get/{id}", method = RequestMethod.GET)
	@HystrixCommand(fallbackMethod="processHystrix_get")
	public Dept get(@PathVariable("id") Long id) {
		Dept dept = deptService.get(id);
		if(null == dept)
		   {
		     throw new RuntimeException("该ID："+id+"没有没有对应的信息");
		   }
		   return dept;
	}
	
	public Dept processHystrix_get(@PathVariable("id") Long id) {
		return new Dept().setDeptno(id)
		           .setDname("该ID："+id+"没有没有对应的信息,null--@HystrixCommand")
		           .setDb_source("no this database in MySQL");
	}

	@RequestMapping(value = "/dept/list", method = RequestMethod.GET)
	public List<Dept> list() {
		return deptService.list();
	}

	@RequestMapping(value = "/dept/discovery", method = RequestMethod.GET)
	public Object discovery() {
		List<String> list = client.getServices();
		System.out.println("******" + list);
		List<ServiceInstance> serviceInstanceList = client.getInstances("MICROSERVICECLOUD-DEPT");
		for (ServiceInstance serviceInstance : serviceInstanceList) {
			System.out.println(serviceInstance.getServiceId() + "\t" + serviceInstance.getHost() + "\t"
					+ serviceInstance.getPort() + "\t" + serviceInstance.getUri());
		}
		return this.client;

	}
}
