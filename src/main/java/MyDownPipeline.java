import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.List;

/**
 * @ClassName : MyDownPipeline
 * @Auther : gy
 * @Date : 2019/5/8 18:00
 * @Description :
 */
public class MyDownPipeline implements Pipeline {


    @Override
    public void process(ResultItems resultItems, Task task) {
/*        String url = resultItems.get("图片链接").toString();
        String name = resultItems.get("图片名称").toString();
        if (url == null) {
            resultItems.setSkip(true);
        }
//		String name = resultItems.get("图片链接").toString();
        DownUtil.downloadPicture(url, name);*/

        List<String> strings = resultItems.get("images");
        for (String string : strings) {
            JSONObject items = JSONObject.parseObject(string);
            Object anAbstract = items.get("abstract");
            if (anAbstract != null) {
                String title = (String) items.get("title");
                JSONArray image_list = (JSONArray) items.get("image_list");
                if(title!= null && image_list!= null){
                    DownUtil.CaiquandownloadPicture(image_list.toString(), title);
                }
            }
        }




    }
}
