package com.brain_socket.photocafe;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.brain_socket.photocafe.data.DataStore;
import com.brain_socket.photocafe.data.PhotoProvider;
import com.brain_socket.photocafe.data.ServerResult;
import com.brain_socket.photocafe.model.CartProductModel;
import com.brain_socket.photocafe.model.CategoryModel;
import com.brain_socket.photocafe.model.OrderModel;
import com.brain_socket.photocafe.model.ProductModel;
import com.brain_socket.photocafe.view.RoundedImageView;
import com.brain_socket.photocafe.view.TextViewCustomFont;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

enum ViewType {List,FullScreen}

public class MainActivity extends AppCompatActivity implements View.OnClickListener, DataStore.LanguageChangedListener{
    private RecyclerView rvProducts;
    private ViewPager vpCategories;
    private ArrayList<CategoryModel> categories;
    //private CategoryModel selectedCategory;
    private int selectedCategoryIndex = 0;
    private ArrayList<ProductModel> products;
    private ProductsRecycleViewAdapter productsAdapter;
    private CategoryAdapter categoriesAdapter;
    private ArrayList<CartProductModel> cartProducts;
    private TextView tvCartProductsCount;
    private Dialog loadingDialog;
    private TextView tvCategory;
    private TextView tvNoProducts;
    private View btnEnglish;
    private View btnArabic;
    private RecyclerView rvCartItems;
    private CartProductsAdapter cartProductsAdapter;
    private View rlCartContents;
    private View tvCartEmpty;
    private View cartContentSlider;
    private DiagTableNo diagTableNo;
    private ViewPager vpProducts;
    private ViewType currentViewType;
    private ProductsFullScreenAdapter productsFullScreenAdapter;

    ImageView ivViewType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setCustomActionBar();
        init();
        bindUserData();
    }

    private void init(){
        try {
            rvProducts = (RecyclerView) findViewById(R.id.rvProducts);
            vpCategories = (ViewPager) findViewById(R.id.vpCategories);
            tvCartProductsCount = (TextView) findViewById(R.id.tvCartProductsCount);
            View ivCart = findViewById(R.id.ivCart);
            btnEnglish = findViewById(R.id.btnEnglish);
            btnArabic = findViewById(R.id.btnArabic);
            tvCategory = (TextView)findViewById(R.id.tvCategory);
            tvNoProducts = (TextView)findViewById(R.id.tvNoProducts);
            rvCartItems = (RecyclerView)findViewById(R.id.rvCartItems);
            rlCartContents = findViewById(R.id.rlCartContents);
            tvCartEmpty = findViewById(R.id.tvCartEmpty);
            View tvReset = findViewById(R.id.tvReset);
            View tvSubmit = findViewById(R.id.tvSubmit);
            cartContentSlider = findViewById(R.id.cartContentSlider);
            vpProducts = (ViewPager)findViewById(R.id.vpProducts);
            ivViewType = (ImageView) findViewById(R.id.ivViewType);
            View ivClose = findViewById(R.id.ivClose);

            cartProducts = new ArrayList<CartProductModel>();
            loadingDialog = PhotoCafeApp.getNewLoadingDilaog(this);
            tvNoProducts.setVisibility(View.GONE);
            ivCart.setOnClickListener(this);
            btnArabic.setOnClickListener(this);
            btnEnglish.setOnClickListener(this);
            tvReset.setOnClickListener(this);
            tvSubmit.setOnClickListener(this);
            ivViewType.setOnClickListener(this);
            ivClose.setOnClickListener(this);

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

//    private void setCustomActionBar(){
//        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        getSupportActionBar().setDisplayShowCustomEnabled(true);
//        getSupportActionBar().setCustomView(R.layout.custom_action_bar_main_activity);
//        View view = getSupportActionBar().getCustomView();
//        View btnLanguage = view.findViewById(R.id.btnLanguage);
//        tvCartProductsCount = (TextView)view.findViewById(R.id.tvCartProductsCount);
//        View ivCart = view.findViewById(R.id.ivCart);
//
//        btnLanguage.setOnClickListener(this);
////        ivCart.setOnTouchListener(new View.OnTouchListener() {
////            private static final int MAX_CLICK_DURATION = 200;
////            private long startClickTime;
////
////            @Override
////            public boolean onTouch(View v, MotionEvent event) {
////                switch (event.getAction()) {
////                }
////                return true;
////            }
////        });
//    }

    private void bindUserData(){
        try {
            DataStore.getInstance().addLanguageChangedListener(this);
            currentViewType = ViewType.List;
            vpProducts.setVisibility(View.GONE);

            PhotoCafeApp.SUPPORTED_LANGUAGE currentLanguage = PhotoCafeApp.getCurrentLanguage();
            if(currentLanguage == PhotoCafeApp.SUPPORTED_LANGUAGE.AR) {
                btnEnglish.setVisibility(View.VISIBLE);
                btnArabic.setVisibility(View.INVISIBLE);
            }
            else {
                btnEnglish.setVisibility(View.INVISIBLE);
                btnArabic.setVisibility(View.VISIBLE);
            }

            diagTableNo = new DiagTableNo(this,tableNoDiagCallBack);
            rlCartContents.setVisibility(View.GONE);
            tvCartEmpty.setVisibility(View.VISIBLE);
            //cartContentSlider.setVisibility(View.GONE);

            categories = DataStore.getInstance().getCategories();
            CategoryModel selectedCategory = CategoryModel.fromJsonString(getIntent().getStringExtra("selectedCategory"));
            selectedCategoryIndex = findCategoryIndex(selectedCategory.getId());
            tvCategory.setText(selectedCategory.getName());

            rvProducts.setLayoutManager(new GridLayoutManager(this, 1));
            productsAdapter = new ProductsRecycleViewAdapter(this);
            rvProducts.setAdapter(productsAdapter);
            rvProducts.scheduleLayoutAnimation();

            products = selectedCategory.getProducts();
            productsAdapter.updateAdapter();
            handleProductsView();

            categoriesAdapter = new CategoryAdapter();
            vpCategories.setAdapter(categoriesAdapter);
            categoriesAdapter.updateAdapter();

            rvCartItems.setLayoutManager(new GridLayoutManager(this, 1));
            cartProductsAdapter = new CartProductsAdapter(this);
            rvCartItems.setAdapter(cartProductsAdapter);

            productsFullScreenAdapter = new ProductsFullScreenAdapter();
            vpProducts.setAdapter(productsFullScreenAdapter);
            productsFullScreenAdapter.updateAdapter();

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private DiagTableNo.TableNoDiagCallBack tableNoDiagCallBack = new DiagTableNo.TableNoDiagCallBack() {
        @Override
        public void onClose(String tableNo) {
            diagTableNo.dismiss();
            if(cartProducts.size() > 0){
                loadingDialog.show();
                DataStore.getInstance().attemptSubmitCart(cartProducts,tableNo,submitCartCallback);
            }
        }
    };

    private int getCartProductsCount(){
        int cartProductsCount = 0;
        CartProductModel cartProductModel = null;
        for (CartProductModel m : cartProducts) {
            cartProductsCount += m.getQuantity();
        }
        return cartProductsCount;
    }

    private int findCategoryIndex(String id){
        int categoryIndex = 0;
        for(int i=0;i<categories.size();i++){
            if(categories.get(i).getId().equals(id)){
                categoryIndex = i;
                break;
            }
        }
        return categoryIndex;
    }

    private void handleProductsView(){
        if(products.size() > 0){
            tvNoProducts.setVisibility(View.GONE);
            if(currentViewType == ViewType.FullScreen){
                vpProducts.setVisibility(View.VISIBLE);
                rvProducts.setVisibility(View.GONE);
            }else{
                vpProducts.setVisibility(View.GONE);
                rvProducts.setVisibility(View.VISIBLE);
            }
        }
        else{
            tvNoProducts.setVisibility(View.VISIBLE);
            vpProducts.setVisibility(View.GONE);
            rvProducts.setVisibility(View.GONE);
        }
    }

    private void resetCart(){
        cartProducts.clear();
        cartProductsAdapter.updateAdapter();
        //cartContentSlider.setVisibility(View.GONE);
        tvCartProductsCount.setText("0");
    }

    private void submitCart(){
        diagTableNo.show();
    }

    private DataStore.DataRequestCallback submitCartCallback = new DataStore.DataRequestCallback() {
        @Override
        public void onDataReady(ServerResult result, boolean success) {
            try {
                loadingDialog.dismiss();
                if (success) {
                    String msg = (String) result.getValue("msg");
                    if (msg.equals("1")) {
                        PhotoCafeApp.Toast(getString(R.string.activity_main_cart_contents_submit_success));
                    } else {
                        PhotoCafeApp.Toast(msg);
                    }
                }
                //cartContentSlider.setVisibility(View.GONE);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    };

    private void toggleLanguage(){
        try {
            TextViewCustomFont.resetFonts();
            PhotoCafeApp.SUPPORTED_LANGUAGE currentLanguage = PhotoCafeApp.getCurrentLanguage();
            if (currentLanguage == PhotoCafeApp.SUPPORTED_LANGUAGE.AR) {
                btnEnglish.setVisibility(View.INVISIBLE);
                btnArabic.setVisibility(View.VISIBLE);
                PhotoCafeApp.setLanguage(PhotoCafeApp.SUPPORTED_LANGUAGE.EN);
            } else {
                btnEnglish.setVisibility(View.VISIBLE);
                btnArabic.setVisibility(View.INVISIBLE);
                PhotoCafeApp.setLanguage(PhotoCafeApp.SUPPORTED_LANGUAGE.AR);
            }
            getIntent().putExtra("selectedCategory", categories.get(selectedCategoryIndex).getJsonString());
            recreate();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void changeViewType(){
        if(currentViewType == ViewType.List){
            currentViewType = ViewType.FullScreen;
            ivViewType.setImageResource(R.drawable.ic_list);
            rvProducts.setVisibility(View.GONE);
            vpProducts.setVisibility(View.VISIBLE);

        }else{
            currentViewType = ViewType.List;
            ivViewType.setImageResource(R.drawable.ic_carousel);
            rvProducts.setVisibility(View.VISIBLE);
            vpProducts.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId){
            case R.id.btnEnglish:
                toggleLanguage();
                break;
            case R.id.btnArabic:
                toggleLanguage();
                break;
            case R.id.tvReset:
                resetCart();
                break;
            case R.id.tvSubmit:
                submitCart();
                break;
            case R.id.ivCart:
                //cartContentSlider.setVisibility(View.VISIBLE);
                break;
            case R.id.ivViewType:
                changeViewType();
                break;
            case R.id.ivClose:

                break;
        }
    }

    private CartProductModel getProductFromCartProducts(String productId){
        CartProductModel model = null;
        for(int i=0;i<cartProducts.size();i++){
            if(cartProducts.get(i).getId().equals(productId)){
                model = cartProducts.get(i);
                break;
            }
        }
        return model;
    }

    private void addProductToCart(int selectedProductIndex){
        if (products != null) {
            ProductModel selectedProduct = products.get(selectedProductIndex);
            CartProductModel cartProduct = getProductFromCartProducts(selectedProduct.getId());
            if(cartProduct != null)
            {
                cartProduct.setQuantity(cartProduct.getQuantity()+1);
                cartProduct.setTotalPrice(cartProduct.getQuantity() * Float.parseFloat(selectedProduct.getPrice()));
            }
            else {
                cartProduct = new CartProductModel(selectedProduct.getId(),selectedProduct.getName(),
                        1,
                        Float.parseFloat(selectedProduct.getPrice()));
                cartProducts.add(cartProduct);
            }
            tvCartProductsCount.setText(Integer.toString(getCartProductsCount()));
            cartProductsAdapter.updateAdapter();
        }
    }

    @Override
    public void onLanguageChanged() {
        categories = DataStore.getInstance().getCategories();
        categoriesAdapter.updateAdapter();

        products = categories.get(selectedCategoryIndex).getProducts();
        productsAdapter.updateAdapter();

        if(PhotoCafeApp.getCurrentLanguage() == PhotoCafeApp.SUPPORTED_LANGUAGE.AR)
            tvNoProducts.setText(getString(R.string.activity_main_activity_no_products));
        else
            tvNoProducts.setText(getString(R.string.activity_main_activity_no_products));
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
                holder.tvPrice.setText(model.getPriceWithUnti());
                holder.btnAdd.setTag(position);
                holder.btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            int selectedProductIndex = (int)v.getTag();
                            addProductToCart(selectedProductIndex);
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
            ivProduct = (ImageView) v.findViewById(R.id.ivProduct);
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

                if(selectedCategoryIndex == position)
                    ivCategory.setBackgroundResource(R.drawable.shape_brown_light_button_badge);
                else
                    ivCategory.setBackgroundResource(R.drawable.shape_brown_dark_button_badge);

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
            return 0.333f;
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
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public void onClick(View v) {
            try {
                selectedCategoryIndex = (int) v.getTag();
                CategoryModel selectedCategory = categories.get(selectedCategoryIndex);
                categoriesAdapter.updateAdapter();
                tvCategory.setText(selectedCategory.getName());
                products = selectedCategory.getProducts();
                productsAdapter.updateAdapter();
                productsFullScreenAdapter.updateAdapter();
                handleProductsView();
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    private class CartProductsAdapter extends RecyclerView.Adapter<CartProductViewHolderItem> implements View.OnClickListener{
        private LayoutInflater inflater;

        View.OnClickListener onItemClickListner = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };

        public CartProductsAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void updateAdapter() {
            try {
                if (cartProducts != null) {
                    notifyDataSetChanged();
                    if(cartProducts.size() > 0){
                        rlCartContents.setVisibility(View.VISIBLE);
                        tvCartEmpty.setVisibility(View.GONE);
                    }
                    else{
                        rlCartContents.setVisibility(View.GONE);
                        tvCartEmpty.setVisibility(View.VISIBLE);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public CartProductViewHolderItem onCreateViewHolder(ViewGroup parent, int viewType) {
            View root = inflater.inflate(R.layout.row_cart_product, parent, false);
            CartProductViewHolderItem holder = new CartProductViewHolderItem(root);
            return holder;
        }

        @Override
        public void onBindViewHolder(CartProductViewHolderItem holder, int position) {
            try {
                final CartProductModel model = cartProducts.get(position);
                holder.root.setTag(position);
                holder.tvProduct.setText(model.getName());
                holder.btnPlus.setTag(position);
                holder.btnPlus.setOnClickListener(this);
                holder.btnMinus.setTag(position);
                holder.btnMinus.setOnClickListener(this);
                holder.etQuantity.setText(Integer.toString(model.getQuantity()));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            if (cartProducts == null)
                return 0;
            return cartProducts.size();
        }

        @Override
        public void onClick(View v) {
            try {
                int viewId = v.getId();
                if(viewId == R.id.btnPlus || viewId == R.id.btnMinus){
                    int pos = (int)v.getTag();
                    CartProductModel m = cartProducts.get(pos);
                    if(viewId == R.id.btnPlus)  m.setQuantity(m.getQuantity() + 1);
                    if(viewId == R.id.btnMinus && m.getQuantity() > 0) m.setQuantity(m.getQuantity() - 1);

                    if(m.getQuantity() <= 0)
                        cartProducts.remove(m);
                }
                cartProductsAdapter.updateAdapter();
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }

    private static class CartProductViewHolderItem extends RecyclerView.ViewHolder {
        public View root;
        public TextView tvProduct;
        public TextView etQuantity;
        public View btnPlus;
        public View btnMinus;

        public CartProductViewHolderItem(View v) {
            super(v);
            root = v;
            tvProduct = (TextView) v.findViewById(R.id.tvProduct);
            btnPlus = v.findViewById(R.id.btnPlus);
            btnMinus = v.findViewById(R.id.btnMinus);
            etQuantity = (TextView) v.findViewById(R.id.etQuantity);
        }
    }

    class ProductsFullScreenAdapter extends PagerAdapter implements View.OnClickListener {
        private Context context;
        private LayoutInflater inflater;

        public ProductsFullScreenAdapter() {
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
                v = inflater.inflate(R.layout.item_product_full_screen, container, false);
                ProductModel p = products.get(position);
                ImageView ivProduct = (ImageView)v.findViewById(R.id.ivProduct);
                TextView tvProduct = (TextView)v.findViewById(R.id.tvProduct);
                TextView tvPrice = (TextView)v.findViewById(R.id.tvPrice);
                TextView tvDescription = (TextView)v.findViewById(R.id.tvDescription);
                View ivAdd = v.findViewById(R.id.ivAdd);
                ivAdd.setTag(position);
                ivAdd.setOnClickListener(this);

                PhotoProvider.getInstance().displayPhotoNormal(p.getImage(), ivProduct);
                tvProduct.setText(p.getName());
                tvPrice.setText(p.getPriceWithUnti());
                tvDescription.setText(p.getDescription());
                container.addView(v);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return v;
        }

        @Override
        public int getCount() {
            if (products == null)
                return 0;
            return products.size();
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
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public void onClick(View v) {
            try {
                if(v.getId() == R.id.ivAdd){
                    int selectedProductIndex = (int)v.getTag();
                    addProductToCart(selectedProductIndex);
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }
}