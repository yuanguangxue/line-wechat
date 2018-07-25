$(function () {
    var _editArea = $('#editArea');
    function getServiceUrl() {
        return $("#serviceUrl").val();
    }
    function getHTTPHead(){
        var env =  $("#env").val();
        if(env === 'TMP'){
            return 'https://'
        }
        return 'http://'
    }

    function getWebSocketHead(){
        var env =  $("#env").val();
        if(env === 'TMP'){
            return 'wss://'
        }
        return 'ws://'
    }
    var appId = $("#appId").val();
    var appKey = $("#appKey").val();
    //显示隐藏发送按钮
    var _editAreaInterval;

    $('#editArea').focus(function () {
        var _this = $(this), html;
        _editAreaInterval = setInterval(function () {
            html = _this.html();
            if (html.length > 0) {
                $('#web_wechat_pic').hide();
                $('#btn_send').show();
            } else {
                $('#web_wechat_pic').show();
                $('#btn_send').hide();
            }
        }, 200);
    });

    $('#editArea').blur(function () {
        clearInterval(_editAreaInterval);
    });

    //显示隐藏表情栏
    $('.web_wechat_face').click(function () {
        $('.box_ft_bd').toggleClass('hide');
        resetMessageArea();
    });

    //切换表情主题
    $('.exp_hd_item').click(function () {
        var _this = $(this), i = _this.data('i');
        $('.exp_hd_item,.exp_cont').removeClass('active');
        _this.addClass('active');
        $('.exp_cont').eq(i).addClass('active');
        resetMessageArea();
    });

    //选中表情
    $('.exp_cont a').click(function () {
        var _this = $(this);
        if(_this.hasClass("sticker")){
             sendSticker(_this);
             $('#web_wechat_pic').hide();
             $('#btn_send').show();
             resetMessageArea();
             $('.box_ft_bd').addClass('hide');
             return;
        }
        if(_this.hasClass("flex")){
             sendFlex(_this);
             $('#web_wechat_pic').hide();
             $('#btn_send').show();
             resetMessageArea();
             $('.box_ft_bd').addClass('hide');
             return;
        }
        var html = '<img class="' + _this[0].className + '" title="' + _this.html() + '" src="images/spacer.gif">';
        _editArea.html(_editArea.html() + html);
        $('#web_wechat_pic').hide();
        $('#btn_send').show();
        resetMessageArea();
    });

    resetMessageArea();

    var sendMsgTpl = [
        '<div class="message me">',
        '	<img class="avatar" src="${avatar}" >',
        '	<div class="content">',
        '		<div class="nickname">${displayName}<span class="time">${time}</span></div>',
        '		<div class="bubble bubble_primary right">',
        '			<div class="bubble_cont">',
        '				<div class="plain">',
        '					<pre>$${text}</pre>',
        '				</div>',
        '			</div>',
        '		</div>',
        '	</div>',
        '</div>'
    ].join('');

     var getMsgTpl = [
            '<div class="message">',
            '	<img class="avatar" src="${avatar}" >',
            '	<div class="content">',
            '		<div class="nickname">${displayName}<span class="time">${time}</span></div>',
            '		<div class="bubble bubble_default left">',
            '			<div class="bubble_cont">',
            '				<div class="plain">',
            '					<pre>$${text}</pre>',
            '				</div>',
            '			</div>',
            '		</div>',
            '	</div>',
            '</div>'
        ].join('');

    $("#btn_send").on("click",function(){
          var html = _editArea.html();
          sendMsg(html);
          _editArea.empty();
    });

    $(".panel > ul > li > a").on ("dblclick",function(){
        var that  = this;
        $(that).addClass("current")
                        .parent().siblings("li").find("a").removeClass("current");
        var userId = $(".panel>ul>li>a.current").data("id");
        loadHistoryPushMsg(userId,function(res){

        });
    });

    $("#messageList").on("click",function(){
        if(!$('.box_ft_bd').hasClass('hide')){
            $('.box_ft_bd').addClass('hide');
        }
    });

    function sendSticker(a){
        var str = JSON.stringify({
            type:"sticker",
            packageId:"1",
            stickerId:a.data("id")
        });
        sendMsg(str);
    }

    function sendFlex(a){
        var str = JSON.stringify({
             "type": "flex",
             "altText": "this is a flex message",
             "contents": {
                 "type": "bubble",
                 "body": {
                     "type": "box",
                     "layout": "vertical",
                     "contents": [
                         {
                           "type": "text",
                           "text": "hello"
                         },
                         {
                           "type": "text",
                           "text": "world"
                         }，
                         {
                            "type": "button",
                            "style": "primary",
                            "action": {
                              "type": "uri",
                              "label": "Primary url button",
                              "uri": "https://guangxue.herokuapp.com"
                            }
                         },
                         {
                            "type": "button",
                            "style": "primary",
                            "action": {
                                "type":"postback",
                                "label":"Buy",
                                "data":"action=buy&itemid=111",
                                "text":"Primary postback button"
                            }
                         },
                         {
                             "type": "button",
                             "style": "primary",
                             "action": {
                                "type":"message",
                                "label":"Primary message button",
                                "text":"Yes"
                             }
                         }
                     ]
                 }
             }
        });
        sendMsg(str);
    }

    function sendMsg(str) {
         var userId = $(".panel>ul>li>a.current").data("id");
         var msg = {
             "appId": appId,
             "appKey": appKey,
             "platform": "ALL",
             "audienceType": "ALIAS",
             "audiences": ["testuser1"],
             "extra": str,
             "alert": "send line message",
             "sound": "default",
             "pushMsgType":"LINE",
             "badge": 1,
             "smsMessage": str,
             "sender" : "me",
             "target": userId
         }

         fetch(getHTTPHead() + getServiceUrl() + "/msgpush/send",{
            method: "POST",
            headers: {
               "Content-Type": "application/json"
            },
            body: JSON.stringify(msg)
         }).then(function(res) {
             console.info(res);
         });
    }

    function sendMsgStr(str,time){
       var html = juicer(sendMsgTpl,{
            text:str,
            time:time,
            avatar:'https://obs.line-scdn.net/0hytw49WslJltKTQmaOhZZDHQQLTl5LzhQaCg9PjoYeD5uKmZaJis8ODwjeGM1KmkMcC0sPGgZeztlKjM'
       });
       $("#messageList").append(html);
       resetMessageAreaButton();
    }

    function getMsgStr(str,time){
        var avatar = $(".panel>ul>li>a.current").data("avatar");
        var html = juicer(getMsgTpl,{
            text:str,
            time:time,
            avatar:avatar
        });
         $("#messageList").append(html);
         resetMessageAreaButton();
    }

    function resetMessageAreaButton(){
        var h = $("#messageList")[0].scrollHeight
        $("#messageList").scrollTop(h);
    }

    function resetMessageArea() {
        $('#messageList').animate({ 'scrollTop': 999 }, 500);
    }

    function init(){
        var userId = $(".panel>ul>li>a.current").data("id");
        loadHistoryPushMsg(userId,function(res){
            reg();
        });
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
                "alias": "testuser" + i,
                "appId": appId,
                "platform": "ANDROID",
                "nativeToken": "andToken0011"+i,
                "tag": ["tag1","sz"]
            })
        }).then(function(res){
            if (res.ok) {
                res.json().then(function(data){
                    wsConn(data.id);
                });
            } else {
                console.info(res);
            }
        });
    }

    function dateFtt(fmt,date){
      var o = {
        "M+" : date.getMonth()+1,                 //月份
        "d+" : date.getDate(),                    //日
        "h+" : date.getHours(),                   //小时
        "m+" : date.getMinutes(),                 //分
        "s+" : date.getSeconds(),                 //秒
        "q+" : Math.floor((date.getMonth()+3)/3), //季度
        "S"  : date.getMilliseconds()             //毫秒
      };
      if(/(y+)/.test(fmt))
        fmt=fmt.replace(RegExp.$1, (date.getFullYear()+"").substr(4 - RegExp.$1.length));
      for(var k in o)
        if(new RegExp("("+ k +")").test(fmt))
      fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
      return fmt;
    }

    function addMessage(userId){
        var ele = $(".panel>ul>li>a[data-id='"+userId+"']>.num");
        if(ele.length > 0){
            var text = ele.text();
            try{
                var num = parseInt(text) + 1;
                ele.text(num + "").show();
            }catch(e){
                ele.text("1").show();
            }
        }
    }

    function cleanMessage(userId){
        var ele = $(".panel>ul>li>a[data-id='"+userId+"']>.num");
        if(ele.length > 0){
            ele.text("0").hide();
        }
    }

    function wsConn(deviceId) {

        var ws; //websocket实例
        var lockReconnect = false;//避免重复连接
        var wsUrl = getWebSocketHead() + getServiceUrl() + "/msgpush.ws?deviceId="+deviceId;
        var connectionLabel = document.getElementById("connectionLabel");

        function createWebSocket(url) {
            try {
                ws = new WebSocket(url);
                initEventHandle();
            } catch (e) {
                reconnect(url);
            }
        }

        function initEventHandle() {

            ws.onmessage = function(evt) {
                //拿到任何消息都说明当前连接是正常的
                heartCheck.reset().start();
                console.info(evt.data);
                var obj = eval('(' + evt.data + ')');
                var time = "";
                if(obj.createdAt !== null){
                    var date = new Date(obj.createdAt);
                    time = dateFtt("yyyy-MM-dd hh:mm",date);
                }
                var userId = $(".panel>ul>li>a.current").data("id");
                if(obj.target === 'me'){
                    if(obj.sender === userId){
                       getMsgStr(obj.extra,time);
                    }else{
                       //执行其他消息弹出提示
                       addMessage(obj.sender);
                    }
                }else{
                    if(obj.target === userId){
                        sendMsgStr(obj.extra,time);
                    }else{
                        //执行其他消息弹出
                        console.info("add error message ...");
                        console.info(obj);
                    }
                }
            };

            ws.onclose = function(evt) {
                connectionLabel.innerHTML = "离线";
                reconnect(wsUrl);
            };

            ws.onopen = function(evt) {
                 connectionLabel.innerHTML = "在线";
                 //心跳检测重置
                 heartCheck.reset().start();
            };

            ws.onerror = function(event) {
                console.info("error ocoure");
                reconnect(wsUrl);
            };
        }

        function reconnect(url) {
            if(lockReconnect) return;
            lockReconnect = true;
            //没连接上会一直重连，设置延迟避免请求过多
            setTimeout(function () {
                createWebSocket(url);
                lockReconnect = false;
            }, 2000);
        }

        //心跳检测
        var heartCheck = {
            timeout: 60000,//60秒
            timeoutObj: null,
            serverTimeoutObj: null,
            reset: function(){
                clearTimeout(this.timeoutObj);
                clearTimeout(this.serverTimeoutObj);
                return this;
            },
            start: function(){
                var self = this;
                this.timeoutObj = setTimeout(function(){
                    //这里发送一个心跳，后端收到后，返回一个心跳消息，
                    //onmessage拿到返回的心跳就说明连接正常
                    ws.send("HeartBeat");
                    self.serverTimeoutObj = setTimeout(function(){//如果超过一定时间还没重置，说明后端主动断开了
                        ws.close();//如果onclose会执行reconnect，我们执行ws.close()就行了.如果直接执行reconnect 会触发onclose导致重连两次
                    }, self.timeout)
                }, this.timeout)
            }
        }

        createWebSocket(wsUrl);
    }

    $("#link").on("click",function(){
        init();
    });

    init();

    function loadHistoryPushMsg(userId,callback){
         fetch(getHTTPHead() + getServiceUrl() + "/msgpush/loadHistoryPushMsg",{
            method: "POST",
            headers: {
               "Content-Type": "application/json"
            },
            body: JSON.stringify({
                 "appId": appId,
                 "platform": "ALL",
                 "appKey": appKey,
                 "userId": userId
            })
         }).then(function(res) {
             console.info(res);
             return res.json();
         }).then(function(data){
             $("#messageList").empty();
             cleanMessage(userId);
             if($.isArray(data)){
                $.each(data,function(i,obj){
                    var time = "";
                    if(obj.createdAt !== null){
                        var date = new Date(obj.createdAt);
                        time = dateFtt("yyyy-MM-dd hh:mm",date);
                    }
                    if(obj.target === 'me'){
                        getMsgStr(obj.extra,time);
                    }else{
                        sendMsgStr(obj.extra,time);
                    }
                });
             }
             if(typeof callback === "function"){
                callback(data);
             }
         });
    }
});

