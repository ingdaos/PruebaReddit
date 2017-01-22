package prueba.pruebareddit;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import prueba.comunication.DownloadImage;
import prueba.comunication.IDownloadImage;

public class InfoActivity extends AppCompatActivity implements IDownloadImage {

    private ImageView iv;
    private TextView infoTitle;
    private TextView infoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        iv = (ImageView) findViewById(R.id.imageBanner);
        infoTitle = (TextView) findViewById(R.id.titleText);
        infoText = (TextView) findViewById(R.id.infoText);

        Bundle extraInfo = getIntent().getExtras();
        String url_image = (String) extraInfo.get("url_banner");
        String title = extraInfo.getString("title");
        String description = extraInfo.getString("description");

        if ("null".equals(url_image) || url_image.isEmpty()) {
            url_image = getResources().getString(R.string.default_url_banner_img);
        }

        infoTitle.setText(title);
        infoText.setText(description);

        DownloadImage process = new DownloadImage(this, url_image);
        process.execute();
    }

    @Override
    public void finishDownload(Bitmap image) {
        iv.setImageBitmap(image);
    }

}
