package com.example.partyPlayer;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class SpotifyScrape extends AsyncTask<Void, Void, String> {

    private static String spotifyUrl = "https://open.spotify.com/search/";

    private String songUri;

    public SpotifyScrape() {
        doInBackground();
    }

    @Override
    protected String doInBackground(Void... params) {
        Document doc = null;

        try {
            doc = Jsoup.connect("https://open.spotify.com/search/watermelon%20man")
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                    .referrer("http://www.google.com")
                    .ignoreHttpErrors(true)
                    .get();
            System.out.println(doc + "yea dude");
            Thread.sleep(1000);
        }
        catch(IOException e) {
            e.printStackTrace();
            return "Failed";
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        Elements elements = doc.getElementsByClass("d9eb38f5d59f5fabd8ed07639aa3ab77-scss _59c935afb8f0130a69a7b07e50bac04b-scss");
        for(Element element: elements) {
            String link = element.attributes().get("href");

            System.out.println(link);
        }
        return "Executed";
    }

    @Override
    protected void onPostExecute(String result) {
        return;
    }
}

