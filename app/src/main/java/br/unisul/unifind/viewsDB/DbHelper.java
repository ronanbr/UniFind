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
    private static final int VERSAO_BASE = 21;

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

        onCreateAdd("blocos", db, "Cettal", -28.475490, -49.026258);
        onCreateAdd("blocos", db, "Saúde", -28.480209, -49.021578);
        onCreateAdd("blocos", db, "Centro de Convivência", -28.480684, -49.021079);
        onCreateAdd("blocos", db, "Ginásio", -28.480798, -49.020101);
        onCreateAdd("blocos", db, "Bloco Sede", -28.482543, -49.019273);
        onCreateAdd("campi", db, "Tubarão", -28.479075, -49.022547);

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

    public void onCreateAdd(String table, SQLiteDatabase db, String descricao, Double lat, Double lon){
        ContentValues cv = new ContentValues();

        cv.put("descricao", descricao);
        cv.put("latitude", lat);
        cv.put("longitude", lon);

        db.insert(table, null, cv);
    }

    public void insertCampus(Campus campus){
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

    public List selectTodosCampi(){
        List<Campus> campi = new ArrayList<Campus>();

        SQLiteDatabase db = getReadableDatabase();

        String sqlSelectTodosBlocos =
                "SELECT * FROM campi";

        Cursor c = db.rawQuery(sqlSelectTodosBlocos, null);
        if (c.moveToFirst()){
            do{
                Campus campus = new Campus();
                campus.setId(c.getInt(0));
                campus.setDescricao(c.getString(1));

                campi.add(campus);
            }while(c.moveToNext());
        }

        return campi;
    }

    public List selectTodosBlocos(){
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

    public List selectTodasSalas(){
        List<Sala> salas = new ArrayList<Sala>();

        SQLiteDatabase db = getReadableDatabase();

        String sqlSelectTodosBlocos =
                "SELECT * FROM salas";

        Cursor c = db.rawQuery(sqlSelectTodosBlocos, null);
        if (c.moveToFirst()){
            do{
                Sala sala = new Sala();
                sala.setId(c.getInt(0));
                sala.setDescricao(c.getString(1));

                salas.add(sala);
            }while(c.moveToNext());
        }

        return salas;
    }

    public List selectTodosServicos(){
        //TODO: Depois de definir os atributos de serviços, elaborar o select
        return null;
    }




    public List selectBlocos(String filtro){
        List<Bloco> blocos = new ArrayList<Bloco>();

        SQLiteDatabase db = getReadableDatabase();

        String sqlSelect = "SELECT * FROM blocos " +
                "WHERE descricao LIKE '%"+filtro+"%'";

        Cursor c = db.rawQuery(sqlSelect, null);
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
}
