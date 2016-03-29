package br.unisul.unifind;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import java.util.ArrayList;

import br.unisul.unifind.adapters.AdapterListView;
import br.unisul.unifind.avaliacao.AppRater;
import br.unisul.unifind.json.AtualizarAsyncTask;
import br.unisul.unifind.json.DownloadJsonAsyncTaskCampus;
import br.unisul.unifind.objetos.ItemListView;
import br.unisul.unifind.viewsDB.DbHelper;

public class Main extends AppCompatActivity {

    private ListView listView;
    private AdapterListView adapterListView;
    private ArrayList<ItemListView> itens;
    private  DbHelper dbh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbh = new DbHelper(this);

        this.configurarListaDeOpcoes();

        AppRater.app_launched(this);

        this.verificaGPSativo();

        //atualizar DB
        new AtualizarAsyncTask(this).execute("http://ronanbr.ddns-intelbras.com.br:36666/unifind/versao");

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
        //itens.add(item3);
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


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.main_menu, menu);
//
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.menuAdmin:
//                Intent intent = new Intent(this, LoginAdmin.class);
//                startActivity(intent);
//                return true;
//            case R.id.atualizar:
//                new AtualizarAsyncTask(this).execute("http://ronanbr.ddns-intelbras.com.br:36666/unifind/versao");
//                return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }


    private void verificaGPSativo(){
        LocationManager manager = (LocationManager) getSystemService( this.LOCATION_SERVICE );
        boolean isOn = manager.isProviderEnabled( LocationManager.GPS_PROVIDER);
        if(!isOn){

            new AlertDialog.Builder(this)
                    .setTitle("GPS inativo!")
                    .setMessage("Para melhor precisão, recomenda-se ativar o GPS")
                    .setPositiveButton("Ativar agora", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            String provider = Settings.Secure.getString(getContentResolver(),
                                    Settings.Secure.ALLOWED_GEOLOCATION_ORIGINS);
                            //Se vier null ou length == 0   é por que o GPS esta desabilitado.
                            //Para abrir a tela do menu pode fazer assim:
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, 1);
                            dialog.dismiss();

                        }
                    })
                    .setNeutralButton(R.string.nao, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

        }
    }

    @Override
    public void onBackPressed() { android.os.Process.killProcess(android.os.Process.myPid()); }
    //fecha a aplicação quando pressionar a tecla voltar;

}