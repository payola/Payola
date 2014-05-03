/* Controllers */

angular.module('dataCube.controllers', []).
    controller('DataCube',
    ['$scope', 'DataCubeService', 'analysisId', 'evaluationId', '$q', '$location', function ($scope, DataCubeService,
        analysisId, evaluationId, $q, $location) {

        const URI_rdfType = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
        const URI_dsd = "http://purl.org/linked-data/cube#DataStructureDefinition";
        const URI_component = "http://purl.org/linked-data/cube#component";
        const URI_dimension = "http://purl.org/linked-data/cube#dimension";
        const URI_measure = "http://purl.org/linked-data/cube#measure";
        const URI_QB_ORDER = "http://purl.org/linked-data/cube#order";
        const URI_attribute = "http://purl.org/linked-data/cube#attribute";
        const URI_label = "http://www.w3.org/2000/01/rdf-schema#label";
        const URI_sparqlResultValue = "http://www.w3.org/2005/sparql-results#value";
        const URI_binding = "http://www.w3.org/2005/sparql-results#binding";
        const URI_variable = "http://www.w3.org/2005/sparql-results#variable";
        const URI_value = "http://www.w3.org/2005/sparql-results#value";
        const URI_concept = "http://purl.org/linked-data/cube#concept";

        $scope.initDone = false;
        $scope.dataStructures = [];
        $scope.evaluationId = evaluationId;
        $scope.selectedDataStructure = null;
        $scope.XAxisDimension = null;
        $scope.activeMeasure = null;
        $scope.error = null;
        $scope.filtersString = "";
        $scope.ticks = [];

        $scope.mapVisible = false;
        $scope.chartVisible = true;

        $scope.showMap = function(){ $scope.mapVisible = true; $scope.chartVisible = false; };
        $scope.showChart = function(){ $scope.mapVisible = false; $scope.chartVisible = true; };

        $scope.czRegionData = {title:"Mapdata", data: []};

        $scope.highcharts = {
            options: {
                chart: {
                    type: 'line',
                    height: 650
                }
            },
            series: [
            ],
            title: {
                text: 'DataCube'
            },
            yAxis: {
                title: {
                    text: ""
                },
                currentMin: 0,
                currentMax: 100
            },
            loading: false
        };

        $scope.labelsMap = {};

        $scope.setDimensionsValuesEnabled = function (map) {
            angular.forEach($scope.dataStructures[$scope.selectedDataStructure].dimensions, function (dim, dimUri) {
                var negative = dim.order == 1;
                if (map[dim.uri]) {
                    angular.forEach(dim.values, function (val) {
                        var ptr = (val.uri ? "<" + val.uri + ">" : "'" + val.value + "'");

                        if (negative) {
                            val.active = map[dim.uri][ptr] !== false;
                        } else {
                            val.active = map[dim.uri][ptr];
                        }
                    });
                }
            });
        };

        $scope.setActiveValue = function (dimUri, v) {
            var dimension = $scope.dataStructures[$scope.selectedDataStructure].dimensions[dimUri];
            var currentValue = v.active;

            if (dimension.order > 2) {
                if (currentValue == false) {
                    v.active = true;
                    return;
                }
                angular.forEach(dimension.values, function (value) {
                    value.active = value == v;
                });
            }
        };

        $scope.applyFilters = function (callback) {
            return function () {
                if ($scope.filtersString) {
                    var filters = decodeURIComponent($scope.filtersString);
                    var components = filters.split(";;");

                    var map = {};

                    angular.forEach(components, function (c) {
                        var plusMinus = c.substring(0, 1);
                        var rest = c.substring(1);
                        var v = rest.split("$:$:$");

                        map[v[0]] = map[v[0]] || {};
                        map[v[0]][v[1]] = plusMinus == "+";
                    });

                    $scope.setDimensionsValuesEnabled(map);
                    $scope.filtersString = "";
                }
                callback();
            };
        };

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

            $scope.labelsMap = {};

            angular.forEach([
                    $scope.dataStructures[$scope.selectedDataStructure].dimensions,
                    $scope.dataStructures[$scope.selectedDataStructure].attributes
                ],
                function (container) {

                    angular.forEach(container, function (def, uri) {

                        def.values = [];

                        promises.push(DataCubeService.get({queryName: "distinct-values", evaluationId: evaluationId, property: uri, isDate: def.isDate},
                                function (data) {

                                    angular.forEach(data, function (value) {

                                        if (value[URI_binding]) {
                                            var res = {labels: []};
                                            angular.forEach(value[URI_binding], function (b) {
                                                var solution = data[b.value];
                                                if (solution[URI_variable][0].value == "o") {
                                                    var o = solution[URI_value][0];
                                                    if (o.type == 'uri') {
                                                        res.uri = o.value;
                                                    } else if (o.type == 'literal') {
                                                        res.value = o.value;
                                                        res.datype = o.datatype;
                                                    }
                                                    res.o = solution[URI_value][0];
                                                }
                                                if (solution[URI_variable][0].value == "date") {
                                                    var o = solution[URI_value][0];
                                                    res.uri = o.value;
                                                }
                                                if (solution[URI_variable][0].value == "spl") {
                                                    res.prefLabel = solution[URI_value][0].value;
                                                }
                                                if (solution[URI_variable][0].value == "l") {
                                                    res.label = solution[URI_value][0].value;
                                                }
                                                if (solution[URI_variable][0].value == "sn") {
                                                    res.notion = solution[URI_value][0].value;
                                                }
                                            });
                                            res.active = (!def.isDimension || (def == $scope.XAxisDimension));
                                            def.values.push(res);

                                            $scope.labelsMap[res.uri] = res.prefLabel || res.label || res.notion || res.uri;
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

        $scope.switchDSD = function ($index, force, setUrl) {

            if (force || $scope.selectedDataStructure != $index) {
                $scope.selectedDataStructure = $index;
                $scope.XAxisDimension = $scope.dataStructures[$index].dimensionsOrdered[0] || $scope.dataStructures[$index].dimensionsOrdered[1];
                $scope.activeMeasure = $scope.dataStructures[$index].measuresOrdered[0] || $scope.dataStructures[$index].measuresOrdered[1];

                $scope.highcharts.title.text = $scope.dataStructures[$index].label;
                $scope.highcharts.yAxis.title.text = $scope.activeMeasure.label.substring(0, 25) + "...";

                angular.forEach($scope.dataStructures, function (dsd, i) {
                    if (i != $index) {
                        dsd.active = false;
                    }
                });
                $scope.dataStructures[$index].active = true;

                $scope.buildUI($scope.applyFilters($scope.loadData));

                if (setUrl) {
                    $location.search("dsd", encodeURIComponent($scope.dataStructures[$index].uri));
                }
            }
        };

        $scope.computeBasicFilters = function () {
            var filters = [];

            filters = filters.concat(computeFilters($scope.dataStructures[$scope.selectedDataStructure].attributes));
            var filtersFrom = [];
            var dimCount = $scope.dataStructures[$scope.selectedDataStructure].dimensionsOrdered.length;
            if (dimCount > 3) {
                filtersFrom = $scope.dataStructures[$scope.selectedDataStructure].dimensionsOrdered.slice(-(dimCount - 3));
            }
            filters = filters.concat(computeFilters(filtersFrom, true));
            filters = filters.concat(computeFilters([$scope.XAxisDimension]));

            return filters;
        };

        $scope.dimensionsAreNumberedFromOne = function () {
            return !$scope.dataStructures[$scope.selectedDataStructure].dimensionsOrdered[0];
        };

        $scope.registerTick = function (value) {
            var tick = $scope.labelsMap[value] || value;
            $scope.ticks.push([tick, $scope.labelsMap[value] || value]);
            return tick;
        };

        $scope.sortTicks = function () {
            $scope.ticks.sort(function (a, b) {
                return (a[1] < b[1]) ? -1 : 1;
            });
        };

        $scope.parseCubeJson = function (serie, data, max, dataQueue) {
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

                        max = Math.max(max, parseInt(res.m.value));

                        var tick;
                        var tickVal;
                        if(res.d.datatype == "http://www.w3.org/2001/XMLSchema#date"){
                            tickVal = res.d.value.substr(0,4);
                        } else {
                            tickVal = res.d.value;
                        }

                        tick = $scope.registerTick(tickVal);


                        dataQueue.push([
                            function (serie, tick, res, tickVal) {
                                serie.data[$scope.seriesIndices[tick]] = {name: tick, y: parseInt(res.m.value), tickValue: tickVal }
                            },
                            {serie: serie, tick: tick, res: res, tickValue: tickVal}
                        ]);
                    }
                }
            });

            return max;
        };

        $scope.fillSeries = function () {
            var maxTicks = Object.keys($scope.seriesIndices).length - 1;

            angular.forEach($scope.highcharts.series, function (s) {
                for (var i = 0; i < maxTicks; ++i) {
                    if (!s.data[i]) {
                        s.data[i] = {};
                    }
                }
            });
        };

        $scope.computeTicksIndices = function () {
            angular.forEach($scope.ticks, function (tArray) {
                if (typeof($scope.seriesIndices[tArray[0]]) === 'undefined') {
                    $scope.seriesIndices[tArray[0]] = Object.keys($scope.seriesIndices).length;
                    $scope.highcharts.xAxis.categories[$scope.seriesIndices[tArray[0]]] = tArray[1];
                }
            });
        };

        $scope.loadData = function () {

            $scope.ticks = [];
            $scope.seriesIndices = {};

            var measureUri = $scope.activeMeasure.uri;

            var filters = $scope.computeBasicFilters();
            var dataQueue = [];

            $scope.highcharts.series = [];
            $scope.highcharts.xAxis = {categories: []};

            var globalFilters = [];
            var cycleDim = $scope.dimensionsAreNumberedFromOne() ? 2 : 1;
            var dim = $scope.dataStructures[$scope.selectedDataStructure].dimensionsOrdered[cycleDim];
            var valuesCopy = dim.values.concat();
            valuesCopy.sort(function (a, b) {
                var aName = a.prefLabel || a.uri || a.value;
                var bName = b.prefLabel || b.uri || b.value;

                return (aName < bName) ? -1 : 1;
            });
            var promises = [];

            angular.forEach(valuesCopy, function (v) {

                if (!v.active) return;

                var localFilters = filters.concat(computeFilters([
                    {uri: dim.uri, isDate: dim.isDate, values: [v]}
                ], true));

                globalFilters = globalFilters.concat(localFilters);

                $scope.seriesIndices = {};
                var max = 0;

                var serie = {name: v.prefLabel || v.uri || v.value, data: []};
                $scope.highcharts.series.push(serie);

                promises.push(DataCubeService.get({queryName: "data", evaluationId: evaluationId, measure: measureUri, dimension: $scope.XAxisDimension.uri, filters: localFilters.map(function (x) {
                    return (x.positive ? "+" : "-") + x.component + "$:$:$" + x.value + "$:$:$" + x.isDate;
                })}, function (data) {
                    max = $scope.parseCubeJson(serie, data, max, dataQueue);
                    $scope.highcharts.yAxis.currentMax = max;
                }));
            });

            $q.all(promises.map(function (x) {
                return x['$promise'];
            })).then(function () {

                $scope.sortTicks();
                $scope.computeTicksIndices();
                angular.forEach(dataQueue, function (x) {
                    x[0](x[1].serie, x[1].tick, x[1].res, x[1].tickValue);
                });

                $scope.fillSeries();

                $scope.czRegionData.data = $scope.highcharts.series;
            });

            persistFilterState(globalFilters);

        };

        function persistFilterState(filters) {
            var f = filters.map(function (x) {
                return (x.positive ? "+" : "-") + x.component + "$:$:$" + x.value;
            });

            $location.search("filters", f.join(";;"));
        }

        $scope.refresh = function () {
            $scope.loadData();
        };

        $scope.loadingDataDone = function () {
            if ($scope.dataStructures.length < 1) {
                $scope.error = "No DSDs found.";
                return;
            }

            if ($location.search().dsd) {
                angular.forEach($scope.dataStructures, function (val, key) {
                    if (val.uri == decodeURIComponent($location.search().dsd)) {
                        $scope.switchDSD(key, true);
                    }
                });
            } else {
                $scope.switchDSD(0, true);
            }

            if ($location.search().chartType) {
                $scope.switchChart($location.search().chartType);
            }

            if ($location.search().polarChart) {
                $scope.switchPolar($location.search().polarChart);
            }

            if ($location.search().filters) {
                $scope.filtersString = $location.search().filters;
            }

            $scope.initDone = true;
        };

        $scope.switchChart = function (chartType, setUrl) {
            $scope.highcharts.options.chart.type = chartType;
            if (setUrl) {
                $location.search("chartType", chartType);
            }
        };

        $scope.switchPolar = function (isPolar, setUrl) {
            $scope.highcharts.options.chart.polar = isPolar === true;
            if (setUrl) {
                $location.search("isPolar", isPolar === true);
            }
        };

        function computeFilters(components, positive) {
            positive = positive || false;

            var filters = [];
            angular.forEach(components, function (component) {
                angular.forEach(component.values, function (componentVal) {
                    if ((positive && componentVal.active) || (!positive && !componentVal.active)) {
                        var val = (componentVal.uri ? "<" + componentVal.uri + ">" : "'" + componentVal.value + "'" + (componentVal.datatype ? "^^<" + componentVal.datatype + ">" : ""));
                        filters.push({component: component.uri, value: val, positive: positive, isDate: component.isDate && !componentVal.uri});
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

            var dsdRef = {label: "Unlabeled DSD", uri: uri, dimensions: {}, measures: {}, attributes: {}, dimensionsOrdered: [], measuresOrdered: [], attributesOrdered: []};

            var queue = [
                {uri: URI_measure, field: 'measures', arrayIdx: 'measuresOrdered'},
                {uri: URI_dimension, field: 'dimensions', arrayIdx: 'dimensionsOrdered'},
                {uri: URI_attribute, field: 'attributes', arrayIdx: 'attributesOrdered'}
            ];

            if (node[URI_component]) {

                if (node[URI_label]) {
                    dsdRef.label = node[URI_label][0].value;
                }

                angular.forEach(node[URI_component], function (component) {
                        if (component.type == "bnode" || component.type == "uri") {
                            var componentValue = data[component.value];

                            angular.forEach(queue, function (queueItem) {
                                    if (componentValue[queueItem.uri]) {
                                        var c = {
                                            uri: componentValue[queueItem.uri][0].value,
                                            label: componentValue[URI_label][0].value,
                                            concept: (componentValue[URI_concept] || [
                                                {value: ""}
                                            ])[0].value,
                                            values: [],
                                            isDimension: queueItem.uri == URI_dimension,
                                            isDate: ((componentValue[URI_concept] || [
                                                {value: ""}
                                            ])[0].value == "http://purl.org/linked-data/sdmx/2009/concept#refPeriod") || (componentValue[queueItem.uri][0].value.substr(-6).toLowerCase() == "period"),
                                            order: parseInt((componentValue[URI_QB_ORDER] || [
                                                {value: dsdRef[queueItem.arrayIdx].length + 1}
                                            ])[0].value)
                                        };

                                        dsdRef[queueItem.field][componentValue[queueItem.uri][0].value] = c;
                                        dsdRef[queueItem.arrayIdx][c.order] = c; //TODO: a QB DSD with some orders defined and some not
                                    }
                                }
                            );
                        }
                    }
                );
            }

            $scope.dataStructures.push(dsdRef);
        }

    }])
;