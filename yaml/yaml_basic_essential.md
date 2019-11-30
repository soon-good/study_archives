# yaml 기초 문법 정리

도커/쿠버네티스를 공부하다보니 yaml 문법에 대해 정통하지는 않더라도 일부 문법에 대해서는 알아야 쉽게 적응할 수 있겠다는 생각이 들었고, 정말 기초적인 문법들만 정리를 해놓으려 한다.

## 참고자료  

- [http://anitoy.pe.kr/yaml-format/](http://anitoy.pe.kr/yaml-format/)

- [https://lejewk.github.io/yaml-syntax/](https://lejewk.github.io/yaml-syntax/)



## yaml의 기본적인 자료형식

### 복합 자료형

배열, 리스트, key/value 형식, 일반 데이터 등을 어떤식으로 표현하는지를 간단하게 정리해보자

- scalar ,스칼라  
  문자, 문자열 또는 숫자를 scalar라고 하는 모양이다.  

  그냥 원자적(atomic)인 데이터를 의미하는 듯 하다.  
  y=c, y=1 과 같은 상수식 처럼 선형적이라고 개념을 정한 것을 보니 납득이 가기도 하지만 human friendly라는 목표와는 전혀 일치하지 않는다는 생각도 든다.  

- sequence, 시퀀스  
  배열 또는 리스트를 표현하기 위한 방식이다.  
  하나의 시퀀스 내에 들어가는 원소들 앞에는 '-'를 붙여서 배열/리스트 내의 원소인 것으로 구분한다.  
  배열을 시작할 때는 -를 사용한다.  

- mapping, 매핑
  해시, 딕셔너리, key/value 와 같은 형식을 의미한다.  
  (java의 Map, python의 dict와 같다고 생각하자)  



### 기본 자료형  

기본 자료형은 int, string, boolean을 지원한다고 한다.  

소숫점 자료형에 대해서는 공부를 더 해봐야 할 듯 하다.  

  

## 기초 문법들

### 문서의 시작, 끝 표시

문서의 시작은 --- 기호로 표시한다.  

문서의 끝은 ... 기호를 사용한다. 

두 가지 모두 선택 사항이다.  



### 주석  

\#으로 시작한다.   

  

### 들여쓰기  
각 블록은 들여쓰기로 구분한다고 한다.  

yaml과 같이 들여쓰기를 사용하는 파이썬의 경우는 jupyter, pycharm, vscode, sublime text의 도움을 받아 문법을 체크할 수 있다.  

yaml의 경우도 vscode 등으로 체크할 수 있지만, 설정이 길어지면 으뜨카지 하는 불안함도 있다.  

  

### 스트림  

이 부분에 대해서는 공부를 더 해봐야 알 듯 하다.  



## 예제

### 스칼라

바로 전에 설명했듯이 스칼라는 문자열 또는 숫자와 같은 데이터라 정리했었다. 스칼라 데이터는 다른 자료형에 대입이 되어야 하는 요소인 듯하다. 이런 걸 보면 정말 human friendly가 아니라 mathmetics friendly다. y=c에 맞춰줘야 한다니...  



매핑 자료에 대입해봤다.  

```yaml
good : 100
also_good : 90
etc : 70
```



### 시퀀스

```yam
- Apple
- Banana
- Cherry
- Melon
# [Apple, Banana, Cherry, Melon]
```

  

### 매핑

매핑은 java의 Map, python의 dict와 같은 형식을 가진다고 했었다.  
여기서는 시퀀스를 매핑에 담아보는 예제를 남겨본다.  
{ blablah... } 와 같이 중괄호로 묶어서 사용할 수 도 있다고 한다.  

```yaml
fruits:
	- Apple
	- Banana
	- Cherry
	- Melon
soccer players:
	- Sonny
	- Kangin Lee
	- Hwang In Bum
	- Hwang Ui Jo
	- Hwang He Chan
```

위의 식을 아래 처럼 표현 가능하다.  

```yaml
fruits:{
	- Apple
	- Banana
	- Cherry
	- Melon
}
soccer players: {
	- Sonny
	- Kangin Lee
	- Hwang In Bum
	- Hwang Ui Jo
	- Hwang He Chan
}
```



