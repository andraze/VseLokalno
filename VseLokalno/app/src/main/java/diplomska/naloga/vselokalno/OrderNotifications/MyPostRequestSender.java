package diplomska.naloga.vselokalno.OrderNotifications;

import static diplomska.naloga.vselokalno.MainActivity.makeLogD;
import static diplomska.naloga.vselokalno.MainActivity.makeLogW;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MyPostRequestSender {

    private static final String TAG = "MyPostRequestSender";
    private final String server_key = "key=AAAALQ7PSkE:APA91bH2ZL8KGolLrGCPGD8FRSSbwy9X4d0waO1qAWJ7-CP8uOb20NEYd-71evs8udNZo6q6kW-imSPuF024sFbtc56uTCqZ5vEyDsEHt4s8MUshwsBdBCSUAdsw2CNZp3YS3tvlb2FO";
    private final String to_key = "/topics/active_orders_update";
    private final String url = "https://fcm.googleapis.com/fcm/send";
    Context mContext;
    RequestQueue mQueue;

    public MyPostRequestSender(Context mContext) {
        this.mContext = mContext;
        mQueue = Volley.newRequestQueue(mContext);
    }

    public void sendRequest(String mOrderID, String userID_to_notify, String mNew_status) throws JSONException {
        JSONObject data = new JSONObject();
//        data.put("ID_from", "");
        data.put("ID_to", userID_to_notify);
        data.put("ID_order", mOrderID);
        data.put("new_status", mNew_status);

        JSONObject requestJson = new JSONObject();
        requestJson.put("to", to_key);
        requestJson.put("data", data);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, requestJson,
                        response -> makeLogD(TAG, "Got response " + response),
                        error -> makeLogW(TAG, "Error! " + error)) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", server_key);
                return headers;
            }
        };
        mQueue.add(jsonObjectRequest);
    }
}

// Send test request:
//        MyPostRequestSender myPostRequestSender = new MyPostRequestSender(getContext());
//        try {
//            myPostRequestSender.sendRequest("Test", "Test", "Test");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
