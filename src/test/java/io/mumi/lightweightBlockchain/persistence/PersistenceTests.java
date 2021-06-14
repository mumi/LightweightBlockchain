
package io.mumi.lightweightBlockchain.persistence;

import io.mumi.lightweightBlockchain.models.Block;
import io.mumi.lightweightBlockchain.models.Chain;
import io.mumi.lightweightBlockchain.models.Transaction;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PersistenceTests
{
	@Test
	public void testWriteChain_WithOneBlock_CorrectJSONFileWritten( )
	{
		Transaction t = new Transaction( "sender", "receiver", 1.5, 0, 0.0000005, 0.000001 );
		Transaction t1 = new Transaction( "fdjkngliuabglbaubf", "fahgibgafadbvbahvbab", 1.5, 0, 0.0000005,
			0.000001 );
		Transaction t2 = new Transaction( "sender", "aslkjdfhalbu fviueabuhbiuvbfvbav", 1.5, 0, 0.0000005,
			0.000001 );

		List<Transaction> transactionList = new ArrayList<>( );
		transactionList.add( t );
		transactionList.add( t1 );
		transactionList.add( t2 );

		Block block = new Block( transactionList, new byte[0] );

		Chain chain = new Chain( 1 );
		chain.add( block );

		Persistence persistence = new Persistence( );
		persistence.writeChain( chain );

		Chain chainRead = persistence.readChain( 1 );

		System.out.println( chainRead );
		System.out.println( chainRead.get( 0 ).getTransactions( ).size( ) );
	}

	@Test
	public void testReadChain_WithEmptyFolder_GenesisChainReturned( )
	{
		Persistence persistence = new Persistence( );
		Chain chain = persistence.readChain( 1 );
		assertEquals( "Size was not 1", 1, chain.size( ) );
	}

	@Test
	public void testWriteChainMultipleTimes_WithTwoBlocks_FirstBlockOnlyWrittenOnce( )
	{
		Transaction t = new Transaction( "sender", "receiver", 1.5, 0, 0.0000005, 0.000001 );
		Transaction t1 = new Transaction( "fdjkngliuabglbaubf", "fahgibgafadbvbahvbab", 1.5, 0, 0.0000005,
			0.000001 );
		Transaction t2 = new Transaction( "sender", "aslkjdfhalbu fviueabuhbiuvbfvbav", 1.5, 0, 0.0000005,
			0.000001 );

		List<Transaction> transactionList = new ArrayList<>( );
		transactionList.add( t );
		transactionList.add( t1 );
		transactionList.add( t2 );

		Block block1 = new Block( transactionList, new byte[0] );

		Chain chain = new Chain( 1 );
		chain.add( block1 );

		Persistence persistence = new Persistence( );
		persistence.writeChain( chain );

		Block block2 = new Block( transactionList, block1.getBlockHash( ) );
		chain.add( block2 );
		persistence.writeChain( chain );
	}
}
