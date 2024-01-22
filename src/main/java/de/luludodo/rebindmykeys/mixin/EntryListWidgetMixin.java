package de.luludodo.rebindmykeys.mixin;

import de.luludodo.rebindmykeys.meta.Comparator;
import net.minecraft.client.gui.widget.EntryListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntryListWidget.class)
public abstract class EntryListWidgetMixin {
    @Inject(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/EntryListWidget;clickedHeader(II)V", shift = At.Shift.AFTER), cancellable = true)
    private void rebindmykeys$fixBugIn1_20(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (Comparator.compareMod("vanilla", "<=1.20.3"))
            cir.setReturnValue(false);
    }
}
