package edu.iit.cs442.team7.iitbazaar.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import edu.iit.cs442.team7.iitbazaar.BazaarActivity;
import edu.iit.cs442.team7.iitbazaar.R;


/**
 * @author <a href="mailto:jnosek@hawk.iit.edu">Janusz M. Nosek</a>
 */


public class SellServiceInitEntryFragment extends Fragment {


    private View view;

    private BazaarActivity parentActivity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.sell_service_init_entry_fragment, container, false);



        // Inflate the layout for this fragment

        EditText editText = (EditText) view.findViewById(R.id.sell_service_title);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {

                    Toast.makeText(parentActivity.getBaseContext(), "choice: sellServiceNext",
                            Toast.LENGTH_SHORT).show();
                    //Toast message of somesort

                    return true;
                }
                return false;
            }
        });






        return view;

    }


    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity){
            parentActivity=(BazaarActivity) context;
            Log.d(this.getClass().getSimpleName(), "Activity context set correctly");
        }
        else{
            Log.e(this.getClass().getSimpleName(),"Attached context of "+this.getClass().getSimpleName()+" is not of activity type.");
        }

    }

}


