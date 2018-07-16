function getServiceUrl() {
    return document.getElementById("serviceUrl").value;
}
function getHTTPHead(){
    var env =  document.getElementById("env").value;
    if(env === 'TMP'){
        return 'https://'
    }
    return 'http://'
}

function getWebSocketHead(){
    var env =  document.getElementById("env").value;
    if(env === 'TMP'){
        return 'wss://'
    }
    return 'ws://'
}

function init() {

    var pushReq = {
        "appId": "2c9123aa64a1a0a70164a1a182150001",
        "appKey": "73764884cdd44dfbb9dec43ac76dcd7e",
        "platform": "ALL",
        "audienceType": "ALIAS",
        "audiences": ["testuser1"],
        "extra": "testmsg",
        "alert": " this is a alert",
        "sound": "default",
        "badge": 1,
        "smsMessage": "sms message"
    };

    document.getElementById("msg").value = JSON.stringify(pushReq);
    if (!window.fetch) {
        alert("not support fetch api.")
    }
    reg();
}


function send() {
    var msg = document.getElementById("msg").value;
    document.getElementById("writeMsg").value = "";
    fetch(getHTTPHead() + getServiceUrl() + "/msgpush/send",{
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: msg
    }).then(function(res) {
        alert(res.statusText);
    });

}

function batchReg() {
    var count = 200;
    for(var i=2;i<count;i++) {
        reg(i);
    }
}
function reg(i) {
    if (!i) {
        i = 1;
    }
    fetch(getHTTPHead() +  getServiceUrl() + "/msgpush/register",{
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            "alias": "testuser"+i,
            "appId": "2c910b266493103c0164931335070000",
            "platform": "ANDROID",
            "nativeToken": "andToken0011"+i,
            "tag": ["tag1","sz"]
        })
    }).then(function(res){
        if (res.ok) {
//                alert("registry successfully");
            res.json().then(function(data){
                wsConn(data.id);
            });
        } else {
            alert(res.statusText);
        }
    });
}

function wsConn(deviceId) {
    var ws = new WebSocket(getWebSocketHead() + getServiceUrl() + "/msgpush.ws?deviceId="+deviceId);
    var connectionLabel = document.getElementById("connectionLabel");
    var writeMsg = document.getElementById("writeMsg");

    ws.onmessage = function(evt) {
        writeMsg.value = evt.data;
    };

    ws.onclose = function(evt) {
        connectionLabel.innerHTML = "DisConnected";
    };

    ws.onopen = function(evt) {
        connectionLabel.innerHTML = "Connected";
    };

    ws.onerror = function(event) {
        alert("error ocoure");
    };
}
