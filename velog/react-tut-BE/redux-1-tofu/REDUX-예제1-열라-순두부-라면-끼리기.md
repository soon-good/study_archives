# redux 예제 (1) - 열라 순두부 라면 끼리기

> velog에 정리하기 전에 다듬는 과정의 일환으로 정리중이다. 정리하는게 쉽지 않다. 이렇게 깨작 개념정리하는 것도 엄청나게 힘든데... react 책 쓰신분들은 득도를 하신분들이 아닐까 싶다. 예제를 정리하면서 참을 인자를 100번은 쓴것 같다. ☹️☹️☹️ 

이번 예제는 정말 단순 예제이다. 중급 (1),(2) 정도의 예제까지 따로 만들어보고 쓱닷컴 클론코딩으로 리덕스 연습을 계속할 생각중이다~ 단순 예제이긴 하지만, 공부한지 너무 오래되었을때 예제가 단순하다면 다시 리마인드 시키기에 정말 좋기 때문에 단순 무식한 예제로 정리했다.  

  

# 참고자료들

- [redux.js.org](https://redux.js.org/)
  - 주로 Getting Started, Tutorial 위주로 살펴보았다.
  - API 명세 역시 찾아보았는데 설명이 자세했다. 
  - Best Practices 에서는 좋은 코딩 관례에 대해 설명하고 있다.
- [Dan Abramov - Presentational and Container Components](https://medium.com/@dan_abramov/smart-and-dumb-components-7ca2f9a7c7d0)
  - Dan Abramov 는 redux의 창시자이다.
- [리액트를 다루는 기술(개정판) - Ebook](http://www.yes24.com/Product/Goods/79260300)



# 1. 스크린샷 🐌

예제는 아래와 같다. 순두부라면을 끼려(?)먹기 위해서 장바구니에 재료들을 담는 과정을 redux를 통해 예제로 만들수 있겠다는 생각이 들어서 시작했다.  
![스크린샷](https://github.com/soongujung/react_examples/blob/master/redux-ramen-shopping/md/img/1.gif)



redux를 굳이 위 그림 처럼 조그만 기능을 만들때에 사용할 필요는 없다. state 또는 props 를 잘 활용하면 된다.  

하지만 어플리케이션 전역에서 관리해야 할 상태가 있다면 redux를 사용하는 것을 고려해볼만 하다.   

# 2. CRA, 의존성 설치
## CRA (Create React App)
```bash
$ yarn create react-app redux-ramen-shopping
$ cd redux-ramen-shopping
```

  

## 의존성 설치

```bash
$ yarn add redux
$ yarn add react-redux
```

  

# 3. store, reducer 작성

리덕스의 개념에 대해서는 추후 다른 글에 정리할 예정이다. 스토어와 reducer의 개념을 이해해야 하는데, 예제를 정리하는 이 글에서 개념을 정리하게 되면 글이 길어지므로 생략.  

  

## reducer - cart.js

reducer 의 이름은 cart로 정했다. 코드를 보자.  
- 버튼 누를 때의 액션 type 명
  
  - 'cart/INCREMENT'
- 버튼 누를 때의 액션 type 명
  - 'cart/DECREMENT'

  

**!TODO : 정리할 내용**  

- reducer의 간단한 개념, 공식 페이지 핵심 문구 쬐끔 인용(핵심만 인용)
  

  

### src/store/modules/cart.js

```javascript
// 액션 타입 정의
const INCREMENT = 'cart/INCREMENT';
const DECREMENT = 'cart/DECREMENT';

export const incrementItem = (itemKind) => ({type: INCREMENT, itemKind: itemKind});
export const decrementItem = (itemKind) => ({type: DECREMENT, itemKind: itemKind});
// export const currentState = () => ()

const initialState = {
    number: 0
};

export default function cart(state = initialState, action){
    const itemType = action.itemKind;

    switch (action.type){
        case INCREMENT:
            console.log('reducer (inc) >>> ', state);
            console.log('reducer (inc) >>> action ::: ', action);
            const incState = {
                ...state,
                // number: state.number + 1,
                // itemKind: action.itemKind,
            };

            incState[itemType] = incState[itemType] || {};
            incState[itemType].number = (incState[itemType].number || 0) + 1;

            return incState;

        case DECREMENT:
            console.log('reducer (dec) >>> ', state);
            const decState = {
                ...state,
                // number: state.number -1,
                // itemKind: action.itemKind,
            };

            decState[itemType] = decState[itemType] || {};
            decState[itemType].number = (decState[itemType].number || 0) - 1;

            return decState;
        default:
            const defaultState = {
                ...state
            };

            defaultState[itemType] = defaultState[itemType] || {};
            defaultState[itemType].number = (defaultState[itemType].number || 0);

            return state;
    }
}
```


내용을 모두 설명해야 하는데 더 자세한 설명은 수요일에 할 예정이다.   

state 의 형태는 아래와 같이 구성했다.  

>  **!TODO** 
>
> 그런데 정리가 조금 필요하다. 기획을 정해놓고 시작한게 아니서 cart.js 에서 정의한 state의 형식과 MartItemList.js(리덕스컨테이너)에서 정의한 state의 형식이 조금 다르다. MartItemList.js 에서  `number: cart.number || 0`  같은 형태로 null 일경우에 대한 처리를 해놓아서 잘 동작하고 있다. 추후 소스에 약간의 수정이 필요하다.(데이터 형식을 맞추는 작업이 필요하다.)  
>
> 이게 참... 갑자기 팍💡! 하고 떠오른걸 그냥 마구잡이로 만들어보려고 했을때의 폐혜이긴 하다;;; 😅



```plain
{
    "ramen":{ number: xx, ..... },
    "pa": { number: xx, ..... },
    "tofu": { number: xx, ..... },
    // ...
}
```

  

## index.js - 루트리듀서 등록

redux 모듈의 combineRedcers 함수를 통해 위에서 작성했던 cart 함수를 combineReducers({...}) 내에 등록한다. cart 함수는 Reducer 역할을 하도록 작성한 함수이다. reducer의 개념에 대해서는 redux.org에서 아주 명확하고 간결하게 설명하고 있는데 !TODO 이부분 역시 발췌!!!  

### combineReducers(reducers) API 명세

참고자료 : [combineReducers(reducers) - redux.js.org](https://redux.js.org/api/combinereducers)

> **combineReducers (reducers) : Function **
>
> #### Arguments[#](https://redux.js.org/api/combinereducers#arguments)
>
> 1. `reducers` (*Object*): An object whose values correspond to different reducing functions that need to be combined into one. See the notes below for some rules every passed reducer must follow.
>
> #### Returns[#](https://redux.js.org/api/combinereducers#returns)
>
> (*Function*): A reducer that invokes every reducer inside the `reducers` object, and constructs a state object with the same shape.

  

### src/store/modules/index.js

```javascript
import { combineReducers } from 'redux';
import cart from './cart';


// 루트리듀서 정의
export default combineReducers({
    cart,
})
```

  

# 4. Provider + App

- 위에서 작성한 src/store/modules/index.js에서 반환하는 rootReducer를 import한다.
  
  - import rootReducer from './store/modules';
- import한 rootReducer를 createStore의 인자로 넘겨주어, store를 생성한다.
  - const store = createStore(rootReducer, ...);
  - 보통 애플리케이션 전역에서는 스토가 하나여야 하는 것이 원칙이다.
  - 하지만 redux 공식문서를 보다보면 여러개의 스토어를 써야 하는 경우에 대한 설명을 하는 메뉴역시 존재했다.
- "react-redux"모듈 내의 Provider 컴포넌트로 App 컴포넌트를 감싸준다.
  - Provider 컴포넌트는 store 객체를 props로 가지고 있는데, App 컴포넌트를 감싸면서 App 컴포넌트가 상태에 접근할 수 있도록 도와주는 역할을 한다.

  

## src/index.js

> [createStore() - API Specification](https://redux.js.org/api/createstore)   
>
> 형식)  
>
> createStore (reducer, [preloadState], [enhancer] )  :  Store  
>
> #### Arguments[#](https://redux.js.org/api/createstore#arguments)
>
> - reducer *(Function)*: A [reducing function](https://redux.js.org/understanding/thinking-in-redux/glossary#reducer) that returns the next [state tree](https://redux.js.org/understanding/thinking-in-redux/glossary#state), given the current state tree and an [action](https://redux.js.org/understanding/thinking-in-redux/glossary#action) to handle.
> - [`preloadedState`] *(any)*: The initial state. You may optionally specify it to hydrate the state from the server in universal apps, or to restore a previously serialized user session. If you produced `reducer` with [`combineReducers`](https://redux.js.org/api/combinereducers), this must be a plain object with the same shape as the keys passed to it. Otherwise, you are free to pass anything that your `reducer` can understand.
> - [`enhancer`] *(Function)*: The store enhancer. You may optionally specify it to enhance the store with third-party capabilities such as middleware, time travel, persistence, etc. The only store enhancer that ships with Redux is [`applyMiddleware()`](https://redux.js.org/api/applymiddleware).
>
> #### Returns[#](https://redux.js.org/api/createstore#returns)
>
> ([*`Store`*](https://redux.js.org/api/store)): An object that holds the complete state of your app. The only way to change its state is by [dispatching actions](https://redux.js.org/api/store#dispatchaction). You may also [subscribe](https://redux.js.org/api/store#subscribelistener) to the changes to its state to update the UI.



```javascript
import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './App';
import * as serviceWorker from './serviceWorker';

import rootReducer from './store/modules';
import {Provider} from "react-redux";

import {createStore} from "redux";
const devTools = window.__REDUX_DEVTOOLS_EXTENSION__ && window.__REDUX_DEVTOOLS_EXTENSION__();
const store = createStore(rootReducer, devTools);
console.log(store.getState())

ReactDOM.render(
    <Provider store={store}>
        <App/>
    </Provider>,
    document.getElementById('root')
);

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();
export default store;
```

  

## App.js

MartItemList.js 컴포넌트를 조금 뒤에 정리하게 되겠지만 MartItemList 컴포넌트가 컨테이너 컴포넌트(스마트 컴포넌트) 역할을 수행한다. ( MartItemList 컴포넌트는 `react-redux` 의 `connect()` 함수를 통해 index.js 에서 기술했던 Provider 컴포넌트로 연결되게끔 해준다.)  

  

```javascript
import React from 'react';
import MartItemList from "./mart/MartItemList";

function App() {

    return (
        <div>
            <h1> &nbsp;&nbsp;&nbsp; 열라 순두부 라면 끼리기 </h1>
            <hr/>
            <MartItemList/>
            <hr/>
        </div>
    );
}

export default App;
```

  

# 5. 컴포넌트 작성

## 프로젝트 구조
프로젝트 구조는 아래와 같이 구성했다.
```plain
src/
 |-mart/
 |   |-item/
 |   |  +- FoodItem.css
 |   |  +- FoodItem.js
 |   |  +- pa.jpg
 |   |  +- pepper.jpg
 |   |  +- ramen.jpg
 |   |- FoodITemContainer.js	// 삭제 예정
 |   |- MartItemList.css	// MartItemList.js의 css
 |   |- MartItemList.js		// FoodItem 을 감싸는 스마트 컴포넌트, 컨테이너 역할을 수행
 |-store/
 |   |-modules/
 |   |    +-cart.js
 |   |    +-index.js
 |-App.js
 |-App.css
 |-serviceWorker.js
 // ... 
```

  

## UI 컴포넌트 - FoodItem

FoodItem 은 라면/두부/청양고추/순두부의 모양을 렌더링해주는 단순 UI 컴포넌트이다. 리덕스의 창시자 Dan Abramov는 이런 컴포넌트를 Dumb Component로 표현했다. (참고: [Dan Abramov - medium](https://medium.com/@dan_abramov/smart-and-dumb-components-7ca2f9a7c7d0) )  

  

### FoodItem.js
```javascript
import React from "react";

import './FoodItem.css';

import pa from './pa.jpg';
import pepper from './pepper.jpg';
import ramen from './ramen.jpg';
import tofu from './tofu.jpg';

const itemInfo = {
    PA : {
        id: 'PA',
        img : pa,
        korDisplay: '흙대파(단)'
    },
    PEPPER : {
        id: 'PEPPER',
        img: pepper,
        korDisplay: '청양고추 150g'
    },
    RAMEN : {
        id: 'RAMEN',
        img: ramen,
        korDisplay: '라면'
    },
    TOFU : {
        id: 'TOFU',
        img: tofu,
        korDisplay: '[풀무원] 국산 콩 순두부 (350g)'
    }
};

const FoodItem = ({itemKind, onIncrement, onDecrement}) => {

    const imgSrc = itemInfo[itemKind].img;
    const imgName = itemInfo[itemKind].korDisplay;


    return (
      <div className="foodItem">
          <img className="foodImg" src={imgSrc} alt="thumbnail"/>
          <span className="displayLabel">{imgName}</span>
          <div className="foodItemButtons">
              <button onClick={(e)=>{onIncrement(itemKind)}}> + </button>
              <button onClick={(e) => {onDecrement(itemKind)}}> - </button>
          </div>
      </div>
    );
};

export default FoodItem;
```


외부 호출단에서 onIncrement, onDecrement, itemKind를 전달받아서 이벤트 핸들러 바인딩 등의 작업을 하고 있다.

- `<button onClick={(e)=>{onIncrement(itemKind)}}> + </button>`
  - 이벤트 핸들러에 파라미터가 필요할 때는 `(e)=>{onIncrement(itemKind)}` 과 같은 방식으로 호출하면 된다.
  

  

### FoodItem.css

css 를 잘하는 것이 아니라서... css를 제대로 잘 설명할 자신도 없어서 그냥 소스만 정리~...
```css
.foodItem {
    margin-top: 5px;
    width: 400px;
    height: 100px;
    border: 1px solid #61dafb;
}

.foodImg {
    margin-top: 7px;
    vertical-align: middle;
    width: 84px;
    height: 84px;
}

.displayLabel {
    vertical-align: middle;
    margin-left: 20px;
}

.foodItemButtons {
    float: right;
    margin-top: 35px;
    margin-right: 5px;
}
```

  

## Redux 컴포넌트 - MartItemList

### MartItemList.js
```javascript
import React, {Component} from "react";
import FoodItem from "./item/FoodItem";
import './MartItemList.css'
import {connect} from 'react-redux';
import {incrementItem, decrementItem} from "../store/modules/cart";

const martItemList = [
    'RAMEN', 'PA', 'PEPPER', 'TOFU'
];

class MartItemList extends Component {

    constructor(props) {
        super(props);
        this.state = {
            RAMEN :0,
            PA: 0,
            PEPPER: 0,
            TOFU: 0
        }
    }

    handleIncrement = (itemKind) => {
        this.props.incrementItem(itemKind);
        this.setState({
            [itemKind]: this.state[itemKind] + 1
        })
    };

    handleDecrement = (itemKind) => {
        this.props.decrementItem(itemKind);
        this.setState({
            [itemKind]: this.state[itemKind] - 1
        })
    };

    render () {

        return (
            <div>
                <div className="martItemList">
                    {
                        martItemList.map(
                            item => (
                                <FoodItem
                                    itemKind={item}
                                    key={item}
                                    onIncrement={this.handleIncrement}
                                    onDecrement={this.handleDecrement}
                                />
                            )
                        )
                    }
                </div>
                <hr/>
                <div>
                    <p>열라면 :  {this.state.RAMEN} 개</p>
                    <p>흙대파(단) : {this.state.PA} 개</p>
                    <p>청양고추 150g : {this.state.PEPPER} 개</p>
                    <p>[풀무원] 국산 콩 순두부 (350g) : {this.state.TOFU} 개</p>
                </div>
            </div>
        );
    }
}

const mapStateToProps = ({cart}) =>{
    const temp = {
        ...cart,
        number: cart.number || 0
    };
    return temp;
}


const mapDispatchToProps = dispatch => ({
    incrementItem: (itemKind) => dispatch(incrementItem(itemKind)),
    decrementItem: (itemKind) => dispatch(decrementItem(itemKind))
});

export default connect(mapStateToProps, mapDispatchToProps)(MartItemList);
```

redux를 react에서 사용할 때는 코딩을 하기에 더 쉬워진 측면이 있기는하다. react가 아닌 순수 redux로 코딩할때는 굉장히 어렵게 이해했었는데, react-redux  라이브러리를 사용하면 조금은 간편하게 코딩을 하게 된다. 아래 내용들만 기억한다면 간단하게 코딩이 가능하다.
- `mapStateToProps`
- `mapDispatchToProps`
- `connect()` 함수
  - connect() 함수는 react-redux 모듈에서 제공하는 함수이다.
  - mapStateToProps, mapDispatchToProps 함수를 전달 받아서 새로운 함수를 반환하는데
  - 이 새로운 함수의 인자에 컨테이너 객체(ex. MartItemList)를 대입하면 
  - 해당 컴포넌트(`MartItemList`) 에 대해서 
    - state를 MartItemList의 props 로 주입
    - dispatch를 MartItemList의 props 로 주입
- 이렇게 state, dispatch 를 props 로 전달하게 되면, state, dispatch를 읽기전용으로 사용할 수있게 되어 불변성을 보장할수 있기 때문에 장점이 되지 않을까 하는 그런 생각이 든다.

  

### MartItemList.css

css에도 소질이 없고, 디자인 감각에도 소질이 없는 편이어서...😅😅 달랑 한줄밖에 없기는 한다.🤓
```css
.martItemList{
    margin-left: 5px;
}
```



# 마치면서 🛩

2018년 말 쯤, 150만원 상당의 사설 react 교육 강의를 주말에 수강했었다. 그것도 사비로 결제했었다. ㅋㅋㅋ 그때 그렇게 아무 욕심없이 아무 댓가 없이 선한 마음으로 4개월 정도를 내 몸을 고생시켰던게 지금 이렇게 정리할 것들을 만들어주고, 마음의 양식이 되도록 만들어준다는 점에서... 사람은 역시 평소에 성실해야 한다는 것을 내심 깨닫는다.😀   

