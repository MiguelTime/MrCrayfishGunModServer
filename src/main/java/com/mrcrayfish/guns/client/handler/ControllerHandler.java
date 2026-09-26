package com.mrcrayfish.guns.client.handler;

import com.mrcrayfish.controllable.Controllable;
import com.mrcrayfish.controllable.client.Action;
import com.mrcrayfish.controllable.client.gui.navigation.BasicNavigationPoint;
import com.mrcrayfish.controllable.client.input.Controller;
import com.mrcrayfish.controllable.event.ControllerEvents;
import com.mrcrayfish.guns.Config;
import com.mrcrayfish.guns.compat.PlayerReviveHelper;
import com.mrcrayfish.guns.util.GunItemData;
import com.mrcrayfish.guns.client.GunButtonBindings;
import com.mrcrayfish.guns.client.screen.WorkbenchScreen;
import com.mrcrayfish.guns.common.Gun;
import com.mrcrayfish.guns.init.ModSyncedDataKeys;
import com.mrcrayfish.guns.item.GunItem;
import com.mrcrayfish.guns.item.attachment.impl.Scope;
import com.mrcrayfish.guns.network.PacketHandler;
import com.mrcrayfish.guns.network.message.C2SMessageUnload;
import com.mrcrayfish.guns.util.GunEnchantmentHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.bus.api.SubscribeEvent;

/**
 * Author: MrCrayfish
 */
public class ControllerHandler
{
    private static int reloadCounter = -1;
    private static ItemStack reloadStack = ItemStack.EMPTY;
    private static int reloadSlot = -1;

    public static void init() {
        NeoForge.EVENT_BUS.register(new ControllerHandler());
        ControllerEvents.UPDATE_CAMERA.register((yawSpeed, pitchSpeed) -> {
            Player player = Minecraft.getInstance().player;
            if(player != null)
            {
                ItemStack heldItem = player.getMainHandItem();
                if(heldItem.getItem() instanceof GunItem && AimingHandler.get().isAiming())
                {
                    double adsSensitivity = Config.CLIENT.controls.aimDownSightSensitivity.get();
                    yawSpeed.set(yawSpeed.get() * (float) adsSensitivity);
                    pitchSpeed.set(pitchSpeed.get() * (float) adsSensitivity);

                    Scope scope = Gun.getScope(heldItem);
                    Controller controller = Controllable.getController();
                    if(scope != null && scope.isStable() && controller != null && GunButtonBindings.STEADY_AIM.isButtonDown())
                    {
                        yawSpeed.set(yawSpeed.get() / 2.0F);
                        pitchSpeed.set(pitchSpeed.get() / 2.0F);
                    }
                }
            }
            return false;
        });
        ControllerEvents.GATHER_ACTIONS.register((actions, visibility) -> {
            Minecraft mc = Minecraft.getInstance();
            if(mc.screen != null) return;

            Player player = Minecraft.getInstance().player;
            if(player != null)
            {
                ItemStack heldItem = player.getMainHandItem();
                if(heldItem.getItem() instanceof GunItem gunItem)
                {
                    actions.put(GunButtonBindings.AIM, new Action(Component.translatable("cgm.action.aim"), Action.Side.RIGHT));
                    actions.put(GunButtonBindings.SHOOT, new Action(Component.translatable("cgm.action.shoot"), Action.Side.RIGHT));

                    Gun modifiedGun = gunItem.getModifiedGun(heldItem);
                    CompoundTag tag = GunItemData.getTag(heldItem);
                    if(tag != null && tag.getInt("AmmoCount") < GunEnchantmentHelper.getAmmoCapacity(heldItem, modifiedGun))
                    {
                        actions.put(GunButtonBindings.RELOAD, new Action(Component.translatable("cgm.action.reload"), Action.Side.LEFT));
                    }

                    Scope scope = Gun.getScope(heldItem);
                    if(scope != null && scope.isStable() && AimingHandler.get().isAiming())
                    {
                        actions.put(GunButtonBindings.STEADY_AIM, new Action(Component.translatable("cgm.action.steady_aim"), Action.Side.RIGHT));
                    }
                }
            }
        });
        ControllerEvents.GATHER_NAVIGATION_POINTS.register(points -> {
            Minecraft mc = Minecraft.getInstance();
            if(mc.screen instanceof WorkbenchScreen)
            {
                WorkbenchScreen workbench = (WorkbenchScreen) mc.screen;
                int startX = workbench.getGuiLeft();
                int startY = workbench.getGuiTop();

                for(int i = 0; i < workbench.getTabs().size(); i++)
                {
                    int tabX = startX + 28 * i + (28 / 2);
                    int tabY = startY - (28 / 2);
                    points.add(new BasicNavigationPoint(tabX, tabY));
                }

                for(int i = 0; i < 6; i++)
                {
                    int itemX = startX + 172 + (80 / 2);
                    int itemY = startY + i * 19 + 63 + (19 / 2);
                    points.add(new BasicNavigationPoint(itemX, itemY));
                }
            }
        });
    }

    public static boolean canUseWeapon()
    {
        Minecraft mc = Minecraft.getInstance();
        return mc.player != null && mc.level != null && mc.screen == null
                && mc.getOverlay() == null && mc.isWindowActive() && !mc.isPaused()
                && !mc.player.isSpectator() && !PlayerReviveHelper.isBleeding(mc.player)
                && mc.player.getMainHandItem().getItem() instanceof GunItem;
    }

    public static void startReload()
    {
        Minecraft mc = Minecraft.getInstance();
        reloadCounter = 0;
        reloadStack = mc.player.getMainHandItem();
        reloadSlot = mc.player.getInventory().selected;
    }

    private static boolean isReloadTargetValid()
    {
        Minecraft mc = Minecraft.getInstance();
        return canUseWeapon() && mc.player.getInventory().selected == reloadSlot
                && mc.player.getMainHandItem() == reloadStack;
    }

    private static void cancelReloadPress()
    {
        reloadCounter = -1;
        reloadStack = ItemStack.EMPTY;
        reloadSlot = -1;
    }

    public static void finishReload()
    {
        if(reloadCounter >= 0 && isReloadTargetValid())
        {
            Player player = Minecraft.getInstance().player;
            ReloadHandler.get().setReloading(!ModSyncedDataKeys.RELOADING.getValue(player));
        }
        cancelReloadPress();
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Pre event)
    {
        if(Controllable.getController() == null || !canUseWeapon())
        {
            cancelReloadPress();
            resetBindings();
            return;
        }

        if(reloadCounter >= 0)
        {
            if(!isReloadTargetValid() || !GunButtonBindings.RELOAD.isButtonDown())
            {
                cancelReloadPress();
            }
            else if(++reloadCounter >= 40)
            {
                ReloadHandler.get().setReloading(false);
                PacketHandler.getPlayChannel().sendToServer(new C2SMessageUnload());
                cancelReloadPress();
            }
        }
    }

    private static void resetBindings()
    {
        GunButtonBindings.SHOOT.resetPressedState();
        GunButtonBindings.AIM.resetPressedState();
        GunButtonBindings.RELOAD.resetPressedState();
        GunButtonBindings.OPEN_ATTACHMENTS.resetPressedState();
        GunButtonBindings.STEADY_AIM.resetPressedState();
    }

    public static boolean isAiming()
    {
        return Controllable.getController() != null && canUseWeapon() && GunButtonBindings.AIM.isButtonDown();
    }

    public static boolean isShooting()
    {
        return Controllable.getController() != null && canUseWeapon() && GunButtonBindings.SHOOT.isButtonDown();
    }
}
