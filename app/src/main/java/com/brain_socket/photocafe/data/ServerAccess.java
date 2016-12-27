package com.brain_socket.photocafe.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.brain_socket.photocafe.model.CartProductModel;
import com.brain_socket.photocafe.model.CategoryModel;
import com.brain_socket.photocafe.model.ProductModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Albert on 12/13/16.
 */
public class ServerAccess {
    // GENERIC ERROR CODES

    public static final int ERROR_CODE_userExistsBefore = -2;
    public static final int ERROR_CODE_userNotExists = -3;
    public static final int ERROR_CODE_UNAUTHENTICATED = -4;
    public static final int ERROR_CODE_unknown = -1000;
    public static final int ERROR_CODE_appVersionInvalid= -121;
    public static final int ERROR_CODE_updateAvailable=  -122;

    public static final int RESPONCE_FORMAT_ERROR_CODE = 601 ;
    public static final int CONNECTION_ERROR_CODE = 600 ;
    public static final int REQUEST_SUCCESS_CODE = 0 ;

    // api
    static final String BASE_SERVICE_URL = "http://photocafe.ae/app/api";

    private static ServerAccess serverAccess = null;

    private ServerAccess() {

    }

    public static ServerAccess getInstance() {
        if (serverAccess == null) {
            serverAccess = new ServerAccess();
        }
        return serverAccess;
    }
    // API calls // ------------------------------------------------

    public ServerResult getCategories() {
        ServerResult result = new ServerResult();
        ArrayList<CategoryModel> categories = null;
        try{
            String url = BASE_SERVICE_URL+"/getCategoriesAndProducts.php";
            ApiRequestResult apiResult = httpRequest(url,null,"get",null);
            JSONArray jsonResponse = apiResult.getResponseJsonArray();
            if(jsonResponse != null){
                categories = new ArrayList<>();
                for(int i=0;i<jsonResponse.length();i++){
                    categories.add(CategoryModel.fromJson(jsonResponse.getJSONObject(i)));
                }
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
        result.addPair("categories",categories);
        return result;
    }

    public ServerResult submitCart(ArrayList<CartProductModel> cartProducts,String tableNo) {
        ServerResult result = new ServerResult();
        try{
            JSONObject jsonToSend = new JSONObject();
            JSONArray cartProductsJsonArr = new JSONArray();
            for(CartProductModel m : cartProducts)
                cartProductsJsonArr.put(m.getJsonObject());
            jsonToSend.put("TableNo",tableNo);
            jsonToSend.put("Products",cartProductsJsonArr.toString());

            String url = BASE_SERVICE_URL+"/addOrder.php?valuesString=" + jsonToSend.toString();
            url = URLEncoder.encode(url,"UTF-8");

            ApiRequestResult apiResult = httpRequest(url,null,"get",null);
            JSONObject jsonResponse = apiResult.getResponseJsonObject();
            if(jsonResponse != null){
                result.addPair("msg",jsonResponse.getString("msg"));
            }
        }catch (Exception ex){
            result.addPair("msg","error");
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * send Https request through connection
     * @param method  get or post
     */
    public ApiRequestResult httpRequest(String url, JSONObject jsonPairs, String method, JSONObject headers) {
        String result = null;
        int responseCode;
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            //configure connection
            con.setUseCaches(false);
            // add headers
            if (headers != null) {
                Iterator keys = headers.keys();
                while (keys.hasNext()) {
                    // loop to get the dynamic key
                    String key = (String) keys.next();
                    String value = headers.getString(key);
                    con.setRequestProperty(key, value);
                }
            }
            //con.setRequestProperty("Content-Type","application/json");
            //con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //application/x-www-form-urlencoded
            if (method.equalsIgnoreCase("post") || method.equalsIgnoreCase("delete")) {
                //String urlParameters = jsonPairs.toString();
                StringBuilder postData = new StringBuilder();
                if (jsonPairs != null) {
                    Iterator<String> keys = jsonPairs.keys();
                    while (keys.hasNext()) {
                        if (postData.length() != 0) postData.append('&');
                        String key = keys.next();
                        postData.append(URLEncoder.encode(key, "UTF-8"));
                        postData.append('=');
                        postData.append(URLEncoder.encode(String.valueOf(jsonPairs.get(key)), "UTF-8"));
                    }
                }
                con.setDoOutput(true); // implicitly set to POST
                con.setRequestMethod(method.toUpperCase());
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(postData.toString());
                wr.flush();
                wr.close();
            }

            responseCode = con.getResponseCode();
            if (responseCode >= 400) {
                try {
                    // try to read returned error response
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    result = response.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                result = response.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseCode = 600; // indicates connection failure
        }
        ApiRequestResult requestResult = new ApiRequestResult();
        requestResult.statusCode = responseCode;
        requestResult.response = result;
        return requestResult;
    }

    public static class ApiRequestResult{
        String response;
        int statusCode;
        int apiErrorCode;
        JSONObject jsonResponse;

        public boolean connectionSuccess(){
            return  statusCode <400;
        }

        public JSONObject getResponseJsonObject() throws JSONException {
            if (response != null && !response.equals("")) { // check if response is empty
                if(jsonResponse == null )
                    jsonResponse = new JSONObject(response);
                if(!jsonResponse.isNull("object"))
                    return jsonResponse.getJSONObject("object");
            }
            return null;
        }

        public JSONArray getResponseJsonArray() throws JSONException{
            JSONArray res = null;
            if (response != null && !response.equals("")) { // check if response is empty
                res = new JSONArray(response);
            }
            return res;
        }

        public int getStatusCode(){
            return statusCode;
        }

        public String getApiError() {
            try {
                if(jsonResponse == null )
                    jsonResponse = new JSONObject(response);
                if(jsonResponse.has("error"))
                    return jsonResponse.getString("error");
                else
                    return null;
            }catch (Exception e){}
            return "";
        }
    }


    private static Bitmap decodeFile(File f,int WIDTH,int HIGHT){
        try {
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);

            //The new size we want to scale to
            final int REQUIRED_WIDTH=WIDTH;
            final int REQUIRED_HIGHT=HIGHT;
            //Find the correct scale value. It should be the power of 2.
            int scale=1;
            while(o.outWidth/scale/2>=REQUIRED_WIDTH && o.outHeight/scale/2>=REQUIRED_HIGHT)
                scale*=2;

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
    }
}
