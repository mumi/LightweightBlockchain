
package io.mumi.lightweightBlockchain.persistence;

import io.mumi.lightweightBlockchain.utils.SHA3Helper;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;

public class AccountPersistence
{
	private Charset encoding = StandardCharsets.UTF_8;

	private String path = "accounts/";

	public AccountPersistence( )
	{
		File file = new File( path );
		if ( !file.exists( ) )
		{
			file.mkdir( );
		}
	}

	public void saveKeyPair( KeyPair keyPair, String minerId )
	{
		File file = new File( path + minerId + ".json" );

		try (OutputStreamWriter outputStream = new OutputStreamWriter( new FileOutputStream( file ), encoding ) )
		{
			outputStream.write( "{\"publicKey\":\"" );
			outputStream.write( SHA3Helper.digestToHex( keyPair.getPublic( ).getEncoded( ) ).substring( 46 ) );
			outputStream.write( "\",\n\"privateKey\":\"" );
			outputStream.write(
				SHA3Helper.digestToHex( ( ( BCECPrivateKey ) keyPair.getPrivate( ) ).getD( ).toByteArray( ) ) );
			outputStream.write( "\"}" );
			outputStream.flush( );
		}
		catch (IOException e)
		{
			e.printStackTrace( );
		}
	}
}
