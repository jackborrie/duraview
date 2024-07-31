package dev.inlitum.mixin;

import dev.inlitum.DuraView;
import dev.inlitum.config.DuraViewConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrawContext.class)
public class DurabilityMixin {

    @Final
    @Shadow
    private MatrixStack matrices;

    @Final
    @Shadow
    private MinecraftClient client;



    @Inject(at = @At("HEAD"), method = "drawItemInSlot(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", cancellable = true)
    private void drawItemInSlot(TextRenderer textRenderer, ItemStack stack, int x, int y, String countOverride, CallbackInfo ci) {
        if (stack.isEmpty()) {
            ci.cancel();
            return;
        }

        if (DuraView.config.enabled) {
            DrawContext thisObject = (DrawContext)(Object)this;

            this.matrices.push();
            if (stack.getCount() != 1 || countOverride != null) {
                String string = countOverride == null ? String.valueOf(stack.getCount()) : countOverride;
                this.matrices.translate(0.0F, 0.0F, 200.0F);
                thisObject.drawText(textRenderer, string, x + 19 - 2 - textRenderer.getWidth(string), y + 6 + 3, 16777215, true);
            }

            int k;
            int l;
            if (stack.isItemBarVisible()) {
                int maxDamage = ((ToolItem)stack.getItem()).getMaterial().getDurability();
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

            ClientPlayerEntity clientPlayerEntity = this.client.player;
            float f = clientPlayerEntity == null ? 0.0F : clientPlayerEntity.getItemCooldownManager().getCooldownProgress(stack.getItem(), this.client.getRenderTickCounter().getTickDelta(true));
            if (f > 0.0F) {
                k = y + MathHelper.floor(16.0F * (1.0F - f));
                l = k + MathHelper.ceil(16.0F * f);
                thisObject.fill(RenderLayer.getGuiOverlay(), x, k, x + 16, l, Integer.MAX_VALUE);
            }

            this.matrices.pop();

            ci.cancel();
        }
    }

}
