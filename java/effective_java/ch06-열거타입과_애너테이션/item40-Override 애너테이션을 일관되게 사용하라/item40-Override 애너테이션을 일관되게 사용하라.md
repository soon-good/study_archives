# 아이템 40 - @Override 애너테이션을 일관되게 사용하라

이번 장에서는 @Override를 해야 하는 구문을 Overloading 하는 경우를 예로 들어서 설명하며, @Override를 할 경우 잘못된 수정을 할 경우 컴파일러에서 컴파일 에러를 내어, 런타임 환경에서 예기치 못한 오류가 발생하는 경우를 미연에 방지할 수 있다는 이야기를 하고 있다.  

메서드를 상속할 경우 재정의(overriding)하는 것이 아니라 다중정의(overloading)를 하는 경우가 있다. 메서드를 다중정의(overloading)을 하기 위한 목적이 아니라면 재정의(overriding)하기 위한 목적의 로직들에는 일관적으로 모두 @Override 애너테이션을 붙이는 것을 강조하고 있다.  

보통 IDE의 단축키(refactor>override methods... ) 등을 통해 override를 할 수 있으므로 그렇게 중요하게 생각하지 않을 수도 있다. 이런 ui 메뉴가 있기 때문에 override할 목적이었는데 실수로 overloading 하게 되는 경우를 방지할 수 있는 것은 맞다.  

하지만  

- 프로그램 내에서 논리적인 오류를 찾아내야 할때 @Override가 아닌 오버로드 구문인데, Overriding된 용도로 사용한 것과 같은 구문에 대한 오류를 찾아내는 경우
- 부주의 및 실수로 잘못 작성된 코드

에 대해 판단의 기준을 가질 수 있기 때문에 이번 item40을 무시하고 넘어가기만 할 수는 없다.  



# ex) Overriding을 잘못하여 Overloading 하는 예



