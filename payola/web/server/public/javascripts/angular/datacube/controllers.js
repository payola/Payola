/* Controllers */

angular.module('dataCube.controllers', []).
    controller('DataCube',
        ['$scope', 'DataCubeService', 'analysisId', 'evaluationId', '$q', function ($scope, DataCubeService, analysisId,
            evaluationId, $q) {

            const URI_rdfType = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
            const URI_dsd = "http://purl.org/linked-data/cube#DataStructureDefinition";
            const URI_component = "http://purl.org/linked-data/cube#component";
            const URI_dimension = "http://purl.org/linked-data/cube#dimension";
            const URI_measure = "http://purl.org/linked-data/cube#measure";
            const URI_attribute = "http://purl.org/linked-data/cube#attribute";
            const URI_label = "http://www.w3.org/2000/01/rdf-schema#label";
            const URI_sparqlResultValue = "http://www.w3.org/2005/sparql-results#value";
            const URI_binding = "http://www.w3.org/2005/sparql-results#binding";
            const URI_variable = "http://www.w3.org/2005/sparql-results#variable";
            const URI_value = "http://www.w3.org/2005/sparql-results#value";

            $scope.initDone = false;
            $scope.dataStructures = [];
            $scope.evaluationId = evaluationId;
            $scope.selectedDataStructure = null;
            $scope.filteringDimension = null;
            $scope.activeMeasure = null;


            $scope.highcharts = {
                options: {
                    chart: {
                        type: 'line'
                    }
                },
                series: [
                ],
                title: {
                    text: 'DataCube'
                },
                loading: false
            };

            $scope.labelsMap = {};

            DataCubeService.get({queryName: "list-cubes", analysisId: analysisId, evaluationId: evaluationId},
                function (data) {

                    angular.forEach(data, function (node, uri) {
                        if (uri.substr(0, 1) != '$') {
                            if (isDSD(node)) {
                                parseDSD(node, data, uri);
                            }
                        }
                    });

                    $scope.loadingDataDone();
                });

            $scope.buildUI = function (callback) {

                var promises = [];

                angular.forEach([
                    $scope.dataStructures[$scope.selectedDataStructure].dimensions,
                    $scope.dataStructures[$scope.selectedDataStructure].attributes
                ],
                    function (container) {

                        angular.forEach(container, function (def, uri) {

                            promises.push(DataCubeService.get({queryName: "distinct-values", evaluationId: evaluationId, property: uri, isDate: def.isDate},
                                function (data) {

                                    $scope.labelsMap = {};

                                    angular.forEach(data, function (value) {

                                        if(value[URI_binding]){
                                            var res = {labels: []};
                                            angular.forEach(value[URI_binding], function (b) {
                                                var solution = data[b.value];
                                                if (solution[URI_variable][0].value == "o") {
                                                    var o = solution[URI_value][0];
                                                    if(o.type == 'uri'){
                                                        res.uri = o.value;
                                                    }else if(o.type == 'literal'){
                                                        res.value = o.value;
                                                        res.datype = o.datatype;
                                                    }
                                                    res.o = solution[URI_value][0];
                                                }
                                                if (solution[URI_variable][0].value == "l") {
                                                    res.label = solution[URI_value][0].value;
                                                }
                                            });
                                            res.active = (!def.isDimension || (def == $scope.filteringDimension));
                                            def.values.push(res);

                                            $scope.labelsMap[res.uri] = res.label;
                                        }
                                    });

                                    if (def.values[0]) {
                                        def.values[0].active = true;
                                    }
                                })
                            );
                        });
                    });

                $q.all(promises.map(function (x) {
                        return x['$promise'];
                    })).then(function () {
                    callback();
                });
            };

            $scope.loadData = function () {

                var measureUri = $scope.activeMeasure.uri;

                var filters = [];

                filters = filters.concat(computeFilters($scope.dataStructures[$scope.selectedDataStructure].attributes));
                filters = filters.concat(computeFilters([$scope.filteringDimension]));

                var i = 0;
                $scope.highcharts.series = [];
                $scope.highcharts.xAxis = {categories: []};

                angular.forEach($scope.dataStructures[$scope.selectedDataStructure].dimensions, function (dim) {
                    if (dim != $scope.filteringDimension) {

                        angular.forEach(dim.values, function(v,k){

                            if(!v.active) return;

                            var localFilters = filters.concat(computeFilters([{uri: dim.uri, isDate: dim.isDate, values: [v]}], true));

                            DataCubeService.get({queryName: "data", evaluationId: evaluationId, measure: measureUri, dimension: $scope.filteringDimension.uri, filters: localFilters.map(function (x) {
                                return (x.positive ? "+" : "-") + x.component + "$:$:$" + x.value + "$:$:$" + x.isDate;
                            })}, function (data) {

                                $scope.highcharts.series[i] = {name: v.value, data: []};

                                angular.forEach(data, function (val, key) {
                                    if (key.substr(0, 1) != '$') {
                                        if (val[URI_binding]) {
                                            var res = {};
                                            angular.forEach(val[URI_binding], function (b) {
                                                var solution = data[b.value];
                                                if (solution[URI_variable][0].value == "m") {
                                                    res.m = solution[URI_value][0];
                                                }
                                                if (solution[URI_variable][0].value == "d") {
                                                    res.d = solution[URI_value][0];
                                                }
                                            });

                                            $scope.highcharts.series[i].data.push({name: $scope.labelsMap[res.d.value], y: parseInt(res.m.value) });
                                            $scope.highcharts.xAxis.categories.push($scope.labelsMap[res.d.value]);
                                        }
                                    }
                                });

                                i++;
                            });
                        });
                    }
                });

            };

            $scope.refresh = function () {
                $scope.loadData();
            };

            $scope.setActiveValue = function (dimension, $valueIndex) {
                /*var currentValue = dimension.values[$valueIndex].active;
                if (dimension != $scope.filteringDimension) {
                    if (currentValue == false) {
                        dimension.values[$valueIndex].active = true;
                        return;
                    }
                    angular.forEach(dimension.values, function (value, key) {
                        value.active = key == $valueIndex;
                    });
                }*/
            };

            $scope.loadingDataDone = function () {
                if ($scope.dataStructures.length != 1) {
                    return;
                }

                $scope.selectedDataStructure = 0;
                $scope.filteringDimension = $scope.dataStructures[$scope.selectedDataStructure].dimensions[Object.getOwnPropertyNames($scope.dataStructures[$scope.selectedDataStructure].dimensions)[0]];
                $scope.activeMeasure = $scope.dataStructures[$scope.selectedDataStructure].measures[Object.getOwnPropertyNames($scope.dataStructures[$scope.selectedDataStructure].measures)[0]];

                $scope.buildUI($scope.loadData);
                $scope.initDone = true;
            };

            function computeFilters(components, positive) {
                positive = positive || false;

                var filters = [];
                angular.forEach(components, function (component) {
                    angular.forEach(component.values, function (componentVal) {
                        if ((positive && componentVal.active) || (!positive && !componentVal.active)) {
                            var val = (componentVal.uri ? "<" + componentVal.uri + ">" : "'" + componentVal.value + "'" + (componentVal.datatype ? "^^<" + componentVal.datatype + ">" : ""));
                            filters.push({component: component.uri, value: val, positive: positive, isDate: component.isDate});
                        }
                    });
                });
                return filters;
            }

            function merge_options(obj1, obj2) {
                var obj3 = {};
                for (var attrname in obj1) {
                    obj3[attrname] = obj1[attrname];
                }
                for (var attrname in obj2) {
                    obj3[attrname] = obj2[attrname];
                }
                return obj3;
            }

            function isDSD(node) {
                return node[URI_rdfType] && node[URI_rdfType][0] && node[URI_rdfType][0].type == "uri" && node[URI_rdfType][0].value == URI_dsd;
            }

            function parseDSD(node, data, uri) {
                var dsdRef = {label: "Unlabeled DSD", uri: uri, dimensions: {}, measures: {}, attributes: {}};

                var queue = [
                    [URI_measure, 'measures'],
                    [URI_dimension, 'dimensions'],
                    [URI_attribute, 'attributes']
                ];

                if (node[URI_component]) {

                    angular.forEach(node[URI_component], function (component) {
                        if (component.type == "bnode" || component.type == "uri") {
                            var componentValue = data[component.value];

                            angular.forEach(queue, function (queueItem) {
                                if (componentValue[queueItem[0]]) {
                                    dsdRef[queueItem[1]][componentValue[queueItem[0]][0].value] = {
                                        uri: componentValue[queueItem[0]][0].value,
                                        label: componentValue[URI_label][0].value,
                                        values: [],
                                        isDimension: queueItem[0] == URI_dimension,
                                        isDate: (componentValue[queueItem[0]][0].value.substr(-6) == "period")
                                    };
                                }
                            });
                        }
                    });
                }

                $scope.dataStructures.push(dsdRef);
            }

        }]);