package com.carona.careasy.careasy.activity.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.carona.careasy.careasy.R;
import com.carona.careasy.careasy.activity.helper.UsuarioFirebase;
import com.carona.careasy.careasy.activity.model.Destino;
import com.carona.careasy.careasy.activity.model.Requisicao;
import com.carona.careasy.careasy.activity.model.Usuario;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.carona.careasy.careasy.activity.config.ConfiguracaoFirebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class PassageiroActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    //Componentes
    private EditText editDestino;
    private LinearLayout linearLayoutDestino;
    private Button buttonChamarCarona;

    private GoogleMap mMap;
    private FirebaseAuth autenticacao;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private LatLng localPassageiro;
    private boolean caronaChamada = false;
    private DatabaseReference firebaseRef;
    private Requisicao requisicao;

    //teste
    private Usuario usuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passageiro);



        inicializarComponentes();

        //Adicionar listener para status da requisição...
        verificaStatusRequisicao();

    }

    private void verificaStatusRequisicao() {

        final Usuario usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        DatabaseReference requisicoes = firebaseRef.child("requisicoes");
        Query requisicaoPesquisa = requisicoes.orderByChild("passageiro/id")
                .equalTo(usuarioLogado.getId());

        requisicaoPesquisa.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<Requisicao> lista = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    lista.add(ds.getValue(Requisicao.class));
                }

                Collections.reverse(lista);

                if (lista != null && lista.size()>0){
                    requisicao = lista.get(0);

                    switch (requisicao.getStatus()) {
                        case Requisicao.STATUS_AGUARDANDO:
                            linearLayoutDestino.setVisibility(View.GONE);
                            buttonChamarCarona.setText("Cancelar Uber");
                            caronaChamada = true;
                            break;

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Recuperar localizacao do usuário
        recuperarLocalizacaoUsuario();

    }



    public void chamarCarona(View view){

        if (!caronaChamada ){//Carona não foi Chamada...

            String enderecoDestino = editDestino.getText().toString();

            if( !enderecoDestino.equals("") || enderecoDestino != null ){

                Address addressDestino = recuperarEndereco( enderecoDestino );
                if( addressDestino != null ){

                    final Destino destino = new Destino();
                    destino.setCidade( addressDestino.getAdminArea() );
                    destino.setCep( addressDestino.getPostalCode() );
                    destino.setBairro( addressDestino.getSubLocality() );
                    destino.setRua( addressDestino.getThoroughfare() );
                    destino.setNumero( addressDestino.getFeatureName() );
                    destino.setLatitude( String.valueOf(addressDestino.getLatitude()) );
                    destino.setLongitude( String.valueOf(addressDestino.getLongitude()) );

                    StringBuilder mensagem = new StringBuilder();
                    mensagem.append( getString(R.string.msg_cidade) + destino.getCidade() );
                    mensagem.append( "\n"+getString(R.string.msg_rua) + destino.getRua() );
                    mensagem.append("\n"+getString(R.string.msg_bairro) + destino.getBairro() );
                    mensagem.append( "\n"+getString(R.string.msg_numero)+ destino.getNumero() );
                    mensagem.append( "\n"+getString(R.string.msg_cep) + destino.getCep() );

                    AlertDialog.Builder builder = new AlertDialog.Builder(this)
                            .setTitle(getString(R.string.msg_confirmar_enderco))
                            .setMessage(mensagem)
                            .setPositiveButton(getString(R.string.confirmar), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    //salvar requisição
                                    salvarRequisicao(destino);
                                    caronaChamada = true;

                                }
                            }).setNegativeButton(getString(R.string.cancelar), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }

            }else {
                Toast.makeText(this,
                        getString(R.string.toast_endereco_destino),
                        Toast.LENGTH_SHORT).show();
            }

        }else{//Cancelar Requisição...

            caronaChamada = false;
        }

    }
    private void salvarRequisicao(Destino destino){

        Requisicao requisicao = new Requisicao();
        requisicao.setDestino( destino );

        Usuario usuarioPassageiro = UsuarioFirebase.getDadosUsuarioLogado();
        usuarioPassageiro.setLatitude( String.valueOf( localPassageiro.latitude ) );
        usuarioPassageiro.setLongitude( String.valueOf( localPassageiro.longitude ) );

        requisicao.setPassageiro( usuarioPassageiro );
        requisicao.setStatus( Requisicao.STATUS_AGUARDANDO );
        requisicao.salvar();

        linearLayoutDestino.setVisibility(View.GONE); //Ocultar Layout...
        buttonChamarCarona.setText(R.string.btn_cancelar_carona); // Mudar nome do Botão...
    }

    private Address recuperarEndereco(String endereco){

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> listaEnderecos = geocoder.getFromLocationName(endereco, 1);
            if( listaEnderecos != null && listaEnderecos.size() > 0 ){
                Address address = listaEnderecos.get(0);

                return address;

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    private void recuperarLocalizacaoUsuario() {

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                //recuperar latitude e longitude
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                localPassageiro = new LatLng(latitude, longitude);

                mMap.clear();
                mMap.addMarker(
                        new MarkerOptions()
                                .position(localPassageiro)
                                .title(getString(R.string.hint_my_local))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.usuario))
                );
                mMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(localPassageiro, 20)
                );

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        //Solicitar atualização de localização
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    10000,
                    10,
                    locationListener
            );
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menuSair :
                autenticacao.signOut();
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void inicializarComponentes(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.titulo_passageiro);
        setSupportActionBar(toolbar);

        //Inicializar componentes
        editDestino = findViewById(R.id.editDestino);
        linearLayoutDestino = findViewById(R.id.linearLayoutDestino);
        buttonChamarCarona = findViewById(R.id.buttonChamarCarona);

        //Configurações iniciais
        firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

}
