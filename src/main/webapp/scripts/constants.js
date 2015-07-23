'use strict';

/* Constants */

geodseaApp.constant('USER_ROLES', {
        all: '*',
        admin: 'ROLE_ADMIN',
        user: 'ROLE_USER',
        skipper: 'ROLE_SKIPPER',
        owner: 'ROLE_OWNER',
        rescue: 'ROLE_RESCUE'
    });

/*
Languages codes are ISO_639-1 codes, see http://en.wikipedia.org/wiki/List_of_ISO_639-1_codes
They are written in English to avoid character encoding issues (not a perfect solution)
*/
geodseaApp.constant('LANGUAGES', {
        ca: 'Catalan',
        da: 'Danish',
        en: 'English',
        es: 'Spanish',
        fr: 'French',
        de: 'German',
        kr: 'Korean',
        pl: 'Polish',
        pt: 'Portuguese',
        ru: 'Russian',
        tr: 'Turkish'
    });

var image = new ol.style.Circle({
    radius: 5,
    fill: null,
    stroke: new ol.style.Stroke({color: 'red', width: 1})
});

geodseaApp.constant('MAPCONSTANTS', {
    'SRID3857': 3857,
    'EPSG3857': 'EPSG:3857',
    'EPSG4326': 'EPSG:4326'
});

geodseaApp.constant('MAPSTYLES', {
    'Point': [new ol.style.Style({
        image: image
    })],
    'LineString': [new ol.style.Style({
        stroke: new ol.style.Stroke({
            color: 'green',
            width: 3
        })
    })],
    'MultiLineString': [new ol.style.Style({
        stroke: new ol.style.Stroke({
            color: 'green',
            width: 3
        })
    })],
    'MultiPoint': [new ol.style.Style({
        image: image
    })],
    'MultiPolygon': [new ol.style.Style({
        stroke: new ol.style.Stroke({
            color: 'yellow',
            width: 1
        }),
        fill: new ol.style.Fill({
            color: 'rgba(255, 255, 0, 0.1)'
        })
    })],
    'Polygon': [new ol.style.Style({
        stroke: new ol.style.Stroke({
            color: 'blue',
            lineDash: [4],
            width: 3
        }),
        fill: new ol.style.Fill({
            color: 'rgba(0, 0, 255, 0.1)'
        })
    })],
    'GeometryCollection': [new ol.style.Style({
        stroke: new ol.style.Stroke({
            color: 'magenta',
            width: 2
        }),
        fill: new ol.style.Fill({
            color: 'magenta'
        }),
        image: new ol.style.Circle({
            radius: 10,
            fill: null,
            stroke: new ol.style.Stroke({
                color: 'magenta'
            })
        })
    })],
    'Circle': [new ol.style.Style({
        stroke: new ol.style.Stroke({
            color: 'red',
            width: 2
        }),
        fill: new ol.style.Fill({
            color: 'rgba(255,0,0,0.2)'
        })
    })]
});
