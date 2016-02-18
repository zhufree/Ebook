//package info.zhufree.ebook;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class ToRes{
    //此程序只做将文本文件按段落分割并存入strings.xml的工作，其他判断交由android程序处理


    public String handle_res(File text_file) throws IOException{
        String encoding = "UTF-8";
        InputStreamReader base_reader = new InputStreamReader(
				    new FileInputStream(text_file), encoding);
        BufferedReader basebufferedReader = new BufferedReader(base_reader);

        String eachline = basebufferedReader.readLine();
        String chap_str = "<string name=\"" +
                text_file.getName().substring(0, text_file.getName().length()-4) + "_title\">" +
                eachline + "</string>\n\t";
        ArrayList<String> lines = new ArrayList<String>();  //实例化一个数组装文章段落
        while (eachline != null) {
            if(eachline.length() > 0){
                lines.add(eachline);
            }
            eachline = basebufferedReader.readLine();
        } //读段落,存入数组
        lines.remove(lines.get(0));//删除标题段落（默认为第一段）

        //遍历段落转换资源
        int i = 0;

        for(String line: lines){
            String para_str = "<string name=\"" + text_file.getName().substring(0, text_file.getName().length()-4) +
            "_para_" + String.valueOf(i) + "\">" + line.trim() + "</string>\n\t";
            i++;
            //加上结尾标识符，添加进文本块
            chap_str += para_str;
        }

        // System.out.println(chap_str);
        base_reader.close();
        return chap_str;
    }
    public static void main(String[] args) throws IOException {
        ToRes tr = new ToRes();//实例化类，设定判定图片，音频，视频的关键词参数
        File text_dir = new File("static/text/");//读取文本文件夹
        System.out.println("读取文件夹。。。");
        String res_str = "<resources>\n\t<string name=\"app_name\">Ebook</string>\n\t";
        if(text_dir.isDirectory()){
            System.out.println("遍历中。。。");
            File[] text_files = text_dir.listFiles();//遍历文本文件，依次执行处理
            for(File text_file: text_files){
                if(!text_file.getName().endsWith("~")){
                    System.out.println("处理" + text_file.getName());
                    res_str += tr.handle_res(text_file);
                }
            }
        }
        res_str += "</resources>";
        //生成xml文件
        System.out.println("处理文件完毕，写入strings.xml");

        FileWriter fileWritter = new FileWriter("values/strings.xml");
        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
        bufferWritter.write(res_str);
        bufferWritter.close();
    }
}
