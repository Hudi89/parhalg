package inf.elte.parhalg.watcher;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class DirFigyelo {
	
	/**
	 * @param dir path to watch
	 * @param period time between fs checks in seconds.
	 * @return blocking queue with new elements supplied every `period` seconds.
	 */
	public static BlockingQueue<Path> changedFiles(final Path dir, int period){
		assert(period > 0);
		final BlockingQueue<Path> q = new LinkedBlockingDeque<>();
		try {
			final WatchService ws = FileSystems.getDefault().newWatchService();
			final WatchKey wk = dir.register(ws, ENTRY_CREATE, ENTRY_MODIFY);
			final TimerTask r = new TimerTask(){
				public void run() {
					Set<Path> s = new HashSet<>();				
					for(final WatchEvent<?> e : wk.pollEvents())
							s.add((Path) e.context());
					q.addAll(s);
				}
			};
			Timer t = new Timer();
			t.schedule(r, 0, period*1000);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return q;
	}
	
	/**
	 * @param dir to watch
	 * @return Blocking queue of created/modified paths. use .take() in a main loop to get vals.
	 */
	public static BlockingQueue<Path> changedFiles(final Path dir){
		final BlockingQueue<Path> q = new ArrayBlockingQueue<>(256);
		final Runnable r = new Runnable(){
			public void run(){
				try {
					final WatchService ws = FileSystems.getDefault().newWatchService();
					final WatchKey wk = dir.register(ws, ENTRY_CREATE, ENTRY_MODIFY);
					for(;;)
						for(final WatchEvent<?> e : wk.pollEvents())
							q.put((Path) e.context());
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		};
		Thread t = new Thread(r);
		t.setDaemon(true);
		t.start();
		return q;
	}
}
