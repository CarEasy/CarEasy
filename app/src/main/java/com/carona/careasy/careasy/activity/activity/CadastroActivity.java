package com.carona.careasy.careasy.activity.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.carona.careasy.careasy.R;
import com.carona.careasy.careasy.activity.config.ConfiguracaoFirebase;
import com.carona.careasy.careasy.activity.helper.UsuarioFirebase;
import com.carona.careasy.careasy.activity.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

// importanto a classe "ValidaCPF" do pacote "meuPacote"
import com.carona.careasy.careasy.activity.util.ValidaCPF;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;


public class CadastroActivity extends AppCompatActivity {

    private TextInputEditText campoNome,campoNascimento, campoCpf, campoEmail, campoSenha;
    private Spinner spinnerSexo;
    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    private ImageButton imageButtonCamera, imageButtonGaleria;
    private static final int SELECAO_CAMERA  = 100;
    private static final int SELECAO_GALERIA = 200;
    private boolean statusImagem = false;
    private DatabaseReference firebaseRef;
    private StorageReference storageReference;
    private ImageView imageViewUser;
    private Bitmap imagem;

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        //INICIALIZAR COMPONENTES
        campoNome = findViewById(R.id.editCadastroNome);
        campoNascimento = findViewById(R.id.editCadastroNascimento);
        campoCpf = findViewById(R.id.editCadastroCpf);
        campoEmail = findViewById(R.id.editCadastroEmail);
        campoSenha = findViewById(R.id.editCadastroSenha);
        spinnerSexo = findViewById(R.id.spinnerSexo);

        imageButtonCamera  = findViewById(R.id.imageButtonCamera);
        imageButtonGaleria = findViewById(R.id.imageButtonGaleria);
        imageViewUser = findViewById(R.id.imageViewUser);

        storageReference = ConfiguracaoFirebase.getFirebaseStorage();


        //Inicializando Array Para Spinner
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this,R.array.spinner_sexo, android.R.layout.simple_spinner_item);
        spinnerSexo.setAdapter(adapter);

        // Inicializa Botoes de Acesso a Galery e Camera
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageButtonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if ( i.resolveActivity(getPackageManager()) != null ){
                    startActivityForResult(i, SELECAO_CAMERA );
                }


            }
        });

        imageButtonGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI );
                if ( i.resolveActivity(getPackageManager()) != null ){
                    startActivityForResult(i, SELECAO_GALERIA );
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ( resultCode == RESULT_OK ){
            imagem = null;

            try {

                switch ( requestCode ){
                    case SELECAO_CAMERA:
                        imagem = (Bitmap) data.getExtras().get("data");
                        break;
                    case SELECAO_GALERIA:
                        Uri localImagemSelecionada = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada );
                        break;
                }
                if ( imagem != null ){


                    statusImagem = true;

                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }

    private void salvarFotoUsuario(Bitmap imagem, String cpf) {
        imageViewUser.setImageBitmap( imagem );
        //Recuperar dados da imagem para o firebase
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos );
        byte[] dadosImagem = baos.toByteArray();

        //Salvar imagem no firebase
        StorageReference imagemRef = storageReference
                .child("imagens")
                .child("perfil")
                .child( cpf+ ".jpeg");

        UploadTask uploadTask = imagemRef.putBytes( dadosImagem );
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CadastroActivity.this,
                        "Erro ao fazer upload da imagem",
                        Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(CadastroActivity.this,
                        "Sucesso ao fazer upload da imagem",
                        Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for ( int permissaoResultado : grantResults ){
            if ( permissaoResultado == PackageManager.PERMISSION_DENIED ){
                alertaValidacaoPermissao();
            }
        }

    }
    private void alertaValidacaoPermissao(){

        AlertDialog.Builder builder = new AlertDialog.Builder( this );
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    public void validarCadastroUsuario(View view) {
        //Recuperar textos dos campos
        String textoNome = campoNome.getText().toString();
        String textoNascimento = campoNascimento.getText().toString();
        String textoCpf = campoCpf.getText().toString();
        String textoEmail = campoEmail.getText().toString();
        String textoSenha = campoSenha.getText().toString();
        String tipoSexo = spinnerSexo.getSelectedItem().toString();


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
                        usuario.setNascimento(textoNascimento);
                        usuario.setSexo(tipoSexo);
                        usuario.setCpf(textoCpf);
                        usuario.setEmail(textoEmail);
                        usuario.setSenha(textoSenha);

                        cadastrarUsuario(usuario, textoCpf);

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

    public void cadastrarUsuario(final Usuario usuario, final String textoCpf) {


        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    try {

                        salvarFotoUsuario(imagem, textoCpf);
                        usuario.setId(textoCpf);
                        usuario.salvar();

                        //Atualizar nome no UserProfile
                        UsuarioFirebase.atualizarNomeUsuario(usuario.getNome());
                        startActivity(new Intent(CadastroActivity.this, PassageiroActivity.class));
                        usuario.setVeiculo(null);
                        usuario.salvar();
                        finish();
                        toast(R.string.toast_passageiro_cadastrado);

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



    public void toast(int string) {
        Toast.makeText(CadastroActivity.this, string, Toast.LENGTH_SHORT).show();
    }

    public void toast(String string) {
        Toast.makeText(CadastroActivity.this, string, Toast.LENGTH_SHORT).show();
    }
}
