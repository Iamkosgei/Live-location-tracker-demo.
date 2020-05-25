package com.iamkosgei.locationtracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity  implements OnMapReadyCallback {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference().child("location");
    private GoogleMap googleMap;
    private Button executeService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setStatusBarColor(Color.WHITE);

        executeService = findViewById(R.id.stop_service);

        setUpMap();

        if (isServiceRunningInForeground(this, BackgroundLocationService.class)) {
            setUpBtn(R.drawable.btn,"Stop Tracking",getResources().getColor(R.color.white));
        } else {
            setUpBtn(R.drawable.btn_white,"Start Tracking",getResources().getColor(R.color.rd));
        }
    }

    private void setUpMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void executeService(View view){
        Intent intent = new Intent(this, BackgroundLocationService.class);
       if(isServiceRunningInForeground(this,BackgroundLocationService.class)){
           stopService(intent);
           setUpBtn(R.drawable.btn_white,"Start Tracking",getResources().getColor(R.color.rd));
       }
       else {
           startService(intent);
           setUpBtn(R.drawable.btn,"Stop Tracking",getResources().getColor(R.color.white));
       }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        getUserLocationFromDb();
    }

    public void getUserLocationFromDb(){
        myRef.child("user1").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               UserLocation userLocation = dataSnapshot.getValue(UserLocation.class);
               setUpMarker(userLocation);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setUpMarker(UserLocation userLocation) {
        LatLng UserLocation = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
        googleMap.clear();
        googleMap.addMarker(new MarkerOptions().position(UserLocation)
                .title(getDate(userLocation.getTime())));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(UserLocation)
                .zoom(17)
                .build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        return DateFormat.format("dd-MM-yyyy", cal).toString();
    }



    public static boolean isServiceRunningInForeground(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {

                return service.foreground;
            }
        }
        return false;
    }

    public void setUpBtn(int drawable, String name, int color){
        executeService.setBackground(getDrawable(drawable));
        executeService.setText(name);
        executeService.setTextColor(color);
    }
}