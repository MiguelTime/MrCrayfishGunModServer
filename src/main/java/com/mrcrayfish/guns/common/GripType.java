package com.mrcrayfish.guns.common;

import com.mrcrayfish.guns.Reference;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class GripType
{
    /**
     * A grip type designed for weapons that are held with only one hand, like a pistol
     */
    public static final GripType ONE_HANDED = new GripType(new ResourceLocation(Reference.MOD_ID, "one_handed"));

    /**
     * A grip type designed for weapons that are held with two hands, like an assault rifle
     */
    public static final GripType TWO_HANDED = new GripType(new ResourceLocation(Reference.MOD_ID, "two_handed"));

    /**
     * A custom grip type designed for the mini gun simply due it's nature of being a completely
     * unique way to hold the weapon
     */
    public static final GripType MINI_GUN = new GripType(new ResourceLocation(Reference.MOD_ID, "mini_gun"));

    /**
     * A custom grip type designed for the bazooka.
     */
    public static final GripType BAZOOKA = new GripType(new ResourceLocation(Reference.MOD_ID, "bazooka"));

     * The grip type map.
     */
    private static Map<ResourceLocation, GripType> gripTypeMap = new HashMap<>();

    static
    {
        /* Registers the standard grip types when the class is loaded */
        registerType(ONE_HANDED);
        registerType(TWO_HANDED);
        registerType(MINI_GUN);
        registerType(BAZOOKA);
    }

    /**
     * Registers a new grip type. If the id already exists, the grip type will simply be ignored.
     *
     * @param type the grip type
     */
    public static void registerType(GripType type)
    {
        gripTypeMap.putIfAbsent(type.getId(), type);
    }

    /**
     * Gets the grip type associated the the id. If the grip type does not exist, it will default to
     * one handed.
     *
     * @param id the id of the grip type
     * @return returns an get of the grip type or ONE_HANDED if it doesn't exist
     */
    public static GripType getType(ResourceLocation id)
    {
        return gripTypeMap.getOrDefault(id, ONE_HANDED);
    }

    private final ResourceLocation id;
    /**
     * Creates a new grip type.
     *
     * @param id the id of the grip type
     */
    public GripType(ResourceLocation id)
    {
        this.id = id;
    }

    /**
     * Gets the id of the grip type
     */
    public ResourceLocation getId()
    {
        return this.id;
    }

}
