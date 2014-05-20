/**
 * Created by argoaava on 13.05.14.
 */


// Module that contains functionality for tab navigation. User must fill 'tabService' with
// available tabs as follows:
//
// app.run(function($rootScope, promiseTracker, $location, tabService) {
//      tabService.setTabs([
//          {"id":"Table1","active" : true},
//          {"id":"Table2","active" : false},
//          {"id":"Table3","active" : false},
//          {"id":"Table4","active" : false},
//          {"id":"Table5","active" : false}]);
// });
//
// then 'TabController' must be set to elements that want to interact with
// tabService.
//
// <ul tabset ng-cloak ng-controller="TabController">
//      <div ng-class="{'invalidTab' : isInvalidTab('Table1')}">
//          <li tab heading="Table 1" active="tabs[getTabIndex('Table1')].active">
//          </li>
//      </div>
// </ul>
//
// or
//
// <div class="animate-show" ng-show="showMenu" style="float:right" ng-controller="TabController">
//      <input type="button" ng-click="previousTab()" value="Prev" class="btn btn-default btn-primary" ng-disabled="getActiveTabIndex() == 0">
//      <input type="button" ng-click="nextTab()" value="Next" class="btn btn-default btn-primary" ng-disabled="getActiveTabIndex() == (tabs.length - 1)"/>
// </div>
//
angular.module('tabs.formTabs', [])
    .service('tabService', function() {
        this.tabs = [];

        this.getTabs = function() {
            return this.tabs;
        }

        this.setTabs = function(tabs) {
            this.tabs = tabs;
        }
    })
    .controller("TabController", function ($scope, $location, $timeout, $anchorScroll, tabService) {

        if (tabService.getTabs().length == 0) {
            throw Error("You must configure tabService");
        }

        $scope.tabs = tabService.tabs;

        $scope.goto = function (tab, id){
            for (var i = 0; i < $scope.tabs.length; i++) {
                if ($scope.tabs[i].id == tab) {
                    $scope.tabs[i].active = true;
                } else {
                    $scope.tabs[i].active = false;
                }
            }

            var old = $location.hash();
            $timeout(function() {
                $location.hash(id);
                $anchorScroll();
                $location.hash(old);
            }, 200);
        };

        $scope.getActiveTabIndex = function() {
            for (var i = 0; i < $scope.tabs.length; i++) {
                if ($scope.tabs[i].active == true) {
                    return i;
                }
            }
            return 0;
        };

        $scope.getTabIndex = function(tabId) {
            for (var i = 0; i < $scope.tabs.length; i++) {
                if ($scope.tabs[i].id == tabId) {
                    return i;
                }
            }
            return -1;
        };

        $scope.nextTab = function(){
            var activeTabIndex = $scope.getActiveTabIndex();
            var nextTab = (activeTabIndex + 1 <= $scope.tabs.length)? activeTabIndex+1 : activeTabIndex;
            $scope.goto($scope.tabs[nextTab].id, 'beginning');
        };

        $scope.previousTab = function(){
            var activeTabIndex = $scope.getActiveTabIndex();
            var previousTab = (activeTabIndex - 1 >= 0)? activeTabIndex - 1 : activeTabIndex;
            $scope.goto($scope.tabs[previousTab].id, 'beginning');
        };

        $scope.isInvalidTab = function(tabId) {
            return $scope.submitted && $scope.appForm[tabId].$invalid;
        };
    })