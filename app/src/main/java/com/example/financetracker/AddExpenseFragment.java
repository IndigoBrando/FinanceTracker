package com.example.financetracker;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class AddExpenseFragment extends Fragment {

    private EditText etAmountSpent;
    private EditText etDescription; // To input item/description of the expense
    private Button btnAddExpense;
    private DashboardFragment dashboardFragment; // Reference to DashboardFragment to update data

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_add_expense, container, false);

        // Initialize views
        etAmountSpent = rootView.findViewById(R.id.etAmountSpent);
        etDescription = rootView.findViewById(R.id.etDescription); // Input field for item/description
        btnAddExpense = rootView.findViewById(R.id.btnAddExpense);

        // Get reference to DashboardFragment
        dashboardFragment = (DashboardFragment) getActivity().getSupportFragmentManager().findFragmentByTag("Dashboard");

        // Add expense functionality
        btnAddExpense.setOnClickListener(v -> {
            String amountText = etAmountSpent.getText().toString();
            String description = etDescription.getText().toString();
            if (TextUtils.isEmpty(amountText) || TextUtils.isEmpty(description)) {
                Toast.makeText(getContext(), "Please enter both amount and description.", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountText);

            // Check if expense exceeds available balance
            if (dashboardFragment != null) {
                dashboardFragment.addExpense(amount, description); // Add expense to the dashboard
            }

            // Clear input fields
            etAmountSpent.setText("");
            etDescription.setText("");
        });

        return rootView;
    }
}
