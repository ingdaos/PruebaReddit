package prueba.pruebareddit;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by PERSONAL on 21/01/2017.
 */

public class TableHandler implements AdapterView.OnItemClickListener {

    private String TAG = TableHandler.class.getSimpleName();

    private MainActivity main;
    private ListView lv;
    ArrayList<HashMap<String, Object>> infoList;

    public TableHandler(MainActivity main, ListView lv, ArrayList infoList) {
        this.main = main;
        this.lv = lv;
        this.infoList = infoList;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String image = (String) infoList.get(position).get("url_banner");
        String title = (String) infoList.get(position).get("title_info");
        String description = (String) infoList.get(position).get("public_description");

        Intent info = new Intent(main, InfoActivity.class);
        info.putExtra("title", title);
        info.putExtra("description", description);
        info.putExtra("url_banner", image);

        main.startActivity(info);
    }

}
