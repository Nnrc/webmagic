import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @ClassName : DownUtil
 * @Auther : gy
 * @Date : 2019/5/8 17:58
 * @Description :
 */
public class DownUtil {
    //下载图片1
    public static void downloadPicture(String u, String name) {
        //将下载的图片保存在 E:\spider 路径中
        String baseDir = "D:\\log\\";
        URL url = null;
        try {
            url = new URL(u);
            DataInputStream dataInputStream = new DataInputStream(url.openStream());

            File file = new File(baseDir + name +".jpg");

            FileOutputStream fileOutputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024 * 50];
            int length;
            while ((length = dataInputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, length);
            }
            System.out.println("已经下载：" + baseDir + name);
            dataInputStream.close();
            fileOutputStream.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void CaiquandownloadPicture(String u, String name) {
        //将下载的图片保存在 E:\spider 路径中
        String baseDir = "D:\\log\\";
        URL url = null;
        //处理json序列化
        JSONArray imglist = JSONArray.parseArray(u);
        try {

            for (int i = 0; i < imglist.size(); i++) {
                JSONObject nowObject= (JSONObject) imglist.get(i);
                String url2 = (String) nowObject.get("url");
                String namenow=baseDir+name+"\\"+i+".jpg";
                //获得网络数据流
                url = new URL(url2);
                DataInputStream dataInputStream = new DataInputStream(url.openStream());
                //创建本地文件夹以及文件流
                File file = mkdir(namenow);
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                byte[] buffer = new byte[1024 * 50];
                int length;
                while ((length = dataInputStream.read(buffer)) > 0) {
                    fileOutputStream.write(buffer, 0, length);
                }
                System.out.println("已经下载：" + namenow);
                dataInputStream.close();
                fileOutputStream.close();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static File mkdir(String i) throws IOException {
        // 指定路径如果没有则创建并添加
        File file = new File(i);
        //获取父目录
        File fileParent = file.getParentFile();
        //判断是否存在
        if (!fileParent.exists()) {
           // 创建父目录文件
            fileParent.mkdirs();
        }
        file.createNewFile();
        return  file;
    }
}
