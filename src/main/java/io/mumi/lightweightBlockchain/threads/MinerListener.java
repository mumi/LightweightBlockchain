
package io.mumi.lightweightBlockchain.threads;

import io.mumi.lightweightBlockchain.models.Block;

public interface MinerListener
{
	void notifyNewBlock( Block block );
}
