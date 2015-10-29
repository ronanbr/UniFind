package br.unisul.unifind;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import br.unisul.unifind.adapter.adapter;
import br.unisul.unifind.objetos.Bloco;
import br.unisul.unifind.viewsDB.DbHelper;

public class ResultadoBusca extends AppCompatActivity {

    private ListView listView;
    private AutoCompleteTextView edBusca;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado_busca);

        listView = (ListView) findViewById(R.id.viewResult);
        edBusca = (AutoCompleteTextView) findViewById(R.id.edBlocoBusca);


        //daqui pra baixo
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        //obter alguma informação do bundle
        String filtro = bundle.getString("filtro");
        Log.i("teste", "Filtro da busca de Bloco:"+filtro);


        DbHelper dbHelper = new DbHelper(this);
        List<Bloco> blocos = dbHelper.selectBlocos(filtro);
        listView.setAdapter(new adapter(this, blocos));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                Bloco teste = (Bloco) listView.getItemAtPosition(position);
                Toast.makeText(ResultadoBusca.this, "Um dia mostra no mapa o item " + teste.getDescricao()+"!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
