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
import android.widget.Toast;
import com.carona.careasy.careasy.R;

import com.carona.careasy.careasy.activity.config.ConfiguracaoFirebase;
import com.carona.careasy.careasy.activity.helper.UsuarioFirebase;
import com.carona.careasy.careasy.activity.model.Requisicao;
import com.carona.careasy.careasy.activity.model.Usuario;
import com.carona.careasy.careasy.activity.model.Veiculo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.carona.careasy.careasy.activity.helper.Permissoes;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CadastroVeiculoActivity extends AppCompatActivity {


    private Spinner spinnerTipoVeiculo;
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
    private boolean statusImagem = false;
    private DatabaseReference firebaseRef;
    private  StorageReference storageReference;
    private String identificadorUsuario;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_veiculo);

        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        //INICIALIZAR COMPONENTES
        campoPlaca = findViewById(R.id.editCadastroPlaca);
        campoCor = findViewById(R.id.editCadastroCor);
        campoModelo = findViewById(R.id.editCadastroModelo);
        campoAno = findViewById(R.id.editCadastroAno);
        imageButtonCamera  = findViewById(R.id.imageButtonCamera);
        imageButtonGaleria = findViewById(R.id.imageButtonGaleria);
        imageViewCNH = findViewById(R.id.imageViewCNH);
        spinnerTipoVeiculo = findViewById(R.id.spinnerTipoVeiculo);
        storageReference = ConfiguracaoFirebase.getFirebaseStorage();


        //Inicializando Array Para Spinner
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this,R.array.spinner_tipoVeiculo, android.R.layout.simple_spinner_item);
        spinnerTipoVeiculo.setAdapter(adapter);

        //Validar permissões
        permissao.validarPermissoes(permissoesNecessarias, this, 1);

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
                    statusImagem = true;
                    //Recuperar dados da imagem para o firebase
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos );
                    byte[] dadosImagem = baos.toByteArray();

                    //Salvar imagem no firebase
                    StorageReference imagemRef = storageReference
                            .child("imagens")
                            .child("usuarios_CNH")

                            .child( identificadorUsuario+ ".jpeg");

                    UploadTask uploadTask = imagemRef.putBytes( dadosImagem );
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CadastroVeiculoActivity.this,
                                    "Erro ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(CadastroVeiculoActivity.this,
                                    "Sucesso ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

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

    public void validarCadastroVeiculo(View view) {
        //Recuperar textos dos campos
        String tipoVeiculo = spinnerTipoVeiculo.getSelectedItem().toString();
        String textoModelo = campoModelo.getText().toString();
        String textoCor = campoCor.getText().toString();
        String textoAno = campoModelo.getText().toString();
        String textoPlaca = campoPlaca.getText().toString();



        if (!tipoVeiculo.isEmpty() && tipoVeiculo != "Selecione") {//verifica nome
            if (!textoModelo.isEmpty()) {//verifica cpf
                if (!textoCor.isEmpty()) {//verifica e-mail
                    if (!textoAno.isEmpty()) {//verifica senha
                        if (!textoPlaca.isEmpty()) {//verifica senha
                            if (statusImagem ==true){
                                 final Usuario usuario = new Usuario();

                                Veiculo veiculo = new Veiculo();

                                veiculo.setDono(usuario);
                                veiculo.setTipo(tipoVeiculo);
                                veiculo.setModelo(textoModelo);
                                veiculo.setCor(textoCor);
                                veiculo.setAno(textoAno);
                                veiculo.setPlaca(textoPlaca);

                                veiculo.salvar();

                            } else {
                                toast("Faltou a Foto!");
                            }
                        } else {
                            toast(R.string.toast_email_vazio);
                        }
                    } else {
                        toast(R.string.toast_cpf_invalido);
                    }
                } else {
                    toast(R.string.toast_cpf_invalido);
                }
            } else {
                toast(R.string.toast_nome_vazio);
            }
        } else {
            toast(R.string.toast_nome_vazio);
        }
    }




    public void toast(int string) {
        Toast.makeText(CadastroVeiculoActivity.this, string, Toast.LENGTH_SHORT).show();
    }

    public void toast(String string) {
        Toast.makeText(CadastroVeiculoActivity.this, string, Toast.LENGTH_SHORT).show();
    }

    private void verificaStatusRequisicao() {

        final Usuario usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        DatabaseReference requisicoes = firebaseRef.child("Usuario");
        Query requisicaoPesquisa = requisicoes.orderByChild("nome").equalTo(usuarioLogado.getId());

        System.out.println("Nome: "+requisicaoPesquisa.toString());

    };


}
