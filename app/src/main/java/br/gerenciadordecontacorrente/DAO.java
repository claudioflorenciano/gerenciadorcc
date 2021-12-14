package br.gerenciadordecontacorrente;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class DAO extends SQLiteOpenHelper {

    private static final String BANCO = "GerenciadorCC";
    private static final int VERSAO = 1;

    public DAO(Context context){
        super(context, BANCO, null, VERSAO);

    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String sql_corrente = "CREATE TABLE correntista (id_correntista INTEGER PRIMARY KEY, conta_corrente INTEGER NOT NULL, conta_senha INTEGER NOT NULL)";
        String sql_conta = "CREATE TABLE conta (id_conta INTEGER PRIMARY KEY, saldo REAL NOT NULL, status_vip INTEGER NOT NULL, id_correntista INTEGER NOT NULL)";
        String sql_operacao = "CREATE TABLE operacao (id_operacao INTEGER PRIMARY KEY, id_conta_partida INTEGER NOT NULL, id_conta_destino INTEGER NOT NULL, saldo_inicial REAL NOT NULL, saldo_inicial_destino REAL NOT NULL, valor REAL NOT NULL, data_operacao DATETIME DEFAULT (datetime('now','localtime')), tipo_operacao TEXT NOT NULL)";

        String sql_insert_correntista_um = "INSERT INTO correntista VALUES (1 ,12345,1234)";
        String sql_insert_correntista_dois = "INSERT INTO correntista VALUES (2 ,54321,4321)";
        String sql_insert_conta_um = "INSERT INTO conta (saldo, status_vip, id_correntista) VALUES (0.00,1,1)";
        String sql_insert_conta_dois = "INSERT INTO conta (saldo, status_vip, id_correntista) VALUES (0.00 ,0,2)";

        db.execSQL(sql_corrente);
        db.execSQL(sql_conta);
        db.execSQL(sql_operacao);
        db.execSQL(sql_insert_correntista_um);
        db.execSQL(sql_insert_correntista_dois);
        db.execSQL(sql_insert_conta_um);
        db.execSQL(sql_insert_conta_dois);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        String sql_corrente = "DROP TABLE IF EXISTS correntista";
        String sql_conta = "DROP TABLE IF EXISTS conta";
        String sql_operacao = "DROP TABLE IF EXISTS  operacao";

        db.execSQL(sql_corrente);
        db.execSQL(sql_conta);
        db.execSQL(sql_operacao);

        onCreate(db);
    }


    //DAO CORRENTISTA
    public boolean checkAuth(String conta, String senha){
        SQLiteDatabase db = getReadableDatabase();

        String contaCorrente = String.valueOf(conta);
        String senhaConta= String.valueOf(senha);;

        Cursor cursor = db.rawQuery("select * from correntista where conta_corrente = ? and conta_senha = ?", new String[] {contaCorrente, senhaConta});

        if(cursor.getCount() > 0){
            cursor.close();
            db.close();
            return true;
        }
        cursor.close();
        db.close();

        return false;
    }

    public boolean checkAuth(String conta){
        SQLiteDatabase db = getReadableDatabase();

        String contaCorrente = String.valueOf(conta);

        Cursor cursor = db.rawQuery("select * from correntista where conta_corrente = ?", new String[] {contaCorrente});

        if(cursor.getCount() > 0){
            cursor.close();
            db.close();
            return true;
        }
        cursor.close();
        db.close();
        return false;
    }

    @SuppressLint("Range")
    public Correntista pickId(String conta, String senha){
        SQLiteDatabase db = getReadableDatabase();
        Correntista correntista = new Correntista(Integer.parseInt(conta), Integer.parseInt(senha));

        Cursor cursor = db.rawQuery("select * from correntista where conta_corrente = ? and conta_senha = ?", new String[] {conta, senha});


        if(cursor.getCount() >= 1) {
            while (cursor.moveToNext()) {
                correntista.setIdCorrentista(cursor.getInt(cursor.getColumnIndex("id_correntista")));
            }
        }



        cursor.close();
        db.close();

        return correntista;
    }

    @SuppressLint("Range")
    public int pickId(String conta){
        SQLiteDatabase db = getReadableDatabase();

        int idConta = 0;

        Cursor cursor = db.rawQuery("select id_correntista from correntista where conta_corrente = ?", new String[] {conta});


        if(cursor.getCount() >= 1) {
            while (cursor.moveToNext()) {
                idConta = cursor.getInt(cursor.getColumnIndex("id_correntista"));
            }
        }



        cursor.close();
        db.close();

        return idConta;
    }

    public boolean signInUser(String contaCorrente, String senha){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues dados = new ContentValues();

        try {
            dados.put("conta_corrente", Integer.parseInt(contaCorrente));
            dados.put("conta_senha", Integer.parseInt(senha));

            db.insert("correntista", null, dados);
            db.close();

            return true;
        }catch(Exception e){
            System.err.println(e);
        }

        return false;
    }

    //DAO CONTA
    @SuppressLint("Range")
    public int pickIdAccount(String idCorrentista){
        SQLiteDatabase db = getReadableDatabase();

        int idConta = 0;

        Cursor cursor = db.rawQuery("select id_conta from conta where id_correntista = ?", new String[] {idCorrentista});


        if(cursor.getCount() >= 1) {
            while (cursor.moveToNext()) {
                idConta = cursor.getInt(cursor.getColumnIndex("id_conta"));
            }
        }



        cursor.close();
        db.close();

        return idConta;
    }

    public boolean createAccount(Correntista correntista, boolean status){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues dados = new ContentValues();

        try {
            dados.put("saldo", 0.00);
            dados.put("status_vip", status);
            dados.put("id_correntista", correntista.getIdCorrentista());

            db.insert("conta", null, dados);
            db.close();

            return true;
        }catch(Exception e){
            System.err.println(e);
        }

        return false;
    }

    @SuppressLint("Range")
    public Conta pickAccount(Correntista correntista){
        Conta conta = new Conta(correntista);

        SQLiteDatabase db = getReadableDatabase();



        Cursor cursor = db.rawQuery("select * from conta where id_correntista = ?", new String[] {Integer.toString(correntista.getIdCorrentista())});


        if(cursor.getCount() >= 1) {
            while (cursor.moveToNext()) {

                conta.setIdConta(cursor.getInt(cursor.getColumnIndex("id_conta")));
                conta.setSaldo(cursor.getDouble(cursor.getColumnIndex("saldo")));
                conta.setStatusVip(cursor.getInt(cursor.getColumnIndex("status_vip")));
            }
        }


        cursor.close();
        db.close();


        return conta;
    }


    @SuppressLint("Range")
    public Double getBalance(int idConta){
        Double valor = 0.00;

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT saldo FROM conta WHERE id_conta = ?", new String[] {Integer.toString(idConta)});

        if(cursor.getCount() >= 1) {
            while (cursor.moveToNext()) {

                valor = cursor.getDouble(cursor.getColumnIndex("saldo"));
            }
        }

        cursor.close();
        db.close();

        return valor;
    }

    public boolean addBalance(int idConta, Double valor, Double saldo){
        SQLiteDatabase db = getWritableDatabase();

        saldo += valor;


        ContentValues dados = new ContentValues();

        try {
            dados.put("saldo", saldo);

            String filtro = "id_conta = ?";
            String[] parametros = {String.valueOf(idConta)};

            db.update("conta", dados, filtro, parametros);

            db.close();

            return true;
        }catch(Exception e){
            System.err.println(e);
        }

        return false;
    }

    public boolean withdrawBalance(int idConta, Double valor, Double saldo){
        SQLiteDatabase db = getWritableDatabase();


        Double saldoFinal = saldo - valor;


        ContentValues dados = new ContentValues();

        try {
            dados.put("saldo", saldoFinal);

            String filtro = "id_conta = ?";
            String[] parametros = {String.valueOf(idConta)};

            db.update("conta", dados, filtro, parametros);

            db.close();

            return true;
        }catch(Exception e){
            System.err.println(e);
        }

        return false;
    }

    public void penalty(int idConta, Double valor, Double saldo){
        SQLiteDatabase db = getWritableDatabase();

        saldo -= valor;

        ContentValues dados = new ContentValues();

        try {
            dados.put("saldo", saldo);

            String filtro = "id_conta = ?";
            String[] parametros = {String.valueOf(idConta)};

            db.update("conta", dados, filtro, parametros);

            db.close();

        }catch(Exception e){
            System.err.println(e);
        }
    }

    //DAO OPERACAO
    public boolean updateOperation(Operacao operacao){
        SQLiteDatabase db = getWritableDatabase();



        ContentValues dados = new ContentValues();

        try {
            dados.put("id_conta_partida", operacao.getIdContaPartida());
            dados.put("id_conta_destino", operacao.getIdContaDestino());
            dados.put("saldo_inicial", operacao.getSaldoInicialPartida());
            dados.put("saldo_inicial_destino", operacao.getGetSaldoInicialDestino());
            dados.put("valor", operacao.getQuantia());
            //dados.put("data_operacao", );
            dados.put("tipo_operacao", operacao.getTipoOperacao());




            db.insert("operacao", null, dados);

            db.close();

            return true;
        }catch(Exception e){
            System.err.println(e.toString());
        }
        return false;
    }

    @SuppressLint("Range")
    public List<Operacao> getOperations(int idConta){
        List<Operacao> operacoes = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT STRFTIME('%d/%m/%Y - %H:%M', data_operacao) AS formatData, * FROM operacao WHERE id_conta_partida = ? OR id_conta_destino = ? order by data_operacao desc", new String[] {Integer.toString(idConta), Integer.toString(idConta)});

        while(cursor.moveToNext()){
            try {
                Operacao operacao = new Operacao();

                operacao.setIdOperacao(cursor.getInt(cursor.getColumnIndex("id_operacao")));
                operacao.setIdContaPartida(cursor.getInt(cursor.getColumnIndex("id_conta_partida")));
                operacao.setIdContaDestino(cursor.getInt(cursor.getColumnIndex("id_conta_destino")));
                operacao.setDataOperacao(cursor.getString(cursor.getColumnIndex("formatData")));
                operacao.setTipoOperacao(cursor.getString(cursor.getColumnIndex("tipo_operacao")));
                operacao.setQuantia(cursor.getDouble(cursor.getColumnIndex("valor")));

                operacoes.add(operacao);
            }catch(Exception e){
                System.err.println(e.toString());
            }
        }

        cursor.close();
        db.close();

        return operacoes;
    }


}
