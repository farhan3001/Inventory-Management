package com.example.qtitest.activities.ui.assets;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qtitest.R;
import com.example.qtitest.data.AssetItems;
import com.example.qtitest.data.AssetResponse;

import java.util.ArrayList;

public class AssetAdapter extends RecyclerView.Adapter<AssetAdapter.ViewHolder> {
    private ArrayList<AssetItems.Result> dataArrayList;

    public AssetAdapter(ArrayList<AssetItems.Result> dataArrayList) {
        this.dataArrayList = dataArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_view, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

//        Log.d("Asset Name", dataArrayList.get(position).getName());

//        Log.d("Asset Items", assetItems.get(1).getName());

        holder.textView.setText(dataArrayList.get(position).getName());

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                view.getContext().startActivity(new Intent(view.getContext(), EditAssetActivity.class)
                        .putExtra("dataAsset", dataArrayList.get(position)));


//                ((Activity)view.getContext()).finish();
            }
        });
    }

    @Override
    public int getItemCount() {

        return (dataArrayList != null) ? dataArrayList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        String name;

        TextView textView;
        AppCompatButton button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.asset_item_name);
            button = itemView.findViewById(R.id.edit_asset_button);

        }
    }

}
