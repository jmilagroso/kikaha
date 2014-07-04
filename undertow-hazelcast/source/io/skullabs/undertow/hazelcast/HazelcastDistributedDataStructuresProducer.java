package io.skullabs.undertow.hazelcast;

import com.hazelcast.core.*;
import trip.spi.*;

@Service
public class HazelcastDistributedDataStructuresProducer {

	@Provided
	HazelcastInstance hazelcast;

	@Producer
	@SuppressWarnings( "rawtypes" )
	public IMap produceIMaps( final ProviderContext context ) {
		final String name = retrieveMapNameFrom( context );
		final IMap map = hazelcast.getMap( name );
		return map;
	}

	@Producer
	@SuppressWarnings( "rawtypes" )
	public IQueue produceIQueues( final ProviderContext context ) {
		final String name = retrieveMapNameFrom( context );
		final IQueue queue = hazelcast.getQueue( name );
		return queue;
	}

	@Producer
	@SuppressWarnings( "rawtypes" )
	public MultiMap produceMultimap( final ProviderContext context ) {
		final String name = retrieveMapNameFrom( context );
		final MultiMap mmap = hazelcast.getMultiMap( name );
		return mmap;
	}

	@Producer
	@SuppressWarnings( "rawtypes" )
	public ISet produceSet( final ProviderContext context ) {
		final String name = retrieveMapNameFrom( context );
		final ISet set = hazelcast.getSet( name );
		return set;
	}

	@Producer
	@SuppressWarnings( "rawtypes" )
	public IList produceList( final ProviderContext context ) {
		final String name = retrieveMapNameFrom( context );
		final IList list = hazelcast.getList( name );
		return list;
	}

	@Producer
	@SuppressWarnings( "rawtypes" )
	public ITopic produceTopic( final ProviderContext context ) {
		final String name = retrieveMapNameFrom( context );
		final ITopic topic = hazelcast.getTopic( name );
		return topic;
	}

	@Producer
	public ILock produceLock( final ProviderContext context ) {
		final String name = retrieveMapNameFrom( context );
		final ILock lock = hazelcast.getLock( name );
		return lock;
	}

	String retrieveMapNameFrom( final ProviderContext context ) {
		final Source name = context.getAnnotation( Source.class );
		if ( name != null )
			return name.value();
		throw new IllegalStateException( "Can't produce a Map: no name provided" );
	}
}