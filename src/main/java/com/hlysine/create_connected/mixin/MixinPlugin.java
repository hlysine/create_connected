package com.hlysine.create_connected.mixin;

import com.hlysine.create_connected.compat.ModMixin;
import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;
import org.spongepowered.asm.util.Annotations;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class MixinPlugin implements IMixinConfigPlugin {
    private boolean isFrameworkInstalled;

    @Override
    public void onLoad(String mixinPackage) {
        MixinExtrasBootstrap.init();
        try {
            Class.forName("com.hlysine.create_connected.CreateConnected", false, this.getClass().getClassLoader());
            isFrameworkInstalled = true;
        } catch (Exception e) {
            isFrameworkInstalled = false;
        }
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        try {
            List<AnnotationNode> annotationNodes = MixinService.getService().getBytecodeProvider().getClassNode(mixinClassName).visibleAnnotations;
            if (annotationNodes == null) return true;

            boolean shouldApply = true;
            for (AnnotationNode node : annotationNodes) {
                if (node.desc.equals(Type.getDescriptor(ModMixin.class))) {
                    List<String> mods = Annotations.getValue(node, "mods", true);
                    boolean applyIfPresent = Annotations.getValue(node, "applyIfPresent", Boolean.TRUE);
                    boolean anyModsLoaded = anyModsLoaded(mods);
                    shouldApply = anyModsLoaded == applyIfPresent;
                }
            }
            return shouldApply;
        } catch (ClassNotFoundException | IOException ignored) {
        }
        return isFrameworkInstalled; // this makes sure that forge's helpful mods not found screen shows up
    }

    private static boolean anyModsLoaded(List<String> mods) {
        for (String mod : mods) {
            if (FMLLoader.getLoadingModList().getMods().stream().anyMatch(m -> m.getModId().equals(mod))) return true;
        }
        return false;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}