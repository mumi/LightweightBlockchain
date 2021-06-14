
package io.mumi.lightweightBlockchain.models;

import com.owlike.genson.annotation.JsonConverter;
import io.mumi.lightweightBlockchain.api.converters.HashConverter;
import io.mumi.lightweightBlockchain.logic.Blockchain;
import io.mumi.lightweightBlockchain.utils.SHA3Helper;

import java.io.Serializable;

public class BlockHeader implements Serializable
{
	private int version;

	private int nonce = 0;

	private long timestamp;

	private byte[] previousHash;

	private byte[] transactionListHash;

	public BlockHeader( )
	{
	}

	public BlockHeader( long timestamp, byte[] previousHash, byte[] transactionListHash )
	{
		this.version = Blockchain.VERSION;
		this.timestamp = timestamp;
		this.previousHash = previousHash;
		this.transactionListHash = transactionListHash;
	}

	public int getVersion( )
	{
		return version;
	}

	public void setVersion( int version )
	{
		this.version = version;
	}

	public int getNonce( )
	{
		return nonce;
	}

	public void setNonce( int nonce )
	{
		this.nonce = nonce;
	}

	public void incrementNonce( ) throws ArithmeticException
	{
		if ( this.nonce == Integer.MAX_VALUE )
		{
			throw new ArithmeticException( "nonce to high" );
		}

		this.nonce++;
	}

	public long getTimestamp( )
	{
		return timestamp;
	}

	public void setTimestamp( long timestamp )
	{
		this.timestamp = timestamp;
	}

	@JsonConverter( HashConverter.class )
	public byte[] getPreviousHash( )
	{
		return previousHash;
	}

	@JsonConverter( HashConverter.class )
	public void setPreviousHash( byte[] previousHash )
	{
		this.previousHash = previousHash;
	}

	@JsonConverter( HashConverter.class )
	public byte[] getTransactionListHash( )
	{
		return transactionListHash;
	}

	@JsonConverter( HashConverter.class )
	public void setTransactionListHash( byte[] transactionListHash )
	{
		this.transactionListHash = transactionListHash;
	}

	@Override public String toString( )
	{
		return "BlockHeader{" +
			"version='" + version + '\'' +
			", nonce=" + nonce +
			", timestamp=" + timestamp +
			", previousHash='" + previousHash + '\'' +
			", transactionListHash='" + transactionListHash + '\'' +
			'}';
	}

	public byte[] asHash( )
	{
		return SHA3Helper.hash256( this );
	}
}
