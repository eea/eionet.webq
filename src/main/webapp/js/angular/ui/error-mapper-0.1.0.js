/**
 * Created by argoaava on 13.05.14.
 */

angular.module('ui.errorMapper', [])
    .service('errorMapperService', function() {
        this.errorMappings = {
            "required" : "This is a required field",
            "pattern_decimal" : "Please provide a number greater than 0",
            "pattern_integer" : "Please provide a whole number greater than 0",
            "telephone" : "Please enter a valid telephone number (at least 7 digits)",
            "email" : "Please enter a valid email address",
            "url" : "Please enter a valid URL"};

        this.getErrorMappings = function() {
            return this.errorMappings;
        }

        this.addErrorMapping = function(errorMapping) {
            this.errorMappings.push(errorMapping);
        }

        this.setErrorMappings = function(errorMappings) {
            this.errorMappings = errorMappings;
        }
    })
    .controller("ErrorController", function ($scope, errorMapperService) {

        $scope.errorMappings = errorMapperService.getErrorMappings();
        $scope.showCurrentError = false;

        $scope.getErrorMessage = function(errorCode) {
            return $scope.errorMappings[errorCode];
        };

        $scope.getController = function(attributes) {
            if (!attributes.watchView) {
                return null;
            }
            var tokens = attributes.watchView.split(".");
            var result = $scope;
            while(tokens.length) {
                result = result[tokens.shift()];
                if (!result) {
                    return null;
                }
            }
            return result;
        };

        $scope.parseErrors = function(ctrl, attributes) {
            var fieldNameIdentifier = !attributes.watchView? attributes.errorMapper : attributes.watchView;
            var nameTokens = fieldNameIdentifier.split('.');
            var name = nameTokens[1];

            var fieldController = !ctrl[name]? $scope.getController(attributes) : ctrl[name];
            if (!fieldController) {
                if (!attributes.radioButtonValues) {
                    throw Error("input field controller not found for " + attributes.errorMapper);
                } else {
                    var radioButtonValues = JSON.parse(attributes.radioButtonValues);
                    $scope.showCurrentError = !radioButtonValues.value && $scope.submitted && radioButtonValues.required;
                    $scope.currentErrorCode = 'required';
                    return;
                }
            }

            var controllerErrors = fieldController.$error;
            var errorsToShowAsArray = JSON.parse(attributes.errorsToShow);

            for (var i = 0; i < errorsToShowAsArray.length; i++) {
                var splitErrorString = errorsToShowAsArray[i].split('_');
                var strippedErrorString = splitErrorString[0];
                if (controllerErrors[strippedErrorString]) {
                    $scope.showCurrentError = $scope.showError(fieldController, strippedErrorString);
                    $scope.currentErrorCode = errorsToShowAsArray[i];
                    break;
                } else {
                    $scope.showCurrentError = false;
                }
            }
        }

        $scope.showError = function(modelController, errorCode) {
            return ($scope.submitted || modelController.$dirty) && modelController.$error[errorCode] && modelController.$invalid;
        };
    })

    // Error mapper directive to simplify showing errors for input fields and get rid of some
    // boilerplate code.
    //
    // Usage:
    //
    // <input name="Money" ng-model="instance.MMRArticle17Questionnaire.Table1.Money" type="text"
    //      ng-pattern="decimalNumberPattern" required/>
    // <div class="invalid-msg" td-error-mapper watch-elements='["Table1.OtherDescription"]'
    //      watch-view="Form.InputName" errors-to-show='["required", "pattern_decimal"]'></div>
    // ...
    // Where <div> with 'td-error-mapper' attribute is used as error div and all functionality is applied.
    //
    // Configuration parameters:
    // td-error-mapper - identifier for error mapper and scope parameter watcher e.g td-error-mapper="userName". Whenever
    //                   $scope.userName changes it is picked up by mapper and correct error is shown when identified.
    // watch-view      - Used to watch view value of the field. Useful for watching number fields etc. when actual scope
    //                   value does not change on invalid field value.
    //                   Should be used as {FormName}.{InputName} so InputName can be extracted to get correct controller.
    // watch-elements  - Array of string that is parsed and registered for watching. Whenever 'Table1.OtherDescription'
    //                   changes then parse function is called and correct errors show for current input.
    // errors-to-show  - Errors to be shown as array. This string is parsed and errors are shown in the same order
    //                   as array. Names can be used for different kind of messages as follows e.g "pattern_decimal"
    //                   means error type is pattern and message type decimal. First part is extracted used together with
    //                   angular ng-pattern directive to get correct result.
    .directive('errorMapper',function () {
        return {
            restrict: 'A',
            require: '?^form',
            scope: true,
            controller: 'ErrorController',
            template: '<span ng-show=\"showCurrentError\">{{getErrorMessage(currentErrorCode)}}</span>',
            link: function(scope, element, attrs, ctrl) {
                if (!attrs.watchView) {
                    scope.$watch(attrs.errorMapper, function() {
                        scope.parseErrors(ctrl, attrs);
                    });
                } else {
                    scope.$watch(attrs.watchView + ".$viewValue", function() {
                        scope.parseErrors(ctrl, attrs);
                    });
                }

                scope.$watch('submitted', function() {
                    scope.parseErrors(ctrl, attrs);
                });

                //For watching other values that this error depends on
                if (attrs.watchElements) {
                    var watchArray = JSON.parse(attrs.watchElements);
                    for (var i = 0; i < watchArray.length; i++) {
                        scope.$watch(watchArray[i] + ".$viewValue", function() {
                            scope.parseErrors(ctrl, attrs);
                        });
                    }
                }

            }
        }
    })