'use strict';

/* Directives */

angular.module('miredot.directives', []);

angular.module('miredot.directives')
    /**
    *  Renders a json-to
    */
    .directive('jsonTo', function ($compile) {

        var _idCount = 0;
        function getNewId() {
            return _idCount++;
        }

        function getHighlightHtml(to) {
            var html = '';
            var id = to.__md_id || to.__sub_id;
            var addHoverClass = 'onmouseover="$(\'#' + id + '\').addClass(\'highlightJsonRecursive\');"';
            var removeHoverClass = 'onmouseout="$(\'#' + id + '\').removeClass(\'highlightJsonRecursive\');"';
            html += '<a href="#' + id + '_a" ' + addHoverClass + ' ' + removeHoverClass + ' class="recursionLink" target="_self" title="recursion">';
            html += '<i class="icon-retweet"></i>';
            html += '</a>';
            return html;
        }

        return {
            restrict: 'E',
            transclude: false,
            scope: {
                to:'=',
                jsonDocConfig:'=',
                toggleJsonDoc:'='
            },
            link: function (scope, element, attrs) {

                /**
                 * Recursively renders the given TO as json.
                 * @param {Object | string} to The current object to render.
                 * @param {string} comment The comment of this field.
                 * @param {Array} history      All TOs that have already been rendered in the path leading here.
                 * @return {String}
                 */
                var build = function(to, comment, history) {
                    history = history || [];
                    var newHistory;

                    var html = '';

                    switch (to.type) {
                        case 'simple':
                            html += '<span class="parameterType">';
                            html += to.typeValue;
                            html += '</span>';
                            html += buildComment(comment);
                            break;

                        case 'enum':
                            html += '<span class="parameterType">';
                            html += enumArrayToString(to.values);
                            html += '</span>';
                            html += buildComment(comment);
                            break;

                        case 'collection':
                            html += '<span>[</span>';
                            html += buildComment(comment);
                            html += '<ul class="toContainer"><li class="parameterItem">';
                            html += build(to.typeValue, to.comment, history);
                            html += '</li></ul>';
                            html += '<span>]</span>';
                            break;

                        case 'map':
                            html += '<span>{</span>';
                            html += buildComment(comment);
                            html += '<ul class="toContainer"><li class="parameterItem">';
                            html += '<span class="parameterType">string</span> =>';
                            html += build(to.typeValue, to.comment, history);
                            html += '</li></ul>';
                            html += '<span>}</span>';
                            break;

                        default: //(abstract or complex)
                            //did wee see this type before?
                            if (_.indexOf(history, to.name) >= 0) {
                                //use it's id to highlight it
                                html += getHighlightHtml(to);
                                html += buildComment(comment);
                            } else {
                                newHistory = history.slice(0); // clone the history
                                newHistory.push(to.name);

                                //set a unique id for this type
                                to.__md_id = 'md_to_' + getNewId();

                                html += buildComment(comment);

                                //start TO div (with id to be able to highlight)
                                html += '<a id="' + to.__md_id + '_a" class="anchor"></a>';
                                html += '<div id="' + to.__md_id + '">';
                                html += '<span>{</span>';
                                html += '<ul class="toContainer">';
                                html += buildToProperties(to, newHistory);

                                //end TO div
                                html += '</ul>';
                                html += '<span>}</span>';
                                html += '</div>';
                            }
                            break;
                    }
                    return html;
                };

                function buildComment(comment) {
                    var result = '';
                    if (scope.jsonDocConfig.enabled && comment) {
                        result += '<span class="propertyComment" ng-show="!jsonDocConfig.hidden">';
                        result += comment;
                        result += '</span>';
                    }
                    return result;
                }

                function buildSubTypeSwitcher(to, beforeFieldComment, afterFieldComment) {
                    var html = '';
                    //property name
                    html += '<li class="parameterItem">';
                    if (to.property) { //property name used in JsonTypeInfo
                        html += '<span class="parameterName">' + to.property + ':</span>';
                    }
                    html += beforeFieldComment;
                    html += buildComment(to.propertyComment);
                    html += '</li>';
                    html += afterFieldComment;
                    return html;
                }

                /**
                 * Renders a TO with all its properties and subtype selector (if needed). Properties are rendered in
                 * the exact order they appear in the TO. No properties of the supertype (if applicable) are rendered.
                 * They must be duplicated in its subtypes.
                 *
                 * @param {Object | string} to The current object to render.
                 * @param {Array} history      All TOs that have already been rendered in the path leading here.
                 */
                function buildToProperties(to, history) {
                    var html = '';

                    // If the TO is 'abstract', we must take into account subtypes
                    var subTypeModel = null;
                    if (to.type === "abstract") {
                        //get a unique name for the angular model for the subType switcher
                        subTypeModel = 'subTypeModel' + getNewId();

                        // set a unique ids for the subTypes
                        _.each(to.subTypes, function(subType) {
                            subType.to.__sub_id = 'md_to_' + getNewId();
                        });

                        //set the default model value to the first subType id
                        scope[subTypeModel] = to.subTypes[0].to.__sub_id;
                    }

                    /**
                     * Creates a single subtype button (used in createSubTypeChooser()).
                     * @param subType    The subtype.
                     * @returns {string} The button.
                     */
                    function createSubTypeButton(subType) {
                        //show the button with the name of the subType (based on JsonTypeInfo)
                        //clicking this button changes the value of the current subTypeModel property to the id of the subType
                        return '<div class="btn" ng-model="$parent.' + subTypeModel + '" ' +
                            'btn-radio="\'' + subType.to.__sub_id + '\'">' + subType.name + '</div>';
                    }

                    /**
                     * Appends the button collection to switch between the all known subtypes. Provides a horizontal and
                     * vertical layout. Only one of these two is shown at te same time.
                     */
                    function appendSubTypeChooser() {
                        // horizontal row of sub types (JSON comments are not shown)
                        html += '<span ng-show="jsonDocConfig.hidden">';

                        var beforeFieldComment = '<span class="parameterType"><div class="btn-group">';
                        // generate buttons
                        _.each(to.subTypes, function (subType) {
                            beforeFieldComment += createSubTypeButton(subType);
                        });
                        beforeFieldComment += '</div></span>';

                        html += buildSubTypeSwitcher(to, beforeFieldComment, '');
                        html += '</span>';

                        // vertical list of sub types (JSON comments ARE shown)
                        html += '<span ng-show="!jsonDocConfig.hidden">';

                        var afterFieldComment = '<span class="subTypeSwitch">';
                        afterFieldComment += '<span class="btn-group-vertical">';

                        _.each(to.subTypes, function (subType) {
                            afterFieldComment += '<span>';
                            afterFieldComment += createSubTypeButton(subType);
                            if (subType.comment) {
                                afterFieldComment += '<span class="propertyComment">';
                                afterFieldComment += subType.comment;
                                afterFieldComment += '</span>';
                            }
                            afterFieldComment += '</span>';
                        });
                        afterFieldComment += '</span>';
                        afterFieldComment += '</span>';

                        html += buildSubTypeSwitcher(to, '', afterFieldComment);
                        html += '</span>';
                    }

                    function appendProperty(field) {
                        html += '<li class="parameterItem">'
                              + '<span class="parameterName" set-class="deprecated: ' + field.deprecated + '">' + field.name + ':</span>';
                        html += build(field.typeValue, field.comment, history);
                        html += "</li>";
                    }

                    if (!to.subTypes) {
                        _.each(to.content, function(field) {
                            appendProperty(field);
                        });
                    } else {
                        //show subTypes, like complex type, but inline fields & only shown when subTypeModel is set to it's id
                        _.each(to.subTypes, function(subType) {
                            html += '<a id="' + subType.to.__sub_id + '_a" class="anchor"></a>';
                            //only show this subType's fields when subTypeModel is set to it's id
                            html += '<div ng-if="' + subTypeModel + ' == \'' + subType.to.__sub_id + '\'" id="' + subType.to.__sub_id + '">';

                            if (!subType.to.ordered) {
                                appendSubTypeChooser();
                            }
                            _.each(subType.to.content, function(field) {

                                if (field.name === to.property) { // The subtype property => render the subtype-choose buttons
                                    if (subType.to.ordered) {
                                        appendSubTypeChooser();
                                    }
                                } else {                          // Another property => render a normal property
                                    appendProperty(field);
                                }
                            });

                            html += '</div>';
                        });
                    }
                    return html;
                }

                var anchorName = 'json_' + getNewId();
                var anchorHtml = '<a name="' + anchorName + '"/>';

                var togglePropertyCommentsHtml = '';
                if (scope.jsonDocConfig.enabled) {
                    togglePropertyCommentsHtml += '<span class="togglePropertyComments" ' +
                    'ng-click="toggleJsonDoc(\'' + anchorName + '\')">' +
                    '<span ng-show="jsonDocConfig.hidden">Show</span>' +
                    '<span ng-show="!jsonDocConfig.hidden">Hide</span>' +
                    ' descriptions</span>';
                }

                var newElement = angular.element(anchorHtml + togglePropertyCommentsHtml + build(scope.to));
                $compile(newElement)(scope);
                element.replaceWith(newElement);
            }
        };
    })

    .directive('widthonblur', function () {
        return function(scope, element, attrs) {
            element.css("width", attrs.widthonblur);

            element.bind("blur", function() {
                element.css("width", attrs.widthonblur);
            });
        }
    })
    .directive('widthonfocus', function () {
        return function(scope, element, attrs) {
            element.bind("focus", function() {
                element.css("width", attrs.widthonfocus);
            })
        }
    })
    //ngFocus will be included in later angular versions
    .directive('onFocus', ['$parse', function($parse) {
        return function(scope, element, attr) {
            var fn = $parse(attr['onFocus']);
            element.bind('focus', function(event) {
                scope.$apply(function() {
                    fn(scope, {$event:event});
                });
            });
        }
}   ])
    //ngBlur will be included in later angular versions
    .directive('onBlur', ['$parse', function($parse) {
        return function(scope, element, attr) {
            var fn = $parse(attr['onBlur']);
            element.bind('blur', function(event) {
                scope.$apply(function() {
                    fn(scope, {$event:event});
                });
            });
        }
    }])
    //focus when some condition becomes true
    .directive('focusWhen', function($parse, $timeout) {
        return function(scope, element, attr) {
            scope.$watch(attr['focusWhen'],
                function(newValue, oldValue) {
                    if (!oldValue && !!newValue) {
                        $timeout(function() {
                            element.focus();

                            //move cursor te end if input field (not required in chrome)
                            if (element.val()) {
                                var tmpStr = element.val();
                                element.val('');
                                element.val(tmpStr);
                            }
                        });
                    }
                }, true);
        };
    })
    //evaluate expression when user presses enter
    .directive('onEnter', function() {
        return function(scope, element, attrs) {
            element.bind("keydown keypress", function(event) {
                if(event.which === 13) {
                    scope.$apply(function(){
                        scope.$eval(attrs.onEnter);
                    });

                    event.preventDefault();
                }
            });
        };
    });



