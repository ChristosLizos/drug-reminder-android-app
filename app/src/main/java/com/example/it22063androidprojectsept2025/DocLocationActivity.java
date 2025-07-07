package com.example.it22063androidprojectsept2025;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.it22063androidprojectsept2025.databinding.ActivityDocLocationBinding;

public class DocLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap; // GoogleMap object for interacting with the map
    private ActivityDocLocationBinding binding; // View binding for layout

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the view using ViewBinding (activity_doc_location.xml should have a <fragment> with id map)
        binding = ActivityDocLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get the map fragment and register the callback when the map is ready
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map); // This ID should exist in the layout
        mapFragment.getMapAsync(this); // Calls onMapReady() when map is ready
    }

    /**
     * Called when the Google Map is ready to use
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap; // Save reference to the map

    }
}
