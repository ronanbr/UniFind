package br.unisul.unifind.json;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import br.unisul.unifind.objetos.Campus;
import br.unisul.unifind.objetos.VersaoBD;
import br.unisul.unifind.viewsDB.DbHelper;

/**
 * Created by Ronan Cardoso on 26/03/2016.
 */
public class AtualizarAsyncTask extends AsyncTask<String, Void, Integer> {
    ProgressDialog dialog;
    Context context;
    DbHelper dbh;

    public AtualizarAsyncTask(Context context) {
        this.context = context;
    }

    //Exibe pop-up indicando que está sendo feito o download do JSON
    @Override
    protected void onPreExecute() {
        dialog = ProgressDialog.show(context, "Aguarde", "Fazendo atualização dos dados.");
        super.onPreExecute();
    }


    //Acessa o serviço do JSON e retorna a lista de serviços
    @Override
    protected Integer doInBackground(String... params) {
        String urlString = params[0];
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(urlString);
        try {
            HttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();
                String json = getStringFromInputStream(instream);
                instream.close();

                JSONObject versaoJson = new JSONObject(json);

                if(versaoJson!=null){
                    return versaoJson.getInt("versaoDB");
                }

            }
        } catch (Exception e) {
            Log.e("Erro", "Falha ao acessar Web service", e);
        }
        return null;
    }


    //Depois de executada a chamada do serviço
    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        this.dbh = new DbHelper(context);

        if(result!= null){

            dbh.atualizar();

            dialog.dismiss();

            new DownloadJsonAsyncTaskCampus(context).execute("http://ronanbr.ddns-intelbras.com.br:36666/unifind/campus/listarTodos");


//        }else{
//                dialog.dismiss();
//            new AlertDialog.Builder(context)
//                    .setTitle("Erro")
//                    .setMessage("Problema na comunicação com o servidor, tente novamente mais tarde.")
//                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            android.os.Process.killProcess(android.os.Process.myPid());
//                        }
//                    })
//                    .setIcon(android.R.drawable.ic_dialog_alert)
//                    .show();
//
//                Toast.makeText(context, "Banco atualizado, versao: "+result, Toast.LENGTH_SHORT).show();
//            }


        }


    }

    // convert InputStream to String
    private static String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();

    }
}