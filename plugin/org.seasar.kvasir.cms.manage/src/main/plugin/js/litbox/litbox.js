LITBox = Class.create();
LITBox.prototype = {
   initialize: function(mes){
   this.mes = mes;
   this.options = Object.extend({
      width: 600,
      height: 500,
      type: 'window',
      func: null,
      draggable: true,
      resizable: true,
      overlay: true,
      opacity: 1,
      left: false,
      top: false
   }, arguments[1] || {});
   this.setup();
   },
   setup: function(){
      this.rn = ( Math.floor ( Math.random ( ) * 100000000 + 1 ) );
      this.getWindow();
      switch(this.options.type){
         case 'window' : this.d4.innerHTML = this.getAjax(this.mes);
            break;
         case 'alert' : this.d4.innerHTML = this.mes;
            break;
         case 'confirm' : this.d4.innerHTML = '<p>' + this.mes + '</p>';
            this.button_y = document.createElement('input');
            this.button_y.type='button';
            this.button_y.value='Yes';
            this.d4.appendChild(this.button_y);
            this.button_y.d= this.d; this.button_y.d2 = this.d2;
            this.button_y.temp = this.options.func;
            this.button_y.onclick=this.remove;
            this.button_n = document.createElement('input');
            this.button_n.type='button';
            this.button_n.value='No';
            this.d4.appendChild(this.button_n);
            this.button_n.d= this.d; this.button_n.d2 = this.d2;
            this.button_n.onclick=this.remove;
      }
      this.display();
   },
   getWindow: function(){
      this.over = null;
      if(this.options.overlay == true){
      this.d = document.createElement('div');
      document.body.appendChild(this.d);
      this.d.className = 'LB_overlay';
      this.d.style.display = 'block';
      this.d.onclick=this.remove;
      }
      this.d2 = document.createElement('div');
      document.body.appendChild(this.d2);
      this.d2.className = 'LB_window';
      this.d2.style.height = parseInt(this.options.height) + 'px';

      this.d3 = document.createElement('div');
      this.d2.appendChild(this.d3);
      this.d3.className='LB_closeAjaxWindow';
      this.d3.d2 = this.d2;
      this.d3.over = this.over;
      this.d3.options = this.options;
      this.d3.onmouseover=this.getDraggable;
      this.d3.onmouseout=this.dropDraggable;
      this.close = document.createElement('a');
      this.d3.appendChild(this.close);
      this.close.d = this.d;
      this.close.d2 = this.d2;
      this.close.onclick=this.remove;
      this.close.href='#';
      this.close.innerHTML='Close';
      this.d4 = document.createElement('div');
      this.d4.className='LB_content';
      this.d4.style.height = parseInt(this.options.height) - 30 + 'px';
      this.d4.style.width = parseInt(this.options.width) + 'px';
      this.d2.appendChild(this.d4);
      this.clear = document.createElement('div');
      this.d2.appendChild(this.clear);
      this.clear.style.clear='both';
      if(this.options.resizable){
      this.d5 = document.createElement('div');
      this.d2.appendChild(this.d5);
      this.d5.className='LB_resize';
      this.d5.d2 = this.d2;
      this.d5.d2.d4 = this.d4;
      this.d5.over = this.over;
      this.d5.options = this.options;
      this.d5.onmouseover=this.getResizer;
      this.d5.onmouseout=this.dropResizer;
      }
      if(this.options.overlay == true){
      this.d.d = this.d;
      this.d.d2 = this.d2;
      }
   },
   getDraggable: function(){
      if(this.options.draggable){
      if(this.resize)this.resize.destroy();
      if(!this.drag || (this.drag && !this.drag.dragging))
      this.drag = new Draggable(this.d2,{});
      }
   },
   getResizer: function(){
      if(this.options.resizable){
      if(this.drag)this.drag.destroy();
      if(!this.resize || (this.resize && !this.resize.dragging))
      this.resize = new Resizer(this.d2,{});
      }
   },
   dropDraggable: function(){
      if(this.options.draggable){
      if(!this.drag.dragging && this.drag){
      this.drag.destroy();
      }}
   },
   dropResizer: function(){
      if(this.options.resizable){
      if(!this.resize.dragging && this.resize){
      this.resize.destroy();
      }}
   },
   display: function(){
      Element.setOpacity(this.d2, 0);
      this.position();
      new Effect.Opacity(this.d2, {from:0,to:this.options.opacity,duration:.5});
   },
   position: function(){
      var de = document.documentElement;
   	var w = self.innerWidth || (de&&de.clientWidth) || document.body.clientWidth;
   	var h = self.innerHeight || (de&&de.clientHeight) || document.body.clientHeight;


     	if (window.innerHeight && window.scrollMaxY) {
   		yScroll = window.innerHeight + window.scrollMaxY;
   	} else if (document.body.scrollHeight > document.body.offsetHeight){ // all but Explorer Mac
   		yScroll = document.body.scrollHeight;
   	} else { // Explorer Mac...would also work in Explorer 6 Strict, Mozilla and Safari
   		yScroll = document.body.offsetHeight;
     	}
      this.d2.style.width = this.options.width + 'px';
      this.d2.style.display = 'block';
      if(!this.options.left || this.options.left < 0){
   	this.d2.style.left = ((w - this.options.width)/2)+"px";
      }else{
      this.d2.style.left=parseInt(this.options.left)+'px';
      }

      var pagesize = this.getPageSize();
      var arrayPageScroll = this.getPageScrollTop();
   	if(this.d2.offsetHeight > h - 100){
         if(!this.options.top || this.options.top < 0){
   		this.d2.style.top = "45px";
         }else{
         this.d2.style.top=parseInt(this.options.top)+'px';
         }
   		this.d2.style.height=h-100 + 'px';
   		this.d4.style.height=h-145 + 'px';
   		this.d4.style.overflow ='auto';
   	} else {
         if(!this.options.top || this.options.top < 0){
   		this.d2.style.top = (arrayPageScroll[1] + ((pagesize[1]-this.d2.offsetHeight)/2))+"px";
         }else{
         this.d2.style.top=parseInt(this.options.top)+'px';
         }
   	}
   	if(this.d){this.d.style.height =   yScroll +"px";}
   },
   remove: function(){
      if(this.temp) this.temp();
   	new Effect.Opacity(this.d2, {from:1,to:0,duration:.5});
   	if(this.d){new Effect.Opacity(this.d, {from:.6,to:0,duration:.5});
   	Element.remove(this.d);}
      Element.remove(this.d2);
      return false;
   },
   parseQuery: function(query){
      var Params = new Object ();
      if ( ! query ) return Params; // return empty object
      var Pairs = query.split(/[;&]/);
      for ( var i = 0; i < Pairs.length; i++ ) {
         var KeyVal = Pairs[i].split('=');
         if ( ! KeyVal || KeyVal.length != 2 ) continue;
         var key = unescape( KeyVal[0] );
         var val = unescape( KeyVal[1] );
         val = val.replace(/\+/g, ' ');
         Params[key] = val;
      }
      return Params;
   },
   getPageScrollTop: function(){
      var yScrolltop;
      if (self.pageYOffset) {
         yScrolltop = self.pageYOffset;
      } else if (document.documentElement && document.documentElement.scrollTop){    // Explorer 6 Strict
         yScrolltop = document.documentElement.scrollTop;
      } else if (document.body) {// all other Explorers
         yScrolltop = document.body.scrollTop;
      }
      arrayPageScroll = new Array('',yScrolltop)
      return arrayPageScroll;
   },
   getPageSize: function(){
      var de = document.documentElement;
      var w = self.innerWidth || (de&&de.clientWidth) || document.body.clientWidth;
      var h = self.innerHeight || (de&&de.clientHeight) || document.body.clientHeight;

      arrayPageSize = new Array(w,h)
      return arrayPageSize;
   },
   getAjax: function(url){
      var xmlhttp=false;
      /*@cc_on @*/
      /*@if (@_jscript_version >= 5)
      // JScript gives us Conditional compilation, we can cope with old IE versions.
      // and security blocked creation of the objects.
        try {
        xmlhttp = new ActiveXObject("Msxml2.XMLHTTP");
        } catch (e) {
         try {
          xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
         } catch (E) {
          xmlhttp = false;
         }
        }
      @end @*/
      if (!xmlhttp && typeof XMLHttpRequest!='undefined') xmlhttp = new XMLHttpRequest();
      if(xmlhttp.overrideMimeType) xmlhttp.overrideMimeType('text/xml');
      if(url != ""){
         xmlhttp.open("GET", url, false);
         xmlhttp.send(null);
         return xmlhttp.responseText;
      }
   }

}


var Resizers = {
  drags: [],
  observers: [],

  register: function(draggable) {
    if(this.drags.length == 0) {
      this.eventMouseUp   = this.endDrag.bindAsEventListener(this);
      this.eventMouseMove = this.updateDrag.bindAsEventListener(this);
      this.eventKeypress  = this.keyPress.bindAsEventListener(this);

      Event.observe(document, "mouseup", this.eventMouseUp);
      Event.observe(document, "mousemove", this.eventMouseMove);
      Event.observe(document, "keypress", this.eventKeypress);
    this.drags.push(draggable);

    }
  },

  unregister: function(draggable) {
    this.drags = this.drags.reject(function(d) { return d==draggable });
    if(this.drags.length == 1){this.drags.pop();}
    if(this.drags.length == 0) {
      Event.stopObserving(document, "mouseup", this.eventMouseUp);
      Event.stopObserving(document, "mousemove", this.eventMouseMove);
      Event.stopObserving(document, "keypress", this.eventKeypress);
    }
  },

  activate: function(draggable) {
    window.focus(); // allows keypress events if window isn't currently focused, fails for Safari
    this.activeDraggable = draggable;
  },

  deactivate: function() {
    this.activeDraggable = null;
  },

  updateDrag: function(event) {
    if(!this.activeDraggable) return;
    var pointer = [Event.pointerX(event), Event.pointerY(event)];
    // Mozilla-based browsers fire successive mousemove events with
    // the same coordinates, prevent needless redrawing (moz bug?)
    if(this._lastPointer && (this._lastPointer.inspect() == pointer.inspect())) return;
    this._lastPointer = pointer;
    this.activeDraggable.updateDrag(event, pointer);
  },

  endDrag: function(event) {
    if(!this.activeDraggable) return;
    this._lastPointer = null;
    this.activeDraggable.endDrag(event);
    this.activeDraggable = null;
  },

  keyPress: function(event) {
    if(this.activeDraggable)
      this.activeDraggable.keyPress(event);
  },

  addObserver: function(observer) {
    this.observers.push(observer);
    this._cacheObserverCallbacks();
  },

  removeObserver: function(element) {  // element instead of observer fixes mem leaks
    this.observers = this.observers.reject( function(o) { return o.element==element });
    this._cacheObserverCallbacks();
  },

  notify: function(eventName, draggable, event) {  // 'onStart', 'onEnd', 'onDrag'
    if(this[eventName+'Count'] > 0)
      this.observers.each( function(o) {
        if(o[eventName]) o[eventName](eventName, draggable, event);
      });
  },

  _cacheObserverCallbacks: function() {
    ['onStart','onEnd','onDrag'].each( function(eventName) {
      Resizers[eventName+'Count'] = Resizers.observers.select(
        function(o) { return o[eventName]; }
      ).length;
    });
  }
}


var Resizer = Class.create();
Resizer.prototype = {
  initialize: function(element) {
  	this.offset = Array();
    var options = Object.extend({
      handle: false,

      reverteffect: function(element, top_offset, left_offset) {
        var dur = Math.sqrt(Math.abs(top_offset^2)+Math.abs(left_offset^2))*0.02;
        element._revert = new Effect.Move(element, { x: -left_offset, y: -top_offset, duration: dur});
      },

      zindex: 1000,
      revert: false,
      scroll: false,
      scrollSensitivity: 20,
      scrollSpeed: 15,
      snap: false   // false, or xy or [x,y] or function(x,y){ return [x,y] }
    }, arguments[1] || {});
    this.element = $(element);

    if(options.handle && (typeof options.handle == 'string')) {
      var h = Element.childrenWithClassName(this.element, options.handle, true);
      if(h.length>0) this.handle = h[0];
    }
    if(!this.handle) this.handle = $(options.handle);
    if(!this.handle) this.handle = this.element;

    if(options.scroll && !options.scroll.scrollTo && !options.scroll.outerHTML)
      options.scroll = $(options.scroll);

    Element.makePositioned(this.element); // fix IE

    this.delta    = this.currentDelta();
    this.options  = options;
    this.dragging = false;

    this.oHeight = parseInt(this.element.style.height);
    this.oWidth = parseInt(this.element.style.width);

    this.eventMouseDown = this.initDrag.bindAsEventListener(this);
    Event.observe(this.handle, "mousedown", this.eventMouseDown);

    Resizers.register(this);
  },

  destroy: function() {
    Event.stopObserving(this.handle, "mousedown", this.eventMouseDown);
    Resizers.unregister(this);
  },

  currentDelta: function() {
    return([
      parseInt(Element.getStyle(this.element,'width') || '0'),
      parseInt(Element.getStyle(this.element,'height') || '0')]);
  },

  initDrag: function(event) {
    if(Event.isLeftClick(event)) {
      // abort on form elements, fixes a Firefox issue
      var src = Event.element(event);
      if(src.tagName && (
        src.tagName=='INPUT' ||
        src.tagName=='SELECT' ||
        src.tagName=='OPTION' ||
        src.tagName=='BUTTON' ||
        src.tagName=='TEXTAREA')) return;

      if(this.element._revert) {
        this.element._revert.cancel();
        this.element._revert = null;
      }

      var pointer = [Event.pointerX(event), Event.pointerY(event)];
      var pos     = Position.cumulativeOffset(this.element);

      this.offset[0] = pointer[0];
      this.offset[1] = pointer[1];
      Resizers.activate(this);
      Event.stop(event);
    }
  },

  startDrag: function(event) {
    this.dragging = true;

    if(this.options.zindex) {
      this.originalZ = parseInt(Element.getStyle(this.element,'z-index') || 0);
      this.element.style.zIndex = this.options.zindex;
    }

    if(this.options.ghosting) {
      this._clone = this.element.cloneNode(true);
      Position.absolutize(this.element);
      this.element.parentNode.insertBefore(this._clone, this.element);
    }


    Resizers.notify('onStart', this, event);
    if(this.options.starteffect) this.options.starteffect(this.element);
  },

  updateDrag: function(event, pointer) {
    if(!this.dragging) this.startDrag(event);
    Position.prepare();
    Resizers.notify('onDrag', this, event);
    this.draw(pointer);
    if(this.options.change) this.options.change(this);

    if(this.options.scroll) {
      this.stopScrolling();

      var p;
      if (this.options.scroll == window) {
        with(this._getWindowScroll(this.options.scroll)) { p = [ left, top, left+width, top+height ]; }
      } else {
        p = Position.page(this.options.scroll);
        p[0] += this.options.scroll.scrollLeft;
        p[1] += this.options.scroll.scrollTop;
        p.push(p[0]+this.options.scroll.offsetWidth);
        p.push(p[1]+this.options.scroll.offsetHeight);
      }
      var speed = [0,0];
      if(pointer[0] < (p[0]+this.options.scrollSensitivity)) speed[0] = pointer[0]-(p[0]+this.options.scrollSensitivity);
      if(pointer[1] < (p[1]+this.options.scrollSensitivity)) speed[1] = pointer[1]-(p[1]+this.options.scrollSensitivity);
      if(pointer[0] > (p[2]-this.options.scrollSensitivity)) speed[0] = pointer[0]-(p[2]-this.options.scrollSensitivity);
      if(pointer[1] > (p[3]-this.options.scrollSensitivity)) speed[1] = pointer[1]-(p[3]-this.options.scrollSensitivity);
      this.startScrolling(speed);
    }

    // fix AppleWebKit rendering
    if(navigator.appVersion.indexOf('AppleWebKit')>0) window.scrollBy(0,0);
    Event.stop(event);
  },

  finishDrag: function(event, success) {
    this.dragging = false;

    if(this.options.ghosting) {
      Position.relativize(this.element);
      Element.remove(this._clone);
      this._clone = null;
    }

    Resizers.notify('onEnd', this, event);

    var revert = this.options.revert;
    if(revert && typeof revert == 'function') revert = revert(this.element);

    var d = this.currentDelta();
    if(revert && this.options.reverteffect) {
      this.options.reverteffect(this.element,
        d[1]-this.delta[1], d[0]-this.delta[0]);
    } else {
      this.delta = d;
    }

    //if(this.options.zindex)
      this.element.style.zIndex = this.originalZ;

    if(this.options.endeffect)
      this.options.endeffect(this.element);

    Resizers.deactivate(this);
    //Resizers.reset();
  },

  keyPress: function(event) {
    if(event.keyCode!=Event.KEY_ESC) return;
    this.finishDrag(event, false);
    Event.stop(event);
  },

  endDrag: function(event) {
    if(!this.dragging) return;
    this.stopScrolling();
    this.finishDrag(event, true);
    Event.stop(event);
    this.oWidth = parseInt(this.element.style.width);
    this.oHeight = parseInt(this.element.style.height);
  },

  draw: function(point) {
    var pos = Position.cumulativeOffset(this.element);
    var d = this.currentDelta();
    pos[0] -= d[0]; pos[1] -= d[1];
    var p = new Array();

    p[0] = this.oWidth + point[0] - this.offset[0];
    p[1] = this.oHeight + point[1] - this.offset[1];

    var style = this.element.style;
    if((!this.options.constraint) || (this.options.constraint=='horizontal')){
      style.width = p[0] + "px"; this.element.d4.style.width = p[0] + 'px';}
    if((!this.options.constraint) || (this.options.constraint=='vertical')){
      style.height  = p[1] + "px"; this.element.d4.style.height = p[1]-30 + 'px';}
    if(style.visibility=="hidden") style.visibility = ""; // fix gecko rendering

  },

  stopScrolling: function() {
    if(this.scrollInterval) {
      clearInterval(this.scrollInterval);
      this.scrollInterval = null;
      Resizers._lastScrollPointer = null;
    }
  },

  startScrolling: function(speed) {
    this.scrollSpeed = [speed[0]*this.options.scrollSpeed,speed[1]*this.options.scrollSpeed];
    this.lastScrolled = new Date();
    this.scrollInterval = setInterval(this.scroll.bind(this), 10);
  },

  scroll: function() {
    var current = new Date();
    var delta = current - this.lastScrolled;
    this.lastScrolled = current;
    if(this.options.scroll == window) {
      with (this._getWindowScroll(this.options.scroll)) {
        if (this.scrollSpeed[0] || this.scrollSpeed[1]) {
          var d = delta / 1000;
          this.options.scroll.scrollTo( left + d*this.scrollSpeed[0], top + d*this.scrollSpeed[1] );
        }
      }
    } else {
      this.options.scroll.scrollLeft += this.scrollSpeed[0] * delta / 1000;
      this.options.scroll.scrollTop  += this.scrollSpeed[1] * delta / 1000;
    }

    Position.prepare();
    Resizers.notify('onDrag', this);
    Resizers._lastScrollPointer = Resizers._lastScrollPointer || $A(Resizers._lastPointer);
    Resizers._lastScrollPointer[0] += this.scrollSpeed[0] * delta / 1000;
    Resizers._lastScrollPointer[1] += this.scrollSpeed[1] * delta / 1000;
    if (Resizers._lastScrollPointer[0] < 0)
      Resizers._lastScrollPointer[0] = 0;
    if (Resizers._lastScrollPointer[1] < 0)
      Resizers._lastScrollPointer[1] = 0;
    this.draw(Resizers._lastScrollPointer);

    if(this.options.change) this.options.change(this);
  },

  _getWindowScroll: function(w) {
    var T, L, W, H;
    with (w.document) {
      if (w.document.documentElement && documentElement.scrollTop) {
        T = documentElement.scrollTop;
        L = documentElement.scrollLeft;
      } else if (w.document.body) {
        T = body.scrollTop;
        L = body.scrollLeft;
      }
      if (w.innerWidth) {
        W = w.innerWidth;
        H = w.innerHeight;
      } else if (w.document.documentElement && documentElement.clientWidth) {
        W = documentElement.clientWidth;
        H = documentElement.clientHeight;
      } else {
        W = body.offsetWidth;
        H = body.offsetHeight
      }
    }
    return { top: T, left: L, width: W, height: H };
  }
}
