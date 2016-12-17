package com.brain_socket.photocafe;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.brain_socket.photocafe.data.DataStore;
import com.brain_socket.photocafe.data.PhotoProvider;
import com.brain_socket.photocafe.model.CategoryModel;
import com.brain_socket.photocafe.view.RoundedImageView;

import java.util.ArrayList;

public class SelectCategoryActivity extends AppCompatActivity implements DataStore.LanguageChangedListener,
        View.OnClickListener {
    private RecyclerView rvCategories;
    private CategoriesRecycleViewAdapter categoriesAdapter;
    private ArrayList<CategoryModel> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_category);
        setCustomActionBar();
        init();
        bindUserData();
    }

    void init(){
        rvCategories = (RecyclerView)findViewById(R.id.rvCategories);
    }

    void bindUserData(){
        DataStore.getInstance().addLanguageChangedListener(this);
        rvCategories.setLayoutManager(new GridLayoutManager(this, 2));
        categoriesAdapter = new CategoriesRecycleViewAdapter(this);
        rvCategories.setAdapter(categoriesAdapter);
        rvCategories.scheduleLayoutAnimation();
        categories = DataStore.getInstance().getCategories();
        categoriesAdapter.updateAdapter();
    }

    private void setCustomActionBar(){
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar_select_category);
        View view = getSupportActionBar().getCustomView();
        View btnArabic = view.findViewById(R.id.btnArabic);
        View btnEnglish = view.findViewById(R.id.btnEnglish);

        btnArabic.setOnClickListener(this);
        btnEnglish.setOnClickListener(this);
    }

    @Override
    public void onLanguageChanged() {
        categories = DataStore.getInstance().getCategories();
        categoriesAdapter.updateAdapter();
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId){
            case R.id.btnArabic:
                PhotoCafeApp.setLanguage(PhotoCafeApp.SUPPORTED_LANGUAGE.AR);
                break;
            case R.id.btnEnglish:
                PhotoCafeApp.setLanguage(PhotoCafeApp.SUPPORTED_LANGUAGE.EN);
                break;
        }
    }

    private class CategoriesRecycleViewAdapter extends RecyclerView.Adapter<CategoryViewHolderItem> {
        private LayoutInflater inflater;

        View.OnClickListener onItemClickListner = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int itemPosition = (int) v.getTag();
                    CategoryModel selectedCategory = categories.get(itemPosition);
                    Intent myIntent = new Intent(SelectCategoryActivity.this, MainActivity.class);
                    if(selectedCategory != null)
                    {
                        myIntent.putExtra("selectedCategory", selectedCategory.getJsonString());
                        startActivity(myIntent);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        public CategoriesRecycleViewAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void updateAdapter() {
            try {
                if (categories != null)
                    notifyDataSetChanged();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public CategoryViewHolderItem onCreateViewHolder(ViewGroup parent, int viewType) {
            View root = inflater.inflate(R.layout.item_category, parent, false);
            CategoryViewHolderItem holder = new CategoryViewHolderItem(root);
            root.setOnClickListener(onItemClickListner);
            return holder;
        }

        @Override
        public void onBindViewHolder(CategoryViewHolderItem holder, int position) {
            try {
                final CategoryModel model = categories.get(position);
                holder.root.setTag(position);
                PhotoProvider.getInstance().displayPhotoNormal(model.getIcon(), holder.ivCategory);
                holder.tvCategory.setText(model.getName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            if (categories == null)
                return 0;
            return categories.size();
        }
    }

    private static class CategoryViewHolderItem extends RecyclerView.ViewHolder {
        public View root;
        public ImageView ivCategory;
        public TextView tvCategory;

        public CategoryViewHolderItem(View v) {
            super(v);
            root = v;
            ivCategory = (RoundedImageView) v.findViewById(R.id.ivCategory);
            tvCategory = (TextView) v.findViewById(R.id.tvCategory);
        }
    }
}
