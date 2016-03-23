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

import br.unisul.unifind.adapters.AdapterListView;
import br.unisul.unifind.objetos.ItemListView;
import br.unisul.unifind.objetos.ItemMenu;
import br.unisul.unifind.objetos.Tabela;

public class BuscaLocal extends AppCompatActivity {

    private ListView listView;
    private AdapterListView adapterListView;
    private ArrayList<ItemListView> itens;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busca_local);


        listView = (ListView) findViewById(R.id.listViewBusca);

        itens = new ArrayList<ItemListView>();
        ItemListView item1 = new ItemListView(1, "Blocos", R.drawable.bloco);
        ItemListView item2 = new ItemListView(2, "Salas", R.drawable.sala);
        ItemListView item3 = new ItemListView(3, "Serviços", R.drawable.servico);

        itens.add(item1);
        itens.add(item2);
        itens.add(item3);

        //Cria o adapter
        adapterListView = new AdapterListView(this, itens);

        //Define o Adapter
        listView.setAdapter(adapterListView);

        final Intent buscaBloco = new Intent(this, BuscaBloco.class);
        final Intent buscaSala = new Intent(this, BuscaSala.class);
        final Intent buscaServico = new Intent(this, BuscaServico.class);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                ItemListView item = (ItemListView) listView.getItemAtPosition(position);

                switch (item.getId()) {
                    case 1:
                        startActivity(buscaBloco);
                        break;
                    case 2:
                        startActivity(buscaSala);
                        break;
                    case 3:
                        startActivity(buscaServico);
                        break;
                }
            }
        });

    }
}
