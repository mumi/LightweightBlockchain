
package io.mumi.lightweightBlockchain.utils;

import io.mumi.lightweightBlockchain.accounts.Account;
import io.mumi.lightweightBlockchain.models.Block;
import io.mumi.lightweightBlockchain.models.Transaction;
import io.mumi.lightweightBlockchain.utils.merkle.MerkleTree;
import io.mumi.lightweightBlockchain.logic.Blockchain;
import io.mumi.lightweightBlockchain.logic.DependencyManager;
import org.apache.log4j.Logger;

import java.util.Arrays;

public class VerificationHelper
{
	private static Logger logger = Logger.getLogger( VerificationHelper.class );

	public static boolean verifyBlock( Block block )
	{
		boolean fulfillsDifficulty = DependencyManager.getBlockchain( ).fulfillsDifficulty( block.getBlockHash( ) );
		boolean correctVersion = Blockchain.VERSION == block.getBlockHeader( ).getVersion( );
		boolean transactionsVerified = true;
		boolean merkleTreeRootVerified = Arrays.equals( block.getBlockHeader( ).getTransactionListHash( ),
			new MerkleTree( block.getTransactions( ) ).getMerkleTreeRoot( ) );

		for ( Transaction transaction : block.getTransactions( ) )
		{
			transactionsVerified = verifyTransaction( transaction );

			if ( !transactionsVerified )
			{
				break;
			}
		}

		return fulfillsDifficulty && correctVersion && transactionsVerified && merkleTreeRootVerified;
	}

	public static boolean verifyTransaction( Transaction transaction )
	{
		boolean signatureVerified = verifySignature( transaction );
		boolean balanceVerified = verifyBalance( transaction );
		boolean pendingTransactionsVerified = verifyPendingTransactions( transaction );

		return signatureVerified && balanceVerified && pendingTransactionsVerified;
	}

	public static boolean verifySignature( Transaction transaction )
	{
		boolean result;
		try
		{
			logger.debug( transaction.asJSONString( ) );
			result = SignatureHelper.verify( transaction.asJSONString( ).getBytes( "UTF-8" ) ,
				transaction.getSignature( ), transaction.getSender( ) );
			logger.debug( "signature verification result: " + result );
		}
		catch ( Exception e )
		{
			result = false;
		}
		return result;
	}

	private static boolean verifyBalance( Transaction transaction )
	{
		Account account = DependencyManager.getAccountStorage( ).getAccount( transaction.getSender( ) );
		double totalCost =
			transaction.getAmount( ) + ( transaction.getTransactionFeeBasePrice( ) * Blockchain.TRANSACTION_FEE_UNITS );
		logger.debug( "balance verification result: " + ( totalCost <= account.getBalance( ) ) );
		return totalCost <= ( account.getBalance( ) - account.getLockedBalance( ) );
	}

	private static boolean verifyPendingTransactions( Transaction transaction )
	{
		return DependencyManager.getPendingTransactions( ).areThereNoOtherPendingTransactionsFor( transaction );
	}
}
