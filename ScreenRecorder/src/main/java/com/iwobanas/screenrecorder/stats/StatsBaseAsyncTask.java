package com.iwobanas.screenrecorder.stats;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings.Secure;
import android.util.Log;

import com.iwobanas.screenrecorder.NativeCommands;
import com.iwobanas.screenrecorder.Utils;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public abstract class StatsBaseAsyncTask extends AsyncTask<Void, Void, Void> {

    protected Map<String, String> params = new HashMap<String, String>();

    public StatsBaseAsyncTask(Context context) {
        params.put("package_name", context.getPackageName());
        params.put("app_version", String.valueOf(Utils.getAppVersion(context)));
        params.put("device_id", Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID));
        params.put("su_version", NativeCommands.getInstance().getSuVersion());
    }

    @Override
    protected void onPreExecute() {
        params.put("build_device", Build.DEVICE);
        params.put("build_board", Build.BOARD);
        params.put("build_hardware", Build.HARDWARE);
        params.put("build_model", Build.MODEL);
        params.put("build_id", Build.ID);
        params.put("build_version_sdk_int", String.valueOf(Build.VERSION.SDK_INT));
        params.put("build_version_release", Build.VERSION.RELEASE);
        params.put("is_x86", formatBoolean(Utils.isX86()));
    }

    protected String formatBoolean(boolean value) {
        return value ? "1" : "0";
    }

    protected abstract String getUrl();

    protected abstract String getTag();

    @Override
    protected Void doInBackground(Void... voids) {

        String url = getUrl();

        for (String key: params.keySet()) {
            String vale = params.get(key);
            if (vale == null) continue;

            try {
                url += key.toLowerCase() + '=' + URLEncoder.encode(vale, "UTF-8") + "&";
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("UTF-8 should always be supported", e);
            }
        }

        url += "request_id=" + Utils.md5(url + "SaltLakeCity");
/*
        AndroidHttpClient client = null;
        try {
            HttpGet get = new HttpGet(url);
            client = AndroidHttpClient.newInstance("SCR");
            HttpResponse response = client.execute(get);
            if (response == null || response.getStatusLine() == null) {
                Log.w(getTag(), "null response received");
            } else if (response.getStatusLine().getStatusCode() != 200) {
                StatusLine statusLine = response.getStatusLine();
                Log.w(getTag(), "Response: " + statusLine.getStatusCode() + " " + statusLine.getReasonPhrase());
            }
        } catch (IOException e) {
            Log.w(getTag(), "HTTP GET execution error", e);
        } catch (SecurityException e) {
            Log.w(getTag(), "Allow internet access to SCR to get best settings for your device!", e);
        } finally {
            if (client != null) {
                client.close();
            }
        }*/
        return null;
    }

}
