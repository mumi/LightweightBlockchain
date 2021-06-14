
package io.mumi.lightweightBlockchain.api.services;

import io.mumi.lightweightBlockchain.logic.DependencyManager;
import io.mumi.lightweightBlockchain.models.Block;
import io.mumi.lightweightBlockchain.models.Transaction;
import io.mumi.lightweightBlockchain.utils.SHA3Helper;
import io.mumi.lightweightBlockchain.utils.VerificationHelper;
import io.mumi.lightweightBlockchain.utils.merkle.MerkleTree;
import org.apache.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Path("transactions")
public class TransactionService
{
	private static Logger logger = Logger.getLogger( TransactionService.class );

	@Context
	UriInfo uriInfo;

	@POST
	@Consumes( MediaType.APPLICATION_JSON )
	public Response sendTransaction( Transaction transaction )
	{
		if ( VerificationHelper.verifyTransaction( transaction ) )
		{
			DependencyManager.getPendingTransactions( ).addPendingTransaction( transaction );
			DependencyManager.getMiner( ).cancelBlock( );

			logger.info( transaction );

			try
			{
				DependencyManager.getBlockchainNetwork( ).sendTransaction( transaction );
				return Response.created(
					new URI( uriInfo.getRequestUriBuilder( ).path( transaction.getTxIdAsString( ) ).toString( ) ) )
							   .build( );
			}
			catch ( URISyntaxException e )
			{
				throw new WebApplicationException( "Uri for transaction could not be generated" );
			}
			catch ( Exception e )
			{
				throw new WebApplicationException( );
			}
		}
		else
		{
			logger.error( "transaction verification failed" );
			throw new WebApplicationException( 422 );
		}
	}

	@GET
	@Produces( MediaType.APPLICATION_JSON )
	@Path("{hash}")
	public Response getTransactionByHash( @PathParam( "hash" ) String hex )
	{
		Transaction trx = DependencyManager.getBlockchain( ).getTransactionByHash( hex );

		Response response;

		if ( trx == null )
		{
			response = Response.status( 404 ).build( );
		}
		else
		{
			response = Response.ok( trx )
							   .header( "Link",
								   uriInfo.getBaseUriBuilder( )
										  .path( "blocks" )
										  .path( SHA3Helper.digestToHex( trx.getBlockId( ) ) ).build( ) )
							   .build( );
		}

		return response;
	}

	@GET
	@Produces( MediaType.APPLICATION_JSON)
	public Response getRecentTransactions( @QueryParam( "size" ) @DefaultValue( "10" ) int size,
		@QueryParam( "offset" ) @DefaultValue( "0" ) int offset )
	{
		List<Transaction> transactions = new ArrayList<>( );

		for ( Block latestBlock : DependencyManager.getBlockchain( ).getLatestBlocks( size, offset ) )
		{
			for ( Transaction transaction : latestBlock.getTransactions( ) )
			{
				transactions.add( transaction );
			}
		}

		return Response.ok( transactions ).build( );
	}

	@GET
	@Produces( MediaType.APPLICATION_JSON )
	@Path( "{hash}/merkle" )
	public Response getMerkleTreeHashesForTransaction( @PathParam( "hash" ) String hex )
	{
		Transaction trx = DependencyManager.getBlockchain( ).getTransactionByHash( hex );

		Response response;

		if ( trx == null )
		{
			response = Response.status( 404 ).build( );
		}
		else
		{
			Block block = DependencyManager.getBlockchain( ).getBlockByHash( trx.getBlockId( ) );
			MerkleTree merkleTree = new MerkleTree( block.getTransactions( ) );
			response = Response.ok( merkleTree.getHashesForTransactionHash( trx.getTxId( ) ) )
							   .build( );
		}

		return response;
	}
}
