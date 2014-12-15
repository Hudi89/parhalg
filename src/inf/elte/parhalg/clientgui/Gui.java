package inf.elte.parhalg.clientgui;

import java.io.File;
import java.util.Date;

import javax.swing.SwingUtilities;

public class Gui implements GuiEventListener {
	private ConnectionFrame connectionFrame;
	private FolderFrame folderFrame;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Gui().start();
			}
		});
	}
	
	public Gui() {
		connectionFrame = new ConnectionFrame(this);
		folderFrame = new FolderFrame(this);
	}

	public void start() {
		connectionFrame.setVisible(true);
	}

	@Override
	public void connectionRequest(String address, int port) {
		// TODO Ide jön a csatlakozás.
		connectionFrame.setVisible(false);
		folderFrame.setVisible(true);
	}

	@Override
	public void addDirectoryRequest(File path) {
		// TODO Ide jön az új mappa hozzáadása.
		folderFrame.addFolder(path, new Date(), 0, "new");
		folderFrame.updateFolder(path, null, 1, null);
	}

}
