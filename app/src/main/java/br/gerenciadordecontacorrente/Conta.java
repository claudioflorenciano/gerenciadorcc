package br.gerenciadordecontacorrente;

import java.io.Serializable;

public class Conta implements Serializable {

    private int idConta;
    private double saldo;
    private int statusVip;
    private Correntista correntista;

    public Conta(Correntista correntista){
        setCorrentista(correntista);
    }


    public int getIdConta() {
        return idConta;
    }

    public void setIdConta(int idConta) {
        this.idConta = idConta;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public int getStatusVip() {
        return statusVip;
    }

    public void setStatusVip(int statusVip) {
        this.statusVip = statusVip;
    }

    public Correntista getCorrentista() {
        return correntista;
    }

    public void setCorrentista(Correntista correntista) {
        this.correntista = correntista;
    }
}
