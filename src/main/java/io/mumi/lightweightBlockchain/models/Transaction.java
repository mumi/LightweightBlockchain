
package io.mumi.lightweightBlockchain.models;

import com.owlike.genson.annotation.JsonConverter;
import com.owlike.genson.annotation.JsonIgnore;
import io.mumi.lightweightBlockchain.api.converters.HashConverter;
import io.mumi.lightweightBlockchain.utils.SHA3Helper;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

public class Transaction implements Serializable
{
	private transient byte[] txId;

	private byte[] sender;

	private byte[] receiver;

	private double amount;

	private int nonce;

	private transient long receivedTimeStamp;

	private transient byte[] blockId;

	private double transactionFeeBasePrice;

	private double transactionFeeLimit;

	private transient byte[] signature;

	private transient double transactionFee;

	private transient int sizeInByte;

	public Transaction( String sender, String receiver, double amount, int nonce, double transactionFeeBasePrice,
		double transactionFeeLimit )
	{
		this.sender = sender.getBytes( Charset.forName( "utf8" ) );
		this.receiver = receiver.getBytes( Charset.forName( "utf8" ) );
		this.amount = amount;
		this.nonce = nonce;
		this.transactionFeeBasePrice = transactionFeeBasePrice;
		this.transactionFeeLimit = transactionFeeLimit;

		initTxId( );
	}

	public Transaction( byte[] sender, byte[] receiver, double amount, int nonce, double transactionFeeBasePrice,
		double transactionFeeLimit )
	{
		this.sender = sender;
		this.receiver = receiver;
		this.amount = amount;
		this.nonce = nonce;
		this.transactionFeeBasePrice = transactionFeeBasePrice;
		this.transactionFeeLimit = transactionFeeLimit;

		initTxId( );
	}

	public Transaction( )
	{
		initTxId( );
	}

	private void initTxId()
	{
		this.setTxId( SHA3Helper.hash256( this ) );
	}

	@JsonConverter( HashConverter.class )
	public byte[] getTxId( )
	{
		this.txId = SHA3Helper.hash256( this );
		return txId;
	}

	@JsonIgnore
	public String getTxIdAsString( )
	{
		return SHA3Helper.hash256AsHex( this );
	}

//	@JsonConverter( HashConverter.class )
	public void setTxId( byte[] txId )
	{
		this.txId = txId;
	}

	@JsonConverter( HashConverter.class )
	public byte[] getSender( )
	{
		return sender;
	}

	@JsonConverter( HashConverter.class )
	public void setSender( byte[] sender )
	{
		this.sender = sender;
	}

	@JsonConverter( HashConverter.class )
	public byte[] getReceiver( )
	{
		return receiver;
	}

	@JsonConverter( HashConverter.class )
	public void setReceiver( byte[] receiver )
	{
		this.receiver = receiver;
	}

	public double getAmount( )
	{
		return amount;
	}

	public void setAmount( double amount )
	{
		this.amount = amount;
	}

	public int getNonce( )
	{
		return nonce;
	}

	public void setNonce( int nonce )
	{
		this.nonce = nonce;
	}

	public long getReceivedTimeStamp( )
	{
		return receivedTimeStamp;
	}

	public void setReceivedTimeStamp( long receivedTimeStamp )
	{
		this.receivedTimeStamp = receivedTimeStamp;
	}

	@JsonConverter( HashConverter.class )
	public byte[] getBlockId( )
	{
		return blockId;
	}

	@JsonIgnore
	public void setBlockId( byte[] blockId )
	{
		this.blockId = blockId;
	}

	public double getTransactionFeeBasePrice( )
	{
		return transactionFeeBasePrice;
	}

	public void setTransactionFeeBasePrice( double transactionFeeBasePrice )
	{
		this.transactionFeeBasePrice = transactionFeeBasePrice;
	}

	public double getTransactionFeeLimit( )
	{
		return transactionFeeLimit;
	}

	public void setTransactionFeeLimit( double transactionFeeLimit )
	{
		this.transactionFeeLimit = transactionFeeLimit;
	}

	@JsonIgnore
	public byte[] getSignature( )
	{
		return signature;
	}

	@JsonConverter( HashConverter.class )
	public void setSignature( byte[] signature )
	{
		this.signature = signature;
	}

	public double getTransactionFee( )
	{
		return transactionFee;
	}

	public void setTransactionFee( double transactionFee )
	{
		this.transactionFee = transactionFee;
	}

	public int getSizeInByte( )
	{
		return sizeInByte;
	}

	@JsonIgnore
	public void setSizeInByte( int sizeInByte )
	{
		this.sizeInByte = sizeInByte;
	}

	public String asJSONString( )
	{
		DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		df.setMaximumFractionDigits(340);

		return "{" +
			"\"sender\":\"" + SHA3Helper.digestToHex( sender ) + '\"' +
			",\"receiver\":\"" + SHA3Helper.digestToHex( receiver ) + '\"' +
			",\"amount\":\"" + amount + "\"" +
			//			",\"nonce\":" + nonce +
			",\"transactionFeeBasePrice\":\"" + df.format( transactionFeeBasePrice ) + "\"" +
			",\"transactionFeeLimit\":\"" + transactionFeeLimit + "\"" +
			'}';
	}

	@Override public String toString( )
	{
		return "Transaction{" +
			"sender='" + SHA3Helper.digestToHex( sender ) + '\'' +
			", receiver='" + SHA3Helper.digestToHex( receiver ) + '\'' +
			", amount=" + amount +
			", nonce=" + nonce +
			", transactionFeeBasePrice=" + transactionFeeBasePrice +
			", transactionFeeLimit=" + transactionFeeLimit +
			'}';
	}

	@Override public boolean equals( Object o )
	{
		if ( this == o )
			return true;
		if ( o == null || getClass( ) != o.getClass( ) )
			return false;
		Transaction that = ( Transaction ) o;
		return Double.compare( that.amount, amount ) == 0 &&
			nonce == that.nonce &&
			Double.compare( that.transactionFeeBasePrice, transactionFeeBasePrice ) == 0 &&
			Double.compare( that.transactionFeeLimit, transactionFeeLimit ) == 0 &&
			Arrays.equals( txId, that.txId ) &&
			Arrays.equals( sender, that.sender ) &&
			Arrays.equals( receiver, that.receiver );
	}

	@Override public int hashCode( )
	{
		int result = Objects.hash( amount, nonce, transactionFeeBasePrice, transactionFeeLimit );
		result = 31 * result + Arrays.hashCode( txId );
		result = 31 * result + Arrays.hashCode( sender );
		result = 31 * result + Arrays.hashCode( receiver );
		return result;
	}
}
