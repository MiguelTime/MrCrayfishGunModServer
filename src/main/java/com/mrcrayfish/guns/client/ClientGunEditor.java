package com.mrcrayfish.guns.client;

import com.mrcrayfish.guns.common.Gun;
import com.mrcrayfish.guns.debug.Debug;
import com.mrcrayfish.guns.debug.IDebugWidget;
import com.mrcrayfish.guns.debug.client.screen.widget.DebugButton;
import com.mrcrayfish.guns.debug.client.screen.widget.DebugSlider;
import com.mrcrayfish.guns.debug.client.screen.widget.DebugToggle;
import com.mrcrayfish.guns.item.ScopeItem;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import org.apache.commons.lang3.tuple.Pair;

public final class ClientGunEditor {
    private ClientGunEditor() {}

    public static void addGunWidgets(Gun gun, List<Pair<Component, Supplier<IDebugWidget>>> widgets) {
        ItemStack scope = Gun.getScopeStack(Objects.requireNonNull(Minecraft.getInstance().player).getMainHandItem());
        if (scope.getItem() instanceof ScopeItem scopeItem) {
            widgets.add(Pair.of(scope.getItem().getName(scope), () -> new DebugButton(Component.literal("Edit"),
                    button -> Minecraft.getInstance().setScreen(ClientHandler.createEditorScreen(Debug.getScope(scopeItem))))));
        }
        widgets.add(Pair.of(gun.getModules().getEditorLabel(), () -> new DebugButton(Component.literal(">"),
                button -> Minecraft.getInstance().setScreen(ClientHandler.createEditorScreen(gun.getModules())))));
    }

    public static void addModuleWidgets(Gun.Modules modules, List<Pair<Component, Supplier<IDebugWidget>>> widgets) {
        widgets.add(Pair.of(Component.literal("Enabled Iron Sights"), () -> new DebugToggle(modules.getZoom() != null,
                modules::setZoomEnabled)));
        widgets.add(Pair.of(Component.literal("Adjust Iron Sights"), () -> new DebugButton(Component.literal(">"),
                button -> {
                    if (button.active && modules.getZoom() != null) {
                        Minecraft.getInstance().setScreen(ClientHandler.createEditorScreen(modules.getZoom()));
                    }
                }, () -> modules.getZoom() != null)));
    }

    public static void addZoomWidgets(Gun.Modules.Zoom zoom, List<Pair<Component, Supplier<IDebugWidget>>> widgets) {
        widgets.add(Pair.of(Component.literal("FOV Modifier"), () -> new DebugSlider(0.0, 1.0, zoom.getFovModifier(),
                0.01, 3, value -> zoom.setFovModifier(value.floatValue()))));
    }
}
