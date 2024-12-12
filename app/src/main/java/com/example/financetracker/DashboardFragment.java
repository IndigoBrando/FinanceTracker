package com.example.financetracker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import androidx.recyclerview.widget.LinearLayoutManager;


public class DashboardFragment extends Fragment {

    private TextView tvWalletBalance;
    private TextView tvRemainingBalance;
    private PieChart pieChart;
    private Button btnAddExpense;
    private Button btnSetWalletBalance;
    private Button btnEditWalletBalance;
    private LinearLayout expensesLayout;

    private double totalSpentToday = 0.0;
    private double walletBalance = 0.0;
    private Map<String, Double> expenseCategories = new HashMap<>();

    private EditText etAmountSpent;
    private Spinner spinnerCategory;

    // SharedPreferences instance
    private SharedPreferences sharedPreferences;

    private static final String PREFS_NAME = "FinanceTrackerPrefs";
    private static final String KEY_WALLET_BALANCE = "wallet_balance";
    private static final String KEY_EXPENSES = "expenses"; // We will save expenses as a string and parse it

    // List to store transaction history
    private List<Transaction> transactionHistory = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the fragment layout
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Initialize SharedPreferences
        sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, getContext().MODE_PRIVATE);

        // Initialize views
        tvWalletBalance = rootView.findViewById(R.id.tvWalletBalance);
        tvRemainingBalance = rootView.findViewById(R.id.tvRemainingBalance);
        pieChart = rootView.findViewById(R.id.pieChart);
        btnAddExpense = rootView.findViewById(R.id.btnAddExpense);
        btnSetWalletBalance = rootView.findViewById(R.id.btnSetWalletBalance);
        btnEditWalletBalance = rootView.findViewById(R.id.btnEditWalletBalance);
        expensesLayout = rootView.findViewById(R.id.expensesLayout);
        etAmountSpent = rootView.findViewById(R.id.etAmountSpent);
        spinnerCategory = rootView.findViewById(R.id.spinnerCategory);

        // Load saved data
        loadData();

        // Set initial values
        updatePieChart();

        // Set Wallet Balance Button
        btnSetWalletBalance.setOnClickListener(v -> {
            String balanceText = etAmountSpent.getText().toString();
            if (!TextUtils.isEmpty(balanceText)) {
                walletBalance = Double.parseDouble(balanceText);
                tvWalletBalance.setText("Wallet Balance: $" + walletBalance);
                tvRemainingBalance.setText("Remaining Balance: $" + walletBalance);
                etAmountSpent.setText(""); // Clear input
                btnSetWalletBalance.setVisibility(View.GONE); // Hide Set button after setting balance
                btnEditWalletBalance.setVisibility(View.VISIBLE); // Show Edit button

                // Save wallet balance to SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putFloat(KEY_WALLET_BALANCE, (float) walletBalance);
                editor.apply();
            } else {
                Toast.makeText(getContext(), "Please enter a valid wallet balance", Toast.LENGTH_SHORT).show();
            }
        });

        // Edit Wallet Balance Button
        btnEditWalletBalance.setOnClickListener(v -> {
            // Allow editing the wallet balance
            btnSetWalletBalance.setVisibility(View.VISIBLE);
            btnEditWalletBalance.setVisibility(View.GONE);
            etAmountSpent.setText(String.valueOf(walletBalance)); // Show current wallet balance for editing
        });

        // Add Expense Button
        btnAddExpense.setOnClickListener(v -> {
            String amountText = etAmountSpent.getText().toString();
            String category = spinnerCategory.getSelectedItem().toString();

            if (TextUtils.isEmpty(amountText) || category.equals("Select Category")) {
                Toast.makeText(getContext(), "Please enter amount and select category.", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountText);

            // Check if the expense exceeds the wallet balance
            if (totalSpentToday + amount <= walletBalance) {
                totalSpentToday += amount;

                // Add the expense to the respective category
                expenseCategories.put(category, expenseCategories.getOrDefault(category, 0.0) + amount);

                // Add a new expense tab dynamically
                addExpenseTab(amount, category);

                // Deduct expense from wallet balance
                walletBalance -= amount;
                tvRemainingBalance.setText("Remaining Balance: $" + walletBalance);

                // Update the pie chart
                updatePieChart();

                // Save expenses to SharedPreferences
                saveExpenses();

                // Add the expense to transaction history
                addExpenseToHistory(amount, category);

                // Clear the input fields
                etAmountSpent.setText("");
            } else {
                Toast.makeText(getContext(), "Cannot add expense, exceeding wallet balance!", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    // Method to update the pie chart
    public void updatePieChart() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Double> entry : expenseCategories.entrySet()) {
            entries.add(new PieEntry(entry.getValue().floatValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Expenses");
        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.invalidate(); // Refresh the chart
    }

    // Method to add a new expense tab dynamically
    private void addExpenseTab(double amount, String category) {
        View expenseTab = LayoutInflater.from(getContext()).inflate(R.layout.item_expense, expensesLayout, false);
        TextView tvExpenseAmount = expenseTab.findViewById(R.id.tvExpenseAmount);
        TextView tvExpenseCategory = expenseTab.findViewById(R.id.tvExpenseCategory);

        tvExpenseAmount.setText("$" + amount);
        tvExpenseCategory.setText(category);

        expensesLayout.addView(expenseTab); // Add the new tab to the layout
    }

    public void addExpense(double amount, String description) {
        // Check if the expense exceeds the available wallet balance
        if (totalSpentToday + amount <= walletBalance) {
            totalSpentToday += amount;

            // Add the expense to the respective category (you can modify this as needed)
            expenseCategories.put(description, expenseCategories.getOrDefault(description, 0.0) + amount);

            // Update the pie chart
            updatePieChart();

            // Update the remaining wallet balance
            walletBalance -= amount;
            tvRemainingBalance.setText("Remaining Balance: $" + walletBalance);

            // Save the expense to SharedPreferences
            saveExpenses();

            // Add the expense to the transaction history
            addExpenseToHistory(amount, description);
        } else {
            Toast.makeText(getContext(), "Cannot add expense, exceeding wallet balance!", Toast.LENGTH_SHORT).show();
        }
    }


    // Method to save expenses data into SharedPreferences
    private void saveExpenses() {
        // Convert expense categories to a string (JSON or simple CSV)
        StringBuilder expenseString = new StringBuilder();
        for (Map.Entry<String, Double> entry : expenseCategories.entrySet()) {
            expenseString.append(entry.getKey()).append(":").append(entry.getValue()).append(";");
        }

        // Save the expense string to SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_EXPENSES, expenseString.toString());
        editor.apply();
    }

    // Method to load saved data from SharedPreferences
    private void loadData() {
        // Load wallet balance
        walletBalance = sharedPreferences.getFloat(KEY_WALLET_BALANCE, 0.0f);
        tvWalletBalance.setText("Wallet Balance: $" + walletBalance);
        tvRemainingBalance.setText("Remaining Balance: $" + walletBalance);

        // Load expenses
        String expensesString = sharedPreferences.getString(KEY_EXPENSES, "");
        if (!TextUtils.isEmpty(expensesString)) {
            String[] expensesArray = expensesString.split(";");
            for (String expense : expensesArray) {
                if (!TextUtils.isEmpty(expense)) {
                    String[] expenseDetails = expense.split(":");
                    String category = expenseDetails[0];
                    double amount = Double.parseDouble(expenseDetails[1]);
                    expenseCategories.put(category, amount);
                    totalSpentToday += amount;
                    addExpenseTab(amount, category);
                }
            }
        }
    }

    // Method to add expense to the transaction history
    // Method to add expense to the transaction history
    public void addExpenseToHistory(double amountSpent, String category) {
        // Create a new transaction object
        Transaction transaction = new Transaction(amountSpent, category, System.currentTimeMillis());

        // Add to the list (or save to database)
        transactionHistory.add(transaction);

        // Update the transaction history UI
        updateTransactionHistory();
    }

    // Method to update the wallet balance after an expense is added
    public void updateWalletBalance(double amount) {
        walletBalance -= amount; // Deduct the amount from the wallet balance
        tvRemainingBalance.setText("Remaining Balance: $" + walletBalance); // Update the TextView to show the new balance
    }

    // Method to update the transaction history UI (e.g., in a RecyclerView or ListView)
    public void updateTransactionHistory() {
        // Assuming you have a RecyclerView to show the transaction history
        RecyclerView recyclerView = getView().findViewById(R.id.recyclerViewTransactions);
        TransactionAdapter adapter = new TransactionAdapter(getContext(), transactionHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }


}
