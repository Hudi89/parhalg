package inf.elte.parhalg.connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerThread extends Thread {

	private static final Logger LOG = Logger.getAnonymousLogger();

	private final PacketProcessor processor;

	private final int port;

	public ServerThread(PacketProcessor processor, int port) {
		this.processor = processor;
		this.port = port;
	}

	@Override
	public void run() {
		try (ServerSocket socket = new ServerSocket(port)) {
			LOG.log(Level.INFO, "Ready to serve incoming connection!");
			while (!socket.isClosed()) {
				try {
					new Responder(socket.accept(), processor).start();
					LOG.log(Level.INFO, "New incoming connection!");
				} catch (Exception ex) {
					LOG.log(Level.SEVERE, "Unable to establish connection...", ex);
				}
			}
		} catch (IOException ex) {
			LOG.log(Level.SEVERE, "Exception occured while waiting for new incloming connections...", ex);
		}
	}

}
