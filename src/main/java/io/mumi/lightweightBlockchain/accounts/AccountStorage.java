
package io.mumi.lightweightBlockchain.accounts;

import io.mumi.lightweightBlockchain.logic.Blockchain;
import io.mumi.lightweightBlockchain.logic.DependencyManager;
import io.mumi.lightweightBlockchain.models.Block;
import io.mumi.lightweightBlockchain.models.GenesisBlock;
import io.mumi.lightweightBlockchain.models.Transaction;
import io.mumi.lightweightBlockchain.utils.SHA3Helper;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AccountStorage
{
	private Map<String, Account> accounts;

	private Map<Integer, Account> minerMap;

	private Map<Integer, Block> blockMap;

	public AccountStorage( )
	{
		accounts = new ConcurrentHashMap<>( );
		minerMap = new ConcurrentHashMap<>( );
		blockMap = new ConcurrentHashMap<>( );
		initAccounts( );
	}

	private void initAccounts( )
	{
		for ( Map.Entry<String, Double> accountBalance :
			( (GenesisBlock) DependencyManager.getBlockchain( ).getGenesisBlock( ) ).getAccountBalances( ).entrySet( ) )
		{
			Account account = getAccount( accountBalance.getKey( ) );
			account.addBalance( accountBalance.getValue( ) );
		}

		for ( Block block : DependencyManager.getBlockchain( ).getChain( ).getChain( ) )
		{
			parseBlock( block );
		}
	}

	public void parseBlock( Block block )
	{
		Account account = getAccount( block.getCoinbase( ) );
		account.addMinedBlock( block );
		minerMap.put( block.getBlockNumber( ), account );
		blockMap.put( block.getBlockNumber( ), block );
		releaseBlockedBalances( block.getBlockNumber( ) );
		parseTransactions( block.getTransactions( ) );
	}

	private void releaseBlockedBalances( int blockNumber )
	{
		int key = blockNumber - Blockchain.REQUIRED_BLOCK_CONFIRMATIONS;

		Account account = minerMap.get( key );

		if ( account != null )
		{
			double sumToUnlock = Blockchain.BLOCK_REWARD;

			Block block = blockMap.get( blockNumber );

			if ( block != null )
			{
				for ( Transaction transaction : block.getTransactions( ) )
				{
					sumToUnlock += transaction.getTransactionFee( );
				}

				blockMap.remove( blockNumber );
			}

			account.unlockBalance( sumToUnlock );
			minerMap.remove( blockNumber );
		}
	}

	private void parseTransactions( List<Transaction> transactions )
	{
		for ( Transaction transaction : transactions )
		{
			Account senderAccount = getAccount( transaction.getSender( ) );
			Account receiverAccount = getAccount( transaction.getReceiver( ) );

			senderAccount.addOutgoingTransaction( transaction );
			receiverAccount.addIncomingTransaction( transaction );
		}
	}

	public Account getAccount( String address )
	{
		Account account = accounts.get( address );

		if ( account == null )
		{
			account = createAccount( SHA3Helper.hexToDigest( address ) );
			accounts.put( address, account );
		}

		return account;
	}

	public Account getAccount( byte[] address )
	{
		String addressAsHex = SHA3Helper.digestToHex( address );

		Account account = accounts.get( addressAsHex );

		if ( account == null )
		{
			account = createAccount( address  );
			accounts.put( addressAsHex, account );
		}

		return account;
	}

	private Account createAccount( byte[] address )
	{
		return new Account( address );
	}
}
