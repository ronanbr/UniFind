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

import java.util.ArrayList;
import java.util.List;

import br.unisul.unifind.adapter.adapter;
import br.unisul.unifind.objetos.Bloco;
import br.unisul.unifind.objetos.Local;
import br.unisul.unifind.viewsDB.DbHelper;

public class ResultadoBusca extends AppCompatActivity {

    private ListView listView;
    private AutoCompleteTextView edBusca;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado_busca);
        listView = (ListView) findViewById(R.id.viewResult);

        //daqui pra baixo
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        //obter alguma informação do bundle
        String filtro = bundle.getString("filtro");

        DbHelper dbHelper = new DbHelper(this);


        //TODO: Elaborar ifs para tipo de resultado ou mandar objeto do tipo 'local' para esta classe
        //talvez receber por parametros uma lista de locais
        List<Bloco> blocos = dbHelper.selectBlocos(filtro);

        //converte bloco em local
        List<Local> locais = new ArrayList<Local>();
        for (Bloco b : blocos) {
            locais.add(new Local(b.getDescricao(), b.getLatitude(), b.getLongitude()));
        }

        listView.setAdapter(new adapter(this, locais));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                Local loc = (Local) listView.getItemAtPosition(position);

                Intent mapa = new Intent(ResultadoBusca.this, Mapa.class);

                Bundle bundle = new Bundle();
                bundle.putString("descricaoMapa", loc.getDescricao());
                bundle.putDouble("latitudeMapa", loc.getLatitude());
                bundle.putDouble("longitudeMapa", loc.getLongitude());

                mapa.putExtras(bundle);
                startActivity(mapa);
            }
        });
    }
}
