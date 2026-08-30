package reforged.mods.gravisuite;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.EnumHelper;
import reforged.mods.gravisuite.blocks.BlockRelocatorPortal;
import reforged.mods.gravisuite.items.ItemComponent;
import reforged.mods.gravisuite.items.armors.ItemAdvancedJetpack;
import reforged.mods.gravisuite.items.armors.ItemAdvancedQuant;
import reforged.mods.gravisuite.items.armors.ItemLappack;
import reforged.mods.gravisuite.items.tools.*;
import reforged.mods.gravisuite.items.tools.base.ItemBaseExcavator;
import reforged.mods.gravisuite.items.tools.base.ItemBaseHammer;
import reforged.mods.gravisuite.items.tools.relocator.EntityRelocatorBall;
import reforged.mods.gravisuite.items.tools.relocator.ItemRelocator;
import reforged.mods.gravisuite.items.tools.relocator.ItemRelocatorPortal;
import reforged.mods.gravisuite.tiles.TileEntityRelocatorPortal;

public class GraviSuiteData {

    public static EnumArmorMaterial GRAVI_MATERIAL = EnumHelper.addArmorMaterial("GRAVI_MATERIAL", 0, new int[] {0, 0, 0, 0}, 0);
    public static EnumToolMaterial GEMS_MATERIAL = EnumHelper.addToolMaterial("GRAVI_TOOL_MATERIAL", 2, 512, 8.0F, 2, 14);

    public static Item COMPONENT;
    public static ItemStack SUPERCONDUCTOR_COVER, SUPERCONDUCTOR, COOLING_CORE, GRAVI_ENGINE, MAGNETRON, VAJRA_CORE, ENGINE_BOOSTER;

    public static Item ADVANCED_LAPPACK, ULTIMATE_LAPPACK, ADVANCED_JETPACK, ADVANCED_NANO, ADVANCED_QUANT;

    public static Item ADVANCED_DRILL, ADVANCED_IRIDIUM_DRILL, ADVANCED_CHAINSAW, VAJRA, MAGNET, GRAVI_TOOL, VOIDER;
    public static Item WOOD_HAMMER, STONE_HAMMER, IRON_HAMMER, DIAMOND_HAMMER, QUARTZ_HAMMER, RUBY_HAMMER, SAPPHIRE_HAMMER, GREEN_SAPPHIRE_HAMMER, BRONZE_HAMMER;
    public static Item WOOD_EXCAVATOR, STONE_EXCAVATOR, IRON_EXCAVATOR, DIAMOND_EXCAVATOR, QUARTZ_EXCAVATOR, RUBY_EXCAVATOR, SAPPHIRE_EXCAVATOR, GREEN_SAPPHIRE_EXCAVATOR, BRONZE_EXCAVATOR;

    public static Item RELOCATOR;
    public static Block RELOCATOR_PORTAL;

    public static void init() {
        // Components
        COMPONENT = new ItemComponent();
        SUPERCONDUCTOR_COVER = new ItemStack(COMPONENT, 1, 0);
        SUPERCONDUCTOR = new ItemStack(COMPONENT, 1, 1);
        COOLING_CORE = new ItemStack(COMPONENT, 1, 2);
        GRAVI_ENGINE = new ItemStack(COMPONENT, 1, 3);
        MAGNETRON = new ItemStack(COMPONENT, 1, 4);
        VAJRA_CORE = new ItemStack(COMPONENT, 1, 5);
        ENGINE_BOOSTER = new ItemStack(COMPONENT, 1, 6);

        ADVANCED_LAPPACK = new ItemLappack.ItemAdvancedLappack();
        ULTIMATE_LAPPACK = new ItemLappack.ItemUltimateLappack();

        ADVANCED_DRILL = new ItemAdvancedDrill.ItemAdvancedDiamondDrill();
        ADVANCED_IRIDIUM_DRILL = new ItemAdvancedDrill.ItemAdvancedIridiumDrill();
        ADVANCED_CHAINSAW = new ItemAdvancedChainsaw();
        VAJRA = new ItemVajra();
        MAGNET = new ItemMagnet();
        GRAVI_TOOL = new ItemGraviTool();
        VOIDER = new ItemVoider();

        if (GraviSuiteConfig.ENABLE_HAMMERS) {
            WOOD_HAMMER = new ItemBaseHammer(GraviSuiteConfig.WOOD_HAMMER_ID.get(), EnumToolMaterial.WOOD, 0, "wooden");
            STONE_HAMMER = new ItemBaseHammer(GraviSuiteConfig.STONE_HAMMER_ID.get(), EnumToolMaterial.STONE, 1, "stone");
            IRON_HAMMER = new ItemBaseHammer(GraviSuiteConfig.IRON_HAMMER_ID.get(), EnumToolMaterial.IRON, 2, "iron");
            DIAMOND_HAMMER = new ItemBaseHammer(GraviSuiteConfig.DIAMOND_HAMMER_ID.get(), EnumToolMaterial.EMERALD, 3, "diamond");
            BRONZE_HAMMER = new ItemBaseHammer(GraviSuiteConfig.BRONZE_HAMMER_ID.get(), EnumToolMaterial.IRON, 4, "bronze");
            if (Loader.isModLoaded("AppliedEnergistics")) {
                QUARTZ_HAMMER = new ItemBaseHammer(GraviSuiteConfig.QUARTZ_HAMMER_ID.get(), EnumToolMaterial.IRON, 5, "quartz");
            }
            if (Loader.isModLoaded("RedPowerBase")) {
                RUBY_HAMMER = new ItemBaseHammer(GraviSuiteConfig.RUBY_HAMMER_ID.get(), GEMS_MATERIAL, 6, "ruby");
                SAPPHIRE_HAMMER = new ItemBaseHammer(GraviSuiteConfig.SAPPHIRE_HAMMER_ID.get(), GEMS_MATERIAL, 7, "sapphire");
                GREEN_SAPPHIRE_HAMMER = new ItemBaseHammer(GraviSuiteConfig.GREEN_SAPPHIRE_HAMMER_ID.get(), GEMS_MATERIAL, 8, "green_sapphire");
            }
        }

        if (GraviSuiteConfig.ENABLE_EXCAVATORS) {
            WOOD_EXCAVATOR = new ItemBaseExcavator(GraviSuiteConfig.WOOD_EXCAVATOR_ID.get(), EnumToolMaterial.WOOD, 0, "wooden");
            STONE_EXCAVATOR = new ItemBaseExcavator(GraviSuiteConfig.STONE_EXCAVATOR_ID.get(), EnumToolMaterial.STONE, 1, "stone");
            IRON_EXCAVATOR = new ItemBaseExcavator(GraviSuiteConfig.IRON_EXCAVATOR_ID.get(), EnumToolMaterial.IRON, 2, "iron");
            DIAMOND_EXCAVATOR = new ItemBaseExcavator(GraviSuiteConfig.DIAMOND_EXCAVATOR_ID.get(), EnumToolMaterial.EMERALD, 3, "diamond");
            BRONZE_EXCAVATOR = new ItemBaseExcavator(GraviSuiteConfig.BRONZE_EXCAVATOR_ID.get(), EnumToolMaterial.IRON, 4, "bronze");
            if (Loader.isModLoaded("AppliedEnergistics")) {
                QUARTZ_EXCAVATOR = new ItemBaseExcavator(GraviSuiteConfig.QUARTZ_EXCAVATOR_ID.get(), EnumToolMaterial.IRON, 5, "quartz");
            }
            if (Loader.isModLoaded("RedPowerBase")) {
                RUBY_EXCAVATOR = new ItemBaseExcavator(GraviSuiteConfig.RUBY_EXCAVATOR_ID.get(), GEMS_MATERIAL, 6, "ruby");
                SAPPHIRE_EXCAVATOR = new ItemBaseExcavator(GraviSuiteConfig.SAPPHIRE_EXCAVATOR_ID.get(), GEMS_MATERIAL, 7, "sapphire");
                GREEN_SAPPHIRE_EXCAVATOR = new ItemBaseExcavator(GraviSuiteConfig.GREEN_SAPPHIRE_EXCAVATOR_ID.get(), GEMS_MATERIAL, 8, "green_sapphire");
            }
        }

        ADVANCED_JETPACK = new ItemAdvancedJetpack.ItemAdvancedElectricJetpack();
        ADVANCED_NANO = new ItemAdvancedJetpack.ItemAdvancedNano();
        ADVANCED_QUANT = new ItemAdvancedQuant();

        RELOCATOR = new ItemRelocator();
        RELOCATOR_PORTAL = new BlockRelocatorPortal(GraviSuiteConfig.RELOCATOR_PORTAL_BLOCK_ID.get());
        GameRegistry.registerTileEntity(TileEntityRelocatorPortal.class, "Relocator Portal");
        GameRegistry.registerBlock(RELOCATOR_PORTAL, ItemRelocatorPortal.class, "BlockRelocatorPortal");
        registerEntity(EntityRelocatorBall.class, "RelocatorBall");
    }

    public static void registerEntity(Class<? extends Entity> paramClass, String paramString) {
        int i = EntityRegistry.findGlobalUniqueEntityId();
        EntityRegistry.registerModEntity(paramClass, paramString, i, GraviSuite.instance, 64, 1, true);
    }
}
