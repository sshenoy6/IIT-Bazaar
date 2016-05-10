package edu.iit.cs442.team7.iitbazaar.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.common.BitMatrix;

import java.text.SimpleDateFormat;

import edu.iit.cs442.team7.iitbazaar.BazaarActivity;
import edu.iit.cs442.team7.iitbazaar.IITBazaar;
import edu.iit.cs442.team7.iitbazaar.Item;
import edu.iit.cs442.team7.iitbazaar.R;
import edu.iit.cs442.team7.iitbazaar.common.QRCodeRasterWriter;


/**
 * @author <a href="mailto:jnosek@hawk.iit.edu">Janusz M. Nosek</a>
 */


public class ItemDescriptionContentFragment extends Fragment {
    private BazaarActivity parentActivity;



    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = (View)inflater.inflate(R.layout.item_description_content_fragment, container, false);



        Item item = IITBazaar.getCurrentItem();
        if (null != item) {

            TextView title = (TextView)view.findViewById(R.id.item_title);
            title.setText(item.getTitle());

            ImageView itemPicture = (ImageView)view.findViewById(R.id.item_picture);
            itemPicture.setImageBitmap(item.getImageBitmap());

            TextView itemPrice = (TextView)view.findViewById(R.id.item_price);
            itemPrice.setText("Price $" + item.getPrice());

            TextView itemEnd = (TextView)view.findViewById(R.id.item_endtime);
            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
            itemEnd.setText("Ending "+df.format(item.getListingEndDate()));

            TextView itemDescription = (TextView)view.findViewById(R.id.item_description);
            itemDescription.setText("Item Description\n"+item.getItemDescription());

            TextView itemCategory = (TextView)view.findViewById(R.id.item_category);
            itemCategory.setText(item.getCategory());

            //for now
            TextView itemSpecification = (TextView)view.findViewById(R.id.item_specifications);
            itemSpecification.setText("Item #"+item.getItemNumber());

            TextView itemSeller = (TextView)view.findViewById(R.id.item_seller_name);
            itemSeller.setText(item.getListingUserEmail());



            com.google.zxing.qrcode.encoder.QRCode qrCode = item.getQRCode();
            QRCodeRasterWriter qrw = new QRCodeRasterWriter();


            BitMatrix qrBitMatrix = qrw.renderResult(qrCode, 500, 500, qrw.getQuietZoneSize(null));

            Bitmap qrBitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);


           // int black = parentActivity.getColor(R.color.iit_red);
            //int black = 0xCF102D;
         //   Log.d("IITRed",black+"");
          //  int white = parentActivity.getColor(R.color.default_background);
           // Log.d("IITWhite",white+"");

                //    ?android:colorBackground

            for (int i = 0; i < 500; i++) {//width
                for (int j = 0; j < 500; j++) {//height
                  //  qrBitmap.setPixel(i, j, qrBitMatrix.get(i, j) ? black : white);
                    qrBitmap.setPixel(i, j, qrBitMatrix.get(i, j) ? Color.BLACK : Color.WHITE);
                }
            }

            ImageView qrCodeView = (ImageView)view.findViewById(R.id.item_qrcode);
            qrCodeView.setImageBitmap(qrBitmap);

        }
        else{
            Log.e(this.getClass().getSimpleName(),"Item is null");
        }

        // Inflate the layout for this fragment
        return view;

    }



    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity){
            parentActivity=(BazaarActivity) context;
            Log.d(this.getClass().getSimpleName(), "Activity context set correctly");
        } else {
            Log.e(this.getClass().getSimpleName(), "Attached context of " + this.getClass().getSimpleName() + " is not of activity type.");
        }

    }
}


