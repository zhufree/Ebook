package info.zhufree.ebook;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private AssetManager mAssetManager;
    private Chronometer et_time;

    private SeekBar sb;
    private static final String TAG = "output";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAssetManager = getAssets();
        final TextView chap_title = new TextView(this);
        try {
            String chaps[] = mAssetManager.list("");
            Log.e(TAG, String.valueOf(chaps.length));
            for(int i = 0; i < chaps.length; i++) {
                if (chaps[i].endsWith(".txt")) {
                    Button chap_btn = new Button(this);


                    chap_btn.setText(chaps[i]);
                    Log.e(TAG, chaps[i]);
                    final String cur_chap = chaps[i];
                    chap_btn.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            Log.e(TAG, cur_chap);
                            handle_text(chap_title, cur_chap, "pic", "sound", "video");
                        }
                    });
                    ((LinearLayout) this.findViewById(R.id.chaplist)).addView(chap_btn);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //按章读取，分段插入TextView
    public void handle_text(TextView chap_title, String filename,
                            String img_keyword, String sound_keyword,
                            String video_keyword){
        ((LinearLayout) this.findViewById(R.id.textbox)).removeAllViews();
        try{
            InputStream chapInp = mAssetManager.open(filename);
            BufferedReader chapInpBufReader = new BufferedReader(new InputStreamReader(chapInp));
            String eachline = chapInpBufReader.readLine();
            chap_title.setTextSize(40);
            chap_title.setText(eachline);
            chap_title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            ((LinearLayout) this.findViewById(R.id.textbox)).addView(chap_title);

            ArrayList<String> lines = new ArrayList<String>();  //实例化一个数组装文章段落
            while (eachline != null) {
                if(eachline.length() > 0){
                    lines.add(eachline);
                }
                eachline = chapInpBufReader.readLine();
            } //读段落,存入数组
            lines.remove(lines.get(0));//删除标题段落（默认为第一段）

            //设置正则匹配符
            Pattern img_pat = Pattern.compile("\\((\\S+?)\\)\\[" + img_keyword + "(\\S+?)\\]");
            Pattern sound_pat = Pattern.compile("\\((\\S+?)\\)\\[" + sound_keyword + "(\\S+?)\\]");
            Pattern video_pat = Pattern.compile("\\((\\S+?)\\)\\[" + video_keyword + "(\\S+?)\\]");
            Log.e(TAG, img_pat.toString());
            //遍历段落转换资源

            for(String line: lines){
                Matcher img_mat = img_pat.matcher(line);
                Matcher sound_mat = sound_pat.matcher(line);
                Matcher video_mat = video_pat.matcher(line);

                //匹配图片
                if(img_mat.find()){
//                    Log.e(TAG, img_mat.group(0));
//                    String img_text = img_mat.group(1);
                    String img_id = img_mat.group(2);
                    String img_filename = img_keyword + img_id;
//                    Log.e(TAG, img_filename);
                    //插入图片
                    ImageView image_view = new ImageView(this);
                    int imgfile_id = R.drawable.class.getField(img_filename).getInt(new R.drawable());
                    Drawable image = getResources().getDrawable(imgfile_id);
                    image_view.setImageDrawable(image);
                    ((LinearLayout) this.findViewById(R.id.textbox)).addView(image_view);
                    //段落文字去掉匹配标示
                    line = line.replace(img_mat.group(0), img_mat.group(1));
                }
                if(sound_mat.find()){
                    final Button bt_play, bt_pause, bt_stop;
                    String sound_id = sound_mat.group(2);
                    String sound_filename = sound_keyword + sound_id;
                    Log.e(TAG, sound_filename);
                    final Uri soundfile_uri = Uri.parse("android.resource://" + getPackageName() + "/raw/" + sound_filename);
                    final MediaPlayer mp = new MediaPlayer();
                    mp.setDataSource(this, soundfile_uri);
                    bt_play = new Button(this);
                    bt_play.setText("播放");
                    bt_play.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                // 采用异步的方式
                                mp.prepareAsync();
                                mp.start();
                                Log.e(TAG, String.valueOf(mp.isPlaying()));
                                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    mp.release();
                                }
                            });
                        }
                    });
                    bt_pause = new Button(this);
                    bt_pause.setText("暂停");
                    bt_pause.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mp.isPlaying()) {
                                mp.pause();
                                bt_pause.setText("继续");
                                Log.e(TAG, String.valueOf(mp.getCurrentPosition()));
                            } else {
                                mp.start();
                                bt_pause.setText("暂停");
                            }
                        }

                    });
                    bt_stop = new Button(this);
                    bt_stop.setText("停止");
                    bt_stop.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mp.isPlaying()) {
                                mp.stop();
                                mp.release();
                            }
                        }
                    });
                    ((LinearLayout) this.findViewById(R.id.textbox)).addView(bt_play);
                    ((LinearLayout) this.findViewById(R.id.textbox)).addView(bt_pause);
                    ((LinearLayout) this.findViewById(R.id.textbox)).addView(bt_stop);
                    // 为播放器注册

                }
                if(video_mat.find()){
                    String video_id = video_mat.group(2);
                    String video_filename = video_keyword + video_id;
                    line = line.replace(video_mat.group(0), video_mat.group(1));
                    Log.e(TAG, video_mat.group(0) + " " + video_mat.group(1));
                    Uri videofile_uri = Uri.parse("android.resource://" + getPackageName() + "/raw/" + video_filename);
                    VideoView video = new VideoView(this);
                    MediaController mediaco=new MediaController(this);
                    video.setVideoURI(videofile_uri);
                    video.setMediaController(mediaco);
                    mediaco.setMediaPlayer(video);
                    video.requestFocus();
                    Log.e(TAG, videofile_uri.toString());
                    Log.e(TAG, String.valueOf(video.isPlaying()));
                    video.setLayoutParams(new FrameLayout.LayoutParams(1000, 600));
                    ((LinearLayout) this.findViewById(R.id.textbox)).addView(video);
                    Log.e(TAG, "here");

                }
                TextView para = new TextView(this);
                para.setText(line);
                ((LinearLayout) this.findViewById(R.id.textbox)).addView(para);
            }
            chapInp.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}