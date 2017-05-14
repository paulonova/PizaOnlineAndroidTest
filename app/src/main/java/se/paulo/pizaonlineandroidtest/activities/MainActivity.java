package se.paulo.pizaonlineandroidtest.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import se.paulo.pizaonlineandroidtest.R;
import se.paulo.pizaonlineandroidtest.adapters.DataItemAdapter;
import se.paulo.pizaonlineandroidtest.model.DataItem;
import se.paulo.pizaonlineandroidtest.services.MyService;
import se.paulo.pizaonlineandroidtest.utils.NetworkHelper;
import se.paulo.pizaonlineandroidtest.utils.RequestPackage;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String JSON_URL =  "http://560057.youcanlearnit.net/services/json/itemsfeed.php";


    List<DataItem> mItemList;
    RecyclerView mRecyclerView;
    DataItemAdapter mItemAdapter;
    boolean networkOk;
    RequestPackage requestPackage;



    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            DataItem[] dataItems = (DataItem[]) intent.getParcelableArrayExtra(MyService.MY_SERVICE_PAYLOAD);
            Toast.makeText(MainActivity.this,"Received " + dataItems.length + " items from service", Toast.LENGTH_SHORT).show();
            mItemList = Arrays.asList(dataItems);

            displayDataItems(null);

        }
    };



    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        boolean grid = settings.getBoolean(getString(R.string.pref_display_grid), false);

        mRecyclerView = (RecyclerView) findViewById(R.id.rvItems);
        if (grid) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        }

        networkOk = NetworkHelper.hasNetworkAccess(this);
        if (networkOk) {
            requestPackage = new RequestPackage();
            requestPackage.setEndPoint(JSON_URL);
//            requestPackage.setParam("category", "Desserts");
            requestPackage.setMethod("GET");

            Intent intent = new Intent(this, MyService.class);
            intent.putExtra(MyService.REQUEST_PACKAGE, requestPackage);
            startService(intent);
        } else {
            Toast.makeText(this, "Network not available", Toast.LENGTH_SHORT).show();
        }

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mBroadcastReceiver,
                new IntentFilter(MyService.MY_SERVICE_MESSAGE));

    }

    private void displayDataItems(String category) {
        if (mItemList != null) {
            mItemAdapter = new DataItemAdapter(this, mItemList);
            mRecyclerView.setAdapter(mItemAdapter);
            mItemAdapter.notifyDataSetChanged();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(getApplicationContext())
                .unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_settings:
                // Show the settings screen
                Intent settingsIntent = new Intent(this, PrefsActivity.class);
                startActivity(settingsIntent);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
