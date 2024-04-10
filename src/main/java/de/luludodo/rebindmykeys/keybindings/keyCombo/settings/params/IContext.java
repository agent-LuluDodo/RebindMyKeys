package de.luludodo.rebindmykeys.keybindings.keyCombo.settings.params;

import java.util.HashSet;
import java.util.Set;

public interface IContext {
    IContext[] getParents();
    boolean isCurrent();

    static boolean conflicts(IContext context1, IContext context2) {
        return getAllParentsAndSelf(context1).contains(context2) || getAllParentsAndSelf(context2).contains(context1);
    }

    static Set<IContext> getAllParentsAndSelf(IContext context) {
        Set<IContext> parents = new HashSet<>();
        parents.add(context);
        for (IContext parent : context.getParents()) {
            parents.addAll(getAllParentsAndSelf(parent));
        }
        return parents;
    }
}
