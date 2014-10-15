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
                    $scope.vessels = resp;
                    if ($scope.vessels.length > 0 && overrideSelection) {
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
         * revise the list of members within an organisation when a selection change occurs
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
        $scope.map = {};
        $scope.map.feature = null;
        $scope.map.draw = null;

        $scope.map.raster = new ol.layer.Tile({
            source: new ol.source.OSM()
        });

        $scope.map.styleFunction = function (feature, resolution) {
            return MAPSTYLES[feature.getGeometry().getType()];
        };

        if ($scope.trip.wayPoints == undefined)
            $scope.map.layers = [$scope.map.raster];
        else {

            $scope.map.vectorSource = new ol.source.GeoJSON(({
                object: $scope.trip.wayPoints
            }));
            $scope.map.vectorLayer = new ol.layer.Vector({
                source: $scope.map.vectorSource,
                style: $scope.map.styleFunction
            });
            $scope.map.layers = [$scope.map.raster, $scope.map.vectorLayer];
        }

        $scope.map.map = new ol.Map({
            layers: $scope.map.layers,
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

        // The features are not added to a regular vector layer/source,
        // but to a feature overlay which holds a collection of features.
        // This collection is passed to the modify and also the draw
        // interaction, so that both can add or modify features.
        $scope.map.featureOverlay = new ol.FeatureOverlay({
            style: new ol.style.Style({
                fill: new ol.style.Fill({
                    color: 'rgba(255, 255, 255, 0.2)'
                }),
                stroke: new ol.style.Stroke({
                    color: '#ffcc33',
                    width: 2
                }),
                image: new ol.style.Circle({
                    radius: 7,
                    fill: new ol.style.Fill({
                        color: '#ffcc33'
                    })
                })
            })
        });
        $scope.map.featureOverlay.setMap($scope.map.map);

        $scope.map.modify = new ol.interaction.Modify({
            features: $scope.map.featureOverlay.getFeatures(),
            // the SHIFT key must be pressed to delete vertices, so
            // that new vertices can be drawn at the same position
            // of existing vertices
            deleteCondition: function (event) {
                return ol.events.condition.shiftKeyOnly(event) &&
                    ol.events.condition.singleClick(event);
            }
        });
        $scope.map.map.addInteraction($scope.map.modify);

        $scope.map.draw = new ol.interaction.Draw({
            features: $scope.map.featureOverlay.getFeatures(),
            source: $scope.map.vectorSource,
            type: /** @type {ol.geom.GeometryType} */ 'LineString'
        });
        $scope.map.map.addInteraction($scope.map.draw);

        $scope.map.jsonformat = new ol.format.GeoJSON;

        $scope.map.draw.on('drawstart',
            function (evt) {
                $scope.map.featureOverlay.getFeatures().clear();
                var feature = evt.feature;
                feature.on('change', function (e) {
                    $scope.maintainFeatureValue(e.currentTarget);
                })
            });
        $scope.map.draw.on('drawend',
            function (evt) {
                $scope.maintainFeatureValue(evt.feature);
            }, this);


        /*
         * Maintain the feature value as drawn on the map
         */
        $scope.maintainFeatureValue = function (feature) {
            if (feature.getGeometry() instanceof ol.geom.LineString) {

//                $scope.map.feature = JSON.stringify(
//                    $scope.map.jsonformat.writeFeatureObject(feature,
//                        {
//                            // the format it is to be written to
//                            dataProjection: 'EPSG:4326',
//
//                            // the format the information is in
//                            featureProjection: 'EPSG:3857'
//                        })
//                );

                $scope.map.feature = $scope.map.jsonformat.writeFeatureObject(feature,
                    {
                        // the format it is to be written to
//                        dataProjection: 'EPSG:4326',
                        // the format the information is in
                        featureProjection: MAPCONSTANTS.EPSG3857
                    });
                $scope.map.feature.crs = {
                    "type": "name",
                    "properties": {
                        "name": MAPCONSTANTS.EPSG3857
                    }};
                $scope.$apply();
            }
        };


        $scope.create = function () {
            $scope.trip.wayPoints = $scope.map.feature;
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


    }]);


