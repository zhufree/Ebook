package info.zhufree.ebook;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private AssetManager mAssetManager;
    private static final String TAG = "output:";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAssetManager = getAssets();

        try {
            String chaps[] = mAssetManager.list("");
            Log.e(TAG, String.valueOf(chaps.length));
            for(int i = 0; i < chaps.length; i++) {
                if(chaps[i].endsWith(".txt")) {
                    handle_text(chaps[i], "pic");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //按章读取，分段插入TextView
    public void handle_text(String filename, String img_keyword){
        try{
            InputStream chapInp = mAssetManager.open(filename);
            BufferedReader chapInpBufReader = new BufferedReader(new InputStreamReader(chapInp));
            String eachline = chapInpBufReader.readLine();
            TextView chap_title = new TextView(this);
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
//            Pattern sound_pat = Pattern.compile("\\((\\W+?)\\)\\[" + sound_keyword + "(\\S+?)\\]");
//            Pattern video_pat = Pattern.compile("\\((\\W+?)\\)\\[" + video_keyword + "(\\S+?)\\]");
            Log.e(TAG, img_pat.toString());
            //遍历段落转换资源
            int j = 0;

            for(String line: lines){
                Matcher img_mat = img_pat.matcher(line);
                //匹配图片
                if(img_mat.find()){
                    Log.e(TAG, img_mat.group(0));
//                    String img_text = img_mat.group(1);
                    String img_id = img_mat.group(2);
                    String img_filename = img_keyword + img_id;
                    Log.e(TAG, img_filename);
                    //插入图片
                    ImageView image_view = new ImageView(this);
                    int imgfile_id = R.drawable.class.getField(img_filename).getInt(new R.drawable());
                    Drawable image = getResources().getDrawable(imgfile_id);
                    image_view.setImageDrawable(image);
                    ((LinearLayout) this.findViewById(R.id.textbox)).addView(image_view);
                    //段落文字去掉匹配标示
                    line = line.replace(img_mat.group(0), img_mat.group(1));
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
