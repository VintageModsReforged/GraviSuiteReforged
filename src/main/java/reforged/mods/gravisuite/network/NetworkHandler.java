package reforged.mods.gravisuite.network;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;

public class NetworkHandler implements IPacketHandler {

    @Override
    public void onPacketData(INetworkManager iNetworkManager, Packet250CustomPayload packet250CustomPayload, Player player) {

    }

    public void sendKeyStateUpdate(int keyState) {}
    public void sendWorldLoadState() {}
}
