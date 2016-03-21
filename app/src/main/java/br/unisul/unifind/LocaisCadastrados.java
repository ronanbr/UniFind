package br.unisul.unifind;

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

        //converte bloco em local
        List<Local> locais = new ArrayList<>();
        for (Bloco b : blocos) {
            locais.add(new Local(b.getDescricao(), b.getLatitude(), b.getLongitude()));
        }
        for (Sala s : salas) {
            locais.add(new Local(s.getDescricao(), s.getLatitude(), s.getLongitude()));
        }

        listView.setAdapter(new adapter(this, locais));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Local teste = (Local) listView.getItemAtPosition(position);
                Toast.makeText(LocaisCadastrados.this, "Um dia mostra no mapa o item " + teste.getDescricao()+"!", Toast.LENGTH_SHORT).show();
            }
        });

    }

}