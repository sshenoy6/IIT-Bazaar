package edu.iit.cs442.team7.iitbazaar.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.Pair;
import android.text.InputFilter;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import edu.iit.cs442.team7.iitbazaar.BazaarActivity;
import edu.iit.cs442.team7.iitbazaar.IITBazaar;
import edu.iit.cs442.team7.iitbazaar.Item;
import edu.iit.cs442.team7.iitbazaar.ItemNumberListener;
import edu.iit.cs442.team7.iitbazaar.MoneyValueFilter;
import edu.iit.cs442.team7.iitbazaar.PhotoListener;
import edu.iit.cs442.team7.iitbazaar.R;
import edu.iit.cs442.team7.iitbazaar.common.PhotoLibrary;


/**
 * @author <a href="mailto:jnosek@hawk.iit.edu">Janusz M. Nosek</a>
 */


public class ItemEntryFragment extends Fragment implements PhotoListener, ItemNumberListener {


    private View view;
    private BazaarActivity parentActivity;
    private TextView titleView;
    private Button addPhotoButton;
    private Button removePhotoButton;
    private EditText priceInput;
    private EditText endTime;
    private Button categoryButton;
    private EditText descriptionInput;
    private Button addSpecButton;
    private Button removeSpecButton;
    private Button listItemButton;

    private PhotoLibrary pl;

    private boolean waitingFOrItemNumer = false;



    private Item newItem;


    //make endtime 23:59
    private DatePicker endDatePicker;

    private SimpleDateFormat dateFormatter;

    private ItemEntryFragment itemEntryFragment;

    final AlertDialog[] alertDialog = new AlertDialog[1];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.item_entry_fragment, container, false);

        itemEntryFragment = this;

        titleView = (TextView) view.findViewById(R.id.item_entry_title);


        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);


        priceInput = (EditText) view.findViewById(R.id.item_entry_price);
        priceInput.setFilters(new InputFilter[] { new MoneyValueFilter() });


        newItem = IITBazaar.getCurrentItem();

        titleView.setText(newItem.getTitle());
        newItem.setCategory("none");



        final String email = IITBazaar.getCurrentUser().getEmail();



        parentActivity.registerPhotoListener(this);

        parentActivity.registerItemNumberListener(this);

        addPhotoButton = (Button) view.findViewById(R.id.item_entry_photo_add_button);



        addPhotoButton.setOnClickListener(new Button.OnClickListener() {



            public void onClick(View v) {


                final FragmentManager fm = getFragmentManager();
                PictureEntryDialogBoxFragment dialogFragment;
                if ( null==(dialogFragment= (PictureEntryDialogBoxFragment) fm.findFragmentByTag("PictureEntry"))){
                    dialogFragment = new PictureEntryDialogBoxFragment();

                }

                dialogFragment.show(fm, "PictureEntry");
            }


        });


    listItemButton = (Button) view.findViewById(R.id.item_entry_list_button);

        listItemButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {



                //Collect entered data
                endDatePicker = (DatePicker) view.findViewById(R.id.item_entry_end_time);

                int day = endDatePicker.getDayOfMonth();
                int month = endDatePicker.getMonth();
                int year = endDatePicker.getYear();


                Calendar endcalendar = Calendar.getInstance();
                endcalendar.setTimeZone(TimeZone.getTimeZone("America/Chicago"));
                endcalendar.set(Calendar.DAY_OF_MONTH, day);
                endcalendar.set(Calendar.MONTH, month);
                endcalendar.set(Calendar.YEAR, year);
                endcalendar.set(Calendar.HOUR_OF_DAY, 23);
                endcalendar.set(Calendar.MINUTE, 59);
                endcalendar.set(Calendar.SECOND, 59);


                descriptionInput = (EditText) view.findViewById(R.id.item_entry_description);
                String descriptionString = descriptionInput.getText().toString();


                String priceString = priceInput.getText().toString();

                //Validate entered data


                Calendar inCalendar = Calendar.getInstance();
                inCalendar.setTimeZone(TimeZone.getTimeZone("America/Chicago"));
                inCalendar.set(Calendar.HOUR_OF_DAY, 0);
                inCalendar.set(Calendar.MINUTE, 0);
                inCalendar.set(Calendar.SECOND, 0);
                inCalendar.set(Calendar.MILLISECOND, 0);


                if (descriptionString != null && !descriptionString.isEmpty()) {
                    if (validatePriceString(priceString)) {
                        if (newItem.getImageByte() != null
                                || newItem.getImageBitmap() != null) {
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                            Date today = Calendar.getInstance().getTime();
                            String todayString = df.format(today);
                            newItem.setListingStartDate(inCalendar.getTime());
                            newItem.setListingEndDate(endcalendar.getTime());
                            newItem.setItemDescription(descriptionString);
                            newItem.setPrice(priceString);
                            newItem.setListingUserEmail(email);


                            Log.d("ItemEntry", "Persisting item");

                            waitingFOrItemNumer = true;
                            parentActivity.listItemRemotely(newItem);

                            Log.d("ItemEntry", "Persisted item?");

                        } else {
                            parentActivity.showImageError();
                        }
                    } else {
                        parentActivity.showNonValidPriceError();
                    }
                } else {
                    parentActivity.showEnterDescriptionError();
                }






            }
        });









        return view;
    }





    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity){
            parentActivity=(BazaarActivity) context;
            pl = new PhotoLibrary(parentActivity);
            Log.d(this.getClass().getSimpleName(), "Activity context set correctly");
        } else {
            Log.e(this.getClass().getSimpleName(), "Attached context of " + this.getClass().getSimpleName() + " is not of activity type.");
        }

    }


    private boolean validateRawDate (String rawDate) {
        if (rawDate!=null && !rawDate.isEmpty()) {
            String pattern = "\\d{4}-\\d{2}-\\d{2}";
            if (rawDate.matches(pattern)) {
                String[] decomposedDate = rawDate.split("-");
                int year = Integer.parseInt(decomposedDate[0]);
                int month = Integer.parseInt(decomposedDate[1]);
                int day = Integer.parseInt(decomposedDate[2]);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                try {
                    Date d = sdf.parse(year + "/" + month + "/" + day);
                    Date now = new Date();
                    if (d.compareTo(now)>=0) {
                        return true;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    return false;
                }
            } else {
                Log.i("Date","Error: does not meet the pattern.");
            }
        }
        else {
            Log.i("Date", "Error: Empty or null.");
        }
        return false;
    }

    private boolean validatePriceString (String priceString) {
        if (priceString==null || priceString.isEmpty()) {
            return false;
        }
        else {
            String pattern = "[0-9]+(?:\\.[0-9]+)?";
            return priceString.matches(pattern);
        }
    }


    //called by camera (as opposed to gallery).
    @Override
    public void loadPhotoFromPath(String currentPhotoPath) {

        Pair <Bitmap, Bitmap> fullAndThumb = pl.loadPhotoPairFromPath(currentPhotoPath);

        ImageView iView = (ImageView) view.findViewById(R.id.item_entry_picture);

        iView.setImageBitmap(fullAndThumb.first);

        newItem.setImage(fullAndThumb.first);
        newItem.setThumbnail(fullAndThumb.second);

    }


    //used on gallery
    @Override
    public void loadPhotoFromURI(Uri uriPath) {

        Pair<Bitmap, Bitmap> fullAndThumb = pl.loadPhotoPairFromURI(uriPath);

        ImageView iView = (ImageView) view.findViewById(R.id.item_entry_picture);

        iView.setImageBitmap(fullAndThumb.first);

        newItem.setImage(fullAndThumb.first);
        newItem.setThumbnail(fullAndThumb.second);

    }



    private int calculateInSampleSize(
            int inHeight, int inWidth, int reqWidth, int reqHeight) {
        // Raw height and width of image

        int inSampleSize = 1;

        if (inHeight > reqHeight || inWidth > reqWidth) {

            final int halfHeight = inHeight / 2;
            final int halfWidth = inWidth / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


    private Pair<Integer,Integer> calculateScaledSize(
            int inHeight, int inWidth, int reqEdge) {
        // Raw height and width of image

        int largest_side = Math.max(inHeight,inWidth);
        double normalScaleFactor = ((double)largest_side/(double)reqEdge);

        int scaledW = (int)Math.floor(inWidth/normalScaleFactor);
        int scaledH = (int)Math.floor(inHeight/normalScaleFactor);

        return new Pair<Integer,Integer>(scaledH,scaledW);

    }

    @Override
    public void notifyItemNumber(int itemNumber) {


        if (waitingFOrItemNumer){


            newItem.setItemNumber(itemNumber);

            parentActivity.listItemLocally(newItem);

            Log.w("WaitingForItemNumber", "Got item = " + newItem.toString());

            waitingFOrItemNumer=false;



            parentActivity.displayItemDescriptionBySell();

        }
        else{
            Log.w("WaitingForItemNumber","Not waiting for item number");
        }


    }
}




