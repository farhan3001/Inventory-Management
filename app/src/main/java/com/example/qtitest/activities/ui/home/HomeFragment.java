package com.example.qtitest.activities.ui.home;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.qtitest.R;
import com.example.qtitest.activities.LoginActivity;
import com.example.qtitest.data.AssetResponseHome;
import com.example.qtitest.data.LocationResponseHome;
import com.example.qtitest.data.LogoutResponse;
import com.example.qtitest.data.RefreshedToken;
import com.example.qtitest.data.UserResponse;
import com.example.qtitest.databinding.FragmentHomeBinding;
import com.example.qtitest.domain.APIClient;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    public SharedPreferences sharedPreferencesToken, sharedPreferencesLog;
    public String tokenVal;

    public ArrayList barChartAsset, barChartLocation;

    public AppCompatButton cancel, confirm;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        sharedPreferencesLog = root.getContext().getSharedPreferences("login", MODE_PRIVATE);
        sharedPreferencesToken = root.getContext().getSharedPreferences("token", MODE_PRIVATE);

        View view = requireActivity().findViewById(R.id.bottomAppBar);
        view.setVisibility(View.VISIBLE);

        View floatingButton = requireActivity().findViewById(R.id.add);
        floatingButton.setVisibility(View.VISIBLE);

        binding.swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshToken();
                binding.swipeToRefresh.setRefreshing(false);

                if (getFragmentManager() != null) {

                    getFragmentManager()
                            .beginTransaction()
                            .detach(HomeFragment.this)
                            .attach(HomeFragment.this)
                            .commit();
                }
            }
        });

        binding.topActionBar.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialogButtonClicked(root.getContext());
            }
        });

        authMe();
        assetStatus();
        locationStatus();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    public void logout(Context context) {
        String token = sharedPreferencesToken.getString("tokenString", "No Token");
        Call<LogoutResponse> logoutResponseCall = APIClient.getService().logoutUser("Bearer " + token);
        logoutResponseCall.enqueue(new Callback<LogoutResponse>() {
            @Override
            public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {
                if (response.isSuccessful()) {
                    LogoutResponse logoutResponse = response.body();
                    if (logoutResponse != null) {
                        sharedPreferencesLog.edit().putBoolean("logged", false).apply();
                        startActivity(new Intent(context, LoginActivity.class));
                    }
                }
            }

            @Override
            public void onFailure(Call<LogoutResponse> call, Throwable t) {

            }
        });

    }

    public void authMe() {
        String token = sharedPreferencesToken.getString("tokenString", "No Token");

        Call<UserResponse> userResponseCall = APIClient.getService().user("Bearer " + token);
        userResponseCall.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful()) {
                    UserResponse userResponse = response.body();
                    if (userResponse != null) {
                        binding.topActionBar.name.setText(userResponse.getUsername());
                        binding.topActionBar.email.setText(userResponse.getEmail());
                    }
                } else {
                    Toast.makeText(requireActivity(), "Error logging out", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {

            }
        });
    }

    private void refreshToken() {
        Call<RefreshedToken> refreshedTokenCall = APIClient.getService().refreshToken("Muhammad Farhan", "farhan1234");
        refreshedTokenCall.enqueue(new Callback<RefreshedToken>() {
            @Override
            public void onResponse(Call<RefreshedToken> call, Response<RefreshedToken> response) {
                if (response.isSuccessful()) {
                    RefreshedToken refreshedToken = response.body();
                    if (refreshedToken != null) {
                        tokenVal = refreshedToken.getAccess_token();
                        sharedPreferencesToken.edit().putString("tokenString", tokenVal).apply();
                    }

                } else {
                    Toast.makeText(requireActivity(), "Error refreshing token", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RefreshedToken> call, Throwable t) {

            }
        });
    }

    private void assetStatus() {
        String token = sharedPreferencesToken.getString("tokenString", "No Token");

        Call<AssetResponseHome> assetResponseHomeCall = APIClient.getService().assetStatusHome("Bearer " + token);
        assetResponseHomeCall.enqueue(new Callback<AssetResponseHome>() {
            @Override
            public void onResponse(Call<AssetResponseHome> call, Response<AssetResponseHome> response) {
                if (response.isSuccessful()) {
                    AssetResponseHome assetResponseHome = response.body();
                    if (assetResponseHome != null) {
                        int expiredAssets = assetResponseHome.getResults().get(0).getCount();
                        int assetSold = assetResponseHome.getResults().get(1).getCount();
                        int assetInStock = assetResponseHome.getResults().get(2).getCount();

                        String assetInStockString = "" + assetInStock;
                        String assetSoldString = "" + assetSold;
                        String expiredAssetsString = "" + expiredAssets;

                        binding.assetInStockText.setText(assetInStockString);
                        binding.assetSoldText.setText(assetSoldString);
                        binding.expiredAssetsText.setText(expiredAssetsString);

                        getDataAssetsChart(assetSold, assetInStock, expiredAssets);


                        BarDataSet barDataSet = new BarDataSet(barChartAsset, "Status");
                        BarData data = new BarData(barDataSet);

                        binding.barChartStatus.setData(data);

                        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                        barDataSet.setValueTextColor(getResources().getColor(R.color.black));
                        barDataSet.setValueTextSize(16f);

                        binding.barChartStatus.getDescription().setEnabled(false);
                        binding.barChartStatus.setDoubleTapToZoomEnabled(false);
                        binding.barChartStatus.setPinchZoom(false);
                    }
                } else {
                    Toast.makeText(requireActivity(), "Error retrieving data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AssetResponseHome> call, Throwable t) {

            }
        });
    }

    private void getDataAssetsChart(int sold, int stored, int expired) {
        barChartAsset = new ArrayList();
        barChartAsset.add(new BarEntry(2f, sold));
        barChartAsset.add(new BarEntry(3f, stored));
        barChartAsset.add(new BarEntry(4f, expired));
    }

    private void locationStatus() {
        String token = sharedPreferencesToken.getString("tokenString", "No Token");

        Call<LocationResponseHome> locationResponseHomeCall = APIClient.getService().locationResponseHome("Bearer " + token);
        locationResponseHomeCall.enqueue(new Callback<LocationResponseHome>() {
            @Override
            public void onResponse(Call<LocationResponseHome> call, Response<LocationResponseHome> response) {
                if (response.isSuccessful()) {
                    LocationResponseHome locationResponseHome = response.body();

                    if (locationResponseHome != null) {
                        int gudang = locationResponseHome.getResult().get(0).getCount();
                        int rak = locationResponseHome.getResult().get(1).getCount();

                        String gudangString = "" + gudang;
                        String rakString = "" + rak;

                        binding.gudangText.setText(gudangString);
                        binding.rakPenjualanText.setText(rakString);

                        getDataLocationChart(gudang, rak);

                        BarDataSet barDataSet = new BarDataSet(barChartLocation, "Location");
                        BarData data = new BarData(barDataSet);

                        binding.barChartLocation.setData(data);

                        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                        barDataSet.setValueTextColor(getResources().getColor(R.color.black));
                        barDataSet.setValueTextSize(16f);

                        binding.barChartLocation.getDescription().setEnabled(false);

                        binding.barChartLocation.setDoubleTapToZoomEnabled(false);
                        binding.barChartLocation.setPinchZoom(false);
                    }

                } else {
                    Toast.makeText(requireActivity(), "Error retrieving data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LocationResponseHome> call, Throwable t) {

            }
        });
    }

    private void getDataLocationChart(int gudang, int rak) {
        barChartLocation = new ArrayList();
        barChartLocation.add(new BarEntry(2f, gudang));
        barChartLocation.add(new BarEntry(3f, rak));
    }

    public void showAlertDialogButtonClicked(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.alert_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;

        confirm = dialog.findViewById(R.id.button_confirm);
        cancel = dialog.findViewById(R.id.button_cancel);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshToken();
                logout(context);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

    }
}