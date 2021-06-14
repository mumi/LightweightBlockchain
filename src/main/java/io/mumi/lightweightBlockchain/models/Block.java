
package io.mumi.lightweightBlockchain.models;

import io.mumi.lightweightBlockchain.utils.merkle.MerkleTree;
import com.owlike.genson.annotation.JsonConverter;
import com.owlike.genson.annotation.JsonIgnore;
import io.mumi.lightweightBlockchain.api.converters.HashConverter;
import io.mumi.lightweightBlockchain.utils.SizeHelper;

import java.util.ArrayList;
import java.util.List;

public class Block
{
	private int magicNumber = 0xD9B4BEF9;

	private int blockSize;

	private int transactionCount;

	private int blockNumber;

	private byte[] coinbase;

	private List<Transaction> transactions;

	private BlockHeader blockHeader;

	public Block( )
	{
	}

	public Block( byte[] previousHash )
	{
		this.transactionCount = 0;
		this.transactions = new ArrayList<>( );
		this.blockSize = SizeHelper.calculateBlockSize( this );

		this.blockHeader = new BlockHeader( System.currentTimeMillis( ), previousHash, getTransactionHash( ) );
	}

	public Block( List<Transaction> transactions, byte[] previousHash )
	{
		this.transactions = transactions;
		this.transactionCount = transactions.size( );
		this.blockSize = SizeHelper.calculateBlockSize( this );
		this.blockHeader = new BlockHeader( System.currentTimeMillis( ), previousHash, getTransactionHash( ) );
	}

	private byte[] getTransactionHash( )
	{
		return new MerkleTree( transactions ).getMerkleTreeRoot( );
	}

	public void addTransaction( Transaction transaction )
	{
		this.transactions.add( transaction );
		this.transactionCount++;

		this.blockHeader.setTransactionListHash( getTransactionHash( ) );
		this.blockSize = SizeHelper.calculateBlockSize( this );
	}

	@JsonConverter( HashConverter.class )
	public byte[] getBlockHash( )
	{
		return blockHeader.asHash( );
	}

	@JsonIgnore
	public int getNonce( )
	{
		return this.blockHeader.getNonce( );
	}

	@JsonIgnore
	public void setNonce( int nonce )
	{
		this.blockHeader.setNonce( nonce );
	}

	public void incrementNonce( ) throws ArithmeticException
	{
		this.blockHeader.incrementNonce( );
	}

	public int getMagicNumber( )
	{
		return magicNumber;
	}

	public void setMagicNumber( int magicNumber )
	{
		this.magicNumber = magicNumber;
	}

	public int getBlockSize( )
	{
		return blockSize;
	}

	public void setBlockSize( int blockSize )
	{
		this.blockSize = blockSize;
	}

	public int getTransactionCount( )
	{
		return transactionCount;
	}

	public int getBlockNumber( )
	{
		return blockNumber;
	}

	public void setBlockNumber( int blockNumber )
	{
		this.blockNumber = blockNumber;
	}

	@JsonConverter( HashConverter.class )
	public byte[] getCoinbase( )
	{
		return coinbase;
	}

	@JsonConverter( HashConverter.class )
	public void setCoinbase( byte[] coinbase )
	{
		this.coinbase = coinbase;
	}

	public void setTransactionCount( int transactionCount )
	{
		this.transactionCount = transactionCount;
	}

	public List<Transaction> getTransactions( )
	{
		return transactions;
	}

	public void setTransactions( List<Transaction> transactions )
	{
		this.transactions = transactions;
	}

	public BlockHeader getBlockHeader( )
	{
		return blockHeader;
	}

	public void setBlockHeader( BlockHeader blockHeader )
	{
		this.blockHeader = blockHeader;
	}
}
