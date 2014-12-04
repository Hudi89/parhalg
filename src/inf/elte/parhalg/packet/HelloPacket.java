package inf.elte.parhalg.packet;

public class HelloPacket extends Packet {

	private static final long serialVersionUID = 3207002816064073125L;

	public HelloPacket() {
		super(PacketType.HELLO);
	}

	@Override
	public String toString() {
		return "HelloPacket []";
	}

}