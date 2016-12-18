# Capture400D
Very simple android app to retrieve "on the fly" captured pictures from a Canon EOS 400D.

Thanks to this app I can continue to use my old Canon even if its CF slot is broken.

- Connect your Canon EOS 400D to your android device via USB.
- Turn on the camera.
- If this is the first time: instruct your android device to always use this app with this camera.
- Wait initialization (the app "beeps" at the end of the initialization process).
- Take a picture.
- The picture is downloaded from the camera to the android device (in the folder "pictures").
- You hear another "beep" when the download is complete.
- Take other pictures...
- The app terminates itself when the camera is turned off.

For debug purpose: a new log file is created at the root of the storage each time the app starts. Don't forget to delete these files from times to times as they can take a lot of place.
