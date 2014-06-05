/**
 * Created by KIMSEONHO on 14. 2. 5.
 */
var nmapp = nmapp || {};

// inheritance - To Use parent's method, use _super("methodName");
Backbone.Model.prototype._super = function (method) {
    return this.constructor.__super__[method].apply(this, _.rest(arguments));
};

$(function () {
    nmapp.AniParam = Backbone.Model.extend({
        defaults: {
            toX: "",
            toY: "",
            easingX: "",
            easingY: "",
            duration: ""
        },
        initialize: function(options) {
            this.toX = this.get("toX");
            this.toY = this.get("toY");
            this.easingX = this.get("easingX");
            this.easingY = this.get("easingY");
            this.duration = this.get("duration");
        },
        setAniParam: function (_toX, _toY, _easingX, _easingY, _duration) {
            this.set({
                toX: _toX,
                toY: _toY,
                easingX: _easingX,
                easingY: _easingY,
                duration: _duration
            });
        }
    });

    nmapp.Group = Backbone.Model.extend({
        defaults: {
            aniName: null,
            aniParam: null,
            selected: false,
            objectContainer: null,
            opacity: 1
        },
        initialize: function () {
            this.x = this.get("x");
            this.y = this.get("y");
            this.width = this.get("width");
            this.height = this.get("height");
            this.aniName = this.get("aniName");

            if (!this.get("aniParam")) {
                // [{x, y}, {easing_left, easing_right}, duration]
                this.set({aniParam: new nmapp.AniParam(this.get("aniParam"))});
                // 일단 하나만 넣고 나중에 추가하기
            };

            if (!this.get("objectContainer")) {
                this.set({objectContainer: new nmapp.ObjectContainer(this.get("objectContainer"))});
                //this.objectContainer.parent = this;
            };

            this.selected = this.get("selected");
            this.background = this.get("background");
            this.opacity = this.get("opacity");
        },
        setTopLeft: function (x, y) {
            this.set({ x: x, y: y});
        },
        setDim: function (w, h) {
            this.set({ width: w, height: h});
        },
        setAniParam: function (m) {
            this.set({aniParam: m});
        }
    });

    nmapp.GroupContainer = Backbone.Collection.extend({
        model: nmapp.Group
    });

    // group를 상속받는다
    nmapp.Object = nmapp.Group.extend({
        defaults: {
            aniName: null,
            aniParam: null,
            selected: false
        },
        initialize: function () {
            this.type = this.get("type");

            if (!this.get("aniParam")) {
                this.set({aniParam: new Backbone.Collection(this.get("aniParam"))});
                // 일단 하나만 넣고 나중에 추가하기
            }
            ;
            this.bgBorder = this.get("bgBorder");
            this.zIndex = this.get("zIndex");
            this.imgSrc = this.get("imgSrc");
            this.content = this.get("content");
        }
    });

    nmapp.ObjectContainer = Backbone.Collection.extend({
        model: nmapp.Object
    });

    nmapp.SlideManager = Backbone.Model.extend({
        defaults: {
            groupContainer: null,
            background: "white",
            opacity: 1
        },
        initialize: function () {
            this.slideNum = this.get("slideNum");
            this.slideTitle = this.get("slideTitle");
            this.slideContent = this.get("slideContent");

            if (!this.get("groupContainer")) {
                this.set({groupContainer: new nmapp.GroupContainer(this.get("groupContainer"))});
                //this.groupContainer.parent = this;
            };

            this.background = this.get("background");
            this.opacity = this.get("opacity");
            this.bgBorder = this.get("bgBorder");
        }
    });

    nmapp.SlideContainer = Backbone.Collection.extend({
        model: nmapp.SlideManager

    });

    nmapp.ProjectManager = Backbone.Model.extend({
        defaults: {
            slideContainer: null,
            authors: null
        },
        initialize: function () {
            this.name = this.get("name");
            this.version = this.get("version");

            if (!this.get("authors")) {
                this.set({authors: new Backbone.Collection(this.get("authors"))})
            }

            this.description = this.get("description");
            this.createDate = this.get("createDate");
            this.updateDate = this.get("updateDate");

            if (!this.get("slideContainer")) {
                this.set({slideContainer: new nmapp.SlideContainer(this.get("slideContainer"))})
            }
        }
    });

    nmapp.DocumentView = Backbone.View.extend({
        // slide를 관리하는 최상단 View
        id: "superContainer",
        el: "#superContainer",
        slideViews: {},
        initialize: function () {
            //slideContainer
            this.collection.bind("add", this.added, this);
            this.collection.bind("remove", this.removed, this);
        },
        render: function () {
            return this;
        },
        added: function (m) {
            // m: slideManager
            this.slideViews[m.cid] = new nmapp.SlideView({
                model: m,
                id: "slide_" + m.cid,
                collection: m.get("groupContainer"),
                parent: this
            }).render();
        },
        removed: function (m) {
            this.slideViews[m.cid].remove();
            delete this.slideViews[m.cid];
        }
    });
    //////////////////////////////////////////////////////////

    nmapp.SlideView = Backbone.View.extend({
        // 각 Slide를 구성하는 View
        // id는 slide_ + m.cid로 생성시 attr로 추가한다.
        groupViews: {},
        initialize: function (options) {
            // GroupContainer
            this.collection.bind("add", this.addGroupView, this);
            this.collection.bind("remove", this.removeGroupView, this);
            //SlideManager
            this.model.bind('change', this.updateSlideView, this);
            //load fullpage.js to use autoScrollView
            //$.fn.fullpage(); - 문제가 있으므로 나중에 하자
            this.parent = options.parent;

        },
        tagName: 'div',
        className: 'section table slide underline',
        events: {
            "click div[data-role='content']": "insertIcon",
            "click .delete-slide-btn": "deleteSlide",
            // change background property
            "click .change-background-btn": "changeBackground",
            "click .change-bgborder-btn": "changebgBorder"
            // mouse 클릭시 아이콘 개체 삽입
        },
        attrbutes: {
        },
        slideTemplate: _.template($("#slide_template").html()),
        navigatorTemplate: _.template($("#navigator_template").html()),
        render: function () {
            this.parent.$el.append(
                this.$el.append(
                    this.slideTemplate(this.model.toJSON()))
            );

            /*$("#superContainer").append(
                this.$el.append(
                    this.slideTemplate(this.model.toJSON()))
            );*/

            this.addedNavigator();
            // dom option이 아닌 것은 attributes로 등록되지 않는다
            // 억지로 해주었음
            // 초기변수시 적용하기 위해서 updateSlideView 실행
            this.updateSlideView();

            return this;
        },
        addedNavigator: function () {
            var target = this.$el;
            var navIdx = ".footer > a[href=\'" + "#" + this.id + "\']";

            $(".footer").append(
                this.navigatorTemplate({
                    nav_id: this.el.id,
                    length: this.model.collection.length
                })
            );

            var navigator = $(navIdx);

            navigator.bind("click", function () {
                $("div[data-role='page']").stop().scrollTo(target, 800);
            });

        },
        updateSlideView: function () {
            $(this.el).css({
                background: this.model.get("background"),
                border: this.model.get("bgBorder")
            });
        },
        deleteSlide: function (e) {
            // 슬라이드 삭제시
            this.parent.collection.remove(this.model);

            var navIdx = ".footer > a[href=\'" + "#" + this.$el.attr("id") + "\']";
            $(navIdx).remove();

            $(".footer > a").each(function (index, element) {
                $(element).text(index + 1);
            });
            //self.model.collection.remove(self.model);
        },      //GroupView Container 역할
        addGroupView: function (m) {
            // adding groupView
            this.groupViews[m.cid] = new nmapp.GroupView({
                model: m,
                id: this.el.id + "_group_" + m.cid,
                collection: m.get("objectContainer"),
                parent: this
            }).render();
        },
        removeGroupView: function (m) {
            // deleting groupView
            this.groupViews[m.cid].remove();
            delete this.groupViews[m.cid];
        },
        changeBackground: function (e) {
            this.model.set({background: prompt('Enter Background value[css Grammer]', this.model.get('background')) });
        },
        changebgBorder: function (e) {
            this.model.set({bgBorder: prompt('Enter Border value[css Grammer]', this.model.get('bgBorder')) });
},
        insertIcon: function (e) {
            // icon insert 관련 method
            // 선택된 아이콘이 있으면
            if (selectedIcon) {
                var content = this.$el.find("div[data-role='content']");
                var offset = content.offset();

                this.model.get("groupContainer").add(new nmapp.Group({
                    x: e.clientX - offset.left,
                    y: e.clientY - offset.top
                }));
                //2번 삽입되지 않게 하기 위해서
                selectedIcon = null;

            }
        }
    });

    ///////////////////////////////////////////////////////////

    nmapp.GroupView = Backbone.View.extend({
        // Object 집합을 담당하는 GroupView
        // id는 $(slide.id)_group_ + m.cid로 생성시 attr로 추가한다.
        tagName: 'div',
        className: 'ui-draggable ui-resizable',
        objectViews: {},
        initialize: function (options) {
            // ObjectContainer : model.get("objectContainer")
            this.collection.bind("add", this.addObjectView, this);
            this.collection.bind("remove", this.removeObjectView, this);
            // Group
            this.model.bind('change', this.updateGroupView, this);
            /*this.model.bind('change:aniParam', this.updateGroupAnimation, this);*/
            this.model.bind('change:opacity', this.updateGroupOpacity, this);

            this.parent = options.parent;

            /*this.$el.bind("resizestop", this.resizeGroup);
            this.$el.bind("dragstop", this.dragGroup);*/
        },
        events: {
            /*"mousedown .ui-draggable-dragging": "dragGroup",
             "resizestup .ui-resizable-resizing": "resizeGroup",*/

            "click .delete-group": "deleteGroup",
            // change background property
            "click .set-animation-group": "setAnimation",
            "click .change-background-group": "changeBackground",
            "resizestop": "resizeGroup",
            "dragstop": "dragGroup"

        },
        addObjectView: function (m) {
            this.objectViews[m.cid] = new nmapp.ObjectView({
                model: m,
                id: this.el.id + "_object_" + m.cid,
                collection: m.get("objectContainer"),
                parent: this
            }).render();
        },
        removeObjectView: function (m) {
            this.objectViews[m.cid].remove();
            delete this.objectViews[m.cid];

            if (this.collection.length == 0) {
                this.deleteGroup();
            }
        },
        attrbutes: {
        },
        render: function () {
            var div = $("<div />");
            div.append(
                deleteBtn("delete-group")
                , setAniBtn("set-animation-group")
                , setBackgroundBtn("change-background-group")
            ).addClass("hide");
            // option button - 처음에는 숨어있다가 클릭하면 나와야됨

            this.parent.$el.find("div[data-role='content']").append(
                /* this.objectViews[this.model.cid].$el, */
                // 앞에 박을것인가 뒤에박을것인가, 그런데 여기서 관여할 일은 아닌 것 같다.
                this.$el.draggable()
                    .resizable({
                        alsoResize: ".ui-object"
                        // 안에 들어온 애들은 같이 줄어든다
                    }).click(function () {
                        $(this).toggleClass("selected-obj");
                        div.toggleClass("hide");
                    }).append(div).addClass("inline-block")
                    .css("position", "absolute")
            );

            var imgSrc = $(selectedIcon).children().attr("imgSrc");

            this.model.get("objectContainer").add(new nmapp.Object({
                imgSrc: imgSrc
            }));

            // 초기변수시 적용하기 위해서 updateGroupView 실행
            this.updateGroupView();

            return this;
        },
        dragGroup: function (event, ui) {
            this.model.setTopLeft(ui.position.left, ui.position.top);

            /*this.model.get("objectContainer").each(function (model) {
                var offset_x = ui.position.left - ui.originalPosition.left;
                var offset_y = ui.position.top - ui.originalPosition.top;
                // 현재 위치 - 이전 위치

                model.setTopLeft(
                    model.get("x") + offset_x, model.get("y") + offset_y);
            })*/
        },
        resizeGroup: function (event, ui) {
            this.model.setDim(ui.size.width, ui.size.height);

            /*this.model.get("objectContainer").each(function (model) {
                var offset_width = ui.size.width - ui.originalSize.width;
                var offset_height = ui.size.height - ui.originalSize.height;
                // 현재 크기 - 이전 크기
                var ab_width = (model.get("width") + offset_width > 0)
                    ? model.get("width") + offset_width : 0;
                var ab_height = (model.get("height") + offset_height > 0)
                    ? model.get("width") + offset_width : 0;
                // 최소 크기는 0이다

                model.setDim(ab_width, ab_height);
            })*/
        },
        updateGroupView: function () {
            $(this.el).css({
                left: this.model.get("x"),
                top: this.model.get("y"),
                width: this.model.get('width'),
                height: this.model.get('height')
            });

            $(this.el).css({
                background: this.model.get("background")
            });


        },
        deleteGroup: function () {
            // Group 삭제시
            // model.collection : groupContainer
            this.model.collection.remove(this.model);
        },
        setAnimation: function (e) {
            var self = this;
            // 일단 애니는 하나만 추가할 수 있다.
            if(self.model.get("aniName")) {
                self.showAnimation();
            }

            $('#animation_list_panel').panel('open')
                .find("#add_animation").bind("click", {
                    self: self,
                    this: this
                }, this.updateGroupAnimation);

            /*this.model.set({
                aniName: prompt('Enter Animation value', this.model.get('aniName'))
            });*/
        },
        changeBackground: function (e) {
            this.model.set({
                background: prompt('Enter Background value[css Grammer]'
                    , this.model.get('background'))
            });
        },
        updateGroupAnimation: function (event) {
            // callback method
            var self = event.data.self;
            var tmpAniParam = {
                toX: $("#animation_toX").val(),
                toY: $("#animation_toY").val(),
                easingX: $("#animation_easingX").val(),
                easingY: $("#animation_easingY").val(),
                duration: $("#animation_duration").val()
            };

            self.model.set({aniName: $("#animation_name").val()});
            self.model.set({aniParam: tmpAniParam});

            console.log("setAnimation : " + self.model.get("aniName"));

            event.data.this.off("click", arguments.callee);

            $('#animation_list_panel').panel('close');

            /*this.$el.click( function() {
                Animation[self.model.get("aniName")].call(self.$el, self.el.id);
            });*/
        },
        updateGroupOpacity: function () {
            // 나중에 추가할 예정, 아직 버튼이 없다
            this.$el.css("opacity", this.model.get("opacity"));
        },
        showAnimation: function() {
            var self = this;
            var aniParam = self.model.get("aniParam");
            var model = self.model;
            // 꼭 기억할 것 - 값을 가져올때는 .get()으로 가져와야 한다.
            this.$el.animate({
                left: aniParam.toX,
                top: aniParam.toY
            }, {
                duration: aniParam.duration,
                specialEasing: {
                    left: aniParam.easingX,
                    height: aniParam.easingY
                },
                complete: function() {
                    console.log("animation :" + model.get("aniName")
                        + " - " + self.el.id);
                }
            }).delay(800)// 1초정도 딜레이를 주자.
                .animate({
                    left: model.get("x"),
                    top: model.get("y")
                }, {
                    duration: aniParam.duration,
                    specialEasing: {
                        left: aniParam.easingX,
                        height: aniParam.easingY
                    },
                    complete: function() {
                        console.log("return animation :" + model.get("aniName")
                            + " - " + self.el.id);
                    }
                });

            // 왕복 애니메이션
        }

    });

    nmapp.ObjectView = Backbone.View.extend({
        initialize: function (options) {
            // Object
            this.model.bind('change:aniName', this.updateObjectAnimation, this);
            this.model.bind('change:opacity', this.updateObjectOpacity, this);

            this.parent = options.parent;

            this.model.bind('change', this.updateObjectView, this);
        },
        tagName: 'div',
        className: 'ui-draggable ui-resizable',
        events: {
            "click .delete-object": "deleteObject",
            // change background property
            "click .set-animation-object": "setAnimation",
            "click .change-background-object": "changeBackground"
            // "resizestop": "resizeObject",
            // "dragstop": "dragObject"

        },
        attrbutes: {
        },
        /*objectTemplate: _.template($("#object_template").html()),*/
        render: function () {
            // group안에 1개만 있는걸 기본으로 하자..

           /* var div = $("<div />");
            div.addClass("inline-block").toggle()
               .append(
                    deleteBtn("delete-object")
                    ,setAniBtn("set-animation-object")
                    ,setBackgroundBtn("change-background-object")
                );*/

            var img = objImg(this.model.get("imgSrc"));
            /*img.click(function () {
                $(this).toggleClass("selected-obj");
                //div.toggle();
            });*/

            this.parent.$el.append(
                /* this.objectViews[this.model.cid].$el, */
                // 앞에 박을것인가 뒤에박을것인가, 그런데 여기서 관여할 일은 아닌 것 같다.
                this.$el.append(img).addClass("inline-block inherit-dim")
                    // .draggable()
                    // .resizable()
                    // 나중에 group 문제가 해결되면 추가

            );
            // 초기변수시 적용하기 위해서 updateObjectView 실행
            this.updateObjectView();
            // dom option이 아닌 것은 attributes로 등록되지 않는다
            // 억지로 해주었음
            return this;
        },
        updateObjectView: function () {
            // model 값 change event callback method
            // 값 변경시
            this.$el.css({
                left: this.model.get("x"),
                top: this.model.get("y"),
                width: this.model.get('width'),
                height: this.model.get('height')
            });

            this.$el.css({
                background: this.model.get("background"),
                border: this.model.get("bgBorder")
            });
        },
        /////////////////////////////////////////////////////
        // dragObject: function (event, ui) {
            // x, y 조정
            // this.model.setTopLeft(ui.offset.left, ui.offset.top);
        //},
        // resizeObject: function (event, ui) {
            // dim 조정
            // this.model.setDim(ui.size.width, ui.size.height);
        // },
        deleteObject: function () {
            // Object 삭제시
            // model.collection : objectContainer
            this.model.collection.remove(this.model);
        },
        setAnimation: function (e) {
            // 일단 애니는 하나만 추가할 수 있다.
            this.model.set({
                aniName: prompt('Enter Animation value', this.model.get("aniName"))
            });
        },
        changeBackground: function (e) {
            // 변경된 background값을 model에 전달함
            this.model.set({
                background: prompt('Enter Background value[css Grammer]'
                    , this.model.get('background'))
            });
        },
        updateObjectAnimation: function () {
            // animation 추가에 따른 출력
            console.log("animation" + this.model.get("aniName"));
        },
        updateObjectOpacity: function () {
            // 나중에 추가할 예정, 아직 버튼이 없다
            this.$el.css("opacity", this.model.get("opacity"));
        }
    });
});



/* test code - will be deleted*/


