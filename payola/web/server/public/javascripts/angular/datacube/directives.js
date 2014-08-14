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

    }])
    .directive('euRegionMap', [function () {
        function link(scope, element) {
            var dataTitle = scope.title, mapping = [], mapp2;
            mapp2 = [[0, 'NL22'], [1, 'NL21'], [2, 'NL33'], [3, 'CY00'], [4, 'NL12'], [5, 'NL13'], [6, 'NL23'], [7, 'NL11'], [8, 'NL41'], [9, 'NL42'], [10, 'NL32'], [11, 'NL31'], [12, 'NL34'], [13, 'ES12'], [14, 'ES41'], [15, 'ES13'], [16, 'ES11'], [17, 'ES43'], [18, 'ES61'], [19, 'ES7'], [20, 'ES7'], [21, 'ES7'], [22, 'ES63'], [23, 'ES51'], [24, 'ES24'], [25, 'ES22'], [26, 'ES52'], [27, 'ES53'], [28, 'ES53'], [29, 'ES21'], [30, 'ES23'], [31, 'ES42'], [32, 'ES30'], [33, 'ES62'], [34, 'ES53'], [35, 'PT11'], [36, 'PT16'], [37, 'PT17'], [38, 'PT18'], [39, 'PT30'], [40, 'PT15'], [41, 'AT12'], [42, 'AT13'], [43, 'AT13'], [44, 'AT11'], [45, 'AT31'], [46, 'AT22'], [47, 'AT21'], [48, 'AT32'], [49, 'AT33'], [50, 'AT33'], [51, 'AT34'], [52, 'ITC1'], [53, 'ITC2'], [54, 'ITC4'], [55, 'ITC3'], [57, 'ITD3'], [58, 'ITD4'], [59, 'ITD5'], [60, 'ITE1'], [61, 'ITE3'], [62, 'ITE2'], [63, 'ITE4'], [64, 'ITF1'], [65, 'ITF2'], [66, 'ITG2'], [67, 'ITF3'], [68, 'ITG1'], [69, 'ITF4'], [70, 'ITF5'], [71, 'ITF6'], [72, 'UKM6'], [73, 'UKN0'], [74, 'UKM3'], [75, 'UKL1'], [76, 'UKM5'], [77, 'UKM2'], [78, 'UKC2'], [79, 'UKD1'], [80, 'UKC1'], [81, 'UKE2'], [82, 'UKD4'], [83, 'UKD2'], [84, 'UKE4'], [85, 'UKF1'], [86, 'UKD3'], [87, 'UKG2'], [88, 'UKE3'], [89, 'UKE1'], [90, 'UKF3'], [91, 'UKH1'], [92, 'UKF2'], [93, 'UKH2'], [97, 'UKJ4'], [98, 'UKJ2'], [99, 'UKG3'], [100, 'UKG1'], [101, 'UKK1'], [102, 'UKJ1'], [103, 'UKL2'], [104, 'UKK2'], [105, 'UKK4'], [106, 'UKJ3'], [107, 'UKK3'], [108, 'CZ07'], [109, 'CZ05'], [110, 'CZ08'], [112, 'CZ06'], [113, 'CZ03'], [114, 'CZ04'], [115, ''], [117, 'SK04'], [118, 'SK02'], [119, 'SK03'], [120, 'SK01'], [121, 'BG31'], [122, 'BG41'], [123, 'BG42'], [124, 'BG32'], [125, 'BG33'], [126, 'BG34'], [129, 'HU31'], [130, 'HU10'], [131, 'HU32'], [132, 'HU22'], [133, 'HU21'], [134, 'HU23'], [135, 'HU33'], [136, 'FI1A'], [137, 'FI19'], [138, 'FI13'], [139, 'FI18'], [140, 'FI20'], [141, 'PL42'], [142, 'PL63'], [143, 'PL41'], [144, 'PL61'], [145, 'PL62'], [146, 'PL34'], [147, 'PL31'], [148, 'PL33'], [149, 'PL32'], [150, 'PL21'], [151, 'PL11'], [152, 'PL52'], [153, 'PL22'], [154, 'PL43'], [155, 'PL51'], [156, 'GR12'], [157, 'GR11'], [158, 'GR13'], [159, 'GR21'], [160, 'GR14'], [161, 'GR23'], [162, 'GR24'], [163, 'GR24'], [164, 'GR3'], [165, 'GR25'], [166, 'GR23'], [167, 'GR42'], [168, 'GR43'], [169, 'IE01'], [170, 'IE02'], [171, 'UKD5'], [172, 'FR52'], [173, 'FR51'], [174, 'FR53'], [175, 'FR61'], [176, 'FR25'], [177, 'FR30'], [178, 'FR22'], [179, 'FR23'], [180, 'FR24'], [181, 'FR10'], [182, 'FR63'], [183, 'FR62'], [184, 'FR21'], [185, 'FR41'], [186, 'FR42'], [187, 'FR81'], [188, 'FR82'], [189, 'FR83'], [190, 'FR72'], [191, 'FR71'], [192, 'FR26'], [193, 'FR43'], [194, 'RO01'], [195, 'RO11'], [196, 'RO12'], [197, 'RO42'], [198, 'RO31'], [199, 'RO41'], [200, 'RO32'], [201, 'RO08'], [202, 'SI01'], [203, 'SI02'], [204, 'DK05'], [205, 'DK04'], [206, 'DK03'], [207, 'DK04'], [208, 'DK01'], [209, 'DK02'], [210, 'DK03'], [211, 'DK02'], [212, 'EE00'], [213, 'EE00'], [214, 'EE00'], [215, 'LV00'], [216, 'LT00'], [217, 'CH02'], [218, 'CH01'], [219, 'CH05'], [220, 'CH04'], [221, 'CH06'], [222, 'CH03'], [223, 'CH07'], [224, 'DE94'], [225, 'DE93'], [226, 'DEF0'], [227, 'DE8'], [228, 'DE41'], [229, 'DEE0'], [230, 'DE42'], [231, 'DE3'], [232, 'DED3'], [233, 'DED2'], [234, 'DE91'], [235, ''], [236, 'DE5'], [237, 'DE92'], [238, 'DEA4'], [239, 'DE73'], [240, 'DEG0'], [241, 'DE26'], [242, 'DE24'], [243, 'DE72'], [244, 'DE71'], [245, 'DE25'], [246, 'DE12'], [247, 'DE11'], [248, 'DE23'], [249, 'DE27'], [250, 'DE21'], [251, 'DED1'], [252, 'DE22'], [253, 'DEA3'], [254, 'DEA1'], [255, 'DEA5'], [256, 'DEA2'], [257, 'DEB1'], [258, 'DEB2'], [259, 'DEC0'], [260, 'DEB3'], [261, 'DE14'], [262, 'DE13'], [263, 'LU00'], [264, 'BE25'], [265, 'BE23'], [266, 'BE21'], [267, 'BE22'], [268, 'BE33'], [269, 'BE22'], [270, 'BE24'], [271, 'BE31'], [272, 'BE35'], [273, 'BE32'], [274, 'BE34'],
                [275, 'SE33'], [276, 'SE32'], [277, 'SE31'], [278, 'SE23'], [279, 'SE12'], [280, 'SE21'], [281, 'SE22'], [282, 'SE11'], [283, 'SE21'],
                [284, 'UKH3'], [285, 'UKI2'], [286, 'UKI1'], [287, 'CZ02'], [288, 'HR01'], [289, 'HR02'], [290, 'HR03'], [291, 'CZ01'],
                [292, 'RO21'], [293, 'RO22'], [294, 'RO31'], [295, 'RO32'],
                [296, 'ITD2'], [297, 'ITD1'], [298, 'PL12']];
            function getVal(value1, value2) {
                switch (scope.dataOperation.id) {
                case 0:
                    return value2 - value1;
                case 1:
                    return value2 / value1;
                case 2:
                    return value1 + value2;
                }
            }
            function numberWithCommas(x) {
                return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
            }
            function setTooltip(title, headline, text, text2) {
                var inc;
                if (text === null) {
                    text = "";
                }
                if (text2 === null) {
                    text2 = "";
                }
                if (title !== null && title !== undefined && title !== "") {
                    title += " - ";
                } else {
                    title = "";
                }
                scope.title = title;
                scope.headline = headline;
                if (text !== "") {
                    if (Number(scope.mapMode) === 1) {
                        inc = "";
                        if (Number(scope.dataOperation.id) === 1) {
                            if (text >= 1) {
                                inc = "+";
                            }
                            scope.text = "Change: " + inc + Math.round((Number(text) * 100 - 100) * 100) / 100 + "%\n\n" + text2;
                        } else if (Number(scope.dataOperation.id) === 0) {
                            if (text >= 0) {
                                inc = "+";
                            }
                            scope.text = "Difference: " + inc + text + "\n\n" + text2;
                        } else {
                            scope.text = "Sum: " + text + "\n\n" + text2;
                        }
                    } else {
                        text = numberWithCommas(text);
                        scope.text = text + "\n\n" + text2;
                    }
                } else {
                    scope.text = "";
                }
                scope.$apply();
            }
            function regionOut(object) {
                var nuts = angular.element(object).attr("nuts"), reg, el, i;
                el = element.find("path");
                for (i = 0; i < el.length; i += 1) {
                    reg = el[i].getAttribute("nuts");
                    if (reg === nuts) {
                        el[i].setAttribute("stroke", "none");
                        el[i].setAttribute("stroke-width", "0px");
                    }
                }
                scope.ttShow = "none";
                scope.$apply();
            }
            /*function RegionClick(object) {
            }*/
            function getRegionName(id) {
                var i;
                for (i = 0; i < mapping.length; i += 3) {
                    if (id.toString() === mapping[i + 1].toString() || id.toString() === mapping[i + 2].toString()) {
                        return mapping[i];
                    }
                }
            }
            function regionOver(object, e) {
                var reg, nuts = angular.element(object).attr("nuts"), el, i;
                el = element.find("path");
                for (i = 0; i < el.length; i += 1) {
                    reg = el[i].getAttribute("nuts");
                    if (reg === nuts) {
                        el[i].setAttribute("stroke", "yellow");
                        if (scope.pollandOnly) {
                            el[i].setAttribute("stroke-width", "1px");
                        } else {
                            el[i].setAttribute("stroke-width", "3px");
                        }
                    }
                }
                scope.ttLeft = e.pageX + 20 - 400;
                scope.ttTop = e.pageY + 20;
                if (scope.ttLeft > 670) {
                    scope.ttLeft -= 250;
                }
                if (scope.ttTop > 560) {
                    scope.ttTop -= 250;
                }
                scope.ttShow = "block";
                scope.$apply();
                setTooltip(dataTitle, getRegionName(angular.element(object).attr("region")),
                    angular.element(object).attr("name"), angular.element(object).attr("title"));
            }
            function getRegionId(name) {
                var i;
                for (i = 0; i < mapping.length; i += 3) {
                    if (name === mapping[i]) {
                        return mapping[i + 1];
                    }
                }
            }
            function getRange(array, index) {
                var min = 9999999999, max = -9999999999, sum = 0, avg, mm, i;
                for (i = 0; i < array.length; i += 1) {
                    if (Number(array[i][index]) < 100000000000000) {
                        sum += Number(array[i][index]);
                        min = Math.min(min, Number(array[i][index]));
                        max = Math.max(max, Number(array[i][index]));
                    }
                }
                avg = sum / array.length;
                mm = Math.min(avg - min, max - avg);
                return [min, max, avg - mm, avg + mm];
            }
            function setRegionColor(id, color, value, value2) {
                var tmp = id, i, el;
                if (id === null || id === undefined) {
                    id = getRegionId(tmp);
                }                
                if (value === null || value === undefined) {
                    value = "";
                }
                if (value2 === null || value2 === undefined) {
                    value2 = "";
                }
                try {
                    el = element.find("path");
                    for (i = 0; i < el.length; i += 1) {
                        if (el[i].getAttribute("nuts") === id && el[i].getAttribute("ignore") !== "1") {
                            angular.element(el[i]).attr("fill", color);
                            angular.element(el[i]).attr("name", value);
                            angular.element(el[i]).attr("title", value2);
                        }
                    }
                } catch (ignore) {
                }
            }
            function getColor(range, value) {
                var index, g, r;
                index = Math.min(1, (value - range[0]) / (range[1] - range[0]));
                if (index.toString() === "NaN") {
                    index = 1;
                }
                r = Math.max(0, Math.min(255, Math.round(index * 255))).toString(16);
                g = Math.max(0, Math.min(255, Math.round(255 - index * 255))).toString(16);
                if (r.length === 1) {
                    r = "0" + r;
                }
                if (g.length === 1) {
                    g = "0" + g;
                }
                return "#" + r + g + "00";
            }
            function setDataArray(list, range) {
                var indexId = 0, indexData = 1, r, id;
                r = getRange(list, indexData);
                if (Number(scope.mapMode) === 1 && Number(scope.dataOperation.id) === 1) {
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
                    scope.barLow = numberWithCommas(Math.round(r[0] * 100) / 100);
                    scope.barHigh = numberWithCommas(Math.round(r[1] * 100) / 100);
                    if (Number(scope.mapMode) === 1 && Number(scope.dataOperation.id) === 0) {
                        if (scope.barLow >= 0) {
                            scope.barLow = "+" + scope.barLow;
                        }
                        if (scope.barHigh >= 0) {
                            scope.barHigh = "+" + scope.barHigh;
                        }
                    }
                }
                if (range === null || range === undefined) {
                    range = r;
                    range = [r[2],  r[3]];
                }
                for (id = 0; id < mapping.length; id += 3) {
                    setRegionColor(mapping[id + 2],  "#aaaaaa");
                }
                scope.pollandOnly = true;
                for (id = 0; id < list.length; id += 1) {
                    if (list[id][indexId].substr(0, 2).toUpperCase() !== "PL") {
                        scope.pollandOnly = false;
                    }
                    setRegionColor(list[id][indexId],  getColor(range, list[id][indexData]), list[id][indexData],
                        list[id][indexData + 1], list[id][indexData + 2]);
                }
                scope.$apply();
            }
            function settingsChanged() {
                var dataObject, i, e, curObj, lau, dataObj1, dataObj2, titleText, newData = [];
                dataObject = scope.data.data[0].data;
                if (Number(scope.mapMode) === 0) {
                    dataObject = scope.data.data[scope.dataSetSimple.id].data;
                    for (i = 0; i < dataObject.length; i += 1) {
                        curObj = dataObject[i];
                        lau = curObj.tickValue.substr(curObj.tickValue.lastIndexOf("/") + 1);
                        newData.push([lau, curObj.y, curObj.label]);
                    }
                } else {
                    dataObj1 = scope.data.data[scope.dataSet1.id].data;
                    dataObj2 = scope.data.data[scope.dataSet2.id].data;
                    for (i = 0; i < dataObj1.length; i += 1) {
                        for (e = 0; e < dataObj2.length; e += 1) {
                            if (dataObj1[i].tickValue === dataObj2[e].tickValue) {
                                titleText = scope.dataSet1.name + ": " + dataObj1[i].y + "\n" + scope.dataSet2.name + ": " + dataObj2[e].y + "\n\n";
                                newData.push([dataObj1[i].tickValue.substr(dataObj1[i].tickValue.lastIndexOf("/") + 1), getVal(dataObj1[i].y,
                                    dataObj2[e].y), titleText]);
                                break;
                            }
                        }
                    }
                }
                setDataArray(newData);
            }
            function updateFunc() {
                var n, i;
                dataTitle = scope.data.title;
                scope.mapMode = 0;
                scope.showSimple = 1;
                scope.dataSets = [];
                scope.pollandOnly = true;
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
                for (i = 0; i < scope.data.data.length; i += 1) {
                    n = scope.data.data[i].name;
                    scope.dataSets.push({name: n, id: i});
                    if (i === 0) {
                        scope.dataSetSimple = scope.dataSets[0];
                        scope.dataSet1 = scope.dataSets[0];
                    } else if (i === 1) {
                        scope.dataSet2 = scope.dataSets[1];
                    }
                }
                settingsChanged();
            }
            function init() {
                var i, e, el, regtmp;
                for (i = 0; i < mapp2.length; i += 1) {
                    mapping.push(mapp2[i][1], mapp2[i][0], mapp2[i][1]);
                }
                el = element.find("path");
                for (i = 0; i < el.length; i += 1) {
                    if (el[i].getAttribute("ignore") !== 1) {
                        regtmp = Number(el[i].getAttribute("region"));
                        el[i].setAttribute("nuts", regtmp);
                        for (e = 0; e < mapp2.length; e += 1) {
                            if (mapp2[e][0] === regtmp) {
                                if (mapp2[e][1].indexOf("CH") === 0) {
                                    el[i].setAttribute("style", "display:none");
                                }
                                el[i].setAttribute("nuts", mapp2[e][1]);
                            }
                        }
                    }
                }
                scope.$watch("data", updateFunc, true);
                element.find("path").on("mouseover", function (event) {
                    if (this.getAttribute("ignore") !== "1") {
                        regionOver(this, event);
                    }
                });
                element.find("path").on("mousemove", function (e) {
                    if (this.getAttribute("ignore") !== "1") {
                        scope.ttLeft = e.pageX - 15 - 400;
                        scope.ttTop = e.pageY + 20;
                        if (scope.ttLeft > 670) {
                            scope.ttLeft -= 250;
                        }
                        if (scope.ttTop > 560) {
                            scope.ttTop -= 250;
                        }
                        scope.ttShow = "block";
                        scope.$apply();
                    }
                });
                element.find("select").on("change", function () {
                    settingsChanged();
                });
    /*            element.find("path").on("click", function () {
                    if (this.getAttribute("ignore") != "1") {
                        //RegionClick(this);
                    }
                });*/
                element.find("path").on("mouseleave", function () {
                    if (this.getAttribute("ignore") !== "1") {
                        regionOut(this);
                    }
                });
                scope.change = function () {
                    scope.showSimple = Number(scope.mapMode) === 0;
                    settingsChanged();
                };
            }
            init();
        }
        return {
            restrict: 'E',
            templateUrl: '/assets/javascripts/angular/datacube/partials/EUMap.html',
            //templateUrl: 'partials/EUMap.html',
            link: link,
            scope: {data: "="}
        };
    }]);