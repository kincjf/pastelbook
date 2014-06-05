/* Backbone Model, Collection */

// Icon Model
/*var Icon = Backbone.Model.extend({
 defaults: {
 type: '',
 imgSrc: '',
 name: '',
 theme: ''
 }
 })

 //Collection of Icon
 var iconCollection = Backbone.Collection.extend({
 model: Icon
 });*/

/* functional method */

// variable
var selectedIcon = null;

var tmpProjectContainer = null;

var clearIconList = function () {
    $('#icon_view > *').remove();
}

var clearSelectedIcon = function () {
    selectedIcon = null;
}

/* Button Template */
var deleteBtn = function(classType) {
    return $("<a></a>")
        .attr("href", "#")
        .attr("title", "Delete")
        .addClass("ui-btn ui-corner-all " +
            "ui-icon-delete ui-btn-icon-notext "
            + classType
        )
        .css("display", "inline-block")
        .html("Delete");
}

var setAniBtn = function(classType) {
    return $("<a></a>")
        .attr("href", "#")
        .attr("title", "Set Animation")
        .addClass("ui-btn ui-corner-all " +
            "ui-icon-star ui-btn-icon-notext "
            + classType
        )
        .css("display", "inline-block")
        .html("Set Animation");
}

var setBackgroundBtn = function(classType) {
    return $("<a></a>")
        .attr("href", "#")
        .attr("title", "Set Background")
        .addClass("ui-btn ui-corner-all " +
            "ui-icon-gear ui-btn-icon-notext "
            + classType
        )
        .css("display", "inline-block")
        .html("Set Background");
}

var objImg = function(imgSrc) {
    return $("<img />")
        .attr("src", imgSrc)
        .css({
            "width": "inherit",
            "height": "inherit"
        })
}

// icon 삽입
var insertIcon = function (view, x, y) {
    if (selectedIcon) {

        var imgSrc = $(selectedIcon).children().attr("imgSrc");
        var top = y + "px";
        var left = x + "px";

        var deleteBtn = $("<a></a>")
            .attr("href", "#")
            .addClass("ui-btn ui-corner-all " +
                "ui-icon-delete ui-btn-icon-notext"
            )
            .html("Delete")
            .click(function (){
                $(obj).remove();
            });

        var img = $("<img />")
            .attr("src", imgSrc)
            .addClass("selected-obj")
            .css({
                "width": "inherit",
                "height": "inherit"
            })
            .click(function () {
                $(this).toggleClass("selected-obj");
                $(deleteBtn).toggle();
            });

        var obj = $("<div></div>")
            .append($(img), $(deleteBtn))
            /*.addClass("ui-object")*/
            .css({
                "top": top,
                "left": left
            })
            .appendTo(view)
            .draggable()
            .resizable();

        /* <a href="#" class="ui-btn ui-corner-all ui-icon-delete ui-btn-icon-notext">Icon only</a>
         .bind("click", function () {
         if (selectedIcon) {
         $(selectedIcon).removeClass("selected-icon");
         // 선택 삭제
         }
         selectedIcon = this;
         // 선택 아이콘 등록
         $(this).addClass("selected-icon");
         // 선택 표시
         });*/
        // 선택, 개체 등록을 위해서 나중에 bind 해준다
        selectedIcon = null;
        // 선택했으니 또 누르면 이벤트 발생을 중지시키기 위해서
    }
}

// icon 추가
var addIconList = function (icon, index) {
    var uiClass = ((index % 2) == 0) ? "ui-block-a" : "ui-block-b";

    $("<div></div>")
        .addClass(uiClass)
        .append(
            $("<img />")
                .attr({
                    "src": icon.iconSrc,
                    "imgSrc": icon.imgSrc,
                    "alt": icon.showName
                })
                .css("width", "97%")
        )
        .appendTo('#icon_view')
        .bind("click", function () {
            if (selectedIcon) {
                $(selectedIcon).removeClass("selected-icon");
                // 선택 삭제
            }
            selectedIcon = this;
            // 선택 아이콘 등록
            $(this).addClass("selected-icon");
            // 선택 표시
        });

};

// 아이콘(.json)파일 load
var getIconList = function (selector) {
    var role = selector.attr("role");

    clearIconList();

    $.ajax({
        url: "json/icon_list.json",
        type: "get",
        dataType: "json",
        success: function (data) {
            $(data).each(function (index, Element) {
                if (Element.type == role) {
                    addIconList(Element, index);
                    // role에 해당하는 data만 집어넣음
                } else if (role == "obj-all") {
                    addIconList(Element, index);
                    // role == "obj-all": 모든 data를 집어넣음
                }
            });


        },
        error: function (xhr, textStatus, errorThrown) {
            console.log("getIconList error - " + textStatus
                + " " + xhr.status + " " + errorThrown);
        }
    });
};

// file API
var fileReadedEvent = function(event) {
    // 일단 저장만 해놓자, 필요할 때 꺼내쓰게
    // 이상한 포멧이 들어와도 죽지 않게 예외처리를 하자
    try {
        tmpProjectContainer = JSON.parse(event.data.readerResult);
    } catch (e) {
        console.log("non proper formattion file");
        console.log(e.name + "  " + e.message);
    }

    $.mobile.sdCurrentDialog.close();
};

var fileRead = function() {
    var fileInput = document.querySelector('#file_input');
    var $fileDisplayArea = $(fileInput).prev();

    fileInput.addEventListener('change', function(e) {
        var file = fileInput.files[0];
        var textType = /[text].*|/;

        if (file.type.match(textType)) {
            var reader = new FileReader();

            reader.onload = function(e) {
                $fileDisplayArea.html(file.name);
                console.log(reader.result);
                // 여기에 로딩하는부분을 추가한다.
                // 일단 localStorage에 추가하겠다.
                if(file) {
                    $("#file_input_ok").bind("click", {
                        readerResult: reader.result
                    }, fileReadedEvent);
                    // file이 존재하면 OK 버튼이 됨
                } else {
                    $("#file_input_ok").unbind(fileReadedEvent);
                }

            }

            reader.readAsText(file);
        } else {
            $fileDisplayArea.html("File not supported!");
        }
    });
};

/*var setAnimationEvent = function(event) {
    var self = event.data.self;
    var tmpAniParam = {
        toX: $("#animation_toX").val(),
        toY: $("#animation_toY").val(),
        easingX: $("#animation_easingX").val(),
        easingY: $("#animation_easingY").val(),
        duration: $("#animation_duration").val()
    }

    self.model.set({aniName: $("#animation_name").val()})
    self.model.set({aniParam: tmpAniParam});

    event.data.this.off("click", setAnimationEvent);

    $('animation_list_panel').panel('close');
}*/

/* project management - UI */
var bindEvents = function () {
    $('#tool_obj').bind("click", function () {
        $('#obj_list_panel').panel('open');
        getIconList($(this));
    });

    $('.obj-list > li > a').bind("click", function () {
        $('#obj_list_panel').panel('open');
        getIconList($(this));
    });

    $('#delete_selected_icon').bind("click", function () {
        selectedIcon = null;
        // icon 선택 해제
    });

    $('#add_slide').bind("click", function() {
        slideContainer.add(new nmapp.SlideManager());
    });
    /*$('#icon_view > .ui-block-a, .ui-block-b').bind("click", function() {
     insertObject($this));
     })*/
    // 파일을 불러온다
    $(document).delegate("#import_project", "click", function() {
        $("#file_import_dialog").simpledialog2();
        fileRead();
    });
    // 가장 최근에 저장한 Project를 파일로 저장한다.
    $(document).delegate("#export_project", "click", function() {
        $("#file_export_dialog").simpledialog2();
        $("#create_file").click(downloadFile);
    });

    // 프로젝트 저장 - localStorage와 tmpProjectContainer에 저장
    $(document).delegate("#save_project", "click", function() {
        $('<div>').simpledialog2({
            mode: 'button',
            headerText: '저장할 이름을 적어주세요',
            headerClose: true,
            buttonPrompt: '브라우저에 임시저장됩니다.',
            buttonInput: true,
            buttons : {
                'OK': {
                    click: function () {
                        var key = $.mobile.sdLastInput;
                        var value = JSON.stringify(slideContainer);
                        // 최상단 Collection을 String으로 저장한다.

                        localStorage.setItem(key, value);
                        // 임시저장 - 가장 마지막에 저장된 걸 저장하기 위해서

                        tmpProjectContainer = slideContainer.clone();
                        // tmp project value를 저장 - object와 string으로 저장
                        $("#saved_project_content").html(value);
                    }
                }
            }
        })
    });

}