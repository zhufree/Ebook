package info.zhufree.ebook;

import android.content.Context;
import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by zhufree on 17-2-13.
 */

public class ChapTitleAdapter  extends BaseAdapter {
    private List<String> itemList;
    private String TAG = "output";
    private Context mContext;
    private LayoutInflater listInflater;

    public ChapTitleAdapter(Context context, List<String> ChapTitles) {
        Log.i(TAG, "初始化Adapter");
        itemList = ChapTitles;
        mContext = context;
        listInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //添加数据
    public void addItem(List<String> newDatas) {
        itemList.addAll(newDatas);
        notifyDataSetChanged();
    }

    // 更新数据
    public void refreshItem(List<String> newDatas) {
        itemList = newDatas;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i(TAG, "getView");
        ViewHolder holder;
        if (convertView == null) {
            convertView = listInflater.inflate(R.layout.list_item, null);
            holder = new ViewHolder();
            /*得到各个控件的对象*/
            holder.titleTextView = (TextView) convertView.findViewById(R.id.title_text);
            convertView.setTag(holder);//绑定ViewHolder对象
        }
        else{
            holder = (ViewHolder)convertView.getTag();//取出ViewHolder对象
        }

        final String item = itemList.get(position);
        holder.titleTextView.setText(item);
        // 添加点击事件，跳转到ChapterActivity
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent redirectIntent = new Intent(mContext, ChapterActivity.class);
                redirectIntent.putExtra("title", item);
                mContext.startActivity(redirectIntent);
            }
        });
        return convertView;
    }

    private final class ViewHolder {
        TextView titleTextView;
    }
}
