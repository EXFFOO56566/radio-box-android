package pro.simpleapp.radiobox.helpers;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pro.simpleapp.radiobox.R;
import pro.simpleapp.radiobox.items.Category;



public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.VH> {

    List<Category> catList;
    private Context context;
    View view;
    Boolean isAdd;
    private static OnItemClickListener mListener;


    public interface OnItemClickListener {

        void onItemClick(int position);
        void onItemDeleteClicked(int position);


    }


    public CategoryAdapter(List<Category> catList, Activity activity, Boolean isAdd) {
        this.catList = catList;
        this.context = activity;
        this.isAdd = isAdd;
        setHasStableIds(false);
    }


    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_dialog, parent, false);
        return new VH(view);
    }


    @Override
    public int getItemCount() {
        return catList.size();
    }


    @Override
    public void onBindViewHolder(VH holder, int position) {
        holder.nameCategoryn.setText(catList.get(position).getCategory());
    }


    public static class VH extends RecyclerView.ViewHolder {

        ImageView coverCategory;
        TextView nameCategoryn;


        public VH(View itemView) {
            super(itemView);
            nameCategoryn = (TextView) itemView.findViewById(R.id.c_name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) mListener.onItemClick(getAdapterPosition());
                    if (getAdapterPosition() == RecyclerView.NO_POSITION) return;

                }
            });

        }


    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }


}
