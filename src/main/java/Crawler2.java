import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class Crawler2 {

    public static int countSaved = 1;

    public static String getHTML(String urlToRead) throws Exception {

        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);

        StringBuilder result = new StringBuilder();
        URL url = new URL(urlToRead);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {
            for (String line; (line = reader.readLine()) != null; ) {
                result.append(line);
            }
        }
        String str = result.toString();

        JSONObject obj = new JSONObject(str);
        JSONObject data = obj.getJSONObject("data");
        JSONArray podcasts = data.getJSONArray("podcasts");

        for(int i = 0 ; i < podcasts.length() ; i++) {
            JSONObject p = (JSONObject) podcasts.get(i);

            String uploadedAt = p.getString("uploadedAt").substring(0, 10);

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Date date = formatter.parse(uploadedAt);
            Date boundary = DateParser.finalFormatter.parse("2013-01-01");

            if (date.after(boundary)) {
                int id = p.getInt("id");
                String link = "https://app.podscribe.ai/episode/" + id;


                String transcriptUrl = "https://backend.podscribe.ai/api/episode?id=" + id;

                StringBuilder result2 = new StringBuilder();
                URL url2 = new URL(transcriptUrl);
                HttpURLConnection conn2 = (HttpURLConnection) url2.openConnection();
                conn2.setRequestMethod("GET");
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn2.getInputStream()))) {
                    for (String line; (line = reader.readLine()) != null; ) {
                        result2.append(line);
                    }
                }
                String str2 = result2.toString();

                JSONObject obj2 = new JSONObject(str2);
                JSONObject transcription = obj2.getJSONObject("transcription");
                String text = transcription.getString("text");

                JSONArray textArray = new JSONArray(text);

                StringBuilder word = new StringBuilder();
                for (int k = 0; k < textArray.length(); k++) {
                    JSONObject t = (JSONObject) textArray.get(k);
                    word.append(t.getString("word") + " ");
                }

                System.out.println(word);

                //            Set<String> tokens = StopWordsRemover.removeStopWords(text);
                //            System.out.println(Arrays.toString(tokens.toArray()));

                System.out.println(link + " - " + uploadedAt);

                Save.save(link, DateParser.finalFormatter.format(date), DateParser.finalFormatter.format(today.getTime()), word.toString(), countSaved++);
            }


        }
        return str;
    }

    public static void main(String[] args) throws Exception
    {
        System.out.println(getHTML(args[0]));
    }

}
