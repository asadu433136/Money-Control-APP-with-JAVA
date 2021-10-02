package com.example.pluralcode;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class budgetAdapter extends RecyclerView.Adapter<budgetAdapter.ViewHolder>{
    private List<Budget> budgets;

    //For click
    budgetAdapter.ClickListener mClickListener;



    public budgetAdapter(List<Budget> budgets, budgetAdapter.ClickListener clickListener) {

        this.budgets = budgets;
        this.mClickListener = clickListener;
    }

    @NonNull
    @Override
    public budgetAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.budget_list_item,parent,false);


        return new budgetAdapter.ViewHolder(view,mClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Budget budget=budgets.get(position);

        String amount = budget.getAmount()+"";
        String progress = budget.getProgress()+"";
        String category = budget.getCategory()+"".trim();

        String available = String.valueOf(Double.parseDouble(amount)  -  Double.parseDouble(progress));


        holder.mAmount.setText("-" + budget.getAmount() +"");

        if(category.equals("Food")){ holder.mIcon.setBackgroundResource(R.drawable.ic_food); }
        if(category.equals("Groceries")){ holder.mIcon.setBackgroundResource(R.drawable.ic_groceries); }
        if(category.equals("Entertainment")){ holder.mIcon.setBackgroundResource(R.drawable.ic_entertainment); }
        if(category.equals("Household")){ holder.mIcon.setBackgroundResource(R.drawable.ic_household); }
        if(category.equals("Education")){ holder.mIcon.setBackgroundResource(R.drawable.ic_education); }
        if(category.equals("Health")){ holder.mIcon.setBackgroundResource(R.drawable.ic_health); }
        if(category.equals("Gift")){ holder.mIcon.setBackgroundResource(R.drawable.ic_gift); }
        if(category.equals("Other")){ holder.mIcon.setBackgroundResource(R.drawable.ic_other); }


        holder.mCategory.setText(budget.getCategory()+"");

        if(Double.parseDouble(available) < 0.00){
            LayerDrawable layerDrawable = (LayerDrawable) holder.mProgressBar.getProgressDrawable();
            Drawable progressDrawable = layerDrawable.findDrawableByLayerId(android.R.id.progress);
            progressDrawable.setColorFilter(0xFFFF0000, PorterDuff.Mode.SRC_IN);
        }
        holder.mProgressBar.setMax(Integer.parseInt(amount));
        holder.mProgressBar.setProgress(Integer.parseInt(progress));
        holder.mAmount.setText(available + "/" + amount);



    }


    @Override
    public int getItemCount() {
        return budgets.size();
    }


    ////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////      /////////////////////////////////////////////
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView mAmount,mCategory,mIcon;
        private ProgressBar mProgressBar;
        budgetAdapter.ClickListener clickListener;

        public ViewHolder(View itemView, budgetAdapter.ClickListener clickListener) {
            super(itemView);
            mAmount =(TextView) itemView.findViewById(R.id.available_figure);
            mCategory =(TextView) itemView.findViewById(R.id.category_budget);
            mIcon = (TextView) itemView.findViewById(R.id.budget_icon_view);
            mProgressBar = (ProgressBar) itemView.findViewById(R.id.budget_progressBar);


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
