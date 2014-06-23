package org.mozilla.mozstumbler.service.datahandling;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.mozstumbler.service.SharedConstants;

public final class StumblerBundleReceiver {
    private static final String LOGTAG = StumblerBundleReceiver.class.getName();

    public void handleBundle(Context context, StumblerBundle bundle) {
        ContentValues values = new ContentValues();
        try {
            JSONObject mlsObj = bundle.toMLSJSON();
            if (SharedConstants.isDebug) Log.d(LOGTAG, "Received bundle: " + mlsObj.toString());

            values.put(DatabaseContract.Reports.TIME, mlsObj.getLong("time"));
            values.put(DatabaseContract.Reports.LAT, mlsObj.getDouble("lat"));
            values.put(DatabaseContract.Reports.LON, mlsObj.getDouble("lon"));

            if (mlsObj.has("altitude")) {
                values.put(DatabaseContract.Reports.ALTITUDE, mlsObj.getInt("altitude"));
            }

            if (mlsObj.has("accuracy")) {
                values.put(DatabaseContract.Reports.ACCURACY, mlsObj.getInt("accuracy"));
            }

            if (mlsObj.has("radio")) {
                values.put(DatabaseContract.Reports.RADIO, mlsObj.getString("radio"));
            }

            if (mlsObj.has("cell")) {
                JSONArray cells = mlsObj.getJSONArray("cell");
                values.put(DatabaseContract.Reports.CELL, cells.toString());
                values.put(DatabaseContract.Reports.CELL_COUNT, cells.length());
            }

            JSONArray wifis = mlsObj.getJSONArray("wifi");
            values.put(DatabaseContract.Reports.WIFI, wifis.toString());
            values.put(DatabaseContract.Reports.WIFI_COUNT, wifis.length());

            Intent message = new Intent(SharedConstants.ACTION_GUI_LOG_MESSAGE);
            message.putExtra(SharedConstants.ACTION_GUI_LOG_MESSAGE_EXTRA, mlsObj.toString());
            LocalBroadcastManager.getInstance(context).sendBroadcast(message);
        } catch (JSONException e) {
            Log.w(LOGTAG, "Failed to convert bundle to JSON: " + e);
            return;
        }

        context.getContentResolver().insert(DatabaseContract.Reports.CONTENT_URI, values);
    }
}
