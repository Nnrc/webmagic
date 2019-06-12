
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.JsonPathSelector;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName : CaiQuan
 * @Auther : gy
 * @Date : 2019/5/10 11:30
 * @Description :
 */
public class CaiQuan implements PageProcessor {
    private static AtomicInteger dognum=new AtomicInteger(1);
    //用来存储cookie信息
    private static Set<Cookie> cookies;

    private Site site = Site.me()
            .setRetryTimes(3)
            .setSleepTime(5000)
            .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36")
            //.addHeader("cookie","cookie: UM_distinctid=16a294908f946e-02ddab14655a8a-8383268-144000-16a294908fa38d; csrftoken=dffbd29b415bc3fae6f1a74cc0cb5411; tt_webid=6680696250788152843; tt_webid=6680696250788152843; WEATHER_CITY=%E5%8C%97%E4%BA%AC; s_v_web_id=30cc19b740cb0360a081b2989825dac2; CNZZDATA1259612802=939357990-1555467246-%7C1557456209")
            .setCharset("utf-8");

    @Override
    public void process(Page page) {

        if(dognum.get()>10){
            //必须要有这个 不然处理数据为空
            page.setSkip(true);
        }else {
            List<String> strings = new JsonPathSelector("$.data").selectList(page.getRawText());
            page.putField("images",strings);
            String url="https://www.toutiao.com/api/search/content/?aid=24&app_name=web_search&offset="+dognum.get()+"&format=json&keyword=%E6%9D%A8%E6%97%B6&autoload=true&count=20&en_qc=1&cur_tab=1&from=search_tab&pd=synthesis&timestamp=1557815731320";
          //  String url="https://www.toutiao.com/api/search/content/?aid=24&app_name=web_search&offset="+dognum.get()+"&format=json&keyword=%E6%9F%B4%E7%8A%AC&autoload=true&count=20&en_qc=1&cur_tab=1&from=search_tab&pd=synthesis&timestamp=1557458612361";
            page.addTargetRequest(url);
            dognum.addAndGet(1);
            new ArrayList<String>();
        }


    }

    @Override
    public Site getSite() {

        //将获取到的cookie信息添加到webmagic中
        for (Cookie cookie : cookies) {
            site.addCookie(cookie.getName().toString(),cookie.getValue().toString());
        }
        return site;
    }
    public static void login(){
        // 登陆
        System.setProperty("webdriver.chrome.driver",
                "D:\\data\\webmagic\\chromedriver_win32\\chromedriver.exe"); // 注册驱动
                 WebDriver driver = new ChromeDriver();
                 driver.get("https://www.toutiao.com/search/?keyword=%E6%9F%B4%E7%8A%AC");
                //获取cookie信息

                cookies = driver.manage().getCookies();
                 //driver.get("https://www.toutiao.com/search/?keyword=%E6%9F%B4%E7%8A%AC");

        driver.close();
    }
    public static void main(String[] args) {
        String url="https://www.toutiao.com/api/search/content/?aid=24&app_name=web_search&offset=0&format=json&keyword=%E6%9D%A8%E6%97%B6&autoload=true&count=20&en_qc=1&cur_tab=1&from=search_tab&pd=synthesis&timestamp=1557815731320";
      //  String url="https://www.toutiao.com/api/search/content/?aid=24&app_name=web_search&offset=0&format=json&keyword=%E6%9F%B4%E7%8A%AC&autoload=true&count=20&en_qc=1&cur_tab=1&from=search_tab&pd=synthesis&timestamp=1557458612361";
              login();
        Spider.create(new CaiQuan())
                .addUrl(url)
                .addPipeline(new ConsolePipeline())
                .addPipeline(new MyDownPipeline())
                .thread(3)
                .run();
    }
}
