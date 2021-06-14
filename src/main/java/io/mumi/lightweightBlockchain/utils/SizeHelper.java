
package io.mumi.lightweightBlockchain.utils;

import io.mumi.lightweightBlockchain.models.Block;
import io.mumi.lightweightBlockchain.models.Transaction;
import io.mumi.lightweightBlockchain.logic.Blockchain;

import java.util.List;

public class SizeHelper
{
	public final static int TRANSACTION_META_DATA_SIZE_IN_BYTES = 242;

	private final static int TRANSACTION_SIGNATURE_MAX_SIZE = 72;

	public final static int BLOCK_HEADER_SIZE_IN_BYTES = 80;

	public final static int BLOCK_META_DATA_SIZE_IN_BYTES = 81;

	public static int calculateBlockSize( Block block )
	{
		return BLOCK_HEADER_SIZE_IN_BYTES + calculateTransactionListSize( block.getTransactions( ) ) +
			BLOCK_META_DATA_SIZE_IN_BYTES;
	}

	public static int calculateTransactionListSize( List<Transaction> transactions )
	{
		int size = 0;

		for ( Transaction transaction : transactions )
		{
			size += calculateTransactionSize( transaction );
		}

		return size;
	}

	private static int calculateTransactionSize( Transaction transaction )
	{
		return TRANSACTION_META_DATA_SIZE_IN_BYTES + transaction.getSignature( ).length;
	}

	public static int calculateTransactionCapacity( )
	{
		return ( Blockchain.MAX_BLOCK_SIZE_IN_BYTES - SizeHelper.BLOCK_META_DATA_SIZE_IN_BYTES -
			SizeHelper.BLOCK_HEADER_SIZE_IN_BYTES ) / ( SizeHelper.TRANSACTION_META_DATA_SIZE_IN_BYTES +
			TRANSACTION_SIGNATURE_MAX_SIZE );
	}
}
