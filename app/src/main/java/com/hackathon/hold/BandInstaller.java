package com.hackathon.hold;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hold.bandlayoutapp.R;
import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandException;
import com.microsoft.band.BandIOException;
import com.microsoft.band.BandInfo;
import com.microsoft.band.ConnectionState;
import com.microsoft.band.tiles.BandTile;
import com.microsoft.band.tiles.TileButtonEvent;
import com.microsoft.band.tiles.TileEvent;
import com.microsoft.band.tiles.pages.FilledButton;
import com.microsoft.band.tiles.pages.FilledButtonData;
import com.microsoft.band.tiles.pages.FlowPanel;
import com.microsoft.band.tiles.pages.FlowPanelOrientation;
import com.microsoft.band.tiles.pages.PageData;
import com.microsoft.band.tiles.pages.PageLayout;
import com.microsoft.band.tiles.pages.TextButton;
import com.microsoft.band.tiles.pages.TextButtonData;

import java.util.List;
import java.util.UUID;

/**
 * Created by nick on 7/28/15.
 */
public class BandInstaller {

    private Activity mActivity;
    private BandClient client = null;
    private Button btnStart;
    private TextView txtStatus;

    private static final UUID tileId = UUID.fromString("cc0D508F-70A3-47D4-BBA3-812BADB1F8Aa");
    private static final UUID pageId1 = UUID.fromString("b1234567-89ab-cdef-0123-456789abcd00");

    public BandInstaller(Activity activity)
    {
        mActivity = activity;
    }

    public void installApp()
    {
        new appTask().execute();
    }

    private class appTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (getConnectedBandClient()) {
                    Log.d("band_console", "Band is connected.\n");
                    if (addTile()) {
                        updatePages();
                    }
                } else {
                    Log.d("band_console", "Band isn't connected. Please make sure bluetooth is on and the band is in range.\n");
                }
            } catch (BandException e) {
                String exceptionMessage="";
                switch (e.getErrorType()) {
                    case DEVICE_ERROR:
                        exceptionMessage = "Please make sure bluetooth is on and the band is in range.";
                        break;
                    case UNSUPPORTED_SDK_VERSION_ERROR:
                        exceptionMessage = "Microsoft Health BandService doesn't support your SDK Version. Please update to latest SDK.";
                        break;
                    case SERVICE_ERROR:
                        exceptionMessage = "Microsoft Health BandService is not available. Please make sure Microsoft Health is installed and that you have the correct permissions.";
                        break;
                    case BAND_FULL_ERROR:
                        exceptionMessage = "Band is full. Please use Microsoft Health to remove a tile.";
                        break;
                    default:
                        exceptionMessage = "Unknown error occured: " + e.getMessage();
                        break;
                }
                Log.d("band_console", exceptionMessage);

            } catch (Exception e) {
                Log.d("band_console", e.getMessage());
            }
            return null;
        }
    }

    private boolean doesTileExist(List<BandTile> tiles, UUID tileId) {
        for (BandTile tile:tiles) {
            if (tile.getTileId().equals(tileId)) {
                return true;
            }
        }
        return false;
    }

    private boolean addTile() throws Exception {
        if (doesTileExist(client.getTileManager().getTiles().await(), tileId)) {
            return true;
        }

		/* Set the options */
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap tileIcon = BitmapFactory.decodeResource(mActivity.getBaseContext().getResources(), R.raw.b_icon, options);

        BandTile tile = new BandTile.Builder(tileId, "Button Tile", tileIcon)
                .setPageLayouts(createButtonLayout())
                .build();
        Log.d("band_console", "Button Tile is adding ...\n");
        if (client.getTileManager().addTile(mActivity, tile).await()) {
            Log.d("band_console", "Button Tile is added.\n");
            return true;
        } else {
            Log.d("band_console", "Unable to add button tile to the band.\n");
            return false;
        }
    }

    private PageLayout createButtonLayout() {
        return new PageLayout(
                new FlowPanel(15, 0, 260, 120, FlowPanelOrientation.HORIZONTAL)
                        .addElements(new FilledButton(0, 0, 100, 100).setMargins(0, 5, 0 ,0).setId(12).setBackgroundColor(Color.RED))
                        .addElements(new TextButton(0, 0, 100, 100).setMargins(0, 5, 0 ,0).setId(21).setPressedColor(Color.YELLOW))
        );
    }

    private void updatePages() throws BandIOException {
        client.getTileManager().setPages(tileId,
                new PageData(pageId1, 0)
                        .update(new FilledButtonData(12, Color.WHITE))
                        .update(new TextButtonData(21, "Pulse")));
        Log.d("band_console", "Send button page data to tile page \n\n");
    }

    private boolean getConnectedBandClient() throws InterruptedException, BandException {
        if (client == null) {
            BandInfo[] devices = BandClientManager.getInstance().getPairedBands();
            if (devices.length == 0) {
                Log.d("band_console", "Band isn't paired with your phone.\n");
                return false;
            }
            client = BandClientManager.getInstance().create(mActivity.getBaseContext(), devices[0]);
        } else if (ConnectionState.CONNECTED == client.getConnectionState()) {
            return true;
        }

        Log.d("band_console", "Band is connecting...\n");
        return ConnectionState.CONNECTED == client.connect().await();
    }
}