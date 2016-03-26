package br.unisul.unifind.json;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
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

import br.unisul.unifind.objetos.Bloco;
import br.unisul.unifind.objetos.Campus;
import br.unisul.unifind.objetos.Sala;
import br.unisul.unifind.viewsDB.DbHelper;

/**
 * Created by Ronan Cardoso on 26/03/2016.
 */
public class DownloadJsonAsyncTaskSala extends AsyncTask<String, Void, List<Sala>> {
    ProgressDialog dialog;
    Context context;
    DbHelper dbh;

    public DownloadJsonAsyncTaskSala(Context context){
        this.dbh = new DbHelper(context);
        this.context=context;
    }


    //Exibe pop-up indicando que está sendo feito o download do JSON
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = ProgressDialog.show(context, "Aguarde", "Atualizando Salas.");
    }


    //Acessa o serviço do JSON e retorna a lista de salas
    @Override
    protected List<Sala> doInBackground(String... params) {
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
                List<Sala> salas = getSalas(json);
                return salas;
            }
        } catch (Exception e) {
            Log.e("Erro", "Falha ao acessar Web service", e);
        }
        return null;
    }



    //Depois de executada a chamada do serviço
    @Override
    protected void onPostExecute(List<Sala> result) {
        super.onPostExecute(result);

        if (result.size() > 0) {
            for (Sala sal : result) {
                dbh.insertSala(sal);
                Log.d("SALA INSERIDA: ", sal.getDescricao());
            }

        }

        dialog.dismiss();

        new DownloadJsonAsyncTaskServico(context).execute("http://ronanbr.ddns-intelbras.com.br:36666/unifind/servico/listarTodos");
    }







    //Retorna uma lista de salas com as informações retornadas do JSON
    private List<Sala> getSalas(String jsonString) {
        List<Sala> salas = new ArrayList<>();
        try {
            JSONArray salasJson = new JSONArray(jsonString);
            JSONObject sala;

            for (int i = 0; i < salasJson.length(); i++) {
                sala = new JSONObject(salasJson.getString(i));
                //Log.i("BLOCO ENCONTRADO: ", "nome=" + sala.getString("descricao"));

                Sala objetoSala = new Sala();
                objetoSala.setDescricao(sala.getString("descricao"));
                objetoSala.setId(sala.getInt("id"));

                JSONObject bloco = sala.getJSONObject("bloco");
                Bloco objetoBloco = new Bloco();
                objetoBloco.setId(bloco.getInt("id"));
                objetoBloco.setDescricao(bloco.getString("descricao"));
                objetoBloco.setLatitude(bloco.getDouble("latitude"));
                objetoBloco.setLongitude(bloco.getDouble("longitude"));

                JSONObject campus = bloco.getJSONObject("campus");
                Campus objetoCampus = new Campus();
                objetoCampus.setId(campus.getInt("id"));
                objetoCampus.setDescricao(campus.getString("descricao"));


                objetoBloco.setCampus(objetoCampus);
                objetoSala.setBloco(objetoBloco);
                salas.add(objetoSala);
            }

        } catch (JSONException e) {
            Log.e("Erro", "Erro no parsing do JSON", e);
        }
        return  salas;
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
