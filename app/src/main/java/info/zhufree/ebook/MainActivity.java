package info.zhufree.ebook;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.VideoView;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "output";


    private List<String> data = new ArrayList<String>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ListView mListView;
        AssetManager mAssetManager;
        ChapTitleAdapter chapTitleAdapter;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 根据章节生成ListView展示标题，点击ListView跳转ChapterActivity
        Log.i(TAG, "onCreate：");

        // 获取自定义的ListView
        mListView = (ListView) findViewById(R.id.listView);
        Log.i(TAG, "getListView：");
//        mListView.setName(name); // 设置name，在后面分频道获取数据时需要
        mAssetManager = getAssets();
        try {
            String chaps[] = mAssetManager.list("");
            Log.i(TAG, String.valueOf(chaps.length));
            for(String c:chaps) {
                if (c.endsWith(".txt")) {
                    data.add(c.split("\\.")[0]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        chapTitleAdapter = new ChapTitleAdapter(this, data);
        mListView.setAdapter(chapTitleAdapter);
    }

//    public void oldHandle() {
//        mAssetManager = getAssets();
//        final TextView chap_title = new TextView(this);
//        try {
//            String chaps[] = mAssetManager.list("");
//            Log.e(TAG, String.valueOf(chaps.length));
//            for(int i = 0; i < chaps.length; i++) {
//                // 每个章节生成了一个按钮
//                if (x.endsWith(".txt")) {
//                    Button chap_btn = new Button(this);
//                    chap_btn.setText(x);
//                    Log.e(TAG, x);
//                    final String cur_chap = x;
//                    chap_btn.setOnClickListener(new OnClickListener() {
//                        public void onClick(View v) {
//                            Log.e(TAG, cur_chap);
//                            handle_text(chap_title, cur_chap, "pic", "sound", "video");
//                        }
//                    });
//                    ((LinearLayout) this.findViewById(R.id.chaplist)).addView(chap_btn);
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

}