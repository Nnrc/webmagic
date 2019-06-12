import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Json;
import us.codecraft.webmagic.selector.JsonPathSelector;

import java.util.LinkedList;
import java.util.List;

/**
 * @ClassName : DownProssor
 * @Auther : gy
 * @Date : 2019/5/8 18:01
 * @Description :
 */
public class DownProssor implements PageProcessor {

    private Site site = Site.me()
            .setRetryTimes(3)
            .setSleepTime(1000)
            .setCharset("utf-8");

    //存储菜谱名的 List
    private static List<String> picName = new LinkedList<String>();

    //存储菜谱图片下载地址的 List
    private static List<String> picUrl = new LinkedList<String>();
    @Override
    public void process(Page page) {
        //列表页上的链接地址：//*[@id="listtyle1_list"]/div/a   //*[@id="container"]/div[1]/ul/li/a
        //翻页的url：//*[@id="listtyle1_w"]/div[2]/div/a[6]/@href
        //文章详情页的url：https://www.meishij.net/zuofa/shuangweijuanchangfen.html
        //https://www\\.meishij\\.net/zuofa/\\w+\\.html
        //详情页上的图片：/html/body/div[7]/div[1]/div[1]/div[1]/img
        //详情页文章标题：//*[@id="tongji_title"] //div[@class="info1"]/h1/a/text()

        //判断页面是否为文章页
       // List<String> selectList = new JsonPathSelector("$.data").selectList(page.getRawText());
        if (!page.getUrl().regex("https://www\\.meishij\\.net/zuofa/\\S+\\.html").match()) {
            // 列表页上的详情页链接
            page.addTargetRequests(
                    page.getHtml().xpath("//div[@class=\"listtyle1_list clearfix\"]/div/a/@href").all());
            // 翻页url
            page.addTargetRequests(
                    page.getHtml().xpath("//*[@id=\"listtyle1_w\"]/div[2]/div/a[@class=\"next\"]/@href").all());
        } else {

            String picname = page.getHtml().xpath("//a[@id=\"tongji_title\"]/text()").get();
            String picURL = page.getHtml().xpath("//div[@class=\"cp_headerimg_w\"]").css("img", "src").toString();
            page.putField("图片名称", picname);
            page.putField("图片链接", picURL);

            if (page.getResultItems().get("图片名称")==null || page.getResultItems().get("图片链接")==null){
                //skip this page
                page.setSkip(true);
            }
            picName.add(picname);
            picUrl.add(picURL);

//			page.putField("用料", page.getHtml().xpath("//div[@class=\"materials_box\"]/div/ul/li/allText()").all().toString());
//			page.putField("步骤", page.getHtml().xpath("//div[@class=\"editnew edit\"]/div/allText()").all().toString());

        }

    }

    @Override
    public Site getSite() {
        // TODO Auto-generated method stub
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new DownProssor())
                .addUrl("https://www.meishij.net/china-food/caixi/yuecai/")
                .addPipeline(new ConsolePipeline())
                .addPipeline(new MyDownPipeline())
                .thread(3)
                .run();

        System.out.println(picName.size());
    }
}
