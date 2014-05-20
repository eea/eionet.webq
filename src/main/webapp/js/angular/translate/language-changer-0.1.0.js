/**
 * Created by argoaava on 22.04.14.
 */

// Module that provides language changing functionality to webform.
//
// Module needs configuration and <div td-language-changer></div> or <td-language-changer/> tags.
// td-language-changer component is replaced with language select input that is configured to use
// pre-configured values.
//
// Example configuration of languageChanger
//
// app.config(["languageChangerProvider", function(languageChangerProvider) {
//    languageChangerProvider.setDefaultLanguage('en');
//    languageChangerProvider.setLanguageFilePrefix('en-labels-');
//    languageChangerProvider.setAvailableLanguages({"item" :[{
//        "code": "bg",
//        "label": "Български (bg)"}, {
//        "code": "cs",
//        "label": "čeština (cs)"}, {
//        "code": "hr",
//        "label": "Hrvatski (hr)"}, {
//        "code": "da",
//        "label": "Dansk (da)"}, {
//        "code": "nl",
//        "label": "Nederlands (nl)"}, {
//        "code": "el",
//        "label": "ελληνικά (el)"}, {
//        "code": "en",
//        "label": "English (en)"}, {
//        "code": "et",
//        "label": "Eesti (et)"}, {
//        "code": "fi",
//        "label": "Suomi (fi)"}, {
//        "code": "fr",
//        "label": "Français (fr)"}, {
//        "code": "de",
//        "label": "Deutsch (de)"}, {
//        "code": "hu",
//        "label": "Magyar (hu)"}, {
//        "code": "is",
//        "label": "Íslenska (is)"}, {
//        "code": "it",
//        "label": "Italiano (it)"}, {
//        "code": "lv",
//        "label": "Latviešu (lv)"}, {
//        "code": "lt",
//        "label": "Lietuvių (lt)"}, {
//        "code": "mt",
//        "label": "Malti (mt)"}, {
//        "code": "no",
//        "label": "Norsk (no)"}, {
//        "code": "pl",
//        "label": "Polski (pl)"}, {
//        "code": "pt",
//        "label": "Português (pt)"}, {
//        "code": "ro",
//        "label": "Română (ro)"}, {
//        "code": "sk",
//        "label": "Slovenčina (sk)"}, {
//        "code": "sl",
//        "label": "Slovenščina (sl)"}, {
//        "code": "es",
//        "label": "Español (es)"}, {
//        "code": "sv",
//        "label": "Svenska (sv)"}, {
//        "code": "tr",
//        "label": "Türkçe (tr)"}]})
//}]);


//TODO Add support for label file format and location configuration.
angular.module('translate.languageChanger', ['pascalprecht.translate'])
    .factory('customLoader', function ($rootScope, $http, $q, languageChanger) {
        return function (options) {
            var deferred = $q.defer();

            if (!languageChanger.getLanguageFilePrefix()) {
                throw new Error("Language file prefix must be define when using languageChanger component.");
            }

            var languageUrl = languageChanger.getLanguageFilePrefix() + options.key + '.json';

            $http({
                method:'GET',
                url: languageUrl
            }, {tracker : $rootScope.loadingTracker})
                .success(function (data) {
                    deferred.resolve(data);
                    //alert('label-' + options.key + '.json' +' success');
                }).error(function () {
                    deferred.reject(options.key);
                    //alert('label-' + options.key + '.json' +' error');

                });
            //alert(options.key);
            return deferred.promise;
        }
    })

    .provider('languageChanger', function languageChangerProvider() {

        this.currentLanguage = 'en';
        this.availableLanguages = [];
        this.languageFilePrefix = '';

        this.setDefaultLanguage = function(defaultLanguage) {
            this.currentLanguage = defaultLanguage;
        };

        this.setLanguageFilePrefix = function(prefix) {
            this.languageFilePrefix = prefix;
        };

        this.setAvailableLanguages = function(availableLanguages) {
            this.availableLanguages = availableLanguages;
        };

        this.$get = function() {
            var availableLanguages = this.availableLanguages;
            var currentLanguage = this.currentLanguage;
            var prefix = this.languageFilePrefix;
            return {
                getLanguage : function() {return currentLanguage;},
                setLanguage : function(newLanguage) {currentLanguage = newLanguage;},
                getAvailableLanguages : function() {return availableLanguages;},
                getLanguageFilePrefix : function() {return prefix;}
            }
        };
    })

    .config( function ($translateProvider) {
        $translateProvider.useLoader('customLoader', {});
        // load 'en' table on startup
        $translateProvider.preferredLanguage('en');
    })

    .controller('LanguageCtrl', ['$scope', '$translate', 'languageChanger', 'dataRepository', function ($scope, $translate, languageChanger, dataRepository) {

        $scope.currentLanguage = languageChanger.getLanguage();
        $scope.availableLanguages = languageChanger.getAvailableLanguages();

        $scope.changeLang = function () {
            languageChanger.setLanguage($scope.currentLanguage)
            $translate.use(languageChanger.getLanguage());
        };

        $scope.$watch('currentLanguage', function(newValue, oldValue) {
            if (!newValue) {
                return;
            } else {
                dataRepository.getCodeList().success(function(codeList) {
                    $scope.codeList = codeList;
                });
            }
        })

    }])

    .directive("languageChanger", function () {
        return {
            restrict: 'A',
            controller: 'LanguageCtrl',
            template: "<div ng-show=\"availableLanguages.item.length > 0\" class=\"span2\" ng-controller=\"LanguageCtrl\" style=\"float: right;\"><select name=\"FormLanguage\" class=\"input-medium\" ng-model=\"currentLanguage\" ng-options=\"language.code as language.label for language in availableLanguages.item\" ng-change=\"changeLang()\"></select></div>"
        }
    })
