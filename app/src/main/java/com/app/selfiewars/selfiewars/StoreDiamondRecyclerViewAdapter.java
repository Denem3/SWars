package com.app.selfiewars.selfiewars;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class StoreDiamondRecyclerViewAdapter extends RecyclerView.Adapter<StoreDiamondRecyclerViewAdapter.MyVievHolder>{
    private Context mcontext;
    private List<StoreDiamond> mData;


    public StoreDiamondRecyclerViewAdapter(Context mcontext, List<StoreDiamond> mData) {
        this.mcontext = mcontext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyVievHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        LayoutInflater mInflater= LayoutInflater.from(mcontext);
        view = mInflater.inflate(R.layout.store_diamond_cardview,viewGroup,false);

        return new MyVievHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyVievHolder myVievHolder, final int i) {
        myVievHolder.diamondValueNumber.setText(mData.get(i).getDiamondValueNumber());
        myVievHolder.diamondPrice.setText(mData.get(i).getDiamondPrice());
        myVievHolder.diamondImage.setImageResource(mData.get(i).getDiamondImage());
        myVievHolder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mcontext,InAppBillingActivity.class);
                i.putExtra("skuid",""+myVievHolder.diamondValueNumber.getText().toString());
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                mcontext.startActivity(i);
                //buyProduct(mData.get(i).getDiamondValueNumber()+"_diamond");
                //Toast.makeText(mcontext,"Fiyat :" + mcontext.getResources().getString(R.string.currency_type)+myVievHolder.diamondPrice.getText(),Toast.LENGTH_LONG).show();
            }
        });
    }
    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyVievHolder extends RecyclerView.ViewHolder{
        TextView diamondValueNumber;
        TextView diamondPrice;
        ImageView diamondImage;
        ConstraintLayout constraintLayout;

    public MyVievHolder(@NonNull View itemView) {
        super(itemView);
        diamondValueNumber = itemView.findViewById(R.id.store_diamond_value_number);
        constraintLayout = itemView.findViewById(R.id.constraintLayout);
        diamondPrice = itemView.findViewById(R.id.store_joker_price);
        diamondImage = itemView.findViewById(R.id.store_joker_imageview);
    }
}

}
