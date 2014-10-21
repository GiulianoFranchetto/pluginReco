var cordova = require('cordova');

cordova.define("cordova/plugin/recognizer", 
    function(require, exports, module) {
        var exec = require('cordova/exec');

        var Recognizer = function() {
          
          this.channels={
            recoNewMessage:cordova.addWindowEventHandler("newmessage"),
            recoSetupCompleted:cordova.addWindowEventHandler("setupcompleted"),
            test:cordova.addWindowEventHandler("test2"),
            recoError:cordova.addWindowEventHandler("error")
          };

        };
        
        Recognizer.prototype.test = function(){
          exec(recognizer.testOK, recognizer._error, "Recognizer", "test", []);
        };

        Recognizer.prototype.testOK = function(){
          cordova.fireWindowEvent("test2", null);
        };

        Recognizer.prototype.message = function (info) {
          cordova.fireWindowEvent("newmessage", info);
        };

        Recognizer.prototype._setupok = function (message) {
          console.log(message);
          cordova.fireWindowEvent("setupcompleted", message);
        };

        Recognizer.prototype._error = function(err) {
          console.log(err);
          cordova.fireWindowEvent("error", err);
        };

        Recognizer.prototype.setupRecognizer = function(acoustic, dictionnary){
          exec(recognizer._setupok, recognizer._error, "Recognizer", "setupRecognizer", [acoustic, dictionnary]);
        };

        Recognizer.prototype.setupGrammar = function(gramars){
          exec(recognizer._setupok, recognizer._error, "Recognizer", "setupGrammar", gramars);
        };

        Recognizer.prototype.setupKeyphrase = function(name, phrase){
          exec(recognizer._setupok, recognizer._error, "Recognizer", "setupKeyphrase", [name, phrase]);

        };

        Recognizer.prototype.startListening = function(name){
          exec(recognizer.message, recognizer._error, "Recognizer", "startListening", [name]);
        };

        Recognizer.prototype.stopListening = function(){
          exec(null, null, "Recognizer", "stopListening", []);
        };

        var recognizer = new Recognizer();

        module.exports = recognizer;
    }
);
