package inf.elte.parhalg.serverstorage;

public interface FreeSpaceListener {

	public static final FreeSpaceListener PRINT_LISTENER = new FreeSpaceListener() {
		@Override
		public void freeSpaceWarningCallback(long freeSpace) {
			System.out.println("Limit reached, available byes:" + Long.toString(freeSpace));
		}
	};

	void freeSpaceWarningCallback(long freeSpace);
}
