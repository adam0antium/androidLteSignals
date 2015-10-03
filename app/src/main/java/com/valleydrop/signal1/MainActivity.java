
package com.valleydrop.signal1;

import android.text.SpannableString;
import android.text.style.UnderlineSpan;
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

import android.text.style.UnderlineSpan;
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
        public void onSignalStrengthsChanged(SignalStrength signalStrength)
        {
            super.onSignalStrengthsChanged(signalStrength);
            TextView comparisonText = (TextView) findViewById(R.id.textViewComparison);
            Object ssFieldValueRsrp = null;
            Object ssFieldValueRsrq = null;
            try
            {
                Field privateStringSsFieldRSRQ = SignalStrength.class.getDeclaredField("mLteRsrq");
                Field privateStringSsFieldRSRP = SignalStrength.class.getDeclaredField("mLteRsrp");

                privateStringSsFieldRSRQ.setAccessible(true);
                ssFieldValueRsrq = privateStringSsFieldRSRQ.get(signalStrength);

                privateStringSsFieldRSRP.setAccessible(true);
                ssFieldValueRsrp = privateStringSsFieldRSRP.get(signalStrength);
            }
            catch (NoSuchFieldException ex) {}
            catch (IllegalAccessException x) {}
            String ssRsrp = Integer.toString((int) ssFieldValueRsrp);
            String ssRsrq = Integer.toString((int) ssFieldValueRsrq);

            String headerString = "Info from \"SignalStrength\":";
            SpannableString spannableHeaderString = new SpannableString(headerString);
            spannableHeaderString.setSpan( new UnderlineSpan(), 0, spannableHeaderString.length(), 0);

            comparisonText.setText(spannableHeaderString);
            comparisonText.append
            (
                "\nRSRP: " + ssRsrp
                + "\nRSRQ: " + ssRsrq
            );

            TextView theText = (TextView) findViewById(R.id.textView1);
            TelephonyManager tm = (TelephonyManager) mContext.getSystemService(mContext.TELEPHONY_SERVICE);
            LocationManager lm = (LocationManager) mContext.getSystemService(mContext.LOCATION_SERVICE);
            Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            List<android.telephony.CellInfo> infor = tm.getAllCellInfo();
            for (android.telephony.CellInfo info : infor)
            {
                if (info instanceof CellInfoLte)
                {
                    CellSignalStrengthLte ss = ((CellInfoLte) info).getCellSignalStrength();
                    //theButton.setText( ss.toString());

                    Object fieldValueRSRP = null;
                    Object fieldValueRSRQ = null;

                    try
                    {
                        Field privateStringFieldRSRQ = CellSignalStrengthLte.class.getDeclaredField("mRsrq");
                        Field privateStringFieldRSRP = CellSignalStrengthLte.class.getDeclaredField("mRsrp");

                        privateStringFieldRSRQ.setAccessible(true);
                        fieldValueRSRQ = privateStringFieldRSRQ.get(ss);

                        privateStringFieldRSRP.setAccessible(true);
                        fieldValueRSRP = privateStringFieldRSRP.get(ss);
                    }
                    catch (NoSuchFieldException ex) {}
                    catch (IllegalAccessException x) {}
                    String rsrp = Integer.toString((int) fieldValueRSRP);
                    String rsrq = Integer.toString((int) fieldValueRSRQ);

                    headerString = "Info from \"CellSignalStrengthLte\":";
                    spannableHeaderString = new SpannableString(headerString);
                    spannableHeaderString.setSpan( new UnderlineSpan(), 0, spannableHeaderString.length(), 0);

                    theText.setText
                    (
                        "\nAltitude: " + loc.getAltitude() + "\n\n"
                    );
                    theText.append(spannableHeaderString);
                    theText.append
                    (
                        "\nRSRP: " + rsrp
                        + "\nRSRQ: " + rsrq
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


}
