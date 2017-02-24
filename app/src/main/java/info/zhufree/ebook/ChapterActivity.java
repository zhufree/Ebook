package info.zhufree.ebook;

import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChapterActivity extends AppCompatActivity {
    private AssetManager mAssetManager;
    private TextView titleView;
    private String TAG = "ouput";
    LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter);
        mAssetManager = getAssets();
        titleView = new TextView(this);
        String chapTitle = getIntent().getStringExtra("title");
        handle_text(titleView, chapTitle, "pic", "sound", "video");
    }

    //按章读取，分段插入TextView
    public void handle_text(TextView chap_title, String filename,
                            String img_keyword, String sound_keyword,
                            String video_keyword){
        try{
            InputStream chapInp = mAssetManager.open(filename);
            BufferedReader chapInpBufReader = new BufferedReader(new InputStreamReader(chapInp));
            String eachline = chapInpBufReader.readLine();
            chap_title.setTextSize(40);
            chap_title.setText(eachline);
            chap_title.setLineSpacing(8, (float)1.5);
            chap_title.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            ((LinearLayout) this.findViewById(R.id.text_box)).addView(chap_title);

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
                    ((LinearLayout) this.findViewById(R.id.text_box)).addView(image_view);
                    //段落文字去掉匹配标示
                    line = line.replace(img_mat.group(0), img_mat.group(1));
                }
                if(sound_mat.find()){
                    String sound_id = sound_mat.group(2);
                    String sound_filename = sound_keyword + sound_id;
                    Log.e(TAG, sound_filename);
                    final Uri soundfile_uri = Uri.parse("android.resource://" + getPackageName() + "/raw/" + sound_filename);
                    final MediaPlayer mp = new MediaPlayer();
                    mp.setDataSource(this, soundfile_uri);
                    boolean isPause = false;

                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp.release();
                        }
                    });
                    BtnGroup btnGroup = new BtnGroup(this, soundfile_uri);
                    btnGroup.setOnBtnGroupClickListener(new BtnGroup.BtnGroupClickListener() {
                        @Override
                        public void playClick() {
                            Toast.makeText(getBaseContext(), "开始播放", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void pauseClick() {
                            Toast.makeText(getBaseContext(), "暂停/继续", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void stopClick() {
                            Toast.makeText(getBaseContext(), "停止播放", Toast.LENGTH_SHORT).show();
                        }
                    });
                    ((LinearLayout) this.findViewById(R.id.text_box)).addView(btnGroup);
                    // 为播放器注册
                    line = line.replace(sound_mat.group(0), sound_mat.group(1));

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
                    video.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 800));
                    ((LinearLayout) this.findViewById(R.id.text_box)).addView(video);

                }
                TextView para = new TextView(this);
                para.setText(line);
                para.setTextSize(20);
                para.setLineSpacing(8, (float)1.2);
                para.setLayoutParams(textParams);
                ((LinearLayout) this.findViewById(R.id.text_box)).addView(para);
            }
            chapInp.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
