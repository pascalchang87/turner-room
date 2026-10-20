# The Turner Room

A night gallery for a projector. The wall stays pitch black and only the paintings appear, each in a real-looking museum frame with an optional wall label. You hang and arrange them from a phone or laptop, and the projector follows along.

```
web/   the gallery website (deploy to Vercel)
tv/    the Google TV app and screensaver (built for you by GitHub)
```

## Try it right now

Open `web/index.html` in Chrome or Safari on your Mac. Everything works except syncing to the projector: search the collections, add paintings, drag them around, then press **Show on wall**. Changes are saved in that browser.

## 1. Put the project on GitHub

Create a new repository on github.com and push this folder to it. GitHub Desktop is the easiest way on a Mac: *File → Add Local Repository*, choose this folder, then *Publish repository*.

Make the repository **public** if you want the projector to download the app directly. The site and the app contain nothing private; the edit key lives in Vercel, not in the code.

## 2. Publish the website on Vercel

1. On vercel.com, choose **Add New → Project** and import the repository.
2. Set **Root Directory** to `web`. Leave everything else as it is and press **Deploy**.
3. Open the project's **Storage** tab, create an **Upstash Redis** database (the free plan is plenty) and connect it to this project.
4. Under **Settings → Environment Variables**, add `EDIT_KEY` with any passphrase you like. Anyone with this key can change the gallery.
5. Go to **Deployments** and redeploy, so the database and key take effect.

Now open your site (e.g. `https://turner-room.vercel.app`) on your phone, go to **Settings → Sync with the projector**, enter the edit key and press **Save key**. The status line should say *Synced with the projector*. Do the same on any other phone or laptop you want to edit from.

## 3. Build the Google TV app

1. In the GitHub repository, open **Settings → Secrets and variables → Actions → Variables** and add a variable `GALLERY_URL` with your site address plus `?show=1`, e.g. `https://turner-room.vercel.app/?show=1`. This is optional, but it means you never have to type the address on the projector.
2. Open the **Actions** tab, choose **Build Google TV app** and press **Run workflow**. It takes about three minutes.
3. When it finishes, the file appears under **Releases** as `turner-room.apk`.

## 4. Install it on the projector

1. Open the **Downloader** app on the projector.
2. Enter `https://github.com/YOUR-NAME/YOUR-REPO/releases/download/tv-latest/turner-room.apk` and install it. Allow Downloader to install unknown apps if it asks.
3. **Turner Room** now appears in your apps. Open it and the gallery fills the wall.

On the remote, **left/right** changes walls. **Hold OK** for a second to change the gallery address.

If you skipped the `GALLERY_URL` variable, the app asks for the address on first launch. The keyboard in the Google TV app on your phone makes this quick.

## 5. Use it as the screensaver (optional)

Google TV's settings only list Google's own screensavers, so this one is switched on from your Mac, once:

1. On the projector: **Settings → System → About**, select **Android TV OS build** seven times to unlock Developer options. Then in **Settings → System → Developer options**, turn on **USB debugging** (and **Wireless debugging** if it is listed).
2. On the Mac, install the Android tools: `brew install android-platform-tools`
3. Connect, using the projector's IP address from its network settings:
   `adb connect 192.168.1.50:5555`
   (With *Wireless debugging*, first run `adb pair` with the IP, port and code it shows.) Accept the prompt on the projector.
4. Note down the current screensaver so you can switch back later:
   `adb shell settings get secure screensaver_components`
5. Switch to the gallery:
   ```
   adb shell settings put secure screensaver_components app.turnerroom.tv/.GalleryDream
   adb shell settings put secure screensaver_enabled 1
   ```

To undo it, run the same `put` command with the value you noted in step 4.

The screensaver starts after the projector's usual idle delay. XGIMI's own power-saving or auto-standby settings can still turn the laser off; turn those off if you want the art to stay up.

## How the pieces fit together

- The site stores the gallery in the Redis database. Anyone can view it; saving needs the edit key.
- The projector opens the site in wall mode (`?show=1`) and checks for changes every minute, so a painting you add from your phone appears on the wall shortly after.
- Without a database (for example, opening the file locally), everything is kept in the browser instead. **Settings → Copy link with this layout** makes a link that carries the whole gallery, which you can open anywhere.
- Paintings come from Wikimedia Commons (photographs of public-domain works from the Tate, National Gallery and many others) and the Art Institute of Chicago's public-domain collection. The images load straight from those sites.

## Frame styles

| Style | Looks like | Good for |
| --- | --- | --- |
| Gilded | Carved gold moulding with a bead and leaf pattern | Turner and most 19th-century oils |
| Walnut | Dark wood with a thin gold slip | Constable, watercolours |
| Silver | Pale silver leaf | Whistler, Monet |
| Ebony | Narrow black moulding | Friedrich, drawings |

Oil paintings normally hang without a mat. Add one (the **Mat** slider) for watercolours and works on paper; works whose title mentions watercolour get a walnut frame and mat automatically.
