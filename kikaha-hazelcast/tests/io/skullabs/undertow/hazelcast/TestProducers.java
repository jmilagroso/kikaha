package io.skullabs.undertow.hazelcast;

import kikaha.core.DefaultConfiguration;

import com.typesafe.config.Config;

import trip.spi.Producer;

public class TestProducers {

	@Producer
	public Config produceDefaultConfiguration() {
		return DefaultConfiguration.loadDefaultConfig();
	}
}
