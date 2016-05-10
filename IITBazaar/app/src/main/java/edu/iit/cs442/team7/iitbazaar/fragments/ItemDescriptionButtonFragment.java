package edu.iit.cs442.team7.iitbazaar.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import edu.iit.cs442.team7.iitbazaar.BazaarActivity;
import edu.iit.cs442.team7.iitbazaar.IITBazaar;
import edu.iit.cs442.team7.iitbazaar.Item;
import edu.iit.cs442.team7.iitbazaar.R;
import edu.iit.cs442.team7.iitbazaar.user.User;


/**
 * @author <a href="mailto:jnosek@hawk.iit.edu">Janusz M. Nosek</a>
 */


public class ItemDescriptionButtonFragment extends Fragment {

    private BazaarActivity parentActivity;
    View view;
    boolean followed;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = (View)inflater.inflate(R.layout.item_description_button_fragment, container, false);


        final User user = IITBazaar.getCurrentUser();
        final Item item = IITBazaar.getCurrentItem();

        Button buyButton = (Button)view.findViewById(R.id.buy_button);

        Button shareButton = (Button)view.findViewById(R.id.share_button);

        if (item.getListingUserEmail().equals(user.getEmail())) {
            buyButton.setText("Unlist");

            //confirmation ?
            //asynchronous...
            //set end date to 0

            //Add callback function to
            //close the item description fragment


        }else{



            buyButton.setOnClickListener(new Button.OnClickListener() {



                public void onClick(View v) {





                    final FragmentManager fm = getFragmentManager();
                    BuyDialogBoxFragment dialogFragment;
                    if ( null==(dialogFragment= (BuyDialogBoxFragment) fm.findFragmentByTag("BuyDialogBoxFragment"))){
                        dialogFragment = new BuyDialogBoxFragment();

                    }

                    dialogFragment.show(fm, "BuyDialogBoxFragment");


                }


            });


            //display dialog fragment between paypal and email.
            //Email will launch intent, paypal will launch browser with special link (use any link)



        }
        followed = IITBazaar.getDBController().isInCurrentUserWatchList(item.getItemNumber());
        buildFollowButton();


        shareButton.setOnClickListener(new Button.OnClickListener() {


            public void onClick(View v) {


                User user = IITBazaar.getCurrentUser();
                Item item = IITBazaar.getCurrentItem();

                String emailCC = user.getEmail();


                //need to "send multiple" to get more than one attachment
                final Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                emailIntent.setType("text/plain");

                emailIntent.putExtra(android.content.Intent.EXTRA_CC,
                        new String[]{emailCC});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Sharing IIT BAZAAR #" + item.getItemNumber() + " | " + item.getTitle());


                String share =
                        "Sharing IIT BAZAAR\n\n" +
                                "Item #: " + item.getItemNumber() + "\n\n" +
                                "Title:\n" + item.getTitle() + "\n\n" +
                                "Description:\n" + item.getItemDescription() + "\n\n" +
                                "Category:\n" + item.getCategory() + "\n\n" +
                                "Price:\n" + item.getPrice() + "\n\n" +
                                "End Date:\n" + item.getListingEndDate() + "\n\n";

                emailIntent.putExtra(Intent.EXTRA_TEXT, share);

                File outputDir = IITBazaar.getAppContext().getExternalCacheDir(); // context being the Activity pointer
                File outputFile = null;
                try {
                    outputFile = File.createTempFile("iitbazaar_" + item.getItemNumber(), ".jpg", outputDir);

                    FileOutputStream fos = new FileOutputStream(outputFile.getPath());
                    fos.write(item.getImageByte());
                    fos.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }


                ArrayList<String> filePaths = new ArrayList<String>();
                filePaths.add(outputFile.getPath());

                ArrayList<Uri> uris = new ArrayList<Uri>();
                //convert from paths to Android friendly Parcelable Uri's
                for (String file : filePaths) {
                    File fileIn = new File(file);
                    Uri u = Uri.fromFile(fileIn);
                    uris.add(u);
                }
                emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);

                emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.

                startActivity(emailIntent);
                //startActivityForResult(emailIntent);


            }


        });


        return view;

    }

    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity){
            parentActivity=(BazaarActivity) context;
            parentActivity.registerFollowStatusListeners(this);
            Log.d(this.getClass().getSimpleName(), "Activity context set correctly");
        } else {
            Log.e(this.getClass().getSimpleName(), "Attached context of " + this.getClass().getSimpleName() + " is not of activity type.");
        }

    }

    private void updateFollowState () {
        followed = followed ? false:true;
    }

    public void buildFollowButton() {
        final Item item = IITBazaar.getCurrentItem();
        final Button followButton = (Button) view.findViewById(R.id.follow_button);

        if (followed) {
            followButton.setText("Unfollow");
            followButton.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateFollowState();
                    IITBazaar.getDBController().deleteItemFromCurrentUserWatchList(item.getItemNumber(), parentActivity, parentActivity);
                }
            });
        } else {
            followButton.setText("Follow");
            followButton.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateFollowState();
                    IITBazaar.getDBController().addItemToCurrentUserWatchList(item.getItemNumber(),parentActivity,parentActivity);
                }
            });
        }
    }


    /**
     *
     *
     *
     *     <Button xmlns:android="http://schemas.android.com/apk/res/android"
     android:layout_width="0dp"
     android:layout_weight="1"
     android:layout_height="wrap_content"
     android:text="Follow"
     android:id="@+id/follow_button" />

     <Button xmlns:android="http://schemas.android.com/apk/res/android"
     android:layout_width="0dp"
     android:layout_weight="1"
     android:layout_height="wrap_content"
     android:text="Buy"
     android:id="@+id/buy_button" />

     <Button xmlns:android="http://schemas.android.com/apk/res/android"
     android:layout_width="0dp"
     android:layout_weight="1"
     android:layout_height="wrap_content"
     android:text="Share"
     android:id="@+id/share_button" />
     *
     */

}


