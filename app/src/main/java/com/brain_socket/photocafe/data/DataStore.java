package com.brain_socket.photocafe.data;

import android.location.Location;
import android.os.Handler;

import com.brain_socket.photocafe.model.CategoryModel;
import com.brain_socket.photocafe.model.ProductModel;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * This class will be responsible for requesting new data from the data providers
 * like  and invoking the callback when ready plus handling multithreading when required
 *
 * @author MolhamStein
 */
@SuppressWarnings({"unchecked", "UnusedAssignment"})
public class DataStore {

    public static String VERSIOIN_ID = "0.1";

    public enum GENERIC_ERROR_TYPE {NO_ERROR, UNDEFINED_ERROR, NO_CONNECTION, NOT_LOGGED_IN, NO_MORE_PAGES}

    public enum App_ACCESS_MODE {NOT_LOGGED_IN, NOT_VERIFIED, VERIFIED}

    ;
    private static DataStore instance = null;

    private Handler handler;
    private ArrayList<DataStoreUpdateListener> updateListeners;
    private ArrayList<LanguageChangedListener> languageChangedListeners;
    private ServerAccess serverHandler;
    private String apiAccessToken;
    private App_ACCESS_MODE accessMode;

    // Home screen data
    private ArrayList<CategoryModel> categories;


    // internal data
    private final int UPDATE_INTERVAL = 60000; // update Data each 60 sec
    private static boolean isUpdatingDataStore = false;


    private DataStore() {
        try {
            handler = new Handler();
            updateListeners = new ArrayList<DataStoreUpdateListener>();
            languageChangedListeners = new ArrayList<LanguageChangedListener>();
            serverHandler = ServerAccess.getInstance();
            getLocalData();
            //requestCategories();
        } catch (Exception ignored) {
        }
    }

    public static DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }


    /**
     * used to invoke the DataRequestCallback on the main thread
     */
    private void invokeCallback(final DataRequestCallback callback, final boolean success, final ServerResult result) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (callback == null)
                    return;
                callback.onDataReady(result, success);
            }
        });
    }

    public void clearLocalData() {
        try {
            DataCacheProvider.getInstance().clearCache();
            categories = null;
        } catch (Exception ignored) {
        }
    }

    public void logout() {
        clearLocalData();
        broadcastloginStateChange();
    }

    public void getLocalData() {
        DataCacheProvider cache = DataCacheProvider.getInstance();

        categories = cache.getStoredArrayWithKey(DataCacheProvider.KEY_APP_ARRAY_CATEGORIES, new TypeToken<ArrayList<CategoryModel>>() {
        }.getType());
    }

    //--------------------
    // DataStore Update
    //-------------------------------------------

    public void startScheduledUpdates() {
        try {
            // start schedule timer
            handler.post(runnableUpdate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopScheduledUpdates() {
        try {
            handler.removeCallbacks(runnableUpdate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Runnable runnableUpdate = new Runnable() {
        @Override
        public void run() {
//            requestBrandsWithProducts(null);
//            requestWorkshops("", null);
//            if (isUserLoggedIn()) {
//
//            }
            handler.postDelayed(runnableUpdate, UPDATE_INTERVAL);
        }
    };

    public void triggerDataUpdate() {
        // get Following list and cache it for later use
    }

    private void broadcastDataStoreUpdate() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (DataStoreUpdateListener listener : updateListeners) {
                    listener.onDataStoreUpdate();
                }
            }
        });
    }

    public void broadcastLanguageChanged(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                for(LanguageChangedListener listener : languageChangedListeners)
                    listener.onLanguageChanged();
            }
        });
    }

    public void removeUpdateBroadcastListener(DataStoreUpdateListener listener) {
        if (updateListeners != null && updateListeners.contains(listener))
            updateListeners.remove(listener);
    }

    public void removeLanguageChangedListener(LanguageChangedListener listener) {
        if (languageChangedListeners != null && languageChangedListeners.contains(listener))
            languageChangedListeners.remove(listener);
    }

    public void addUpdateBroadcastListener(DataStoreUpdateListener listener) {
        if (updateListeners == null)
            updateListeners = new ArrayList();
        if (!updateListeners.contains(listener))
            updateListeners.add(listener);
    }

    public void addLanguageChangedListener(LanguageChangedListener listener) {
        if (languageChangedListeners == null)
            languageChangedListeners = new ArrayList();
        if (!languageChangedListeners.contains(listener))
            languageChangedListeners.add(listener);
    }

    private void broadcastloginStateChange() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (DataStoreUpdateListener listener : updateListeners) {
                    listener.onLoginStateChange();
                }
            }
        });
    }

    /**
     * get Product by Id this is used incase of opening product details activity through deeplinking
     *
     * @param prodId
     * @param callback
     */
    public void requestProduct(final String prodId, final DataRequestCallback callback) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                ServerResult result = serverHandler.getProductById(prodId);
//                broadcastDataStoreUpdate();
//                invokeCallback(callback, !result.connectionFailed(), result);
//            }
//        }).start();
    }

    public void requestCategories(final DataRequestCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean success = true;
                ServerResult result = serverHandler.getCategories();
                if (result.connectionFailed()) {
                    success = false;
                } else {
                    if (result.isValid()) {
                        categories = (ArrayList<CategoryModel>) result.get("categories");
                        DataCacheProvider.getInstance().storeArrayWithKey(DataCacheProvider.KEY_APP_ARRAY_CATEGORIES, categories);
                        result.addPair("categories",categories);
                    }
                }
                invokeCallback(callback, success, result); // invoking the callback
            }
        }).start();
    }

    public void attemptSubmitCart(final ArrayList<ProductModel> products,final DataRequestCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean success = true;
                ServerResult result = serverHandler.submitCart();
                if (result.connectionFailed()) {
                    success = false;
                } else {
                    if (result.isValid()) {
                    }
                }
                invokeCallback(callback, success, result); // invoking the callback
            }
        }).start();
    }

    //--------------------
    // Getters
    //----------------------------------------------

    public ArrayList<CategoryModel> getCategories() { return categories; }

    public void setApiAccessToken(String apiAccessToken) {
        this.apiAccessToken = apiAccessToken;
        DataCacheProvider.getInstance().storeStringWithKey(DataCacheProvider.KEY_ACCESS_TOKEN, apiAccessToken);

    }

    public void setAccessMode(App_ACCESS_MODE accessMode) {
        this.accessMode = accessMode;
        DataCacheProvider.getInstance().storeIntWithKey(DataCacheProvider.KEY_APP_ACCESS_MODE, accessMode.ordinal());
    }

    public App_ACCESS_MODE getAccessMode() {
        return accessMode;
    }

    public boolean isLoggedIn() {
        return apiAccessToken != null && !apiAccessToken.isEmpty() && accessMode == App_ACCESS_MODE.VERIFIED;
    }

    // interfaces
    public interface DataRequestCallback {
        void onDataReady(ServerResult result, boolean success);
    }

    public interface DataStoreUpdateListener {
        void onDataStoreUpdate();

        void onNewEventNotificationsAvailable();

        void onLoginStateChange();
    }

    public interface LanguageChangedListener{
        void onLanguageChanged();
    }

    public interface DataStoreErrorListener {
        void onError(GENERIC_ERROR_TYPE error);
    }
}