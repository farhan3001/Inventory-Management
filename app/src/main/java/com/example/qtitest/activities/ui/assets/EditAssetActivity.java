package com.example.qtitest.activities.ui.assets;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.qtitest.R;
import com.example.qtitest.data.AssetGetResponse;
import com.example.qtitest.data.AssetItems;
import com.example.qtitest.data.AssetRequest;
import com.example.qtitest.data.AssetResponse;
import com.example.qtitest.databinding.ActivityEditAssetBinding;
import com.example.qtitest.domain.APIClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditAssetActivity extends AppCompatActivity {

    private AssetItems.Result data;

    private ActivityEditAssetBinding binding;

    private SharedPreferences sharedPreferencesToken;

    String assetId, assetName, idAssetStatus, idAssetLocation;

    String assetsStatus[] = {"Sold", "In Stock", "Expired"};
    String assetsLocation[] = {"Gudang", "Rak Penjualan"};

    ArrayAdapter<String> adapterStatusAsset, adapterLocationAsset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditAssetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        data = (AssetItems.Result) bundle.get("dataAsset");

        if (data != null) {
            assetId = data.getId();
            assetName = data.getName();
        }

        adapterStatusAsset = new ArrayAdapter<String>(EditAssetActivity.this, R.layout.list_item, assetsStatus);
        adapterLocationAsset = new ArrayAdapter<String>(EditAssetActivity.this, R.layout.list_item, assetsLocation);

        binding.autoCompleteTxtStatus.setAdapter(adapterStatusAsset);
        binding.autoCompleteTxtLocation.setAdapter(adapterLocationAsset);

        binding.topActionBar.title.setText(getResources().getString(R.string.edit_asset));
        sharedPreferencesToken = this.getSharedPreferences("token", MODE_PRIVATE);

        getDataById();

        binding.buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(binding.autoCompleteTxtLocation.getText().toString())
                        && !TextUtils.isEmpty(binding.inputAssetEt.getText().toString())
                        && !TextUtils.isEmpty(binding.inputAssetEt.getText().toString())) {
                    editData();

                } else {
                    Toast.makeText(EditAssetActivity.this, "Please fill in the required information", Toast.LENGTH_SHORT).show();
                }

            }
        });

        binding.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialogButtonClicked(EditAssetActivity.this);
            }
        });

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

        binding.topActionBar.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });
    }

    private void getDataById() {
        String token = sharedPreferencesToken.getString("tokenString", "No Token");
        Call<AssetGetResponse> assetGetResponseCall = APIClient.getService().getAssetById("Bearer " + token, assetId);
        assetGetResponseCall.enqueue(new Callback<AssetGetResponse>() {
            @Override
            public void onResponse(Call<AssetGetResponse> call, Response<AssetGetResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AssetGetResponse asset = response.body();

                    binding.inputAssetEt.setText(asset.getName());
                    binding.autoCompleteTxtLocation.setText(asset.getLocation().getName());

                    String assetStatusId = asset.getStatus().getId();

                    if (assetStatusId.equals("c8179b05-6a77-41a3-a564-089b0bc323ff")) {
                        binding.autoCompleteTxtStatus.setText("Sold");
                    }
                    if (assetStatusId.equals("ddb92699-2f09-4c23-83dd-d0197107f757")) {
                        binding.autoCompleteTxtStatus.setText("In Stock");
                    }
                    if (assetStatusId.equals("bd455ff3-1b85-4730-855c-5bf0018db2e5")) {
                        binding.autoCompleteTxtStatus.setText("Expired");
                    }
                }
            }

            @Override
            public void onFailure(Call<AssetGetResponse> call, Throwable t) {

            }
        });

    }

    private void editData() {
        String token = sharedPreferencesToken.getString("tokenString", "No Token");

        String name = binding.inputAssetEt.getText().toString();
        String location = binding.autoCompleteTxtLocation.getText().toString();
        String status = binding.autoCompleteTxtStatus.getText().toString();

        if (status.equalsIgnoreCase("Sold")) {
            idAssetStatus = "c8179b05-6a77-41a3-a564-089b0bc323ff";
        }
        if (status.equalsIgnoreCase("In Stock")) {
            idAssetStatus = "ddb92699-2f09-4c23-83dd-d0197107f757";
        }
        if (status.equalsIgnoreCase("Expired")) {
            idAssetStatus = "bd455ff3-1b85-4730-855c-5bf0018db2e5";
        }
        if (location.equalsIgnoreCase("Gudang")) {
            idAssetLocation = "aa104896-3f10-42cf-88d7-3a567961f8e4";
        }
        if (location.equalsIgnoreCase("Rak Penjualan")) {
            idAssetLocation = "be1a090f-9f0e-446c-b0f9-8aee486ba0be";
        }

        AssetRequest assetRequestBody = new AssetRequest();
        assetRequestBody.setName(name);
        assetRequestBody.setStatus_id(idAssetStatus);
        assetRequestBody.setLocation_id(idAssetLocation);

        Call<AssetResponse> assetResponseCall = APIClient.getService().editAsset("Bearer " + token, assetId, assetRequestBody);
        assetResponseCall.enqueue(new Callback<AssetResponse>() {
            @Override
            public void onResponse(Call<AssetResponse> call, Response<AssetResponse> response) {
                setDialogSuccessEdit();
            }

            @Override
            public void onFailure(Call<AssetResponse> call, Throwable t) {

            }
        });
    }

    private void deleteData() {
        // call this on successful deletion
        String token = sharedPreferencesToken.getString("tokenString", "No Token");

        Call<String> deleteAssetCall = APIClient.getService().deleteAsset("Bearer " + token, assetId);
        deleteAssetCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                setDialogSuccessDelete();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

    }

    public void showAlertDialogButtonClicked(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.alert_dialog_delete);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;

        AppCompatButton confirm = dialog.findViewById(R.id.button_confirm);
        AppCompatButton cancel = dialog.findViewById(R.id.button_cancel);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteData();
                dialog.cancel();
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

    public void setDialogSuccessEdit() {
        Dialog dialog = new Dialog(EditAssetActivity.this);
        dialog.setContentView(R.layout.success_data_post);

        TextView text = dialog.findViewById(R.id.success_dialog_text);
        text.setText(getResources().getString(R.string.data_updated));

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    public void setDialogSuccessDelete() {
        Dialog dialog = new Dialog(EditAssetActivity.this);
        dialog.setContentView(R.layout.success_data_post);

        TextView text = dialog.findViewById(R.id.success_dialog_text);
        text.setText(getResources().getString(R.string.data_deleted));

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
        dialog.setCanceledOnTouchOutside(true);

        dialog.show();
    }
}