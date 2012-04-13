package com.redinput.wifi;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class WidgetProvider extends AppWidgetProvider {
	public static String EXTRA_WORD = "com.commonsware.android.appwidget.lorem.WORD";

	@Override
	public void onUpdate(Context ctxt, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {

		for (int i = 0; i < appWidgetIds.length; i++) {

			RemoteViews widget = new RemoteViews(ctxt.getPackageName(),
					R.layout.widget);

			Intent it = new Intent(ctxt, WidgetReceiver.class);
			PendingIntent pi = PendingIntent.getActivity(ctxt, 0, it,
					PendingIntent.FLAG_UPDATE_CURRENT);

			widget.setOnClickPendingIntent(R.id.btnWidget, pi);

			appWidgetManager.updateAppWidget(appWidgetIds[i], widget);
		}

		super.onUpdate(ctxt, appWidgetManager, appWidgetIds);
	}
}
