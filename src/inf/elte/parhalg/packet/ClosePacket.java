package inf.elte.parhalg.packet;

public class ClosePacket extends Packet {

	public ClosePacket() {
		super(PacketType.CLOSE);
	}

	@Override
	public String toString() {
		return "ClosePacket []";
	}

}