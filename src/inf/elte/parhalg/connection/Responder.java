package inf.elte.parhalg.connection;

import inf.elte.parhalg.packet.Packet;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

import java.net.Socket;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.lang.RuntimeException;

import javax.crypto.SealedObject;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.CipherOutputStream;
import javax.crypto.CipherInputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.BadPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

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

	private final SecretKey key64;
	private final Cipher ecipher;
	private final Cipher dcipher;

	public Responder(Socket socket, PacketProcessor processor) throws IOException {
		key64 = new SecretKeySpec( new byte[] { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07 }, "Blowfish" );
		try{
			ecipher = Cipher.getInstance( "Blowfish" );
			ecipher.init( Cipher.ENCRYPT_MODE, key64 );
	
			dcipher = Cipher.getInstance( "Blowfish" );
			dcipher.init( Cipher.DECRYPT_MODE, key64 );
		} catch(InvalidKeyException ex){
			throw new RuntimeException("Cipher init failed!",ex);
		} catch(NoSuchAlgorithmException ex){
			throw new RuntimeException("Cipher init failed!",ex);
		} catch(NoSuchPaddingException ex){
			throw new RuntimeException("Cipher init failed!",ex);
		}

		this.socket = socket;
		this.oos = new ObjectOutputStream(new CipherOutputStream(new BufferedOutputStream(socket.getOutputStream()),ecipher));
		this.processor = processor;
	}

	@Override
	public void run() {
		RESPONDERS.add(this);
		try (ObjectInputStream ois = new ObjectInputStream(new CipherInputStream(new BufferedInputStream(socket.getInputStream()),dcipher))) {
			while (socket.isConnected()) {
				SealedObject sealedObject = (SealedObject)ois.readObject();
				Object obj = sealedObject.getObject( dcipher );
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
		} catch (IllegalBlockSizeException ex) {
			LOG.log(Level.SEVERE, "Exception occured while encrypting data...", ex);
		} catch (BadPaddingException ex) {
			LOG.log(Level.SEVERE, "Exception occured while encrypting data...", ex);
		}finally {
			RESPONDERS.remove(this);
		}
	}

	public void send(Packet packet) throws IOException {
		try{
			oos.writeObject(new SealedObject( packet, ecipher));
		}catch(IllegalBlockSizeException ex){
			LOG.log(Level.SEVERE, "Exception occured while encrypt data...", ex);		
		}
	}

	public void close() throws IOException {
		socket.shutdownOutput();
		socket.shutdownInput();
		socket.close();
	}

}
