# TwitterVisualizer
Displays tweets happening around you on a google map, using REST API. This is an example of Twitter application-only-authentication for making queries to the search REST API in an android application. Twitter data has been queried for 
getting the geo-tagged tweets that can be displayed on a map. Below is a high-level description of how to query the worldwide
conversations happenning on twitter.

# Twitter Platform
With so many conversations happening on Twitter every second, Twitter Platform provides different ways to access these 
conversations. Check out this link - https://dev.twitter.com/overview/documentation and choose what best suits your requirement.

Fabric!

An SDK which can be used for integrating mobile apps(both android and iOS) with:

1. Mobile App analytics and crash reporting
2. User login, sharing app data on Twitter as tweets, retweeting and monetization using MoPub
3. Customized views for displaying tweets by making request to Rest API 

Twitter for websites!

This suite of tools can be used for:

1. Embedding buttons, widgets, tweets, timelines including follow and tweet button to your website.

Cards!

Add rich photos, videos or media when you links are shared on Twitter to drive additional traffic to your website, iOS, 
or Android app.

OAuth!

Just a way of authorizing the request you are sending to Twitter API to retrieve twitter data.

MoPub and Ads API!

You can use these for monetization purposes by integrating advertisements in your app.

Streaming and REST API!

For having a read/write access to Twitter data, you need to make a authorized request to one of these API's. 
In a way these API's provide programmatic access to Twitter data without having to use any of the other ways. 
It is at the base of twitter integration for reading and writing the twitter data.

Querying Twitter data using Twitter API's is main focus of this application.

#Difference between streaming and REST API
Say for example, you need to show 10 recent tweets based on a search keyword every time user presses a button.
For this one-time business kind of situation, you should use REST API. On press of button, a connection is established
to send an authorized request to REST API and server responds with a result in JSON, after which connection is closed. 

On the contrary, in a situation where user is following a football match, you need to display tweets for whole duration
of match. Here, you need a long-lived connection to retrieve tweets continuosly for a long duration, so you can go with
streaming API instead, which is more suitable for real-time querying.

For more details check this out ----https://dev.twitter.com/streaming/overview

#Using API's to retrieve data
If you decide to work with the Twitter API's, below are some concepts involved in there usage:

1. Authentication
2. Creating and submitting requests
3. Retrieving results

- Authentication!

  So Twitter is not going to provide the data just to anyone, you need to authenticate the request before submitting. There 
  can be two ways of doing that.

1. User Authentication

  This kind of authentication is required to submit request on behalf of user, for e.g. posting a tweet. It has a user-context 
  and requires user's login credentials for authentication. As of latest version API 1.1, you need to perform user-based 
  authentication using OAuth 1.0a.
  
  https://dev.twitter.com/oauth/overview/authorizing-requests

2. Application-ony authentication

  This kind of authentication is required when you don't need a user's reference to search tweets. The source code provided 
  in this repository is a good example of this authentication. Here, I want to display tweets in 100km radius of anyone who 
  uses the app, so no need of user credentials.

  As of latest version API 1.1, you need to perform application only authentication using OAuth 2
  
  This process comprises of following steps:
  
  - Sign in/sign up on twitter and go to link https://dev.twitter.com/apps to create new app.
  
  - Enter your app credentials, accept agreement and create new app.
  
  - Click on your app to get your consumer API key and secret.
  
  - These credentials are vital and will be used when you create a request for getting a bearer token, which again will be used for autheticating search api
  requests.
  
  https://dev.twitter.com/oauth/reference/post/oauth2/token
  
  You can also use both of these authentications at the same time

- Creating and Submitting requests!!

  Whenever you make requests to Twitter API, you need to hit a specific URL for performing a specific task. Basically, this 
  is how REST API works as it returns representations of the resource identified in your request. 
  
  So for getting the bearer token for app authentication, first you need to hit this URL -- https://api.twitter.com/oauth2/token 
  which will be the host, and to that you need to append your API key and secret, and an entity which in this case is 
  grant-type-credentials. For more information check out this link -- https://dev.twitter.com/oauth/reference/post/oauth2/token
  
  and for user based authentication check out this link --  https://dev.twitter.com/oauth/overview/authorizing-requests
  
  also check this -- https://dev.twitter.com/rest/public, for other types of queries you can submit including GET and POST.


- Retrieving results!!

  So when you submit your query to REST API, you will be returned a representation of resource identified in your query, if 
  your query was correct.
  
  As mentioned in the Twitter documentation, the REST API returns responses in json. You need to parse this json response, to 
  get information about tweets.
  
  This project uses GSON library to parse json results.


