package com.mrcrayfish.guns.mixin;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class MixinPlugin implements IMixinConfigPlugin {
    private boolean isFrameworkInstalled;
    private boolean isSimplePlanesInstalled;
    @Override
    public void onLoad(String mixinPackage) {
        try {
            Class.forName("com.mrcrayfish.framework.FrameworkNeoForge", false, this.getClass().getClassLoader());
            isFrameworkInstalled = true;
        } catch (Exception e) {
            isFrameworkInstalled = false;
        }
        try {
            Class.forName("xyz.przemyk.simpleplanes.SimplePlanesMod", false, this.getClass().getClassLoader());
            isSimplePlanesInstalled = true;
        } catch (Exception e) {
            isSimplePlanesInstalled = false;
        }
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (!isFrameworkInstalled) return false;
        if (mixinClassName.contains("simpleplanes")) return isSimplePlanesInstalled;
        return true;
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
