
package io.mumi.lightweightBlockchain.utils;

import io.mumi.lightweightBlockchain.persistence.AccountPersistence;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DLSequence;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.ECPointUtil;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.jce.spec.ECParameterSpec;

import java.math.BigInteger;
import java.security.*;
import java.security.spec.ECPoint;
import java.security.spec.ECPrivateKeySpec;
import java.security.spec.ECPublicKeySpec;
import java.util.Arrays;

public class SignatureHelper
{
	private static final String CURVE_NAME = "secp256k1";

	private static final String ALGORITHM = "ECDSA";

	private static final String PROVIDER = "BC";

	private static final X9ECParameters CURVE = SECNamedCurves.getByName( CURVE_NAME );

	private static final ECDomainParameters DOMAIN =
		new ECDomainParameters( CURVE.getCurve( ), CURVE.getG( ), CURVE.getN( ), CURVE.getH( ) );

	public static boolean verify( byte[] hash, byte[] signature, byte[] publicKey )
	{
		boolean result;

		try (ASN1InputStream asn1 = new ASN1InputStream( signature ))
		{
			ECDSASigner signer = new ECDSASigner( );
			signer.init( false, new ECPublicKeyParameters( CURVE.getCurve( ).decodePoint( publicKey ), DOMAIN ) );

			DLSequence seq = ( DLSequence ) asn1.readObject( );
			BigInteger r = ( ( ASN1Integer ) seq.getObjectAt( 0 ) ).getPositiveValue( );
			BigInteger s = ( ( ASN1Integer ) seq.getObjectAt( 1 ) ).getPositiveValue( );
			result = signer.verifySignature( hash, r, s );
		}
		catch ( Exception e )
		{
			result = false;
		}

		return result;
	}

	public static KeyPair generateKeyPair( )
	{
		KeyPair keyPair;

		try
		{
			Security.addProvider( new org.bouncycastle.jce.provider.BouncyCastleProvider( ) );
			ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec( CURVE_NAME );
			KeyPairGenerator g = KeyPairGenerator.getInstance( ALGORITHM, PROVIDER );
			g.initialize( ecSpec, new SecureRandom( ) );
			keyPair = g.generateKeyPair( );
		}
		catch ( Exception e )
		{
			keyPair = null;
		}

		return keyPair;
	}

	public static void saveKeyPair( KeyPair keyPair, String minerId )
	{
		AccountPersistence accountPersistence = new AccountPersistence( );
		accountPersistence.saveKeyPair( keyPair, minerId );
	}

	public static PublicKey getPublicKeyFromBytes( byte[] publicKey )
	{
		PublicKey result;

		try
		{
			ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec( CURVE_NAME );
			KeyFactory kf = KeyFactory.getInstance( ALGORITHM, PROVIDER );
			ECNamedCurveSpec params =
				new ECNamedCurveSpec( CURVE_NAME, spec.getCurve( ), spec.getG( ), spec.getN( ) );
			ECPoint point = ECPointUtil.decodePoint( params.getCurve( ), publicKey );
			ECPublicKeySpec pubKeySpec = new ECPublicKeySpec( point, params );
			result = kf.generatePublic( pubKeySpec );
		}
		catch ( Exception e )
		{
			result = null;
		}

		return result;
	}

	public static byte[] getCoinbaseFromPublicKey( KeyPair keyPair )
	{
		return getCoinbaseFromPublicKey( keyPair.getPublic( ) );
	}

	public static byte[] getCoinbaseFromPublicKey( PublicKey publicKey )
	{
		return Arrays.copyOfRange( publicKey.getEncoded( ), 23, 88 );
	}

	public static PrivateKey getPrivateKeyFromECBigIntAndCurve( BigInteger s )
	{
		PrivateKey result;

		ECNamedCurveParameterSpec spec = ECNamedCurveTable.getParameterSpec( CURVE_NAME );
		ECNamedCurveSpec ecCurveSpec =
			new ECNamedCurveSpec( CURVE_NAME, spec.getCurve( ), spec.getG( ), spec.getN( ), spec.getH( ), spec
				.getSeed( ) );
		ECPrivateKeySpec privateKeySpec = new ECPrivateKeySpec( s, ecCurveSpec );
		try
		{
			KeyFactory keyFactory = KeyFactory.getInstance( ALGORITHM, PROVIDER );
			result = keyFactory.generatePrivate( privateKeySpec );
		}
		catch ( Exception e )
		{
			result = null;
		}

		return result;
	}
}
