package br.gerenciadordecontacorrente;


import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class Controller {

    private Context context;

    public Controller(Context context){
        this.context = context;
    }

    //CONTROLE DE CORRENTISTA
    public boolean checkAuth(String conta, String senha){
        DAO dao = new DAO(context);
        return dao.checkAuth(conta, senha);
    }

    public boolean checkAuth(String conta){
        DAO dao = new DAO(context);
        return dao.checkAuth(conta);
    }

    public Correntista pickCorrentista(String conta, String senha){
        DAO dao = new DAO(context);
        return dao.pickId(conta, senha);
    }

    public int pickCorrentista(String conta){
        DAO dao = new DAO(context);
        return dao.pickId(conta);
    }

    public boolean signIn(String contaCorrente, String senha){
        DAO dao = new DAO(context);
        return dao.signInUser(contaCorrente,senha);
    }

    //CONTROLE DE CONTA
    public boolean createUserAccount(Correntista correntista, boolean status){
        DAO dao = new DAO(context);
        return dao.createAccount(correntista, status);
    }

    public Conta pickAccount(Correntista correntista){
        DAO dao = new DAO(context);
        return dao.pickAccount(correntista);
    }

    public int pickIdAccount(String idCorrentista){
        DAO dao = new DAO(context);
        return dao.pickIdAccount(idCorrentista);
    }

    public Double updateBalance(int idConta){
        DAO dao = new DAO(context);
        return dao.getBalance(idConta);
    }

    public boolean addBalance(int idConta, Double valor, Double saldo){
        DAO dao = new DAO(context);
        return dao.addBalance(idConta, valor, saldo);
    }

    public void penalty(int idConta, Double valor, Double saldo){
        DAO dao = new DAO(context);
        dao.penalty(idConta, valor, saldo);
    }

    public boolean withdrawBalance(int idConta, Double valor, Double saldo){
        DAO dao = new DAO(context);
        return dao.withdrawBalance(idConta, valor, saldo);
    }

    //CONTROLE DE OPERACAO
    public boolean updateOperation(Operacao operacao){
        DAO dao = new DAO(context);
        return dao.updateOperation(operacao);
    }

    public List<Operacao> getStatement(int idConta){
        DAO dao = new DAO(context);
        return dao.getOperations(idConta);
    }

    //CONTROLE DE VALIDACAO
    public String userValidation(String conta, String senha){
        String erros = "";
        if((conta.length() < 5) || (senha.length() < 4)){
            erros += "Campos inválidos!\n";
        }
        if((conta.equals("")) || (senha.equals(""))){
            erros += "Campos em branco!\n";
        }

        return erros;
    }

    public String getExtratoString(Operacao operacao, int idConta){
        String extrato;

        String data = operacao.getDataOperacao();

        String valor = String.format("R$ %.2f", operacao.getQuantia());

        if((operacao.getTipoOperacao().equals("SAQUE")) || (operacao.getTipoOperacao().equals("TAXA TRANSFERENCIA")) || (operacao.getTipoOperacao().equals("TAXA VISITA"))){

            extrato = data + " \n" + operacao.getTipoOperacao() + " \n-("+ valor +")\n" ;
        }else if((operacao.getTipoOperacao().equals("TRANSFERENCIA")) && (operacao.getIdContaPartida() == idConta)){
            extrato = data + " \n" + operacao.getTipoOperacao() + " \n-("+ valor +")\n" ;
        }else{
            extrato = data + " \n" + operacao.getTipoOperacao() + " \n" + valor + "\n";
        }




        return extrato;
    }

}
