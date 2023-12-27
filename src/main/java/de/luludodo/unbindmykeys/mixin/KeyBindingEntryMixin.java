package de.luludodo.unbindmykeys.mixin;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(ControlsListWidget.KeyBindingEntry.class)
public abstract class KeyBindingEntryMixin {
    @Shadow @Final private KeyBinding binding;

    @Shadow @Final ControlsListWidget field_2742;
    @Shadow @Final private Text bindingName;

    @Unique
    private final ButtonWidget unbindButton = ButtonWidget.builder(
            Text.translatable("controls.unbind"),
            button -> {
                field_2742.client.options.setKeyCode(binding, InputUtil.UNKNOWN_KEY);
                field_2742.update();
            }
    ).dimensions(
            0,
            0,
            20,
            20
    ).tooltip(
            Tooltip.of(
                    Text.translatable("controls.unbind.tooltip")
            )
    ).narrationSupplier(
            textSupplier -> Text.translatable("narrator.controls.unbind", bindingName)
    ).build();

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ButtonWidget$Builder;dimensions(IIII)Lnet/minecraft/client/gui/widget/ButtonWidget$Builder;", ordinal = 1), index = 2)
    private int editButtonWidth(int x) {
        return x - 5;
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ButtonWidget$Builder;dimensions(IIII)Lnet/minecraft/client/gui/widget/ButtonWidget$Builder;", ordinal = 2), index = 2)
    private int resetButtonWidth(int x) {
        return x - 10;
    }

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ButtonWidget;setX(I)V", ordinal = 0))
    private int resetButtonX(int x) {
        return x + 10;
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ButtonWidget;render(Lnet/minecraft/client/gui/DrawContext;IIF)V", ordinal = 0))
    private void renderUnbindButton(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta, CallbackInfo ci) {
        unbindButton.setX(x + 175);
        unbindButton.setY(y);
        unbindButton.render(context, mouseX, mouseY, tickDelta);
    }

    @Inject(method = "children", at = @At("RETURN"), cancellable = true)
    private void children(CallbackInfoReturnable<List<? extends Element>> cir) {
        List<Element> children = new ArrayList<>(cir.getReturnValue());
        children.add(unbindButton);
        cir.setReturnValue(ImmutableList.copyOf(children));
    }

    @Inject(method = "selectableChildren", at = @At("RETURN"), cancellable = true)
    private void selectableChildren(CallbackInfoReturnable<List<? extends Selectable>> cir) {
        List<Selectable> children = new ArrayList<>(cir.getReturnValue());
        children.add(unbindButton);
        cir.setReturnValue(ImmutableList.copyOf(children));
    }

    @Inject(method = "update", at = @At("HEAD"))
    private void update(CallbackInfo ci) {
        unbindButton.active = !binding.isUnbound();
    }
}
