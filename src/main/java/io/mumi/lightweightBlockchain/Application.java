
package io.mumi.lightweightBlockchain;

import io.mumi.lightweightBlockchain.api.services.AccountService;
import io.mumi.lightweightBlockchain.api.services.BlockService;
import io.mumi.lightweightBlockchain.api.services.DispatcherService;
import io.mumi.lightweightBlockchain.api.services.TransactionService;
import io.mumi.lightweightBlockchain.logic.DependencyManager;
import io.mumi.lightweightBlockchain.p2p.BlockchainNetwork;
import io.mumi.lightweightBlockchain.threads.Miner;
import com.owlike.genson.GensonBuilder;
import com.owlike.genson.ext.jaxrs.GensonJaxRSFeature;
import org.apache.log4j.Logger;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath( "blockchain/api" )
public class Application extends ResourceConfig
{
	private static Logger logger = Logger.getLogger( Application.class );

	public Application( )
	{
		packages(true, "de.etherbasics.basicblockchain.api.services" );
		registerClasses( getServiceClasses( ) );
		register( new GensonJaxRSFeature( ).use(
			new GensonBuilder( ).setSkipNull( true )
								.useIndentation( true )
								.useDateAsTimestamp( false )
								.useDateFormat( new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss" ) )
								.create( ) ) );

		BlockchainNetwork blockchainNetwork = DependencyManager.getBlockchainNetwork( );

		if ( blockchainNetwork == null )
		{
			logger.error( "Blockchain Network could not be initialized" );
		}

		Miner miner = DependencyManager.getMiner( );
		miner.registerListener( blockchainNetwork );

		Thread thread = new Thread( miner );
		thread.start( );
	}

	protected Set<Class<?>> getServiceClasses( )
	{
		final Set<Class<?>> returnValue = new HashSet<>( );

		returnValue.add( BlockService.class );
		returnValue.add( TransactionService.class );
		returnValue.add( DispatcherService.class );
		returnValue.add( AccountService.class );

		return returnValue;
	}
}
