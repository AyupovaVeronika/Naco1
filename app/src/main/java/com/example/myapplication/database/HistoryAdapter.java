package com.example.myapplication.database;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<FileHistory> historyList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(FileHistory fileHistory);
    }

    public HistoryAdapter(List<FileHistory> historyList, OnItemClickListener listener) {
        this.historyList = historyList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FileHistory item = historyList.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView fileNameText;
        TextView dateText;
        ImageView thumbnailImage;

        ViewHolder(View itemView) {
            super(itemView);
            fileNameText = itemView.findViewById(R.id.file_name);
            dateText = itemView.findViewById(R.id.file_date);
            thumbnailImage = itemView.findViewById(R.id.thumbnail);
        }

        void bind(final FileHistory item, final OnItemClickListener listener) {
            fileNameText.setText(item.getFileName());

            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
            dateText.setText(sdf.format(new Date(item.getLastOpened())));

            // Здесь можно установить миниатюру, если она есть
            // if (item.getThumbnailPath() != null) {
            //     thumbnailImage.setImageBitmap(...);
            // }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}