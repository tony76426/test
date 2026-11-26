package tw.motif.player;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.*;
import androidx.media3.common.*;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.*;
@androidx.media3.common.util.UnstableApi
public class PlaybackService extends MediaSessionService {
 private MediaSession session; private ExoPlayer player; private final Handler h=new Handler(Looper.getMainLooper());
 private final Runnable save=new Runnable(){public void run(){if(player!=null)Store.savePosition(PlaybackService.this,player);h.postDelayed(this,2000);}};
 @Override public void onCreate(){super.onCreate();player=new ExoPlayer.Builder(this).build();player.setAudioAttributes(new AudioAttributes.Builder().setUsage(C.USAGE_MEDIA).setContentType(C.AUDIO_CONTENT_TYPE_MUSIC).build(),true);player.setHandleAudioBecomingNoisy(true);player.setWakeMode(C.WAKE_MODE_LOCAL);player.addListener(new Player.Listener(){@Override public void onEvents(Player p,Player.Events e){if(e.contains(Player.EVENT_TIMELINE_CHANGED)||e.contains(Player.EVENT_MEDIA_ITEM_TRANSITION)||e.contains(Player.EVENT_PLAY_WHEN_READY_CHANGED)||e.contains(Player.EVENT_POSITION_DISCONTINUITY)||e.contains(Player.EVENT_SHUFFLE_MODE_ENABLED_CHANGED)||e.contains(Player.EVENT_REPEAT_MODE_CHANGED))Store.save(PlaybackService.this,p);}});
 PendingIntent pi=PendingIntent.getActivity(this,0,new Intent(this,MainActivity.class),PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_UPDATE_CURRENT);
 session=new MediaSession.Builder(this,player).setSessionActivity(pi).setCallback(new MediaSession.Callback(){
 @Override public com.google.common.util.concurrent.ListenableFuture<java.util.List<MediaItem>> onAddMediaItems(MediaSession s,MediaSession.ControllerInfo c,java.util.List<MediaItem> items){java.util.List<MediaItem> resolved=new java.util.ArrayList<>();for(MediaItem m:items){android.net.Uri u=m.requestMetadata.mediaUri;if(u==null&&m.localConfiguration!=null)u=m.localConfiguration.uri;if(u!=null&&"content".equals(u.getScheme()))resolved.add(m.buildUpon().setUri(u).build());}return com.google.common.util.concurrent.Futures.immediateFuture(resolved);}
 }).build();h.post(save);}
 @Override public MediaSession onGetSession(MediaSession.ControllerInfo info){return session;}
 @Override public void onDestroy(){h.removeCallbacks(save);if(player!=null)Store.save(this,player);if(session!=null)session.release();if(player!=null)player.release();super.onDestroy();}
}
