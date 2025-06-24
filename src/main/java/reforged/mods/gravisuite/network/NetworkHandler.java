package reforged.mods.gravisuite.network;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import mods.vintage.core.helpers.pos.BlockPos;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import reforged.mods.gravisuite.GraviSuite;
import reforged.mods.gravisuite.events.server.ServerArmorHandler;
import reforged.mods.gravisuite.items.tools.relocator.ItemRelocator;
import reforged.mods.gravisuite.items.tools.relocator.TeleportPoint;

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
                        ServerArmorHandler.firstLoad = true;
                    case 3:
                        if (player.getCurrentEquippedItem() != null) {
                            ItemStack handStack = player.getCurrentEquippedItem();
                            if (handStack.getItem() instanceof ItemRelocator) {
                                ItemRelocator relocator = (ItemRelocator) handStack.getItem();
                                stream = new DataInputStream(buffer);
                                String name = stream.readUTF();
                                byte actionID = stream.readByte();
                                if (actionID == 1) {
                                    TeleportPoint point = new TeleportPoint();
                                    point.DIMENSION_ID = player.worldObj.provider.dimensionId;
                                    point.NAME = name;
                                    point.POS = new BlockPos(player.posX, player.posY, player.posZ);
                                    point.YAW = player.rotationYaw;
                                    point.PITCH = player.cameraPitch;
                                    relocator.addNewPoint(player, player.getCurrentEquippedItem(), point);
                                } else if (actionID == 0) {
                                    relocator.removePoint(player.getCurrentEquippedItem(), name);
                                } else if (actionID == 2) {
                                    relocator.teleportPlayer(player, player.getCurrentEquippedItem(), name);
                                } else if (actionID == 3) {
                                    relocator.setDefaultPoint(player, player.getCurrentEquippedItem(), name);
                                }
                            }
                        }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendKeyStateUpdate(int keyState) {}
    public void sendWorldLoadState() {}
    public void sendRelocatorPoints(String name, byte actionID) {}
}
