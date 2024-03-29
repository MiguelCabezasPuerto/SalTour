package com.miguelcabezas.tfm.saltour;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Vista de la representación del mapa en su integración con Google Maps
 * @author Miguel Cabezas Puerto
 *
 * */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String lat, lon, name;

    /**
     * Gestiona los movimientos en el mapa
     */
    CameraUpdate cameraUpdate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        lat = getIntent().getExtras().getString("latitude");
        lon = getIntent().getExtras().getString("longitude");
        name = getIntent().getExtras().getString("name");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipula el mapa una vez disponible.
     * Esta devolución de llamada se activa cuando el mapa está listo para usarse.
     * Se añade el marcador del reto seleccionado recuperando sus coordenadas
     * Si los servicios de Google Play no están instalados en el dispositivo, se le pedirá al usuario que instale
     * dentro del SupportMapFragment. Este método solo se activará una vez que el usuario haya
     * instaló los servicios de Google Play y volvió a la aplicación.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    200);
        } else {
            mMap.setMyLocationEnabled(true);
        }

        LatLng challenge = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
        mMap.addMarker(new MarkerOptions().position(challenge).title(name));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(challenge, 90));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener(){
            @Override
            public boolean onMyLocationButtonClick()
            {
                boolean ubicacion=localizacionActiva(getApplicationContext());

                if(ubicacion){
                    Location loc = mMap.getMyLocation();
                    if (loc != null) {
                        LatLng latLng = new LatLng(loc.getLatitude(), loc
                                .getLongitude());
                        cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 7);
                        mMap.animateCamera(cameraUpdate);

                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),getString(R.string.activar_ubicacion),Toast.LENGTH_LONG).show();
                }


                return false;
            }
        });
    }

    @SuppressLint("MissingPermission")
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 200: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    mMap.setMyLocationEnabled(true);
                } else {
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    public boolean localizacionActiva(Context c){
        String provider = Settings.Secure.getString(c.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (provider.contains("gps") || provider.contains("network")){
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        /*super.onBackPressed();*/
        setResult(Activity.RESULT_FIRST_USER);
        finish();
    }
}