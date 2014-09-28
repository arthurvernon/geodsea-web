README for geodsea
==========================


MAVEN_OPTS=-Xmx1024m -XX:MaxPermSize=128M -Djava.security.egd=file:/dev/./urandom

For instructions on installing Postgres and POstGIS on linux, go to 
http://www.postgresonline.com/journal/archives/329-An-almost-idiots-guide-to-install-PostgreSQL-9.3,-PostGIS-2.1-and-pgRouting-with-Yum.html

Closure Compiler
================

Grunt is hampered on windows by a finite command line which is exhausted before all dependencies for ol3 can be resolved.
Unfortunately the google closure tools does not figure out dependencies and so all google modules need to be declared.
Consequently the command line exceeds the maximum (presumably 1024 characters)

The workaround is to install the closure library and the open layers source code separately. You will need python 2.7 
(not 3 as it is incompatible). Then run this command... to produce the js file

D:\openlayers>D:\closure-library\closure\bin\build\closurebuilder.py --root=D:/closure-library/ --root=D:/openlayers/ol3/src --namespace="ol" --namespace="ol.Map" --namespace="ol.layer.Tile" --namespace="ol.View" --namespace="ol.source.MapQuest" --namespace="ol.source.OSM" --namespace="ol.source.Vector" --namespace="ol.layer.Vector" --namespace="ol.style.Style" --namespace="ol.style.Fill" --namespace="ol.style.Stroke" --namespace="ol.style.Circle" --namespace="ol.interaction.Draw" --output_mode=compiled --output_file=ol4.min.js --compiler_jar=D:\closure-library\compiler.jar