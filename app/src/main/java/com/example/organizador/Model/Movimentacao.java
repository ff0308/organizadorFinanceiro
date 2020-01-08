package com.example.organizador.Model;

import android.util.Base64;

import com.example.organizador.config.ConfiguracaoFireBase;
import com.example.organizador.helper.Base64Custom;
import com.example.organizador.helper.DateCustom;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

public class Movimentacao implements Serializable {

    private String data;
    private String categoria;
    private String descricao;
    private String tipo;
    private double valor;
    private String key;


    public Movimentacao() {

    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void salvar(String data){

            String mesAno = DateCustom.dataEscolhida(data);

            //Retorna Email do Usuário
            FirebaseAuth autenticacao = ConfiguracaoFireBase.getFirebaseAutenticacao();
            String idUsuario = Base64Custom.codificarBase64(autenticacao.getCurrentUser().getEmail());

            DatabaseReference firebase = ConfiguracaoFireBase.getFirebaseDataBase();
            firebase.child("movimentacao")
                    .child(idUsuario)
                    .child(mesAno)
                    .push()
                    .setValue(this);

    }
}
