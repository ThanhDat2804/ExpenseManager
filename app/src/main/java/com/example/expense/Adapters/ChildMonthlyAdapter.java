package com.example.expense.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expense.Model.MonthlyChildModel;
import com.example.expense.R;

import java.util.List;

public class ChildMonthlyAdapter extends RecyclerView.Adapter<ChildMonthlyAdapter.ViewHolder> {
    List<MonthlyChildModel> modelList;

    public interface OnDataChangedListener {
        void onDataChanged();
    }

    private OnDataChangedListener dataChangedListener;

    public void setOnDataChangedListener(OnDataChangedListener listener) {
        this.dataChangedListener = listener;
    }

    public ChildMonthlyAdapter(List<MonthlyChildModel> modelList) {
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public ChildMonthlyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_childmonthly, parent, false);
        return new ChildMonthlyAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildMonthlyAdapter.ViewHolder holder, int position) {
        if (modelList.get(position).getIncomeTitle().equalsIgnoreCase("")) {
            holder.expenseamount.setText(modelList.get(position).getExpenseAmount());
            holder.expensetitle.setText(modelList.get(position).getExpenseTitle());
        } else {
            holder.incomeamount.setText(modelList.get(position).getIncomeAmount());
            holder.incometitle.setText(modelList.get(position).getIncomeTitle());
        }

        // Example logic to notify data changes (if applicable)
        holder.itemView.setOnClickListener(v -> {
            if (dataChangedListener != null) {
                dataChangedListener.onDataChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView incometitle, expensetitle, incomeamount, expenseamount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            incometitle = itemView.findViewById(R.id.txt_incometitle);
            expensetitle = itemView.findViewById(R.id.txt_expensetitle);
            incomeamount = itemView.findViewById(R.id.txt_incomeamount);
            expenseamount = itemView.findViewById(R.id.txt_expenseamount);
        }
    }
}
