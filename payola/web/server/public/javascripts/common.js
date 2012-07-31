$(document).ready(function(){

    $('textarea:not(.textarea-fixed)').livequery(function(){
        $(this).autosize();
    });

    $('input[type=text].autosize').live('focus',function(){

        var w = $(this).width();

        $(this).blur(function(){
            $(this).animate({
                width: w+'px'
            }, 500);
        });

        //animate the box
        $(this).animate({
            width: (w*2)+'px'
        }, 400);

    });
});