package br.gerenciadordecontacorrente;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ActivityCorrentista extends AppCompatActivity {

    private TextView txtInfoConta;
    private Button btnVisita;
    private Conta conta;

    private Controller ctrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_correntista);

        txtInfoConta = (TextView) findViewById(R.id.txtInfoConta);
        btnVisita = (Button) findViewById(R.id.btnVisita);


        recebeConta();
        multaContaNegativa();
    }


    public void multaContaNegativa(){
        long time = 60000;
        Timer timer = new Timer();
        TimerTask multa = new TimerTask(){
            public void run() {
                try {
                    conta.setSaldo(ctrl.updateBalance(conta.getIdConta()));
                    if((conta.getStatusVip() == 1) && (conta.getSaldo() < 0)){
                        conta.setSaldo(ctrl.updateBalance(conta.getIdConta()));
                        ctrl.penalty(conta.getIdConta(), conta.getSaldo() * -(0.001), conta.getSaldo());
                    }
                } catch (Exception e) {
                    System.err.println(e.toString());
                }

            }
        };
        timer.scheduleAtFixedRate(multa, time, time);
    }

    public void recebeConta(){
        Intent intent = getIntent();

        conta = (Conta) intent.getSerializableExtra("conta");

        String stats;

        if(conta.getStatusVip() == 1){
            stats = "VIP";
            btnVisita.setVisibility(View.VISIBLE);
            btnVisita.setEnabled(true);
        }else{
            stats = "NORMAL";
            btnVisita.setVisibility(View.GONE);
        }

        txtInfoConta.setText("Conta: " + Integer.toString(conta.getCorrentista().getContaCorrente()) + " - Status: " + stats);


    }

    public void btnVerSaldoClick(View view){

        ctrl = new Controller(ActivityCorrentista.this);

        Double valor = ctrl.updateBalance(conta.getIdConta());

        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityCorrentista.this);
        builder.setTitle("Seu Saldo");
        builder.setMessage(String.format("Seu saldo é de R$ %.2f", valor));
        builder.setPositiveButton("Fechar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void btnExtratoClick(View view){
        ctrl = new Controller(ActivityCorrentista.this);

        
        List<Operacao> lista = ctrl.getStatement(conta.getIdConta());

        if(lista.isEmpty()){
            Toast.makeText(ActivityCorrentista.this, "Extrato vazio!", Toast.LENGTH_SHORT).show();
        }else {


            List<String> extrato = new ArrayList<>();
            for (Operacao o : lista) {
                extrato.add(ctrl.getExtratoString(o, conta.getIdConta()));

            }

            ListView listView = new ListView(ActivityCorrentista.this);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(ActivityCorrentista.this, android.R.layout.simple_list_item_1, extrato);

            listView.setAdapter(adapter);

            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityCorrentista.this);
            builder.setCancelable(true);

            builder.setTitle("Seu Extrato");

            builder.setNegativeButton("Fechar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

            builder.setView(listView);

            final AlertDialog dialog = builder.create();

            dialog.show();
        }
    }

    public void btnDepositoClick(View view){

        ctrl = new Controller(ActivityCorrentista.this);

        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityCorrentista.this);

        final View customLayout = getLayoutInflater().inflate(R.layout.custom_layout, null);

        builder.setView(customLayout);

        builder.setTitle("Depósito");

        builder.setMessage("Digíte o valor a ser depositado!");

        builder.setPositiveButton("Depositar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EditText inputValor = customLayout.findViewById(R.id.inputValor);

                if((inputValor.getText().toString().equals("")) || Double.parseDouble(inputValor.getText().toString()) < 1){
                    Toast.makeText(ActivityCorrentista.this, "Valor inválido!", Toast.LENGTH_SHORT).show();

                }else{

                    Double valor = Double.parseDouble(inputValor.getText().toString());

                    conta.setSaldo(ctrl.updateBalance(conta.getIdConta()));

                    if (ctrl.addBalance(conta.getIdConta(), valor, conta.getSaldo())) {

                        Operacao operacao = new Operacao();

                        operacao.setIdContaPartida(conta.getIdConta());
                        operacao.setIdContaDestino(conta.getIdConta());
                        operacao.setSaldoInicialPartida(conta.getSaldo());
                        operacao.setGetSaldoInicialDestino(conta.getSaldo());
                        operacao.setQuantia(valor);
                        operacao.setTipoOperacao("DEPOSITO");

                        if (ctrl.updateOperation(operacao)) {
                            conta.setSaldo(ctrl.updateBalance(conta.getIdConta()));
                            Toast.makeText(ActivityCorrentista.this, "Depósito efetuado!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ActivityCorrentista.this, "Erro ao registrar deposito!", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(ActivityCorrentista.this, "Ocorreu um erro!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void btnSaqueClick(View view){

        ctrl = new Controller(ActivityCorrentista.this);

        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityCorrentista.this);

        final View customLayout = getLayoutInflater().inflate(R.layout.custom_layout, null);

        builder.setView(customLayout);

        builder.setTitle("Saque");

        builder.setMessage("Digíte o valor a ser sacado!");

        builder.setPositiveButton("Sacar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EditText inputValor = customLayout.findViewById(R.id.inputValor);

                if((inputValor.getText().toString().equals("")) || Double.parseDouble(inputValor.getText().toString()) < 1){
                    Toast.makeText(ActivityCorrentista.this, "Valor inválido!", Toast.LENGTH_SHORT).show();
                }else{
                    Double valor = Double.parseDouble(inputValor.getText().toString());

                    conta.setSaldo(ctrl.updateBalance(conta.getIdConta()));

                    if((valor > conta.getSaldo()) && (conta.getStatusVip() != 1)){

                        Toast.makeText(ActivityCorrentista.this, "Valor maior que o seu saldo!", Toast.LENGTH_SHORT).show();


                    }else{
                        if ((valor > conta.getSaldo()) && (conta.getStatusVip() == 1)) {
                            Toast.makeText(ActivityCorrentista.this, "Saldo negativo. Faça um depósito para impedir multas!", Toast.LENGTH_SHORT).show();
                        }
                        if (ctrl.withdrawBalance(conta.getIdConta(), valor, conta.getSaldo())) {

                            Operacao operacao = new Operacao();

                            operacao.setIdContaPartida(conta.getIdConta());
                            operacao.setIdContaDestino(conta.getIdConta());
                            operacao.setSaldoInicialPartida(conta.getSaldo());
                            operacao.setGetSaldoInicialDestino(conta.getSaldo());
                            operacao.setQuantia(valor);
                            operacao.setTipoOperacao("SAQUE");

                            if (ctrl.updateOperation(operacao)) {
                                conta.setSaldo(ctrl.updateBalance(conta.getIdConta()));
                                Toast.makeText(ActivityCorrentista.this, "Saque efetuado!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ActivityCorrentista.this, "Erro ao registrar saque!", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(ActivityCorrentista.this, "Ocorreu um erro!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });


        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void btnTransferenciaClick(View view){
        ctrl = new Controller(ActivityCorrentista.this);


        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityCorrentista.this);

        final View customLayoutTransferir = getLayoutInflater().inflate(R.layout.custom_layout_transferencia, null);

        builder.setView(customLayoutTransferir);

        builder.setTitle("Transferência");

        builder.setMessage("Digíte o valor e a conta destino!");

        builder.setPositiveButton("Transferir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EditText inputValorTransferencia = customLayoutTransferir.findViewById(R.id.inputValorTransferencia);
                EditText inputContaDestino = customLayoutTransferir.findViewById(R.id.inputContaDestino);

                if((inputValorTransferencia.getText().toString().equals("")) || Double.parseDouble(inputValorTransferencia.getText().toString()) < 1){
                    Toast.makeText(ActivityCorrentista.this, "Valor inválido!", Toast.LENGTH_SHORT).show();
                }else if((inputContaDestino.getText().toString().equals("")) || (inputContaDestino.getText().toString().length() < 5)){
                    Toast.makeText(ActivityCorrentista.this, "Conta inválida!", Toast.LENGTH_SHORT).show();
                }else{
                    Double valor = Double.parseDouble(inputValorTransferencia.getText().toString());
                    int contaDestino = Integer.parseInt(inputContaDestino.getText().toString());

                    conta.setSaldo(ctrl.updateBalance(conta.getIdConta()));





                    int idCorrentistaDestino = ctrl.pickCorrentista(Integer.toString(contaDestino));

                    int idContaDestino = ctrl.pickIdAccount(Integer.toString(idCorrentistaDestino));

                    Double saldoDestino = ctrl.updateBalance(idContaDestino);

                    if(valor > conta.getSaldo()){

                        Toast.makeText(ActivityCorrentista.this, "Valor maior que o seu saldo!", Toast.LENGTH_SHORT).show();

                    }else{

                        if((ctrl.checkAuth(Integer.toString(contaDestino))) && (conta.getCorrentista().getContaCorrente() != contaDestino)){
                            if(conta.getStatusVip() == 1){
                                //transferencia

                                try{
                                    ctrl.addBalance(idContaDestino,valor,saldoDestino);
                                    ctrl.withdrawBalance(conta.getIdConta(), valor + (valor * 0.008), conta.getSaldo());





                                    Operacao operacao = new Operacao();

                                    operacao.setIdContaPartida(conta.getIdConta());
                                    operacao.setIdContaDestino(idContaDestino);
                                    operacao.setSaldoInicialPartida(conta.getSaldo());
                                    operacao.setGetSaldoInicialDestino(saldoDestino);
                                    operacao.setQuantia(valor);
                                    operacao.setTipoOperacao("TRANSFERENCIA");




                                    Operacao op = new Operacao();

                                    op.setIdContaPartida(conta.getIdConta());
                                    op.setIdContaDestino(conta.getIdConta());
                                    op.setSaldoInicialPartida(conta.getSaldo());
                                    op.setGetSaldoInicialDestino(conta.getSaldo());
                                    op.setQuantia(valor * 0.008);
                                    op.setTipoOperacao("TAXA TRANSFERENCIA");


                                    if (ctrl.updateOperation(operacao)) {
                                        ctrl.updateOperation(op);
                                        conta.setSaldo(ctrl.updateBalance(conta.getIdConta()));
                                        Toast.makeText(ActivityCorrentista.this, "Transferência efetuada!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(ActivityCorrentista.this, "Erro ao registrar Transferência!", Toast.LENGTH_SHORT).show();
                                    }
                                }catch(Exception e){
                                    System.err.println(e.toString());
                                    Toast.makeText(ActivityCorrentista.this, "Ocorreu um erro!", Toast.LENGTH_SHORT).show();
                                }


                            }else{
                                if(valor <= 1000){
                                    //transferencia

                                    try{
                                        ctrl.withdrawBalance(conta.getIdConta(), valor + 8.0, conta.getSaldo());
                                        ctrl.addBalance(idContaDestino,valor,saldoDestino);

                                        Operacao operacao = new Operacao();

                                        operacao.setIdContaPartida(conta.getIdConta());
                                        operacao.setIdContaDestino(idContaDestino);
                                        operacao.setSaldoInicialPartida(conta.getSaldo());
                                        operacao.setGetSaldoInicialDestino(saldoDestino);
                                        operacao.setQuantia(valor);
                                        operacao.setTipoOperacao("TRANSFERENCIA");



                                        Operacao op = new Operacao();

                                        op.setIdContaPartida(conta.getIdConta());
                                        op.setIdContaDestino(conta.getIdConta());
                                        op.setSaldoInicialPartida(conta.getSaldo());
                                        op.setGetSaldoInicialDestino(conta.getSaldo());
                                        op.setQuantia(8.0);
                                        op.setTipoOperacao("TAXA TRANSFERENCIA");


                                        if (ctrl.updateOperation(operacao)) {
                                            ctrl.updateOperation(op);
                                            conta.setSaldo(ctrl.updateBalance(conta.getIdConta()));
                                            Toast.makeText(ActivityCorrentista.this, "Transferência efetuada!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(ActivityCorrentista.this, "Erro ao registrar Transferência!", Toast.LENGTH_SHORT).show();
                                        }
                                    }catch(Exception e){
                                        System.err.println(e.toString());
                                        Toast.makeText(ActivityCorrentista.this, "Ocorreu um erro!", Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    Toast.makeText(ActivityCorrentista.this, "Valor maior que o permitido para sua conta!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }else{
                            if(conta.getCorrentista().getContaCorrente() == contaDestino){
                                Toast.makeText(ActivityCorrentista.this, "Operação inválida!", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(ActivityCorrentista.this, "Conta inexistente!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }
        });


        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void setBtnVisitaClick(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityCorrentista.this);
        builder.setTitle("Solicitar Visita do Gerente");
        builder.setMessage("Uma taxa de R$50,00 será cobrada! \nDeseja confirmar?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    ctrl = new Controller(ActivityCorrentista.this);

                    conta.setSaldo(ctrl.updateBalance(conta.getIdConta()));


                    ctrl.penalty(conta.getIdConta(), 50.00, conta.getSaldo());

                    Operacao operacao = new Operacao();

                    operacao.setIdContaPartida(conta.getIdConta());
                    operacao.setIdContaDestino(conta.getIdConta());
                    operacao.setSaldoInicialPartida(conta.getSaldo());
                    operacao.setGetSaldoInicialDestino(conta.getSaldo());
                    operacao.setQuantia(50.00);
                    operacao.setTipoOperacao("TAXA VISITA");

                    ctrl.updateOperation(operacao);

                    Toast.makeText(ActivityCorrentista.this, "Visita agendada com sucesso!", Toast.LENGTH_SHORT).show();
                }catch(Exception e){
                    System.err.println(e.toString());
                    Toast.makeText(ActivityCorrentista.this, "Ocorreu um erro!", Toast.LENGTH_SHORT).show();
                }
                dialogInterface.dismiss();
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void btnTrocarClick(View view){
        Toast.makeText(ActivityCorrentista.this, "Logoff efetuado!", Toast.LENGTH_SHORT).show();
        finish();
    }
}