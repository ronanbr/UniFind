package br.unisul.unifind.json;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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
import br.unisul.unifind.objetos.Servico;
import br.unisul.unifind.viewsDB.DbHelper;

/**
 * Created by Ronan Cardoso on 26/03/2016.
 */
public class DownloadJsonAsyncTaskServico extends AsyncTask<String, Void, List<Servico>> {
    ProgressDialog dialog;
    Context context;
    DbHelper dbh;

    public DownloadJsonAsyncTaskServico(Context context){
        this.dbh = new DbHelper(context);
        this.context=context;
    }

    //Exibe pop-up indicando que está sendo feito o download do JSON
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = ProgressDialog.show(context, "Aguarde", "Atualizando Serviço.");
    }


    //Acessa o serviço do JSON e retorna a lista de serviços
    @Override
    protected List<Servico> doInBackground(String... params) {
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
                List<Servico> servicos = getServicos(json);
                return servicos;

            }
        } catch (Exception e) {
            Log.e("Erro", "Falha ao acessar Web service", e);
        }
        return null;
    }



    //Depois de executada a chamada do serviço
    @Override
    protected void onPostExecute(List<Servico> result) {
        super.onPostExecute(result);
        if (result.size() > 0) {
            for (Servico ser : result) {
                dbh.insertServico(ser);
                Log.d("SERVICO INSERIDO: ", ser.getDescricao());
            }

            dialog.dismiss();

            Toast.makeText(context, "Dados atualizados com sucesso!", Toast.LENGTH_SHORT).show();

        }
    }







    //Retorna uma lista de blocos com as informações retornadas do JSON
    private List<Servico> getServicos(String jsonString) {
        List<Servico> servicos = new ArrayList<Servico>();
        try {
            JSONArray servicosJson = new JSONArray(jsonString);
            JSONObject servico;

            for (int i = 0; i < servicosJson.length(); i++) {
                servico = new JSONObject(servicosJson.getString(i));

                Servico objetoServico = new Servico();
                objetoServico.setDescricao(servico.getString("descricao"));
                objetoServico.setId(servico.getInt("id"));
                objetoServico.setLatitude(servico.getDouble("latitude"));
                objetoServico.setLongitude(servico.getDouble("longitude"));

                JSONObject campus = servico.getJSONObject("campus");
                Campus objetoCampus = new Campus();
                objetoCampus.setId(campus.getInt("id"));
                objetoCampus.setDescricao(campus.getString("descricao"));

                objetoServico.setCampus(objetoCampus);
                servicos.add(objetoServico);

                Log.i("SERVICO ENCONTRADO: ", objetoServico.toString());
            }

        } catch (JSONException e) {
            Log.e("Erro", "Erro no parsing do JSON", e);
        }
        return  servicos;
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
