package com.example.demo;

import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.TwitterCredentialsBearer;
import com.twitter.clientlib.TwitterCredentialsOAuth2;
import com.twitter.clientlib.api.TweetsApi;
import com.twitter.clientlib.api.TwitterApi;
import com.twitter.clientlib.model.*;

import java.util.*;

public class TwitterApiExample {

    public static void main(String[] args) {
        /**
         * Set the credentials for the required APIs.
         * The Java SDK supports TwitterCredentialsOAuth2 & TwitterCredentialsBearer.
         * Check the 'security' tag of the required APIs in https://api.twitter.com/2/openapi.json in order
         * to use the right credential object.
         */

        // devi sostituire con il tuo bearer token
        TwitterCredentialsBearer credentials = new TwitterCredentialsBearer("xxx");
        TwitterApi apiInstance = new TwitterApi(credentials);



        // Set the params values
        // la lista completa dei parametri che puoi inlcudere nella query la trovi nella documenatazione e nell'esempio
        String keyword ="cane";
        String query = keyword+" -is:retweet"; // String
        // -is:retweet esclude i retweet (i messaggi che ci sembravano tagliati erano tuti retweet)
        Set<String> tweetFields = new HashSet<>(Arrays.asList("id,author_id,text")); // Set<String> | A comma separated list of Tweet fields to display.

        class tweetrow {
            String tw_id;
            String tw_author_id;
            String tw_text;
            boolean tw_inlcude;
            String tw_us_name; // i campi us_* non esistono nelle risposta alla query api per la ricerca by keyword. Li riempiremo in seguito con la ricerca dell`utente
            String tw_us_description;
            int tw_us_follower_count;
        }

        ArrayList<tweetrow> tw_list =new ArrayList<tweetrow>(); // per presentare i risultati nella gui devi usare l´observable list invece dell`array list come fatto per la risposta dal db
        int twcount =0;

        class userrow {
            String us_author_id;
            String us_name;
            String us_description;
            int us_follower_count;
        }

        ArrayList<userrow> us_list =new ArrayList<userrow>();
        int uscount =0;

        try {
            //query della api per ottenere la lista di tweet in base alla keyword
            Get2TweetsSearchRecentResponse result = apiInstance.tweets().tweetsRecentSearch(query)
                   .maxResults(10)
                   .tweetFields(tweetFields)
                   .execute();
            twcount=result.getMeta().getResultCount();
           if (result.getData() != null) {
               for (Tweet tweet : result.getData()) {
                   tweetrow tw = new tweetrow();
                   tw.tw_id=tweet.getId();
                   tw.tw_author_id=tweet.getAuthorId();
                   tw.tw_text=tweet.getText();
                   tw_list.add(tw);
                   //System.out.println("tweet id "+tweet.getId());
                   //System.out.println("tweet text "+tweet.getText());
               }
           }
           //System.out.println("result full " + result.getData());
        } catch (ApiException e) {
            System.err.println("Exception when calling TweetsApi#tweetsRecentSearch");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
            // qui devi aggiungere l´handling delle exeptions più o meno come fatto per il db
        }

        String author_id_list = null; // stringa con la lista di authot,id da usare per la query

        // stampa di tutti i tweet (array accessibile al di fuori del try)
        for (int i = 0; i < twcount; i++) {
            //System.out.println(i + " tw_list.id "+tw_list.get(i).tw_id);
            //System.out.println(i + " tw_list.author_id "+tw_list.get(i).tw_author_id);
            //System.out.println(i + " tw_list.text "+tw_list.get(i).tw_text);

            if  (i==0) {
                author_id_list = tw_list.get(i).tw_author_id;
            }
            else {
                author_id_list = author_id_list +","+tw_list.get(i).tw_author_id;
            }
        }

        System.out.println(" author_id_list "+author_id_list);

        // Query delle info riguardo gli autori dei tweet

        List<String> ids = Arrays.asList(author_id_list); // List<String> | A list of User IDs, comma-separated. You can specify up to 100 IDs.
        Set<String> userFields = new HashSet<>(Arrays.asList("created_at,description,entities,id,location,name,pinned_tweet_id,profile_image_url,protected,public_metrics,url,username,verified,withheld")); // Set<String> | A comma separated list of User fields to display.
        Set<String> expansions = new HashSet<>(Arrays.asList("public_metrics")); // Set<String> | A comma separated list of fields to expand.
        //Set<String> tweetFields = new HashSet<>(Arrays.asList()); // Set<String> | A comma separated list of Tweet fields to display.


        try {
            Get2UsersResponse result = apiInstance.users().findUsersById(ids)
                    .userFields(userFields)
                    //.expansions(expansions)
                    //.tweetFields(tweetFields)
                    .execute();

            if (result.getData() != null) {

                    for (User user : result.getData()) {
                        userrow us = new userrow();
                        us.us_author_id=user.getId();
                        us.us_description=user.getDescription();
                        us.us_name=user.getName();
                        us.us_follower_count=user.getPublicMetrics().getFollowersCount();
                        us_list.add(us);
                        //System.out.println("tweet id "+tweet.getId());
                        //System.out.println("tweet text "+tweet.getText());

                /*for (User user : result.getData()) {
                    System.out.println("user id "+user.getId());
                    System.out.println("user name "+user.getName());
                    System.out.println("user description "+user.getDescription());
                    System.out.println("user metrics "+user.getPublicMetrics().getFollowersCount());

                    }*/
            }
            }
            //System.out.println(result);



        } catch (ApiException e) {
            System.err.println("Exception when calling UsersApi#findUsersById");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }

        int us_count= us_list.size();
        for (int j = 0; j < us_count; j++) {

        //System.out.println(j+" user.id "+ us_list.get(1).us_author_id);
        //System.out.println(j+" user.name "+ us_list.get(1).us_name);
        //System.out.println(j+" user.description "+ us_list.get(1).us_description);
        //System.out.println(j+" user.fwcount "+ us_list.get(1).us_follower_count);
        //System.out.println(i + " tw_list.author_id "+tw_list.get(i).tw_author_id);
        //System.out.println(i + " tw_list.text "+tw_list.get(i).tw_text);
        }
// adesso hai un oggetto con i tweet ed uno con gli utenti, andiamo ad aggiungere gli attributi dell´utente nell´oggetto con i tweeet

        for (int i = 0; i < twcount; i++) {

            for (int j = 0; j < us_count; j++) {

                if (Objects.equals(tw_list.get(i).tw_author_id, us_list.get(j).us_author_id)) {

                    tw_list.get(i).tw_us_name = us_list.get(j).us_name;
                    tw_list.get(i).tw_us_description = us_list.get(j).us_description;
                    tw_list.get(i).tw_us_follower_count = us_list.get(j).us_follower_count;

                    if (tw_list.get(i).tw_us_follower_count > 100) {
                       tw_list.get(i).tw_inlcude=true;
                    }
                    else {
                       tw_list.get(i).tw_inlcude=false;
                    }
                   }

            }
        }

        for (int i = 0; i < twcount; i++) {
            System.out.println(i + " tw_list.id "+tw_list.get(i).tw_id);
            System.out.println(i + " tw_list.author_id "+tw_list.get(i).tw_author_id);
            System.out.println(i + " tw_list.text "+tw_list.get(i).tw_text);
            System.out.println(i + " tw_list.include "+tw_list.get(i).tw_inlcude);
            System.out.println(i + " tw_list.name "+tw_list.get(i).tw_us_name);
            System.out.println(i + " tw_list.description "+tw_list.get(i).tw_us_description);
            System.out.println(i + " tw_list.follower "+tw_list.get(i).tw_us_follower_count);
            }




    }
}