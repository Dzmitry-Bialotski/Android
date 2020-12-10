package by.belotskiy.battleship.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkStateReceiver extends BroadcastReceiver {
    private static boolean isConnected;
    public static boolean IsConnected(){
        return isConnected;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        boolean isConnected = (netInfo != null && netInfo.isConnected());
        NetworkStateReceiver.isConnected = isConnected;
    }
}
