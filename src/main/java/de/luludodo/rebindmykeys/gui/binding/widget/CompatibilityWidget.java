package de.luludodo.rebindmykeys.gui.binding.widget;

import de.luludodo.rebindmykeys.gui.binding.screen.CompatibilityPopup;
import de.luludodo.rebindmykeys.gui.widget.ConfigWidget;
import de.luludodo.rebindmykeys.modSupport.VanillaKeyBindingWrapper;
import de.luludodo.rebindmykeys.modSupport.operationMode.OriginalMode;
import de.luludodo.rebindmykeys.util.interfaces.Action;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class CompatibilityWidget extends ConfigWidget<CompatibilityWidget, CompatibilityPopup> {
    public static class Editor {
        private final VanillaKeyBindingWrapper binding;
        private int toggleDelay;
        private OriginalMode original;
        public Editor(VanillaKeyBindingWrapper binding) {
            this.binding = binding;
            reset();
        }

        public void reset() {
            original = binding.getOriginalMode();
            toggleDelay = binding.getToggleDelay();
        }

        public OriginalMode getOriginalMode() {
            return original;
        }

        public void setOriginalMode(OriginalMode original) {
            this.original = original;
        }

        public int getToggleDelay() {
            return toggleDelay;
        }

        public void setToggleDelay(int toggleDelay) {
            this.toggleDelay = toggleDelay;
        }

        public void apply() {
            binding.setOriginalMode(original);
            binding.setToggleDelay(toggleDelay);
        }

        public boolean hasChanges() {
            return original != binding.getOriginalMode() || toggleDelay != binding.getToggleDelay();
        }
    }

    private final Editor editor;
    public CompatibilityWidget(MinecraftClient client, CompatibilityPopup parent, VanillaKeyBindingWrapper binding) {
        super(client, parent);
        editor = new Editor(binding);
    }

    @Override
    public void loadEntries() {
        addEntry(new OriginalModeEntry(this::reload, editor));
        if (editor.getOriginalMode() == OriginalMode.TOGGLE)
            addEntry(new ToggleDelayEntry(editor));
    }

    public void reset() {
        editor.reset();
        reload();
    }

    public void save() {
        editor.apply();
    }

    public boolean hasChanges() {
        return editor.hasChanges();
    }

    public static class OriginalModeEntry extends CyclingButtonEntry<OriginalMode> {
        public OriginalModeEntry(Action reload, Editor editor) {
            super(
                    Text.translatable("rebindmykeys.gui.compatibility.originalMode"),
                    value -> Text.translatable(value.getTranslationKey()),
                    editor.getOriginalMode(),
                    (button, value) -> {
                        editor.setOriginalMode(value);
                        reload.run();
                    },
                    OriginalMode.values()
            );
        }
    }

    public static class ToggleDelayEntry extends NumberEntry<Integer> {
        public ToggleDelayEntry(Editor editor) {
            super(
                    Text.translatable("rebindmykeys.gui.compatibility.toggleDelay"),
                    editor.getToggleDelay(),
                    value -> Text.translatable(value == 1 ? "rebindmykeys.gui.compatibility.tick" : "rebindmykeys.gui.compatibility.ticks"),
                    Integer::parseInt,
                    value -> value >= 0,
                    editor::setToggleDelay
            );
        }
    }
}
