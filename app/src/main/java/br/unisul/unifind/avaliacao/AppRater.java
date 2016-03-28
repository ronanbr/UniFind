package br.unisul.unifind.avaliacao;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import br.unisul.unifind.R;

//AppRater.app_launched(this);
public class AppRater {
    private final static String APP_TITLE = "UniFind";// App Name
    private final static String APP_PNAME = "br.unisul.unifind";// Package Name

    private final static int DAYS_UNTIL_PROMPT = 0;//Min number of days
    private final static int LAUNCHES_UNTIL_PROMPT = 3;//Min number of launches

    public static void app_launched(Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("unifind", 0);
        if (prefs.getBoolean("dontshowagain", false)) { return ; }

        SharedPreferences.Editor editor = prefs.edit();

        // Increment launch counter
        long launch_count = prefs.getLong("launch_count", 0) + 1;
        editor.putLong("launch_count", launch_count);

        // Get date of first launch
        Long date_firstLaunch = prefs.getLong("date_firstlaunch", 0);
        if (date_firstLaunch == 0) {
            date_firstLaunch = System.currentTimeMillis();
            editor.putLong("date_firstlaunch", date_firstLaunch);
        }

        // Wait at least n days before opening
        if (launch_count >= LAUNCHES_UNTIL_PROMPT) {
            if (System.currentTimeMillis() >= date_firstLaunch + 
                    (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
                showRateDialog(mContext, editor);
            }
        }

        editor.commit();
    }   

    public static void showRateDialog(final Context mContext, final SharedPreferences.Editor editor) {
        new AlertDialog.Builder(mContext)
                .setTitle("Atenção")
                .setMessage("O UniFind é resultado de um Trabalho de Conclusão de Curso, desenvolvido por Ronan Cardoso e Lucas Rosa, " +
                        "pedimos a gentileza de avaliar o APP para análise e utilização dos resultados no artigo final, " +
                        "seus dados pessoais não serão coletados.")
                .setPositiveButton(R.string.avaliar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://goo.gl/forms/ub0lsL20Qr")));
                        mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PNAME)));
                        if (editor != null) {
                            editor.putBoolean("dontshowagain", true);
                            editor.commit();
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.maistarde, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setNeutralButton(R.string.nao, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        if (editor != null) {
                            editor.putBoolean("dontshowagain", true);
                            editor.commit();
                        }
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();


//        final Dialog dialog = new Dialog(mContext);
//        dialog.setTitle("Atenção");
//
//        LinearLayout ll = new LinearLayout(mContext);
//        ll.setOrientation(LinearLayout.VERTICAL);
//
//        TextView tv = new TextView(mContext);
//        tv.setText("\nO UniFind é resultado de um Trabalho de Conclusão de Curso, por favor, avalie este app. Agradecemos sua colaboração!\n");
//        tv.setWidth(240);
//        tv.setPadding(4, 0, 4, 10);
//        ll.addView(tv);

//        Button b1 = new Button(mContext);
//        b1.setText("Avaliar " + APP_TITLE);
//        b1.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//
//                dialog.dismiss();
//            }
//        });
//        ll.addView(b1);
//
//        Button b2 = new Button(mContext);
//        b2.setText("Depois");
//        b2.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//        ll.addView(b2);
//
//        Button b3 = new Button(mContext);
//        b3.setText("Não, Obrigado");
//        b3.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                if (editor != null) {
//                    editor.putBoolean("dontshowagain", true);
//                    editor.commit();
//                }
//                dialog.dismiss();
//            }
//        });
//        ll.addView(b3);
//
//        dialog.setContentView(ll);
//        dialog.show();
//        Dialog d = new Dialog();
//    }
    }
}