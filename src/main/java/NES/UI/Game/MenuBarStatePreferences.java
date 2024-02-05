package NES.UI.Game;

import java.util.prefs.Preferences;

public class MenuBarStatePreferences {

    public static MenuBarStatePreferences instance = new MenuBarStatePreferences();

    private Preferences prefs;

    public static final String NAMETABLE_GRIDLINES = "debug.pixel_grid";
    public static final String NAMETABLE_HOVER = "debug.nametable_hover";
    public static final String PIXEL_HOVER = "debug.pixel_hover";


    private MenuBarStatePreferences() {
        prefs = Preferences.userRoot().node(this.getClass().getName());
    }

    public void saveState(String key, boolean value) {
        prefs.putBoolean(key, value);
    }

    public boolean getState(String key) {
        return prefs.getBoolean(key, false);
    }

    public boolean isNametableHover() {
        return getState(NAMETABLE_HOVER);
    }

    public boolean isPixelHover() {
        return getState(PIXEL_HOVER);
    }

    public boolean isNametableGridlines() {
        return getState(NAMETABLE_GRIDLINES);
    }
}
