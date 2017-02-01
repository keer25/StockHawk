package widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.TextView;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.sync.QuoteSyncJob;
import com.udacity.stockhawk.ui.MainActivity;;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import timber.log.Timber;

public class WidgetIntentService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        int appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        return new ListRemoteViewsFactory(this.getApplicationContext(), intent);
    }

    class ListRemoteViewsFactory implements RemoteViewsFactory {

        private Context context;
        private int widgetId;

        private DecimalFormat dollarFormatWithPlus;
        private DecimalFormat dollarFormat;
        private DecimalFormat percentageFormat;
        private Cursor cursor;

        public ListRemoteViewsFactory(Context context, Intent intent){

            dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);

            dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
            dollarFormatWithPlus.setPositivePrefix("+$");

            percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
            percentageFormat.setMaximumFractionDigits(2);
            percentageFormat.setMinimumFractionDigits(2);
            percentageFormat.setPositivePrefix("+");

            this.context = context;
            widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        class StockViewHolder  {

            int symbol;

            int price;

            int change;

            StockViewHolder() {
                symbol = R.id.symbol;
                price = R.id.price;
                change = R.id.change;
            }

        }

        @Override
        public void onCreate() {
            Cursor cursor = getContentResolver().query(Contract.Quote.URI,
                    Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}),
                    null, null, Contract.Quote.COLUMN_SYMBOL);

            if (cursor == null){
                Timber.d("Error retrieving database");
            } else {
                this.cursor = cursor;
            }
        }

        @Override
        public void onDataSetChanged() {

        }

        @Override
        public void onDestroy() {
            cursor.close();
        }

        @Override
        public int getCount() {
            return cursor.getCount();
        }

        @Override
        public RemoteViews getViewAt(int i) {
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.list_item_quote);
            StockViewHolder holder = new StockViewHolder();
            cursor.moveToPosition(i);
            rv.setTextViewText(holder.symbol, cursor.getString(Contract.Quote.POSITION_SYMBOL));
            rv.setTextViewText(holder.price, dollarFormat.format(cursor.getFloat(Contract.Quote.POSITION_PRICE)));

            float rawAbsoluteChange = cursor.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE);
            float percentageChange = cursor.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE);

            if (rawAbsoluteChange > 0) {
                rv.setInt(holder.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
            } else {
                rv.setInt(holder.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
            }

            String change = dollarFormatWithPlus.format(rawAbsoluteChange);
            String percentage = percentageFormat.format(percentageChange / 100);

            if (PrefUtils.getDisplayMode(context)
                    .equals(context.getString(R.string.pref_display_mode_absolute_key))) {
                rv.setTextViewText(holder.change, change);
            } else {
                rv.setTextViewText(holder.change, percentage);
            }

            Intent fillIntent = new Intent();
            fillIntent.putExtra(MainActivity.GRAPH_INTENT_EXTRA,
                    cursor.getString(cursor.getColumnIndex(Contract.Quote.COLUMN_HISTORY)));
            rv.setOnClickFillInIntent(holder.symbol, fillIntent );
            return rv;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
