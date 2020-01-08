package com.example.organizador.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.organizador.Model.Usuario;
import com.example.organizador.R;
import com.example.organizador.config.ConfiguracaoFireBase;
import com.example.organizador.helper.Base64Custom;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CadastroActivity extends AppCompatActivity {

    private EditText campoNome, campoEmail, campoSenha;
    private TextView teste;
    private Button botaoCadastrar;
    private FirebaseAuth autenticacao;
    private Usuario usuario;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        campoNome = findViewById(R.id.editNome);
        campoEmail = findViewById(R.id.editEmail);
        campoSenha = findViewById(R.id.editSenha);
        botaoCadastrar = findViewById(R.id.btnCadastrar);
        teste = findViewById(R.id.txtD);

        botaoCadastrar.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                String nome = campoNome.getText().toString();
                String email = campoEmail.getText().toString();
                String senha = campoSenha.getText().toString();

                if(nome.isEmpty()){
                    Toast.makeText(CadastroActivity.this, "Preencha o nome", Toast.LENGTH_SHORT).show();
                }
                else if (email.isEmpty()){
                    Toast.makeText(CadastroActivity.this, "Preencha o email", Toast.LENGTH_SHORT).show();
                }
                else if(senha.isEmpty()){
                    Toast.makeText(CadastroActivity.this, "Digite a senha", Toast.LENGTH_SHORT).show();
                }
                else{

                    Usuario usuario = new Usuario();
                    usuario.setEmail(email);
                    usuario.setSenha(senha);
                    cadastrarUsuario(usuario.getEmail(),usuario.getSenha());
                }
            }
        });


    }

    public void cadastrarUsuario(String email, String senha){

        autenticacao = ConfiguracaoFireBase.getFirebaseAutenticacao();

        //Insert de Usuario
        autenticacao.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    String nome = campoNome.getText().toString();
                    String email = campoEmail.getText().toString();
                    String senha = campoSenha.getText().toString();

                    Usuario usuario = new Usuario();
                    usuario.setNome(nome);
                    usuario.setEmail(email);
                    usuario.setSenha(senha);
                    String idUsuario = Base64Custom.codificarBase64(usuario.getEmail());
                    usuario.setIdUsuario(idUsuario);
                    usuario.salvar();
                    finish();

                }else{

                    String excessao = "";

                    try{
                        throw task.getException();

                    }catch(FirebaseAuthWeakPasswordException e){
                        excessao = "Digite uma senha mais forte";
                    }
                    catch(FirebaseAuthInvalidCredentialsException e)
                    {
                       excessao = "Por favor, digite um email válido";
                    }
                    catch(FirebaseAuthUserCollisionException e){
                        excessao = "Esta conta já foi cadastrada!";
                    }
                    catch(Exception e) {
                        excessao = "Erro ao cadastrar usuário " +e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(CadastroActivity.this, excessao, Toast.LENGTH_SHORT).show();
                }
            }
        });


    }




}
