package com.example.qtitest.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.qtitest.R;
import com.example.qtitest.activities.ui.assets.AssetsFragment;
import com.example.qtitest.activities.ui.home.HomeFragment;
import com.example.qtitest.activities.ui.input.InputFragment;
import com.example.qtitest.data.LoginResponse;
import com.example.qtitest.data.RefreshedToken;
import com.example.qtitest.databinding.ActivityMainBinding;
import com.example.qtitest.domain.APIClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private LoginResponse data;

    private String token;

    private SharedPreferences sharedPreferencesToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferencesToken = getSharedPreferences("token", MODE_PRIVATE);

        try {
            Bundle bundle = getIntent().getExtras();
            data = (LoginResponse) bundle.get("data");
            token = data.getToken().toString();
        } catch (Exception e) {

        }

        if (!TextUtils.isEmpty(token)) {
            sharedPreferencesToken.edit().putString("tokenString", token).apply();
        }

//
//        String data = bundle.getString("data");

//        data = bundle.getSerializable("data");
//        Log.d("data", data.toString());

//        BottomNavigationView navView = findViewById(R.id.bottomNavigationView);
//        // Passing each menu ID as a set of Ids because each
//        // menu should be considered as top level destinations.
//        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_assets)
//                .build();
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
////        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//        NavigationUI.setupWithNavController(binding.navView, navController);

        replaceFragment(new HomeFragment());
        binding.bottomNavigationView.setBackground(null);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.navigation_assets:
                    replaceFragment(new AssetsFragment());
                    break;
            }
            return true;
        });

        binding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new InputFragment());
            }
        });

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}