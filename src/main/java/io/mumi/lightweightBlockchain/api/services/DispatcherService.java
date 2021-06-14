
package io.mumi.lightweightBlockchain.api.services;

import io.mumi.lightweightBlockchain.accounts.Account;
import io.mumi.lightweightBlockchain.logic.DependencyManager;
import io.mumi.lightweightBlockchain.models.Block;
import io.mumi.lightweightBlockchain.models.Transaction;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public class DispatcherService
{
	@GET
	@Produces( MediaType.APPLICATION_JSON )
	@Path( "{hash}" )
	public Response searchForHash( @PathParam( "hash" ) String hex )
	{
		Response response = null;

		if ( hex.length( ) == 130 )
		{
			Account account = DependencyManager.getAccountStorage( ).getAccount( hex );

			if ( account == null )
			{
				response = Response.status( 404 ).build( );
			}
			else
			{
				response = Response.ok( account ).build( );
			}
		}
		else
		{
			Transaction transaction = null;
			Block block = DependencyManager.getBlockchain( ).getBlockByHash( hex );

			if ( block == null )
			{
				transaction = DependencyManager.getBlockchain( ).getTransactionByHash( hex );

				if ( transaction == null )
				{
					response = Response.status( 404 ).build( );
				}
				else
				{
					response = Response.ok( transaction ).build( );
				}
			}
			else
			{
				response = Response.ok( block ).build( );
			}
		}

		return response;
	}
}
