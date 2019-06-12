import com.alibaba.fastjson.JSONObject;
import sun.net.www.protocol.http.HttpURLConnection;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @ClassName : aaa
 * @Auther : gy
 * @Date : 2019/5/6 16:33
 * @Description :
 */
public class aaa implements PageProcessor {
    private  static File f= new File("D:" + File.separator + "test.txt") ;    // 声明File对象
    // 第2步、通过子类实例化父类对象
    static  Writer out = null ;    // 准备好一个输出的对象
    private static AtomicInteger aint=new AtomicInteger(0);
    private Site site = Site.me().setRetryTimes(3).setSleepTime(100).setTimeOut(10000);
     private  AtomicInteger aa= new  AtomicInteger();

    @Override
    public void process(Page page) {
        page.addTargetRequests(page.getHtml().links().regex("(https://github\\.com/[\\w\\-]+/[\\w\\-]+)").all());
        page.addTargetRequests(page.getHtml().links().regex("(https://github\\.com/\\w+/\\w+)").all());
        //System.out.println(page.getUrl().regex("https://github\\.com/(\\w+)/.*").toString());
        page.putField("author", page.getUrl().regex("https://github\\.com/(\\w+)/.*").toString());
        page.putField("name", page.getHtml().xpath("//h1[@class='public']/strong/a/text()").toString());
        if (page.getResultItems().get("name") == null) {
            //skip this page
            page.setSkip(true);
        }
        if(aint.get()==10){
            page.setSkip(true);
        }
        page.putField("readme", page.getHtml().xpath("//div[@id='readme']/tidyText()"));

        // 第3步、进行写操作
        synchronized (this) {
            if (page.getResultItems().get("name") == null) {

            } else {
                if(aint.get()==10){
                    return;
                }
            String line = System.getProperty("line.separator");
            String str = "author " + page.getUrl().regex("https://github\\.com/(\\w+)/.*").toString();
            String name = "   name  " + page.getHtml().xpath("//h1[@class='public']/strong/a/text()").toString();
            try {
                out.write(str);
                out.write(name);
                out.write(line);
            } catch (IOException e) {
                e.printStackTrace();
            }
                aint.addAndGet(1);
        }

    }
        // 第4步、关闭输出流
            // System.out.println(page.toString());
            //aa.addAndGet(1);
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        out = new FileWriter(f)  ;    // 通过对象多态性，进行实例化



        Spider spider = Spider.create(new aaa()).addUrl("https://github.com/code4craft");
       // spider.addPipeline(new ConsolePipeline());
        spider.addPipeline(new JsonFilePipeline("D:\\log\\"));
        spider.thread(5).start();
        while (true){
            if(aint.get()==10){
                break;
            }
        }
        spider.stop();
        while(true){
           // System.out.println(spider.getThreadAlive());
            if(spider.getThreadAlive()==0){
                break;
            }
        }
        /*while(true){
            if ("Stopped".endsWith(spider.getStatus().name())){
                break;
            }
        }*/
        spider.close();
        out.close() ;
    }


}
