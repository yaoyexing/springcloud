package com.demo.myRule;

import java.util.List;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;

public class MyIRule extends AbstractLoadBalancerRule {
	
	// 需求：轮询，每台机器访问5次
	private int total = 0; // 当前第几次访问，从0开始
	private int currentIndex = 0; // 当前是第几台机器，下标，从0开始

    public Server choose(ILoadBalancer lb, Object key) {
        Server server = null;

        while (server == null) {
            if (Thread.interrupted()) {
                return null;
            }
            List<Server> upList = lb.getReachableServers();
            List<Server> allList = lb.getAllServers();

            int serverCount = allList.size();
            if (serverCount == 0) {
                return null;
            }

            if(total < 5) {
            	server = upList.get(currentIndex);
            	total++;
            }else {
            	total = 0; // total置为0
            	currentIndex++;
            	if(currentIndex >= allList.size()) {
            		currentIndex = 0;
            	}
            }
            
            if (server == null) {
                /*
                 * The only time this should happen is if the server list were
                 * somehow trimmed. This is a transient condition. Retry after
                 * yielding.
                 */
                Thread.yield();
                continue;
            }

            if (server.isAlive()) {
                return (server);
            }
            // Shouldn't actually happen.. but must be transient or a bug.
            server = null;
            Thread.yield();
        }
        return server;
    }

	@Override
	public Server choose(Object key) {
		return choose(getLoadBalancer(), key);
	}
	
	@Override
	public void initWithNiwsConfig(IClientConfig clientConfig) {}
}
