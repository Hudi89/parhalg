package inf.elte.parhalg.example;

import inf.elte.parhalg.serverstorage.FreeSpaceListener;
import inf.elte.parhalg.serverstorage.USBWatcher;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ExampleUsbWatcher {
	public static void main(String[] args) throws IOException, InterruptedException {
//		Path path = Paths.getPath("/media/brian/MYLINUXLIVE");
		Path path = Paths.get("C:\\");
		new USBWatcher(path, FreeSpaceListener.PRINT_LISTENER);
		Thread.sleep(1000000);
	}
}
