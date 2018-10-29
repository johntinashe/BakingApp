package com.github.johntinashe.bakingapp.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import com.github.johntinashe.bakingapp.R;

/**
 * Implementation of App Widget functionality.
 */
@SuppressWarnings("WeakerAccess")
public class RecipeIngWidget extends AppWidgetProvider {

    private static final String PREFERENCES_WIDGET_TITLE = "TITLE";
    private static final String PREFERENCES_WIDGET_CONTENT = "CONTENT";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_ing_widget);
        SharedPreferences sharedPreferences = context.getSharedPreferences("WIDGET", Context.MODE_PRIVATE);
        views.setTextViewText(R.id.recipe_widget_title, sharedPreferences.getString(PREFERENCES_WIDGET_TITLE, ""));
        views.setTextViewText(R.id.widget_ingredients_container, sharedPreferences.getString(PREFERENCES_WIDGET_CONTENT, ""));

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

