package tw.motif.player;
import android.content.*;
import android.net.Uri;
import androidx.media3.common.*;
import org.json.*;
import java.util.*;
final class Store {
 static SharedPreferences prefs(Context c){return c.getSharedPreferences("motif",0);}
 static class Song {
  final String uri,title,artist,album;
  Song(String u,String t,String a,String b){uri=u;title=t;artist=a;album=b;}
  MediaItem item(){return new MediaItem.Builder().setMediaId(uri).setUri(uri).setRequestMetadata(new MediaItem.RequestMetadata.Builder().setMediaUri(Uri.parse(uri)).build()).setMediaMetadata(new MediaMetadata.Builder().setTitle(title).setArtist(artist).setAlbumTitle(album).build()).build();}
  JSONObject json() throws JSONException{return new JSONObject().put("uri",uri).put("title",title).put("artist",artist).put("album",album);}
  static Song from(JSONObject o){return new Song(o.optString("uri"),o.optString("title"),o.optString("artist"),o.optString("album"));}
 }
 static List<Song> read(Context c,String key){List<Song> out=new ArrayList<>();try{JSONArray a=new JSONArray(prefs(c).getString(key,"[]"));for(int i=0;i<a.length();i++)out.add(Song.from(a.getJSONObject(i)));}catch(Exception ignored){}return out;}
 static String encode(List<Song> s){JSONArray a=new JSONArray();for(Song x:s)try{a.put(x.json());}catch(Exception ignored){}return a.toString();}
 static void write(Context c,String key,List<Song> s){prefs(c).edit().putString(key,encode(s)).apply();}
 static List<Song> queue(Player p){List<Song> q=new ArrayList<>();for(int i=0;i<p.getMediaItemCount();i++){MediaItem m=p.getMediaItemAt(i);MediaMetadata d=m.mediaMetadata;q.add(new Song(m.mediaId,str(d.title),str(d.artist),str(d.albumTitle)));}return q;}
 static String str(CharSequence s){return s==null?"":s.toString();}
 static void save(Context c,Player p){if(p.getMediaItemCount()==0)return;MediaItem m=p.getCurrentMediaItem();if(m==null)return;prefs(c).edit().putString("queue",encode(queue(p))).putString("last",m.mediaId).putLong("pos",Math.max(0,p.getCurrentPosition())).putBoolean("shuffle",p.getShuffleModeEnabled()).putInt("repeat",p.getRepeatMode()).apply();}
 static void savePosition(Context c,Player p){MediaItem m=p.getCurrentMediaItem();if(m!=null)prefs(c).edit().putString("last",m.mediaId).putLong("pos",Math.max(0,p.getCurrentPosition())).apply();}
 static Set<String> favorites(Context c){return new HashSet<>(prefs(c).getStringSet("favorites",Collections.emptySet()));}
 static void favorite(Context c,String id){Set<String> s=favorites(c);if(!s.add(id))s.remove(id);prefs(c).edit().putStringSet("favorites",s).apply();}
}
