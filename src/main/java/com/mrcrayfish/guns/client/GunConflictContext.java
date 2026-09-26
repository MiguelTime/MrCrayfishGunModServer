package com.mrcrayfish.guns.client;

import com.mrcrayfish.controllable.client.binding.context.InGameContext;
import net.minecraft.resources.ResourceLocation;

/** Gun handlers check the held item and yield to vanilla bindings when not holding a gun. */
public final class GunConflictContext extends InGameContext
{
    public static final GunConflictContext IN_GAME_HOLDING_WEAPON = new GunConflictContext();

    private GunConflictContext()
    {
        super(ResourceLocation.fromNamespaceAndPath("cgm", "holding_weapon"));
    }

    @Override
    public int priority()
    {
        return super.priority() + 1;
    }
}
