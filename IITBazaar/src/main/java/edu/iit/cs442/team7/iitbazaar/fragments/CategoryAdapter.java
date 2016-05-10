/*
* Copyright (C) 2014 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package edu.iit.cs442.team7.iitbazaar.fragments;



import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import edu.iit.cs442.team7.iitbazaar.BazaarActivity;
import edu.iit.cs442.team7.iitbazaar.CursorAdapter;
import edu.iit.cs442.team7.iitbazaar.R;


/**
 * @author <a href="mailto:jnosek@hawk.iit.edu">Janusz M. Nosek</a>
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private static final String TAG = "CategoryAdapter";

    private boolean mDataValid;


    private LayoutInflater inflater;
    private BazaarActivity activity;
    private Cursor mCursor;
    private List<String> mDataSet;


    /**
     * Initialize the dataset of the Adapter.
     *
     * @param categories of strings - containing the data to populate views to be used by RecyclerView.
     */


    public CategoryAdapter(BazaarActivity activity, List<String> categories) {
        this.activity = activity;
        mDataSet = categories;
        /*boolean cursorPresent = cursor != null;
        mCursor = cursor;
        mDataValid = cursorPresent;*/
        Log.i("N local db items", getItemCount() + "");
    }

    // BEGIN_INCLUDE(recyclerViewSampleViewHolder)
    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView categoryName;

        public ViewHolder(View v) {
            super(v);
            categoryName = (TextView) v.findViewById(R.id.buy_category_text);
            // Define click listener for the ViewHolder's View.
        //    v.setOnClickListener(new View.OnClickListener() {
         //       @Override
          //      public void onClick(View v) {
            //        Log.d(TAG, "Element " + getPosition() + " clicked.");
 //                   Toast.makeText(IITBazaar.getAppContext(), "Element " + getPosition() + " clicked.", Toast.LENGTH_SHORT).show();


//                }
  //          });
    //        textView = (TextView) v.findViewById(R.id.textView);
        }
        public void setCategoryName(String category) {
            this.categoryName.setText(category);
        }

      //  public TextView getTextView() {
        //    return textView;
        //}
    }

    // END_INCLUDE(recyclerViewSampleViewHolder)



    // BEGIN_INCLUDE(recyclerViewOnCreateViewHolder)
    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.category_fragment, viewGroup, false);

        return new ViewHolder(v);
    }
    // END_INCLUDE(recyclerViewOnCreateViewHolder)

    // BEGIN_INCLUDE(recyclerViewOnBindViewHolder)
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set.");


       // mCursor.moveToPosition(position);

        viewHolder.setCategoryName(mDataSet.get(position));

        viewHolder.categoryName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.itemSelected(position);
            }
        });





        // Get element from your dataset at this position and replace the contents of the view
        // with that element
     //   viewHolder.getTextView().setText(mDataSet[position]);
    }
    // END_INCLUDE(recyclerViewOnBindViewHolder)

    // Return the size of your dataset (invoked by the layout manager)
    /*@Override
    public int getItemCount() {
        return mDataSet.size();
    }



    public void updateList(List<String> data){
        mDataSet = data;
        notifyDataSetChanged();
    }
    public void addCategory(int position, String category){
        mDataSet.add(position,category);
        notifyItemInserted(position);
    }
    public void deleteCategory(int position){
        mDataSet.remove(position);
        notifyItemRemoved(position);
    }
*/
   /* @Override
    public void notifyCursorChanged(Cursor cursor) {
        Cursor old = swapCursor(cursor);
        if (old != null) {
            old.close();
        }
    }

    private Cursor swapCursor(Cursor cursor) {
        if (mCursor == cursor) {
            return null;
        }
        Cursor oldCursor = mCursor;
        this.mCursor = cursor;
        if (cursor != null) {
            this.notifyDataSetChanged();
        }
        return oldCursor;
    }*/
    public void addCategory(int position, String category){
        mDataSet.add(position,category);
        notifyItemInserted(position);
    }
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
    /*Swathi Shenoy: To fix repetition of items in list*/
    @Override
    public long getItemId(int position){
        return position;
    }
}
