package org.example;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main implements NativeKeyListener {
    private static final int key1=NativeKeyEvent.VC_Q;
    private static final int key2=NativeKeyEvent.VC_ALT;
    private static final int key3=NativeKeyEvent.VC_W;
    private boolean QPressed = false, ALTPressed = false,WPressed = false;
    public static String dil,dil1,tessdata,googleKey,mouseIcon;

    public static final void main(String[] args) throws NativeHookException, FileNotFoundException, URISyntaxException {

        // env.json dosyasını bulma ve dagıtma işlemi
        File jarDosya = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        String jarDizin = jarDosya.getParent();
        String envDosyasi = new File(jarDizin, "src/env.json").getPath();

        JsonReader jsonReader = Json.createReader(new FileReader(envDosyasi));
        JsonObject jsonObject = jsonReader.readObject();
        googleKey = jsonObject.getString("googleKey");
        dil = jsonObject.getString("HangiDile");
        dil1 = jsonObject.getString("HangiDilden");
        tessdata = new File(jarDizin, "src/tessdata").getPath();
        mouseIcon = new File(jarDizin, "src/MouseIcon.png").getPath();
        jsonReader.close();

        // jnativehook rütieli
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        GlobalScreen.registerNativeHook();
        GlobalScreen.addNativeKeyListener(new Main());
    }
    static GetImage getImage = new GetImage();
    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
    }
    int i=0;
    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {

        switch (e.getKeyCode()) {
            case key1:
                QPressed = true;
                break;
            case key2:
                ALTPressed = true;
                break;
            case key3:
                WPressed = true;
                break;
            default:
                break;
        }
        if (QPressed && ALTPressed) { // Programı calıştırma
            QPressed = false;
            ALTPressed = false;

            System.gc(); // Java garbage collecter a talep yolluyoruz
            draw.Start(); // Programın Ana merkezi
            Clear(); // Kendimce optimizasyon
            System.gc();
        }
        // Programi Kapatma
        if(ALTPressed && WPressed){
            GlobalScreen.removeNativeKeyListener(this);
            try {
                GlobalScreen.unregisterNativeHook();
            } catch (NativeHookException ex) {
                ex.printStackTrace();
            }
            System.exit(0);
        }
    }
    private void Clear() {
        getImage.Clear();
        draw.Clear();
    }
    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        switch (e.getKeyCode()) {
            case key1:
                QPressed = false;
                break;
            case key2:
                ALTPressed = false;
                break;
            case key3:
                WPressed = false;
                break;
            default:
                break;
        }
    }
}
