package com.example.expense.Fragment;

import android.app.Dialog;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expense.Adapters.IncomeAdapter;
import com.example.expense.Model.IncomeModel;
import com.example.expense.R;
import com.example.expense.Tools.Constraints;
import com.example.expense.Tools.DBHelper;
import com.example.expense.Tools.SharedPrefs;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class DailyFragment extends Fragment {
    ImageView back;
    ImageView forward;
    TextView icomeSum, expenseSum;
    TextView monthyear;
    TextView notxtIncome;
    TextView notxtExpense;
    TextView balance;
    TextView cf;

    IncomeAdapter adapter;
    List<IncomeModel> incomeList = new ArrayList<>();
    List<IncomeModel> expenseList = new ArrayList<>();
    TextView date;
    TextView day;
    FloatingActionButton addNote;
    Boolean incomecheck = true;
    Boolean expensecheck = false;
    DBHelper dbHelper;
    SharedPrefs prefs;
    RecyclerView incomeRecyclerView;
    RecyclerView expenseRecyclerView;
    MaterialCardView strokeCard;

    int numberMonth, numberYear, numberDate;
    int currentDate, currentMonth, currentYear;
    Dialog dialog;

    public DailyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_daily, container, false);

        icomeSum = view.findViewById(R.id.incomeSum);
        expenseSum = view.findViewById(R.id.expenseSum);
        back = view.findViewById(R.id.imageView6);
        dialog = new Dialog(getContext());
        forward = view.findViewById(R.id.imageView7);
        date = view.findViewById(R.id.date);
        monthyear = view.findViewById(R.id.textView7);
        notxtIncome = view.findViewById(R.id.tapAddIncome);
        notxtExpense = view.findViewById(R.id.tapaddExpense);
        day = view.findViewById(R.id.textView8);
        final Calendar c = Calendar.getInstance();
        numberMonth = c.get(Calendar.MONTH);
        numberDate = c.get(Calendar.DATE);
        numberYear = c.get(Calendar.YEAR);
        currentDate = numberDate;
        currentMonth = numberMonth;
        currentYear = numberYear;
        addNote = view.findViewById(R.id.addTrans);
        incomeRecyclerView = view.findViewById(R.id.incomeRecyclerView);
        expenseRecyclerView = view.findViewById(R.id.debitRecyclerView);
        dbHelper = new DBHelper(getContext());
        strokeCard = view.findViewById(R.id.materialCardView);
        balance = view.findViewById(R.id.textView10);
        cf = view.findViewById(R.id.txt_cf);

        setListeners();
        setDate(numberYear, numberMonth, numberDate);

        if (prefs.getStr(Constraints.cashBalance).equalsIgnoreCase("none")) {
            cf.setText("0");
        } else {
            cf.setText(prefs.getStr(Constraints.cashBalance));
        }

        return view;
    }

    private void getIncomeData(int numberYear, int numberMonth, int numberDate) {
        String date = numberDate + " " + numberMonth + " " + numberYear;
        float sum = 0;
        incomeList.clear();
        Cursor cursor = dbHelper.getIncomeTransaction(date, "Income");
        while (cursor.moveToNext()) {
            IncomeModel model = new IncomeModel(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9));
            incomeList.add(model);
            sum += Float.parseFloat(cursor.getString(5));
        }
        cursor.close();
        icomeSum.setText("" + sum);
        incomeRecyclerView.setHasFixedSize(true);
        incomeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new IncomeAdapter(getContext(), incomeList);

        // Set listener for data changes
        adapter.setOnDataChangedListener(new IncomeAdapter.OnDataChangedListener() {
            @Override
            public void onDataChanged() {
                getIncomeData(numberYear, numberMonth, numberDate);
                updateBalance();
            }
        });

        incomeRecyclerView.setAdapter(adapter);
        if (incomeList.isEmpty()) {
            notxtIncome.setVisibility(View.VISIBLE);
            incomeRecyclerView.setVisibility(View.GONE);
        } else {
            notxtIncome.setVisibility(View.GONE);
            incomeRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void getExpenseData(int numberYear, int numberMonth, int numberDate) {
        String date = numberDate + " " + numberMonth + " " + numberYear;
        expenseList.clear();
        Cursor cursor = dbHelper.getIncomeTransaction(date, "Expense");
        float sum = 0;
        while (cursor.moveToNext()) {
            IncomeModel model = new IncomeModel(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9));
            expenseList.add(model);
            sum += Float.parseFloat(cursor.getString(5));
        }
        cursor.close();
        expenseSum.setText("" + sum);
        expenseRecyclerView.setHasFixedSize(true);
        expenseRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new IncomeAdapter(getContext(), expenseList);
        expenseRecyclerView.setAdapter(adapter);
        if (expenseList.isEmpty()) {
            notxtExpense.setVisibility(View.VISIBLE);
            expenseRecyclerView.setVisibility(View.GONE);
        } else {
            notxtExpense.setVisibility(View.GONE);
            expenseRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void setDate(int year, int month, int numberdate) {
        final String[] Selected = new String[]{"January", "February", "March", "April",
                "May", "June", "July", "August", "September", "October", "November", "December"};

        date.setText("" + numberdate);
        monthyear.setText(Selected[month] + " " + year);

        Calendar selectedDate = Calendar.getInstance();
        Calendar current = Calendar.getInstance();

        selectedDate.set(year, month, numberdate);
        current.set(currentYear, currentMonth, currentDate);
        if (current.equals(selectedDate)) {
            strokeCard.setStrokeColor(getResources().getColor(R.color.white));
        } else {
            strokeCard.setStrokeColor(getResources().getColor(R.color.cardTeal));
        }
        switch (selectedDate.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.SATURDAY:
                day.setText("Saturday");
                break;
            case Calendar.MONDAY:
                day.setText("Monday");
                break;
            case Calendar.TUESDAY:
                day.setText("Tuesday");
                break;
            case Calendar.WEDNESDAY:
                day.setText("Wednesday");
                break;
            case Calendar.THURSDAY:
                day.setText("Thursday");
                break;
            case Calendar.FRIDAY:
                day.setText("Friday");
                break;
            case Calendar.SUNDAY:
                day.setText("Sunday");
                break;
        }
        getIncomeData(year, month, numberdate);
        getExpenseData(year, month, numberdate);
        updateBalance();
    }

    private void updateBalance() {
        float incomeSum = Float.parseFloat(icomeSum.getText().toString());
        float expensesurf = Float.parseFloat(expenseSum.getText().toString());
        Float diff = incomeSum - expensesurf;
        balance.setText("" + diff);
    }

    private void setListeners() {
        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (numberDate == 1) {
                    if (numberMonth == 0) {
                        numberMonth = 11;
                        numberYear--;
                    } else {
                        int previousMonthDays = getMonthDays(--numberMonth);
                        numberDate = previousMonthDays;
                    }
                } else {
                    numberDate--;
                }
                setDate(numberYear, numberMonth, numberDate);
            }
        });
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentMonthDays = getMonthDays(numberMonth);
                if (numberDate == currentMonthDays) {
                    if (numberMonth == 11) {
                        numberMonth = 0;
                        numberYear++;
                    } else {
                        numberMonth++;
                    }
                    numberDate = 1;
                } else {
                    numberDate++;
                }
                setDate(numberYear, numberMonth, numberDate);
            }
        });
    }

    private void openDialog() {
        final String[] Selected = new String[]{"January", "February", "March", "April",
                "May", "June", "July", "August", "September", "October", "November", "December"};

        final Calendar c = Calendar.getInstance();

        dialog.setContentView(R.layout.item_addtransaction);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        MaterialCardView income = dialog.findViewById(R.id.materialCardView3);
        MaterialCardView expense = dialog.findViewById(R.id.materialCardView2);
        income.setStrokeColor(getResources().getColor(R.color.cardTeal));
        FloatingActionButton addTrans = dialog.findViewById(R.id.editText6);

        EditText title = dialog.findViewById(R.id.editText);
        TextInputEditText description = dialog.findViewById(R.id.textInputEditText);
        EditText amount = dialog.findViewById(R.id.editText2);

        income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                income.setStrokeColor(getResources().getColor(R.color.cardTeal));
                expense.setStrokeColor(getResources().getColor(R.color.white));
                incomecheck = true;
                expensecheck = false;
            }
        });
        expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expense.setStrokeColor(getResources().getColor(R.color.cadRed));
                income.setStrokeColor(getResources().getColor(R.color.white));
                incomecheck = false;
                expensecheck = true;
            }
        });
        dbHelper = new DBHelper(getContext());
        prefs = new SharedPrefs(getContext());

        addTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkValues(title, description, amount)) {
                    try {
                        String date = numberDate + " " + numberMonth + " " + numberYear;
                        if (incomecheck) {
                            if (dbHelper.insertTransaction(prefs.getStr(Constraints.userUID), createTransactionID(), date, String.valueOf(numberMonth), String.valueOf(numberYear), amount.getText().toString(), title.getText().toString(), description.getText().toString(), "none", "Income")) {
                                getIncomeData(numberYear, numberMonth, numberDate);
                                updateBalance();
                                dialog.dismiss();
                            }
                        } else {
                            dbHelper.insertTransaction(prefs.getStr(Constraints.userUID), createTransactionID(), date, String.valueOf(numberMonth), String.valueOf(numberYear), amount.getText().toString(), title.getText().toString(), description.getText().toString(), "none", "Expense");
                            getExpenseData(numberYear, numberMonth, numberDate);
                            updateBalance();
                            dialog.dismiss();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        dialog.show();
    }

    private boolean checkValues(EditText title, TextInputEditText description, EditText amount) {
        if (title.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Title cannot empty", Toast.LENGTH_SHORT).show();
            return false;
        } else if (description.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Description cannot empty", Toast.LENGTH_SHORT).show();
            return false;
        } else if (amount.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Amount cannot empty", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    int getMonthDays(int monthnumber) {
        switch (monthnumber) {
            case Calendar.JANUARY:
            case Calendar.MARCH:
            case Calendar.MAY:
            case Calendar.JULY:
            case Calendar.AUGUST:
            case Calendar.OCTOBER:
            case Calendar.DECEMBER: {
                return 31;
            }
            case Calendar.FEBRUARY: {
                return 28;
            }
            case Calendar.APRIL:
            case Calendar.JUNE:
            case Calendar.SEPTEMBER:
            case Calendar.NOVEMBER: {
                return 30;
            }
        }
        return 0;
    }

    public String createTransactionID() throws Exception {
        return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }
}
