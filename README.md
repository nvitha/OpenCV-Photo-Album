Hello team, this repository currently contains an example from the OpenCV Android SDK, "tutorial-1-camerapreview". If you successfully build it, you should see a camera preview with the FPS and image dimensions in the upper left corner. 

What Needs to Be Done:
QUERYING -- l 86 of MainActivity.java : You get an int (pos) from the user's click. This indicates the position in the spinner; i.e. which query to execute. Use this to execute a function which does the relevant query (e.g. order by red, select only photos with circles, etc.), then: (1) change the contents of ArrayList<File> pictures, to reflect the output of the query, and (2) reset the currentPictureIndex.

INSERTING -- CameraView.java has the functions getStatistics(), edgeDetection(), and circleDetection(). These are void right now; change them to whatever return type you want, call them after the photo is taken, then store there values in the DBMS.

You shouldn't need to change any XML or go into PhotoActivity or PhotoMath.
