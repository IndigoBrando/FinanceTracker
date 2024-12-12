package com.example.financetracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private Context context;
    private List<Transaction> transactionList;

    // Constructor
    public TransactionAdapter(Context context, List<Transaction> transactionList) {
        this.context = context;
        this.transactionList = transactionList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the item layout
        View view = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);

        // Bind data to views
        holder.tvAmount.setText("$" + String.format(Locale.getDefault(), "%.2f", transaction.getAmount()));
        holder.tvCategory.setText(transaction.getCategory());
        holder.tvTimestamp.setText(formatDate(transaction.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    // ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAmount, tvCategory, tvTimestamp;

        public ViewHolder(View itemView) {
            super(itemView);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvTimestamp = itemView.findViewById(R.id.tvTransactionTimestamp);
        }
    }

    // Helper method to format the timestamp to a readable date
    private String formatDate(long timestamp) {
        if (timestamp <= 0) {
            return "Invalid Date"; // Handle invalid timestamps
        }
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        return sdf.format(timestamp);
    }
}
