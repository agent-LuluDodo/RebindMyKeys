package de.luludodo.rebindmykeys.gui.profile.widget;

import de.luludodo.rebindmykeys.RebindMyKeys;
import de.luludodo.rebindmykeys.gui.binding.screen.KeyBindingScreen;
import de.luludodo.rebindmykeys.gui.profile.screen.ProfileConfigPopup;
import de.luludodo.rebindmykeys.gui.widget.ConfigWidget;
import de.luludodo.rebindmykeys.gui.widget.IconButtonWidget;
import de.luludodo.rebindmykeys.profiles.DuplicatedProfile;
import de.luludodo.rebindmykeys.profiles.Profile;
import de.luludodo.rebindmykeys.profiles.ProfileManager;
import de.luludodo.rebindmykeys.util.CollectionUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class ProfileConfigWidget extends ConfigWidget {
    private static final Comparator<Entry> COMPARATOR = (e1, e2) -> ((ProfileEntry)e1).profile.getName().compareToIgnoreCase(((ProfileEntry)e2).profile.getName());
    private Profile currentProfile = ProfileManager.getCurrentProfile();
    private boolean hasChanges = false;
    public ProfileConfigWidget(MinecraftClient client, ProfileConfigPopup parent) {
        super(client, parent);
    }

    @Override
    public void loadEntries() {
        for (UUID id : ProfileManager.options()) {
            addEntry(new ProfileEntry(ProfileManager.get(id)));
        }
        sort();
    }

    public void setCurrentProfile(Profile profile) {
        currentProfile = profile;
        for (Entry child : children()) {
            ((ProfileEntry) child).updateCurrentProfile();
        }
        hasChanges = true;
    }
    public boolean isCurrentProfile(Profile profile) {
        return currentProfile.getUUID().equals(profile.getUUID());
    }

    private final List<Profile> toDelete = new ArrayList<>();
    public void delete(Profile profile) {
        toDelete.add(profile);
    }

    public void restore(Profile profile) {
        toDelete.remove(profile);
    }

    public void save() {
        hasChanges = false;
        children().forEach(entry -> ((ProfileEntry) entry).save());
        for (Profile delete : toDelete) {
            if (!(delete instanceof DuplicatedProfile))
                ProfileManager.delete(delete.getUUID());
        }
        toDelete.clear();
        ProfileManager.setCurrentProfile(currentProfile);
        if (getParent().getParent() instanceof KeyBindingScreen keyBindingScreen)
            keyBindingScreen.reloadEntries();
    }

    public boolean removeEntry(Entry entry) {
        hasChanges = true;
        return super.removeEntry(entry);
    }

    public boolean hasChanges() {
        return hasChanges || CollectionUtil.any(children(), entry -> ((ProfileEntry) entry).hasChanges());
    }

    private void sort() {
        children().sort(COMPARATOR);
    }

    public class ProfileEntry extends Entry {
        private TextFieldWidget name;
        private final IconButtonWidget select = IconButtonWidget.builder(
                Identifier.of("rebindmykeys", "textures/gui/star.png"),
                this::onSelectPressed
        ).size(20, 20).iconTextureSize(18, 36).setRenderBackground(false).build();
        private final IconButtonWidget duplicate = IconButtonWidget.builder(
                Identifier.of("rebindmykeys", "textures/gui/copy.png"),
                this::onDuplicatePressed
        ).size(20, 20).build();
        private final IconButtonWidget delete = IconButtonWidget.builder(
                Identifier.of("rebindmykeys", "textures/gui/remove.png"),
                this::onDeletePressed
        ).size(20, 20).build();
        private final IconButtonWidget restore = IconButtonWidget.builder(
                Identifier.of("rebindmykeys", "textures/gui/restore.png"),
                this::onRestorePressed
        ).size(20, 20).build();
        private Profile profile;
        private boolean isCurrentProfile;
        private boolean deleted = false;
        private boolean isDuplicate;
        private boolean hasChanges;
        private final boolean editable;
        public ProfileEntry(Profile profile) {
            super(Text.of(profile.getUUID()));
            this.profile = profile;
            isDuplicate = profile instanceof DuplicatedProfile;
            hasChanges = isDuplicate;
            editable = profile.isEditable();
            updateCurrentProfile();
        }

        @Override
        public void setTextRenderer(TextRenderer textRenderer) {
            super.setTextRenderer(textRenderer);
            name = new TextFieldWidget(
                    textRenderer,
                    0,
                    20,
                    Text.empty()
            );
            name.setText(profile.getName());
            name.setCursor(0, false); // doesn't render the text until you click on it otherwise
            name.setDrawsBackground(false);
        }

        @Override
        public Text getTitle() {
            return Text.of(profile.getUUID());
        }

        @Override
        protected Selectable getSelectable() { return null; }
        @Override
        protected Element getChild() { return null; }

        public void save() {
            hasChanges = false;
            if (deleted) return;

            if (isDuplicate) {
                profile = ((DuplicatedProfile) profile).create();
                isDuplicate = false;
            }

            if (editable)
                profile.rename(name.getText());
        }

        private void onSelectPressed(ButtonWidget button) {
            setCurrentProfile(profile);
        }

        private void onDuplicatePressed(ButtonWidget button) {
            profile.rename(name.getText());
            addEntry(children().indexOf(this) + 1, new ProfileEntry(new DuplicatedProfile(profile, UUID.randomUUID())));
            sort();
            getParent().resize();
        }

        private void onDeletePressed(ButtonWidget button) {
            if (deleted) {
                removeEntry(this);
                sort();
                getParent().resize();
            } else {
                delete(profile);
                hasChanges = true;
                deleted = true;
            }
        }

        private void onRestorePressed(ButtonWidget button) {
            restore(profile);
            hasChanges = true;
            deleted = false;
        }

        public void updateCurrentProfile() {
            isCurrentProfile = isCurrentProfile(profile);
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return deleted? List.of(delete, restore) : List.of(select, name, duplicate, delete);
        }

        @Override
        public List<? extends Element> children() {
            return deleted? List.of(delete, restore) : List.of(select, name, duplicate, delete);
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovered, float delta) {
            if (!deleted) {
                select.setUV(0, isCurrentProfile? 18 : 0);
                select.setPosition(x,  y);
                select.render(context, mouseX, mouseY, delta);
            }
            name.active = !deleted && editable;
            name.setWidth(width - 68);
            name.setPosition(x + 22, y + height / 2 - getTextRenderer().fontHeight / 2);
            name.render(context, mouseX, mouseY, delta);
            if (deleted) {
                restore.setPosition(x + width - 20, y);
                restore.render(context, mouseX, mouseY, delta);
            } else {
                duplicate.setPosition(x + width - 40, y);
                duplicate.render(context, mouseX, mouseY, delta);
            }
            delete.active = !isCurrentProfile && editable;
            delete.setPosition(x + width - (deleted? 40 : 20), y);
            delete.render(context, mouseX, mouseY, delta);
        }

        @Override
        protected void renderWidget(int x, int y, int width, int height, DrawContext context, int mouseX, int mouseY, float delta) {}

        public boolean hasChanges() {
            return hasChanges;
        }
    }
}
