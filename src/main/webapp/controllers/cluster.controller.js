var app = angular.module("kafka-manager");

app.controller("ClusterController", function ($scope, ClusterService) {

    ClusterService.registerCallback(function () {
        $scope.$apply();
    });

    $scope.xCluster = {};
    $scope.checkedClusters = {};
    $scope.clusters = ClusterService.getAll();

    $scope.clusterCount = function () {
        return ClusterService.count();
    };

    $scope.checkedClusterCount = function () {
        return Object.keys($scope.checkedClusters).length;
    };

    $scope.check = function (clusterId) {
        if ($scope.checkedClusters.hasOwnProperty(clusterId)) {
            delete $scope.checkedClusters[clusterId];
        } else {
            $scope.checkedClusters[clusterId] = ClusterService.get(clusterId).name;
        }

        if ($scope.checkedClusterCount() === 1) {
            $scope.xCluster = ClusterService.get(Object.keys($scope.checkedClusters)[0]);
            $scope.xCluster.zookeepers = $scope.xCluster.zookeepers.join(",");
        } else {
            $scope.xCluster = {};
        }
    };

    $scope.add = function () {
        // TO DO: Validate xCluster variable
        $scope.clusterPromise = ClusterService.add($scope.xCluster);
        $scope.clusterPromise = $scope.clusterPromise.finally(function () {
            $scope.xCluster = {};
            $("#add-cluster-modal").modal('close');
        });
    };

    $scope.update = function () {
        console.log($scope.xCluster);
        // TO DO: Validate xCluster variable
        $scope.clusterPromise = ClusterService.update($scope.xCluster);
        $scope.clusterPromise = $scope.clusterPromise.finally(function () {
            $scope.xCluster = {};
            $scope.checkedClusters = {};
            $("#edit-cluster-modal").modal('close');
        });
    };

    $scope.remove = function () {
        for (var clusterId in $scope.checkedClusters) {
            $scope.clusterPromise = ClusterService.remove(clusterId);
        }
        $scope.checkedClusters = {};
        $("#delete-cluster-modal").modal('close');
    };

    $scope.cancel = function () {
        $scope.xCluster = {};
    };
});