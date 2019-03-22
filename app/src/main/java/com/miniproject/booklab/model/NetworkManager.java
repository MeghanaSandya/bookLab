package com.miniproject.booklab.model;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import androidx.annotation.NonNull;

public class NetworkManager {

    private static NetworkManager ssInstance;

    private NetworkManager() {
    }


    public static NetworkManager getInstance() {

        if (ssInstance == null) {
            synchronized (NetworkManager.class) {
                if (ssInstance == null) {
                    ssInstance = new NetworkManager();
                }
            }
        }
        return ssInstance;
    }

    public void getNetworkRequester(Context context, int method, String url, @NonNull final NetworkResponseListener responseListener) {
        if (TextUtils.isEmpty(url)) {
            responseListener.onError(new VolleyError());
        }
        StringRequest request = new StringRequest(method, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                responseListener.onResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                responseListener.onError(error);
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }


    public interface NetworkResponseListener {
        void onResponse(String response);

        void onError(VolleyError error);
    }
}
