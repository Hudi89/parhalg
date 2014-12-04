package inf.elte.parhalg.connection;

import inf.elte.parhalg.packet.Packet;

public interface PacketProcessor {
	
	public static final PacketProcessor PRINT_PROCESSOR = new PacketProcessor() {
		@Override
		public void process(Responder responder, Packet packet) {
			System.out.println(packet);
		}
	};
	
	public void process(Responder responder, Packet packet);

}
