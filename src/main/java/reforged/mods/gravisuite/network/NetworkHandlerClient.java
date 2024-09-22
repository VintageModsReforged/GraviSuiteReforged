package reforged.mods.gravisuite.network;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import reforged.mods.gravisuite.events.client.ClientArmorHandler;
import reforged.mods.gravisuite.utils.Refs;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@SideOnly(Side.CLIENT)
public class NetworkHandlerClient extends NetworkHandler {

    @Override
    public void onPacketData(INetworkManager iNetworkManager, Packet250CustomPayload packet, Player iPlayer) {
        if (packet.data.length != 0) {
            switch (packet.data[0]) {
                case 1:
                default:
                    break;
                case 2:
                    ClientArmorHandler.firstLoad = true;
            }
        }
    }

    @Override
    public void sendKeyStateUpdate(int keyState) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream(buffer);
        try {
            outputStream.writeByte(1);
            outputStream.writeInt(keyState);
            outputStream.close();
            Packet250CustomPayload packet = new Packet250CustomPayload();
            packet.channel = Refs.id;
            packet.isChunkDataPacket = false;
            packet.data = buffer.toByteArray();
            packet.length = packet.data.length;
            PacketDispatcher.sendPacketToServer(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendWorldLoadState() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream outputStream = new DataOutputStream(buffer);
        try {
            outputStream.writeByte(2);
            outputStream.close();
            Packet250CustomPayload packet = new Packet250CustomPayload();
            packet.channel = Refs.id;
            packet.data = buffer.toByteArray();
            packet.length = packet.data.length;
            PacketDispatcher.sendPacketToServer(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
