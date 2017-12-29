package com.kczechowski.data;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.kczechowski.data.models.AlbumModel;
import com.kczechowski.data.models.ArtistModel;
import com.kczechowski.data.models.LibraryWrapper;
import com.kczechowski.data.models.SongModel;
import com.kczechowski.utils.FilesUtils;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class Library {

    private List<File> resources; //All directories with music
    private List<File> songsFiles;

    private List<ArtistModel> gArtists = null;
    private List<AlbumModel> gAlbums = null;
    private List<SongModel> gSongs = null;

    private Comparator<ArtistModel> artistModelComparator = (o1, o2) -> o1.getArtistID().compareTo(o2.getArtistID());
    private Comparator<AlbumModel> albumModelComparator = (o1, o2) -> o1.getAlbumID().compareTo(o2.getAlbumID());
    private Comparator<SongModel> songModelComparator = (o1, o2) -> o1.getSongID().compareTo(o2.getSongID());

    public static final String TAG_TITLE = "title";
    public static final String TAG_ALBUM = "album";
    public static final String TAG_ARTIST = "artist";
    public static final String TAG_TRACK = "track";
    public static final String TAG_DURATION = "duration";

    public Library() {
        resources = new ArrayList<>();
    }

    public void build(String outputPath){

        //Add all music files from all sources to list
        for(File file : resources){
            songsFiles = FilesUtils.getMusicFilesInFolderIncludingSubfolders(file);
        }

        //Create library directory and delete the old one
        Path libraryOutputPath = Paths.get(outputPath + "library");
        try {
            FilesUtils.deleteDirectory(libraryOutputPath.toFile());
            Files.createDirectories(libraryOutputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

/*        Example library.json file
        {
            "artists": [
                {
                    "artistID": "Arctic_Monkeys",
                        "artistName": "Arctic Monkeys"
                }
            ],
            "albums": [
                {
                    "albumID": "Arctic_Monkeys>AM",
                        "albumName": "AM"
                }
            ],
            "songs": [
            {
                "songID": "Arctic_Monkeys>AM>Knee_Socks",
                    "songName": "Knee Socks",
                    "path": ""
            }
            ]
        }
        */

        HashMap<String, ArtistModel> artistModelHashMap = new HashMap<>();
        HashMap<String, AlbumModel> albumModelHashMap = new HashMap<>();
        HashMap<String, SongModel> songModelHashMap = new HashMap<>();

        for(File file : songsFiles){

            HashMap songTags = getTagsFromMP3File(file);
            String songName = (String) songTags.get(TAG_TITLE);
            String artistName = (String) songTags.get(TAG_ARTIST);
            String albumName = (String) songTags.get(TAG_ALBUM);
            String songTrack = (String) songTags.get(TAG_TRACK);
            String songDuration = (String) songTags.get(TAG_DURATION);
            String songPath = file.getAbsolutePath();

            String artistKey = formatID(artistName);
            String albumKey = artistKey + ">" + formatID(albumName);
            String songKey = albumKey + ">" + formatID(songName);

            //Assign artists to map
            if(!artistModelHashMap.containsKey(artistKey)){
                ArtistModel artistModel = new ArtistModel();
                artistModel.setArtistName(artistName);
                artistModel.setArtistID(artistKey);
                artistModelHashMap.put(artistKey, artistModel);

                //Create directory for every artist
                try {
                    Files.createDirectory(Paths.get(libraryOutputPath + File.separator + artistKey));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //Assign albums to map
            if(!albumModelHashMap.containsKey(albumKey)){
                AlbumModel albumModel = new AlbumModel();
                albumModel.setAlbumName(albumName);
                albumModel.setAlbumID(albumKey);
                albumModelHashMap.put(albumKey, albumModel);

                //Create directory for every album
                try {
                    Files.createDirectory(Paths.get(libraryOutputPath + File.separator + artistKey + File.separator + formatID(albumName)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //Assign songs to map
            if(!songModelHashMap.containsKey(songKey)){
                String songTargetPath = File.separator + artistKey + File.separator + formatID(albumName) + File.separator + FilesUtils.getCleanFilePath(formatID(songName)) + ".mp3";
                Path sourcePath = Paths.get(songPath);
                Path targetPath = Paths.get(libraryOutputPath + songTargetPath);
                SongModel songModel = new SongModel();
                songModel.setSongName(songName);
                songModel.setSongID(songKey);
                songModel.setSongPath("library" + songTargetPath);
                songModel.setSongTrack(songTrack);
                songModel.setSongDuration(songDuration);
                songModelHashMap.put(songKey, songModel);

                //Copy every file from source to library directory
                try {

                    Files.copy(sourcePath, targetPath, StandardCopyOption.COPY_ATTRIBUTES);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        //Save library.json file
        LibraryWrapper libraryWrapper = new LibraryWrapper();
        libraryWrapper.setArtists(new ArrayList<ArtistModel>(artistModelHashMap.values()));
        libraryWrapper.setAlbums(new ArrayList<AlbumModel>(albumModelHashMap.values()));
        libraryWrapper.setSongs(new ArrayList<SongModel>(songModelHashMap.values()));

        Gson gson = new Gson();
        String toSave = gson.toJson(libraryWrapper, LibraryWrapper.class);

        try {
            FileWriter fileWriter = new FileWriter(libraryOutputPath + File.separator + "library.json");
            fileWriter.write(toSave);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void loadLibrary(Path path){
        try {
            JsonReader jsonReader = new JsonReader(new FileReader(path + "\\library.json"));
            Gson gson = new Gson();
            LibraryWrapper wrapper = gson.fromJson(jsonReader, LibraryWrapper.class);
            gArtists = wrapper.getArtists();
            gAlbums = wrapper.getAlbums();
            gSongs = wrapper.getSongs();

            Collections.sort(gArtists, artistModelComparator);
            Collections.sort(gAlbums, albumModelComparator);
            Collections.sort(gSongs, songModelComparator);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String formatID(String s){
        return s.replaceAll("\\s+", "_").toLowerCase();
    }

    public void addResourceDirectory(File file){
        resources.add(file);
    }

    public void removeResourceDirectory(File file){
        resources.remove(file);
    }

    public static HashMap<String, Object> getTagsFromMP3File(File file){
        HashMap<String, Object> map = new HashMap<>();

        try {
            Mp3File mp3File = new Mp3File(file);

            if(mp3File.hasId3v1Tag() || mp3File.hasId3v2Tag()){
                map.put(TAG_TITLE, mp3File.getId3v1Tag().getTitle());
                map.put(TAG_ALBUM, mp3File.getId3v1Tag().getAlbum());
                map.put(TAG_ARTIST, mp3File.getId3v1Tag().getArtist());
                map.put(TAG_TRACK, mp3File.getId3v1Tag().getTrack());
                map.put(TAG_DURATION, "0:00");
                //add duration
            }
        } catch (IOException | UnsupportedTagException | InvalidDataException e) {
            e.printStackTrace();
        }

        return map;
    }

    public static String getArtistID(String id){
        return id.split(">")[0];
    }

    public static String getAlbumID(String id){
        return getArtistID(id) + ">" + id.split(">")[1];
    }

    public static String getSongID(String id){
        return getAlbumID(id) + ">" + id.split(">")[2];
    }

    public ArtistModel getArtistById(String id){
        ArtistModel temp = new ArtistModel();
        temp.setArtistID(id);
        int found = Collections.binarySearch(gArtists, temp, artistModelComparator);
        return gArtists.get(found);
    }

    public AlbumModel getAlbumById(String id){
        AlbumModel temp = new AlbumModel();
        temp.setAlbumID(id);
        int found = Collections.binarySearch(gAlbums, temp, albumModelComparator);
        return gAlbums.get(found);
    }

    public SongModel getSongById(String id){
        SongModel temp = new SongModel();
        temp.setSongID(id);
        int found = Collections.binarySearch(gSongs, temp, songModelComparator);
        return gSongs.get(found);
    }


    public List<ArtistModel> getAllArtists(){
        return gArtists;
    }

    public List<AlbumModel> getAlbumsByArist(String id){
        List<AlbumModel> list = new ArrayList<>();
        for(AlbumModel albumModel : gAlbums){
            if(albumModel.getAlbumID().startsWith(id)){
                list.add(albumModel);
            }
        }
        return list;
    }

    public List<SongModel> getSongsByAlbum(String id){
        List<SongModel> list = new ArrayList<>();
        for(SongModel songModel : gSongs){
            if(songModel.getSongID().startsWith(id)){
                list.add(songModel);
            }
        }
        return list;
    }


}
