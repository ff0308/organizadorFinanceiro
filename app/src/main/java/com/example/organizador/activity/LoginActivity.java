package com.example.organizador.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.organizador.Model.Usuario;
import com.example.organizador.R;
import com.example.organizador.config.ConfiguracaoFireBase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class LoginActivity extends AppCompatActivity {

    private EditText campoEmail, campoSenha;
    private Button botaoEntrar;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        campoEmail = findViewById(R.id.editEmail);
        campoSenha = findViewById(R.id.editSenha);
        botaoEntrar = findViewById(R.id.btnEntrar);

        botaoEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = campoEmail.getText().toString();
                String senha = campoSenha.getText().toString();

                if (email.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Preencha o email", Toast.LENGTH_SHORT).show();
                }
                else if(senha.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Digite a senha", Toast.LENGTH_SHORT).show();
                }
                else{

                    Usuario usuario = new Usuario();
                    usuario.setEmail(email);
                    usuario.setSenha(senha);
                    validarLogin(email,senha);
                }
            }
        });

    }

    public void validarLogin(String email, String senha){

      //Classe Configuração FireBase
      autenticacao = ConfiguracaoFireBase.getFirebaseAutenticacao();

      autenticacao.signInWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
          @Override
          public void onComplete(@NonNull Task<AuthResult> task) {

              if(task.isSuccessful()){

                 telaPrincipal();


              }else{


                  String excessao = "";

                  try
                  {
                      throw task.getException();

                  }
                  catch(FirebaseAuthInvalidCredentialsException e)
                  {
                      excessao = "Email e/ou Senha inválidos!";
                  }

                  catch(FirebaseAuthInvalidUserException e)
                  {
                      excessao = "Email inválido!";
                  }


                  catch(Exception e)
                  {
                      excessao = "Erro ao fazer login " +e.getMessage();
                      e.printStackTrace();
                  }
                  Toast.makeText(LoginActivity.this, excessao, Toast.LENGTH_SHORT).show();
              }
              }

      });
    }

    public void telaPrincipal(){
        startActivity(new Intent(this, PrincipalActivity.class));
        finish();
    }
}
