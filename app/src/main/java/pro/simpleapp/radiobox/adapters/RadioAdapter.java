package pro.simpleapp.radiobox.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.rishabhharit.roundedimageview.RoundedImageView;

import java.util.List;

import pro.simpleapp.radiobox.R;
import pro.simpleapp.radiobox.helpers.CategoryAdapter;
import pro.simpleapp.radiobox.items.Radio;
import pro.simpleapp.radiobox.items.User;

public class RadioAdapter extends RecyclerView.Adapter<RadioAdapter.VH> {

    List<Radio> rList;
    private Context context;
    View view;
    Boolean isAdd;
    private static CategoryAdapter.OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onItemDeleteClicked(int position);
    }

    public RadioAdapter(List<Radio> rList, Activity activity, Boolean isAdd) {
        this.rList = rList;
        this.context = activity;
        this.isAdd = isAdd;

        setHasStableIds(false);
    }

    @Override
    public RadioAdapter.VH onCreateViewHolder(ViewGroup parent, int viewType) {

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.radio_item, parent, false);

        return new RadioAdapter.VH(view);
    }

    @Override
    public int getItemCount() {
        return rList.size();
    }

    @Override
    public void onBindViewHolder(RadioAdapter.VH holder, int position) {

        holder.radio_name.setText(rList.get(position).getName());

        User us = rList.get(position).getRUser();

        try {

            String name = us.fetchIfNeeded().getString("nickname");
            holder.author.setText("author: " + name);

        } catch (com.parse.ParseException e) {

        }

        String imgUrl = rList.get(position).getLogo().getUrl();

        Glide
                .with(context)
                .load(imgUrl)
                .centerCrop()
                .placeholder(R.drawable.logo)
                .into(holder.cover);

        int rating = rList.get(position).getRating();
        int voiting = rList.get(position).getVoiting();

        if (voiting == 0) {
            holder.simpleRatingBar.setRating(0);
        } else {
            holder.simpleRatingBar.setRating(rating / voiting);

        }

    }

    public static class VH extends RecyclerView.ViewHolder {

        SimpleRatingBar simpleRatingBar;
        RoundedImageView cover;
        TextView author;
        TextView radio_name;

        public VH(View itemView) {
            super(itemView);

            simpleRatingBar = itemView.findViewById(R.id.rbar);
            cover = itemView.findViewById(R.id.image_cover);
            author = itemView.findViewById(R.id.text_by);
            radio_name = itemView.findViewById(R.id.radio_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mListener != null) mListener.onItemClick(getAdapterPosition());

                    if (getAdapterPosition() == RecyclerView.NO_POSITION) return;

                }
            });

        }
    }

    public void setOnItemClickListener(CategoryAdapter.OnItemClickListener listener) {
        this.mListener = listener;
    }

}
