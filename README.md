AndroidTwitterClient
====================

An simple Android Twitter Client which provides features like getting time line, composing
a new tweet, query for a single tweet and many more.

User Stories
====================

* [x] User can sign in to Twitter using OAuth login
* [x] User can view the tweets from their home timeline
* [x] User should be displayed the username, name, and body for each tweet
* [x] User should be displayed the relative timestamp for each tweet "8m", "7h"
* [x] User can view more tweets as they scroll with infinite pagination
* [x] Optional: Links in tweets are clickable and will launch the web browser (see autolink)
* [x] User can compose a new tweet
* [x] User can click a “Compose” icon in the Action Bar on the top right
* [x] User can then enter a new tweet and post this to twitter
* [x] User is taken back to home timeline with new tweet visible in timeline
* [x] Optional: User can see a counter with total number of characters left for tweet
* [x] Extra : It won't allow to post if the tweet size is above threshold.

The following advanced user stories are optional:

* [x] Advanced: User can refresh tweets timeline by pulling down to refresh (i.e pull-to-refresh)
* [x] Advanced: User can tap a tweet to display a "detailed" view of that tweet
* [x] User can open the twitter app offline and see last loaded tweets
* [x] Tweets are persisted into sqlite and can be displayed from the local DB
* [x] Advanced: Improve the user interface and theme the app to feel "twitter branded"
* [x] Bonus: User can see embedded image media within the tweet detail view
* [x] Bonus: Compose activity is replaced with a modal overlay

Known Bug
====================
* [x] For detailed view of a tweet, there might be change in data from previous load, like
change in favorite count etc. For that I am calling url for specific tweet with id/id_str
but, due to some reason it is  sometime returning the neighbor tweet.
Easy fix for this would be to parce the Tweet object, but in that case we will miss the
updated tweet info.

####Demo(Week 1):
![Video Walkthrough](demo.gif)
