package com.kczechowski.data;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.kczechowski.data.models.AlbumModel;
import com.kczechowski.data.models.ArtistModel;
import com.kczechowski.data.models.LibraryWrapper;
import com.kczechowski.data.models.SongModel;
import com.kczechowski.listeners.LibraryStatusChangeEvent;
import com.kczechowski.main.App;
import com.kczechowski.utils.FilesUtils;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class Library {

    private Set<File> resources; //All directories with music

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
    public static final String TAG_ALBUM_IMAGE = "image";
    public static final Path NULL_ALBUM_IMAGE_PATH = Paths.get("res/nullAlbumImage.png");

    private Path loadedLibraryPath;

    private boolean isLoaded = false;

    private Path lastBuiltLibraryPath;

    public boolean isLoaded() {
        return isLoaded;
    }

    public Library() {
        resources = new HashSet<>();
    }

    public void build(String outputPath){
        try {
            App.eventManager.fireLibraryStatusChangeEvent(new LibraryStatusChangeEvent(this, LibraryStatusChangeEvent.STARTED_BUILDING));

            List<File> songsFiles = new ArrayList<>();
            //Add all music files from all sources to list
            for (File file : resources) {
                songsFiles.addAll(FilesUtils.getMusicFilesInFolderIncludingSubfolders(file));
            }

            System.out.println(songsFiles.size() + " songs found.");

            //Create library directory and delete the old one
            Path libraryOutputPath = Paths.get(outputPath + File.separator + "library");
            FileUtils.deleteDirectory(libraryOutputPath.toFile());
            Files.createDirectories(libraryOutputPath);

            HashMap<String, ArtistModel> artistModelHashMap = new HashMap<>();
            HashMap<String, AlbumModel> albumModelHashMap = new HashMap<>();
            HashMap<String, SongModel> songModelHashMap = new HashMap<>();

            for (File file : songsFiles) {

                //check if file has tags
                if (!hasMP3FileProperTags(file))
                    continue;

                HashMap songTags = getTagsFromMP3File(file);
                String songName = (String) songTags.get(TAG_TITLE);
                String artistName = (String) songTags.get(TAG_ARTIST);
                String albumName = (String) songTags.get(TAG_ALBUM);
                String songTrack = (String) songTags.get(TAG_TRACK);
                String songDuration = (String) songTags.get(TAG_DURATION);
                String songPath = file.getAbsolutePath();
                byte[] albumImage = (byte[]) songTags.get(TAG_ALBUM_IMAGE);

                String artistKey = formatID(artistName);
                String albumKey = artistKey + ">" + formatID(albumName);
                String songKey = albumKey + ">" + formatID(songName);

                System.out.println("Adding \"" + songKey + "\" to library.");

                //Assign artists to map
                if (!artistModelHashMap.containsKey(artistKey)) {
                    ArtistModel artistModel = new ArtistModel();
                    artistModel.setArtistName(artistName);
                    artistModel.setArtistID(artistKey);
                    artistModelHashMap.put(artistKey, artistModel);

                    //Create directory for every artist
                    Files.createDirectories(Paths.get(libraryOutputPath + File.separator + artistKey));
                }

                //Assign albums to map
                if (!albumModelHashMap.containsKey(albumKey)) {
                    AlbumModel albumModel = new AlbumModel();
                    albumModel.setAlbumName(albumName);
                    albumModel.setAlbumID(albumKey);
                    albumModel.setAlbumImage(albumImage);
                    albumModelHashMap.put(albumKey, albumModel);

                    //Create directory for every album
                    Files.createDirectories(Paths.get(libraryOutputPath + File.separator + artistKey + File.separator + formatID(albumName)));
                }

                //Assign songs to map
                if (!songModelHashMap.containsKey(songKey)) {
                    String songTargetPath = File.separator + artistKey + File.separator + formatID(albumName) + File.separator + FilesUtils.getCleanFilePath(formatID(songName)) + ".mp3";
                    Path sourcePath = Paths.get(songPath);
                    Path targetPath = Paths.get(libraryOutputPath + songTargetPath);
                    SongModel songModel = new SongModel();
                    songModel.setSongName(songName);
                    songModel.setSongID(songKey);
                    songModel.setSongPath(songTargetPath);
                    songModel.setSongTrack(songTrack);
                    songModel.setSongDuration(songDuration);
                    songModelHashMap.put(songKey, songModel);

                    //Copy every file from source to library directory
                    Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                }
            }

            //Save library.json file
            LibraryWrapper libraryWrapper = new LibraryWrapper();
            libraryWrapper.setArtists(new ArrayList<ArtistModel>(artistModelHashMap.values()));
            libraryWrapper.setAlbums(new ArrayList<AlbumModel>(albumModelHashMap.values()));
            libraryWrapper.setSongs(new ArrayList<SongModel>(songModelHashMap.values()));

            Gson gson = new Gson();
            String toSave = gson.toJson(libraryWrapper, LibraryWrapper.class);

            FileWriter fileWriter = new FileWriter(libraryOutputPath + File.separator + "library.json");
            fileWriter.write(toSave);
            fileWriter.close();

            lastBuiltLibraryPath = Paths.get(outputPath);

            App.eventManager.fireLibraryStatusChangeEvent(new LibraryStatusChangeEvent(this, LibraryStatusChangeEvent.FINISHED_BUILDING));
        }catch(IOException e){
            App.eventManager.fireLibraryStatusChangeEvent(new LibraryStatusChangeEvent(this, LibraryStatusChangeEvent.FAILED_TO_BUILD, e));
        }
    }

    public void loadLibrary(Path path){
        App.eventManager.fireLibraryStatusChangeEvent(new LibraryStatusChangeEvent(this, LibraryStatusChangeEvent.STARTED_LOADING));
        try {
            isLoaded = false;
            JsonReader jsonReader = new JsonReader(new FileReader(path + File.separator + "library.json"));
            Gson gson = new Gson();
            LibraryWrapper wrapper = gson.fromJson(jsonReader, LibraryWrapper.class);
            jsonReader.close();
            gArtists = wrapper.getArtists();
            gAlbums = wrapper.getAlbums();
            gSongs = wrapper.getSongs();

            Collections.sort(gArtists, artistModelComparator);
            Collections.sort(gAlbums, albumModelComparator);
            Collections.sort(gSongs, songModelComparator);

            //assign artist and album to every song
            for (SongModel songModel : gSongs) {
                ArtistModel artistModel = App.library.getArtistById(Library.getArtistID(songModel.getSongID()));
                AlbumModel albumModel = App.library.getAlbumById(Library.getAlbumID(songModel.getSongID()));
                songModel.setArtist(artistModel);
                songModel.setAlbum(albumModel);
            }

            this.loadedLibraryPath = path;
            isLoaded = true;
            App.eventManager.fireLibraryStatusChangeEvent(new LibraryStatusChangeEvent(this, LibraryStatusChangeEvent.FINISHED_LOADING));
        }catch(IOException e){
            App.eventManager.fireLibraryStatusChangeEvent(new LibraryStatusChangeEvent(this, LibraryStatusChangeEvent.FAILED_TO_LOAD, e));
        }

    }

    public String formatID(String s){
        return s.replaceAll("\\s+", "_").toLowerCase();
    }

    public void addResourceDirectory(File... file){
        resources.addAll(Arrays.asList(file));
    }

    public void removeResourceDirectory(File file){
        resources.remove(file);
    }

    public static boolean hasMP3FileProperTags(File file){
        try {
            Mp3File mp3File = new Mp3File(file);
            if(!mp3File.hasId3v1Tag() || !mp3File.hasId3v2Tag())
                return false;
            else if(mp3File.getId3v2Tag().getTitle() == null || mp3File.getId3v2Tag().getAlbum() == null || mp3File.getId3v2Tag().getArtist() == null || mp3File.getId3v2Tag().getTrack() == null){
                return false;
            }
        } catch (IOException | UnsupportedTagException | InvalidDataException e) {
            return false;
        }
        return true;
    }

    public static HashMap<String, Object> getTagsFromMP3File(File file){
        HashMap<String, Object> map = new HashMap<>();

        try {
            Mp3File mp3File = new Mp3File(file);

            if(mp3File.hasId3v1Tag() || mp3File.hasId3v2Tag()){
                map.put(TAG_TITLE, mp3File.getId3v2Tag().getTitle());
                map.put(TAG_ALBUM, mp3File.getId3v2Tag().getAlbum());
                map.put(TAG_ARTIST, mp3File.getId3v2Tag().getArtist());
                map.put(TAG_TRACK, mp3File.getId3v2Tag().getTrack());
                map.put(TAG_DURATION, mp3File.getLengthInSeconds()+"");

                //if there's no album image assigned -> assign default image
                if(mp3File.getId3v2Tag().getAlbumImage() == null){
                    byte[] img = Files.readAllBytes(NULL_ALBUM_IMAGE_PATH);
                    map.put(TAG_ALBUM_IMAGE, img);
                }else{
                    map.put(TAG_ALBUM_IMAGE, mp3File.getId3v2Tag().getAlbumImage());
                }

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

    public Path getLoadedLibraryPath() {
        return loadedLibraryPath;
    }

    public void dispose(){
        isLoaded = false;
        gSongs.clear();
        gAlbums.clear();
        gArtists.clear();
        resources.clear();
    }

    public Path getLastBuiltLibraryPath() {
        return lastBuiltLibraryPath;
    }
}
