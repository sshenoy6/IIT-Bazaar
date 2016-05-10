package edu.iit.cs442.team7.iitbazaar;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import edu.iit.cs442.team7.iitbazaar.database.DBController;
import edu.iit.cs442.team7.iitbazaar.database.RemoteUpdateListener;
import edu.iit.cs442.team7.iitbazaar.fragments.BuyFragment;
import edu.iit.cs442.team7.iitbazaar.fragments.BuySellButtonFragment;
import edu.iit.cs442.team7.iitbazaar.fragments.ItemDescriptionButtonFragment;
import edu.iit.cs442.team7.iitbazaar.fragments.ItemDescriptionFragment;
import edu.iit.cs442.team7.iitbazaar.fragments.ItemEntryFragment;
import edu.iit.cs442.team7.iitbazaar.fragments.RecyclerViewCategoryListFragment;
import edu.iit.cs442.team7.iitbazaar.fragments.RecyclerViewItemListFragment;
import edu.iit.cs442.team7.iitbazaar.fragments.RecyclerViewSellingListFragment;
import edu.iit.cs442.team7.iitbazaar.fragments.RecyclerViewWatchlistFragment;
import edu.iit.cs442.team7.iitbazaar.fragments.SellFragment;
import edu.iit.cs442.team7.iitbazaar.fragments.SellItemInitEntryFragment;
import edu.iit.cs442.team7.iitbazaar.fragments.SellNavKeys;
import edu.iit.cs442.team7.iitbazaar.user.User;

/**
 * @author <a href="mailto:jnosek@hawk.iit.edu">Janusz M. Nosek</a>
 */

public class BazaarActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ItemRetrievalListener,
        SellItemUpdateListener,
        RemoteUpdateListener,
        WatchlistUpdateListener,
        FollowStatusListener
{

    private User user;
  //  private DBController controller;


    private String mCurrentPhotoPath;

    private static final int ACTION_TAKE_PHOTO_B = 1;
    private static final int SELECT_PICTURE = 2;


    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";



    private ArrayList<RadioGroup> radioGroups;

    private ItemNumberListener currentItemNumberListener = null;

    private CursorAdapter adapterListener = null;
    private CursorAdapter adapterListenerSell = null;
    private CursorAdapter adapterListenerCategory = null;
    private ItemDescriptionButtonFragment itemDescriptionButtons = null;
    private BuySellButtonFragment buySellButtonFragment = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bazaar);

        Intent intent = getIntent();
        boolean bypass = intent.getBooleanExtra("BYPASS", false);
        DBController controller = IITBazaar.getDBController();

        //Only for test


        user = IITBazaar.getCurrentUser();

        if (bypass) {

            //See the Login activity or IITBazaar class for dbController
            /* Update DB version comment/uncomment if needed
            userController.onUpgrade(userController.getWritableDatabase(),
                    userController.getWritableDatabase().getVersion(),
                    userController.getWritableDatabase().getVersion()+1);
                                                */

            /* Debug Lines */
            ArrayList<User> users = controller.getAllUsers();
            Log.i("BYPASS","Users in database");
            for (User u : users) {
                Log.i("USER","User email: " + u.getEmail());
            }
            /* End of debug lines*/
        }

        //Sets up the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        FrameLayout frameLayout = (FrameLayout)toolbar.findViewById(R.id.toolbar_frame);


        final String appName = getResources().getString(R.string.app_name);

        final String space = getResources().getString(R.string.space);

        final SearchView searchView = (SearchView)frameLayout.findViewById(R.id.action_search);

        final TextView titleTextView = (TextView)frameLayout.findViewById(R.id.toolbar_title);

        /*Swathi Shenoy: To clear title on click of search icon*/
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleTextView.setText(space);
                menuSelected(MenuKeys.BUY);
            }
        });

        titleTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                titleTextView.setText(space);
                searchView.setIconified(false);
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                titleTextView.setText(appName);
                return false;
            }
        });


        final ImageButton refreshButton = (ImageButton)frameLayout.findViewById(R.id.btn_refresh);


        final BazaarActivity baz = this;

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "Refresh Initialized Manually",
                        Toast.LENGTH_SHORT).show();

                DBController controller = IITBazaar.getDBController();
                controller.syncRemoteItems(baz);

            }
        });
        /*Search implementation*/
        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            public boolean onQueryTextChange(String newText) {
                // this is your adapter that will be filtered
                titleTextView.setText(space);
                DBController controller = IITBazaar.getDBController();
                Cursor mCursor = controller.getSearchItems(newText);
                mCursor.moveToFirst();

                adapterListener.notifyCursorChanged(mCursor);
                return false;
            }

            public boolean onQueryTextSubmit(String query) {
                //Here you can get the value "query" which is entered in the search box.
                DBController controller = IITBazaar.getDBController();
                Cursor mCursor = controller.getSearchItems(query);
                mCursor.moveToFirst();

                adapterListener.notifyCursorChanged(mCursor);
                if(mCursor != null) return true;
                else
                    return false;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);

        setSupportActionBar(toolbar);

        //Sets up the drawer layout
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.nav_drawer_layout);







        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_bazaar);


        TextView userName = (TextView) headerView.findViewById(R.id.navpane_name);
        ImageView userImage = (ImageView) headerView.findViewById(R.id.navpane_user_image);

        TextView userMajor = (TextView) headerView.findViewById(R.id.navpane_major);
        TextView userEmail = (TextView) headerView.findViewById(R.id.navpane_email);

        userName.setText(user.getFirstName() + " " + user.getLastName());
        userImage.setImageBitmap(user.getPictureThumbnailBitmap());

        userMajor.setText(user.getMajor_department());
        userEmail.setText(user.getEmail());




        //probably want a different listener than this
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            RecyclerViewItemListFragment fragListFragment = new RecyclerViewItemListFragment();

            BuyFragment buyFragment = new BuyFragment();

            transaction.replace(R.id.buy_sell_subfragment, buyFragment);
            transaction.addToBackStack("buy_sell_subfragment");
            transaction.replace(R.id.buy_list_fragment, fragListFragment);
            transaction.addToBackStack("buy_list_fragment");
            transaction.commit();


            //ending button registration
            //categories button registration
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.nav_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

//        MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.options_menu, menu);
        //  inflater.inflate(R.menu.bazaar, menu);

        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

            menuSelected(MenuKeys.HOME);

        } else if (id == R.id.nav_following) {

            menuSelected(MenuKeys.WATCH_LIST);

        } else if (id == R.id.nav_logout) {

            IITBazaar.signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);

     //   } else if (id == R.id.nav_help) {

        } else if (id == R.id.nav_disconnect) {

            IITBazaar.signOutAndDisconnect();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }else if (id == R.id.nav_scan_barcode) {


            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.initiateScan();

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.nav_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void registerRadioButtons (BuySellButtonFragment buySellButtonFragment) {
        this.buySellButtonFragment = buySellButtonFragment;
    }

    public void hideRadioButtons () {
        if (null!=buySellButtonFragment) {
            buySellButtonFragment.hideRadioButtons();
        }
    }

    public void showRadioButtons() {
        if (null!=buySellButtonFragment) {
            buySellButtonFragment.showRadioButtons();
        }
    }

    public void menuSelected(MenuKeys key) {

    //FIXME:this might be triggered twice....

        switch (key) {

            case HOME:
                showRadioButtons();

            case BUY:

                Toast.makeText(getBaseContext(), "choice: BUY",
                        Toast.LENGTH_SHORT).show();


            {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                BuyFragment buyFragment = new BuyFragment();

                transaction.replace(R.id.buy_sell_subfragment, buyFragment);
                transaction.addToBackStack("buyFragment");
                transaction.commit();
            }


            case BUY_ENDING: {
                Toast.makeText(getBaseContext(), "choice: ending",
                        Toast.LENGTH_SHORT).show();

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                RecyclerViewItemListFragment buyEndingListFragment = new RecyclerViewItemListFragment();

                //pass the db list to the fragment somehow

                transaction.replace(R.id.buy_list_fragment, buyEndingListFragment);
                transaction.addToBackStack("buyEndingListFragment");
                transaction.commit();


            }
            break;


            case SELL:
                Toast.makeText(getBaseContext(), "choice: SELLING",
                        Toast.LENGTH_SHORT).show();
            {

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                SellFragment sellFragment = new SellFragment();


                transaction.replace(R.id.buy_sell_subfragment, sellFragment);
                transaction.addToBackStack("sellFragment");
                transaction.commit();

            }



            case SELL_ITEM:

            {



                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                SellItemInitEntryFragment sellItemInitEntryFragment = new SellItemInitEntryFragment();


                //pass the db list to the fragment somehow
                transaction.replace(R.id.sell_content_fragment, sellItemInitEntryFragment);
                transaction.addToBackStack("sellItemInitEntryFragment");
                transaction.commit();


            }

            break;



            case SELLING:

            {




             //   Toast.makeText(getBaseContext(), "choice: sell_list",
               //         Toast.LENGTH_SHORT).show();


                Log.d("Selling","Launched!");

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                RecyclerViewSellingListFragment sellListFragment = new RecyclerViewSellingListFragment();

                //pass the db list to the fragment somehow

                transaction.replace(R.id.sell_content_fragment, sellListFragment);
                transaction.addToBackStack("sellListFragment");
                transaction.commit();


            }

            break;






            case BUY_CATEGORIES:
                Toast.makeText(getBaseContext(), "choice: cate",
                        Toast.LENGTH_SHORT).show();

            {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                RecyclerViewCategoryListFragment categoryListFragment = new RecyclerViewCategoryListFragment();

                //pass the db list to the fragment somehow

                transaction.replace(R.id.category_list_fragment, categoryListFragment);
                transaction.addToBackStack("categoryListFragment");
                transaction.commit();

            }

            break;



            case WATCH_LIST:
                Toast.makeText(getBaseContext(), "choice: watchlist",
                        Toast.LENGTH_SHORT).show();

            {
                //clearRadioButtons();

                // Just a test
                hideRadioButtons();

                //

                final BazaarActivity baz = this;
                IITBazaar.getDBController().syncCurrentUserWatchList(baz, baz);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                RecyclerViewWatchlistFragment watchListFragment = new RecyclerViewWatchlistFragment();
                //transaction.replace(R.id.buy_list_fragment, watchListFragment);
                transaction.replace(R.id.buy_sell_subfragment, watchListFragment);
                transaction.addToBackStack("watchListFragment");
                transaction.commit();
            }

            break;






            case SEARCH:
             //   Toast.makeText(getBaseContext(), "choice: search",
               //         Toast.LENGTH_SHORT).show();

            {

                clearRadioButtons();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                RecyclerViewCategoryListFragment fragcaTListFragment = new RecyclerViewCategoryListFragment();

                //pass the db list to the fragment somehow


                transaction.replace(R.id.buy_list_fragment, fragcaTListFragment);
                transaction.addToBackStack("buy_list_fragment");
                transaction.commit();

            }

            break;



        }


    }




    public void getNextSellFragment(SellNavKeys key) {


        switch (key) {


            case SELL_ITEM_DESCRIPTION_ENTRY:


            {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                ItemEntryFragment itemEntryFragment = new ItemEntryFragment();
                transaction.replace(R.id.sell_content_fragment, itemEntryFragment);
                transaction.addToBackStack("sell_content_fragment");
                transaction.commit();

            }


            break;


        }


    }


    public void itemSelected(final int itemNumber) {


        DBController dbController = IITBazaar.getDBController();
        dbController.syncCurrentUserWatchList(this, this);
        dbController.getItem(itemNumber, this);


    }

    public void displayItemDescription(Item item){


        IITBazaar.setCurrentItem(item);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        ItemDescriptionFragment itemDescriptionFragment = new ItemDescriptionFragment();

        //Pass an intent here to the fragment

        transaction.replace(R.id.buy_list_fragment, itemDescriptionFragment);
        transaction.addToBackStack("buy_sell_subfragment");
        transaction.commit();
    }

    public void displayItemDescriptionBySell(){




        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        ItemDescriptionFragment itemDescriptionFragment = new ItemDescriptionFragment();

        //Pass an intent here to the fragment

        transaction.replace(R.id.sell_content_fragment, itemDescriptionFragment);
        transaction.addToBackStack("buy_sell_subfragment");
        transaction.commit();
    }

    public void showImageError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please, a valid image must be selected.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        builder.create().show();
    }

    public void showNonValidPriceError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please, a valid price must be introduced.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        builder.create().show();
    }

    public void showEnterDescriptionError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please, an item description must be introduced.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        builder.create().show();
    }

    public void showNonValidDateError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please, a valid date in the future must be introduced.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        builder.create().show();
    }

    public void listItemRemotely(Item newItem) {


        if (IITBazaar.isConnected()){

            if (null != currentItemNumberListener) {

                DBController controller = IITBazaar.getDBController();
                controller.insertItemRemotely(newItem, currentItemNumberListener);
                controller.insertIntoRemoteSellTable(newItem, currentItemNumberListener);
                // Log.d("Item Listing", "Item number retrieved = " + newItem.getItemNumber());

            } else {

            }

        }
        else{
            Toast.makeText(IITBazaar.getAppContext(), "Not connected to internet. Cannot list item. Please try again.",
                    Toast.LENGTH_SHORT).show();


        }

       // controller.syncItemSQLiteMySQLDB();



    }



    public void listItemLocally(Item newItem) {


        DBController controller = IITBazaar.getDBController();
            controller.insertItemLocally(newItem);
            controller.insertSellItemLocally(newItem);


    }


    @Override
    public void notifyItemFound(final Item item) {


        displayItemDescription(item);

    }

    @Override
    public void notifyItemNotFound(final int item) {

    }
    @Override
    public void notifySellItemUpdated(){
        if (null!=adapterListenerSell){
            Log.d("notifySellDataChange","Sell Data adapter listener notified");

            //query local here
            Cursor mCursor = IITBazaar.getDBController().getSellingItems();
            mCursor.moveToFirst();

            //call up sell list fragment


            adapterListenerSell.notifyCursorChanged(mCursor);
            Toast.makeText(getBaseContext(), "Sell:Refresh Adapter Notified",
                    Toast.LENGTH_SHORT).show();
        }
        else{
            Log.e("Sell:notifyDataChange", "Data adapter listener is invalid");
        }

    }

    @Override
    public void notifyItemRetrievalError(final int statusCode) {

    }




    private PhotoListener pl;

    public void getPictureFromCamera() {




        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


        File f = null;

        try {
            f = setUpPhotoFile();
            mCurrentPhotoPath = f.getAbsolutePath();
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        } catch (IOException e) {
            e.printStackTrace();
            f = null;
            mCurrentPhotoPath = null;
        }


        startActivityForResult(takePictureIntent, ACTION_TAKE_PHOTO_B);


 //       Log.d(this.getClass().getSimpleName(), "blobber " + mCurrentPhotoPath);



    }

    public void registerPhotoListener(PhotoListener pl){
        this.pl =  pl;
    }

    public PhotoListener getPhotoListener(){
            return pl;
    }


    public void getPictureFromGallery() {


        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), SELECT_PICTURE);


    }








    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();

        return f;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
        return imageF;
    }

    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = getAlbumStorageDir(getAlbumName());

            if (storageDir != null) {
                if (!storageDir.mkdirs()) {
                    if (!storageDir.exists()) {
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    private String getAlbumName() {
        return "IITBazaar";
        // return getString(R.string.album_name);
    }


    public File getAlbumStorageDir(String albumName) {
        // TODO Auto-generated method stub
        return new File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES
                ),
                albumName
        );

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {



        switch (requestCode) {
            case ACTION_TAKE_PHOTO_B: {
                if (resultCode == RESULT_OK) {
                    //Log.d(this.getClass().getSimpleName(),"DONE with photo !");
                    if (null != pl){
                        Log.d(this.getClass().getSimpleName(),"camera->"+mCurrentPhotoPath);
                        if (null != pl) {
                        pl.loadPhotoFromPath(mCurrentPhotoPath);
                        }
                    }
                }
                break;
            }
            case SELECT_PICTURE: {
                if (resultCode == RESULT_OK) {
                    //Log.d(this.getClass().getSimpleName(),"DONE with photo !");
                    if (null != pl){

                        Uri selectedImageUri = data.getData();


                                Log.d(this.getClass().getSimpleName(), "gallery->" + "got Something");

                       if (null != pl) {
                           pl.loadPhotoFromURI(selectedImageUri);
                       }

                    }
                }
                break;
            }
        }

        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null) {


           processScan(scanResult);



           //pull up the item
        }



    }


    String LINKPREFIX = "iitbazaar";
    String DOMAINPREFIX = "item";
    private void processScan(IntentResult scanResult){

        String scanData = scanResult.getContents();
        String[] linky = scanData.split(LINKPREFIX);
        if (linky.length==2){
            String[] domain = linky[1].split(DOMAINPREFIX+".");

            if (domain.length==2){
                try {
                    int item = Integer.parseInt(domain[1]);
                    //set as home
                    menuSelected(MenuKeys.HOME);
                    itemSelected(item);
                }catch(NumberFormatException nfe){
                        nfe.printStackTrace();
                }


            }


        }

          //      iitbazaar://item.6




    }


    public void registerRadioGroupListener(RadioGroup rg) {

        ArrayList<RadioGroup> rgs = getOrCreateRadioGroup();


        rgs.add(rg);
    }


    private void clearRadioButtons(){


       Log.d("clearRadio ","clearing");

        ArrayList<RadioGroup> rgs = getOrCreateRadioGroup();

        Iterator<RadioGroup> rgIT = rgs.iterator();
        int color = getResources().getColor(R.color.iit_gray);

        while (rgIT.hasNext()){

            Log.d("clearRadio ","clearing--");
            RadioGroup rg = rgIT.next();
            if (null != rg) {

                int count = rg.getChildCount();
                ArrayList<RadioButton> listOfRadioButtons = new ArrayList<RadioButton>();
                for (int i=0;i<count;i++) {

                    rg.removeViewAt(i);
                    View o = rg.getChildAt(i);
                    if (o instanceof RadioButton) {

                        ((RadioButton)o).setChecked(false);
                    }

                }

            }else{
                Log.d("clearRadio ","clearing--null!");
            }

        }


    }

    public void registerItemNumberListener(ItemNumberListener inl){
        this.currentItemNumberListener = inl;
    }

    public void notifyItemNumberListener(int itemNumber) {

        if (null != currentItemNumberListener) {

            currentItemNumberListener.notifyItemNumber(itemNumber);
        }

    }

    private ArrayList<RadioGroup> getOrCreateRadioGroup(){

        if (null == radioGroups){
            radioGroups = new ArrayList<>();
        }

        return radioGroups;

    }


    @Override
    public void notifyDataChange() {

        if (null!=adapterListener){
            Log.d("notifyDataChange","Data adapter listener notified");

            //query local here
            Cursor mCursor = IITBazaar.getDBController().getCurrentItems();
            mCursor.moveToFirst();

            adapterListener.notifyCursorChanged(mCursor);
            Toast.makeText(getBaseContext(), "Refresh Adapter Notified",
                    Toast.LENGTH_SHORT).show();
        }
        else{
            Log.e("notifyDataChange", "Data adapter listener is invalid");
        }

    }

    @Override
    public void registerDataChangeListeners(CursorAdapter adapter) {
        Log.d("notifyDataChange","Data adapter listener registered");
        this.adapterListener = adapter;
    }

    @Override
    public void registerSellDataChangeListeners(CursorAdapter adapter){
        Log.d("SellnotifyDataChange","Sell Data adapter listener registered");
        this.adapterListenerSell = adapter;
    }

    @Override
    public void notifyWatchlistUpdated() {
        if (null!=adapterListener){
            Log.d("notifyWatchlistUpdated","Wishlist adapter listener notified");

            //query local here
            Cursor mCursor = IITBazaar.getDBController().getAllItemsInWatchList();
            mCursor.moveToFirst();

            adapterListener.notifyCursorChanged(mCursor);
            Toast.makeText(getBaseContext(), "Refresh Wishlist Adapter Notified",
                    Toast.LENGTH_SHORT).show();
        }
        else{
            Log.e("notifyWatchlistUpdated", "Adapter listener is invalid");
        }
    }

    @Override
    public void registerWatchlistChangeListeners(CursorAdapter cursorAdapter) {
        this.adapterListener = cursorAdapter;
    }
    @Override
    public void notifyFollowStatusUpdated() {
        if (null!=itemDescriptionButtons) {
            itemDescriptionButtons.buildFollowButton();
        }
        else{
            Log.e("FollowStatusUpdated", "Listener is invalid");
        }
    }




    public void registerFollowStatusListeners(ItemDescriptionButtonFragment itemDescriptionButtons) {
        this.itemDescriptionButtons = itemDescriptionButtons;
    }
}
