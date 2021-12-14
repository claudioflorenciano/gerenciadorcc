package br.gerenciadordecontacorrente;

import java.io.Serializable;

public class Correntista implements Serializable {

    private int idCorrentista;
    private int contaCorrente;
    private int senha;

    public Correntista(){

    }

    public Correntista(int conta, int senha){
        setContaCorrente(conta);
        setSenha(senha);
    }

    public int getIdCorrentista() {
        return idCorrentista;
    }

    public void setIdCorrentista(int idCorrentista) {
        this.idCorrentista = idCorrentista;
    }

    public int getContaCorrente() {
        return contaCorrente;
    }

    public void setContaCorrente(int contaCorrente) {
        this.contaCorrente = contaCorrente;
    }

    public int getSenha() {
        return senha;
    }

    public void setSenha(int senha) {
        this.senha = senha;
    }
}
