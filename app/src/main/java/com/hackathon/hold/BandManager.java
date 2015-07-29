package com.hackathon.hold;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

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
import com.microsoft.band.tiles.pages.ElementColorSource;
import com.microsoft.band.tiles.pages.FilledButton;
import com.microsoft.band.tiles.pages.FilledButtonData;
import com.microsoft.band.tiles.pages.FlowPanel;
import com.microsoft.band.tiles.pages.FlowPanelOrientation;
import com.microsoft.band.tiles.pages.PageData;
import com.microsoft.band.tiles.pages.PageLayout;
import com.microsoft.band.tiles.pages.TextBlock;
import com.microsoft.band.tiles.pages.TextBlockData;
import com.microsoft.band.tiles.pages.TextBlockFont;
import com.microsoft.band.tiles.pages.TextButton;
import com.microsoft.band.tiles.pages.TextButtonData;

import java.util.List;
import java.util.UUID;

/**
 * Created by nick on 7/28/15.
 */
public class BandManager {

    private Activity mActivity;
    private BandClient client = null;

    private static final UUID tileId = UUID.fromString("cc0D508F-70A3-47D4-BBA3-812BADB1F8Aa");
    private static final UUID pageId1 = UUID.fromString("b1234567-89ab-cdef-0123-456789abcd00");
    private static final UUID pageId2 = UUID.fromString("b1234567-89ab-cdef-0123-456789abcd01");

    private enum VIEW_ID
    {
        EMERGENCY(1),
        PULSE(2),
        START_TITLE(3),
        START_MESSAGE(4);

        public int id;
        VIEW_ID(int id)
        {
            this.id = id;
        }
        public int getId()
        {
            return id;
        }
    };


    public BandManager(Activity activity)
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

        PageLayout startLayout = createStartLayout();
        PageLayout actionLayout = createActionLayout();


        BandTile tile = new BandTile.Builder(tileId, "Button Tile", tileIcon)
                .setPageLayouts(createStartLayout(), createActionLayout())
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

    private PageLayout createStartLayout() {

        return new PageLayout(

                new FlowPanel(40, 0, 245, 100, FlowPanelOrientation.VERTICAL)
                        .addElements(

                                new TextBlock(0, 5, 200, 30, TextBlockFont.SMALL)
                                        .setMargins(0, 10, 0, 0)
                                        .setId(VIEW_ID.START_TITLE.getId())
                                        .setColor(Color.WHITE),

                                new TextBlock(0, 30, 200, 30, TextBlockFont.MEDIUM)
                                        .setMargins(0, 0, 0, 0)
                                        .setId(VIEW_ID.START_MESSAGE.getId())
                                        .setColorSource(ElementColorSource.TILE_BASE)
                        )
        );
    }

    private PageLayout createActionLayout() {

        return new PageLayout(
                new FlowPanel(0, 0, 300, 100, FlowPanelOrientation.HORIZONTAL)
                    .addElements(

                            new TextButton(0, 0, 160, 100)
                                    .setMargins(0, 0, 0, 0)
                                    .setId(VIEW_ID.PULSE.getId())
                                    .setPressedColor(Color.YELLOW),

                            new FilledButton(0, 0, 180, 100)
                                .setMargins(0, 0, 0, 0)
                                .setId(VIEW_ID.EMERGENCY.getId())
                                .setBackgroundColor(Color.RED)
                    )
        );
    }

    private void updatePages() throws BandIOException {

            client.getTileManager().setPages(tileId,
                    getPageData(1), getPageData(2));
            Log.d("band_console", "Send button page data to tile page \n\n");
    }

    private PageData getPageData(int page)
    {
        if (page == 1)
        {
            return new PageData(pageId2, 1)
                    .update(new FilledButtonData(VIEW_ID.EMERGENCY.getId(), Color.WHITE))
                    .update(new TextButtonData(VIEW_ID.PULSE.getId(), "Pulsing!"));
        }
        else if (page == 2)
        {
            return new PageData(pageId1, 0)
                    .update(new TextBlockData(VIEW_ID.START_TITLE.getId(), "swipe to"))
                    .update(new TextBlockData(VIEW_ID.START_MESSAGE.getId(), "Start"));
        }

        return null;
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

    public void onActionTileOpened(Intent intent)
    {
        TileEvent tileOpenData = intent.getParcelableExtra(TileEvent.TILE_EVENT_DATA);
        Log.d("band_console", "Tile open event received\n" + tileOpenData.toString() + "\n\n");
    }

    public void onActionTileButtonPressed(Intent intent)
    {
        TileButtonEvent buttonData = intent.getParcelableExtra(TileEvent.TILE_EVENT_DATA);
        if(buttonData.getElementID() == VIEW_ID.EMERGENCY.getId()) {
            Log.d("band_console", "Emergency button pressed.\n");
        }
        else if(buttonData.getElementID() == VIEW_ID.PULSE.getId())
        {
            Log.d("band_console", "Pulse sent.\n");
            //notify that friends are being contacted

        }
    }

    public void onActionTileClosed(Intent intent)
    {
        TileEvent tileCloseData = intent.getParcelableExtra(TileEvent.TILE_EVENT_DATA);
        Log.d("band_console", "Tile close event received\n" + tileCloseData.toString() + "\n\n");
    }
}
