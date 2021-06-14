
package io.mumi.lightweightBlockchain.p2p;

import io.mumi.lightweightBlockchain.logic.DependencyManager;
import io.mumi.lightweightBlockchain.models.Block;
import io.mumi.lightweightBlockchain.p2p.adapters.BlockAdapter;
import io.mumi.lightweightBlockchain.p2p.adapters.TransactionAdapter;
import io.mumi.lightweightBlockchain.utils.SHA3Helper;
import org.apache.log4j.Logger;

public class MessageHandler
{
	private static Logger logger = Logger.getLogger( MessageHandler.class );

	public MessageHandler( )
	{
	}

	public void handleTransaction( TransactionAdapter transactionAdapter )
	{
		DependencyManager.getPendingTransactions( ).addPendingTransaction( transactionAdapter.getTransaction( ) );
	}

	public void handleBlock( BlockAdapter blockAdapter )
	{
		logger.info( "receiving block: " + SHA3Helper.digestToHex( blockAdapter.getBlock( ).getBlockHash( ) ) );
		Block block = blockAdapter.getBlock( );

		DependencyManager.getBlockchain( ).addBlock( block );
		DependencyManager.getMiner( ).cancelBlock( );
	}
}
