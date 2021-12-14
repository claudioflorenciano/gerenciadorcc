package br.gerenciadordecontacorrente;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Operacao {
    private int idOperacao;
    private int idContaPartida;
    private int idContaDestino;
    private Double saldoInicialPartida;
    private Double getSaldoInicialDestino;
    private Double quantia;
    private String dataOperacao;
    private String tipoOperacao;

    public int getIdOperacao() {
        return idOperacao;
    }

    public void setIdOperacao(int idOperacao) {
        this.idOperacao = idOperacao;
    }

    public int getIdContaPartida() {
        return idContaPartida;
    }

    public void setIdContaPartida(int idContaPartida) {
        this.idContaPartida = idContaPartida;
    }

    public int getIdContaDestino() {
        return idContaDestino;
    }

    public void setIdContaDestino(int idContaDestino) {
        this.idContaDestino = idContaDestino;
    }

    public Double getSaldoInicialPartida() {
        return saldoInicialPartida;
    }

    public void setSaldoInicialPartida(Double saldoInicialPartida) {
        this.saldoInicialPartida = saldoInicialPartida;
    }

    public Double getGetSaldoInicialDestino() {
        return getSaldoInicialDestino;
    }

    public void setGetSaldoInicialDestino(Double getSaldoInicialDestino) {
        this.getSaldoInicialDestino = getSaldoInicialDestino;
    }

    public Double getQuantia() {
        return quantia;
    }

    public void setQuantia(Double quantia) {
        this.quantia = quantia;
    }

    public String getDataOperacao() {
        return dataOperacao;
    }

    public void setDataOperacao(String dataOperacao) {
        this.dataOperacao = dataOperacao;
    }

    public String getTipoOperacao() {
        return tipoOperacao;
    }

    public void setTipoOperacao(String tipoOperacao) {
        this.tipoOperacao = tipoOperacao;
    }


}


