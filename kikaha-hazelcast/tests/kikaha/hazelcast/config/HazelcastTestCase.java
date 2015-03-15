package kikaha.hazelcast.config;

import kikaha.core.api.conf.Configuration;
import kikaha.core.impl.conf.DefaultConfiguration;
import lombok.SneakyThrows;
import lombok.val;

import org.junit.After;
import org.junit.Before;

import trip.spi.ServiceProvider;

import com.hazelcast.core.Hazelcast;
import com.typesafe.config.Config;

public class HazelcastTestCase {

	@Before
	@SneakyThrows
	public void injectDependencies() {
		val config = DefaultConfiguration.loadDefaultConfiguration();
		val provider = new ServiceProvider();
		provider.providerFor( Config.class, config.config() );
		provider.providerFor( Configuration.class, config );

		provideExtraDependencies( provider );
		provider.provideOn( this );
		afterProvideDependencies();
	}

	protected void afterProvideDependencies() {}

	protected void provideExtraDependencies( ServiceProvider provider ) {}

	@After
	public void shutdownHazelcast() {
		Hazelcast.shutdownAll();
	}
}