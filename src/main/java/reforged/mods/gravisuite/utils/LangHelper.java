package reforged.mods.gravisuite.utils;

import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.util.StatCollector;
import reforged.mods.gravisuite.GraviSuite;
import reforged.mods.gravisuite.GraviSuiteMainConfig;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public final class LangHelper {

    public LangHelper() {
        throw new UnsupportedOperationException();
    }

    public static void init() {
        String[] LANGS = GraviSuiteMainConfig.LANGUAGES.split(",");
        if (LANGS.length == 1) {
            loadLanguage(GraviSuiteMainConfig.LANGUAGES);
        } else {
            for (String lang : LANGS) {
                loadLanguage(lang);
            }
        }
    }

    public static void loadLanguage(String lang) {
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
                LanguageRegistry.instance().addStringLocalization(key, lang, props.getProperty(key));
            }

        } catch (Throwable t) {
            GraviSuite.LOGGER.severe("Error loading language " + lang + ".");
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

    public static String format(String key, Object... args) {
        return StatCollector.translateToLocalFormatted(key, args);
    }

    public static String format(boolean b) {
        return format(b ? "true" : "false");
    }

}
