package com.example.financetracker;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class ExpenseFragment extends Fragment {

    private EditText etAmountSpent;
    private Spinner spinnerCategory; // Spinner to select expense category
    private Button btnAddExpense;
    private DashboardFragment dashboardFragment; // Reference to DashboardFragment to update data

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_expense, container, false);

        // Initialize views
        etAmountSpent = rootView.findViewById(R.id.etAmountSpent);
        spinnerCategory = rootView.findViewById(R.id.spinnerCategory); // Category selection
        btnAddExpense = rootView.findViewById(R.id.btnAddExpense);

        // Set up categories for the spinner
        String[] categories = {"Select Category", "Food", "Transport", "Entertainment", "Bills", "Other"};

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinnerCategory.setAdapter(adapter);

        // Get reference to DashboardFragment
        dashboardFragment = (DashboardFragment) getActivity().getSupportFragmentManager().findFragmentByTag("Dashboard");

        // Add expense functionality
        btnAddExpense.setOnClickListener(v -> {
            String amountText = etAmountSpent.getText().toString();
            String category = spinnerCategory.getSelectedItem().toString(); // Get selected category

            if (TextUtils.isEmpty(amountText) || category.equals("Select Category")) {
                Toast.makeText(getContext(), "Please enter amount and select category.", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountText);

            // Add expense to the dashboard and update wallet balance
            if (dashboardFragment != null) {
                dashboardFragment.addExpense(amount, category); // Update expense data
                dashboardFragment.updateWalletBalance(amount); // Deduct amount from wallet balance

                // Update the pie chart after adding the expense
                dashboardFragment.updatePieChart(); // Refresh the pie chart
            }

            // Clear input fields
            etAmountSpent.setText("");
            spinnerCategory.setSelection(0);  // Reset spinner to default (Select Category)
        });



        return rootView;
    }
}
