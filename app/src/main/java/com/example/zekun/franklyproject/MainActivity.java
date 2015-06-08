package com.example.zekun.franklyproject;


import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;

/*
Created by Zekun Wang for Frankly Inc's coding challenge, 6/7/2015
 */



public class MainActivity extends ActionBarActivity {

    public static ArrayList<myMusicClass> mySuperList = new ArrayList<>();//we store our music entires in an ArrayList data structure
    public static ArrayList<Integer> myDeletedItems = new ArrayList<>();//we keep track of which entries the user deletes in an ArrayList
    public static String myFileLocation; //where we store the user's music entires
    public static String myPartialFileLocation;//where we store partial information from the input fields to be reopened upon the next session
    public static Context myCurrContext; //used in case any of our methods need a static reference to a Context



    @Override
    public void onCreate(Bundle savedInstanceState){
         /*basically as soon as the app is opened, the first thing it does is find out where
         it will place files and create a static String of it as so not to lose track of where it should keep
         the files used to record user data, then it will attempt to locate a previously existing file if there is one
         and pull the data from there in the form of a serializable ArrayList that holds a custom myMusicClass object that contains
         all the information associated with each entry.

         It will then call upon the initializelist() method to load the previously stored entires inside the Scrollview in our main
         layouts.

         After that it will monitor the Search box and Update button for user activity and update the data accordingly.
          */





         /*
        First step upon starting up a new instance of the client is to try to access and restore the songs
        entered and saved on the app. We do this and store everything in a file called myFile.ser

        We also have to restore the input fields and we store that information in a file called myPartial.ser
         */

        myFileLocation  = getFilesDir().toString()+"/myFile.ser"; //path of the file used to store user entires
        myPartialFileLocation = getFilesDir().toString() + "myPartial.ser"; //path of the file used to store partial entires in input fields



        //next few lines account for using the right layout for the corresponding orientation
        super.onCreate(savedInstanceState);
        if (getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.activity_main);

        } else {
            setContentView(R.layout.main_landscape);

        }



        myCurrContext = this; //used for later parts of code, and also passing to other methods
        final EditText myArtistET = (EditText) findViewById(R.id.artistBox);//input box for artist
        final EditText mySongET = (EditText) findViewById(R.id.songBox);//input box for song title
        final EditText myAlbumET = (EditText) findViewById(R.id.albumBox);//input box for album






        /*now we call upon the initializeList() method to initially set the ScrollView to
        the saved music entires that we pulled form myFile.ser
         */
        initializeList(this);



        /*
        myPartialFile.ser is where we store all the Partially finished Text fields from a previously
        destroyed session. Here we are retrieving it and setting all the input EditTexts to the
        previous Strings from the last session when it closed

        NOTE: I did not consider it desirable for the Search input to be restored, since it seems better
        and nicer to have it there constantly in case the user needs to be reminded where the search bar is.
         */
        File myPartialFile = new File(myPartialFileLocation);

        if(myPartialFile.exists() && !myPartialFile.isDirectory()) {

            try {
                FileInputStream streamIn = new FileInputStream(myPartialFileLocation);
                ObjectInputStream objectinputstream = new ObjectInputStream(streamIn);
                myMusicClass retrievedPartialFields = (myMusicClass) objectinputstream.readObject();
                objectinputstream.close();
                streamIn.close();

                /*
                restore the input fields to previous configurations from the last session
                 */

                myAlbumET.setText(retrievedPartialFields.album);
                mySongET.setText(retrievedPartialFields.song);
                myArtistET.setText(retrievedPartialFields.artist);


            } catch (Exception e) {

                e.printStackTrace();
            }
        }








        /*next few lines concern the update button, and contains instructions for creating an entry in the
        scrollview used to display the entires that the user put in
         */

        Button updateButton = (Button) findViewById(R.id.updateButton);

        updateButton.setOnClickListener(new View.OnClickListener() {//when user hits the post button
            @Override
            public void onClick(View view) {

                /*The ET stands for "EditText". I know I can make it more efficient
                 by combining everything into one line but I thought this would be more clear
                  */


                String myArtist = myArtistET.getText().toString();
                String mySong = mySongET.getText().toString();
                String myAlbum = myAlbumET.getText().toString();

                /*now that we saved the data from the input boxes, restore the original strings
                so that the user can enter in another song
                 */

                myAlbumET.setText("Enter Album");
                myArtistET.setText("Enter Artist");
                mySongET.setText("Enter Song");


                /*
                we will now dynamically update the ScrollView when the user presses the update button, and
                display the information
                 */

                ViewGroup parent = (ViewGroup) findViewById(R.id.myScrollLinear);//the ScrollView that displays the music entires

                view = LayoutInflater.from(myCurrContext).inflate(R.layout.basic_song_unit, null);//basic song unit is how we will visually display the entires

                final TextView myTextview = (TextView)view.findViewById(R.id.displayWindow);//we now update the fields to what the user inputted
                myTextview.setText("Artist: " + myArtist+ "\nSong: " + mySong + "\nAlbum: "+ myAlbum);

                final TextView myIdHolder = (TextView)view.findViewById(R.id.idHolder);//used to help link each entry in the music to its dynamically updated display
                myIdHolder.setText(Integer.toString(mySuperList.size()));//this invisible TextView is basically each music entry's ID

                final Button deleteButton = (Button) view.findViewById(R.id.button);//the blue 'X' icon that the user can press to delete stuff
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mySuperList = readMySuperList();
                        if(mySuperList!=null) {
                            for (int r = 0; r < mySuperList.size(); r++) {
                                myMusicClass myCurrObj = (mySuperList.get(r));
                                if (myCurrObj.id== Integer.parseInt((String) myIdHolder.getText())) {
                                    removeAtIndex(r);
                                }
                            }
                            /*next two lines are unneccesarry because I updated the refresh() method to take care of things, but I
                            think they do a good job of explaining that the graphical entries are removed
                             */
                            ((ViewManager) v.getParent()).removeView(myTextview);
                            ((ViewManager) v.getParent()).removeView(deleteButton);
                        }

                        refresh();//will wipe the entire ScrollView and then rebuild it using the updated ArrayList of music entires
                    }
                });

                /*now we finished constructing the visual conterpart of the music that the user entered, now
                put it in the ScrollView
                 */
                parent.addView(view);



                /*now we take care of updating the data in our File, which stores the data in an ArrayList data structure
                called mySuperList
                 */

                myMusicClass myNewAddition = new myMusicClass();
                myNewAddition.song = mySong;
                myNewAddition.album = myAlbum;
                myNewAddition.artist = myArtist;
                myNewAddition.id = mySuperList.size();//we assign an id for the visual conterpart based on the entry's index in the ArrayList


                /*
                Finished creating the new myMusicClass object, now add it to the ArrayList
                 */
                mySuperList.add(myNewAddition);


                /*update the ArrayList stored in myFile.ser

                 */
                try {

                    FileOutputStream fout = new FileOutputStream(myFileLocation);
                    ObjectOutputStream oos = new ObjectOutputStream(fout);
                    oos.writeObject(mySuperList);
                    fout.close();
                    oos.close();
                }
                catch (Exception e){
                    e.printStackTrace();
                }


            }
        });





        /*Next lines concern the Search box, which as per the guidelines will be dynamic and the ScrollView will
        change as text is being entered
         */

        final EditText mySearchBox = (EditText) findViewById(R.id.searchBox);//input field for the search

        myCurrContext = this;//might need for later

        mySearchBox.addTextChangedListener(new TextWatcher() {//notifies program if user begins to type
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {



            }

            @Override
            public void afterTextChanged(Editable s) {//as user types, will update the entries displayed in ScrollView accordingly

                ViewGroup parent = (ViewGroup) findViewById(R.id.myScrollLinear);
                parent.removeAllViews();//wipes the scrollview

                /*now that our scrollview is blank, we start hunting for appropriate data entires and display them accordingly

                 */

                String s2String = s.toString();

                if(mySuperList!=null) {
                    flushDeleted();//in case there are residual non deleted entries in our data
                    for (int h = 0; h < mySuperList.size(); h++) {//look for entires that match the search query
                        myMusicClass myCurrObj = mySuperList.get(h);
                        if (myCurrObj.containsString(s2String)) {//if a good entry is located, we display it


                            View view = LayoutInflater.from(myCurrContext).inflate(R.layout.basic_song_unit, null);//basic song unit is how we will visually display the entires

                            final TextView myTextview = (TextView)view.findViewById(R.id.displayWindow);//we now update the fields to what the user inputted
                            myTextview.setText("Artist: " + myCurrObj.artist+ "\nSong: " + myCurrObj.song + "\nAlbum: "+ myCurrObj.album);

                            final TextView myIdHolder = (TextView)view.findViewById(R.id.idHolder);//used to help link each entry in the music to its dynamically updated display
                            myIdHolder.setText(Integer.toString(myCurrObj.id));//this invisible TextView is basically each music entry's ID

                            final Button deleteButton = (Button) view.findViewById(R.id.button);//the blue 'X' icon that the user can press to delete stuff
                            deleteButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mySuperList = readMySuperList();
                                    if(mySuperList!=null) {
                                        for (int r = 0; r < mySuperList.size(); r++) {
                                            myMusicClass myCurrObj = (mySuperList.get(r));
                                            if (myCurrObj.id== Integer.parseInt((String) myIdHolder.getText())) {
                                                removeAtIndex(r);
                                            }
                                        }
                            /*next two lines are unneccesarry because I updated the refresh() method to take care of things, but I
                            think they do a good job of explaining that the graphical entries are removed
                             */
                                        ((ViewManager) v.getParent()).removeView(myTextview);
                                        ((ViewManager) v.getParent()).removeView(deleteButton);
                                    }

                                    refresh();//will wipe the entire ScrollView and then rebuild it using the updated ArrayList of music entires
                                }
                            });

                /*now we finished constructing the visual conterpart of the music that the user entered, now
                put it in the ScrollView
                 */
                            parent.addView(view);

                        }
                    }
                }

            }
        });




         /*this will detect when the user has exited search by selecting something else and so will restore
            all entires except for just the one from the dynamic search
             */
        mySearchBox.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    initializeList(myCurrContext);
                }
            }
        });


       /*below code is an attempt at smoothing out the UI for portrait views, it is supposed to minimize
       the other input fields so that only one is displayed at a time, allowing for more available room on
       the display to let the user see what they are typing and to view their results in a bigger window. Unfortunately
       it still isn't working and so I decided to leave off on this for now
        */
        /*
        final ImprovedEditText mySongBox = (ImprovedEditText) findViewById(R.id.songBox);
        final ImprovedEditText myArtistBox = (ImprovedEditText) findViewById(R.id.artistBox);
        final ImprovedEditText myAlbumBox = (ImprovedEditText) findViewById(R.id.albumBox);

        myCurrContext = this;



        mySongBox.setOnFocusChangeListener(new View.OnFocusChangeListener(){

            @Override
            public void onFocusChange(View v, boolean hasFocus) {


                if(hasFocus)
                {
                    mySearchBox.

                    myArtistBox.setVisibility(View.);
                    myAlbumBox.setVisibility(View.INVISIBLE);
                    mySearchBox.setVisibility(View.INVISIBLE);
                }
                if(!hasFocus)
                {
                    myArtistBox.setVisibility(View.VISIBLE);
                    myAlbumBox.setVisibility(View.VISIBLE);
                    mySearchBox.setVisibility(View.VISIBLE);
                }
            }
        });
        */
    }

    public void initializeList(ArrayList myList, Context myCurrContext){
        for(int i=0; i<myList.size();i++){
            myMusicClass myItem = (myMusicClass) myList.get(i);
            String song = myItem.song;
            String artist = myItem.artist;
            String album = myItem.album;

            ViewGroup parent = (ViewGroup) findViewById(R.id.myScrollLinear);

            View view = LayoutInflater.from(myCurrContext).inflate(R.layout.basic_song_unit, null);
            TextView myTextview = (TextView)view.findViewById(R.id.displayWindow);
            myTextview.setText("Artist: " + artist + "\nSong: " + song + "\nAlbum: "+ album);


            parent.addView(view);
        }
    }

    public void removeAtIndex(int r){//used to record which entires the user wishes to remove
        myDeletedItems.add(r);//stores the index of the entries to remove in an ArrayList myDeletedItems

    }

    public void flushDeleted(){//for deleting the items the user wishes to delete


        if ( (myDeletedItems != null)) { //null check
            Collections.sort(myDeletedItems);//sort the numbers into increasing order, this way we can safely and easily remove from mySuperList
            for (int i = myDeletedItems.size()-1; i > -1; i--) { //start from the biggest and go down
                int deleteIndex = myDeletedItems.get(i);

                {
                    for(int h=deleteIndex;h<mySuperList.size();h++){
                        mySuperList.get(h).id--;//decrease the id of every myMusicClass Object after the one created in order to account for the removal of one
                    }
                    mySuperList.remove(deleteIndex);//permanently removes the entry from the ArrayList
                }

            }

            myDeletedItems = new ArrayList<>();//after deleting all the selected indexes, no more things to delete


        }

        /*next update myFile by writing in the updated ArrayList

         */
        try {
            FileOutputStream fout = new FileOutputStream(myFileLocation);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(mySuperList);
            fout.close();
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();

        }


    }



    @Override
    public void onResume(){//refresh and update when the session is resumed
        super.onResume();
        refresh();
    }


    /*although I ended up simply pasting the code directly into other methods for debugging, I think leaving the code
     for deleteInSuperList  is good for explaning how I updated the ID's of all the music entries in the ArrayList
     */
    public void deleteInSuperList(int i){
        for(int h=i;h<mySuperList.size();h++){
            mySuperList.get(h).id--;//decrease the id of every myMusicClass Object after the one created in order to account for the removal of one
        }
        mySuperList.remove(i);
    }




    /*used to update the ScrollView with the current entires stored in the ArrayList from  myFile

     */
    public void initializeList(Context myContext){

        ViewGroup parent = (ViewGroup) findViewById(R.id.myScrollLinear);
        parent.removeAllViews();



        //First we grab the file

        File myFile = new File(myFileLocation);
        ArrayList<myMusicClass> myList = new ArrayList<>();

        if(myFile.exists() && !myFile.isDirectory()) {

            try {
                FileInputStream streamIn = new FileInputStream(myFileLocation);
                ObjectInputStream objectinputstream = new ObjectInputStream(streamIn);
                ArrayList<myMusicClass> retrievedList = (ArrayList) objectinputstream.readObject();
                objectinputstream.close();
                streamIn.close();

                myList = retrievedList;
                mySuperList = myList;


            } catch (Exception e) {

                e.printStackTrace();
            }
        }




        /*Now that we have the ArrayList stored in the file, we iterate through it to grab each
        individual music entry to display in the ScrollView
         */

        for (int e=0;e<myList.size();e++) {

            myMusicClass myMusicObj = myList.get(e);

            String myArtist = myMusicObj.artist;
            String mySong = myMusicObj.song;
            String myAlbum = myMusicObj.album;
            int myId = myMusicObj.id;

            /*Now we take care of setting it up visually

             */

            parent = (ViewGroup) findViewById(R.id.myScrollLinear);

            View view = LayoutInflater.from(myContext).inflate(R.layout.basic_song_unit, null);

            final TextView myTextview = (TextView) view.findViewById(R.id.displayWindow);
            myTextview.setText("Artist: " + myArtist + "\nSong: " + mySong + "\nAlbum: " + myAlbum);

            final TextView myIdHolder = (TextView) view.findViewById(R.id.idHolder);
            myIdHolder.setText(Integer.toString(myId));

            final Button deleteButton = (Button) view.findViewById(R.id.button);
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mySuperList != null) {
                        for (int r = 0; r < mySuperList.size(); r++) {
                            myMusicClass myCurrObj = (mySuperList.get(r));
                            if (myCurrObj.id == Integer.parseInt((String) myIdHolder.getText())) {
                                removeAtIndex(r);
                            }
                        }
                        ((ViewManager) v.getParent()).removeView(myTextview);
                        ((ViewManager) v.getParent()).removeView(deleteButton);


                    }
                    refresh();
                }

            });


            parent.addView(view);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//skeleton code from default android program
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//skeleton code from default android program
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








    public void savePartials(){//used to store the data in the input boxes, not search though

        EditText myArtistET = (EditText) findViewById(R.id.artistBox);
        String myArtist = myArtistET.getText().toString();

        EditText mySongET = (EditText) findViewById(R.id.songBox);
        String mySong = mySongET.getText().toString();

        EditText myAlbumET = (EditText) findViewById(R.id.albumBox);
        String myAlbum = myAlbumET.getText().toString();



        myMusicClass myPartialFields = new myMusicClass();
        myPartialFields.song = mySong;
        myPartialFields.album = myAlbum;
        myPartialFields.artist = myArtist;




        try {

            FileOutputStream fout = new FileOutputStream(myPartialFileLocation);
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(myPartialFields);
            fout.close();
            oos.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }



    public void refresh(){//combines flushDeleted() and initializeList() to update the ScrollView
        flushDeleted();
        initializeList(this);

    }

    public ArrayList<myMusicClass> readMySuperList(){//method I made to expedite the process of grabbing the ArrayList from myFile
        File myFile = new File(myFileLocation);
        ArrayList<myMusicClass> myList = new ArrayList<>();

        if(myFile.exists() && !myFile.isDirectory()) {

            try {
                FileInputStream streamIn = new FileInputStream(myFileLocation);
                ObjectInputStream objectinputstream = new ObjectInputStream(streamIn);
                ArrayList<myMusicClass> retrievedList = (ArrayList) objectinputstream.readObject();
                objectinputstream.close();
                streamIn.close();

                myList = retrievedList;
                mySuperList = myList;


            } catch (Exception e) {

                e.printStackTrace();
            }
        }

        return mySuperList;
    }


}