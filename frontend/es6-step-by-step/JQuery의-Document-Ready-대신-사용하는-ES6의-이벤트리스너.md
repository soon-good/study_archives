# JQuery의 Document.ready 대신 사용하는 ES6의 이벤트 리스너

손가락에 고름이 혐짤 수준으로 커졌는데 2주 가까이 참아왔었다. 오늘 그 고름을 병원에서 **가위로 슉슉 찌익**, **바늘로 찔러서**  `손으로 꾸욱 꼭꼭` **빨래 짜듯이 매우 세게** 짜낸 다음에 ㅋㅋ 소독하고 나서 손가락에 붕대를 크게 감았더니 타이핑이 너무 불편하다. 너무 불편하지만, 꼭 정리해야 할 내용인 것 같아서 대충이라도 정리해두기로 했다.  


# 참고자료

- [[JS]Document.ready 의 대안](https://blog.hodory.dev/2020/01/05/Document-Ready-Alternative/)
- [onload, DOMContentLoaded 이벤트 리스너들](https://webdir.tistory.com/515)

  

# DOMContentLoaded

HTML 과 script 가 로드된 시점에 발생하는 이벤트이다. onload 이벤트보다 먼저 발생한다. 

```javascript
window.addEventListener('DOMContentLoaded', function(){
  // .. TODO ... 
});
```

- 단점이 하나 있는데 IE8 이하에서는 동작하지 않는 다는 점이 단점이다.



# window 객체에 리스너 등록하는 방식

문서의 모든 컨텐츠(images, script, css 등)이 모두 로드된 후 발생하는 이벤트  


## window 객체의 onload 프로퍼티에 함수를 지정하는 방식

```javascript
window.onload = function(){
  // .. TODO
}
```

  

## window 객체에 addEventListener 를 사용해 리스너를 등록하는 방식

```javascript
window.addEventListener('load', function(){
  // .. TODO
})
```





# 개별 엘리먼트(body, etc) 에 load 이벤트 리스너 등록하기

## ex) body 태그의 onload 에 대한 동작(함수) 지정하기

```html
<html>
  <body onload="renderChart()">
    <!-- ... -->
  </body>
	...
  <script>
    function renderChart(){
      // todo ...
    }
  </script>
</html>
```



## ex) 개별 엘리먼트의 'load' 이벤트 리스너 등록하기

```javascript
window.addEventListener('load', function(){ 
  // .. TODO ...
});
```





