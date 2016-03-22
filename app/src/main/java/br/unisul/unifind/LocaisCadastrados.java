package br.unisul.unifind;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.unisul.unifind.adapters.adapter;
import br.unisul.unifind.objetos.Bloco;
import br.unisul.unifind.objetos.Local;
import br.unisul.unifind.objetos.Sala;
import br.unisul.unifind.objetos.Servico;
import br.unisul.unifind.viewsDB.DbHelper;

//TODO transformar em generica.
public class LocaisCadastrados extends AppCompatActivity{

    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locais_cadastrados);

        listView = (ListView) findViewById(R.id.listView);
    }

    @Override
    protected void onResume() {
        super.onResume();

        DbHelper dbHelper = new DbHelper(this);

        List<Bloco> blocos = dbHelper.selectTodosBlocos();
        List<Sala> salas = dbHelper.selectTodasSalas();
        List<Servico> servicos = dbHelper.selectTodosServicos();

        //converte bloco em local
        List<Local> locais = new ArrayList<>();
        for (Servico svc : servicos) {
            locais.add(new Local("Serviço: " + svc.getDescricao(), "Campus: " +
                    svc.getCampus().getDescricao(), svc.getLatitude(), svc.getLongitude()));
        }

        for (Bloco b : blocos) {
            locais.add(new Local("Bloco: "+b.getDescricao(), "Campus: "+ b.getCampus().getDescricao(),
                    b.getLatitude(), b.getLongitude()));
        }
        for (Sala s : salas) {
            locais.add(new Local("Sala: "+s.getDescricao(), "Bloco: "+s.getBloco().getDescricao(),
                    s.getBloco().getLatitude(), s.getBloco().getLongitude()));
        }


        listView.setAdapter(new adapter(this, locais));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Local loc = (Local) listView.getItemAtPosition(position);

                Bundle bundle = new Bundle();
                bundle.putString("descricaoMapa", loc.getDescricao());
                bundle.putDouble("latitudeMapa", loc.getLatitude());
                bundle.putDouble("longitudeMapa", loc.getLongitude());

                Intent mapa = new Intent(LocaisCadastrados.this, Mapa.class);
                mapa.putExtras(bundle);
                startActivity(mapa);
            }
        });

    }

}