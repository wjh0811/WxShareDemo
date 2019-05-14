window.onload = function() {
	var videoListId = ''
	//切换菜单栏
	$(".tablink").on("click", "a", function() {
		if(!$(this).hasClass('active')) {
			$(".tablink a.active").removeClass('active');
			$(this).addClass('active');
			//ajax
			videoListId = $(this).attr("videoListId")
			alert(videoListId)
		}

	})

	//视频详情
	$(".lvideo_view li").on("click", "a", function() {

		var videoDetailId = $(this).attr("data-id");
		alert(videoDetailId);
		window.location.href = "video_play.html?dataId=" + $(this).attr("data-id");
	})

}