package inf.elte.parhalg.watcher;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DirectoryChangeWatcher extends Thread {

	private static final Logger LOG = Logger.getAnonymousLogger();

	private static final Kind<?>[] WATCHED_KIND = new Kind<?>[] { ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY };

	private final Path root;

	private final DirectoryChangeListener listener;

	private final boolean recursive;

	public DirectoryChangeWatcher(Path root, DirectoryChangeListener listener, boolean recursive) {
		this.root = root;
		this.listener = listener;
		this.recursive = recursive;
		this.setDaemon(true);
	}

	@Override
	public void run() {
		try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
			// Register directories
			final HashMap<WatchKey, Path> watchedDirs = new HashMap<>();
			if (recursive) {
				Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
						LOG.log(Level.INFO, "Watching subdirectory of " + root + ": " + dir);
						watchedDirs.put(dir.register(watcher, WATCHED_KIND), dir);
						return FileVisitResult.CONTINUE;
					}
				});
			} else {
				LOG.log(Level.INFO, "Watching directory: " + root);
				watchedDirs.put(root.register(watcher, WATCHED_KIND), root);
			}
			
			for (;;) {
				WatchKey key = watcher.take();
				Path dir = watchedDirs.get(key);
				for (WatchEvent<?> event : key.pollEvents()) {
					if (event.kind() == ENTRY_CREATE) {
						Path relative = root.relativize(dir.resolve((Path) event.context()));
						listener.onEntryCreate(root, relative);
					} else if (event.kind() == ENTRY_MODIFY) {
						Path relative = root.relativize(dir.resolve((Path) event.context()));
						listener.onEntryModify(root, relative);
					} else if (event.kind() == ENTRY_DELETE) {
						Path relative = root.relativize(dir.resolve((Path) event.context()));
						listener.onEntryDelete(root, relative);
					} else if (event.kind() == OVERFLOW) {
						listener.onOverflow();
					}
				}
				key.reset();
			}
		} catch (IOException | InterruptedException ex) {
			LOG.log(Level.SEVERE, "DirectoryChangeListener unexpectedly halted!", ex);
		}
	}
}
