# 라우터 (react-router) - (2) 중첩 구조 라우터 작성, 파라미터 연동

>  티스토리의 계정정책이 변경되어 [https://silentcargo.tistory.com/130](https://silentcargo.tistory.com/130) 의 글을 백업하는 동시에 벨로그에 작성할 글을 백업하는 용도로 작성.

  

# 중첩구조 라우터 작성, 단순 파라미터 연동

우선 Contributor들의 목록을 `<a></a>`를 확장한 컴포넌트인`<Link></Link>`로 나열하는 Contributors.js를 신규 작성해보겠습니다.  

  

(새로 작성하는 컴포넌트의 이름은 Contributors입니다. Contributor의 복수형입니다. 끝에 s가 붙어있으니 주의해주세요.)  

**Contributors.js**  

경로 : src/components/Contributors.js  

