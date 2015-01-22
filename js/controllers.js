'use strict';

/* Controllers */


/**
 * The main viewmodel for the doc. Loads a variable named restApiSource.
 * @param $scope
 * @constructor
 */
function DocRoot($scope, $location, $localStorage, $filter, $http, $anchorScroll, $timeout) {

    $scope.restBase = 'http://www.miredot.com/miredot/rest/';
    $scope.visitWebsiteForProVersion = 'Visit our <a href="http://www.miredot.com/price/?licencerequest=pro" target="_blank">website</a> to get the full version (free for open source).';

    $scope.projectTitle = com.qmino.miredot.restApiSource.projectTitle;
    $scope.miredotVersion = com.qmino.miredot.restApiSource.miredotVersion;
    $scope.validLicence = com.qmino.miredot.restApiSource.validLicence;
    $scope.licenceType = com.qmino.miredot.restApiSource.licenceType;
    $scope.licenceErrorMessage = com.qmino.miredot.restApiSource.licenceErrorMessage;
    $scope.licenceHash = com.qmino.miredot.restApiSource.licenceHash;
    $scope.allowUsageTracking = com.qmino.miredot.restApiSource.allowUsageTracking;
    $scope.dateOfGeneration = com.qmino.miredot.restApiSource.dateOfGeneration;
    $scope.issuesTabHidden = com.qmino.miredot.restApiSource.issuesTabHidden;

    $scope.$storage = $localStorage.$default({
        globalCollapsedState: false
    });
    $scope.baseUrl = { value : (function() {
        var baseUrl = "http://example.com";
        if ($scope.validLicence && $scope.licenceType == 'PRO') {
            baseUrl = $location.search().baseUrl || com.qmino.miredot.restApiSource.baseUrl || baseUrl;
        }
        return baseUrl;
    })()};
    $scope.editingBaseUrl = false;
    $scope.projectWarnings = com.qmino.miredot.projectWarnings;
    $scope.interfaces = com.qmino.miredot.restApiSource.interfaces;
    $scope.tos = com.qmino.miredot.restApiSource.tos;
    $scope.processErrors = com.qmino.miredot.processErrors;
    $scope.jsonDocConfig = {
        enabled: com.qmino.miredot.restApiSource.jsonDocEnabled,
        hidden: com.qmino.miredot.restApiSource.jsonDocHidden
    };

    $scope.searchByExample = "";
    $scope.searchQuery = {url : "", http: ""};
    $scope.location = $location;
    $scope.navigationView = 'hierarchical';

    ensureLinksHaveTargetInApiIntro();

    setGlobalCollapsedState($localStorage.globalCollapsedState);
    $scope.hierarchyOpen = true;

    // Set the view, based on the current location hash.
    setView($location.hash());
    // Watch the location hash and update the view when it changes.
    $scope.$watch('location.hash()', function(newValue, oldValue) {
        if (oldValue != newValue) {
            setView(newValue);
        }
    });

    $http.jsonp(
        $scope.restBase +
        'version' +
        '?hash=' + $scope.licenceHash + '&version=' + $scope.miredotVersion + '&licencetype=' + ($scope.licenceType || 'FREE') +
        '&callback=JSON_CALLBACK')
        .success(function(data) {
            if (data.upToDate) {
                $scope.versionCheckResult = "";
            } else {
                $scope.versionCheckResult = " | New version available: " + data.version;
            }
        });

    $scope.formatTypeValue = function(typeValue) {
        switch(typeValue.type) {
            case 'collection':
                return '[ ' + $scope.formatTypeValue(typeValue.typeValue) + ' ]';

            case 'enum':
                return 'enum';

            default:
                return typeValue.typeValue;
        }
    };

    $scope.formatDefaultValue = function(typeValue, defaultValue) {
        switch(typeValue.type) {
            case 'enum':
                var enumValues = enumArrayToString(typeValue.values);
                enumValues = enumValues.replace(defaultValue, '<span class="default" title="Default value">' + defaultValue + '</span>');
                return enumValues;
            default:
                if (defaultValue != undefined) {
                    return '<span class="default" title="Default value">' + defaultValue + '</span>'
                }
                return "";
        }
    };

    /**
     * Sets $scope.view, based on the given hash.
     * Currently sets the view to 'interfaces' when it's not 'warnings'.
     * eg. #1465486435 (interface hash) will cause the view to be 'interfaces'
     *
     * @param hash A string from which the current view will be determined
     */
    function setView(hash) {
        if (hash === 'warnings') {
            $scope.view = 'warnings';
        } else {
            $scope.view = 'interfaces';
        }
    }

    $scope.toggleJsonDoc = function(anchor) {
        $scope.jsonDocConfig.hidden = !$scope.jsonDocConfig.hidden;
        //scroll after digest
        $timeout(function(){
            $location.hash(anchor);
            $anchorScroll();
        });
    };

    /**
     * Ensures that all links in the API intro have a target.
     */
    function ensureLinksHaveTargetInApiIntro() {
        $("#intro a").attr('target', function(i, current) {
            return current || '_self';
        });
    }

    /**
     * Function to clear the selection of service tags
     */
    function clearServiceTagSelection() {
        _.each($scope.serviceTags, function(serviceTag) { serviceTag.selected = false; } );
    }

    // Build a map interfaceHash->interfaceObject
    var interfacesByHash = {};
    _.each($scope.interfaces, function(iface) {
        interfacesByHash[iface.hash] = iface;
    });

    /**
     * Build a list of tag objects
     * {
     *   name: string, tag name
     *   selected: boolean, selected for filtering?
     * }
     */
    $scope.serviceTags = (function() {
        var tagNames = [];
        _.each($scope.interfaces, function(currentInterface) {
            _.each(currentInterface.tags, function(tagName) {
                tagNames.push(tagName);
            });
        });
        tagNames = _.uniq(tagNames);
        return _.map(tagNames, function(tagName) { return {name : tagName, selected: false }; } );
    }());

    /**
     * Function to look up the state of a service tag by name
     * @param tagName The name of the service tag
     */
    $scope.isServiceTagSelected = function(tagName) {
        var tag = _.find($scope.serviceTags, function(serviceTag) { return serviceTag.name === tagName; } );
        return tag.selected;
    };

    // Build a map warningCategory->[warning]
    $scope.projectWarningsByType = (function() {
        var result = {};
        _.each($scope.projectWarnings, function(projectWarning) {
            result[projectWarning.category] = result[projectWarning.category] || [];
            result[projectWarning.category].push(projectWarning);
        });
        return result;
    }());

    function appendUrl(rootParts, url, method, hash, rootResource) {

        var currentResource = null;
        var parentResource = rootResource;
        _.each(rootParts, function(rootPart) {

            if (rootPart != "") {
                currentResource = _.find(parentResource.resources, function(resource) {return resource.name === rootPart});

                if (!currentResource) {
                    currentResource = {
                        name: rootPart,
                        resources: [],
                        leafResources: []
                    };
                    parentResource.resources.push(currentResource);
                }
                parentResource = currentResource;
            } else {
                currentResource = parentResource;
            }
        });

        var existingLeaf = _.find(currentResource.leafResources, function(leaf) {
            return leaf.url === url;
        });

        if (existingLeaf) {
            existingLeaf.methods.push({method: method, hash: hash});
        } else {
            currentResource.leafResources.push({
                name: '',
                url: url,
                methods: [{method: method, hash: hash}]
            });
        }
    }

    /**
     * Constructs a tree structure of the available resources.
     * {
     *    '/rest' :{
     *       resource: '/rest',
     *       subresources: {
     *          '/myresource': {
     *             resource: '/myresource',
     *             subresources: [ .. ]
     *          },
     *          {
     *             ..
     *          }
     *       }
     *    }
     * }
     */
     function splitPaths(interfaces) {
        //add the root resource
        var resources = [
            {
                name: "",
                resources: [],
                leafResources: []
            }
        ];

        _.each(interfaces, function(element, index, list) {
            if (element.url.indexOf('/') !== 0) {
                element.url = '/' + element.url;
            }

            var baseUrl = element.url;
            if (baseUrl === '/') {
                baseUrl = '';
            }

            appendUrl(baseUrl.split('/'), element.url, element.http, element.hash, resources[0]);
        });

        //compact empty middle resources: like IntelliJ IDEA's "Compact Empty Middle Packages"
        _.each(resources, function(subResource, index, list) {
            compactEmptyMiddleResources(subResource.resources, subResource.leafResources);
        });

        //remove the root resource if not used
        if (resources[0].leafResources.length === 0) {
            resources = resources[0].resources;
        }

        //sort resources on highest level
        resources.sort(function(r1, r2) { return r1.name.localeCompare(r2.name) } );

        return resources;
    }

    function compactEmptyMiddleResources(resources, leafResources) {
        var subResourcesToRemove = [];
        _.each(resources, function(subResource, index, list) {
            var foundLeafResult = findSolitaryLeaf(subResource, "");
            if (foundLeafResult) {
                foundLeafResult.leaf.name = foundLeafResult.aggregatedName;
                leafResources.push(foundLeafResult.leaf);
                subResourcesToRemove.push(index);
            } else {
                compactEmptyMiddleResources(subResource.resources, subResource.leafResources);
            }
        });
        //remove sub resources after iterating: splice from highest index to lowest
        for (var i = subResourcesToRemove.length - 1; i >= 0; i--) {
            resources.splice(subResourcesToRemove[i], 1);
        }
    }

    /**
     * Searches and returns a solitary leaf with it's aggregated name.
     * Returns false if the given resource contains more than 1 leaf.
     *
     * returns {
     *             leaf: //the solitary leaf,
     *             aggregatedName: //the aggregated name of the solitary leaf
     *         }
     * @param subResource the sub resource to scan for solitary leafs (recursive)
     * @param name the aggregated leaf name (path)
     */
    function findSolitaryLeaf(subResource, name) {
        if (subResource.resources.length + subResource.leafResources.length !== 1) {
            return false; //we need exactly 1
        }
        var aggregatedName = concatenateWithSlash(name, subResource.name);
        if (subResource.leafResources.length === 1) {
            return {
                leaf: subResource.leafResources[0],
                aggregatedName : concatenateWithSlash(aggregatedName, subResource.leafResources[0].name)
            };
        } else {
            return findSolitaryLeaf(subResource.resources[0], aggregatedName);
        }
    }

    function concatenateWithSlash(firstPart, secondPart) {
        var result = '';
        if (firstPart) {
            result += firstPart;
        }
        if (firstPart && secondPart) {
            result += '/';
        }
        if (secondPart) {
            result += secondPart;
        }
        return result;
    }

    $scope.resourceTree = splitPaths(com.qmino.miredot.restApiSource.interfaces);

    $scope.isComplexObject = function(type) {
        return angular.isObject(type);
    };

    /**
     * Gives the interface (from $scope.interfaces) given the interface's hash ($scope.interfaces[x].hash).
     *
     * @param {string} interfaceHash The hash value of the interface
     * @return {object} An object contained in $scope.interfaces or undefined if not found.
     */
    $scope.getInterfaceByHash = function(interfaceHash) {
        return interfacesByHash[interfaceHash];
    };

    $scope.interfaceHttpOrderFunction = function(iface) {
        return _.indexOf($scope.httpMethods, iface.http);
    };

    $scope.methodHttpOrderFunction = function(method) {
        return _.indexOf($scope.httpMethods, method.method);
    };

    $scope.httpMethods = ['GET','HEAD','PUT','POST','DELETE'];

    $scope.toggleSearchQueryHttp = function(http) {
        if ($scope.searchQuery.http === http) {
            $scope.searchQuery.http = "";
        } else {
            $scope.searchQuery.http = http;
        }
    };

    $scope.setGlobalCollapsedState = setGlobalCollapsedState;

    function setGlobalCollapsedState(collapsed) {
        $localStorage.globalCollapsedState = collapsed;
        _.each($scope.interfaces, function(currentInterface) {
            currentInterface.collapsed = collapsed;
        })
    }

    $scope.getFirstLeaf = function(resource) {
        var orderBy = $filter('orderBy');
        if (resource.leafResources.length > 0) {
            var orderedLeafResources = orderBy(resource.leafResources, 'url');
            var firstLeafResource = orderedLeafResources[0];
            var orderedMethods = orderBy(firstLeafResource.methods, $scope.methodHttpOrderFunction);
            return orderedMethods[0];
        } else {
            return $scope.getFirstLeaf(orderBy(resource.resources, 'name')[0]);
        }
    };

    $scope.collapseTree = function() {
        collapseResources($scope.resourceTree, false);
    };

    $scope.expandTree = function() {
        collapseResources($scope.resourceTree, true);
    };

    function collapseResources(resources, open, stopLevel) {
        if (stopLevel === undefined || stopLevel === null ) {
            _.each(resources, function(resource) {
                resource.hierarchyOpen = open;
                collapseResources(resource.resources, open);
            })
        } else if (stopLevel < com.qmino.miredot.restApiSource.initialCollapseLevel) {
            _.each(resources, function(resource) {
                resource.hierarchyOpen = open;
                collapseResources(resource.resources, open, stopLevel+1);
            })
        }
    }

    function countResources(resources, leafResources) {
        var count = 0;
        if (leafResources) {
            count += leafResources.length;
        }
        if (resources) {
            count += resources.length;
            _.each(resources, function(resource) {
                count += countResources(resource.resources, resource.leafResources);
            });
        }
        return count;
    }

    if (com.qmino.miredot.restApiSource.initialCollapseLevel === undefined || com.qmino.miredot.restApiSource.initialCollapseLevel === null) {
        if (countResources($scope.resourceTree, null) > 35) {
            $scope.collapseTree();
        } else {
            $scope.expandTree();
        }
    } else {
        $scope.collapseTree();
        collapseResources($scope.resourceTree, true, 0);
    }
}

/**
 * Parses an escaped url query string into key-value pairs.
 * @source AngularJS
 * @returns Object.<(string|boolean)>
 */
function parseKeyValue(/**string*/keyValue) {
    var obj = {}, key_value, key;
    angular.forEach((keyValue || "").split('&'), function(keyValue) {
        if (keyValue) {
            key_value = keyValue.split('=');
            key = decodeURIComponent(key_value[0]);
            obj[key] = angular.isDefined(key_value[1]) ? decodeURIComponent(key_value[1]) : true;
        }
    });
    return obj;
}

/**
 * Assumes as input an array of strings (eg ['a', 'b']) and generates a string representation '"a" | "b"'
 * @param enumArray the array of strings
 * @returns {string} the stringrepresentation of the given array
 */
function enumArrayToString(enumArray) {
    var output = '';
    for (var i=0;i<enumArray.length; i++) {
        if (i != 0) {
            output += ' | ';
        }
        output += enumArray[i];
    }
    return output;
}

String.prototype.stripTrailingSlash = function() {
    if (this.substr(-1) === '/') {
        return this.substr(0, this.length - 1);
    }
    return this;
};
String.prototype.ensureStartsWithSlash = function() {
    if (this.substr(0, 1) !== '/') {
        return '/' + this;
    }
    return this;
};