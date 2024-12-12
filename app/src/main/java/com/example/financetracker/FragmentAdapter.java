package com.example.financetracker;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class FragmentAdapter extends FragmentStateAdapter {

    public FragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return the fragment based on the selected position
        switch (position) {
            case 0:
                return new DashboardFragment();
            case 1:
                return new ExpenseFragment(); // Create a BudgetFragment
            case 2:
                return new HistoryFragment(); // Create a HistoryFragment
            default:
                return new DashboardFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3; // The number of tabs
    }
}
