package com.ronyonatan.foodlocater;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.orm.SugarContext;


public class MainActivity extends AppCompatActivity  {

    CheckCharging myBatteryReciver;
    RelativeLayout activty_mainFrag;
    CheckCharging charging;
   // ConstraintLayout Main;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // Main=(ConstraintLayout)findViewById(R.id.Main);
        activty_mainFrag = (RelativeLayout) findViewById(R.id.activty_mainFrag);
        MainFragA mainFragment = new MainFragA();
        getFragmentManager().beginTransaction().replace(R.id.activty_mainFrag, mainFragment).commit();

  //  charging= new CheckCharging();
        SugarContext.init(getApplicationContext());
   //     IntentFilter myFilter= new IntentFilter("android.intent.action.ACTION_POWER_CONNECTED");
    //    registerReceiver(charging,myFilter );
         myBatteryReciver= new CheckCharging();




    }

    class CheckCharging extends BroadcastReceiver {

       // BroadcastReceiver receiver= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {



            if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
                Toast.makeText(context, "The device is charging", Toast.LENGTH_SHORT).show();
            } else {
                intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED);
                Toast.makeText(context, "The device is not charging", Toast.LENGTH_SHORT).show();
            }
        }






          /*
            int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
            boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
            boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
            if (usbCharge || acCharge)
            {
                Toast.makeText(context, "plugged in", Toast.LENGTH_SHORT).show();


            }else
            {

                Toast.makeText(context, "not charging", Toast.LENGTH_SHORT).show();

            }*/


        }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.favo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(myBatteryReciver);
       // unregisterReceiver(charging);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //receiver = new PowerConnectionReceiver();

        IntentFilter ifilter = new IntentFilter();
        ifilter.addAction(Intent.ACTION_POWER_CONNECTED);
        ifilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        registerReceiver(myBatteryReciver, ifilter);
    }
}
