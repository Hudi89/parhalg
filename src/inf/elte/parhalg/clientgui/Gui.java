package inf.elte.parhalg.clientgui;

import inf.elte.parhalg.connection.PacketProcessor;
import inf.elte.parhalg.connection.Responder;
import inf.elte.parhalg.packet.ClosePacket;
import inf.elte.parhalg.packet.FileSendPacket;
import inf.elte.parhalg.packet.HelloPacket;
import inf.elte.parhalg.watcher.DirectoryChangeListener;
import inf.elte.parhalg.watcher.DirectoryChangeWatcher;

import java.io.File;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Gui implements GuiEventListener, DirectoryChangeListener {

	private final ConnectionFrame connectionFrame;

	private final FolderFrame folderFrame;

	private Responder responder;

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(
			         "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException e) {

		} catch (InstantiationException e) {

		} catch (IllegalAccessException e) {

		} catch (UnsupportedLookAndFeelException e) {
			try {
				UIManager.setLookAndFeel(
				         "com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
			} catch (Exception e1) {
			}

		}
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
		new DirectoryChangeWatcher(directory.toPath(), this, true).start();
		folderFrame.addFolder(directory, new Date(), 0, "new");
		folderFrame.updateFolder(directory, null, 1, null);
	}

	@Override
	public void onEntryCreate(Path root, Path relative) {
		try {
			if (!Files.isDirectory(root.resolve(relative))) {
				FileSendPacket packet = FileSendPacket.createFromPath(root, relative, "testClient");
				responder.send(packet);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onEntryModify(Path root, Path relative) {
		try {
			if (!Files.isDirectory(root.resolve(relative))) {
				FileSendPacket packet = FileSendPacket.createFromPath(root, relative, "testClient");
				responder.send(packet);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onEntryDelete(Path root, Path relative) {
		// ignored...
	}

	@Override
	public void onOverflow() {
		// ignored...
	}

}
