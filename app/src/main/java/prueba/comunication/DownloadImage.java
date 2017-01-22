package prueba.comunication;

import android.graphics.Bitmap;
import android.os.AsyncTask;

/**
 * Created by PERSONAL on 21/01/2017.
 */

public class DownloadImage extends AsyncTask<Object[], Void, Void> {

    private IDownloadImage main;
    private String url;
    private Bitmap imageDownloaded;

    public DownloadImage(IDownloadImage main, String url) {
        this.main = main;
        this.url = url;
    }

    @Override
    protected Void doInBackground(Object[]... params) {
        imageDownloaded = GetInfo.drawableToBitmap(HttpHandler.downloadImage(url));
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        main.finishDownload(imageDownloaded);
    }

}
