
package io.mumi.lightweightBlockchain.logic;

import io.mumi.lightweightBlockchain.models.Transaction;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PendingTransactionsTests
{
	private PendingTransactions pendingTransactions = new PendingTransactions( );

	private Transaction t1;
	private Transaction t2;
	private Transaction t3;
	private Transaction t4;
	private Transaction t5;
	private Transaction t6;
	private Transaction t7;
	private Transaction t8;

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
	}

	@Test
	public void testGetTransactionsForNextBlock_FullBlockAvailable_EnoughTransactionsReturned( )
	{
		pendingTransactions.addPendingTransaction( t1 );
		pendingTransactions.addPendingTransaction( t2 );
		pendingTransactions.addPendingTransaction( t3 );
		pendingTransactions.addPendingTransaction( t4 );
		pendingTransactions.addPendingTransaction( t5 );
		pendingTransactions.addPendingTransaction( t6 );
		pendingTransactions.addPendingTransaction( t7 );
		pendingTransactions.addPendingTransaction( t8 );

		List<Transaction> transactionList = pendingTransactions.getTransactionsForNextBlock( );

		assertEquals( 8, transactionList.size( ) );
		assertTrue( transactionList.contains( t1 ) );
		assertTrue( transactionList.contains( t2 ) );
		assertTrue( transactionList.contains( t3 ) );
		assertTrue( transactionList.contains( t4 ) );
		assertTrue( transactionList.contains( t5 ) );
		assertTrue( transactionList.contains( t6 ) );
		assertTrue( transactionList.contains( t7 ) );
		assertTrue( transactionList.contains( t8 ) );
	}

	@Test
	public void testGetTransactionsForNextBlock_NoTransactionAvailable_EmptyListReturned( )
	{
		List<Transaction> transactionList = pendingTransactions.getTransactionsForNextBlock( );

		assertEquals( 0, transactionList.size( ) );
	}

	@Test
	public void testGetTransactionsForNextBlock_OneTransactionAvailable_ListWithOneElementReturned( )
	{
		pendingTransactions.addPendingTransaction( t1 );
		List<Transaction> transactionList = pendingTransactions.getTransactionsForNextBlock( );

		assertEquals( 1, transactionList.size( ) );
		assertTrue( transactionList.contains( t1 ) );
	}
}
