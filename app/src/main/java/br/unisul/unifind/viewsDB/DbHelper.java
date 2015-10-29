package br.unisul.unifind.viewsDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import br.unisul.unifind.objetos.Bloco;
import br.unisul.unifind.objetos.Campus;
import br.unisul.unifind.objetos.Sala;

/**
 * Created by Ronan Cardoso on 28/10/2015.
 */
public class DbHelper extends SQLiteOpenHelper {
    private static final String NOME_BASE = "UniFindData";
    private static final int VERSAO_BASE = 19;

    public DbHelper(Context context) {

        super(context, NOME_BASE, null, VERSAO_BASE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sqlCreateTabelaCampi = "CREATE TABLE campi("
                +"id INTEGER PRIMARY KEY AUTOINCREMENT, "
                +"descricao TEXT"
                +")";

        String sqlCreateTabelaBlocos = "CREATE TABLE blocos("
                +"id INTEGER PRIMARY KEY AUTOINCREMENT, "
                +"descricao TEXT,"
                +"latitude DOUBLE,"
                +"longitude DOUBLE"
                +")";

        String sqlCreateTabelaSalas = "CREATE TABLE salas("
                +"id INTEGER PRIMARY KEY AUTOINCREMENT, "
                +"descricao TEXT,"
                +"latitude TEXT,"
                +"longitude TEXT"
                +")";

        String sqlCreateTabelaServicos = "CREATE TABLE servicos("
                +"id INTEGER PRIMARY KEY AUTOINCREMENT, "
                +"descricao TEXT,"
                +"latitude TEXT,"
                +"longitude TEXT"
                +")";

        db.execSQL(sqlCreateTabelaCampi);
        db.execSQL(sqlCreateTabelaBlocos);
        db.execSQL(sqlCreateTabelaSalas);
        db.execSQL(sqlCreateTabelaServicos);

//        onCreateAdd(db, new Bloco(0, "Labs de Informática", -28.475490, -49.026258));
//        onCreateAdd(db, new Bloco(0, "Saúde", -28.480209, -49.021578));
//        onCreateAdd(db, new Bloco(0, "Shopping Unisul", -28.480684, -49.021079));
//        onCreateAdd(db, new Bloco(0, "Ginásio", -28.480798, -49.020101));
//        onCreateAdd(db, new Bloco(0, "Bloco Sede", -28.482543, -49.019273));
    }


   @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //drop tables
        db.execSQL("DROP TABLE campi");
        db.execSQL("DROP TABLE blocos");
        db.execSQL("DROP TABLE salas");
        db.execSQL("DROP TABLE servicos");
        //cria novamente
        onCreate(db);

    }

    public void insertCampi(Campus campus){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put("descricao", campus.getDescricao());

        db.insert("campi", null, cv);

        db.close();
    }

    public void insertBloco(Bloco bloco){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put("descricao", bloco.getDescricao());
        cv.put("latitude", bloco.getLatitude());
        cv.put("longitude", bloco.getLongitude());

        db.insert("blocos", null, cv);

        db.close();
    }

    public void insertSala(Sala sala){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put("descricao", sala.getDescricao());
        cv.put("latitude", sala.getLatitude());
        cv.put("longitude", sala.getLongitude());

        db.insert("salas", null, cv);

        db.close();
    }

    public List selectBlocos(){
        List<Bloco> blocos = new ArrayList<Bloco>();

        SQLiteDatabase db = getReadableDatabase();

        String sqlSelectTodosBlocos =
                "SELECT * FROM blocos";

        Cursor c = db.rawQuery(sqlSelectTodosBlocos, null);
        if (c.moveToFirst()){
            do{
                Bloco bloco = new Bloco();
                bloco.setId(c.getInt(0));
                bloco.setDescricao(c.getString(1));
                bloco.setLatitude(c.getDouble(2));
                bloco.setLongitude(c.getDouble(3));

                blocos.add(bloco);
            }while(c.moveToNext());
        }

        return blocos;
    }


    public void onCreateAdd(SQLiteDatabase db, Bloco bloco){
        ContentValues cv = new ContentValues();

        cv.put("descricao", bloco.getDescricao());
        cv.put("latitude", bloco.getLatitude());
        cv.put("longitude", bloco.getLongitude());

        db.insert("blocos", null, cv);
    }

}
