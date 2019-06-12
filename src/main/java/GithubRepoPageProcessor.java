import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;
import us.codecraft.webmagic.selector.Selectable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName : GithubRepoPageProcessor
 * @Auther : gy
 * @Date : 2019/5/6 15:20
 * @Description :
 */
public class GithubRepoPageProcessor    implements PageProcessor {
    private static AtomicInteger aint=new AtomicInteger(0);
    private  static File f= new File("D:" + File.separator + "test.txt") ;    // 声明File对象
    // 第2步、通过子类实例化父类对象
    static Writer out = null ;    // 准备好一个输出的对象
    private Site site = Site.me().setRetryTimes(10).setSleepTime(1000);
            //.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");

    @Override
    public void process(Page page) {
        List<String> urls = page.getHtml().css("div.pagination").links().regex(".*/search\\?l=Java&p=[0-9]{1,2}&.*").all();
        page.addTargetRequests(urls);
        //List<String> Name = page.getHtml().xpath("//*[@id='js-pjax-container']/div/div/div/ul//li/div/h3/a/text()").all();
        List<Selectable> nodes = page.getHtml().xpath("//*[@id='js-pjax-container']/div/div/div/ul//[@class='repo-list-item']").nodes();
       if(aint.get()==100){
           return;
       }
       synchronized (this) {
           try {
               String line = System.getProperty("line.separator");
               for (Selectable node : nodes) {
                   String sname = node.xpath("//div/h3/a/text()").toString();
                   List<String> all = node.xpath("//div/div[1]/a/text()").all();
                   String nums = node.xpath("//div/div[2]/a/text()").toString();
                   out.write(sname + "             ");
                   out.write(all.toString() + "        ");
                   out.write(nums);
                   out.write(line);
                   aint.addAndGet(1);
               }
               // out.write(Name.toString());
           } catch (Exception e) {
               e.printStackTrace();
           }
       }
    }
    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) throws IOException {
        out = new FileWriter(f)  ;    // 通过对象多态性，进行实例化
        Spider spider = Spider.create(new GithubRepoPageProcessor()).addUrl("https://github.com/search?l=Java&p=1&q=stars%3A%3E1&s=stars&type=Repositories").setExitWhenComplete(false);
       // spider.addPipeline(new ConsolePipeline());
        //spider.addPipeline(new JsonFilePipeline("D:\\log\\"));
/*
        HttpClientDownloader downloader = new HttpClientDownloader(){
            @Override
            protected void onError(Request request) {
                setProxyProvider(SimpleProxyProvider.from(new Proxy("49.86.180.152", 9999)
                ,new Proxy("112.87.68.181", 9999)
                ,new Proxy("60.13.42.101", 9999)
                ,new Proxy("112.87.70.172", 9999)));
            }
        };*/

        HttpClientDownloader downloader = new HttpClientDownloader();
    /*    try {
            List<Proxy> proxies = buildProxyIP();
            System.out.println("请求代理IP： " + proxies.toString());
            downloader.setProxyProvider(new SimpleProxyProvider(proxies));
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        downloader.setProxyProvider(SimpleProxyProvider.from(
                new Proxy("210.5.10.87", 53281)
                ,new Proxy("112.85.169.221", 9999)
                ,new Proxy("140.207.50.246", 51426)
                ,new Proxy("124.205.143.213", 32612)));
        spider.setDownloader(downloader);
        spider.thread(1).start();
        while (true){
            if(aint.get()==100){
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
        spider.close();
        out.close() ;
     }

    private static List<Proxy> buildProxyIP() throws IOException {
        Document parse = Jsoup.parse(new URL("http://www.89ip.cn/tqdl.html?api=1&num=20&port=&address=&isp="), 5000);
        String pattern = "(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+):(\\d+)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(parse.toString());
        List<Proxy> proxies = new ArrayList<Proxy>();
        while (m.find()) {
            String[] group = m.group().split(":");
            int prot = Integer.parseInt(group[1]);
            proxies.add(new Proxy(group[0], prot));
        }
        return proxies;
    }
}
