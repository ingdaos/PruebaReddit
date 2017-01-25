package prueba.pruebareddit;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import prueba.database.DBHandler;

public class InfoActivity extends AppCompatActivity {

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
        String title = extraInfo.getString("title");
        String description = extraInfo.getString("description");

        DBHandler db = new DBHandler(this);
        Bitmap url_image = db.getBanner(title);

        infoTitle.setText(title);
        infoText.setText(description);
        iv.setImageBitmap(url_image);
    }

}
