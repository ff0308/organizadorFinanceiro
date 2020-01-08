package com.example.organizador.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.organizador.Adapter.AdapterMovimentacao;
import com.example.organizador.Model.Movimentacao;
import com.example.organizador.Model.Usuario;
import com.example.organizador.config.ConfiguracaoFireBase;
import com.example.organizador.helper.Base64Custom;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.organizador.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PrincipalActivity extends AppCompatActivity {

    private MaterialCalendarView calendarView;
    private TextView textoSauda, textoSaldo;
    DatabaseReference firebase = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth autenticacao = ConfiguracaoFireBase.getFirebaseAutenticacao();
    private DatabaseReference firebaseRef = ConfiguracaoFireBase.getFirebaseDataBase();
    private DatabaseReference usuarioRef;
    private ValueEventListener valueEventListenerUsuario;
    private ValueEventListener valueEventListenerMovimentacoes;

    private RecyclerView recyclerView;
    private AdapterMovimentacao adapterMovimentacao;
    private List<Movimentacao> movimentacoes = new ArrayList<>();
    private Movimentacao movimentacao;
    private DatabaseReference movimentacaoRef;
    private String mesAnoSelecionado;

    double receita;
    double despesa;
    double saldo;
    String emailUsuario = autenticacao.getCurrentUser().getEmail();
    String idUsuario = Base64Custom.codificarBase64( emailUsuario );


    DatabaseReference tb_usuario = firebase.child("usuarios");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Organizador");

        textoSauda = findViewById(R.id.txtSauda);
        textoSaldo = findViewById(R.id.txtSaldo);
        calendarView = findViewById(R.id.calendarView);
        recyclerView = findViewById(R.id.recyclerMovimentos);

        configuraCalendarView();
        swipe();

        //Configurar adapter

        adapterMovimentacao = new AdapterMovimentacao(movimentacoes,this);

        //Configurar RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager( layoutManager );
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter( adapterMovimentacao );


        /*
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

    }


    public void recuperarMovimentacoes(){

        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.codificarBase64( emailUsuario );
        movimentacaoRef = firebaseRef.child("movimentacao")
                .child( idUsuario )
                .child( mesAnoSelecionado );

        valueEventListenerMovimentacoes = movimentacaoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                movimentacoes.clear();
                for (DataSnapshot dados: dataSnapshot.getChildren() ){

                    Movimentacao movimentacao = dados.getValue( Movimentacao.class );
                    //pegar a chave da movimentacao para excluir depois
                    movimentacao.setKey(dados.getKey());
                    movimentacoes.add( movimentacao );


                }

                adapterMovimentacao.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void recuperarResumo(){

        usuarioRef = firebaseRef.child("usuarios").child(idUsuario);

        valueEventListenerUsuario = usuarioRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                assert usuario != null;

                receita = usuario.getReceitaTotal();
                despesa = usuario.getDespesaTotal();

                saldo = receita - despesa;

                textoSauda.setText("Olá, "+usuario.getNome());
                DecimalFormat decimalFormat = new DecimalFormat("0.##");
                String saldoFormatado = decimalFormat.format(saldo);
                textoSaldo.setText("R$ "+saldoFormatado);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void atualizarSaldo(){

        //update no banco para alterar valor de receita e despesa e o recuperarResumo soma o saldo novamente()

        usuarioRef = firebaseRef.child("usuarios").child(idUsuario);

        if(movimentacao.getTipo().equals("r")){
            receita = receita - movimentacao.getValor();
            usuarioRef.child("receitaTotal").setValue(receita);
        }else{
            despesa = despesa - movimentacao.getValor();
            usuarioRef.child("despesaTotal").setValue(despesa);
        }

        Toast.makeText(PrincipalActivity.this, "Excluido com Sucesso", Toast.LENGTH_SHORT).show();


    }

    //adiciona Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void excluirMovimentacao(final RecyclerView.ViewHolder viewHolder){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Excluir Movimentação da Conta");
        alertDialog.setMessage("Você tem certeza que deseja excluir a movimentação ?");
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               int position = viewHolder.getAdapterPosition();
               //Pega a movimentação certa do array list
               movimentacao = movimentacoes.get(position);
               movimentacao.getKey();
                String emailUsuario = autenticacao.getCurrentUser().getEmail();
                String idUsuario = Base64Custom.codificarBase64( emailUsuario );
                movimentacaoRef = firebaseRef.child("movimentacao")
                        .child( idUsuario )
                        .child( mesAnoSelecionado );
                //Delete
                movimentacaoRef.child(movimentacao.getKey()).removeValue();
                adapterMovimentacao.notifyItemRemoved(position);

                atualizarSaldo();
            }
        });

        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(PrincipalActivity.this, "Cancelado", Toast.LENGTH_SHORT).show();
                recuperarMovimentacoes();
            }
        });

        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    //swipe deslizar movimentações
    public void swipe(){
        final ItemTouchHelper.Callback itemTouch = new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView,  RecyclerView.ViewHolder viewHolder) {

                int dragFlags = ItemTouchHelper.ACTION_STATE_IDLE;
                //deslizar dando do comeco ou do fim
                int swiperFlags = ItemTouchHelper.START | ItemTouchHelper.END;

                return makeMovementFlags(dragFlags, swiperFlags);

            }

            @Override
            public boolean onMove(RecyclerView recyclerView,  RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
               excluirMovimentacao(viewHolder);
            }
        };

        //adicionando no Recycler View
        new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerView);
    }

    //ao clicar nos itens do menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_sair) {
            autenticacao = ConfiguracaoFireBase.getFirebaseAutenticacao();
            autenticacao.signOut();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void adicionarDespesa(View view){

      startActivity(new Intent(this, DespesasActivity.class));
    }

    public void adicionarReceita(View view){

      startActivity(new Intent(this, ReceitasActivity.class));

    }

        public void configuraCalendarView(){

            CharSequence meses[] = {"Janeiro","Fevereiro", "Março","Abril","Maio","Junho","Julho","Agosto","Setembro","Outubro","Novembro","Dezembro"};
            calendarView.setTitleMonths( meses );

            CalendarDay dataAtual = calendarView.getCurrentDate();
            String mesSelecionado = String.format("%02d", (dataAtual.getMonth() + 1) );
            mesAnoSelecionado = String.valueOf( mesSelecionado + "" + dataAtual.getYear() );

            calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
                @Override
                public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                    String mesSelecionado = String.format("%02d", (date.getMonth() + 1) );
                    mesAnoSelecionado = String.valueOf( mesSelecionado + "" + date.getYear() );

                    movimentacaoRef.removeEventListener( valueEventListenerMovimentacoes );
                    recuperarMovimentacoes();
                }
            });

        }

        //Tratamento Correto de Eventos onStart

        @Override
        protected void onStart() {
            super.onStart();
            recuperarResumo();
            recuperarMovimentacoes();

        }


    //Tratamento Correto de Eventos onStop
    @Override
    protected void onStop() {
        super.onStop();
        usuarioRef.removeEventListener(valueEventListenerUsuario);
    }
}
