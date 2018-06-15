package com.rgk.android.translator;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import com.rgk.android.translator.database.DbConstants;
import com.rgk.android.translator.database.beans.MessageBean;
import com.rgk.android.translator.utils.Utils;

public class ComposeListAdapter extends RecyclerView.Adapter<ComposeListAdapter.ComposeListViewHolder> {

    private static final int VIEW_TYPE_SEND = 1;
    private static final int VIEW_TYPE_RECEIVE = 2;

    private Context mContext;
    private List<MessageBean> mDatas;

    interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public ComposeListAdapter(Context context, List<MessageBean> data) {
        mContext = context;
        mDatas = data;
    }


    public void addItem(MessageBean item) {
        mDatas.add(item);
        notifyItemInserted(mDatas.size() - 1);
    }

    public void updateItemText(int index, String text) {
        mDatas.get(index).setText(text);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ComposeListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case VIEW_TYPE_RECEIVE: {
                view = LayoutInflater.from(mContext).inflate(R.layout.item_recorder_receive, parent, false);
                break;
            }

            case VIEW_TYPE_SEND: {
                view = LayoutInflater.from(mContext).inflate(R.layout.item_recorder_send, parent, false);
                break;
            }

            default: {
                view = LayoutInflater.from(mContext).inflate(R.layout.item_recorder_send, parent, false);
                break;
            }
        }

        ComposeListViewHolder holder = new ComposeListViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ComposeListViewHolder holder, final int position) {
        holder.time.setText(Utils.getTimeString("HH:mm", mDatas.get(position).getDate()));
        if (mDatas.get(position).isSend()) {
            holder.txt.setText(mDatas.get(position).getText());
        } else {
            if (mDatas.get(position).getType() == DbConstants.MessageType.TYPE_TEXT) {
                holder.txt.setText(mDatas.get(position).getText());
            } else {
                SpannableString spannableString = new SpannableString(mDatas.get(position).getText() + " ");
                ImageSpan imageSpan = new ImageSpan(mContext, R.mipmap.ic_play_recordor_wave_receive_v3);
                int l = mDatas.get(position).getText().length();
                spannableString.setSpan(imageSpan, l, l + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.txt.setText(spannableString);
            }
        }
        holder.txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mDatas.get(position).isSend() ? VIEW_TYPE_SEND : VIEW_TYPE_RECEIVE;
    }

    class ComposeListViewHolder extends RecyclerView.ViewHolder {
        TextView time;
        TextView txt;

        public ComposeListViewHolder(View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.id_msg_time);
            txt = itemView.findViewById(R.id.id_msg_txt);
        }
    }
}
