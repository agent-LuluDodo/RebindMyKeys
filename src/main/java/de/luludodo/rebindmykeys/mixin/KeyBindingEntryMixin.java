package de.luludodo.rebindmykeys.mixin;

import com.google.common.collect.ImmutableList;
import de.luludodo.rebindmykeys.keyBindings.BasicKeyBinding;
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
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(ControlsListWidget.KeyBindingEntry.class)
public abstract class KeyBindingEntryMixin {
    @Shadow @Final private KeyBinding binding;

    @Shadow @Final ControlsListWidget field_2742;
    @Shadow @Final private Text bindingName;

    @Shadow @Final private ButtonWidget resetButton;
    @Shadow @Final private ButtonWidget editButton;
    @Unique
    private final ButtonWidget rebindmykeys$unbindButton = ButtonWidget.builder(
            Text.translatable("rebindmykeys.controls.unbind"),
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
                    Text.translatable("rebindmykeys.controls.unbind.tooltip")
            )
    ).narrationSupplier(
            textSupplier -> Text.translatable("rebindmykeys.narrator.controls.unbind", bindingName)
    ).build();

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ButtonWidget$Builder;dimensions(IIII)Lnet/minecraft/client/gui/widget/ButtonWidget$Builder;", ordinal = 1), index = 2)
    private int rebindmykeys$editButtonWidth(int x) {
        return x - 5;
    }

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ButtonWidget$Builder;dimensions(IIII)Lnet/minecraft/client/gui/widget/ButtonWidget$Builder;", ordinal = 2), index = 2)
    private int rebindmykeys$resetButtonWidth(int x) {
        return x - 10;
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ButtonWidget;render(Lnet/minecraft/client/gui/DrawContext;IIF)V", ordinal = 0))
    private void rebindmykeys$renderUnbindButton(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta, CallbackInfo ci) {
        resetButton.setX(x + 200);
        rebindmykeys$unbindButton.setX(x + 175);
        rebindmykeys$unbindButton.setY(y);
        rebindmykeys$unbindButton.render(context, mouseX, mouseY, tickDelta);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ButtonWidget;render(Lnet/minecraft/client/gui/DrawContext;IIF)V", ordinal = 1))
    private void rebindmykeys$renderUnsupportedTooltip(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta, CallbackInfo ci) {

    }

    @Inject(method = "children", at = @At("RETURN"), cancellable = true)
    private void rebindmykeys$children(CallbackInfoReturnable<List<? extends Element>> cir) {
        List<Element> children = new ArrayList<>(cir.getReturnValue());
        children.add(rebindmykeys$unbindButton);
        cir.setReturnValue(ImmutableList.copyOf(children));
    }

    @Inject(method = "selectableChildren", at = @At("RETURN"), cancellable = true)
    private void rebindmykeys$selectableChildren(CallbackInfoReturnable<List<? extends Selectable>> cir) {
        List<Selectable> children = new ArrayList<>(cir.getReturnValue());
        children.add(rebindmykeys$unbindButton);
        cir.setReturnValue(ImmutableList.copyOf(children));
    }

    @Inject(method = "update", at = @At("HEAD"))
    private void rebindmykeys$update(CallbackInfo ci) {
        rebindmykeys$unbindButton.active = !binding.isUnbound();
    }

    @Redirect(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;isUnbound()Z"))
    private boolean rebindmykeys$duplicates(KeyBinding keyBinding) {
        return keyBinding.isUnbound() || (keyBinding instanceof BasicKeyBinding keyboardOnlyKeyBinding && keyboardOnlyKeyBinding.isUnsupported());
    }

    @Inject(method = "update", at = @At("RETURN"))
    private void rebindmykeys$tooltip(CallbackInfo ci) {
        if (binding instanceof BasicKeyBinding keyboardOnlyKeyBinding && keyboardOnlyKeyBinding.isUnsupported()) {
            editButton.setTooltip(Tooltip.of(Text.translatable("rebindmykeys.key.unsupported.tooltip")));
        }
    }
}
