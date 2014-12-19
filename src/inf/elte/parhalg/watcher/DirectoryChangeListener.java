package inf.elte.parhalg.watcher;

import java.nio.file.Path;

public interface DirectoryChangeListener {
	
	public void onEntryCreate(Path root, Path relative);
	
	public void onEntryModify(Path root, Path relative);
	
	public void onEntryDelete(Path root, Path relative);
	
	public void onOverflow();

}
