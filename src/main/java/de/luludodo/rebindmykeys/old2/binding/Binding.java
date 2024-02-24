package de.luludodo.rebindmykeys.old2.binding;

import de.luludodo.rebindmykeys.old2.binding.requirement.Requirement;
import de.luludodo.rebindmykeys.util.CollectionUtil;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Binding {
    private final String id;
    private boolean orderSensitive = true;
    private final boolean defaultOrderSensitive = true;
    private List<Requirement> requirements;
    private final List<Requirement> defaultRequirements;
    public Binding(String id, Requirement... requirements) {
        this.id = id;
        defaultRequirements = List.of(requirements);
        this.requirements = new ArrayList<>(defaultRequirements);
    }

    public String getId() {
        return id;
    }

    public Text getTranslation() {
        return Text.translatable(id);
    }

    public Text getValueTranslation() {
        MutableText translation = Text.empty();
        AtomicBoolean first = new AtomicBoolean(true);
        CollectionUtil.reverseForEach(requirements, requirement -> {
            if (first.get()) {
                first.set(false);
            } else {
                translation.append(" + ");
            }
            translation.append(requirement.getTranslation());
        });
        return translation;
    }

    public int addRequirement(Requirement requirement) {
        requirements.add(requirement);
        return requirements.size() - 1;
    }
    public boolean removeRequirement(int index) {
        if (index < 0 || index >= requirements.size())
            return false;
        requirements.remove(index);
        return true;
    }
    public void setRequirements(List<Requirement> requirements) {
        this.requirements = new ArrayList<>(requirements);
    }
    public List<Requirement> getRequirements() {
        return Collections.unmodifiableList(requirements);
    }

    public void reset() {
        requirements = new ArrayList<>(defaultRequirements);
    }
}
