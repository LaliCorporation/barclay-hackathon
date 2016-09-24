var dispatcher = require('httpdispatcher');
var http = require('http');
var bot = require('./index');
var url = require('url');
var http = require('http');
var https = require('https');
var async = require('async');
var url = require('url');
var botRunner = require('./botRunner');
var ngrok = require('ngrok');
var request = require("request");

process.argv.forEach(function(val, index, array) {
    console.log(index + ': ' + val);
});

var PropertiesReader = require('properties-reader');
var properties = PropertiesReader('.botconfig');

var args = process.argv.slice(2);
const PORT = args[0] || 8081;
const stage = args[1] || "prod";
var botName = properties.get('botName');
var smApiUrl = '';
if ("prod" == stage) {
    smApiUrl = 'https://api.gupshup.io/sm/api/v1/bot/' + botName + '/settings/type/callback-get';
} else {
    smApiUrl = 'https://dev-api.gupshup.io/sm/api/v1/bot/' + botName + '/settings/type/callback-get';
}

function handleRequest(request, response) {
    try {
        console.log(request.url);
        dispatcher.dispatch(request, response);
    } catch (err) {
        console.log(err);
    }
}
var server = http.createServer(handleRequest);
server.listen(PORT, function() {
    //Callback triggered when server is successfully listening. Hurray!
    console.log("Server listening on: http://localhost:%s", PORT);
});

var publicUrl = "";
ngrok.connect({
    proto: 'http', // http|tcp|tls
    addr: PORT, // port or network address
    // auth: 'user:pwd', // http basic authentication for tunnel
    // subdomain: 'mainBot', // reserved tunnel name https://alex.ngrok.io
    // authtoken: '12345', // your authtoken from ngrok.com
    region: 'us' // one of ngrok regions (us, eu, au, ap), defaults to us
}, function(err, url) {
    console.log("*** Ngrok initialization ***");
    if (err) {
        console.error("Error on Ngrok initialization");
        console.error(err);
    }
    console.log("*** Ngrok URL ***");
    console.log(url);
    publicUrl = url;

    setCallbackUrl(url + "/botcallback");
});


var botenv = {};
botenv.botcontext = {};
botenv = botRunner.setupContext();

dispatcher.onGet("/botcallback", function(req, res) {
    var queryObject = url.parse(req.url, true).query;
    try {
        var event = setupEvent(queryObject);
        var context = botenv.botcontext;

        botenv.botcontext.sendResponse = function(respstr) {
            context.simpledb.saveCommonAndContinue(function() {
                console.log('Response sent to user :-> ', res);
                res.end(respstr);
            });
        }
        botenv.botcontext.sendError = function(err, f) {
            context.simpledb.saveCommonAndContinue(function() {
                console.log('Sending Error to user :-> ', err);
                if (f) {
                    res.end(JSON.stringify(err, null, '\t'));
                } else {
                    if (isJson(err)) {
                        res.end('ERROR : ' + JSON.stringify(err, null, '\t'));
                    } else {
                        res.end('ERROR : ' + err);
                    }
                }
            });
        }

        botRunner.executeFunction(context, event, res)
            // bot.handleMessage(context, event);
    } catch (e) {
        console.error('####');
        console.error(e);
        if (isJson(e)) {
            res.end('ERROR : ' + JSON.stringify(e, null, '\t'));
        } else {
            res.end('ERROR : ' + e);
        }
    }
});

function isJson(jsonStr) {
    try {
        JSON.parse(jsonStr);
    } catch (e) {
        return false;
    }
    return true;
}


function setupEvent(queryObject) {
    var contextobj = JSON.parse(queryObject.contextobj);
    var messageobj = JSON.parse(queryObject.messageobj);
    var senderobj = JSON.parse(queryObject.senderobj);

    var botevent = {};
    botevent.botname = queryObject.botname;
    botevent.sender = senderobj;
    botevent.senderobj = senderobj;
    botevent.message = messageobj.text;
    botevent.messageobj = messageobj;
    botevent.context = contextobj;
    botevent.contextobj = contextobj;
    return botevent;
}


function setCallbackUrl(callbackUrl) {
    // https: //dev-api.gupshup.io/sm/api/v1/bot/MainBotIBC/settings/type/callback-get
    var botName = properties.get('botName');
    var apikey = properties.get('apikey');
    var options = {
        method: 'POST',
        url: smApiUrl,
        headers: {
            'content-type': 'application/x-www-form-urlencoded',
            'cache-control': 'no-cache',
            apikey: apikey
        },
        form: {
            version: 'v1',
            url: callbackUrl,
            method: 'POST',
            bot: botName

        }
    };

    request(options, function(error, response, body) {
        if (error) throw new Error(error);
        console.log(body);
    });
}
