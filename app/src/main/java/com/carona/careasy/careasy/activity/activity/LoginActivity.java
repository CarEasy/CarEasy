package com.carona.careasy.careasy.activity.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.carona.careasy.careasy.R;
import com.carona.careasy.careasy.activity.config.ConfiguracaoFirebase;
import com.carona.careasy.careasy.activity.helper.UsuarioFirebase;
import com.carona.careasy.careasy.activity.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText campoEmail, campoSenha;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Inicializar componentes
        campoEmail = findViewById(R.id.editLoginEmail);
        campoSenha = findViewById(R.id.editLoginSenha);
    }

    public void validarLoginUsuario( View view){

        //Recuperar textos dos campos
        String textoEmail = campoEmail.getText().toString();
        String textoSenha = campoSenha.getText().toString();

        if( !textoEmail.isEmpty()){//Verifica o email
            if( !textoSenha.isEmpty()){//Verifica a senha
                Usuario usuario = new Usuario();
                usuario.setEmail(textoEmail);
                usuario.setSenha(textoSenha);

                logarUsuario(usuario);

            }else{
                toast(R.string.toast_senha_vazio);
            }
        }else{
            toast(R.string.toast_email_vazio);
        }
    }

    public void logarUsuario( Usuario usuario ){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.signInWithEmailAndPassword(
                usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if( task.isSuccessful() ){
                    Intent i = new Intent(LoginActivity.this, PassageiroActivity.class);
                    startActivity(i);
                    UsuarioFirebase.redirecionaUsuarioLogado(LoginActivity.this);

                }else{
                    String excecao="";
                    try {
                        throw task.getException();
                    }catch ( FirebaseAuthInvalidUserException e ) {
                        excecao = getString(R.string.excecao_usuario);
                    }catch ( FirebaseAuthInvalidCredentialsException e ){
                        excecao = getString(R.string.excecao_login_invalido);
                    }catch (Exception e){
                        excecao = getString(R.string.excecao_cadastro) + e.getMessage();
                        e.printStackTrace();
                    }
                    toast(excecao);

                }
            }
        });
    }

    public  void toast(int string){
        Toast.makeText(LoginActivity.this, string, Toast.LENGTH_SHORT).show();
    }

    public  void toast(String string){
        Toast.makeText(LoginActivity.this, string, Toast.LENGTH_SHORT).show();
    }

}
