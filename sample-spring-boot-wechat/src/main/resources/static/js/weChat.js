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

    function sendMsg(str) {
         var userId = $(".panel>ul>li>a.current").data("id");
         var msg = {
             "appId": appId,
             "appKey": appKey,
             "platform": "ALL",
             "audienceType": "ALIAS",
             "audiences": ["testuser1"],
             "extra": str,
             "alert": str,
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

    function dateFtt(fmt,date)
    { //author: meizz
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

    function wsConn(deviceId) {

        var ws = new WebSocket(getWebSocketHead() + getServiceUrl() + "/msgpush.ws?deviceId="+deviceId);
        var connectionLabel = document.getElementById("connectionLabel");
        ws.onmessage = function(evt) {
            console.info(evt.data);
            var obj = eval('(' + evt.data + ')');
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
        };

        ws.onclose = function(evt) {
            connectionLabel.innerHTML = "离线";
        };

        ws.onopen = function(evt) {
            connectionLabel.innerHTML = "在线";
        };

        ws.onerror = function(event) {
            alert("error ocoure");
        };
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
            body: "userId="+userId
         }).then(function(res) {
             console.info(res);
             if(typeof callback === "function"){
                callback(res);
             }
         });
    }
});

