'use strict';

geodseaApp.controller('TripController', ['$scope', 'resolvedTrip', 'Trip', '$timeout',
    function ($scope, resolvedTrip, Trip, $timeout) {

        $scope.trips = resolvedTrip;
        $scope.map = {};
        $scope.map.draw = null;
        $scope.map.source = new ol.source.Vector();
        $scope.map.raster = new ol.layer.Tile({
            source: new ol.source.OSM()
        });
        $scope.map.vector = new ol.layer.Vector({
            source: $scope.map.source,
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

//        $scope.interact = function () {
//            if ($scope.draw != null)
//                $scope.map.removeInteraction($scope.draw);
//            if ($scope.geotype !== 'None') {
//                $scope.draw = new ol.interaction.Draw({
//                    source: $scope.source,
//                    type: /** @type {ol.geom.GeometryType} */ 'LineString'
//                });
//                $scope.map.addInteraction($scope.draw);
//            }
//        }


        $scope.initmap = function(){

            // timeout required so that the containing div is rendered and gains a height and width
            $timeout(function(){
                if ($scope.map.map == undefined) {
                    $scope.map.map = new ol.Map({
                        layers: [$scope.map.raster, $scope.map.vector],
                        target: 'map',
                        view: new ol.View({
                            center: [-11000000, 4600000],
                            zoom: 4
                        })
                    });
                    $scope.map.draw = new ol.interaction.Draw({
                        source: $scope.map.source,
                        type: /** @type {ol.geom.GeometryType} */ 'LineString'
                    });
                    $scope.map.map.addInteraction($scope.map.draw);

                }
            },200,false);

        };

        $scope.create = function () {
            Trip.save($scope.trip,
                function () {
                    $scope.trips = Trip.query();
                    $('#saveTripModal').modal('hide');
                    $scope.clear();
                });
        };

        $scope.update = function (id) {
            $scope.trip = Trip.get({id: id});
            $('#saveTripModal').modal('show');
            $scope.initmap();
        };

        $scope.delete = function (id) {
            Trip.delete({id: id},
                function () {
                    $scope.trips = Trip.query();
                });
        };

        $scope.clear = function () {
            $scope.trip = {id: null, sampleTextAttribute: null, sampleDateAttribute: null};
            $scope.initmap();
        };
    }]);


