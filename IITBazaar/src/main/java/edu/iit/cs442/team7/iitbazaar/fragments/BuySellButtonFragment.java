package edu.iit.cs442.team7.iitbazaar.fragments;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import edu.iit.cs442.team7.iitbazaar.BazaarActivity;
import edu.iit.cs442.team7.iitbazaar.MenuKeys;
import edu.iit.cs442.team7.iitbazaar.R;


/**
 * @author <a href="mailto:jnosek@hawk.iit.edu">Janusz M. Nosek</a>
 */


public class BuySellButtonFragment extends Fragment {

    private View view;

    private BazaarActivity parentActivity;


    private RadioGroup buySellMenuRadioGroup;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.buysellfragment, container, false);
        buySellMenuRadioGroup = (RadioGroup) view.findViewById(R.id.buy_sell_radio_group);


        buySellMenuRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if(checkedId == R.id.buy_button) {


                    parentActivity.menuSelected(MenuKeys.BUY);
                    //Toast.makeText(view.getContext(), "choice: BUY",
                      //      Toast.LENGTH_SHORT).show();


                } else if(checkedId == R.id.sell_button) {

                    parentActivity.menuSelected(MenuKeys.SELL);
                  //  Toast.makeText(view.getContext(), "choice: SELL",
                    //        Toast.LENGTH_SHORT).show();
                }
            }

        });



        buySellMenuRadioGroup.check(R.id.buy_button);

        parentActivity.registerRadioGroupListener(buySellMenuRadioGroup);



        return view;



    }


    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity){
            parentActivity=(BazaarActivity) context;
            parentActivity.registerRadioButtons(this);


            Log.d(this.getClass().getSimpleName(), "Activity context set correctly");
        }
        else{
            Log.e(this.getClass().getSimpleName(),"Attached context of "+this.getClass().getSimpleName()+" is not of activity type.");
        }

    }

    public void hideRadioButtons() {
        buySellMenuRadioGroup.setVisibility(View.INVISIBLE);
    }

    public void showRadioButtons() {
        buySellMenuRadioGroup.setVisibility(View.VISIBLE);
    }



}


