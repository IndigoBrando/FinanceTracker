package com.example.financetracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private RecyclerView recyclerViewHistory;
    private TransactionAdapter transactionAdapter;
    private List<Transaction> transactionList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        // Initialize RecyclerView and data list
        recyclerViewHistory = view.findViewById(R.id.recyclerViewHistory);
        transactionList = new ArrayList<>();

        // Set up RecyclerView with LayoutManager and Adapter
        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        transactionAdapter = new TransactionAdapter(getContext(), transactionList);
        recyclerViewHistory.setAdapter(transactionAdapter);

        // Add some sample data
        addSampleData();

        return view;
    }

    // Sample data for testing
    private void addSampleData() {
        transactionList.add(new Transaction(50.0, "Food", System.currentTimeMillis()));
        transactionList.add(new Transaction(20.0, "Transport", System.currentTimeMillis()));
        transactionAdapter.notifyDataSetChanged();
    }
}
