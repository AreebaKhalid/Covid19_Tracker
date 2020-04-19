package com.syedaareebakhalid.covid_19tracker;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class BackgroundTask extends AsyncTask {
    @Override
    protected String doInBackground(Object[] gitHubURL) {
        String urlfromgithb="";
        System.out.println(gitHubURL[0].toString());
        try {
            URL url = new URL(gitHubURL[0].toString());
            System.out.println(url);
            Scanner s = new Scanner(url.openStream());
            // read from your scanner
            while (s.hasNextLine()){
                urlfromgithb = urlfromgithb + s.nextLine();
            }
        }
        catch(IOException ex) {
            ex.printStackTrace(); // for now, simply output it.
        }
        if (urlfromgithb == "") {
            urlfromgithb="https://corona.lmao.ninja/v2/";
        }
        return urlfromgithb;
    }
}
