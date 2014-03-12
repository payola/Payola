'use strict';

/* Services */


// Demonstrate how to register services
// In this case it is a simple value service.
angular.module('dataCube.services', ['ngResource'])
    .factory('DataCubeService', ['$resource', function($resource) {
    return $resource('/evaluation/query/:queryName/:evaluationId', null, {});
}]);
