package inf.elte.parhalg.example;

import inf.elte.parhalg.connection.PacketProcessor;
import inf.elte.parhalg.connection.Responder;
import inf.elte.parhalg.connection.ServerThread;
import inf.elte.parhalg.packet.FileSendPacket;
import inf.elte.parhalg.packet.BackupDonePacket;
import inf.elte.parhalg.packet.Packet;
import inf.elte.parhalg.packet.StorageWarningPacket;
import inf.elte.parhalg.serverstorage.FreeSpaceListener;
import inf.elte.parhalg.serverstorage.USBWatcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExampleServer implements PacketProcessor, FreeSpaceListener {

	private static final Logger LOG = Logger.getAnonymousLogger();

	private final Path mountPoint;

	public ExampleServer(Path mountPoint, int port) throws IOException {
		this.mountPoint = mountPoint;
		new USBWatcher(mountPoint, this);
		new ServerThread(this, port).start();
	}

	@Override
	public void freeSpaceWarningCallback(long freeSpace) {
		Responder.broadcast(new StorageWarningPacket());
	}

	@Override
	public void process(Responder responder, Packet packet) {
		System.out.println(packet);
		switch (packet.getType()) {
		case FILE_SEND:
			FileSendPacket filesend = (FileSendPacket) packet;
			try {
				Path backupPath = mountPoint.resolve(filesend.getBackupName()).resolve(filesend.getRelative());
				if (!backupPath.startsWith(mountPoint)) {
					LOG.log(Level.SEVERE, "Backup path not in mount point!", filesend);
					return;
				}
				Files.createDirectories(backupPath.getParent());
				Files.write(backupPath, filesend.getData());
				responder.send(new BackupDonePacket(filesend.getRoot(),filesend.getRelative(),filesend.getBackupName()));
			} catch (IOException ex) {
				LOG.log(Level.SEVERE, "Could not write file...", ex);
			}
			break;
		case CLOSE:
			try {
				responder.close();
			} catch (IOException e) {
				// ignored
			}
			break;
		default:
		}
	}

	private static void printHelp() {
		System.out.println("Usage:");
		System.out.println("ExampleServer [backup mount point] [port to listen]");
	}

	public static void main(String[] args) throws IOException {
		if (args.length < 2) {
			printHelp();
			return;
		}

		String mountPoint = args[0];
		int port = Integer.parseInt(args[1]);

		Path mountPointPath = Paths.get(mountPoint);
		if (!Files.exists(mountPointPath)) {
			System.err.println("The mount point doesn't exists!");
			return;
		}

		new ExampleServer(mountPointPath, port);
	}

}
