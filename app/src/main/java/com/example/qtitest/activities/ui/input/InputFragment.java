package com.example.qtitest.activities.ui.input;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.qtitest.R;
import com.example.qtitest.activities.MainActivity;
import com.example.qtitest.data.AssetRequest;
import com.example.qtitest.data.AssetResponse;
import com.example.qtitest.databinding.FragmentInputBinding;
import com.example.qtitest.domain.APIClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InputFragment extends Fragment {

    private FragmentInputBinding binding;

    public SharedPreferences sharedPreferencesToken;

    String assetsStatus[] = {"Sold", "In Stock", "Expired"};
    String assetsLocation[] = {"Gudang", "Rak Penjualan"};

    ArrayAdapter<String> adapterStatusAsset, adapterLocationAsset;

    String idAssetStatus, idAssetLocation;

    public String tokenVal;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        InputViewModel dashboardViewModel =
                new ViewModelProvider(this).get(InputViewModel.class);

        binding = FragmentInputBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        sharedPreferencesToken = root.getContext().getSharedPreferences("token", MODE_PRIVATE);

        View view = requireActivity().findViewById(R.id.bottomAppBar);
        view.setVisibility(View.GONE);

        View floatingButton = requireActivity().findViewById(R.id.add);
        floatingButton.setVisibility(View.GONE);

        binding.topActionBar.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(requireActivity(), MainActivity.class));
            }
        });

        adapterStatusAsset = new ArrayAdapter<String>(requireActivity(), R.layout.list_item, assetsStatus);
        adapterLocationAsset = new ArrayAdapter<String>(requireActivity(), R.layout.list_item, assetsLocation);

        binding.autoCompleteTxtStatus.setAdapter(adapterStatusAsset);
        binding.autoCompleteTxtLocation.setAdapter(adapterLocationAsset);

        binding.inputAssetEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.inputAssetTil.setHelperText(getResources().getString(R.string.required));
                binding.inputAssetTil.setHelperTextEnabled(true);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(binding.inputAssetEt.getText().toString())) {
                    binding.inputAssetTil.setHelperText("");
                    binding.inputAssetTil.setHelperTextEnabled(false);
                } else {
                    binding.inputAssetTil.setHelperText(getResources().getString(R.string.required));
                    binding.inputAssetTil.setHelperTextEnabled(true);
                }
            }
        });

        binding.autoCompleteTxtStatus.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.statusInputAsset.setHelperText(getResources().getString(R.string.required));
                binding.statusInputAsset.setHelperTextEnabled(true);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(binding.autoCompleteTxtStatus.getText().toString())) {
                    binding.statusInputAsset.setHelperText("");
                    binding.statusInputAsset.setHelperTextEnabled(false);
                } else {
                    binding.statusInputAsset.setHelperText(getResources().getString(R.string.required));
                    binding.statusInputAsset.setHelperTextEnabled(true);
                }
            }
        });

        binding.autoCompleteTxtLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                binding.locationInputAsset.setHelperText(getResources().getString(R.string.required));
                binding.locationInputAsset.setHelperTextEnabled(true);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(binding.autoCompleteTxtLocation.getText().toString())) {
                    binding.locationInputAsset.setHelperText("");
                    binding.locationInputAsset.setHelperTextEnabled(false);
                } else {
                    binding.locationInputAsset.setHelperText(getResources().getString(R.string.required));
                    binding.locationInputAsset.setHelperTextEnabled(true);
                }
            }
        });

        binding.autoCompleteTxtLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        binding.autoCompleteTxtStatus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        binding.buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(binding.autoCompleteTxtLocation.getText().toString())
                        && !TextUtils.isEmpty(binding.inputAssetEt.getText().toString())
                        && !TextUtils.isEmpty(binding.inputAssetEt.getText().toString())) {
                    inputData();
                } else {
                    Toast.makeText(requireActivity(), "Please fill in the required information", Toast.LENGTH_SHORT).show();
                }

            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void inputData() {

        String token = sharedPreferencesToken.getString("tokenString", "No Token");
        String autoCompleteLocation = binding.autoCompleteTxtLocation.getText().toString();
        String autoCompleteStatus = binding.autoCompleteTxtStatus.getText().toString();
        String assetName = binding.inputAssetEt.getText().toString();

        if (autoCompleteStatus.equalsIgnoreCase("Sold")) {
            idAssetStatus = "c8179b05-6a77-41a3-a564-089b0bc323ff";
        }
        if (autoCompleteStatus.equalsIgnoreCase("In Stock")) {
            idAssetStatus = "ddb92699-2f09-4c23-83dd-d0197107f757";
        }
        if (autoCompleteStatus.equalsIgnoreCase("Expired")) {
            idAssetStatus = "bd455ff3-1b85-4730-855c-5bf0018db2e5";
        }
        if (autoCompleteLocation.equalsIgnoreCase("Gudang")) {
            idAssetLocation = "aa104896-3f10-42cf-88d7-3a567961f8e4";
        }
        if (autoCompleteLocation.equalsIgnoreCase("Rak Penjualan")) {
            idAssetLocation = "be1a090f-9f0e-446c-b0f9-8aee486ba0be";
        }

        AssetRequest assetRequestBody = new AssetRequest();
        assetRequestBody.setName(assetName);
        assetRequestBody.setStatus_id(idAssetStatus);
        assetRequestBody.setLocation_id(idAssetLocation);

        Call<AssetResponse> assetResponseCall = APIClient.getService().createAsset("Bearer " + token, assetRequestBody);
        assetResponseCall.enqueue(new Callback<AssetResponse>() {
            @Override
            public void onResponse(Call<AssetResponse> call, Response<AssetResponse> response) {
                if (response.isSuccessful()) {
                    Dialog dialog = new Dialog(requireActivity());
                    dialog.setContentView(R.layout.success_data_post);
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.setCancelable(false);
                    dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();
                } else {
                    Toast.makeText(requireActivity(), "Error submitting data", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<AssetResponse> call, Throwable t) {

            }
        });
    }
}