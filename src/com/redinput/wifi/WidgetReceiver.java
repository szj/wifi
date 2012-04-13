package com.redinput.wifi;

import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.dataframework.DataFramework;
import com.android.dataframework.Entity;

public class WidgetReceiver extends ListActivity {
	List<Entity> listStr;

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.selector_profile);

		if (Build.VERSION.SDK_INT > 10) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.HC_or_ICS)
					.setCancelable(false)
					.setPositiveButton(R.string.yes,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.dismiss();
									WidgetReceiver.this.finish();
								}
							});
			AlertDialog alert = builder.create();
			alert.show();
		}

		if (!isWifiConnected()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.wifi_off)
					.setCancelable(false)
					.setPositiveButton(R.string.yes,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {

									try {
										WifiManager wifiManager = (WifiManager) getBaseContext()
												.getSystemService(
														Context.WIFI_SERVICE);
										wifiManager.setWifiEnabled(true);
									} catch (Exception ex) {
										Toast.makeText(WidgetReceiver.this,
												R.string.error_wifi_on,
												Toast.LENGTH_SHORT).show();
									}
								}
							});
			builder.setNegativeButton(R.string.no,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();

							finish();
						}
					});
			AlertDialog alert = builder.create();
			alert.show();
		}

		try {
			DataFramework.getInstance().open(this, getPackageName());

			listStr = DataFramework.getInstance().getEntityList("profiles");

			Entity e = new Entity("profiles");
			e.setValue("name", "DHCP");
			listStr.add(0, e);

			setListAdapter(new ArrayAdapter<Entity>(this, R.layout.item_list,
					listStr));

		} catch (Exception e) {
			e.printStackTrace();
			finish();
		}

	}

	@Override
	public void onListItemClick(ListView l, View v, int pos, long id) {

		if (pos == 0) {

			android.provider.Settings.System.putString(getContentResolver(),
					android.provider.Settings.System.WIFI_USE_STATIC_IP, "0");

			Toast.makeText(this, "启用配置DHCP", Toast.LENGTH_SHORT)
					.show();

		} else {
			long itemId = listStr.get(pos).getId();

			Entity ent = new Entity("profiles", itemId);

			android.provider.Settings.System.putString(getContentResolver(),
					android.provider.Settings.System.WIFI_USE_STATIC_IP, "1");
			android.provider.Settings.System.putString(getContentResolver(),
					android.provider.Settings.System.WIFI_STATIC_IP,
					ent.getString("ip"));
			android.provider.Settings.System.putString(getContentResolver(),
					android.provider.Settings.System.WIFI_STATIC_GATEWAY,
					ent.getString("gateway"));
			android.provider.Settings.System.putString(getContentResolver(),
					android.provider.Settings.System.WIFI_STATIC_NETMASK,
					ent.getString("netmask"));

			android.provider.Settings.System.putString(getContentResolver(),
					android.provider.Settings.System.WIFI_STATIC_DNS1,
					ent.getString("dns1"));
			android.provider.Settings.System.putString(getContentResolver(),
					android.provider.Settings.System.WIFI_STATIC_DNS2,
					ent.getString("dns2"));

			Toast.makeText(this, "启用配置" + ent.getString("name"),
					Toast.LENGTH_SHORT).show();

		}

		finish();
	}

	private boolean isWifiConnected() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = null;
		if (connectivityManager != null) {
			networkInfo = connectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		}
		return networkInfo == null ? false : networkInfo.isConnected();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		DataFramework.getInstance().close();
	}
}
