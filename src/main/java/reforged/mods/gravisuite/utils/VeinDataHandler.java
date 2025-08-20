package reforged.mods.gravisuite.utils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.vintage.core.utils.VeinSearchResult;
import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class VeinDataHandler {
    private static final Map<String, Boolean> SHOW_MAP = new HashMap<String, Boolean>();
    private static final Map<String, VeinSearchResult> SEARCH_RESULT_MAP = new HashMap<String, VeinSearchResult>();

    public static void setShow(EntityPlayer player, boolean show) {
        if (player != null) {
            SHOW_MAP.put(player.username, show);
        }
    }

    public static boolean getShow(EntityPlayer player) {
        if (player != null) {
            Boolean show = SHOW_MAP.remove(player.username);
            return show != null && show;
        }
        return false;
    }

    public static void setSearchResult(EntityPlayer player, VeinSearchResult result) {
        if (player != null) {
            SEARCH_RESULT_MAP.put(player.username, result);
        }
    }

    public static VeinSearchResult getSearchResult(EntityPlayer player) {
        if (player != null) {
            VeinSearchResult result = SEARCH_RESULT_MAP.remove(player.username);
            return result != null ? result : VeinSearchResult.NULL;
        }
        return VeinSearchResult.NULL;
    }
}
