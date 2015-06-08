package com.example.zekun.franklyproject;

import java.io.Serializable;



/*myMusicClass is what we're going to call the class that is used to represent each individual entry that the user made

 */

public class myMusicClass implements Serializable {

    String song;
    String artist;
    String album;

    int id;


    public void setSong(String song){
        this.song = song;
    }

    public void setArtist(String artist){
        this.artist = artist;
    }

    public void setAlbum(String album){
        this.album = album;
    }


    public String getSong(){
        return this.song;
    }

    public String getArtist(){
        return this.artist;
    }

    public String getAlbum(){
        return this.album;
    }

    @Override
    public String toString() {//I think you needed this to save objects into files
        return new StringBuffer(" Song : ")
                .append(this.song)
                .append(" Artist : ")
                .append(this.artist)
                .append(" Album : ")
                .append(this.album).toString();
    }

    public boolean containsString(String inputString){//used for search functions
        return (this.song.contains(inputString)|this.artist.contains(inputString)|this.album.contains(inputString) );

    }


}
