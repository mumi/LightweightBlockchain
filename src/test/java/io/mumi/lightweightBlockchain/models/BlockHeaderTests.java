package io.mumi.lightweightBlockchain.models;

import org.junit.Test;

import java.math.BigInteger;


public class BlockHeaderTests
{
	@Test
	public void sizeTest()
	{
		BlockHeader blockHeader = new BlockHeader( System.currentTimeMillis( ), null, null );
		System.out.println( blockHeader.asHash( ) );
		System.out.println( new BigInteger( blockHeader.asHash( ) ) );
	}
}
