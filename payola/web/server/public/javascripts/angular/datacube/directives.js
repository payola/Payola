'use strict';

/* Directives */

angular.module('dataCube.directives', [])
    .directive('czRegionMap', [function () {

        function link(scope, element, attr) {
            scope.$watch("data", updateFunc, true);
            var dataTitle = scope.title;
            var regionId = null;

            var el = element.find("path");
            for (var i = 0; i < el.length; i++) {
                el[i].setAttribute("region", i)
            }

            var mapping = [
                "Praha", 10, "CZ0100",
                "Beroun", 46, "CZ0202",
                "Blansko", 7, "CZ0641",
                "Brno-mesto", 38, "CZ0642",
                "Benesov", 53, "CZ0201",
                "Brno-venkov", 74, "CZ0643",
                "Brunt\u00e1l", 3, "CZ0801",
                "B\u0159eclav", 48, "CZ064",
                "\u010cesk\u00e9 Bud\u011bjovice", 36, "CZ0311",
                "Cheb", 0, "CZ0411",
                "\u010cesk\u00fd Krumlov", 67, "CZ0312",
                "\u010cesk\u00e1 L\u00edpa", 6, "CZ0511",
                "Chrudim", 23, "CZ0531",
                "Chomutov", 14, "CZ0422",
                "Decin", 18, "CZ0421",
                "Doma\u017elice", 32, "CZ0321",
                "Fr\u00fddek-M\u00edstek", 75, "CZ0802",
                "Havl\u00ed\u010dk\u016fv Brod", 39, "CZ0631",
                "Hradec Kr\u00e1lov\u00e9", 64, "CZ0521",
                "Hodon\u00edn", 54, "CZ0645",
                "Ji\u010d\u00edn", 76, "CZ0522",
                "Jesen\u00edk", 60, "CZ0711",
                "Jind\u0159ich\u016fv Hradec", 42, "CZ0313",
                "Jihlava", 4, "CZ0632",
                "Jablonec nad Nisou", 25, "CZ0512",
                "Kutn\u00e1 Hora", 52, "CZ0205",
                "Karvin\u00e1", 73, "CZ0803",
                "Kladno", 50, "CZ0203",
                "Krom\u011b\u0159\u00ed\u017e", 2, "CZ0721",
                "Kol\u00edn", 51, "CZ0204",
                "Klatovy", 33, "CZ0322",
                "Karlovy Vary", 15, "CZ0412",
                "Liberec", 22, "CZ0513",
                "Louny", 21, "CZ0424",
                "Litom\u011b\u0159ice", 16, "CZ0423",
                "Mlad\u00e1 Boleslav", 58, "CZ0207",
                "M\u011bln\u00edk", 59, "CZ0206",
                "Most", 20, "CZ0425",
                "N\u00e1chod", 5, "CZ0523",
                "Nymburk", 57, "CZ0208",
                "Nov\u00fd Ji\u010d\u00edn", 66, "CZ0804",
                "Olomouc", 63, "CZ0712",
                "Opava", 69, "CZ0805",
                "Ostrava", 72, "CZ0806",
                "P\u0159\u00edbram", 47, "CZ020B",
                "Pelh\u0159imov", 41, "CZ0633",
                "Praha-v\u00fdchod", 24, "CZ0209",
                "P\u00edsek", 35, "CZ0314",
                "Plze\u0148-jih", 37, "CZ0324",
                "Plze\u0148-m\u011bsto", 40, "CZ0323",
                "P\u0159erov", 1, "CZ0714",
                "Plze\u0148-sever", 43, "CZ0325",
                "Prachatice", 68, "CZ0315",
                "Pardubice", 8, "CZ0532",
                "Prost\u011bjov", 62, "CZ0713",
                "Praha-z\u00e1pad", 49, "CZ020A",
                "Rakovn\u00edk", 45, "CZ020C",
                "Rychnov nad Kn\u011b\u017enou", 27, "CZ0524",
                "Rokycany", 12, "CZ0326",
                "Semily", 26, "CZ0514",
                "Sokolov", 13, "CZ0413",
                "Strakonice", 30, "CZ0316",
                "\u0160umperk", 61, "CZ0715",
                "Svitavy", 65, "CZ0533",
                "T\u00e1bor", 34, "CZ0317",
                "Tachov", 31, "CZ0327",
                "Teplice", 11, "CZ0426",
                "T\u0159eb\u00ed\u010d", 55, "CZ0634",
                "Trutnov", 17, "CZ0525",
                "Uhersk\u00e9 Hradi\u0161t\u011b", 44, "CZ0722",
                "\u00dast\u00ed nad Labem", 19, "CZ0427",
                "\u00dast\u00ed nad Orlic\u00ed", 71, "CZ0534",
                "Vset\u00edn", 28, "CZ0723",
                "Vy\u0161kov", 29, "CZ0646",
                "Zl\u00edn", 70, "CZ0724",
                "Znojmo", 56, "CZ0647",
                "\u017d\u010f\u00e1r nad S\u00e1zavou", 9, "CZ0635"
            ];

            element.find("path").on("mouseover", function (event) {
                RegionOver(this, event)

            });

            element.find("path").on("mousemove", function (e) {
                scope.ttLeft = e.pageX - 415;
                scope.ttTop = e.pageY + 20;
                scope.ttShow = "block";
                scope.$apply();

            });
            element.find("select").on("change", function (e) {

                settingsChanged()

            });

            element.find("path").on("click", function () {
                RegionClick(this);
            });

            element.find("path").on("mouseleave", function () {
                RegionOut(this);

            });

            scope.change = function () {
                scope.showSimple = scope.mapMode == 0;

                settingsChanged();
            };

            function getVal(value1, value2) {

                switch (scope.dataOperation.id) {
                    case 0:
                        return value2 - value1;
                        break;
                    case 1:

                        return value2 / value1;
                        break;
                    case 2:

                        return value1 + value2;
                        break
                }

            }

            function settingsChanged() {

                var dataObject = scope.data.data[0].data;
                var newData = new Array();
                var dataObject;
                if (scope.mapMode == 0) {

                    dataObject = scope.data.data[scope.dataSetSimple.id].data;

                    for (var i = 0; i < dataObject.length; i++) {
                        var curObj = dataObject[i];
                        var lau = curObj.tickValue.substr(curObj.tickValue.lastIndexOf("/") + 1);
                        newData.push([lau, curObj.y])
                    }
                } else {

                    var dataObj1 = scope.data.data[scope.dataSet1.id].data;
                    var dataObj2 = scope.data.data[scope.dataSet2.id].data;
                    for (var i = 0; i < dataObj1.length; i++) {
                        for (var e = 0; e < dataObj2.length; e++) {
                            if (dataObj1[i].tickValue == dataObj2[e].tickValue) {
                                var titleText = scope.dataSet1.name + ": " + dataObj1[i].y + "\n" + scope.dataSet2.name + ": " + dataObj2[e].y + "\n\n";

                                newData.push([dataObj1[i].tickValue.substr(dataObj1[i].tickValue.lastIndexOf("/") + 1), getVal(dataObj1[i].y,
                                    dataObj2[e].y), titleText]);
                                break;
                            }
                        }

                    }
                }

                SetDataArray(newData)
            }

            function updateFunc(oldval, newval) {

                dataTitle = scope.data.title;
                scope.mapMode = 0;

                scope.showSimple = 1;
                scope.dataSets = new Array();
                scope.dataOperations = [
                    {name: 'Difference', id: 0},
                    {name: 'Ratio', id: 1},
                    {name: 'Sum', id: 2}
                ];
                scope.dataOperation = scope.dataOperations[0];

                if (scope.data.data.length > 1) {
                    scope.hideOptions = false;
                } else {
                    scope.hideOptions = true;
                }
                for (var i = 0; i < scope.data.data.length; i++) {

                    var n = scope.data.data[i].name;

                    scope.dataSets.push({name: n, id: i});
                    if (i == 0) {
                        scope.dataSetSimple = scope.dataSets[0];
                        scope.dataSet1 = scope.dataSets[0];

                    } else if (i == 1) {
                        scope.dataSet2 = scope.dataSets[1];
                    }
                }
                settingsChanged();

            }

            function SetTooltip(title, headline, text, text2) {
                if (text == null) {
                    text = "";
                }
                if (text2 == null) {
                    text2 = "";
                }
                if (title != null && title != "") {
                    title += " - ";
                } else {
                    title = "";
                }
                scope.title = title;
                scope.headline = headline;

                if (text != "") {
                    if (scope.mapMode == 1) {
                        var inc = "";
                        if (scope.dataOperation.id == 1) {
                            if (text >= 1) {
                                inc = "+";
                            }
                            scope.text = "Change: " + inc + Math.round((Number(text) * 100 - 100) * 100) / 100 + "%\n\n" + text2;
                        } else if (scope.dataOperation.id == 0) {
                            if (text >= 0) {
                                inc = "+"
                            }
                            scope.text = "Difference: " + inc + text + "\n\n" + text2;
                        } else {
                            scope.text = "Sum: " + text + "\n\n" + text2;
                        }
                    } else {
                        scope.text = text + "\n\n" + text2;
                    }
                } else {
                    scope.text = "";
                }

                scope.$apply();

            }

            function RegionOut(object) {
                angular.element(object).attr("stroke", "none");
                angular.element(object).attr("stroke-width", "0px");
                scope.ttShow = "none";
                scope.$apply();
            }

            function RegionClick(object) {
                //alert('todo graf')
                //drawGraph();
            }

            function RegionOver(object, e) {
                angular.element(object).attr("stroke", "red");
                angular.element(object).attr("stroke-width", "5px");

                scope.ttLeft = e.pageX + 20;
                scope.ttTop = e.pageY + 20;
                scope.ttShow = "block";
                scope.$apply();

                SetTooltip(dataTitle, GetRegionName(angular.element(object).attr("region")),
                    angular.element(object).attr("name"), angular.element(object).attr("title"));
            }

            function GetRegionName(id) {
                for (var i = 0; i < mapping.length; i += 3) {
                    if (id == mapping[i + 1] || id == mapping[i + 2]) {
                        return mapping[i];
                    }
                }
            }

            function GetRegionId(name) {
                for (var i = 0; i < mapping.length; i += 3) {

                    if (name == mapping[i]) {
                        return mapping[i + 1];
                    }
                }
            }

            function GetLauRegionId(lau) {
                for (var i = 0; i < mapping.length; i += 3) {
                    if (lau == mapping[i + 2]) {
                        return mapping[i + 1];
                    }
                }
            }

            function GetRange(array, index) {
                var min = 9999999999;
                var max = -9999999999;
                var sum = 0;
                for (var i = 0; i < array.length; i++) {
                    sum += Number(array[i][index]);
                    min = Math.min(min, Number(array[i][index]));
                    max = Math.max(max, Number(array[i][index]));
                }
                var avg = sum / array.length;
                var mm = Math.min(avg - min, max - avg);
                return [min, max, avg - mm, avg + mm];
            }

            function SetDataArray(list, range) {
                var indexId = 0;
                var indexData = 1;
                var r = GetRange(list, indexData);

                if (scope.mapMode == 1 && scope.dataOperation.id == 1) {
                    scope.barLow = Math.round((r[0] * 100 - 100) * 100) / 100;
                    scope.barHigh = Math.round((r[1] * 100 - 100) * 100) / 100;
                    if (scope.barLow >= 0) {
                        scope.barLow = "+" + scope.barLow;
                    }
                    if (scope.barHigh >= 0) {
                        scope.barHigh = "+" + scope.barHigh;
                    }
                    scope.barLow += "%";
                    scope.barHigh += "%";

                } else {
                    scope.barLow = Math.round(r[0] * 100) / 100;
                    scope.barHigh = Math.round(r[1] * 100) / 100;
                    if (scope.mapMode == 1 && scope.dataOperation.id == 0) {
                        if (scope.barLow >= 0) {
                            scope.barLow = "+" + scope.barLow;
                        }
                        if (scope.barHigh >= 0) {
                            scope.barHigh = "+" + scope.barHigh;
                        }
                    }
                }

                if (range == null) {
                    range = r;
                    range = [r[2], r[3]]
                }

                for (var i = 0; i < mapping.length; i += 3) {
                    SetRegionColor(mapping[i + 2], "#555555");
                }
                for (var i = 0; i < list.length; i++) {

                    SetRegionColor(list[i][indexId], GetColor(range, list[i][indexData]), list[i][indexData],
                        list[i][indexData + 1]);
                }
                scope.$apply();
            }

            function SetRegionColor(id, color, value, value2) {

                var tmp = id;
                if (Number(id).toString() == "NaN") {
                    id = GetLauRegionId(id);
                }
                if (id == null) {
                    id = GetRegionId(tmp);

                }
                if (value == null) {
                    value = "";
                }
                if (value2 == null) {
                    value2 = "";
                }

                try {
                    var el = element.find("path");
                    for (i = 0; i < el.length; i++) {

                        if (el[i].getAttribute("region") == id) {

                            angular.element(el[i]).attr("fill", color);
                            angular.element(el[i]).attr("name", value);
                            angular.element(el[i]).attr("title", value2);
                        }
                    }
                } catch (e) {

                }
            }

            function GetColor(range, value) {
                var index = Math.min(1, (value - range[0]) / (range[1] - range[0]));
                if (index.toString() == "NaN") {
                    index = 1
                }
                var g = Math.max(0, Math.min(255, Math.round(index * 255))).toString(16);
                var r = Math.max(0, Math.min(255, Math.round(255 - index * 255))).toString(16);
                if (r.length == 1) {
                    r = "0" + r;
                }
                if (g.length == 1) {
                    g = "0" + g;
                }
                if (r.toString() == "NaN") {
                    alert(index)
                }
                return "#" + r + g + "00";
            }

        }

        return {
            restrict: 'E',
            templateUrl: '/assets/javascripts/angular/datacube/partials/CZMap.html',
            link: link,
            scope: {data: "="}
        }

    }]);