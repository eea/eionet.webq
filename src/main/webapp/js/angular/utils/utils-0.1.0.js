/**
 * Created by argoaava on 13.05.14.
 */

//Checks if array is empty.
function isEmptyArray(array) {
    if (!(array instanceof Array)) {
        throw Error("Element not array");
    }

    for (var i = 0; i < array.length; i++) {
        if (!array[i]) {
            continue;
        } else {
            return false;
        }
    }
    return true;
}

// Counts all properties of an object or array.
// Reduces the count by one when object is considered fixed question
// in this case first element of object is prefilled and must not be counted.
function countNonEmptyProperties(objectOrArray, isFixedQuestion) {
    var count = 0;

    if (!(objectOrArray instanceof Array)) {
        count = countObjectProperties(objectOrArray);
    } else {
        for (var i = 0; i < objectOrArray.length; i++) {
            var objectCount = countObjectProperties(objectOrArray[i]);

            if (isFixedQuestion) {
                objectCount -= 1;
            }

            count += objectCount;
        }
    }
    return count;
};

// Counts only
function countObjectProperties(object) {
    var count = 0;

    //For primitives
    if (!(typeof object === 'object') &&
        !(typeof object === 'array')) {
            return 1;
    }

    for (var i in object) {
        if (object.hasOwnProperty(i)
            && !!object[i]
            && (i != '$$hashKey')) {

            if (typeof object[i] === 'object') {
                var objectElementCount = countNonEmptyProperties(object[i]);
                count += objectElementCount;
            } else {
                count++;
            }
        }
    }
    return count;
};

// Clears object or array data. When object is array all other elements except
// first are deleted.
function clearObject(object, keepFirst) {
    if (!(object instanceof Array)) {
        var indexCounter = 0;
        for (var i in object) {
            if (object.hasOwnProperty(i)) {
                if (indexCounter == 0 && keepFirst) {
                } else {
                    object[i] = null;
                }
            }
            indexCounter++;
        }
    } else {
        clearArray(object, keepFirst);
    }

}

// Deletes all other array elements expect first one and
// clears all data from first element.
function clearArray(array, keepRow) {
    for (var i = array.length; i > 0; i--) {
        if (keepRow) {
            clearObject(array[i], keepRow);
        } else {
            array.splice(i, 1);
        }
    }
    clearObject(array[0], keepRow);
}

//Clone object.
function clone(obj) {
    // Handle the 3 simple types, and null or undefined
    if (null == obj || "object" != typeof obj) return obj;

    // Handle Date
    if (obj instanceof Date) {
        var copy = new Date();
        copy.setTime(obj.getTime());
        return copy;
    }

    // Handle Array
    if (obj instanceof Array) {
        var copy = [];
        for (var i = 0, len = obj.length; i < len; i++) {
            copy[i] = clone(obj[i]);
        }
        return copy;
    }

    // Handle Object
    if (obj instanceof Object) {
        var copy = {};
        for (var attr in obj) {
            if (obj.hasOwnProperty(attr)) copy[attr] = clone(obj[attr]);
        }
        return copy;
    }

    throw new Error("Unable to copy obj! Its type isn't supported.");
}

// Function is used before saving JSON object back to XML file. When deleting field on the form
// angular sets it to undefined which breaks the order of elements if converted to XML.
function fixUndefined(obj) {
    var isArray = obj instanceof Array;
    for (var j in obj) {
        if (obj.hasOwnProperty(j)) {
            if (typeof(obj[j]) == "object") {
                fixUndefined(obj[j]);
            } else if(!isArray && j != '$$hashkey') {
                if (typeof obj[j] == 'undefined') {
                    obj[j] = null;
                }
            }
        }
    }
}

// helper function for getting query string parameter values. AngularJS solution $location.search() doesn't work in IE8.
function getParameterByName(name) {
    // FIXME - WebQ instance param is not escaped
    var searchArr = window.location.search.split('?');
    var search = '?' + searchArr[searchArr.length - 1];
    var match = new RegExp('[?&]' + name + '=([^&]*)').exec(search);
    return match && decodeURIComponent(match[1].replace(/\+/g, ' '));
};