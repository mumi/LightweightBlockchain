package io.mumi.lightweightBlockchain.logic;

import org.junit.Test;

import java.math.BigInteger;


public class BlockchainTests
{
	@Test
	public void testFulfillsDifficulty_LessThanDifficulty_TrueReturned( )
	{
		Blockchain blockchain = new Blockchain( );

		System.out.println( 0x4000 );
	}

	@Test
	public void testFulffilsDifficulty_GreaterThanDifficulty_FalseReturned( )
	{

	}

	@Test
	public void testBigInteger( )
	{
		BigInteger bigint = new BigInteger( "53865969500000000000000000000000000000000" );

		System.out.println( bigint );
	}
}
