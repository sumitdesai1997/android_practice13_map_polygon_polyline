package com.example.android_practice13_map_polygon_polyline;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.android_practice13_map_polygon_polyline.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    // create the application as usual we're creating for fetching user location, then just add marker at user location.
    // then keep adding marker on the long click of map, and draw polygon and polyline as per our need (by implementing that logic inside long click of map method)

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    public static final int REQUEST_CODE = 1;

    LocationManager locationManager;
    LocationListener locationListener;

    private Marker currentMarker;
    private Marker destMarker;
    Polyline line;
    Polygon shape;
    private static final int POLYGON_SIDES = 5;
    List<Marker> markerList = new ArrayList();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                setCurrentLocationMarker(location);
            }
        };

        if (!hasLocationPermission()) {
            requestLocationPermission();
        } else {
            startUpdateLocation();
        }

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                setMarker(latLng);
            }

            private void setMarker(LatLng latLng) {
                MarkerOptions marker = new MarkerOptions().position(latLng)
                        .title("Your destination");

                /*if (destMarker != null){
                    clearMap();
                };

                destMarker = mMap.addMarker(marker);
                drawPolyline();*/

                if(markerList.size() == POLYGON_SIDES){
                    clearMap();
                }

                markerList.add(mMap.addMarker(marker));
                if(markerList.size() == POLYGON_SIDES){
                    drawPolygon();
                }
            }

            private void drawPolygon() {
                PolygonOptions polygon = new PolygonOptions()
                        .fillColor(0x33000000)
                        .strokeColor(Color.RED)
                        .strokeWidth(5);

                for(int i=0;  i<POLYGON_SIDES; i++){
                    polygon.add(markerList.get(i).getPosition());
                }
                shape = mMap.addPolygon(polygon);
            }

            /*private void drawPolyline() {
                PolylineOptions polyline = new PolylineOptions()
                        .color(Color.BLACK)
                        .width(10)
                        .add(currentMarker.getPosition(), destMarker.getPosition());
                line =mMap.addPolyline(polyline);
            }*/

            private void clearMap() {
                /*if(destMarker != null){
                    destMarker.remove();
                    destMarker = null;
                }
                line.remove();*/

                for(Marker marker:markerList){
                    marker.remove();
                }
                markerList.clear();
                shape.remove();
                shape = null;
            }
        });

    }

    private void setCurrentLocationMarker(Location location) {
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions marker = new MarkerOptions().position(userLocation)
                .title("You are here")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .snippet("Your location");
        currentMarker = mMap.addMarker(marker);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15));
    }

    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }


    private void startUpdateLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (REQUEST_CODE == requestCode) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
        }
    }
}