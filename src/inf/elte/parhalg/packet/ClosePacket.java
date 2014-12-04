package inf.elte.parhalg.packet;

public class ClosePacket extends Packet {

	private static final long serialVersionUID = -1921130528762606594L;

	public ClosePacket() {
		super(PacketType.CLOSE);
	}

	@Override
	public String toString() {
		return "ClosePacket []";
	}

}