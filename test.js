/**
 * Created by KIMSEONHO on 14. 1. 15.
 */
/*
 function add(x, y) {
 console.dir(arguments.callee);
 return x + y;
 }

 add.result = add(3, 2);
 add.status = 'OK';

 console.log(add.result);
 console.log(add.status);

 function myFunction() {
 return true;
 }

 function parent() {
 var a = 100;

 var child = function() {
 console.log(a);
 a++;
 }

 return child;
 }

 var inner = parent();
 inner();

 function Person(name) {
 this.name = name;
 }

 Function.prototype.method = function(name, func) {
 if(!this.prototype[name]) {
 this.prototype[name] = func;
 }
 }

 Person.method("setName", function(value) {
 this.name = name;
 })

 Person.method("getName", function() {
 return this.name;
 })

 var me = new Person("me");
 var you = new Person("you");

 console.log(me.getName());
 console.log(you.getName());

 var person = {
 name : "zzoon",
 getName : function() {
 return this.name;
 },
 setName : function(arg) {
 this.name = arg;
 }
 }

 function create_object(o) {
 function F() {};
 F.prototype = o;
 return new F();
 }

 function extend(obj, prop) {
 if (!prop) {
 prop = obj; obj = this;
 }
 for (var i in prop) {
 obj[i] = prop[i];
 }
 }

 var student = create_object(person);

 var added = {
 setAge : function(age) {
 this.age = age;
 },
 getAge : function() {
 return this.age;
 }
 }

 student.setName("me");

 console.log(student.getName());

 function Student(arg) {

 }

 function F() {};
 F.prototype = Person.prototype;
 Student.prototype = new F();
 Student.prototype.constructor = Student();
 Student.super = Person.prototype;

 var me = new Student();
 me.setName("zzoon");

 */


function whatsThis() {
    return this.toString();
}

var unikys = {
    what: whatsThis,
    toString: function () {
        return "[object unikys]";
    }
};

whatsThis();
unikys.what();
whatsThis.call();
whatsThis.apply(unikys);
unikys.what.call(undefined);
unikys.what.apply(unikys);

/*var collection;
 collection = new Backbone.Collection();
 collection.add({'name' : '김선호'});
 collection.add({'name' : '김증'});
 collection.add({'name' : 'asdf'});
 collection.add({'name' : 'fdsa'});
 collection.add({'name' : '1234'});*/
$(function () {
    var Shape = Backbone.Model.extend({
        defaults: { x: 50, y: 50, width: 150, height: 150, color: 'black' },
        setTopLeft: function (x, y) {
            this.set({ x: x, y: y });
        },
        setDim: function (w, h) {
            this.set({ width: w, height: h });
        }
    });

    var ShapeView = Backbone.View.extend({
        initialize: function () {
            $('#page').mousemove(this, this.mousemove).mouseup(this, this.mouseup);
            this.model.bind('change', this.updateView, this);
        },
        render: function () {
            $('#page').append(this.el);
            $(this.el).html('<div class="shape"/>'
                    + '<div class="control delete hide"/>'
                    + '<div class="control change-color hide"/>'
                    + '<div class="control resize hide"/>')
                .css({ position: 'absolute', padding: '10px' });
            this.updateView();
            return this;
        },
        updateView: function () {
            $(this.el).css({
                left: this.model.get('x'),
                top: this.model.get('y'),
                width: this.model.get('width') - 10,
                height: this.model.get('height') - 10 });
            this.$('.shape').css({ background: this.model.get('color') });
        },
        events: {
            'mouseenter .shape': 'hoveringStart',
            'mouseleave': 'hoveringEnd',
            'mousedown .shape': 'draggingStart',
            'mousedown .resize': 'resizingStart',
            'mousedown .change-color': 'changeColor',
            'mousedown .delete': 'deleting'
        },
        hoveringStart: function () {
            this.$('.control').removeClass('hide');
        },
        hoveringEnd: function () {
            this.$('.control').addClass('hide');
        },
        draggingStart: function (e) {
            this.dragging = true;
            this.initialX = e.pageX - this.model.get('x');
            this.initialY = e.pageY - this.model.get('y');
            return false; // prevents text selection
        },
        resizingStart: function () {
            this.resizing = true;
            return false;
        },
        changeColor: function () {
            this.model.set({ color: prompt('Enter color value', this.model.get('color')) });
        },
        deleting: function () {
            this.model.collection.remove(this.model);
        },
        mouseup: function (e) {
            if (!e.data) return;
            var self = e.data;
            self.dragging = self.resizing = false;
        },
        mousemove: function (e) {
            if (!e.data) return;
            var self = e.data;
            if (self.dragging) {
                self.model.setTopLeft(e.pageX - self.initialX, e.pageY - self.initialY);
            } else if (self.resizing) {
                self.model.setDim(e.pageX - self.model.get('x'), e.pageY - self.model.get('y'));
            }
        }
    });

    var Document = Backbone.Collection.extend({ model: Shape });

    var DocumentView = Backbone.View.extend({
        id: 'page',
        views: {},
        initialize: function () {
            this.collection.bind('add', this.added, this);
            this.collection.bind('remove', this.removed, this);
        },
        render: function () {
            return this;
        },
        added: function (m) {
            this.views[m.cid] = new ShapeView({
                model: m,
                id: 'view_' + m.cid
            }).render();
        },
        removed: function (m) {
            this.views[m.cid].remove();
            delete this.views[m.cid];
        }
    });

    var document = new Document();
    var documentView = new DocumentView({ collection: document });
    documentView.render();

    $('#new-shape').click(function () {
        document.add(new Shape());
    });
});

/*nmapp = nmapp || {};

 $(function () {
 //  Start from inside. submodel --> submodel collection --> album model --> album collection

 nmapp.Subalbum = Backbone.Model.extend({
 initialize: function () {
 this.subId = this.get('id');
 this.subTitle = this.get('title');
 this.subImg = this.get('image');
 this.subCanvas = this.get('canvas');
 this.subSize = this.get('size');
 }
 });

 nmapp.Subalbums = Backbone.Collection.extend({ model: nmapp.Subalbum });

 nmapp.Album = Backbone.Model.extend({
 initialize: function () {
 *//* next line is important : this is how you nest your models/collections.
 "Subalbum Collection" is inside the Album Model. So we have to instantiate the "Subalbum Collection" inside the "Album" Model
 *//*
 this.subs = new nmapp.Subalbums(this.get('subalbum'));
 this.subs.parent = this;
 this.albumId = this.get('id');
 this.albumTitle = this.get('title');
 this.albumImg = this.get('image');
 }
 });
 // get the data from server on "Album Collection"
 nmapp.Albums = Backbone.Collection.extend({
 model : nmapp.Album,
 url : 'data/data.json',
 parse : function(data){
 return data;
 }
 });

 var albums = new nmapp.Albums();
 albums.fetch();
 console.log(albums);
 // you should be able to see the collection of models nested properly inside it
 nmapp.Albums = Backbone.Collection.extend({
 model: nmapp.Album,
 url: 'data/data.json',
 parse: function (response) {
 *//* We know we have "subalbum" collection in each "Album" Model
 collection.reset([models], [options]) --> Triggering 'reset' event on a Collection will replace the entire collection with a new set of models.
 Bulk Replacement.
 *//*
 this.subalbums.reset(response.subalbum);
 delete response.subalbum;    // this will delete the "subalbum" collection from our "Album" models
 return response;
 }
 });
 });*/

$(function () {
    window.Action = Backbone.Model.extend({
        initialize: function () {
            this.fromNodeId = this.get('FromNodeId');
            this.toNodeId = this.get('ToNodeId');
            this.name = this.get('Name');
        },
        nextNode: function (nodes) {
            return nodes.detect(function (node) {
                return node.id == this.toNodeId
            }, this);
        },
        previousNode: function (nodes) {
            return nodes.detect(function (node) {
                return node.id == this.fromNodeId
            }, this);
        }
    });
    window.Actions = Backbone.Collection.extend({ model: Action });
    window.Asset = Backbone.Model.extend({
        initialize: function () {
            this.assetBinaryId = this.get('AssetBinaryId');
            this.data = this.get('Data');
            this.id = this.get('Id');
            this.name = this.get('Name');
        }
    });
    window.Assets = Backbone.Collection.extend({ model: Asset });
    window.Node = Backbone.Model.extend({
        initialize: function () {
            this.assets = new Assets(this.get('Assets'));
            this.actions = new Actions(this.get('Actions'));
        }
    });
    window.Nodes = Backbone.Collection.extend({ model: Node });
    window.Package = Backbone.Model.extend({
        initialize: function () {
            // the following line forces 'this' to refer to the Package instance in the
            // function `fetch_success`
            _.bindAll(this, 'fetch_success');
            this.bind('change', this.fetch_success);
        },
        // specifying the URL as a function gives us a bit more flexibility
        url: function () {
            return "data/package/" + this.id + ".json"
        },
        // invoked automatically when the change event is invoked which happens when fetch is successful
        fetch_success: function () {
            this.nodes = new Nodes(this.get('Nodes'));
            this.createTime = this.get('CreateTime');
            this.modifyTime = this.get('ModifyTime');
            this.name = this.get('Name');
        }
    });
});

/* test nmapp.Object */
/*var testObject = {
 type: "icon",
 x: 100,
 y: 200,
 width: 100,
 height: 200,
 aniName: "aniNAme1",
 aniParam: [1, 2],
 zIndex: 100,
 selected: false
 };
 var myObject;
 *//* test nmapp.custom-Structure *//*
 $(function() {
 myObject = new nmapp.Object(testObject);
 })*/


