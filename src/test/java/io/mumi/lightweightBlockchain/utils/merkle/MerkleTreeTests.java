
package io.mumi.lightweightBlockchain.utils.merkle;

import io.mumi.lightweightBlockchain.models.Transaction;
import io.mumi.lightweightBlockchain.utils.SHA3Helper;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class MerkleTreeTests
{
	private Transaction t1;
	private Transaction t2;
	private Transaction t3;
	private Transaction t4;
	private Transaction t5;
	private Transaction t6;
	private Transaction t7;
	private Transaction t8;

	private List<Transaction> transactionList;

	@Before
	public void setUp( )
	{
		t1 = new Transaction( "sender1", "receiver1", 1.5, 0,
			0.0000005, 0.000001 );
		t2 = new Transaction( "sender2", "receiver2", 1.5, 0,
			0.000005, 0.000001 );
		t3 = new Transaction( "sender3", "receiver3", 1.5, 0,
			0.0000004, 0.000001 );
		t4 = new Transaction( "sender4", "receiver4", 1.5, 0,
			0.0000002, 0.000001 );
		t5 = new Transaction( "sender5", "receiver5", 1.5, 0,
			0.0000003, 0.000001 );
		t6 = new Transaction( "sender6", "receiver6", 1.5, 0,
			0.0000001, 0.000001 );
		t7 = new Transaction( "sender7", "receiver7", 1.5, 0,
			0.0000006, 0.000001 );
		t8 = new Transaction( "sender8", "receiver8", 1.5, 0,
			0.0000008, 0.000001 );

		transactionList = new ArrayList<>( );

		transactionList.add( t1 );
		transactionList.add( t2 );
		transactionList.add( t3 );
		transactionList.add( t4 );
		transactionList.add( t5 );
		transactionList.add( t6 );
		transactionList.add( t7 );
		transactionList.add( t8 );
	}

	@Test
	public void testMerkleTree( )
	{
		MerkleTree merkleTree = new MerkleTree( transactionList );

		for ( Transaction transaction : transactionList )
		{
			System.out.println( SHA3Helper.digestToHex( transaction.getTxId( ) ) );
			System.out.println( );
		}

		System.out.println("------------------------------" );

		for ( byte[] hashesForTransactionHash : merkleTree.getHashesForTransactionHash( t7.getTxId( ) ) )
		{
			System.out.println( SHA3Helper.digestToHex( hashesForTransactionHash ) );
		}
	}
}
