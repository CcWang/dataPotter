var offset = 0;
var count = 10;
var total = 100;

$(document).ready(function () {

    getSavy();

    function getSavy() {


        jQuery.ajax ({
            // url:  "/api/contributors/" + finalvalue.contributorId + "/books?offset=" + offset + "&count="  + count,
            url:"/api/savy",
            type: "GET",
        })
            .done(function(data){

                console.log("here"+data.content);
                $("#savyTable").find(".cloned").remove();
                data.content.forEach(function(item){
                    $( "#qRow" ).clone().prop("id",item.id).appendTo( "#savyTable" );
                    $("#"+item.id).find("#question").text(item.question);
                    $("#"+item.id).find("#qAns1").text(item.answer01);
                    $("#"+item.id).find("#qAns2").text(item.answer02);
                    $("#"+item.id).find("#qAns3").text(item.answer03);
                    $("#"+item.id).find("#qAns4").text(item.answer04);
                    $("#"+item.id).prop("class","cloned");
                    $("#"+item.id).show();
                });
            })
            .fail(function(data){
                console.log("here");
                $("#questionList").text("Sorry no Questions");
            })

    }

    $(document).on('click', '.ans1', function() {
        var id = $(this).closest("tr")[0].id;
        // console.log(id);
        var l;
        count = getCount(id, 1);

    });

    $(document).on('click', '.ans2', function() {
        var id = $(this).closest("tr")[0].id;
        // console.log(id);
        var l;
        count = getCount(id, 2);

    });

    $(document).on('click', '.ans3', function() {
        var id = $(this).closest("tr")[0].id;
        // console.log(id);
        var l;
        count = getCount(id, 3);

    });

    $(document).on('click', '.ans4', function() {
        var id = $(this).closest("tr")[0].id;
        // console.log(id);
        var l;
        count = getCount(id, 4);

    });

    function getCount(id, num) {


        jQuery.ajax ({
            // url:  "/api/contributors/" + finalvalue.contributorId + "/books?offset=" + offset + "&count="  + count,
            url:"/api/savy/"+id,
            type: "GET",
        })
            .done(function(data){

                console.log("here"+data.content);

                if (num == 1){
                    var count_data = data.content.answer01count;

                    alert(count_data);
                    var l = count_data+1;

                    alert(l);
                    jQuery.ajax ({
                        url: "../api/savy/"+id,
                        type: "PATCH",
                        data: JSON.stringify({answer01count:l}),
                        dataType: "json",
                        contentType: "application/json; charset=utf-8",

                    }).done(function(data){
                        alert("Your information has been updated");

                    }).fail(function(data){
                        $("#greeting").text("You might want to try it again");
                    })
                }

                if (num == 2){
                    var count_data = data.content.answer02count;

                    alert(count_data);
                    var l = count_data+1;

                    alert(l);
                    jQuery.ajax ({
                        url: "../api/savy/"+id,
                        type: "PATCH",
                        data: JSON.stringify({answer02count:l}),
                        dataType: "json",
                        contentType: "application/json; charset=utf-8",

                    }).done(function(data){
                        alert("Your information has been updated");

                    }).fail(function(data){
                        $("#greeting").text("You might want to try it again");
                    })
                }

                if (num == 3){
                    var count_data = data.content.answer03count;

                    alert(count_data);
                    var l = count_data+1;

                    alert(l);
                    jQuery.ajax ({
                        url: "../api/savy/"+id,
                        type: "PATCH",
                        data: JSON.stringify({answer03count:l}),
                        dataType: "json",
                        contentType: "application/json; charset=utf-8",

                    }).done(function(data){
                        alert("Your information has been updated");

                    }).fail(function(data){
                        $("#greeting").text("You might want to try it again");
                    })
                }

                if (num == 4){
                    var count_data = data.content.answer04count;

                    alert(count_data);
                    var l = count_data+1;

                    alert(l);
                    jQuery.ajax ({
                        url: "../api/savy/"+id,
                        type: "PATCH",
                        data: JSON.stringify({answer04count:l}),
                        dataType: "json",
                        contentType: "application/json; charset=utf-8",

                    }).done(function(data){
                        alert("Your information has been updated");

                    }).fail(function(data){
                        $("#greeting").text("You might want to try it again");
                    })
                }


                return count_data;
            })
            .fail(function(data){
                console.log("here");
                $("#questionList").text("Sorry no Questions");
            })

    }


});


