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

### Run
Grab and extract the zip for either
* win64\atne-windows64.zip -> in CLI run `atne-windows64.exe` 
* mac\atne-mac.zip -> in CLI run `Contents\MacOS\atne-mac`

> n.b. These are prepackaged artifacts with _no_ dependencies. Unzip and run!

### Build from Source
Uses [packr](https://github.com/libgdx/packr) to create a Portable Executable. No Java/Maven etc required for the end-user!

Requires
* Java JDK 11+
* Maven 3+
* JRE for mac, JRE for Windows, from these [jres](https://adoptopenjdk.net/releases.html)
   
To build: 
  1. `maven clean install` runs the tests and builds the shaded jar. Use the packr config to define where the jar is.
  2. Either/or    
     * `.\jdk-11.0.9.1+1\bin\java.exe -jar .\packr-all-3.0.1.jar .\win\win-packr-config.json` 
     * `.\jdk-11.0.9.1+1\bin\java.exe -jar .\packr-all-3.0.1.jar .\mac\mac-packr-config.json`



## License
Apache License 2.0 - http://www.apache.org/licenses/LICENSE-2.0

#### TODO

* Test out the mac build locally.    
* Cache Ableton executable to preserve original behaviour.
* Automate packr build
* Store artifacts in CDN/GitHub, not Git
