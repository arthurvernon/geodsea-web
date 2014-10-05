'use strict';

geodseaApp.controller('SkipperTripController', ['$scope', 'resolvedSkipperTrip', 'vessels', 'SkipperTrip',
    'VesselSkipper', '$timeout',
    function ($scope, resolvedSkipperTrip, vessels, SkipperTrip, VesselSkipper, $timeout) {

        $scope.errorcode = null;
        $scope.error = null;
        $scope.vessels = vessels;
        $scope.skippers = {};
        $scope.trips = resolvedSkipperTrip;
        $scope.map = {};
        $scope.linestring = null;
        $scope.map.draw = null;
        $scope.map.source = new ol.source.Vector();
        $scope.map.raster = new ol.layer.Tile({
            source: new ol.source.OSM()
        });

        $scope.initmap = function () {

            // timeout required so that the containing div is rendered and gains a height and width
            // from the form once it is displayed.
            $timeout(function () {
                if ($scope.map.map == undefined) {
                    $scope.map.map = new ol.Map({
                        layers: [$scope.map.raster],
                        target: 'map',
                        controls: ol.control.defaults().extend([
//                            new ol.control.ScaleLine(), new ol.control.FullScreen()
                            new ol.control.ScaleLine()
                        ]),
                        view: new ol.View({
                            projection: 'EPSG:3857',
                            center: ol.proj.transform([115.44, -31.98], 'EPSG:4326', 'EPSG:3857'),
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
                        source: $scope.map.source,
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

                }
            }, 200, false);

        };

        /*
         * Maintain the linestring value as drawn on the map
         */
        $scope.maintainFeatureValue = function (feature) {
            if (feature.getGeometry() instanceof ol.geom.LineString) {

                $scope.map.linestring = JSON.stringify(
                    $scope.map.jsonformat.writeFeatureObject(feature,
                        {
                            // the format it is to be written to
                            dataProjection: 'EPSG:4326',

                            // the format the information is in
                            featureProjection: 'EPSG:3857'
                        })
                );
                $scope.$apply();
            }
        };

        /*
         * revise the list of members within an organisation when a selection change occurs
         */
        $scope.loadSkippers = function() {
            // clear any previous list immediately
            $scope.skippers = null;
            VesselSkipper.query({ vesselId: $scope.trip.vessel.id}, function (resp) {
                    $scope.skippers = resp;
                    $scope.trip.skipper = {id : $scope.skippers[0].id };
                },
                function (httpResponse) {
                    $scope.success = null;
                    if (httpResponse.data)
                        $scope.errorcode = "errors."+ httpResponse.data;
                    else
                        $scope.error = "ERROR";
                })};


        $scope.create = function () {
            SkipperTrip.save($scope.trip,
                function () {
                    $scope.trips = SkipperTrip.query();
                    $('#saveTripModal').modal('hide');
                    $scope.clear();
                    $scope.errorcode = null;
                    $scope.error = null;
                },
                function (httpResponse) {
                    $scope.success = null;
                        if (httpResponse.data)
                            $scope.errorcode = "errors."+ httpResponse.data;
                        else
                            $scope.error = "ERROR";
                });
        };

        $scope.update = function (id) {
            $scope.trip = SkipperTrip.get({id: id});
            SkipperTrip.get({id: id},
            function(resp){
                $scope.trip =  resp;
                $scope.loadSkippers();
            });
            $('#saveTripModal').modal('show');
            $scope.initmap();
        };

        $scope.delete = function (id) {
            SkipperTrip.delete({id: id},
                function () {
                    $scope.trips = SkipperTrip.query();
                });
        };

        $scope.clear = function () {
            $scope.trip = {id: null, headline: null, summary: null, peopleOnBoard: null, fuelOnBoard: null,
                scheduledStart_dt: null, actualStart_dt: null, scheduledEnd_dt: null, actualEnd_dt:null,
                skipper: null, vessel: null };

            $scope.initmap();
        };

    }]);


