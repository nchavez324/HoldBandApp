package com.hackathon.hold;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hold.bandlayoutapp.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class SettingsFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    private MainActivity mMainActivity;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static SettingsFragment newInstance(MainActivity mainActivity) {
        SettingsFragment fragment = new SettingsFragment();
        fragment.setMainActivity(mainActivity);
        return fragment;
    }

    public SettingsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        // auto fill out the items
        ParseUser user = ParseUser.getCurrentUser();


        String imgUrl = "http://www.muttsbetter.com/gallery/lilybefore.JPG";
        try {
            imgUrl = ((ParseFile) user.get("imgFile")).getUrl();
        }
        catch (Exception e) {}
        ImageView i = (ImageView)rootView.findViewById(R.id.settings_imageButton);
        i.setTag(imgUrl);
        new DownloadImagesTask().execute(i);


        try {
            ((EditText) rootView.findViewById(R.id.settings_email)).setText(user.get("email").toString(), TextView.BufferType.EDITABLE);
        } catch( Exception e) {
            ((EditText) rootView.findViewById(R.id.settings_email)).setText("Email", TextView.BufferType.EDITABLE);
        }
        try {
            ((EditText) rootView.findViewById(R.id.settings_name)).setText(user.get("name").toString(), TextView.BufferType.EDITABLE);
        }
        catch (Exception e) {
            ((EditText) rootView.findViewById(R.id.settings_name)).setText("Name", TextView.BufferType.EDITABLE);
        }
        try {
            ((EditText) rootView.findViewById(R.id.settings_phone)).setText(user.get("phone").toString(), TextView.BufferType.EDITABLE);
        }
        catch (Exception e) {
            ((EditText) rootView.findViewById(R.id.settings_phone)).setText("Phone", TextView.BufferType.EDITABLE);
        }




        Button mSaveButton = (Button) rootView.findViewById(R.id.settings_save);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View rootView = (View) view.getParent();
                String name = "";
                String phone ="";
                String email ="";

                try {
                    name = ((EditText) rootView.findViewById(R.id.settings_name)).getText().toString();
                } catch (Exception e) {}

                try {
                    phone = ((EditText) rootView.findViewById(R.id.settings_phone)).getText().toString();
                } catch (Exception e) {}

                try {
                    email = ((EditText) rootView.findViewById(R.id.settings_email)).getText().toString();
                } catch(Exception e) {}

                ParseUser user = ParseUser.getCurrentUser();
                user.put("name", name);
                user.put("phone", phone);
                user.put("email", email);
                user.saveInBackground(new SaveCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(getActivity(),
                                    "Settings saved.", Toast.LENGTH_LONG)
                                    .show();
                        } else {
                            Toast.makeText(getActivity(),
                                    "Error: " + e.toString(), Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                });
            }
        });

        Button mLogoutButton = (Button) rootView.findViewById(R.id.settings_logout);
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logOut();
                Intent intent = new Intent(getActivity(),
                        LoginActivity.class);
                startActivity(intent);
            }
        });

        ImageButton mImageButton = (ImageButton) rootView.findViewById(R.id.settings_imageButton);
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), 1);

            }
        });

        return rootView;
    }


    public class DownloadImagesTask extends AsyncTask<ImageView, Void, Bitmap> {
        ImageView imageView = null;
        @Override
        protected Bitmap doInBackground(ImageView... imageViews) {
            this.imageView = imageViews[0];
            return download_Image((String)imageView.getTag());
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);              // how do I pass a reference to mChart here ?
        }


        private Bitmap download_Image(String src) {
            try {
                Log.e("src",src);
                URL url = new URL(src);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                Log.e("Bitmap","returned");
                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Exception",e.getMessage());
                return null;
            }
        }


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == 1 && resultCode == Activity.RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String imgDecodableString = cursor.getString(columnIndex);
                cursor.close();

                ImageButton i = (ImageButton) getView().findViewById(R.id.settings_imageButton);
                i.setImageBitmap(BitmapFactory
                        .decodeFile(imgDecodableString));

                Toast.makeText(getActivity(),"Pictures are not saved for now!", Toast.LENGTH_LONG)
                        .show();

            } else {
                Toast.makeText(getActivity(),"You did not pick an image.", Toast.LENGTH_LONG)
                        .show();
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(),"Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }

    public void setMainActivity(MainActivity mainActivity)
    {
        mMainActivity = mainActivity;
    }
}