package com.example.organizador.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.organizador.R;
import com.example.organizador.activity.CadastroActivity;
import com.example.organizador.activity.LoginActivity;
import com.example.organizador.config.ConfiguracaoFireBase;
import com.google.firebase.auth.FirebaseAuth;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;

public class MainActivity extends IntroActivity {

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        setButtonBackVisible(false);
        setButtonNextVisible(false);

        addSlide( new FragmentSlide.Builder()
                   .background(android.R.color.white)
                   .fragment(R.layout.intro_1)
                   .build()
        );

        addSlide( new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_2)
                .build()
        );

        addSlide( new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_3)
                .build()
        );


        addSlide( new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_4)
                .build()
        );

        addSlide( new FragmentSlide.Builder()
                .background(android.R.color.white)
                .fragment(R.layout.intro_cadastro)
                //Ultimo Slide
                .canGoForward(false)
                .build()
        );
    }

    protected void onStart(){
        super.onStart();
        usuarioLogado();
    }

    public void usuarioLogado(){

        autenticacao = ConfiguracaoFireBase.getFirebaseAutenticacao();

         if(autenticacao.getCurrentUser() != null){
             startActivity(new Intent(this, PrincipalActivity.class));
         }
    }

    public void btnEntrar(View view){
       startActivity(new Intent(this, LoginActivity.class));
    }

    public void btnCadastrar(View view){
      startActivity(new Intent(this, CadastroActivity.class));
    }




}
