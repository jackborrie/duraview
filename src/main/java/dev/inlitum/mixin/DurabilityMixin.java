package dev.inlitum.mixin;

import dev.inlitum.DuraView;
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

    @Final
    @Shadow
    private MatrixStack matrices;

    @Redirect(
            method = "drawItemBar",
            at = @At( 
                    value = "INVOKE", 
                    target = "Lnet/minecraft/client/gui/DrawContext;fill(Lnet/minecraft/client/render/RenderLayer;IIIIII)V"
            )
    )
    private void fill (DrawContext instance, RenderLayer layer, int x1, int y1, int x2, int y2, int z, int color) {
        if (!DuraView.config.enabled) {
            instance.fill(layer, x1, y1, x2, y2, color);
        }
    }
    
    @Inject(
            method = "drawStackOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawStackCount(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V",
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

        DrawContext drawContext = (DrawContext)(Object)this;

        int maxDamage = stack.getMaxDamage();
        int currentDamage = stack.getDamage();

        if (maxDamage != currentDamage) {
            int color = stack.getItemBarColor();

            String text;

            if (DuraView.config.showAsPercentage) {
                int percentage = (int)Math.round((1 - (double)currentDamage / (double)maxDamage) * (double) 100);
                text = percentage + "%";
            } else {
                text = String.valueOf(maxDamage - currentDamage);
            }

            boolean displayAbove = DuraView.config.displayAbove;

            int xPos, yPos;
            if (displayAbove) {
                xPos = (x * 2) + 2;
                yPos = (y * 2) + 2;
            } else {
                int stringWidth = textRenderer.getWidth(text) - 1;
                xPos = ((x + 8) * 2 + 1 + stringWidth / 2 - stringWidth);
                yPos = (y * 2) + 22;
            }

            this.matrices.push();
            this.matrices.scale(0.5F, 0.5F, 0.5F);
            this.matrices.translate(0.0F, 0.0F, 399.0F);

            drawContext.drawTextWithShadow(textRenderer, text, xPos, yPos, color);
            
            this.matrices.scale(2.0F, 2.0F, 2.0F);
            this.matrices.translate(0.0F, 0.0F, 200.0F);
            this.matrices.pop();
        }
    }
}
