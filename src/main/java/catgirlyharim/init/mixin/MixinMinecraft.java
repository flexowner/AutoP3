package catgirlyharim.init.mixin;

import catgirlyharim.init.events.PreKeyInputEvent;
import catgirlyharim.init.events.PreMouseInputEvent;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Inject(method = {"runTick"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;dispatchKeypresses()V")})
    public void keyPresses(CallbackInfo ci) {
        int k = (Keyboard.getEventKey() == 0) ? (Keyboard.getEventCharacter() + 256) : Keyboard.getEventKey();
        char character = Keyboard.getEventCharacter();
        if (Keyboard.getEventKeyState()) {
            System.out.println("AAA");
            MinecraftForge.EVENT_BUS.post(new PreKeyInputEvent(k, character));
        }
    }
    @Inject(method = {"runTick"}, at = {@At(value = "INVOKE", target = "Lorg/lwjgl/input/Mouse;getEventButton()I", remap = false)})
    public void mouseKeyPresses(CallbackInfo ci) {
        int k = Mouse.getEventButton();
        if (Mouse.getEventButtonState()) {
            MinecraftForge.EVENT_BUS.post(new PreMouseInputEvent(k));
        }
    }
}