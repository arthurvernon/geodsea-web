'use strict';

geodseaApp.controller('SkipperTripEditController', ['$scope', '$location', 'Vessel', 'SkipperTrip', 'VesselSkipper', '$timeout',
    'MAPSTYLES', 'MAPCONSTANTS', 'TripService',
    function ($scope, $location, Vessel, SkipperTrip, VesselSkipper, $timeout, MAPSTYLES, MAPCONSTANTS, TripService) {

        $scope.trip = TripService.getTrip();

        $scope.clear = function () {
            $scope.trip = {id: null, headline: null, summary: null, peopleOnBoard: null, fuelOnBoard: null,
                scheduledStart_dt: null, actualStart_dt: null, scheduledEnd_dt: null, actualEnd_dt: null,
                skipper: null, vessel: null, wayPoints: null };
        };

        $scope.newTrip = function () {

            alert('creating a new trip');
            // starting time in 5 minutes from now. Finish time 2 hours later.
            var now = new Date();
            now.setMilliseconds(0);
            now.setSeconds(0);
            now.setMinutes(0);
            now = new Date(now.getTime() + 5 * 60 * 1000);
            var twoHoursTime = new Date(now.getTime() + (2 * 60 * 60 * 1000));
            $scope.trip = {id: null, headline: null,
                summary: null, peopleOnBoard: null, fuelOnBoard: null,
                scheduledStart_dt: now, actualStart_dt: null, scheduledEnd_dt: twoHoursTime,
                actualEnd_dt: null,
                skipper: null, vessel: null, wayPoints: null };
        };

        $scope.loadVessels = function (overrideSelection) {
            // clear any previous list immediately
            $scope.vessels = null;
            $scope.skippers = null;
            Vessel.query({}, function (resp) {
                    console.log('got vessels back');
                    $scope.vessels = resp;
                    if ($scope.vessels.length > 0)
                    {
                        if (overrideSelection)
                            $scope.trip.vessel = $scope.vessels[0];
                        $scope.loadSkippers(overrideSelection);
                    }
                },
                function (httpResponse) {
                    $scope.success = null;
                    if (httpResponse.data)
                        $scope.errorcode = "errors." + httpResponse.data;
                    else
                        $scope.error = "ERROR";
                })
        }

        /*
         * revise the list of skippers when a selection change occurs
         */
        $scope.loadSkippers = function (overrideSelection) {
            // clear any previous list immediately
            $scope.skippers = null;
            if ($scope.trip && $scope.trip.vessel) {
                VesselSkipper.query({ vesselId: $scope.trip.vessel.id}, function (resp) {
                        $scope.skippers = resp;
                        if (overrideSelection && $scope.skippers.length > 0)
                            $scope.trip.skipper = {id: $scope.skippers[0].id };
                    },
                    function (httpResponse) {
                        $scope.success = null;
                        if (httpResponse.data)
                            $scope.errorcode = "errors." + httpResponse.data;
                        else
                            $scope.error = "ERROR";
                    })
            }
        };


        // if the trip is undefined
        if ($scope.trip === null) {
            // create a new trip and set. Set the scheduled start time and end time.
            // Load vessels and skippers by default assigning the first vessel and its first skipper.
            $scope.newTrip();
            $scope.loadVessels(true);
        } else {
            // load vessels and skippers without altering the current trip
            $scope.loadVessels(false);
        }
        $scope.errorcode = null;
        $scope.error = null;
        $scope.geo = {interactions: {}, feature: null, draw: null, layers: {}, source: null};


        $scope.geo.styleFunction = function (feature, resolution) {
            return MAPSTYLES[feature.getGeometry().getType()];
        };

        $scope.geo.layers.tile = new ol.layer.Tile({
            source: new ol.source.OSM()
        });



        $scope.geo.map = new ol.Map({
            layers: [$scope.geo.layers.tile],
            target: 'map',
            controls: ol.control.defaults().extend([
                new ol.control.ScaleLine(),
                new ol.control.FullScreen()
            ]),
            view: new ol.View({
                projection: MAPCONSTANTS.EPSG3857,
                center: ol.proj.transform([115.44, -31.98], MAPCONSTANTS.EPSG4326, MAPCONSTANTS.EPSG3857),
//                            center: [-11000000, 4600000],
                zoom: 10
            })
        });

        $scope.geo.jsonformat = new ol.format.GeoJSON;


        /*
         * Maintain the feature value as drawn on the map
         */
        $scope.maintainFeatureValue = function (feature) {
            if (feature.getGeometry() instanceof ol.geom.LineString) {

                $scope.geo.feature = $scope.geo.jsonformat.writeFeatureObject(feature,
                    {
                        // the format it is to be written to
//                        dataProjection: 'EPSG:4326',
                        // the format the information is in
                        featureProjection: MAPCONSTANTS.EPSG3857
                    });
                $scope.geo.feature.crs = {
                    "type": "name",
                    "properties": {
                        "name": MAPCONSTANTS.EPSG3857
                    }};
                $scope.$apply();
            }
        };


        $scope.create = function () {
//            $scope.trip.wayPoints = $scope.geo.feature;
            $scope.trip.wayPoints.crs = {
                "type": "name",
                "properties": {
                    "name": MAPCONSTANTS.EPSG3857
                }};

            SkipperTrip.save($scope.trip,
                function () {
                    $scope.clear();
                    $scope.errorcode = null;
                    $scope.error = null;
                    $scope.errorList = null;
                    $location.path('/skipper/trips')
                },
                function (httpResponse) {
                    $scope.success = null;
                    if (httpResponse.data) {
                        if (httpResponse.data instanceof String)
                            $scope.errorcode = "errors." + httpResponse.data;
                        else {
                            $scope.errorList = httpResponse.data;
                            for (var i in $scope.errorList.errors) {
                                if ($scope.errorList.errors[i].field) {
                                    $scope.form[$scope.errorList.errors[i].field].$setValidity('server', false)
                                }
                            }
                        }
                    }
                    else
                        $scope.error = "ERROR";
                });
        };


        // build up modify interaction
        // needs a select and a modify interaction working together
        function addModifyInteraction() {
            console.log('adding select/modify interaction');

            // remove draw interaction
            $scope.geo.map.removeInteraction($scope.geo.interactions.draw);

            // create select interaction
            $scope.geo.interactions.select = new ol.interaction.Select({
                condition: ol.events.condition.click,
                // make sure only the desired layer can be selected
                // function that returns true if the layer is enabled.
                layers: function (vector_layer) {
                    return vector_layer.get('name') === 'wayPointLayer';
                }
            });
            $scope.geo.map.addInteraction($scope.geo.interactions.select);

            // grab the features from the select interaction to use in the modify interaction
            var selected_features = $scope.geo.interactions.select.getFeatures();
            // when a feature is selected...
            selected_features.on('add', function(event) {
                // grab the feature
                var feature = event.element;
                // ...listen for changes and save them
                feature.on('change', saveData);
                // listen to pressing of delete key, then delete selected features
                $(document).on('keyup', function(event) {
                    if (event.keyCode == 46) {
                        // remove all selected features from select_interaction and my_vectorlayer
                        selected_features.forEach(function(selected_feature) {
                            var selected_feature_id = selected_feature.getId();
                            // remove from select_interaction
                            selected_features.remove(selected_feature);
                            // features aus vectorlayer entfernen
                            var vectorlayer_features = $scope.geo.layers.vector.getSource().getFeatures();
                            console.log('Number of features: ' + vectorlayer_features.length);
                            vectorlayer_features.forEach(function(source_feature) {
                                var source_feature_id = source_feature.getId();
                                if (source_feature_id === selected_feature_id) {
                                    // remove from my_vectorlayer
                                    $scope.geo.layers.vector.getSource().removeFeature(source_feature);
                                    // save the changed data
                                    saveData();
                                }
                            });
                            // removed way points? then allow a new one to be created...
                            if (vectorlayer_features.length == 1)
                                addDrawInteraction();
                        });
                        // remove listener
                        $(document).off('keyup');
                    }
                });
            });
            // create the modify interaction
            $scope.geo.interactions.modify = new ol.interaction.Modify({
//                features: $scope.geo.interactions.select.getFeatures(),
                features: selected_features,
                // delete vertices by pressing the SHIFT key
                deleteCondition: function(event) {
                    return ol.events.condition.shiftKeyOnly(event) &&
                        ol.events.condition.singleClick(event);
                }
            });
            // add it to the map
            $scope.geo.map.addInteraction($scope.geo.interactions.modify);

        }

        // creates a draw interaction
        function addDrawInteraction() {
            console.log('adding draw interaction');
            // remove other interactions
            $scope.geo.map.removeInteraction($scope.geo.interactions.select);
            $scope.geo.map.removeInteraction($scope.geo.interactions.modify);

            // create the interaction
            $scope.geo.interactions.draw = new ol.interaction.Draw({
                source: $scope.geo.layers.vector.getSource(),
//                source: $scope.geo.source,
                type: /** @type {ol.geom.GeometryType} */ 'LineString'
            });

            // add it to the map
            $scope.geo.map.addInteraction($scope.geo.interactions.draw);

            // when a new feature has been drawn...
            $scope.geo.interactions.draw.on('drawend', function(event) {

                console.log('drawend');

                // create a unique id
                // it is later needed to delete features
                var id = uid();
                // give the feature this id
                event.feature.setId(id);
                // save the changed data
                saveData();

                addModifyInteraction();
            });
        }

        function createVectorLayer() {
            $scope.geo.source = new ol.source.GeoJSON(({
                object: $scope.trip.wayPoints
            }));

            $scope.geo.layers.vector = new ol.layer.Vector({
                name: 'wayPointLayer',
                source: $scope.geo.source,
                style: $scope.geo.styleFunction
            });

            $scope.geo.map.addLayer($scope.geo.layers.vector);
        }


        // creates unique id's
        function uid(){
            var id = 0;
            return function() {
                if (arguments[0] === 0) {
                    id = 0;
                }
                return id++;
            }
        }

        // shows data in textarea
// replace this function by what you need
        function saveData() {
            // get the format the user has chosen
            // define a format the data shall be converted to
                var format = new ol.format['GeoJSON']();
            try {
                // convert the data of the vector_layer into the chosen format
//                $scope.geo.feature = format.writeFeatures($scope.geo.layers.vector.getSource().getFeatures());
                $scope.trip.wayPoints = format.writeFeatures($scope.geo.layers.vector.getSource().getFeatures());
                $scope.$apply();
//                $scope.geo.feature = 'new version' + uid();

                console.log('successfully extracted feature');
            } catch (e) {
                // at time of creation there is an error in the GPX format (18.7.2014)
                $scope.error = e.name + ": " + e.message;
                console.log('Failed to extracted feature');
                return;
            }
            console.log('Updating context now');

            $scope.$apply();
        }


        // set up an empty way points object if one is not defined.
        if ($scope.trip.wayPoints == undefined || $scope.trip.wayPoints == null) {
            $scope.trip.wayPoints =
            {
                'type': 'FeatureCollection',
                "crs": {
                    "type": "name",
                    "properties": {
                        "name": "EPSG:3857"
                    }
                },
                'features': []
            };
            createVectorLayer();
            addDrawInteraction();
        }
        else
        {
            createVectorLayer();
            addModifyInteraction();
        }

    }]);


