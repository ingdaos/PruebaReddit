package prueba.comunication;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import prueba.pruebareddit.MainActivity;
import prueba.pruebareddit.R;

/**
 * Created by PERSONAL on 21/01/2017.
 */

public class GetInfo extends AsyncTask<Object[], Void, String> {

    private String TAG = GetInfo.class.getSimpleName();

    private MainActivity main;
    private String url;
    private ArrayList<HashMap<String, Object>> infoList;

    private ProgressDialog pDialog;

    public GetInfo(MainActivity main, String url, ArrayList listItems) {
        this.main = main;
        this.url = url;
        this.infoList = listItems;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(main);
        pDialog.setMessage(main.getResources().getString(R.string.getting_info));
        pDialog.setCancelable(false);
        pDialog.show();
    }

    @Override
    protected String doInBackground(Object[]... params) {
        HttpHandler sh = new HttpHandler();

        String jsonStr = sh.makeServiceCall(url);
        Log.e(TAG, "Response from url: " + jsonStr);

        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);

                JSONObject data = (JSONObject) jsonObj.get("data");
                JSONArray children = data.getJSONArray("children");

                Log.d(TAG, "data: " + data);
                Log.d(TAG, "children: " + children);

                for (int i = 0; i < children.length(); i++) {
                    JSONObject c = children.getJSONObject(i).getJSONObject("data");

                    String display_name = c.getString("display_name");
                    String display_icon = c.getString("icon_img");
                    String info_description = c.getString("public_description");
                    String url_banner_img = c.getString("banner_img");
                    String title = c.getString("title");

                    if ("null".equals(display_icon) || display_icon.isEmpty()) {
                        display_icon = c.getString("header_img");
                    }

                    if ("null".equals(display_icon) || display_icon.isEmpty()) {
                        display_icon = main.getResources().getString(R.string.default_url_img);
                    }

                    Log.d(TAG, "display_name: " + display_name);
                    Log.d(TAG, "public_description: " + info_description);

                    Drawable image_item = sh.downloadImage(display_icon);
                    Bitmap imageTmp = drawableToBitmap(image_item);

                    HashMap<String, Object> itemObject = new HashMap<>();

                    itemObject.put("display_name", display_name);
                    itemObject.put("display_icon", imageTmp);
                    itemObject.put("public_description", info_description);
                    itemObject.put("url_banner", url_banner_img);
                    itemObject.put("title_info", title);

                    infoList.add(itemObject);
                }
            } catch (final JSONException e) {
                Log.e(TAG, "Json parsing error: " + e.getMessage());
            }
        } else {
            Log.e(TAG, "Couldn't get json from server.");
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (pDialog.isShowing()) {
            pDialog.dismiss();
        }
        main.loadItems();
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable != null) {

            if (drawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                if (bitmapDrawable.getBitmap() != null) {
                    return bitmapDrawable.getBitmap();
                }
            }

            if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
                bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;

        } else {
            return null;
        }

    }

}
