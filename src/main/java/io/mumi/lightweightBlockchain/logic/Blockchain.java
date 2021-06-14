
package io.mumi.lightweightBlockchain.logic;

import io.mumi.lightweightBlockchain.models.Block;
import io.mumi.lightweightBlockchain.models.Chain;
import io.mumi.lightweightBlockchain.models.Transaction;
import io.mumi.lightweightBlockchain.utils.SHA3Helper;
import io.mumi.lightweightBlockchain.utils.VerificationHelper;
import org.apache.log4j.Logger;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Blockchain
{
	private static Logger logger = Logger.getLogger( Blockchain.class );

	public final static int MAX_BLOCK_SIZE_IN_BYTES = 1048576;

	public final static int NETWORK_ID = 1;

	public final static double BLOCK_REWARD = 50.0;

	public final static int VERSION = 1;

	public final static int REQUIRED_BLOCK_CONFIRMATIONS = 1;

	public final static int TRANSACTION_FEE_UNITS = 10;

	private BigInteger difficulty;

	private List<Chain> altChains;

	private Block bestBlock;

	private Chain chain;

	private Map<String, Block> blockCache;

	private Map<String, Transaction> transactionCache;

	public Blockchain( )
	{
		this.altChains = new CopyOnWriteArrayList<>( );
		this.chain = new Chain( NETWORK_ID );
		this.altChains.add( chain );
		this.bestBlock = this.chain.getLast( );
		this.blockCache = new ConcurrentHashMap<>( );
		this.blockCache.put( SHA3Helper.digestToHex( getGenesisBlock( ).getBlockHash( ) ), getGenesisBlock( ) );
		this.transactionCache = new ConcurrentHashMap<>( );
		this.difficulty = new BigInteger(
			"-57896000000000000000000000000000000000000000000000000000000000000000000000000" );
	}

	public Blockchain( BigInteger difficulty, List<Chain> altChains )
	{
		this.difficulty = difficulty;
		this.altChains = altChains;

		this.blockCache = new ConcurrentHashMap<>( );
		this.transactionCache = new ConcurrentHashMap<>( );

		int max = 0;
		Chain chain = null;

		for ( Chain altChain : altChains )
		{
			if ( max < altChain.size( ) )
			{
				max = altChain.size( );
				chain = altChain;
			}

			for ( Block block : altChain.getChain( ) )
			{
				this.blockCache.put( SHA3Helper.digestToHex( block.getBlockHash( ) ), block );

				for ( Transaction transaction : block.getTransactions( ) )
				{
					this.transactionCache.put( transaction.getTxIdAsString( ), transaction );
				}
			}
		}

		this.chain = chain;
		this.bestBlock = chain.getLast( );
	}

	public synchronized void addBlock( Block block )
	{
		if ( VerificationHelper.verifyBlock( block ) )
		{
			byte[] previousBlockHash = block.getBlockHeader( ).getPreviousHash( );

			if ( previousBlockIsBestBlock( previousBlockHash ) )
			{
				logger.debug( "previous block was best block" );
				block.setBlockNumber( chain.size( ) );
				chain.add( block );
				bestBlock = block;
				DependencyManager.getPendingTransactions( ).clearPendingTransactions( block );
			}
			else
			{
				logger.debug( "previous block wasnt best block: available Chains: " + altChains.size( ) );
				checkAltChains( previousBlockHash, block );
			}

			blockCache.put( SHA3Helper.digestToHex( block.getBlockHash( ) ), block );

			for ( Transaction transaction : block.getTransactions( ) )
			{
				transactionCache.put( transaction.getTxIdAsString( ), transaction );
			}

			DependencyManager.getAccountStorage( ).parseBlock( block );
		}
	}

	private boolean previousBlockIsBestBlock( byte[] blockHash )
	{
		return Arrays.equals( DependencyManager.getBlockchain( ).getPreviousHash( ), blockHash );
	}

	private void checkAltChains( byte[] previousBlockHash, Block block )
	{
		boolean isNoBlockOfAltChain = true;
		for ( Chain altChain : altChains )
		{
			if ( Arrays.equals( altChain.getLast( ).getBlockHash( ), previousBlockHash ) )
			{
				logger.debug( "using alt chain" );
				block.setBlockNumber( altChain.size( ) );
				altChain.add( block );
				switchChainsIfNecessary( altChain );
				isNoBlockOfAltChain = false;
				break;
			}
		}

		if ( isNoBlockOfAltChain )
		{
			createNewAltChain( previousBlockHash, block );
		}
	}

	private void createNewAltChain( byte[] previousBlockHash, Block block )
	{
		logger.debug( "new chain created" );

		Chain chain = getChainForBlock( getBlockByHash( previousBlockHash ) );

		for ( int i = chain.getChain( ).size( ) - 1; i >= 0; i-- )
		{
			if ( Arrays.equals( chain.get( i ).getBlockHash( ), previousBlockHash ) )
			{
				List<Block> newChain = new CopyOnWriteArrayList<>( chain.getChain( ).subList( 0, i + 1 ) );
				block.setBlockNumber( newChain.size( ) );
				newChain.add( block );
				Chain newChainChain = new Chain( NETWORK_ID, newChain );
				altChains.add( newChainChain );
				i = -1;

				switchChainsIfNecessary( newChainChain );
			}
		}
	}

	private void switchChainsIfNecessary( Chain chain )
	{
		logger.debug( "chains switched" );

		if ( chain.size( ) > this.chain.size( ) )
		{
			correctPendingTransactions( this.chain, chain );
			this.chain = chain;
			this.bestBlock = chain.getLast( );
		}
	}

	private void correctPendingTransactions( Chain previousChain, Chain chain )
	{
		int index = getIndexOfFork( previousChain, chain );

		Set<Transaction> transactionsToRemove = new HashSet<>( );
		for ( int i = index; i < chain.size( ); i++ )
		{
			transactionsToRemove.addAll( chain.get( i ).getTransactions( ) );
		}

		Set<Transaction> transactionToInsert = new HashSet<>( );
		for( int i = index; i < previousChain.size(); i++ )
		{
			for ( Transaction transaction : previousChain.get( i ).getTransactions( ) )
			{
				if ( !transactionsToRemove.contains( transaction ) )
				{
					transactionToInsert.add( transaction );
				}
			}
		}

		DependencyManager.getPendingTransactions( ).clearPendingTransactions( transactionsToRemove );
		DependencyManager.getPendingTransactions( ).addPendingTransactions( transactionToInsert );
	}

	private int getIndexOfFork( Chain previousChain, Chain chain )
	{
		int index = -1;

		for(int i = previousChain.size() - 1; i >= 0; i--)
		{
			index = chain.getChain( ).indexOf( previousChain.get( i ) );
			if ( index > -1 )
			{
				break;
			}
		}

		return ( index > -1 ) ? (index + 1) : 0;
	}

	public boolean fulfillsDifficulty( byte[] digest )
	{
		BigInteger temp = new BigInteger( digest );

		return temp.compareTo( difficulty ) <= 0;
	}

	public BigInteger getDifficulty( )
	{
		return difficulty;
	}

	public void setDifficulty( BigInteger difficulty )
	{
		this.difficulty = difficulty;
	}

	public byte[] getPreviousHash( )
	{
		return bestBlock.getBlockHash( );
	}

	public Block getGenesisBlock( )
	{
		return chain.get( 0 );
	}

	public int size( )
	{
		return chain.size( );
	}

	public List<Chain> getAltChains( )
	{
		return altChains;
	}

	public Chain getChain( )
	{
		return chain;
	}

	public Transaction getTransactionByHash( String hex )
	{
		return transactionCache.get( hex );
	}

	public Block getBlockByHash( String hex )
	{
		return blockCache.get( hex );
	}

	public Block getBlockByHash( byte[] hash )
	{
		return blockCache.get( SHA3Helper.digestToHex( hash ) );
	}

	public Block getLatestBlock( )
	{
		return bestBlock;
	}

	public List<Block> getLatestBlocks( int size, int offset )
	{
		List<Block> blocks = new ArrayList<>( );

		Block block = this.getLatestBlock( );

		for ( int i = 0; i < ( size + offset ); i++ )
		{
			if ( block != null )
			{
				if ( i >= offset )
				{
					blocks.add( block );
				}

				String previousHash = SHA3Helper.digestToHex( block.getBlockHeader( ).getPreviousHash( ) );
				block = this.getBlockByHash( previousHash );
			}
		}

		return blocks;
	}

	public Block getChildOfBlock( Block block )
	{
		Block result = null;

		Chain chain = getChainForBlock( block );

		if ( chain != null )
		{
			result = chain.get( chain.getChain( ).indexOf( block ) + 1 );
		}

		return result;
	}

	private Chain getChainForBlock( Block block )
	{
		Chain result = null;

		int index = chain.getChain( ).indexOf( block );

		if ( index == -1 )
		{
			for ( Chain altChain : altChains )
			{
				if ( altChain.getChain( ).indexOf( block ) > -1 )
				{
					result = altChain;
				}
			}
		}
		else
		{
			result = chain;
		}

		return result;
	}
}
