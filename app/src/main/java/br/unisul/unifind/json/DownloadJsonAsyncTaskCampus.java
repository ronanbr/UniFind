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

import br.unisul.unifind.objetos.Campus;
import br.unisul.unifind.objetos.Servico;
import br.unisul.unifind.viewsDB.DbHelper;

/**
 * Created by Ronan Cardoso on 26/03/2016.
 */
public class DownloadJsonAsyncTaskCampus extends AsyncTask<String, Void, List<Campus>> {
    ProgressDialog dialog;
    Context context;
    DbHelper dbh;

    public DownloadJsonAsyncTaskCampus(Context context){
        this.dbh = new DbHelper(context);
        this.context=context;
    }

    //Exibe pop-up indicando que está sendo feito o download do JSON
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = ProgressDialog.show(context, "Aguarde", "Atualizando Campi...");
    }


    //Acessa o serviço do JSON e retorna a lista de serviços
    @Override
    protected List<Campus> doInBackground(String... params) {
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
                List<Campus> campi = getCampi(json);
                return campi;
            }
        } catch (Exception e) {
            Log.e("Erro", "Falha ao acessar Web service", e);
        }
        return null;
    }



    //Depois de executada a chamada do serviço
    @Override
    protected void onPostExecute(List<Campus> result) {
        super.onPostExecute(result);

        if (result.size()>0) {
            for (Campus camp : result) {
                dbh.insertCampus(camp);
                //Log.d("CAMPUS INSERIDO: ", camp.getDescricao());
            }
        }

        dialog.dismiss();
        new DownloadJsonAsyncTaskBloco(context).execute("http://ronanbr.ddns-intelbras.com.br:36666/unifind/bloco/listarTodos");
    }







    //Retorna uma lista de blocos com as informações retornadas do JSON
    private List<Campus> getCampi(String jsonString) {
        List<Campus> campi = new ArrayList<>();
        try {
            JSONArray campiJson = new JSONArray(jsonString);
            JSONObject campus;

            for (int i = 0; i < campiJson.length(); i++) {
                campus = new JSONObject(campiJson.getString(i));

                Campus objetoCampus = new Campus();
                objetoCampus.setId(campus.getInt("id"));
                objetoCampus.setDescricao(campus.getString("descricao"));

                campi.add(objetoCampus);
                //Log.i("CAMPUS ENCONTRADO: ", objetoCampus.toString());
            }

        } catch (JSONException e) {
            Log.e("Erro", "Erro no parsing do JSON", e);
        }
        return  campi;
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
