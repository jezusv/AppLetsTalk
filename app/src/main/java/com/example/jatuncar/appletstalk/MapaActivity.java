package com.example.jatuncar.appletstalk;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MapaActivity extends AppCompatActivity {

    private static final String TAG = MapaActivity.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST = 100;
    private GoogleMap mMap;
    private List<User> users = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapa);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                initMap();
            }
        });

    }

    final UserAdapter UserAdapter = new UserAdapter();

    private void initMap(){

        if(ContextCompat.checkSelfPermission(MapaActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapaActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST);
            return;
        }
        mMap.setMyLocationEnabled(true);

        // Custom UiSettings
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);    // Controles de zoom
        uiSettings.setCompassEnabled(true); // Brújula
        uiSettings.setMapToolbarEnabled(true);
        uiSettings.setTiltGesturesEnabled(true);

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {

                final User addedUser = dataSnapshot.getValue(User.class);
                users = UserAdapter.getUsers();
                users.add(0, addedUser);

                LatLng latLng = new LatLng(addedUser.getLatitude(), addedUser.getLongitude());

                Marker marker= mMap.addMarker(new MarkerOptions().position(latLng).title(addedUser.getDisplayName()).snippet(addedUser.getEmail()));
                marker.setTag(addedUser.getPhotoUrl());
                marker.showInfoWindow();

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,14));

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                // Not implemented yet
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View view = getLayoutInflater().inflate(R.layout.tarjeta_contenido, null);

                Picasso.with(MapaActivity.this)
                        .load(marker.getTag().toString())
                        .into(((ImageView) view.findViewById(R.id.icon)));

                TextView titleText = view.findViewById(R.id.title);
                titleText.setText(marker.getTitle());

                TextView snippetText = view.findViewById(R.id.snippet);
                snippetText.setText(marker.getSnippet());

                return view;
            }
        });

        usersRef.addChildEventListener(childEventListener);

    }
}
