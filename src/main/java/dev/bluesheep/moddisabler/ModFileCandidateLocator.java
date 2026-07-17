package dev.bluesheep.moddisabler;

import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.fml.loading.moddiscovery.ModFile;
import net.neoforged.neoforgespi.ILaunchContext;
import net.neoforged.neoforgespi.language.IModInfo;
import net.neoforged.neoforgespi.locating.IDiscoveryPipeline;
import net.neoforged.neoforgespi.locating.IModFileCandidateLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Pattern;

public class ModFileCandidateLocator implements IModFileCandidateLocator {
    Logger logger = LoggerFactory.getLogger("moddisabler");

    @Override
    synchronized public void findCandidates(ILaunchContext context, IDiscoveryPipeline pipeline) {
        logger.debug("Modify loadedFiles");
        Map<String, Pattern> config = loadConfig();
        try {
            Class<?> pipelineClass = Class.forName("net.neoforged.fml.loading.moddiscovery.ModDiscoverer$DiscoveryPipeline");
            Field field = pipelineClass.getDeclaredField("loadedFiles");
            field.setAccessible(true);
            ArrayList<ModFile> modFiles = (ArrayList<ModFile>) field.get(pipeline);
            modFiles.removeIf(modFile -> check(modFile, config));
        } catch (Exception e) {
            logger.error(e.toString());
        }
        logger.debug("Modify complete");
    }

    @Override
    public int getPriority() {
        return Integer.MIN_VALUE;
    }

    private Map<String, Pattern> loadConfig() {
        File configFile = FMLPaths.CONFIGDIR.get().resolve("moddisabler.txt").toFile();
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                logger.error(e.toString());
                return Map.of();
            }
        }
        if (configFile.isFile()) {
            try {
                List<String> lines = Files.readAllLines(configFile.toPath());
                Map<String, Pattern> config = new HashMap<>();
                for (String line : lines) {
                    if (line.isBlank()) continue;
                    var x = line.split(" ", 2);
                    config.put(x[0].toUpperCase(Locale.ROOT), Pattern.compile(x[1]));
                }
                return config;
            } catch (IOException e) {
                logger.error(e.toString());
            }
        }
        return Map.of();
    }

    private Boolean check(ModFile modFile, Map<String, Pattern> config) {
        var mods = modFile.getModFileInfo().getMods();
        if (!mods.isEmpty()) {
            for (IModInfo mod : mods) {
                String id = mod.getModId().toUpperCase(Locale.ROOT);
                if (config.containsKey(id)) {
                    return config.get(id).matcher(modFile.getFileName()).find();
                }
            }
        }
        return false;
    }
}
