package reforged.mods.gravisuite.network;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import reforged.mods.gravisuite.ClientTickHandler;
import reforged.mods.gravisuite.GraviSuite;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class NetworkHandler implements IPacketHandler {

    @Override
    public void onPacketData(INetworkManager iNetworkManager, Packet250CustomPayload packet, Player iPlayer) {
        if (packet.data.length != 0) {
            EntityPlayer player = (EntityPlayer) iPlayer;
            ByteArrayInputStream buffer = new ByteArrayInputStream(packet.data, 1, packet.data.length - 1);
            try {
                DataInputStream stream;
                int intData;
                switch (packet.data[0]) {
                    case 1:
                        stream = new DataInputStream(buffer);
                        intData = stream.readInt();
                        GraviSuite.KEYBOARD.processKeyUpdate(player, intData);
                        break;
                    case 2:
                        ClientTickHandler.firstLoad = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendKeyStateUpdate(int keyState) {}
    public void sendWorldLoadState() {}
}
