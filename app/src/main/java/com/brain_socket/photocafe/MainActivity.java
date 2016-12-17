package com.brain_socket.photocafe;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.brain_socket.photocafe.data.DataStore;
import com.brain_socket.photocafe.data.PhotoProvider;
import com.brain_socket.photocafe.data.ServerResult;
import com.brain_socket.photocafe.model.CategoryModel;
import com.brain_socket.photocafe.model.OrderModel;
import com.brain_socket.photocafe.model.ProductModel;
import com.brain_socket.photocafe.view.RoundedImageView;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, DataStore.LanguageChangedListener{
    private RecyclerView rvProducts;
    private ViewPager vpCategories;
    private ArrayList<CategoryModel> categories;
    //private CategoryModel selectedCategory;
    private int selectedCategoryIndex = 0;
    private ArrayList<ProductModel> products;
    private ProductsRecycleViewAdapter productsAdapter;
    private CategoryAdapter categoriesAdapter;
    private ArrayList<ProductModel> cartProducts;
    private TextView tvCartProductsCount;
    private Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setCustomActionBar();
        init();
        bindUserData();
    }

    private void init(){
        rvProducts = (RecyclerView)findViewById(R.id.rvProducts);
        vpCategories = (ViewPager)findViewById(R.id.vpCategories);

        cartProducts = new ArrayList<ProductModel>();
        loadingDialog = PhotoCafeApp.getNewLoadingDilaog(this);
    }

    private void setCustomActionBar(){
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar_main_activity);
        View view = getSupportActionBar().getCustomView();
        View btnArabic = view.findViewById(R.id.btnArabic);
        View btnEnglish = view.findViewById(R.id.btnEnglish);
        tvCartProductsCount = (TextView)view.findViewById(R.id.tvCartProductsCount);
        View ivCart = view.findViewById(R.id.ivCart);

        btnArabic.setOnClickListener(this);
        btnEnglish.setOnClickListener(this);
        ivCart.setOnTouchListener(new View.OnTouchListener() {
            private static final int MAX_CLICK_DURATION = 200;
            private long startClickTime;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                }
                return true;
            }
        });
    }

    private void bindUserData(){
        try {
            DataStore.getInstance().addLanguageChangedListener(this);
            categories = DataStore.getInstance().getCategories();
            CategoryModel selectedCategory = CategoryModel.fromJsonString(getIntent().getStringExtra("selectedCategory"));

            rvProducts.setLayoutManager(new GridLayoutManager(this, 1));
            productsAdapter = new ProductsRecycleViewAdapter(this);
            rvProducts.setAdapter(productsAdapter);
            rvProducts.scheduleLayoutAnimation();

            products = selectedCategory.getProducts();
            productsAdapter.updateAdapter();

            vpCategories.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    selectedCategoryIndex = position;
                    CategoryModel selectedCategory = categories.get(position);
                    categoriesAdapter.updateAdapter();
                    products = selectedCategory.getProducts();
                    productsAdapter.updateAdapter();
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            categoriesAdapter = new CategoryAdapter();
            vpCategories.setAdapter(categoriesAdapter);
            categoriesAdapter.updateAdapter();


        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void resetCart(){
        cartProducts.clear();
    }

    private void submitCart(){
        if(cartProducts.size() > 0){
            loadingDialog.show();;
            DataStore.getInstance().attemptSubmitCart(cartProducts,submitCartCallback);
        }
    }

    private DataStore.DataRequestCallback submitCartCallback = new DataStore.DataRequestCallback() {
        @Override
        public void onDataReady(ServerResult result, boolean success) {
            loadingDialog.dismiss();
            if(success){

            }
        }
    };

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

    @Override
    public void onLanguageChanged() {
        categories = DataStore.getInstance().getCategories();
        categoriesAdapter.updateAdapter();

        products = categories.get(selectedCategoryIndex).getProducts();
        productsAdapter.updateAdapter();
    }

    private class ProductsRecycleViewAdapter extends RecyclerView.Adapter<ProductViewHolderItem> {
        private LayoutInflater inflater;

        View.OnClickListener onItemClickListner = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                try {
//                    int itemPosition = (int) v.getTag();
//                    CategoryModel selectedCategory = categories.get(itemPosition);
//                    Intent myIntent = new Intent(SelectCategoryActivity.this, MainActivity.class);
//                    if(selectedCategory != null)
//                    {
//                        myIntent.putExtra("selectedCategory", selectedCategory.getJsonString());
//                        startActivity(myIntent);
//                    }
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
            }
        };

        public ProductsRecycleViewAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void updateAdapter() {
            try {
                if (products != null)
                    notifyDataSetChanged();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public ProductViewHolderItem onCreateViewHolder(ViewGroup parent, int viewType) {
            View root = inflater.inflate(R.layout.row_product, parent, false);
            ProductViewHolderItem holder = new ProductViewHolderItem(root);
            root.setOnClickListener(onItemClickListner);
            return holder;
        }

        @Override
        public void onBindViewHolder(ProductViewHolderItem holder, int position) {
            try {
                final ProductModel model = products.get(position);
                holder.root.setTag(position);
                PhotoProvider.getInstance().displayPhotoNormal(model.getImage(), holder.ivProduct);
                holder.tvProduct.setText(model.getName());
                holder.tvDescription.setText(model.getDescription());
                holder.tvPrice.setText(model.getPrice());
                holder.btnAdd.setTag(position);
                holder.btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if (products != null) {
                                int selectedProductIndex = (int) v.getTag();
                                ProductModel selectedProduct = products.get(selectedProductIndex);
                                cartProducts.add(selectedProduct);
                                tvCartProductsCount.setText(Integer.toString(cartProducts.size()));
                            }
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            if (products == null)
                return 0;
            return products.size();
        }
    }

    private static class ProductViewHolderItem extends RecyclerView.ViewHolder {
        public View root;
        public ImageView ivProduct;
        public TextView tvProduct;
        public TextView tvDescription;
        public TextView tvPrice;
        public View btnAdd;

        public ProductViewHolderItem(View v) {
            super(v);
            root = v;
            ivProduct = (RoundedImageView) v.findViewById(R.id.ivProduct);
            tvProduct = (TextView) v.findViewById(R.id.tvProduct);
            tvDescription = (TextView) v.findViewById(R.id.tvDescription);
            tvPrice = (TextView) v.findViewById(R.id.tvPrice);
            btnAdd = v.findViewById(R.id.btnAdd);
        }
    }

    class CategoryAdapter extends PagerAdapter implements View.OnClickListener {
        private Context context;
        private LayoutInflater inflater;

        public CategoryAdapter() {
            this.context = PhotoCafeApp.getAppContext();
            this.inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
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
        public Object instantiateItem(ViewGroup container, int position) {
            View v = null;
            try {
                v = inflater.inflate(R.layout.item_category_view_pager, container, false);
                ImageView ivCategory = (ImageView) v.findViewById(R.id.ivCategory);
                TextView tvCategory = (TextView) v.findViewById(R.id.tvCategory);
                v.setTag(position);
                v.setOnClickListener(this);

                CategoryModel categoryModel = categories.get(position);
                tvCategory.setText(categoryModel.getName());
                PhotoProvider.getInstance().displayPhotoNormal(categoryModel.getIcon(), ivCategory);
                container.addView(v);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return v;
        }

        @Override
        public int getCount() {
            if (categories == null)
                return 0;
            return categories.size();
        }

        @Override
        public float getPageWidth(int position) {
            return 0.222f;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object view) {
            container.removeView((View) view);
        }

        @Override
        public void onClick(View v) {
//            int selectedBrandIndex = (Integer) v.getTag();
//            //selectedBrandIndex--;
//            selectedBrandIndex = selectedBrandIndex < 0 ? 0 : selectedBrandIndex;
//            Intent i = new Intent(MainActivity.this,BrandDetailsActivity.class);
//            i.putExtra("brand",brands.get(selectedBrandIndex).getJsonString());
//            startActivity(i);
        }
    }
}