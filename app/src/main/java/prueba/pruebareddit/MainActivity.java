package prueba.pruebareddit;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import prueba.comunication.GetInfo;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();

    private ListView lv;
    private String url = "https://www.reddit.com/reddits.json";

    ArrayList<HashMap<String, Object>> infoList;
    private TableHandler listenerRows;
    private GetInfo getInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initApp();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (getInfo.getpDialog() != null && getInfo.getpDialog().isShowing()) {
            getInfo.getpDialog().dismiss();
        }
        getInfo.setpDialog(null);
    }

    private void initApp() {
        infoList = new ArrayList<>();
        lv = (ListView) findViewById(R.id.list);
        listenerRows = new TableHandler(this, lv, infoList);
        getInfo = new GetInfo(this, url, infoList);
        getInfo.execute();
    }

    public void loadItems() {
        ListAdapter adapter = new ExtendedSimpleAdapter(MainActivity.this,
                infoList, R.layout.list_item,
                new String[]{"display_icon", "display_name"},
                new int[]{R.id.imgItem, R.id.titleItem});

        lv.setAdapter(adapter);
        lv.setOnItemClickListener(listenerRows);
    }

}
