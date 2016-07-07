package jp.co.ctc_g.baggagetracking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<History> histories = new ArrayList<>();
        histories.add(new History(
                "Claimed",
                "Japan International Airport\nTerminal #1\n#Belt 1\n2016/07/06 16:50"));
        histories.add(new History(
                "On Claim Belt",
                "Japan International Airport\nTerminal #1\n#Belt 1\n2016/07/06 16:35"));

        HistoryAdapter adapter = new HistoryAdapter(this, 0, histories);

        ListView listView = (ListView) findViewById(R.id.history_listview);
        listView.setAdapter(adapter);
    }

    public class HistoryAdapter extends ArrayAdapter<History> {
        private LayoutInflater layoutInflater;

        public HistoryAdapter(Context c, int id, ArrayList<History> histories) {
            super(c, id, histories);
            this.layoutInflater = (LayoutInflater) c.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE
            );
        }

        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = layoutInflater.inflate(
                        R.layout.list_item,
                        parent,
                        false
                );
                holder = new ViewHolder();
                holder.status = (TextView) convertView.findViewById(R.id.status);
                holder.location = (TextView) convertView.findViewById(R.id.location);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            History history = getItem(pos);
            holder.status.setText(history.getStatus());
            holder.location.setText(history.getLocation());

            return convertView;
        }
    }

    static class ViewHolder {
        TextView status;
        TextView location;
    }

    public class History {
        private String status;
        private String location;

        public History(String status, String location) {
            this.status = status;
            this.location = location;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }
    }

    public void jumpToLoginActivity(View view) {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

}
