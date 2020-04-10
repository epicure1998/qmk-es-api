package com.qmk.utils;

import com.qmk.pojo.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;

public class HtmlParseUtils {
//    public static void main(String[] args) throws IOException {
//        parse("数学" );
//    }
    public static LinkedList<Content> parse(String str) throws IOException {
        String url = "https://search.jd.com/Search?keyword="+str;
        Document document = Jsoup.parse(new URL(url), 30000);
        Element element = document.getElementById("J_goodsList");
       // System.out.println(element.html());
        Elements elements = element.getElementsByTag("li");

        LinkedList<Content> al = new LinkedList<Content>();
        for (Element el : elements) {
            String img = el.getElementsByTag("img").eq(0).attr("source-data-lazy-img");
            String name = el.getElementsByClass("p-name").eq(0).text();
            String price = el.getElementsByClass("p-price").eq(0).text();
            al.add(new Content(price, name, img));
        }
//        for (Content temp : al) {
//            System.out.println("========================================================");
//            System.out.println(temp.getName());
//            System.out.println(temp.getPrice());
//            System.out.println(temp.getImg());
//        }
        return al;
    }
}
