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

import prueba.database.DBHandler;
import prueba.database.RedditInfoDB;
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

    public ProgressDialog getpDialog() {
        return pDialog;
    }

    public void setpDialog(ProgressDialog pDialog) {
        this.pDialog = pDialog;
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
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
        }
        main.loadItems();
    }

    @Override
    protected String doInBackground(Object[]... params) {

        if (HttpHandler.isOnline()) {
            Log.d(TAG, "Descargando informacion");
            downloadInfo();
        } else {
            Log.d(TAG, "No hay conexion a Internet");
            selectInfo();
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        main.loadItems();
    }

    private String checkUrlIcon(String url, String url2, String defaultUrl) {
        String resp = url;
        if ("null".equals(url) || url.isEmpty()) {
            resp = url2;
        }
        if ("null".equals(url2) || url2.isEmpty()) {
            resp = defaultUrl;
        }
        return resp;
    }

    private String checkUrlBanner(String url, String defaultUrl) {
        String resp = url;
        if ("null".equals(url) || url.isEmpty()) {
            resp = defaultUrl;
        }
        return resp;
    }

    private void downloadInfo() {

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

                for (int i = 0; i < children.length() && pDialog != null; i++) {
                    JSONObject c = children.getJSONObject(i).getJSONObject("data");

                    String display_name = c.getString("display_name");

                    String display_icon = c.getString("icon_img");
                    String other_display_icon = c.getString("header_img");
                    String default_display_icon = main.getResources().getString(R.string.default_url_img);
                    String icon = checkUrlIcon(display_icon, other_display_icon, default_display_icon);

                    String info_description = c.getString("public_description");
                    String title = c.getString("title");
                    String url_banner_img = c.getString("banner_img");
                    String default_banner = main.getResources().getString(R.string.default_url_banner_img);
                    String banner = checkUrlBanner(url_banner_img, default_banner);

                    Drawable image_item = sh.downloadImage(icon);
                    Bitmap imageIcon = drawableToBitmap(image_item);

                    Drawable image_banner = sh.downloadImage(banner);
                    Bitmap imageBanner = drawableToBitmap(image_banner);

                    HashMap<String, Object> itemObject = new HashMap<>();
                    itemObject.put("display_name", display_name);
                    itemObject.put("display_icon", imageIcon);
                    itemObject.put("public_description", info_description);
                    itemObject.put("url_banner", imageBanner);
                    itemObject.put("title_info", title);
                    infoList.add(itemObject);

                    RedditInfoDB item = new RedditInfoDB();
                    item.setId(Integer.toString(i));
                    item.setName(display_name);
                    item.setIcon(imageIcon);
                    item.setDescription(info_description);
                    item.setTitle(title);
                    item.setBanner(imageBanner);
                    DBHandler db = new DBHandler(main);
                    db.insert(item);

                    publishProgress();
                }

            } catch (final JSONException e) {
                Log.e(TAG, "Json parsing error: " + e.getMessage());
            }
        } else {
            Log.e(TAG, "Couldn't get json from server.");
        }
    }

    private void selectInfo() {
        DBHandler db = new DBHandler(main);
        ArrayList<RedditInfoDB> read = db.read();

        for (RedditInfoDB tmp : read) {
            HashMap<String, Object> itemObject = new HashMap<>();
            itemObject.put("display_name", tmp.getName());
            itemObject.put("display_icon", tmp.getIcon());
            itemObject.put("public_description", tmp.getDescription());
            itemObject.put("url_banner", tmp.getBanner());
            itemObject.put("title_info", tmp.getTitle());
            infoList.add(itemObject);
            publishProgress();
        }
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
