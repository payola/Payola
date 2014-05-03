
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
