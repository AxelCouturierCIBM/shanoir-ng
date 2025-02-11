package org.shanoir.uploader.action.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This concrete state class defines the state where application startup
 * has failed due to the fact that Shanoir remote server is unreachable:
 * This state is a dead end.
 * 
 * @author mkain
 * @author atouboulic
 * 
 */
public class ServerUnreachableState implements State {

	private static final Logger logger = LoggerFactory.getLogger(ServerUnreachableState.class);
	
	public void load(StartupStateContext context) {
		context.getShUpStartupDialog().updateStartupText("\nShanoir server unreachable, ShanoirUploader stopped.");
		logger.error("Shanoir Server not reachable.");
	}

}
