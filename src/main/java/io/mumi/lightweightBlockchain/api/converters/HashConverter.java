
package io.mumi.lightweightBlockchain.api.converters;

import io.mumi.lightweightBlockchain.utils.SHA3Helper;
import com.owlike.genson.Context;
import com.owlike.genson.Converter;
import com.owlike.genson.stream.ObjectReader;
import com.owlike.genson.stream.ObjectWriter;

public class HashConverter implements Converter<byte[]>
{
	@Override public void serialize( byte[] bytes, ObjectWriter objectWriter, Context context ) throws Exception
	{
		objectWriter.writeString( SHA3Helper.digestToHex( bytes ) );
	}

	@Override public byte[] deserialize( ObjectReader objectReader, Context context ) throws Exception
	{
		return SHA3Helper.hexToDigest( objectReader.valueAsString( ) );
	}

}
