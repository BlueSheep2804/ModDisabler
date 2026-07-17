package dev.bluesheep.moddisabler;

import net.neoforged.fml.loading.moddiscovery.ModFile;
import net.neoforged.neoforgespi.ILaunchContext;
import net.neoforged.neoforgespi.locating.IDiscoveryPipeline;
import net.neoforged.neoforgespi.locating.IModFileCandidateLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Locale;

public class ModFileCandidateLocator implements IModFileCandidateLocator {
    Logger logger = LoggerFactory.getLogger("moddisabler");

    @Override
    synchronized public void findCandidates(ILaunchContext context, IDiscoveryPipeline pipeline) {
        logger.debug("Modify loadedFiles");
        try {
            Class<?> pipelineClass = Class.forName("net.neoforged.fml.loading.moddiscovery.ModDiscoverer$DiscoveryPipeline");
            Field field = pipelineClass.getDeclaredField("loadedFiles");
            field.setAccessible(true);
            ArrayList<ModFile> modFiles = (ArrayList<ModFile>) field.get(pipeline);
            modFiles.removeIf(modFile -> {
                var mods = modFile.getModFileInfo().getMods();
                return !mods.isEmpty()
                        && mods.stream().anyMatch(it -> it.getModId().toUpperCase(Locale.ROOT).equals("JEI"))
                        && modFile.getFileName().toUpperCase(Locale.ROOT).startsWith("JEI");
            });
        } catch (Exception e) {
            logger.error(e.toString());
        }
        logger.debug("Modify complete");
    }

    @Override
    public int getPriority() {
        return Integer.MIN_VALUE;
    }
}
