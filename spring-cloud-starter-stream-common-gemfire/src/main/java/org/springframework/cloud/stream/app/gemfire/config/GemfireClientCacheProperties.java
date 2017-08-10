package org.springframework.cloud.stream.app.gemfire.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("gemfire.client")
public class GemfireClientCacheProperties {

	String durableClientId;
	Integer durableClientTimeout;

	public String getDurableClientId() {
		return durableClientId;
	}

	public void setDurableClientId(String durableClientId) {
		this.durableClientId = durableClientId;
	}

	public Integer getDurableClientTimeout() {
		return durableClientTimeout;
	}

	public void setDurableClientTimeout(Integer durableClientTimeout) {
		this.durableClientTimeout = durableClientTimeout;
	}

}
