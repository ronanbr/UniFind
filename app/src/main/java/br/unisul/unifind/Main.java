package br.unisul.unifind;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import br.unisul.unifind.objetos.ItemMenu;

public class Main extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.configurarListaDeOpcoes();

    }

    private void configurarListaDeOpcoes(){
        listView = (ListView) findViewById(R.id.mainListView);

        ArrayList<ItemMenu> opcoes = new ArrayList<ItemMenu>();

        opcoes.add(new ItemMenu(1, "Buscar Local"));
        opcoes.add(new ItemMenu(2, "Navegar no Mapa"));
        opcoes.add(new ItemMenu(3, "Menu Admin"));
        opcoes.add(new ItemMenu(4, "Sair"));

        ArrayAdapter<ItemMenu> adp = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, opcoes);

        listView.setAdapter(adp);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                ItemMenu item = (ItemMenu) listView.getItemAtPosition(position);
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