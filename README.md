# here-android
here android app

## rebuild
if need, add http proxy in file gradle.properties, for example:
``` 
systemProp.https.proxyHost=bluecoat-proxy
systemProp.https.proxyPort=8080
```

add project root folder local.properties includes android sdk home, for example:
``` 
sdk.dir=C\:\\dev\\programs\\Android\\sdk
``` 

## issues

Phone's screen resolution and styles tileSize relationship

## mapbox-gl-style-spec
npm install -g mapbox-gl-style-spec
$ gl-style-validate style.json
$ gl-style-migrate bright-v7.json > bright-v8.json