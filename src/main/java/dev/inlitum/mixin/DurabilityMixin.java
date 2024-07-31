package dev.inlitum.mixin;

import dev.inlitum.DuraView;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrawContext.class)
public class DurabilityMixin {

    @Redirect(
            method = "drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", 
            at = @At( 
                    value = "INVOKE", 
                    target = "Lnet/minecraft/client/gui/DrawContext;fill(Lnet/minecraft/client/render/RenderLayer;IIIII)V"
            ),
            slice = @Slice (
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItemBarStep()I"),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;floor(F)I")
            )
    )
    private void fill (DrawContext instance, RenderLayer layer,  int x1, int y1, int x2, int y2, int color) {
        // DO NOTHING AT ALL!
        // This will prevent the durability bar from displaying
        if (!DuraView.config.enabled) {
            instance.fill(layer, x1, y1, x2, y2, color);
        }
    }
    
    @Inject(
            method = "drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;getItemBarColor()I",
                    shift = At.Shift.AFTER
            )
    )
    private void renderPercentage (TextRenderer textRenderer, ItemStack stack, int x, int y, String countOverride, CallbackInfo ci) {
        if (stack.isEmpty()) {
            return;
        }
        
        if (!DuraView.config.enabled) {
            return;
        }

        DrawContext thisObject = (DrawContext)(Object)this;

        int maxDamage = stack.getMaxDamage();
        int currentDamage = stack.getDamage();

        int percentage = (int)Math.round((1 - (double)currentDamage / (double)maxDamage) * (double) 100);

        if (percentage < 100) {
            int color = stack.getItemBarColor();

            int stringWidth = textRenderer.getWidth(String.valueOf(percentage));

            boolean displayAbove = DuraView.config.displayAbove;

            int xPos, yPos;
            if (displayAbove) {
                xPos = (x * 2) + 2;
                yPos = (y * 2) + 2;
            } else {
                xPos = ((x + 8) * 2 + 1 + stringWidth / 2 - stringWidth);
                yPos = (y * 2) + 22;
            }

            this.matrices.push();
            this.matrices.scale(0.5F, 0.5F, 0.5F);
            this.matrices.translate(0.0F, 0.0F, 399.0F);
            thisObject.drawTextWithShadow(textRenderer, percentage + "%", xPos, yPos, color);
            this.matrices.scale(2.0F, 2.0F, 2.0F);
            this.matrices.translate(0.0F, 0.0F, 200.0F);
            this.matrices.pop();
        }
    }
}
