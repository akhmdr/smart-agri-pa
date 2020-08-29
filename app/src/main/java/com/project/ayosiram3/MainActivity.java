package com.project.ayosiram3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.project.ayosiram3.riwayatSoil.RiwayatSoilFragment;
import com.project.ayosiram3.riwayatTemp.RiwayatTempFragment;
import com.project.ayosiram3.riwayatWater.RiwayatWaterFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(MainActivity.this,
                new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        String newtToken = instanceIdResult.getToken();
                        Toast.makeText(MainActivity.this,newtToken,Toast.LENGTH_SHORT).show();
                        Log.d("DEVICE_TOKEN", newtToken);
                        databaseReference.child("Device/Token").setValue(newtToken);
                    }
                });

        Toolbar toolbar = findViewById(R.id.theToolbar);
        setSupportActionBar(toolbar);
        try {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }catch (NullPointerException e){}

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
                    new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_dashboard);
        }
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_dashboard:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HomeFragment()).commit();
                break;
            case R.id.nav_soil:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new RiwayatSoilFragment()).commit();
                break;
            case R.id.nav_temp:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new RiwayatTempFragment()).commit();
                break;
            case R.id.nav_water_rw:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new RiwayatWaterFragment()).commit();
                break;
            case R.id.nav_relay:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new SetRelayFragment()).commit();
                break;
            case R.id.nav_water:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new SetWaterFragment()).commit();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}