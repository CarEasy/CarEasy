package com.carona.careasy.careasy.activity.activity;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.carona.careasy.careasy.R;
import com.carona.careasy.careasy.activity.config.ConfiguracaoFirebase;
import com.carona.careasy.careasy.activity.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class CadastroActivity extends AppCompatActivity {

    private TextInputEditText campoNome, campoCpf, campoEmail, campoSenha;
    private Switch switchTipoUsuario;

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        //INICIALIZAR COMPONENTES
        campoNome  = findViewById (R.id.editCadastroNome);
      //  campoCpf   = findViewById (R.id.editCadastroCPF);
        campoEmail = findViewById (R.id.editCadastroEmail);
        campoSenha = findViewById(R.id.editCadastroSenha);
        switchTipoUsuario = findViewById (R.id.switchTipoUsuario);
    }

    public void validarCadastroUsuaario(View view){
        //Recuperar textos dos campos
        String textoNome  = campoNome.getText().toString();
    //    String textoCpf   = campoCpf.getText().toString();
        String textoEmail = campoEmail.getText().toString();
        String textoSenha = campoSenha.getText().toString();

        if( !textoNome.isEmpty() ) {//verifica nome
            if( !textoEmail.isEmpty() ) {//verifica e-mail
                if( !textoSenha.isEmpty() ) {//verifica senha

                    Usuario usuario = new Usuario();
                    usuario.setNome( textoNome );
                    usuario.setEmail( textoEmail );
                    usuario.setSenha( textoSenha );
                    usuario.setTipo( verificaTipoUsuario() );

                    cadastrarUsuario( usuario );

                }else {
                    Toast.makeText(CadastroActivity.this,
                            "Preencha a senha!",
                            Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(CadastroActivity.this,
                        "Preencha o email!",
                        Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(CadastroActivity.this,
                    "Preencha o nome!",
                    Toast.LENGTH_SHORT).show();
        }

    }

    public void cadastrarUsuario( Usuario usuario ){

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if ( task.isSuccessful() ){
                    Toast.makeText(CadastroActivity.this,
                            "Sucesso ao cadastrar UsuÃ¡rio!",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


    public String verificaTipoUsuario(){
        return switchTipoUsuario.isChecked() ? "M" : "P" ;
    }


}
