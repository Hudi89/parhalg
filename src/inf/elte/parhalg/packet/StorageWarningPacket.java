package inf.elte.parhalg.packet;

public class StorageWarningPacket extends Packet {

	private static final long serialVersionUID = -2371008243677195717L;

	public StorageWarningPacket() {
		super(PacketType.STORAGE_WARNING);
	}

	@Override
	public String toString() {
		return "StorageWarningPacket []";
	}

}
