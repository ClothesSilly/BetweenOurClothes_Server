function preview(file){
  $.ajax({
    type:"GET",
    url:"/api/v1/closets/post/thumbnails/display",
    dataType: "image/jpeg",

    success: function(args){
      if(args.exist){
      	$("#img").attr("src", "data:image/jpeg;base64," + res.blob);
      }else{
      	alert("파일이 존재하지 않습니다.");
      }
    },

    error: function(error){
    	console.log(error);
    	alert("오류가 발생했습니다.");
    }
  });
}