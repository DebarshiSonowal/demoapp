package com.deb.demoapp;

import android.Manifest;
import android.app.Activity;
import android.app.VoiceInteractor;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.deb.demoapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static androidx.core.content.ContextCompat.getSystemService;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    //initialize variable
    private GoogleMap mMap;
    LocationManager locationManager;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    Marker marker;
    LocationListener locationListener;
    Button btDraw,btClear,btLoc;
    Polygon mPolygon = null;
    List<LatLng> mLatLngs = new ArrayList<>();
    List<Marker> markerList = new ArrayList<>();
    LatLng mLng;
    private  static  final int Request_Code=101;
    Boolean flag = false,incr = false;
    final static int polypoint = 5;
    int k = 1;
    int red =0, blue = 0, green = 0;
    String polyname;
    DatabaseReference rootRef,locRef,markRef;
    Activity activity = getActivity();

    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        btLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = true;
            }
        });

        btClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPolygon != null) mPolygon.remove();
                for(Marker marker:markerList) marker.remove();
                mLatLngs.clear();

            }
        });
        btDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PolygonOptions polygonOptions = new PolygonOptions().addAll(mLatLngs).clickable(true);

                mPolygon = mMap.addPolygon(polygonOptions);
                mPolygon.setTag("First Location");
                mPolygon.setStrokeColor(Color.rgb(red,green,blue));
                mPolygon.setFillColor(Color.BLACK);
                mLatLngs.clear();
                for(Marker marker:markerList) marker.remove();
                incr = true;


            }

        });

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        LocationManager locationManager =
                (LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);


        btDraw = this.getView().findViewById(R.id.bt_draw);
        btClear =this.getView().findViewById(R.id.bt_clear);
        btLoc = this.getView().findViewById(R.id.bt_loc);


        //database reference
        rootRef = FirebaseDatabase.getInstance().getReference();

        //database reference
        locRef = rootRef.child("Current Location");
        markRef = FirebaseDatabase.getInstance().getReference("Marked Location");
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
        else{

            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    LocationHelper helper = new LocationHelper(location.getLongitude(),location.getLatitude());
                    locRef.setValue(helper).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Log.d("Saved","Location saved");
                            }else {
                                Log.d("Saved","Location not saved");
                            }

                        }
                    });

                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    //get the location name from latitude and longitude
                    Geocoder geocoder = new Geocoder(getContext());
                    try {
                        List<Address> addresses =
                                geocoder.getFromLocation(latitude, longitude, 1);
                        String result = addresses.get(0).getLocality()+":";
                        result += addresses.get(0).getCountryName();
                        LatLng latLng = new LatLng(latitude, longitude);
                        if (marker != null){
                            if(flag) {
                                marker.remove();
                                marker = mMap.addMarker(new MarkerOptions().position(latLng).title(result).icon(BitmapDescriptorFactory.fromResource(R.drawable.purple)));

                                CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(20).build();
                                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                flag = false;
                            }
                        }
                        else{
                            marker = mMap.addMarker(new MarkerOptions().position(latLng).title(result).icon(BitmapDescriptorFactory.fromResource(R.drawable.purple)));


                            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(20).build();
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){

            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if (flag) {
                        LocationHelper helper = new LocationHelper(location.getLongitude(), location.getLatitude());
                        locRef.setValue(helper).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    Log.d("Saved", "Location saved");
                                } else {

                                    Log.d("Saved", "Location not saved");
                                }

                            }
                        });
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        //get the location name from latitude and longitude
                        Geocoder geocoder = new Geocoder(getContext());
                        try {
                            List<Address> addresses =
                                    geocoder.getFromLocation(latitude, longitude, 1);
                            String result = addresses.get(0).getLocality() + ":";
                            result += addresses.get(0).getCountryName();
                            LatLng latLng = new LatLng(latitude, longitude);
                            if (marker != null) {
                                marker.remove();
                                marker = mMap.addMarker(new MarkerOptions().position(latLng).title(result).icon(BitmapDescriptorFactory.fromResource(R.drawable.purple)));
                                CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(20).build();
                                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            } else {
                                marker = mMap.addMarker(new MarkerOptions().position(latLng).title(result).icon(BitmapDescriptorFactory.fromResource(R.drawable.purple)));


                                CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(20).build();
                                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            }


                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    flag = false;
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
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.setBuildingsEnabled(true);
        mMap.setIndoorEnabled(true);
        mMap.setMinZoomPreference(1.0f);
        mMap.setMaxZoomPreference(25.0f);
        CameraUpdateFactory.scrollBy(6, 6);
        mMap .setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (flag) {
                    for (Marker marker : markerList) marker.remove();
                }
                MarkerOptions markerOptions = new MarkerOptions().position(latLng).draggable(true);
                Marker marker = mMap.addMarker(markerOptions);
                mLatLngs.add(latLng);
                markerList.add(marker);
                int pol = mLatLngs.size()/polypoint;
                for (int i = 1; i <= pol; i++) {
                    for (int j = 0; j < polypoint; j++) {
                        mLng = mLatLngs.get(j);
                        polylocation loc = new polylocation(mLng.longitude,mLng.latitude);
                        markRef.child(k+" st poly").child(j + " no corners").setValue(loc);

                    }
                    k++;
                }

            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        locationManager.removeUpdates(locationListener);
    }
}
