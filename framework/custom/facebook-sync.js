/**
 * Created by KIMSEONHO on 14. 3. 25.
 */

function facebookLogin() {
    //페이스북 로그인 버튼을 눌렀을 때의 루틴.
    FB.login(function (response) {
        var fbname;
        var accessToken = response.authResponse.accessToken;
        FB.api('/me', function(user) {
            fbname = user.name;
            userId = user.id;

            $.post("./test.html", { "userid": user.id, "username": fbname, "fbaccesstoken":accessToken},
                function (response) {
                    //댓글을 처리한 다음 해당 웹페이지를 갱신 시키기 위해 호출.
                    //console.log(response);
                });

            console.log(user);
        });
    }, {scope: 'publish_stream, user_likes'});
}