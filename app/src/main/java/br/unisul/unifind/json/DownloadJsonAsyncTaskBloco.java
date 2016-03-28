package br.unisul.unifind.json;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import br.unisul.unifind.Main;
import br.unisul.unifind.objetos.Bloco;
import br.unisul.unifind.objetos.Campus;
import br.unisul.unifind.viewsDB.DbHelper;

/**
 * Created by Ronan Cardoso on 26/03/2016.
 */
public class DownloadJsonAsyncTaskBloco extends AsyncTask<String, Void, List<Bloco>> {
    ProgressDialog dialog;
    Context context;
    DbHelper dbh;

    public DownloadJsonAsyncTaskBloco(Context context){
        this.dbh = new DbHelper(context);
        this.context=context;
    }


    //Exibe pop-up indicando que está sendo feito o download do JSON
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = ProgressDialog.show(context, "Aguarde", "Atualizando Blocos...");
    }


    //Acessa o serviço do JSON e retorna a lista de pessoas
    @Override
    protected List<Bloco> doInBackground(String... params) {
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
                List<Bloco> blocos = getBlocos(json);
                return blocos;
            }
        } catch (Exception e) {
            Log.e("Erro", "Falha ao acessar Web service", e);
        }
        return null;
    }



    //Depois de executada a chamada do serviço
    @Override
    protected void onPostExecute(List<Bloco> result) {
        super.onPostExecute(result);
        if (result.size() > 0) {
            for (Bloco blo : result) {
                dbh.insertBloco(blo);
                //Log.d("BLOCO INSERIDO: ", blo.getDescricao());
            }

        }
        dialog.dismiss();


        //chama atualização de salas
        new DownloadJsonAsyncTaskSala(context).execute("http://ronanbr.ddns-intelbras.com.br:36666/unifind/sala/listarTodas");


    }







    //Retorna uma lista de blocos com as informações retornadas do JSON
    private List<Bloco> getBlocos(String jsonString) {
        List<Bloco> blocos = new ArrayList<Bloco>();
        try {
            JSONArray blocosJson = new JSONArray(jsonString);
            JSONObject bloco;

            for (int i = 0; i < blocosJson.length(); i++) {
                bloco = new JSONObject(blocosJson.getString(i));

                Bloco objetoBloco = new Bloco();
                objetoBloco.setDescricao(bloco.getString("descricao"));
                objetoBloco.setId(bloco.getInt("id"));
                objetoBloco.setLatitude(bloco.getDouble("latitude"));
                objetoBloco.setLongitude(bloco.getDouble("longitude"));

                JSONObject campus = bloco.getJSONObject("campus");
                Campus objetoCampus = new Campus();
                objetoCampus.setId(campus.getInt("id"));
                objetoCampus.setDescricao(campus.getString("descricao"));

                objetoBloco.setCampus(objetoCampus);
                blocos.add(objetoBloco);
            }

        } catch (JSONException e) {
            Log.e("Erro", "Erro no parsing do JSON", e);
        }
        return  blocos;
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
