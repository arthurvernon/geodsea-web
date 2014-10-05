README for geodsea
==========================


MAVEN_OPTS=-Xmx1024m -XX:MaxPermSize=128M -Djava.security.egd=file:/dev/./urandom

For instructions on installing Postgres and POstGIS on linux, go to [this guide](http://www.postgresonline.com/journal/archives/329-An-almost-idiots-guide-to-install-PostgreSQL-9.3,-PostGIS-2.1-and-pgRouting-with-Yum.html)

Closure Compiler
----------------

Grunt is hampered on windows by a finite command line which is exhausted before all dependencies for ol3 can be resolved.
Unfortunately the google closure tools does not figure out dependencies and so all google modules need to be declared.
Consequently the command line exceeds the maximum (presumably 1024 characters)

The workaround is to install the closure library and the open layers source code separately. You will need python 2.7 
(not 3 as it is incompatible). Then run this command... to produce the js file

change output_mode=script for debugging.

`D:\openlayers>D:\closure-library\closure\bin\build\closurebuilder.py --root=D:/closure-library/ --root=D:/openlayers/ol3/src --namespace="ol" --namespace="ol.Map" --namespace="ol.layer.Tile" --namespace="ol.View" --namespace="ol.source.MapQuest" --namespace="ol.source.OSM" --namespace="ol.source.Vector" --namespace="ol.layer.Vector" --namespace="ol.FeatureOverlay" --namespace="ol.style.Style" --namespace="ol.style.Fill" --namespace="ol.style.Stroke" --namespace="ol.style.Circle" --namespace="ol.interaction.Draw" --namespace="ol.interaction.Modify" --namespace="ol.control.ScaleLine" --namespace="ol.control.FullScreen" --namespace="ol.format.GeoJSON" --output_mode=compiled --output_file=ol4.min.js --compiler_jar=D:\closure-library\compiler.jar`

JSON and Date Objects
---------------------

By default Jackson on the server side will serialise a date object as an int representing the time in 
milliseconds since epoch, Jan 1st 1970 GMT.

In the other direction, Jackson is happy to deal with a date in a String in RFC-1123/ISO-8601 format.

On the client side, a javascript date object can be automatically converted into a JSON string as a date/time in UTC (Z) time.
This aligns nicely with the behaviour of Jackson on the server.

The problem is that deserialising a JSON object on the client side to a date simply does not work. JSON has no way of
knowing that the value is supposed to be a date and so it remains a string.

There are various articles on the net including [this one](http://aboutcode.net/2013/07/27/json-date-parsing-angularjs.html) that
 suggest a strategy using a regular expression to guess if a string is a date. Unfortunately there are problems with
 this approach including:
 
 * A field such as a postcode might get interpreted as (and converted to) a date object
 * The actual number of variations of the format to support dates and different representations of timezones leads to
 a complicated regular expression that adds a performance overhead on the client.
 
To simplify matters a design decision was made to go a different way, namely to suffix date fields with "_dt".
This leads to a nicer/self documenting implementation where the client knows explicitly that a field is to be casted 
to a date object. 

Using this approach, a data object on a DTO requires no annotation. Jackson efficiently serialises the object as 
an int. On the client side the date object can be created simply with a constructor call. Because the value is independent
of timezone, the client can render the date however it is appropriate.

This also allows the Angular 1.3 <input type="datetime-local"... to work out of the box. 