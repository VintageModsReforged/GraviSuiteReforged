package reforged.mods.gravisuite.utils;

import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.item.ItemStack;
import reforged.mods.gravisuite.GraviSuite;
import reforged.mods.gravisuite.GraviSuiteConfig;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public final class LangHelper {

    public LangHelper() {
        throw new UnsupportedOperationException();
    }

    public static void addEntry(Object object, boolean special) {
        String[] LANGS = GraviSuiteConfig.languages.split(",");
        if (LANGS.length == 1) {
            addEntry(object, GraviSuiteConfig.languages, special);
        } else {
            for (String lang : LANGS) {
                addEntry(object, lang, special);
            }
        }
    }

    private static void addEntry(Object object, String lang, boolean special) {
        InputStream stream = null;
        InputStreamReader reader = null;
        try {
            stream = LangHelper.class.getResourceAsStream("/mods/gravisuite/lang/" + lang + ".lang");
            if (stream == null) {
                throw new IOException("Couldn't load language file.");
            }

            reader = new InputStreamReader(stream, "UTF-8");
            Properties props = new Properties();
            props.load(reader);
            for (String key : props.stringPropertyNames()) {
                if (special) {
                    if (object instanceof ItemStack) {
                        if (key.equals(((ItemStack) object).getItem().getUnlocalizedName((ItemStack) object))) {
                            LanguageRegistry.instance().addStringLocalization(key, lang, props.getProperty(key));
                        }
                    }
                }
                LanguageRegistry.instance().addStringLocalization(key, lang, props.getProperty(key));
            }

        } catch (Throwable t) {
            GraviSuite.logger.severe("Error loading language " + lang + ".");
            t.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (Throwable ignored) {
                }
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (Throwable ignored) {
                }
            }
        }
    }
}
