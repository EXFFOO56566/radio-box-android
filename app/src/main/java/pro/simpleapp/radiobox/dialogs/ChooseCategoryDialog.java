package pro.simpleapp.radiobox.dialogs;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.appcompat.app.AppCompatDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pro.simpleapp.radiobox.R;
import pro.simpleapp.radiobox.helpers.CategoryAdapter;
import pro.simpleapp.radiobox.items.Category;



public class ChooseCategoryDialog extends AppCompatDialog implements
        View.OnClickListener {

    public Activity df;
    List<Category> clist;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    public CategoryAdapter categoryAdapter;


    public ChooseCategoryDialog(Activity a, List<Category> lcat, Activity activity) {
        super(a);
        this.df = a;
        this.clist = lcat;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_dialog);
        recyclerView = (RecyclerView) findViewById(R.id.r_view);
        getWindow().setGravity(Gravity.CENTER);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        linearLayoutManager.setReverseLayout(false);
        recyclerView.setLayoutManager(linearLayoutManager);
        make();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
        dismiss();
    }


    private void make() {
        categoryAdapter = new CategoryAdapter(clist, df, true);
        recyclerView.setAdapter(categoryAdapter);
        int resId = R.anim.layout_animation_fall_down;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(df, resId);
        recyclerView.setLayoutAnimation(animation);
    }


}
