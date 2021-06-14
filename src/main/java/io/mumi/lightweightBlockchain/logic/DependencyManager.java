
package io.mumi.lightweightBlockchain.logic;

import io.mumi.lightweightBlockchain.accounts.AccountStorage;
import io.mumi.lightweightBlockchain.threads.Miner;
import io.mumi.lightweightBlockchain.p2p.BlockchainNetwork;
import org.apache.log4j.Logger;

public class DependencyManager
{
	private static Logger logger = Logger.getLogger( DependencyManager.class );

	private static PendingTransactions pendingTransactions;

	public static PendingTransactions getPendingTransactions( )
	{
		if ( pendingTransactions == null )
		{
			pendingTransactions = new PendingTransactions( );
		}

		return pendingTransactions;
	}

	public static void injectPendingTransactions( PendingTransactions pendingTransactions )
	{
		DependencyManager.pendingTransactions = pendingTransactions;
	}

	private static Blockchain blockchain;

	public static Blockchain getBlockchain( )
	{
		if ( blockchain == null )
		{
			blockchain = new Blockchain( );
		}

		return blockchain;
	}

	public static void injectBlockchain( Blockchain blockchain )
	{
		DependencyManager.blockchain = blockchain;
	}

	private static Miner miner;

	public static Miner getMiner( )
	{
		if ( miner == null )
		{
			miner = new Miner( );
		}

		return miner;
	}

	private static BlockchainNetwork blockchainNetwork;

	public static BlockchainNetwork getBlockchainNetwork( )
	{
		if ( blockchainNetwork == null )
		{
			try
			{
				blockchainNetwork = new BlockchainNetwork( );
			}
			catch ( Exception e )
			{
				logger.debug( e.getMessage( ) );
			}
		}

		return blockchainNetwork;
	}

	private static AccountStorage accountStorage;

	public static AccountStorage getAccountStorage( )
	{
		if ( accountStorage == null )
		{
			accountStorage = new AccountStorage( );
		}

		return accountStorage;
	}
}
