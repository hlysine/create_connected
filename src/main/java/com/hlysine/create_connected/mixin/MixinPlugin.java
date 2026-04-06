package com.hlysine.create_connected.mixin;

import com.hlysine.create_connected.compat.ModMixin;
import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import net.neoforged.fml.loading.FMLLoader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.IClassBytecodeProvider;
import org.spongepowered.asm.service.MixinService;
import org.spongepowered.asm.util.Annotations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MixinPlugin implements IMixinConfigPlugin {
    private boolean isFrameworkInstalled; // this makes sure that forge's helpful mods not found screen shows up

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
        String modMixin = Type.getDescriptor(ModMixin.class);
        String pseudo = Type.getDescriptor(Pseudo.class);
        IClassBytecodeProvider bytecodeProvider = MixinService.getService().getBytecodeProvider();

        ClassNode mixinClass;
        ClassNode targetClass = null;
        try {
            mixinClass = bytecodeProvider.getClassNode(mixinClassName);
        } catch (ClassNotFoundException | IOException ignored) {
            return isFrameworkInstalled;
        }
        try {
            targetClass = bytecodeProvider.getClassNode(targetClassName);
        } catch (ClassNotFoundException | IOException ignored) {
        }

        List<AnnotationNode> annotations = new ArrayList<>();
        {
            if (mixinClass.invisibleAnnotations != null) {
                annotations.addAll(mixinClass.invisibleAnnotations);
            }
            if (mixinClass.visibleAnnotations != null) {
                annotations.addAll(mixinClass.visibleAnnotations);
            }
        }

        for (AnnotationNode node : annotations) {
            if (node.desc.equals(modMixin)) {
                List<String> mods = Annotations.getValue(node, "mods", true);
                boolean applyIfPresent = Annotations.getValue(node, "applyIfPresent", Boolean.TRUE);
                boolean anyModsLoaded = anyModsLoaded(mods);
                return anyModsLoaded == applyIfPresent;
            }

            if (node.desc.equals(pseudo)) {
                return targetClass != null;
            }
        }
        return true;
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