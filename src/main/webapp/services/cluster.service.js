var app = angular.module("kafka-manager");

app.service("ClusterService", function ($rootScope, $http) {

    var CLUSTER_API = $rootScope.appContext + "clusters";

    var observerCallbacks = [];
    var clusters = {};

    var promise = $http
            .get(CLUSTER_API)
            .then(function (response) {
                if (response.status === 200) {
                    response.data.forEach(function (cluster) {
                        clusters[cluster.id] = cluster;
                    });
                }
            });

    var webSocket = new WebSocket("ws://" + window.location.host + $rootScope.appContext + "ws/clusters");

    webSocket.onmessage = function (message) {
        var data = JSON.parse(message.data);
        if (data.event === "DELETE") {
            delete clusters[data.clusterId];
        } else if (data.event === "CREATE" || data.event === "UPDATE") {
            clusters[data.clusterId] = JSON.parse(data.cluster);
        }

        observerCallbacks.forEach(function (callback) {
            callback();
        });
    };

    var registerCallback = function (callback) {
        if (callback) {
            observerCallbacks.push(callback);
        }
    };

    var count = function () {
        return Object.keys(clusters).length;
    };

    var getAll = function () {
        return clusters;
    };

    var get = function (clusterId) {
        return Object.assign({}, clusters[clusterId]);
    };

    var remove = function (clusterId) {
        return $http
                .delete(CLUSTER_API + "/" + clusterId)
                .then(function (response) {
                    if (response.status === 200) {
                        _toast("Cluster [" + clusters[clusterId].name + "] removed.");
                        delete clusters[clusterId];
                    } else {
                        _toast("Error removing cluster: " + response.data);
                    }
                });
    };

    var create = function (cluster) {
        return $http
                .post(CLUSTER_API, {
                    name: cluster.name,
                    zookeepers: cluster.zookeepers.split(","),
                    jmx: cluster.jmx,
                    jmxUsername: cluster.jmxUsername,
                    jmxPassword: cluster.jmxPassword
                })
                .then(function (response) {
                    if (response.status === 201) {
                        cluster = response.data;
                        clusters[cluster.id] = cluster;
                        _toast("Cluster [" + cluster.name + "] created.");
                    } else {
                        _toast("Error creating cluster: " + response.data);
                    }
                });
    };

    var update = function (cluster) {
        return $http
                .put(CLUSTER_API + "/" + cluster.id, {
                    id: cluster.id,
                    name: cluster.name,
                    zookeepers: cluster.zookeepers.split(","),
                    jmx: cluster.jmx,
                    jmxUsername: cluster.jmxUsername,
                    jmxPassword: cluster.jmxPassword
                })
                .then(function (response) {
                    if (response.status === 200) {
                        cluster = response.data;
                        clusters[cluster.id] = cluster;
                        _toast("Cluster [" + cluster.name + "] updated.");
                    } else {
                        _toast("Error updating cluster: " + response.data);
                    }
                });
    };

    function _toast(message) {
        Materialize.toast(message, 2000, 'rounded');
    }

    return {
        initPromise: promise,
        add: create,
        update: update,
        remove: remove,
        get: get,
        getAll: getAll,
        count: count,
        registerCallback: registerCallback
    };

});