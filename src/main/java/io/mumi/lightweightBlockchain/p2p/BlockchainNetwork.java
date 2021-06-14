
package io.mumi.lightweightBlockchain.p2p;

import io.mumi.lightweightBlockchain.logic.DependencyManager;
import io.mumi.lightweightBlockchain.models.Block;
import io.mumi.lightweightBlockchain.models.Transaction;
import io.mumi.lightweightBlockchain.p2p.adapters.BlockAdapter;
import io.mumi.lightweightBlockchain.p2p.adapters.BlockchainAdapter;
import io.mumi.lightweightBlockchain.p2p.adapters.TransactionAdapter;
import io.mumi.lightweightBlockchain.threads.MinerListener;
import com.owlike.genson.Genson;
import org.apache.log4j.Logger;
import org.jgroups.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class BlockchainNetwork extends ReceiverAdapter implements MinerListener
{
	private static Logger logger = Logger.getLogger( BlockchainNetwork.class );
	private static Genson genson = new Genson( );

	private JChannel channel;

	private View view;

	private MessageHandler handler;

	public BlockchainNetwork( ) throws Exception
	{
		handler = new MessageHandler( );

		System.setProperty( "java.net.preferIPv4Stack", "true" );
		this.channel = new JChannel( "src/main/resources/udp.xml" );
		channel.setReceiver( this );
		channel.setDiscardOwnMessages( true );
		channel.connect( "basicblockchain" );
		channel.getState( null, 0 );

		logger.info( channel.getAddressAsString( ) );
	}

	@Override public void receive( Message msg )
	{
		try
		{
			String json = new String( msg.getRawBuffer( ) );
			if ( json.contains( "\"type\":\"BlockAdapter\"" ) )
			{
				handler.handleBlock( genson.deserialize( json, BlockAdapter.class ) );
			} else if ( json.contains( "\"type\":\"TransactionAdapter\"" ) )
			{
				handler.handleTransaction( genson.deserialize( json, TransactionAdapter.class ) );
			}
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
		}
	}

	@Override public void viewAccepted( View view )
	{
		if (this.view == null) {
			System.out.println("Received initial view:");
			view.forEach(System.out::println);
		} else {
			// Compare to last view
			System.out.println("Received new view.");

			List<Address> newMembers = View.newMembers(this.view, view);
			System.out.println("New members: ");
			newMembers.forEach(System.out::println);

			List<Address> exMembers = View.leftMembers(this.view, view);
			System.out.println("Exited members:");
			exMembers.forEach(System.out::println);
		}
		this.view = view;
	}

	@Override public void getState( OutputStream output ) throws Exception
	{
		System.out.println("Sending state" );
		System.out.println( "Chain Length: " + DependencyManager.getBlockchain( ).getAltChains( ).get( 0 ).size( ) );
		System.out.println( "Difficulty " + DependencyManager.getBlockchain( ).getDifficulty( ) );
		genson.serialize( new BlockchainAdapter( DependencyManager.getBlockchain( ) ), output );
	}

	@Override public void setState( InputStream input ) throws Exception
	{
		System.out.println( "Received state:" );
		BlockchainAdapter blockchainAdapter = genson.deserialize( input, BlockchainAdapter.class );

		DependencyManager.injectBlockchain( blockchainAdapter.getBlockchain( ) );
		DependencyManager.getAccountStorage( );
		System.out.println( "Available Chains: " + blockchainAdapter.getBlockchain( ).getAltChains( ).size( ) );
		System.out.println( "Chain Length: " + blockchainAdapter.getBlockchain( ).getAltChains( ).get( 0 ).size( ) );
		System.out.println( "Best Block: " + blockchainAdapter.getBlockchain( ).getLatestBlock( ) );
		System.out.println( "Difficulty: " + blockchainAdapter.getBlockchain( ).getDifficulty( ) );
	}

	public void sendTransaction( Transaction transaction ) throws Exception
	{
		Message message = new Message( null, transactionToJSON( transaction ) );
		channel.send( message );
	}

	private byte[] transactionToJSON( Transaction transaction )
	{
		return genson.serializeBytes( new TransactionAdapter( transaction ) );
	}

	public void sendBlock( Block block ) throws Exception
	{
		Message message = new Message( null, blockToJSON( block ) );
		channel.send( message );
	}

	private byte[] blockToJSON( Block block )
	{
		return genson.serializeBytes( new BlockAdapter( block ) );
	}

	@Override public void notifyNewBlock( Block block )
	{
		try
		{
			sendBlock( block );
		}
		catch ( Exception e )
		{
			logger.error( "Could not send block: " + block );
		}
	}
}
