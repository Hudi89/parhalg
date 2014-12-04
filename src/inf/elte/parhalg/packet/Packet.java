package inf.elte.parhalg.packet;

public abstract class Packet {

	protected final PacketType type;

	public Packet(PacketType type) {
		this.type = type;
	}

	public PacketType getType() {
		return type;
	}

	@Override
	public String toString() {
		return "Packet [type=" + type + "]";
	}

}
