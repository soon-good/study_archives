# spring data jpa CRUD



- Request 데이터 Dto
- API 요청에 대한 Controller
- 트랜잭션, 도메인 기능간의 순서를 보장하는 Service



> 보통 Service에서 비지니스 로직을 처리한다고 오해를 할 수 있다. 하지만 그렇지 않다.  
>
> Service는 
>
> - 트랜잭션
> - 도메인 간의 순서 보장
>
> 의 역할만을 수행한다.  



![Spring Web Layer](./img/Spring_Web_Layer.png)



- Web Layer
  - 컨트롤러(@Controller), JSP/Freemarker 등 뷰 템플릿 영역
  - @Filter, 인터셉터, 컨트롤러 어드바이스(@ControllerAdvice) 등 외부 요청과 응답에 대한 전반적인 영역
- Service Layer
  - @Service에 사용되는 서비스 영역
  - Controller, Dao의 중간영역에서 사용되는 영역
  - @Transactional 이 사용되어야 하는 영역
- Repository Layer
  - Database와 같은 데이터 저장소에 접근하는 영역 (DAO 영역)
- Dtos
  - Dto (Data Transfer Object)
  - Dto : 계층 간에 데이터 교환을 위한 객체
  - Dtos는 Dto들의 영역을 이야기한다.
  - ex)
    - 뷰 템플릿 엔진에서 사용될 객체나 Repository Layer에서 결과로 넘겨준 객체 등이 이들을 이야기한다.  
- Domain Model
  - 도메인이라 불리는 개발 대상을 모든 사람이 동일한 관점에서 이해할 수 있고 공유할 수 있도록 단순화 시킨 것
  - 택시 앱을 예로 들면 배차, 탑승, 요금 등을 도메인이라고 할 수 있다.
  - @Entity가 사용된 영역 역시 도메인 모델이라고 이해할 수 있다.
  - 데이터베이스의 테이블과 관계가 있어야만 하는 것은 아니다.



JPA에서는 Web(Controller), Service, Repository, Dto, Domain 이 5가지의 레이어에서  

- Domain

이 비지니스 처리를 담당하도록 작성하게 할 수 있다.  

기존에(Mybatis, iBatis 등) 서비스로 처리하던. 방식은. **트랜잭션 스크립트**라고 한다. 주문 취소로직을 작성한다면 아래와 같다.  



### 서비스로 처리할 때(트랜잭션 스크립트)의 코드

### psuedo code

```java
@Transactional
public Order cancelOrder(int orderId){
  1) 데이터베이스로부터 주문정보(Orders), 결제정보(Billing), 배송정보(Delivery) 조회
  2) 배송 취소를 해야 하는지 확인
  3) if(배송 중이라면){
    배송 취소로 변경
  }
  4) 각 테이블에 취소상태 update
}
```



### 실제 코드

```java
@Transactional
public Order cancelOrder(int orderId){
  //1)
  OrdersDto order = ordersDao.selectOrders(orderId);
  BillingDto billing = billingDao.selectBilling(orderId);
  DeliveryDto delivery = deliveryDao.selectDelivery(orderId);
  
  //2)
  String deliveryStatus = delivery.getStatus();
  
  //3)
  if("IN_PROGRESS".equals(deliveryStatus)){
    delivery.setStatus("CANCEL");
    deliveryDao.update(delivery);
  }
  
  //4)
  order.setStatus("CANCEL");
  ordersDao.update(order);
  
  billing.setStatus("CANCEL");
  deliveryDao.update(billing);
  
  return order;
}
```



### 도메인 모델에서 처리할 경우

- order, billing, delivery가 각자 본인의 취소 이벤트 처리를 한다.
- 서비스 메서드는 트랜잭션과 도메인 간의 순서만 보장해준다.

```java
@Transactional
public Order cancelOrder(int orderId){
  //1)
  Orders order = ordersRepository.findById(orderId);
  Billing billing = billingRepository.findByOrderId(orderId);
  Delivery delivery = deliveryRepository.findByOrderId(orderId);
  
  //2,3)
  delivery.cancel();
  
  //4) 
  order.cancel();
  billing.cancel();
  
  return order;
}
```



