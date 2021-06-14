
package io.mumi.lightweightBlockchain.logic;

import io.mumi.lightweightBlockchain.models.Block;
import io.mumi.lightweightBlockchain.models.Transaction;
import io.mumi.lightweightBlockchain.utils.SizeHelper;
import io.mumi.lightweightBlockchain.utils.TransactionComparatorByFee;
import org.apache.log4j.Logger;

import java.util.*;

public class PendingTransactions
{
	private static Logger logger = Logger.getLogger( PendingTransactions.class );

	private PriorityQueue<Transaction> pendingTransactions;

	public PendingTransactions( )
	{
		Comparator<Transaction> comparator = new TransactionComparatorByFee( );
		pendingTransactions = new PriorityQueue<>( 11, comparator );
	}

	public PendingTransactions( Comparator<Transaction> comparator )
	{
		pendingTransactions = new PriorityQueue<>( 11, comparator );
	}

	public void addPendingTransaction( Transaction transaction )
	{
		logger.info( this + " add transactions" );
		pendingTransactions.add( transaction );
	}

	public void addPendingTransactions( Collection<Transaction> transactions )
	{
		for ( Transaction transaction : transactions )
		{
			addPendingTransaction( transaction );
		}
	}

	public List<Transaction> getTransactionsForNextBlock( )
	{
		List<Transaction> nextTransactions = new ArrayList<>( );

		int transactionCapacity = SizeHelper.calculateTransactionCapacity( );

		PriorityQueue<Transaction> temp = new PriorityQueue<>( pendingTransactions );
		while ( transactionCapacity > 0 && !temp.isEmpty( ) )
		{
			nextTransactions.add( temp.poll( ) );
			transactionCapacity--;
		}

		return nextTransactions;
	}

	public void clearPendingTransactions( Block block )
	{
		clearPendingTransactions( block.getTransactions( ) );
	}

	public void clearPendingTransactions( Collection<Transaction> transactions )
	{
		for ( Transaction transaction : transactions )
		{
			pendingTransactions.remove( transaction );
		}
	}

	public void clearPendingTransaction( Transaction transaction )
	{
		pendingTransactions.remove( transaction );
	}

	public boolean pendingTransactionsAvailable( )
	{
		return !pendingTransactions.isEmpty( );
	}

	public boolean areThereNoOtherPendingTransactionsFor( Transaction transaction )
	{
		boolean result = true;

		for ( Transaction pendingTransaction : pendingTransactions )
		{
			if ( pendingTransaction.equals( transaction ) )
			{
				continue;
			}

			if ( Arrays.equals( pendingTransaction.getSender( ), transaction.getSender( ) ) )
			{
				result = false;
			}
		}

		return result;
	}
}
