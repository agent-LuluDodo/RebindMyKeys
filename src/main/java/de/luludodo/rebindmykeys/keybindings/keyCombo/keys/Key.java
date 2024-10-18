package de.luludodo.rebindmykeys.keybindings.keyCombo.keys;

import de.luludodo.rebindmykeys.gui.keyCombo.widget.KeyComboWidget;
import de.luludodo.rebindmykeys.keybindings.registry.LuluRegistry;
import de.luludodo.rebindmykeys.util.interfaces.JsonLoadable;
import de.luludodo.rebindmykeys.util.interfaces.JsonSavable;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

import java.util.List;

public interface Key extends JsonSavable, JsonLoadable, LuluRegistry.Indexable {
    void onKeyDown(InputUtil.Key key);
    void onKeyUp(InputUtil.Key key);
    void release();
    void press();
    boolean isPressed();
    Text getText();
    String getTranslation();
    List<KeyComboWidget.KeyEntry.Button> getButtons();
}
