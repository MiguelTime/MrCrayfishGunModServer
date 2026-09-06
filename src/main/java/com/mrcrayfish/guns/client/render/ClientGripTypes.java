package com.mrcrayfish.guns.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mrcrayfish.guns.common.GripType;
import com.mrcrayfish.guns.client.render.pose.BazookaPose;
import com.mrcrayfish.guns.client.render.pose.MiniGunPose;
import com.mrcrayfish.guns.client.render.pose.OneHandedPose;
import com.mrcrayfish.guns.client.render.pose.TwoHandedPose;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;

import java.util.Map;

public final class ClientGripTypes {
    private static final Map<ResourceLocation, IHeldAnimation> ANIMATIONS = Map.of(
            GripType.ONE_HANDED.getId(), new OneHandedPose(),
            GripType.TWO_HANDED.getId(), new TwoHandedPose(),
            GripType.MINI_GUN.getId(), new MiniGunPose(),
            GripType.BAZOOKA.getId(), new BazookaPose()
    );

    private ClientGripTypes() {}

    public static IHeldAnimation get(GripType gripType) {
        return ANIMATIONS.getOrDefault(gripType.getId(), ANIMATIONS.get(GripType.ONE_HANDED.getId()));
    }

    public static boolean applyBackTransforms(Player player, PoseStack poseStack) {
        if (player.getItemBySlot(EquipmentSlot.CHEST).getItem() == Items.ELYTRA) {
            return false;
        }
        poseStack.mulPose(Axis.YP.rotationDegrees(180F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(180F));
        if (player.isCrouching()) {
            poseStack.translate(0, -7 * 0.0625, -4 * 0.0625);
            poseStack.mulPose(Axis.XP.rotationDegrees(30F));
        } else {
            poseStack.translate(0, -5 * 0.0625, -2 * 0.0625);
        }
        if (!player.getItemBySlot(EquipmentSlot.CHEST).isEmpty()) {
            poseStack.translate(0, 0, -1 * 0.0625);
        }
        poseStack.mulPose(Axis.ZP.rotationDegrees(-45F));
        poseStack.scale(0.5F, 0.5F, 0.5F);
        return true;
    }
}
