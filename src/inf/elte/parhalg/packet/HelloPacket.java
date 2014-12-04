package inf.elte.parhalg.packet;

public class HelloPacket extends Packet {

	public HelloPacket() {
		super(PacketType.HELLO);
	}

	@Override
	public String toString() {
		return "HelloPacket []";
	}

}