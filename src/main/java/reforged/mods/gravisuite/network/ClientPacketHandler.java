package reforged.mods.gravisuite.network;

import cpw.mods.fml.common.network.Player;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import reforged.mods.gravisuite.ClientTickHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class ClientPacketHandler extends ServerPacketHandler {

    @Override
    public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
        DataInputStream dataStream = new DataInputStream(new ByteArrayInputStream(packet.data));
        try {
            String typePacket = dataStream.readUTF();
            if (typePacket.equalsIgnoreCase("firstLoad")) {
                ClientTickHandler.firstLoad = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
