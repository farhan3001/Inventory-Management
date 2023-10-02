package com.example.qtitest.activities.ui.assets;

import static android.content.Context.MODE_PRIVATE;

import static com.example.qtitest.Helper.*;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.qtitest.R;
import com.example.qtitest.activities.LoginActivity;
import com.example.qtitest.data.AssetItems;
import com.example.qtitest.data.LogoutResponse;
import com.example.qtitest.data.UserResponse;
import com.example.qtitest.databinding.FragmentAssetsBinding;
import com.example.qtitest.domain.APIClient;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AssetsFragment extends Fragment {

    private SharedPreferences sharedPreferencesToken, sharedPreferencesLog;
    private String tokenVal;

    private FragmentAssetsBinding binding;

    private ArrayList<AssetItems.Result> assetItemsResponse, assetTotal, allAssetSearched;

    private int page = 1, limit = 10;

    private AssetAdapter assetAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentAssetsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        View view = requireActivity().findViewById(R.id.bottomAppBar);
        view.setVisibility(View.VISIBLE);

        assetTotal = new ArrayList<AssetItems.Result>();

        View floatingButton = requireActivity().findViewById(R.id.add);
        floatingButton.setVisibility(View.VISIBLE);

        sharedPreferencesToken = root.getContext().getSharedPreferences("token", MODE_PRIVATE);
        sharedPreferencesLog = root.getContext().getSharedPreferences("login", MODE_PRIVATE);

        authMe();

        binding.topActionBar.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialogButtonClicked(root.getContext());
            }
        });

        binding.refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.refresh.setRefreshing(false);

                if (getFragmentManager() != null) {

                    assetTotal.clear();

                    getFragmentManager()
                            .beginTransaction()
                            .detach(AssetsFragment.this)
                            .attach(AssetsFragment.this)
                            .commit();
                    page = 1;
                    limit = 10;
                    getData(page, limit);
                }
            }
        });


        binding.nestedScrollView.scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    // reach last item position
                    page++;
                    binding.nestedScrollView.progressBar.setVisibility(View.VISIBLE);

                    getData(page, limit);
                }
            }
        });

        binding.search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    searchData();
                    return true;
                }
                return false;
            }
        });

        getData(page, limit);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void searchData() {
        String searchedData = binding.search.getText().toString();
        allAssetSearched = new ArrayList<>();

        for (int i = 0; i < assetTotal.size(); i++) {
            if (containsIgnoreCase(assetTotal.get(i).getName(), searchedData)) {
                allAssetSearched.add(assetTotal.get(i));
            }
        }
        if (searchedData.equalsIgnoreCase("") || searchedData.length() < 1) {
            allAssetSearched = assetTotal;
        }

        putAdapter(allAssetSearched);
    }

    private void getData(int page, int limit) {
        String token = sharedPreferencesToken.getString("tokenString", "No Token");

        Call<AssetItems> assetItemsResponseCall = APIClient.getService().getAllAsset("Bearer " + token, page, limit);
        assetItemsResponseCall.enqueue(new Callback<AssetItems>() {
            @Override
            public void onResponse(Call<AssetItems> call, Response<AssetItems> response) {

                if (response.isSuccessful() && response.body() != null) {
                    binding.nestedScrollView.progressBar.setVisibility(View.GONE);
                    AssetItems assetItemsResponseData = response.body();

                    ArrayList<AssetItems.Result> assetItems = assetItemsResponseData.getResults();

                    if (assetItems.size() > 0) {
                        assetTotal.addAll(assetItems);
                        putAdapter(assetTotal);
                    }
                }

            }

            @Override
            public void onFailure(Call<AssetItems> call, Throwable t) {

            }
        });
    }

    private void putAdapter(ArrayList<AssetItems.Result> assetItems) {
        assetAdapter = new AssetAdapter(assetItems);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireActivity());
        binding.nestedScrollView.recyclerView.setLayoutManager(layoutManager);
        binding.nestedScrollView.recyclerView.setAdapter(assetAdapter);
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

    public void showAlertDialogButtonClicked(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.alert_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;

        AppCompatButton confirm = dialog.findViewById(R.id.button_confirm);
        AppCompatButton cancel = dialog.findViewById(R.id.button_cancel);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

    private void authMe() {
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

}