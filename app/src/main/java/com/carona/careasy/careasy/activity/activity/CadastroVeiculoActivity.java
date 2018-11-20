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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toolbar;
import com.carona.careasy.careasy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.carona.careasy.careasy.activity.helper.Permissoes;

public class CadastroVeiculoActivity extends AppCompatActivity {



    private TextInputEditText campoPlaca, campoCor, campoModelo, campoAno;
    private ImageView imageViewCNH;
    private FirebaseAuth autenticacao;
    private Permissoes permissao;
    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    private ImageButton imageButtonCamera, imageButtonGaleria;
    private static final int SELECAO_CAMERA  = 100;
    private static final int SELECAO_GALERIA = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_veiculo);

        //INICIALIZAR COMPONENTES
        campoPlaca = findViewById(R.id.editCadastroPlaca);
        campoCor = findViewById(R.id.editCadastroCor);
        campoModelo = findViewById(R.id.editCadastroModelo);
        campoAno = findViewById(R.id.editCadastroAno);


        //Validar permissões
        permissao.validarPermissoes(permissoesNecessarias, this, 1);

        imageButtonCamera  = findViewById(R.id.imageButtonCamera);
        imageButtonGaleria = findViewById(R.id.imageButtonGaleria);
        imageViewCNH = findViewById(R.id.imageViewCNH);


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
            Bitmap imagem = null;

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

                    imageViewCNH.setImageBitmap( imagem );

                }

            }catch (Exception e){
                e.printStackTrace();
            }

        }

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
