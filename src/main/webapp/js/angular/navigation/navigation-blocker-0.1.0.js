/**
 * Created by argoaava on 22.04.14.
 */

// Module that provides functionality to ask confirmation from user when
// form has changed and user tries to leave page without saving.
//
// Usage example:
//
// <div ng-form="appForm" novalidate class="css-form" ng-class="{ 'submitted' : submitted }" td-navigation-blocker-form>
//
// 'td-navigation-blocker-form' must be specified as attribute for the form that confirmation functionality
// must apply to.

//FIXME should be able to make it so that rootScope is not used.
angular.module('navigation.navigationBlocker', [])
    .run(['$rootScope', '$location', function ($rootScope, $location) {
        var _preventNavigation = false;
        var _preventNavigationUrl = null;

        $rootScope.allowNavigation = function() {
            _preventNavigation = false;
        };

        $rootScope.preventNavigation = function() {
            _preventNavigation = true;
            _preventNavigationUrl = $location.absUrl();
        }

        $rootScope.$on('$locationChangeStart', function (event, newUrl, oldUrl) {
            // Allow navigation if our old url wasn't where we prevented navigation from
            if (_preventNavigationUrl != oldUrl || _preventNavigationUrl == null) {
                $rootScope.allowNavigation();
                return;
            }

            if (_preventNavigation && !confirm("You have unsaved changes, do you want to continue?")) {
                event.preventDefault();
            }
            else {
                $rootScope.allowNavigation();
            }
        });

        // Take care of preventing navigation out of our angular app
        window.onbeforeunload = function() {
            // Use the same data that we've set in our angular app
            if (_preventNavigation && $location.absUrl() == _preventNavigationUrl) {
                return "You have unsaved changes, do you want to continue?";
            }
        }
    }])

    .directive("BlockFormNavigation", function () {
        return {
            restrict: 'A',
            require: ['^form'],
            link: function (scope, element, attrs, formController) {
                scope.$watch(attrs.ngForm + '.$dirty', function (dirty) {
                    if (dirty) {
                        scope.$root.preventNavigation();
                    } else {
                        scope.$root.allowNavigation();
                    }
                });
            }
        }
    })


