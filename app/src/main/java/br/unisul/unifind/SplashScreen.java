package br.unisul.unifind;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashScreen extends Activity {
    // Timer da splash screen
         private static int SPLASH_TIME_OUT = 3000;

         @Override
         protected void onCreate(Bundle savedInstanceState) {
               super.onCreate(savedInstanceState);
               setContentView(R.layout.activity_splash_screen);

               new Handler().postDelayed(new Runnable() {
                      /*
                       * Exibindo splash com um timer.
                       */
                      @Override
                      public void run() {
                             // Esse método será executado sempre que o timer acabar
                             // E inicia a activity principal
                             Intent i = new Intent(SplashScreen.this, Main.class);
                             startActivity(i);

                             // Fecha esta activity
                             finish();
                      }
               }, SPLASH_TIME_OUT);
         }

}
