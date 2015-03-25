Space Time Toolkit is Java based software that was initiated by the University of Alabama in Huntsville (UAH), mainly for demonstrating sensor web related technologies via an interactive user interface allowing for 4D visualization and drag and drop processing of data. It is also a visualization and processing toolkit that can be used without the GUI components.

The main focus is the support of interoperable data exchange and web data access standards developed by the Open Geospatial Consortium. A secondary but also important aspect is the support of real time data streams and real time processing (e.g. geolocation, classification), so the rendering engine is optimized for that.

The software was originally developed by the [VAST Team](http://vast.uah.edu) at the University of Alabama in Huntsville (UAH), but is now maintained by [Botts Innovative Research Inc.](http://www.botts-inc.net), [Sensia Software LLC](http://www.sensiasoftware.com), and [Spot Image](http://www.spotimage.fr).

#### Dependencies ####

This project relies on code from several other open source projects also hosted on Google Code:

  * The [SWE Common Data Model Framework](http://code.google.com/p/swe-common-data-framework/)
  * The [OGC Service Framework](http://code.google.com/p/ows-service-framework/)
  * The [SensorML Data Processing Framework](http://code.google.com/p/sensorml-data-processing/)
  * The [SensorML Process Library](http://code.google.com/p/sensorml-profile-library/).

All external libraries required by these frameworks are usually necessary to compile and run Space Time Toolkit. Please go to the page of these projects to see what you need.

Additionally, these other libraries are also needed in order to compile the code published on SVN or in the download section:

  * [Eclipse Ganymede Platform 3.4](http://www.eclipse.org/ganymede/) - Space Time Toolkit is an Eclipse plugin
  * [JOGL 1.1.1](https://jogl.dev.java.net/servlets/ProjectDocumentList?folderID=9260&expandFolder=9260&folderID=0) - Java OpenGL bindings (jar and native libraries for your platform) from https://jogl.dev.java.net/
  * [JAI 1.1.3](https://jai.dev.java.net/binary-builds.html) - Java Advanced Imaging from https://jai.dev.java.net
  * [JFreeChart 1.0.12](http://sourceforge.net/project/showfiles.php?group_id=15494) - Java Charting library from http://www.jfree.org/jfreechart/ (need JFreeChart and JCommon libs)
  * [Berkeley DB Java Edition 3.3](http://www.oracle.com/technology/software/products/berkeley-db/je/index.html) from [Oracle](http://www.oracle.com/technology/products/berkeley-db/je/index.html)
