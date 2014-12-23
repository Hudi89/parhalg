package inf.elte.parhalg.clientgui;

import java.io.File;

public interface GuiEventListener {

	public void connectionRequest(String address, int port);

	public void addDirectoryRequest(File directory);

	public void closeGUI();

}
