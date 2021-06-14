
package io.mumi.lightweightBlockchain.p2p.adapters;

import io.mumi.lightweightBlockchain.models.Block;
import io.mumi.lightweightBlockchain.models.Chain;
import com.owlike.genson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChainAdapter
{
	private Chain chain;

	public ChainAdapter( )
	{
		this.chain = new Chain( );
	}

	public ChainAdapter( Chain chain )
	{
		this.chain = chain;
	}

	@JsonIgnore
	public Chain getChainObject( )
	{
		return chain;
	}

	public List<BlockAdapter> getChain( )
	{
		List<BlockAdapter> blocks = new ArrayList<>( );

		for ( Block block : chain.getChain( ) )
		{
			blocks.add( new BlockAdapter( block ) );
		}

		return blocks;
	}

	public void setChain( List<BlockAdapter> chain )
	{
		CopyOnWriteArrayList<Block> blocks = new CopyOnWriteArrayList<>( );

		for ( BlockAdapter blockAdapter : chain )
		{
			blocks.add( blockAdapter.getBlock( ) );
		}

		this.chain.setChain( blocks );
	}

	public int getNetworkId( )
	{
		return chain.getNetworkId( );
	}

	public void setNetworkId( int networkId )
	{
		this.chain.setNetworkId( networkId );
	}
}
