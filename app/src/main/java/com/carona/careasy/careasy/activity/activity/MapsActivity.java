package com.carona.careasy.careasy.activity.activity;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.carona.careasy.careasy.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String[] permissoes = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private LocationManager locationManager;
    private LocationListener locaitonListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //Validar Permissões
        Permissoes.validarPermissoes(permissoes, this, 1);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        //Objeto responsável por gerenciar a localização do Usuário.
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locaitonListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("Localizacao", "onLocationChanged: "+location.toString());

                Double latitude = location.getLatitude();
                Double longitude = location.getLongitude();

                LatLng localUsuario = new LatLng(latitude, longitude);

                markerMap( latitude, longitude);

                mMap.moveCamera(CameraUpdateFactory
                        .newLatLngZoom(localUsuario, 18)
                );




            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0,
                    0,
                    locaitonListener
            );
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int permissaoResultado : grantResults) {
            if (permissaoResultado == PackageManager.PERMISSION_DENIED) {
                //Alerta
                alertaValidacaoPermissao();
            } else if (permissaoResultado == PackageManager.PERMISSION_GRANTED) {
                //Recuperar localizacao do usuario.

                /*
                 * 1) Provedor da localização
                 * 2) Tempo mínimo entre atualizações de localização (milesegundos)
                 * 3) Distancia minima entre atualizações de localização (metros)
                 * 4) Location Listerner (para recebermos as atualizações)
                 */
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            10000,
                            2,
                            locaitonListener
                    );
                }
            }
        }
    }
    public void alertaValidacaoPermissao(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utilizar o app é nescessário aceitar as permições");
        builder.setCancelable(false);
        builder.setPositiveButton("'Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    public void markerMap(Double latitude, Double longitude){
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            //Geocoding -> processo de transformar um endereço ou descrição de um local em latitude/longitude.

            List<Address> listaEndereco = geocoder.getFromLocation(latitude,longitude,1);

            //Reverse Geocoding -> processo de transformar latitude/longitude em um endereço.
            //String nome = " R. Tuere, 63 - Colônia Terra Nova, Manaus - AM, 69093-095, Brasil";
            //List<Address> listaEndereco = geocoder.getFromLocationName(nome,1);

            if (listaEndereco != null && listaEndereco.size() > 0){
                Address endereco = listaEndereco.get(0);

                Double lat = endereco.getLatitude();
                Double lon = endereco.getLongitude();

                mMap.clear();
                LatLng localUsuario = new LatLng(lat, lon);
                mMap.addMarker(new MarkerOptions()
                        .position(localUsuario)
                        .title(endereco.getAddressLine(0))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_my_location))
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
