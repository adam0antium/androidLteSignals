
package com.valleydrop.signal1;

import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.CellInfoLte;
import android.telephony.CellSignalStrengthLte;
import android.telephony.TelephonyManager;

//these to try to reproduce data from the Advanced Signal app with weird data.
//to make this possible also need to add read_phone_state to permissions in manifest
import android.telephony.SignalStrength;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.view.View;
import android.widget.TextView;
import java.lang.reflect.*;
import java.util.List;
import android.location.Location;

public class MainActivity extends Activity
{

    private int mInterval = 1000;
    //private Handler mHandler;
    private Context mContext;
    //private boolean isUpdating = false;
    private TelephonyManager telephonyManagerToListen = null;


    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //mHandler= new Handler();
        mContext=this;
        telephonyManagerToListen = (TelephonyManager)this.getSystemService
                (this.TELEPHONY_SERVICE);
        telephonyManagerToListen.listen(mPhoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }


    private PhoneStateListener mPhoneStateListener = new PhoneStateListener()
    {
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            TextView comparisonText = (TextView) findViewById(R.id.textViewComparison);
            comparisonText.setText("Listener to SignalStrength Method: \n" + signalStrength.toString());

            TextView theText = (TextView) findViewById(R.id.textView1);
            TelephonyManager tm = (TelephonyManager) mContext.getSystemService(mContext.TELEPHONY_SERVICE);
            LocationManager lm = (LocationManager) mContext.getSystemService(mContext.LOCATION_SERVICE);
            Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            List<android.telephony.CellInfo> infor = tm.getAllCellInfo();
            for (android.telephony.CellInfo info : infor) {
                if (info instanceof CellInfoLte) {
                    CellSignalStrengthLte ss = ((CellInfoLte) info).getCellSignalStrength();
                    //theButton.setText( ss.toString());

                    Object fieldValueRSRP = null;
                    Object fieldValueRSRQ = null;

                    try {
                        Field privateStringFieldRSRQ = CellSignalStrengthLte.class.getDeclaredField("mRsrq");
                        Field privateStringFieldRSRP = CellSignalStrengthLte.class.getDeclaredField("mRsrp");

                        privateStringFieldRSRQ.setAccessible(true);
                        fieldValueRSRQ = privateStringFieldRSRQ.get(ss);

                        privateStringFieldRSRP.setAccessible(true);
                        fieldValueRSRP = privateStringFieldRSRP.get(ss);
                    } catch (NoSuchFieldException ex) {
                    } catch (IllegalAccessException x) {
                    }
                    String rsrp = Integer.toString((int) fieldValueRSRP);
                    String rsrq = Integer.toString((int) fieldValueRSRQ);

                    theText.setText
                            (
                                "CellSignalStrengthLte Method:"
                                + "\nRSRP: " + rsrp
                                + "\nRSRQ: " + rsrq
                                + "\nAltitude: " + loc.getAltitude()
                            );
                }
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }
/*
    public void buttonClick(View v)
    {
        if(isUpdating==false) {isUpdating=true;}
        else
        {
            isUpdating=false;
        }
        //mStatus.run();
    }
*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

/*
    private Runnable mStatus = new Runnable()
    {
        @Override
        public void run()
        {
            Button theButton = (Button)findViewById(R.id.button);
            while(isUpdating)
            {
                theButton.setText("fff");
                TextView theText = (TextView)findViewById(R.id.textView1);
                TelephonyManager tm = (TelephonyManager)mContext.getSystemService(mContext.TELEPHONY_SERVICE);
                LocationManager lm = (LocationManager)mContext.getSystemService(mContext.LOCATION_SERVICE);
                Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                List<android.telephony.CellInfo> infor = tm.getAllCellInfo();
                for (android.telephony.CellInfo info: infor)
                {
                    if (info instanceof CellInfoLte)
                    {
                        CellSignalStrengthLte ss = ((CellInfoLte) info).getCellSignalStrength();
                        //theButton.setText( ss.toString());

                        Object fieldValue = null;
                        Object fieldValueRSRP = null;
                        Object fieldValueRSSI = null;

                        //PrivateObject privateObject = new PrivateObject("CellSignalStrengthLte");
                        //privateObject = (PrivateObject)((CellInfoLte)info).getCellSignalStrength();
                        try
                        {
                            Field privateStringField = CellSignalStrengthLte.class.getDeclaredField("mRsrq");
                            Field privateStringFieldRSRP = CellSignalStrengthLte.class.getDeclaredField("mRsrp");
                            //Field privateStringFieldRSSI = CellSignalStrengthLte.class.getDeclaredField("mRssi");

                            privateStringField.setAccessible(true);
                            fieldValue = privateStringField.get(ss);

                            privateStringFieldRSRP.setAccessible(true);
                            fieldValueRSRP = privateStringFieldRSRP.get(ss);

                            //privateStringFieldRSSI.setAccessible(true);
                            //fieldValueRSSI = privateStringFieldRSSI.get(ss);

                        }
                        catch (NoSuchFieldException ex)
                        { }
                        catch (IllegalAccessException x)
                        { }
                        String rsrp1 = Integer.toString(ss.getDbm());
                        String rsrp2 = Integer.toString((int)fieldValueRSRP);
                        String rsrq = Integer.toString((int)fieldValue);


                        //String rssi = Integer.toString((int) fieldValueRSSI);

                        String ss2 = Integer.toString(ss.describeContents());
                        //theButton.setText(ss.toString());
                        theButton.setText("üpdating");

                        theText.setText
                            (
                                "RSRP: " + rsrp1
                                + "\nRSRQ: " + rsrq
                                + "\nAltitude: " + loc.getAltitude()
                                + "\nRSRP2: " + rsrp2
                                //+ "\nRSSI: " + rssi
                                + ss.toString()
                            );

                        mHandler.postDelayed(mStatus, mInterval);
                    }
                }

            }
            theButton.setText("stopped");
            return;

        }
    };
*/
}
