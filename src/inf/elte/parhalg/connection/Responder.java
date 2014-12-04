package inf.elte.parhalg.connection;

import inf.elte.parhalg.packet.Packet;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Responder extends Thread implements AutoCloseable {

	private static final Logger LOG = Logger.getAnonymousLogger();

	private static final CopyOnWriteArraySet<Responder> RESPONDERS = new CopyOnWriteArraySet<>();

	public static void broadcast(Packet packet) {
		for (Responder responder : RESPONDERS) {
			try {
				responder.send(packet);
			} catch (IOException ex) {
				LOG.log(Level.SEVERE, "Exception occured while broadcasting packet...", ex);
			}
		}
	}

	private final Socket socket;

	private final ObjectOutputStream oos;

	private final PacketProcessor processor;

	public Responder(Socket socket, PacketProcessor processor) throws IOException {
		this.socket = socket;
		this.oos = new ObjectOutputStream(socket.getOutputStream());
		this.processor = processor;
	}

	@Override
	public void run() {
		RESPONDERS.add(this);
		try (ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {
			while (socket.isConnected()) {
				Object obj = ois.readObject();
				if (obj instanceof Packet) {
					Packet packet = (Packet) obj;
					LOG.log(Level.INFO, "New " + packet.getType() + " packet received!");
					processor.process(this, packet);
				} else {
					LOG.log(Level.SEVERE, "Unknown packet received! Discarding...");
				}
			}
		} catch (EOFException ex) {
			LOG.log(Level.INFO, "The the connection is now closed...");
		} catch (ClassNotFoundException ex) {
			LOG.log(Level.SEVERE, "Could not read packet...", ex);
		} catch (IOException ex) {
			LOG.log(Level.SEVERE, "Exception occured while processing connection...", ex);
		} finally {
			RESPONDERS.remove(this);
		}
	}

	public void send(Packet packet) throws IOException {
		oos.writeObject(packet);
	}

	public void close() throws IOException {
		socket.shutdownOutput();
		socket.shutdownInput();
		socket.close();
	}

}
