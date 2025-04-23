package org.example.test;

import org.example.core.EngineManager;
import org.example.core.WindowManager;
import org.example.core.utils.Consts;
import org.lwjgl.Version;

public class Launcher {

   private static WindowManager window;
   private static TestGame game;

    public static void main(String [] args){
         window = new WindowManager(Consts.TITLE, 1000,900,false);
        game = new TestGame();
        EngineManager engine = new EngineManager();

         try{
             engine.start();
         }
         catch (Exception e){
             e.printStackTrace();
         }


    }

    public static WindowManager getWindow() {
        return window;
    }

    public static TestGame getGame() {
        return game;
    }
}
