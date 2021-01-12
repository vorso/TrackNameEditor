## TrackNameEditor
A utility which allows batch conversion of user-editable text between
 * CAPITALS
 * lower case
 * Title Case. 

## Credits
Created by Vorso   
Contributions by Rob

#### Before running (optional)
  Specify the location of the Ableton.exe file in the file paths.properties using LINUX path formatting (forward slashes /).

### Build & Run from Source

Requires
* Java JDK 11+
* Maven 3+
* JDK for either mac or Windows [jdks](https://adoptopenjdk.net/releases.html)
   
To build: 
  * Navigate to directory containing pom.xml
  * `maven clean install` runs the tests and builds the shaded jar
  
To Run
  * Navigate to directory containing pom.xml
  * `mvn exec:java -Dexec.mainClass="com.vorso.TrackNameEditor"`
  or
  * `java -jar Target/ableton-track-name-editor-0.jar` to run without Maven (currently does not support text colouring in Commmand Prompt)
  
> n.b Alternate, ready-to-run solutions tba.  

## License
Apache License 2.0 - http://www.apache.org/licenses/LICENSE-2.0

#### TODO

* Tweak packr minification to reduce artifact size.
* Test out the mac build locally.    
* Cache Ableton executable to preserve original behaviour.
* Automate packr build
* Store artifacts in CDN/GitHub, not Git
