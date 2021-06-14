package io.mumi.lightweightBlockchain.models;

import io.mumi.lightweightBlockchain.utils.SHA3Helper;
import org.junit.Test;


public class TransactionTests
{
	@Test
	public void test()
	{
		Transaction t = new Transaction( "sender", "receiver", 1.5, 0, 0.0000005, 0.000001 );
		Transaction t1 = new Transaction( "fdjkngliuabglbaubf", "fahgibgafadbvbahvbab", 1.5, 0, 0.0000005,
			0.000001 );
		Transaction t2 = new Transaction( "sender", "aslkjdfhalbu fviueabuhbiuvbfvbav", 1.5, 0, 0.0000005,
			0.000001 );
		System.out.println( t.getTxId( ) );
		System.out.println( t1.getTxId( ) );
		System.out.println( SHA3Helper.digestToHex( t2.getTxId( ) ) );

		System.out.println( SHA3Helper.hash256( t ).length );
	}
}
