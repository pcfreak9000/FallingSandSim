package de.pcfreak9000.pixelsimtest.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import de.pcfreak9000.pixelsimtest.PixelSimTest;

public class DesktopLauncher {
    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setForegroundFPS(45);
        new Lwjgl3Application(new PixelSimTest(), config);
    }
}
