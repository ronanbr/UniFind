package br.unisul.unifind;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import br.unisul.unifind.objetos.ItemMenu;
import br.unisul.unifind.objetos.Tabela;

public class BuscaLocal extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busca_local);


        listView = (ListView) findViewById(R.id.listViewBusca);

        List<Tabela> tables = new ArrayList<Tabela>();

        tables.add(new Tabela("Blocos", "blocos"));
        tables.add(new Tabela("Salas", "salas"));
        tables.add(new Tabela("Serviços", "servicos"));

        ArrayAdapter<Tabela> adp = new ArrayAdapter<Tabela>(this, android.R.layout.simple_expandable_list_item_1, tables);

        listView.setAdapter(adp);

        final Intent buscaBloco = new Intent(this, BuscaBloco.class);
        final Intent buscaSala = new Intent(this, BuscaSala.class);
        final Intent buscaServico = new Intent(this, BuscaServico.class);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Tabela tbl = (Tabela) listView.getItemAtPosition(position);

                switch (tbl.getNomeNoBD()) {
                    case "blocos":
                        startActivity(buscaBloco);
                        break;
                    case "salas":
                        startActivity(buscaSala);
                        break;
                    case "servicos":
                        startActivity(buscaServico);
                        break;
                }
            }
        });

    }
}
