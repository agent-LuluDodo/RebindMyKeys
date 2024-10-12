package de.luludodo.rebindmykeys.profiles;

import java.util.UUID;

public class DuplicatedProfile extends Profile {

    private boolean deleted = false;
    public DuplicatedProfile(Profile original, UUID uuid) {
        super(
                uuid,
                true,
                original.getConfig().duplicate(uuid.toString()),
                original.getGlobal().duplicate(uuid.toString())
        );
        rename("Copy of " + getName());
    }

    public void delete() {
        deleted = true;
        getGlobal().delete();
        getConfig().delete();
    }

    public Profile create() {
        getGlobal().save();
        getConfig().save();
        return ProfileManager.create(getUUID(), getName());
    }
}
