package pro.simpleapp.radiobox.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pro.simpleapp.radiobox.R;
import pro.simpleapp.radiobox.items.Category;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.VH> {

    List<Category> cList;
    private Context context;
    View view;
    Boolean isAdd;
    private static pro.simpleapp.radiobox.helpers.CategoryAdapter.OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onItemDeleteClicked(int position);
    }

    public CategoryAdapter(List<Category> cList, Activity activity, Boolean isAdd) {
        this.cList = cList;
        this.context = activity;
        this.isAdd = isAdd;

        setHasStableIds(false);
    }

    @Override
    public CategoryAdapter.VH onCreateViewHolder(ViewGroup parent, int viewType) {

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cat_item, parent, false);

        return new CategoryAdapter.VH(view);
    }

    @Override
    public int getItemCount() {
        return cList.size();
    }

    @Override
    public void onBindViewHolder(CategoryAdapter.VH holder, int position) {

        holder.cat_name.setText(cList.get(position).getCategory());

    }

    public static class VH extends RecyclerView.ViewHolder {

        TextView cat_name;

        public VH(View itemView) {
            super(itemView);

            cat_name = (TextView) itemView.findViewById(R.id.txt_vv);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mListener != null) mListener.onItemClick(getAdapterPosition());

                    if (getAdapterPosition() == RecyclerView.NO_POSITION) return;

                }
            });

        }
    }

    public void setOnItemClickListener(pro.simpleapp.radiobox.helpers.CategoryAdapter.OnItemClickListener listener) {
        this.mListener = listener;
    }

}
