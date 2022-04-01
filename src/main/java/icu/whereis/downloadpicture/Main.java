package icu.whereis.downloadpicture;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {
        String url = "https://www.54k5.com/44127.html/";
        String currentDir = System.getProperty("user.dir")+ File.separator;
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.82 Safari/537.36";

        downloadPicture(url, currentDir,"陈亦菲", userAgent);
    }

    public static void downloadPicture(String url, String currentDir, String saveName, String userAgent) throws IOException {
        String savePath = currentDir+saveName+File.separator;

        // 先请求一次拿页码
        Document document0 = Jsoup.connect(url).userAgent(userAgent).get();
        Elements paging = document0.select("div[class=article-paging]");
        int x = 1;
        if (paging.size() > 0) {
            Elements as = paging.select("a");
            x = as.size() + 1; // 当前页没有a标签，所以+1
            System.out.println("共" + x + "页");
        }

        String url2 = url;
        for (int j = 0; j < x; j++) {
            url2 = String.format(url+"%s", (j+1));
            System.out.println("正在下载第"+(j+1)+"页("+url2+")的图片...");

            Document document = Jsoup.connect(url2).userAgent(userAgent).get();
            Elements srcLinks = document.select("img[src$=.jpg]");

            for (int i = 0; i < srcLinks.size(); i++) {
                Element link = srcLinks.get(i);
                String imgUrl = link.attr("src");

                System.out.println("第"+(i+1)+"张图片链接："+imgUrl);

                // 不加ignoreContentType(true)会报下面的错
                // org.jsoup.UnsupportedMimeTypeException: Unhandled content type. Must be text/*, application/xml, or application/xhtml+xml.
                Connection.Response response = Jsoup.connect(imgUrl).ignoreContentType(true).execute();
                byte[] bodyBytes = response.bodyAsBytes();

                String imgName = getPictureName(imgUrl);
                saveImage(bodyBytes, savePath, imgName);
            }
        }

    }

    public static String getPictureName(String url) {
        // int lastIndex = url.lastIndexOf(".");
        int lastIndexLine = url.lastIndexOf("/");
        return url.substring(lastIndexLine+1);
    }

    public static void saveImage(byte[] imgBytes, String filePath, String filename) {
        BufferedOutputStream out = null;
        FileOutputStream fos = null;
        File file = null;
        File dir = new File(filePath);

        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            file = new File(filePath+File.separator+filename);
            fos = new FileOutputStream(file);
            out = new BufferedOutputStream(fos);
            out.write(imgBytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(out != null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
