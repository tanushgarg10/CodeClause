package com.example.stickynotes;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

// Implementation of App Widget functionality.
class AppWidget extends AppWidgetProvider {
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {

        // There may be multiple
        // widgets active, so update
        // all of them
        for (int appWidgetId : appWidgetIds) {updateAppWidget(context, appWidgetManager, appWidgetId);
            Intent launchIntent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context,0,launchIntent, PendingIntent.FLAG_MUTABLE);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            remoteViews.setOnClickPendingIntent(R.id.idTVWidget,pendingIntent);
            appWidgetManager.updateAppWidget (appWidgetId,remoteViews);
        }
    }

    // Enter relevant functionality for
    // when the first widget is created
    @Override public void onEnabled(Context context)
    {
        super.onEnabled(context);
    }

    // Enter relevant functionality for
    // when the last widget is disabled
    @Override public void onDisabled(Context context)
    {
        super.onDisabled(context);
    }

    private void
    updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId)
    {
        String widgetText = context.getString(R.string.appwidget_text);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}