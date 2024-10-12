package de.luludodo.rebindmykeys.keybindings.keyCombo.settings.params;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import de.luludodo.rebindmykeys.util.interfaces.JsonSavable;

import java.util.HashSet;
import java.util.Set;

public interface IContext extends JsonSavable {
    IContext[] getParents();
    boolean isCurrent();
    String getId();

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

    default JsonElement save() {
        return new JsonPrimitive(getId());
    }

    static IContext load(JsonElement json) {
        //RebindMyKeys.DEBUG.info("Loading context: " + json);
        IContext context = IContextRegistry.get(json.getAsString());
        //RebindMyKeys.DEBUG.info("Class: {} ID: {}", context.getClass().getSimpleName(), context.getId());
        return context;
    }
}
