package br.unisul.unifind;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;

import br.unisul.unifind.adapters.AdapterListView;
import br.unisul.unifind.objetos.ItemListView;
import br.unisul.unifind.objetos.ItemMenu;

public class Main extends AppCompatActivity {

    private ListView listView;
    private AdapterListView adapterListView;
    private ArrayList<ItemListView> itens;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.configurarListaDeOpcoes();

    }

    private void configurarListaDeOpcoes(){
        listView = (ListView) findViewById(R.id.mainListView);

        itens = new ArrayList<ItemListView>();
        ItemListView item1 = new ItemListView(1, "Buscar Local", R.drawable.search);
        ItemListView item2 = new ItemListView(2, "Navegar no Mapa", R.drawable.location);
        ItemListView item3 = new ItemListView(3, "Menu Admin", R.drawable.admin);
        ItemListView item4 = new ItemListView(4, "Sair", R.drawable.close2);

        itens.add(item1);
        itens.add(item2);
        itens.add(item3);
        itens.add(item4);

        //Cria o adapter
        adapterListView = new AdapterListView(this, itens);

        //Define o Adapter
        listView.setAdapter(adapterListView);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                ItemListView item = (ItemListView) listView.getItemAtPosition(position);
                switch (item.getId()) {
                    case 1:
                        Intent busca = new Intent(Main.this, BuscaLocal.class);
                        startActivity(busca);
                        break;
                    case 2:
                        Intent mapa = new Intent(Main.this, Mapa.class);
                        //Obrigatorio pois o mapa verifica dados na bundle
                        Bundle bundle = new Bundle();
                        mapa.putExtras(bundle);
                        startActivity(mapa);
                        break;
                    case 3:
                        Intent admin = new Intent(Main.this, LoginAdmin.class);
                        startActivity(admin);
                        break;
                    case 4:
                        onBackPressed();
                        break;
                }
            }
        });
    }

    @Override
    public void onBackPressed() { android.os.Process.killProcess(android.os.Process.myPid()); }
    //fecha a aplicação quando pressionar a tecla voltar;

}