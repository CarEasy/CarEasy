package com.carona.careasy.careasy.activity.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

// importanto a classe "ValidaCPF" do pacote "meuPacote"
import com.carona.careasy.careasy.activity.util.ValidaCPF;


public class CadastroActivity extends AppCompatActivity {

    private TextInputEditText campoNome, campoCpf, campoEmail, campoSenha;
    private Switch switchTipoUsuario;

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        //INICIALIZAR COMPONENTES
        campoNome = findViewById(R.id.editCadastroNome);
        campoCpf = findViewById(R.id.editCadastroCPF);
        campoEmail = findViewById(R.id.editCadastroEmail);
        campoSenha = findViewById(R.id.editCadastroSenha);
        switchTipoUsuario = findViewById(R.id.switchTipoUsuario);
    }

    public void validarCadastroUsuario(View view) {
        //Recuperar textos dos campos
        String textoNome = campoNome.getText().toString();
        String textoCpf = campoCpf.getText().toString();
        String textoEmail = campoEmail.getText().toString();
        String textoSenha = campoSenha.getText().toString();

        //Varialvel logica para validação de CPF.
        boolean aux = ValidaCPF.isCPF(textoCpf);

        System.out.println("Variavel aux:" + aux);
        if (!textoNome.isEmpty()) {//verifica nome
            if (aux) {//verifica cpf
                if (!textoEmail.isEmpty()) {//verifica e-mail
                    if (!textoSenha.isEmpty()) {//verifica senha
                        //Valores a serem enviados ao Banco de Dados...
                        Usuario usuario = new Usuario();
                        usuario.setNome(textoNome);
                        usuario.setCpf(textoCpf);
                        usuario.setEmail(textoEmail);
                        usuario.setSenha(textoSenha);
                        usuario.setTipo(verificaTipoUsuario());
                        cadastrarUsuario(usuario);

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

    public void cadastrarUsuario(final Usuario usuario) {


        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    try {
                        String idUsuario = task.getResult().getUser().getUid();
                        usuario.setId(idUsuario);
                        usuario.salvar();

                        //Atualizar nome no UserProfile
                        UsuarioFirebase.atualizarNomeUsuario(usuario.getNome());
                        //Redireciona o usuário com base no seu tipo
                        //Se o usuário for passageiro chama a activity maps
                        //senão chama a activity requisições
                        if (verificaTipoUsuario() != "M") {
                            startActivity(new Intent(CadastroActivity.this, PassageiroActivity.class));
                            usuario.setVeiculo(null);
                            usuario.salvar();
                            finish();
                            toast(R.string.toast_passageiro_cadastrado);

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    String excecao = "";
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        excecao = getString(R.string.excecao_senha);
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        excecao = getString(R.string.excecao_email);
                    } catch (FirebaseAuthUserCollisionException e) {
                        excecao = getString(R.string.excecao_conta);
                    } catch (Exception e) {
                        excecao = getString(R.string.excecao_cadastro) + e.getMessage();
                        e.printStackTrace();
                    }
                    toast(excecao);
                }

            }
        });


    }

    public String verificaTipoUsuario() {
        return switchTipoUsuario.isChecked() ? "M" : "P";
    }

    public void toast(int string) {
        Toast.makeText(CadastroActivity.this, string, Toast.LENGTH_SHORT).show();
    }

    public void toast(String string) {
        Toast.makeText(CadastroActivity.this, string, Toast.LENGTH_SHORT).show();
    }
}
