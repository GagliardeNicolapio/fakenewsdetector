$(".circle_percent").each(function() {
    var $this = $(this),
        $dataV = $this.data("percent"),
        $dataDeg = $dataV * 3.6,
        $round = $this.find(".round_per");
    $round.css("transform", "rotate(" + parseInt($dataDeg + 180) + "deg)");
    $this.append('<div class="circle_inbox"><span class="percent_text"></span></div>');
    $this.prop('Counter', 0).animate({Counter: $dataV},
        {
            duration: 2000,
            easing: 'swing',
            step: function (now) {
                $this.find(".percent_text").text(Math.ceil(now)+"%");
            }
        });
    if($dataV >= 51){
        $round.css("transform", "rotate(" + 360 + "deg)");
        setTimeout(function(){
            $this.addClass("percent_more");
        },1000);
        setTimeout(function(){
            $round.css("transform", "rotate(" + parseInt($dataDeg + 180) + "deg)");
        },1000);
    }
});

var percent = document.getElementsByClassName('circle_percent')[0].getAttribute('data-percent');
if(percent <= 49){
    document.getElementsByClassName('round_per')[0].style.background = 'red';
}else if(percent >= 90){
    document.getElementsByClassName('round_per')[0].style.background = 'green';
}else{
    document.getElementsByClassName('round_per')[0].style.background = '#ffdd55';
}

window.onbeforeunload = function() {
    document.getElementById('contenitoreProg').style.display = 'block';
    document.getElementById('containerFeedback').style.display = 'none';
    document.getElementsByTagName('header')[0].style.display = 'none';
};


function abortPreloader(){
    window.onbeforeunload = function (){}
}