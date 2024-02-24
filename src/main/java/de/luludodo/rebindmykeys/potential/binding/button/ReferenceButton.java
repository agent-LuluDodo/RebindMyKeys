package de.luludodo.rebindmykeys.potential.binding.button;

import com.google.gson.JsonElement;
import de.luludodo.rebindmykeys.util.BindingUtil;
import de.luludodo.rebindmykeys.util.JsonUtil;
import de.luludodo.rebindmykeys.util.Modifier;
import net.minecraft.client.util.InputUtil;

import java.util.List;

public class ReferenceButton extends BasicButton {
    public static ReferenceButton fromJson(JsonElement json) {
        return new ReferenceButton(
                JsonUtil.requireString(json)
        );
    }

    private String targetId;
    public ReferenceButton(String targetId) {
        this.targetId = targetId;
        BindingUtil.registerLoadingDoneCallback(this::onAllBindingsLoaded);
    }

    public void setTargetId(String targetId) {
        validateTargetId(targetId);
        this.targetId = targetId;
    }
    public String getTargetId() {
        return targetId;
    }

    private boolean broken = true;
    private boolean shouldValidateTargetId = false;
    private void onAllBindingsLoaded() {
        shouldValidateTargetId = true;
        validateTargetId(targetId);
    }
    private void validateTargetId(String targetId) {
        if (!shouldValidateTargetId)
            return;
        if (BindingUtil.contains(targetId)) {
            broken = false;
        } else {
            throw new IllegalArgumentException("Target id '" + targetId + "' isn't registered");
        }
    }

    @Override
    public boolean validate(InputUtil.Key key, List<Modifier> modifiers) {
        if (broken)
            return false;
        return BindingUtil.get(targetId).valid(key, modifiers);
    }
}
