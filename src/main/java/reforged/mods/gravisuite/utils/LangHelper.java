package reforged.mods.gravisuite.utils;

import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringTranslate;
import reforged.mods.gravisuite.GraviSuite;
import reforged.mods.gravisuite.GraviSuiteConfig;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public final class LangHelper {

    public LangHelper() {
        throw new UnsupportedOperationException();
    }

    // TODO: make it possible to use different languages on the fly

    public static void init() {
        if (!GraviSuiteConfig.additional_languages.isEmpty()) {
            String[] LANGS = GraviSuiteConfig.additional_languages.split(",");
            if (LANGS.length == 1) {
                addEntry(GraviSuiteConfig.additional_languages);
            } else {
                for (String lang : LANGS) {
                    addEntry(lang);
                }
            }
        } else {
            String[] LANGS = GraviSuiteConfig.default_language.split(",");
            if (LANGS.length == 1) {
                addEntry(GraviSuiteConfig.default_language);
            } else {
                for (String lang : LANGS) {
                    addEntry(lang);
                }
            }
        }
        LanguageRegistry.reloadLanguageTable();
    }

    private static void addEntry(String lang) {
        InputStream stream = null;
        InputStreamReader reader = null;
        try {
            if (GraviSuiteConfig.additional_languages.isEmpty()) {
                stream = LangHelper.class.getResourceAsStream("/mods/gravisuite/lang/" + lang + ".lang"); // use the default .lang file from modJar
            } else {
                stream = new FileInputStream(Minecraft.getMinecraftDir() + "/config/gravisuite/lang/" + lang + ".lang"); // use the lang files from config/gravisuite/lang folder
            }
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

    public static String format(String key, Object... args) {
        return format(null, key, args);
    }

    public static String format(StringTranslate translator, String key, Object... args) {
        if (translator == null) return StatCollector.translateToLocalFormatted(key, args);
        return translator.translateKeyFormat(key, args);
    }
}
