package com.mrcrayfish.guns.client;

import com.mrcrayfish.controllable.Controllable;
import com.mrcrayfish.controllable.client.binding.ButtonBinding;
import com.mrcrayfish.controllable.client.binding.handlers.OnPressAndReleaseHandler;
import com.mrcrayfish.controllable.client.input.Buttons;
import com.mrcrayfish.guns.client.handler.ControllerHandler;
import com.mrcrayfish.guns.client.handler.ShootingHandler;
import com.mrcrayfish.guns.network.PacketHandler;
import com.mrcrayfish.guns.network.message.C2SMessageAttachments;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;
import java.util.function.Consumer;

public class GunButtonBindings
{
    public static final ButtonBinding SHOOT = binding(Buttons.RIGHT_TRIGGER, "shoot",
            player -> ShootingHandler.get().fire(player, player.getMainHandItem()), () -> {});
    public static final ButtonBinding AIM = binding(Buttons.LEFT_TRIGGER, "aim", player -> {}, () -> {});
    public static final ButtonBinding RELOAD = binding(Buttons.X, "reload",
            player -> ControllerHandler.startReload(), ControllerHandler::finishReload);
    public static final ButtonBinding OPEN_ATTACHMENTS = binding(Buttons.B, "attachments",
            player -> PacketHandler.getPlayChannel().sendToServer(new C2SMessageAttachments()), () -> {});
    public static final ButtonBinding STEADY_AIM = binding(Buttons.RIGHT_THUMB_STICK, "steadyAim", player -> {}, () -> {});

    private static ButtonBinding binding(int button, String name, Consumer<Player> pressed, Runnable released)
    {
        return new ButtonBinding(button, "cgm.button." + name, "button.categories.cgm",
                GunConflictContext.IN_GAME_HOLDING_WEAPON,
                OnPressAndReleaseHandler.create(context -> {
                    // Empty means fall through to lower-priority vanilla bindings.
                    if(!ControllerHandler.canUseWeapon())
                        return Optional.empty();
                    return context.player().map(player -> () -> pressed.accept(player));
                }, context -> {
                    released.run();
                    return true;
                }));
    }

    public static void register()
    {
        var registry = Controllable.getBindingRegistry();
        registry.register(SHOOT);
        registry.register(AIM);
        registry.register(RELOAD);
        registry.register(OPEN_ATTACHMENTS);
        registry.register(STEADY_AIM);
    }
}
