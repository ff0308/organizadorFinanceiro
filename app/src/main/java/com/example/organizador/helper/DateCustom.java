package com.example.organizador.helper;

import java.text.SimpleDateFormat;

public class DateCustom {

    //data atual

    public static String dataAtual(){

        long data = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dataString = simpleDateFormat.format(data);

        return dataString;

    }

    public static String dataEscolhida(String data){
        String retornoData[] = data.split("/");
        String dia = retornoData[0];
        String mes = retornoData[1];
        String ano = retornoData[2];

        String mesAno = mes+ano;
        return mesAno;
    }
}
