package inf.elte.parhalg.clientgui;

import inf.elte.parhalg.connection.PacketProcessor;
import inf.elte.parhalg.connection.Responder;
import inf.elte.parhalg.packet.ClosePacket;
import inf.elte.parhalg.packet.FilesendPacket;
import inf.elte.parhalg.packet.HelloPacket;
import inf.elte.parhalg.watcher.DirFigyelo;

import java.io.File;
import java.net.Socket;
import java.nio.file.Path;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

import javax.swing.SwingUtilities;

public class Gui implements GuiEventListener {

	private final ConnectionFrame connectionFrame;

	private final FolderFrame folderFrame;

	private Responder responder;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Gui().start();
			}
		});
	}

	public Gui() {
		this.connectionFrame = new ConnectionFrame(this);
		this.folderFrame = new FolderFrame(this);
	}

	public void start() {
		connectionFrame.setVisible(true);
	}

	@Override
	public void closeGUI() {
		try {
			responder.send(new ClosePacket());
			responder.close();
		} catch (Exception e) {
			// ignored
		}
	}

	@Override
	public void connectionRequest(String address, int port) {
		try {
			responder = new Responder(new Socket(address, port), PacketProcessor.PRINT_PROCESSOR);
			responder.send(new HelloPacket());
		} catch (Exception e) {
			// ignored
		}
		connectionFrame.setVisible(false);
		folderFrame.setVisible(true);
	}

	@Override
	public void addDirectoryRequest(File directory) {
		final Path path = directory.toPath();
		final Runnable r = new Runnable() {
			public void run() {
				BlockingQueue<Path> changes = DirFigyelo.changedFiles(path);
				while (true) {
					try {
						Path changedFile = changes.take();
						FilesendPacket packet = FilesendPacket.createFromPath(path, changedFile, "testClient");
						responder.send(packet);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		Thread t = new Thread(r);
		t.setDaemon(true);
		t.start();
		folderFrame.addFolder(directory, new Date(), 0, "new");
		folderFrame.updateFolder(directory, null, 1, null);
	}

}
