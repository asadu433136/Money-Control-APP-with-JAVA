package com.example.pluralcode;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<Transaction>transaction;

    //For click
    ClickListener mClickListener;



    public MyAdapter(List<Transaction> transaction, ClickListener clickListener) {

        this.transaction = transaction;
        this.mClickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_list_item,parent,false);


        return new ViewHolder(view,mClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction trans=transaction.get(position);

        String type = trans.getType().trim();
        String category = trans.getCategory().trim();
        String currency = trans.getCurrency().trim();

        if(currency.equals("Euro")){
            holder.mAmount.setText("€" + trans.getAmount() +"");
        }else{
            holder.mAmount.setText("$" + trans.getAmount() +"");
        }


        if (type.equals("Income")){
            holder.mAmount.setTextColor(Color.parseColor("#008000"));  //set to green
            holder.mIcon.setBackgroundResource(R.drawable.ic_income);
        }else{
            if(category.equals("Food")){ holder.mIcon.setBackgroundResource(R.drawable.ic_food); }
            if(category.equals("Groceries")){ holder.mIcon.setBackgroundResource(R.drawable.ic_groceries); }
            if(category.equals("Entertainment")){ holder.mIcon.setBackgroundResource(R.drawable.ic_entertainment); }
            if(category.equals("Household")){ holder.mIcon.setBackgroundResource(R.drawable.ic_household); }
            if(category.equals("Education")){ holder.mIcon.setBackgroundResource(R.drawable.ic_education); }
            if(category.equals("Health")){ holder.mIcon.setBackgroundResource(R.drawable.ic_health); }
            if(category.equals("Gift")){ holder.mIcon.setBackgroundResource(R.drawable.ic_gift); }
            if(category.equals("Other")){ holder.mIcon.setBackgroundResource(R.drawable.ic_other); }
        }

        holder.mCategory.setText(trans.getCategory()+"");
        holder.mDescription.setText(trans.getDescription()+"");
        holder.mPaymentType.setText(trans.getPayment_type()+"");
        holder.mDate.setText(trans.getDate()+"");
        holder.mTime.setText(trans.getTime()+"");
        holder.mRepeat.setText("Repeat : "+ trans.getRepeat()+"");


    }

    @Override
    public int getItemCount() {
        return transaction.size();
    }


    ////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////      /////////////////////////////////////////////
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mAmount,mCategory,mDescription,mPaymentType,mDate,mTime,mIcon,mRepeat;
        ClickListener clickListener;

        public ViewHolder(View itemView, ClickListener clickListener) {
            super(itemView);
            mAmount =(TextView) itemView.findViewById(R.id.amount_txtView);
            mCategory =(TextView) itemView.findViewById(R.id.category_txtView);
            mDescription =(TextView) itemView.findViewById(R.id.description_txtView);
            mPaymentType =(TextView) itemView.findViewById(R.id.paymentType_txtView);
            mDate =(TextView) itemView.findViewById(R.id.date_txtView);
            mTime =(TextView) itemView.findViewById(R.id.time_txtView);
            mIcon = (Button) itemView.findViewById(R.id.icon_view);
            mRepeat = (TextView) itemView.findViewById(R.id.repeat_txtView);

            this.clickListener=clickListener;
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition());
        }
    }

    public interface ClickListener {
        void onItemClick(int position);
        void onItemLongClick(int position, View v);
    }
}