package info.zhufree.ebook;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;


/**
 * Created by admin on 2016/9/7.
 */
public class BtnGroup extends RelativeLayout{

    // 定义各个控件及其属性
    private Button playButton, pauseButton, stopButton;
    private LayoutParams playParams, pauseParams, stopParams;

    private BtnGroupClickListener listener;
    private MediaPlayer mp = new MediaPlayer();
    boolean isPause = false;

    // 给按钮添加点击事件
    // 创建接口，供使用时实例化一个监听器
    public interface BtnGroupClickListener {
        public void playClick();
        public void pauseClick();
        public void stopClick();
    }

    // 添加点击事件监听器
    public void setOnBtnGroupClickListener(BtnGroupClickListener listener) {
        this.listener = listener;
    }

    public BtnGroup(final Context context, final Uri soundPath) {
        super(context);
        try {
            mp.setDataSource(context, soundPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });

        // 实例化控件
        playButton = new Button(context);
        pauseButton = new Button(context);
        stopButton = new Button(context);

        // 给控件添加获取到的属性值
        playButton.setText("播放");
        pauseButton.setText("暂停");
        stopButton.setText("停止");


        // 以LayoutParams的方式设置布局属性，并把控件添加到整个组件中
        playParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        playParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, TRUE);
        addView(playButton, playParams); //加入控件中

        pauseParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        pauseParams.addRule(RelativeLayout.CENTER_IN_PARENT, TRUE);
        addView(pauseButton, pauseParams);

        stopParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        stopParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, TRUE);
        addView(stopButton, stopParams);



        // 当按钮被点击时，调用监听器的方法
        playButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.playClick();
                mp.reset();
                try {
                    mp.setDataSource(context, soundPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                    }
                });
                mp.prepareAsync();
                if(isPause){
                    pauseButton.setText("暂停");
                    isPause = false;//设置暂停标记变量的值为false
                }
                pauseButton.setEnabled(true);//“暂停/继续”按钮可用
                stopButton.setEnabled(true);//"停止"按钮可用
                playButton.setEnabled(false);//“播放”按钮不可用
            }
        });

        pauseButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.pauseClick();
                if(mp.isPlaying()&&!isPause){
                    mp.pause();//暂停播放
                    isPause = true;
                    ((Button)view).setText("继续");
                    playButton.setEnabled(true);//“播放”按钮可用
                }else{
                    mp.start();//继续播放
                    ((Button)view).setText("暂停");
                    isPause = false;
                    playButton.setEnabled(false);//“播放”按钮不可用
                }
            }
        });

        stopButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.stopClick();
                mp.stop();//停止播放
                pauseButton.setEnabled(false);//“暂停/继续”按钮不可用
                stopButton.setEnabled(false);//“停止”按钮不可用
                playButton.setEnabled(true);//“播放”按钮可用
            }
        });
    }
}