package de.luludodo.rebindmykeys.old3.binding.validator;

import de.luludodo.rebindmykeys.util.BindingUtil;
import de.luludodo.rebindmykeys.util.Modifier;
import net.minecraft.client.util.InputUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ReferenceValidator implements Validator {
    @NotNull private String targetId;
    public ReferenceValidator(@NotNull String targetId) {
        this.targetId = targetId;
        BindingUtil.registerLoadingDoneCallback(this::onLoadingDone);
    }

    private boolean broken = false;
    private boolean shouldValidate = false;
    private void onLoadingDone() {
        shouldValidate = true;
        validate(targetId); // can't do this on <init> because loading is still in progress!
    }

    private void validate(String targetId) { // Original name: "throwAndBreakIfTargetIdIsNotRegistered" (naming is hard...)
        if (!shouldValidate)
            return;
        if (BindingUtil.contains(targetId)) {
            broken = false;
        } else {
            broken = true;
            throw new IllegalArgumentException("targetId '" + targetId + "' is not registered");
        }
    }

    public void setTargetId(@NotNull String targetId) {
        validate(targetId);
        this.targetId = targetId;
    }
    public @NotNull String getTargetId() {
        return targetId;
    }

    @Override
    public boolean valid(InputUtil.Key key, List<Modifier> modifiers) {
        if (broken)
            return false;
        return BindingUtil.get(targetId).valid(key, modifiers);
    }
}
