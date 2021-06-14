
package io.mumi.lightweightBlockchain.accounts;

import com.owlike.genson.annotation.JsonConverter;
import com.owlike.genson.annotation.JsonIgnore;
import io.mumi.lightweightBlockchain.api.converters.HashConverter;
import io.mumi.lightweightBlockchain.logic.Blockchain;
import io.mumi.lightweightBlockchain.models.Block;
import io.mumi.lightweightBlockchain.models.Transaction;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Account
{
	private byte[] address;

	private double balance;

	private double lockedBalance;

	private List<Transaction> incomingTransactions;

	private List<Transaction> outgoingTransactions;

	private List<Block> minedBlocks;

	public Account( byte[] address )
	{
		this.address = address;
		this.incomingTransactions = new CopyOnWriteArrayList<>( );
		this.outgoingTransactions = new CopyOnWriteArrayList<>( );
		this.minedBlocks = new CopyOnWriteArrayList<>( );
	}

	public void addIncomingTransaction( Transaction transaction )
	{
		this.incomingTransactions.add( transaction );
		this.balance += transaction.getAmount( );
	}

	public void addOutgoingTransaction( Transaction transaction )
	{
		this.outgoingTransactions.add( transaction );
		this.balance -= transaction.getAmount( );
		this.balance -= transaction.getTransactionFee( );
	}

	public void addMinedBlock( Block block )
	{
		this.minedBlocks.add( block );
		this.balance += Blockchain.BLOCK_REWARD;
		this.lockedBalance += Blockchain.BLOCK_REWARD;

		for ( Transaction transaction : block.getTransactions( ) )
		{
			this.balance += transaction.getTransactionFee( );
			this.lockedBalance += transaction.getTransactionFee( );
		}
	}

	@JsonConverter( HashConverter.class )
	public byte[] getAddress( )
	{
		return address;
	}

	@JsonConverter( HashConverter.class )
	public void setAddress( byte[] address )
	{
		this.address = address;
	}

	public List<Transaction> getTransactions( )
	{
		List<Transaction> result = new CopyOnWriteArrayList<>( incomingTransactions );
		result.addAll( outgoingTransactions );

		Collections.sort( result, ( o1, o2 ) -> {
			int result1 = ( o1.getReceivedTimeStamp( ) < o2.getReceivedTimeStamp( ) ) ? -1 : 1;
			result1 = ( o1.getReceivedTimeStamp( ) == o2.getReceivedTimeStamp( ) ) ? 0 : result1;
			return result1;
		} );

		return result;
	}

	public List<Block> getMinedBlocks( )
	{
		return minedBlocks;
	}

	@JsonIgnore
	public List<Transaction> getIncomingTransactions( )
	{
		return this.incomingTransactions;
	}

	@JsonIgnore
	public List<Transaction> getOutgoingTransactions( )
	{
		return this.outgoingTransactions;
	}

	public double getBalance( )
	{
		return this.balance;
	}

	public void addBalance( double amount )
	{
		this.balance += amount;
	}

	@JsonIgnore
	public double getLockedBalance( )
	{
		return lockedBalance;
	}

	public void unlockBalance( double sumToUnlock )
	{
		this.lockedBalance -= sumToUnlock;
	}

	@JsonIgnore
	public int getNextNonce( )
	{
		return outgoingTransactions.size( );
	}

	@Override public boolean equals( Object o )
	{
		if ( this == o )
			return true;
		if ( o == null || getClass( ) != o.getClass( ) )
			return false;
		Account account = ( Account ) o;
		return Arrays.equals( address, account.address );
	}

	@Override public int hashCode( )
	{
		return Arrays.hashCode( address );
	}
}
