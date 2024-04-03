## TCX-to-JSON

<p><a href="https://en.wikipedia.org/wiki/Training_Center_XML">TCX files</a> are XML files that are widely used to track
    sport activities. All major sport platforms such as <a href="http://www.strava.com">Strava</a>,
    <a href="https://en-gb.smashrun.com/">Smashrun</a>, and <a href="https://sports.garmin.com">Garmin</a> offer the 
    ability to export activities captured by smartphone or sports watches in the form of TCX files.</p>
<p>

This application reads .tcx files that comply with the official xml specifications found
here https://www8.garmin.com/xmlschemas/TrainingCenterDatabasev2.xsd, and and
here https://www8.garmin.com/xmlschemas/ActivityExtensionv2.xsd and returns them as JSON.

Input is either the path to a tcx file in the file system, or the full content of a tcx file. Output is either the exact
translation of tcx to json, or a simplified (opinionated) model.
Tested on exports of Strava, Garmin, TomTom, SmashRun and ttbin2tcx (https://github.com/alexspurven/ttbin2tcx), but
should work on all TCX applications that comply with the xsd.
<p>
An example of the simplified model:

```json
[
  {
    "start": "2024-03-23T08:06:14Z",
    "creatorName": "Forerunner 245 Music",
    "sport": "RUNNING",
    "laps": [
      {
        "lapStart": "2024-03-23T08:06:14Z",
        "totalTimeSeconds": 360.784,
        "distanceMeters": 1000.0,
        "maximumSpeed": 2.9670000076293945,
        "calories": 57,
        "averageHeartRateBpm": 142,
        "maximumHeartRateBpm": 153,
        "intensity": "ACTIVE",
        "cadence": 79,
        "triggerMethod": "MANUAL",
        "avgSpeed": 2.7720000743865967,
        "maxRunCadence": 86,
        "trackpoints": [
          {
            "timeStamp": "2024-03-23T08:06:14Z",
            "latitude": 52.072810493409634,
            "longitude": 5.120186051353812,
            "altitudeMeters": 4.400000095367432,
            "distanceMeters": 0.0,
            "heartRateBpm": 109,
            "cadence": 0,
            "speed": 0.0
          }
        ]
      }
    ]
  }
]
```

### Run in your local own development

Ensure java 21 and maven is installed. Run:

- `mvn install`
- `java -jar target/tcx-to-json-1.0.jar`

Go to `http://localhost:8080/swagger-ui/index.html` to see the specifics of the POST requests to run.

### Run in docker

Ensure Docker is installed. Run:

- `docker build -t tcx-to-json .`
- `docker run -p 8080:8080 tcx-to-json`

Go to `http://localhost:8080/swagger-ui/index.html` to see the required POST requests to read your TCX. Note that the
endpoints with a reference to a file in the body will use that file reference to refer to a path inside the docker
container. So when running in Docker it may be easier to use the endpoints with the content of the complete file
instead (or use a volume in which you have your tcx files available).