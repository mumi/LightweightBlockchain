
package io.mumi.lightweightBlockchain.api.converters;

import io.mumi.lightweightBlockchain.utils.SHA3Helper;
import com.owlike.genson.Context;
import com.owlike.genson.Converter;
import com.owlike.genson.stream.ObjectReader;
import com.owlike.genson.stream.ObjectWriter;

import java.util.ArrayList;
import java.util.List;

public class HashListConverter implements Converter<List<byte[]>>
{
	@Override public void serialize( List<byte[]> bytes, ObjectWriter objectWriter, Context context ) throws Exception
	{
		objectWriter.beginArray( );
		for ( byte[] aByte : bytes )
		{
			objectWriter.writeString( SHA3Helper.digestToHex( aByte ) );
		}
		objectWriter.endArray( );
	}

	@Override public List<byte[]> deserialize( ObjectReader objectReader, Context context ) throws Exception
	{
		List<byte[]> hashes = new ArrayList<>( );

		objectReader.beginArray( );

		while ( objectReader.hasNext( ) )
		{
			objectReader.next( );
			hashes.add( SHA3Helper.hexToDigest( objectReader.valueAsString( ) ) );
		}

		objectReader.endArray( );

		return hashes;
	}
}
