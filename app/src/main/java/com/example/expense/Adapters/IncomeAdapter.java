package com.example.expense.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expense.Model.IncomeModel;
import com.example.expense.R;
import com.example.expense.Tools.Constraints;
import com.example.expense.Tools.DBHelper;
import com.example.expense.Tools.SharedPrefs;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.List;

public class IncomeAdapter extends RecyclerView.Adapter<IncomeAdapter.ViewHolder> {
    Context context;
    List<IncomeModel> listData;
    Boolean incomecheck=true;
    Boolean expensecheck=false;
    DBHelper dbHelper;
    SharedPrefs prefs;

    public IncomeAdapter(Context context, List<IncomeModel> listData) {
        this.context = context;
        this.listData = listData;
    }
    public interface OnDataChangedListener {
        void onDataChanged();
    }

    private OnDataChangedListener dataChangedListener;

    public void setOnDataChangedListener(OnDataChangedListener listener) {
        this.dataChangedListener = listener;
    }


    @NonNull
    @Override
    public IncomeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_income, parent, false);
        return new IncomeAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IncomeAdapter.ViewHolder holder, int position) {
        holder.incomeTitle.setText(listData.get(position).getTitle());
        holder.incomeDescription.setText(listData.get(position).getDescription());
        holder.incomeAmount.setText(listData.get(position).getTransammount());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupOptions(holder,holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
    private void popupOptions(IncomeAdapter.ViewHolder holder, int adapterPosition) {
        Dialog dialog;
        dialog=new Dialog(context);

        dialog.setContentView(R.layout.item_options);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL);

        ImageView edit=dialog.findViewById(R.id.imageView5);
        ImageView delete=dialog.findViewById(R.id.imageView17);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editItem(holder,adapterPosition);
                dialog.dismiss();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteItem(adapterPosition);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void deleteItem(int adapterPosition) {
        dbHelper=new DBHelper(context);
        dbHelper.deletefromTransaction(listData.get(adapterPosition).getTransID());
        listData.remove(adapterPosition);
        notifyDataSetChanged();
    }

    private void editItem(ViewHolder holder, int adapterPosition) {
        Dialog dialog = new Dialog(context);

        dialog.setContentView(R.layout.item_addtransaction);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        MaterialCardView income = dialog.findViewById(R.id.materialCardView3);
        MaterialCardView expense = dialog.findViewById(R.id.materialCardView2);
        FloatingActionButton addTrans = dialog.findViewById(R.id.editText6);

        EditText title = dialog.findViewById(R.id.editText);
        TextInputEditText description = dialog.findViewById(R.id.textInputEditText);
        EditText amount = dialog.findViewById(R.id.editText2);

        // Đặt giá trị ban đầu từ danh sách
        title.setText(listData.get(adapterPosition).getTitle());
        amount.setText(listData.get(adapterPosition).getTransammount());
        description.setText(listData.get(adapterPosition).getDescription());

        // Kiểm tra trạng thái hiện tại (Income hoặc Expense)
        String currentType = listData.get(adapterPosition).getType();
        if ("Income".equalsIgnoreCase(currentType)) {
            income.setStrokeColor(context.getResources().getColor(R.color.cardTeal));
            expense.setStrokeColor(context.getResources().getColor(R.color.white));
            incomecheck = true;
            expensecheck = false;
            expense.setEnabled(false); // Khóa nút Expense
        } else if ("Expense".equalsIgnoreCase(currentType)) {
            expense.setStrokeColor(context.getResources().getColor(R.color.cadRed));
            income.setStrokeColor(context.getResources().getColor(R.color.white));
            incomecheck = false;
            expensecheck = true;
            income.setEnabled(false); // Khóa nút Income
        }

        // Sự kiện khi nhấn vào Income
        income.setOnClickListener(view -> {
            if (!income.isEnabled()) return; // Không làm gì nếu nút bị khóa
            income.setStrokeColor(context.getResources().getColor(R.color.cardTeal));
            expense.setStrokeColor(context.getResources().getColor(R.color.white));
            incomecheck = true;
            expensecheck = false;
            expense.setEnabled(false); // Khóa nút Expense
            income.setEnabled(true); // Đảm bảo nút Income được kích hoạt
        });

        // Sự kiện khi nhấn vào Expense
        expense.setOnClickListener(view -> {
            if (!expense.isEnabled()) return; // Không làm gì nếu nút bị khóa
            expense.setStrokeColor(context.getResources().getColor(R.color.cadRed));
            income.setStrokeColor(context.getResources().getColor(R.color.white));
            incomecheck = false;
            expensecheck = true;
            income.setEnabled(false); // Khóa nút Income
            expense.setEnabled(true); // Đảm bảo nút Expense được kích hoạt
        });

        dbHelper = new DBHelper(context);
        prefs = new SharedPrefs(context);

        addTrans.setOnClickListener(view -> {
            if (checkValues(title, description, amount)) {
                try {
                    String type = incomecheck ? "Income" : "Expense";

                    dbHelper.editTransaction(
                            prefs.getStr(Constraints.userUID),
                            listData.get(adapterPosition).getTransID(),
                            listData.get(adapterPosition).getDate(),
                            listData.get(adapterPosition).getMonth(),
                            listData.get(adapterPosition).getYear(),
                            amount.getText().toString(),
                            title.getText().toString(),
                            description.getText().toString(),
                            "none",
                            type
                    );

                    // Cập nhật dữ liệu trong danh sách
                    listData.get(adapterPosition).setTransammount(amount.getText().toString());
                    listData.get(adapterPosition).setTitle(title.getText().toString());
                    listData.get(adapterPosition).setDescription(description.getText().toString());
                    listData.get(adapterPosition).setType(type);

                    // Cập nhật giao diện RecyclerView
                    notifyDataSetChanged();

                    // Gửi sự kiện thay đổi
                    if (dataChangedListener != null) {
                        dataChangedListener.onDataChanged();
                    }

                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        dialog.show();
    }

    private boolean checkValues(EditText title, TextInputEditText description, EditText amount) {
        if(title.getText().toString().isEmpty()){
            Toast.makeText(context, "Title cannot empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(description.getText().toString().isEmpty()){
            Toast.makeText(context, "Description cannot empty", Toast.LENGTH_SHORT).show();

            return  false;
        }
        else if(amount.getText().toString().isEmpty()){
            Toast.makeText(context, "Amount cannot empty", Toast.LENGTH_SHORT).show();

            return false;
        }
        return  true;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView incomeTitle,incomeDescription,incomeAmount;
        MaterialCardView layout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            incomeAmount=itemView.findViewById(R.id.txt_transactionAmount);
            incomeTitle=itemView.findViewById(R.id.txt_transactionTitle);
            incomeDescription=itemView.findViewById(R.id.txt_transactionDescription);
            layout=itemView.findViewById(R.id.item_notes_layout);

        }

    }
}