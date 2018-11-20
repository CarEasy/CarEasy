package com.carona.careasy.careasy.activity.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.carona.careasy.careasy.R;
import com.carona.careasy.careasy.activity.config.ConfiguracaoFirebase;
import com.carona.careasy.careasy.activity.helper.UsuarioFirebase;
import com.carona.careasy.careasy.activity.model.Usuario;
import com.carona.careasy.careasy.activity.util.ValidaCPF;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class RequisicoesActivity extends AppCompatActivity {

    private TextInputEditText campoPlaca, campoCor, campoModelo, campoAno;


    private FirebaseAuth autenticacao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requisicoes);

        //INICIALIZAR COMPONENTES
        campoPlaca = findViewById(R.id.editCadastroPlaca);
        campoCor = findViewById(R.id.editCadastroCor);
        campoModelo = findViewById(R.id.editCadastroModelo);
        campoAno = findViewById(R.id.editCadastroAno);

    }

    public void validarCadastroCarro(View view) {
        //Recuperar textos dos campos
        String textoPlaca = campoPlaca.getText().toString();
        String textoCor = campoCor.getText().toString();
        String textoModelo = campoModelo.getText().toString();
        String textoAno = campoModelo.getText().toString();

        /*
        //Valores a serem enviados ao Banco de Dados...
        Usuario usuario = new Usuario();
        usuario.setNome(textoNome);
        usuario.setCpf(textoCpf);
        usuario.setEmail(textoEmail);
        usuario.setSenha(textoSenha);
        usuario.setTipo(verificaTipoUsuario());
        cadastrarUsuario(usuario);

        if (!textoNome.isEmpty()) {//verifica nome
            if ( aux == true) {//verifica cpf
                if (!textoEmail.isEmpty()) {//verifica e-mail
                    if (!textoSenha.isEmpty()) {//verifica senha


                    } else {
                        toast(R.string.toast_senha_vazio);
                    }
                } else {
                    toast(R.string.toast_email_vazio);
                }
            } else {
                toast(R.string.toast_cpf_invalido);
            }
        } else {
            toast(R.string.toast_nome_vazio);
        }

    }
     public void toast(int string) {
        Toast.makeText(RequisicoesActivity.this, string, Toast.LENGTH_SHORT).show();
    }

    public void toast(String string) {
        Toast.makeText(RequisicoesActivity.this, string, Toast.LENGTH_SHORT).show();
    }
         */







    }

}
