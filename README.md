

# 📦 JPA 배달 도메인 프로젝트 (JPA Delivery Domain Project)

Spring 환경에서 **JPA(Java Persistence API)의 핵심 동작 원리와 영속성 컨텍스트**를 깊이 있게 이해하기 위해 구현한 간단한 배달 애플리케이션 도메인 모델입니다.

단순히 Spring Data JPA에 의존하는 것을 넘어, `EntityManager`와 `EntityTransaction`을 직접 제어하며 객체 중심의 설계와 데이터베이스 연동을 실습하는 데 목적을 두었습니다.

## 🛠 Tech Stack

* **Language:** Java
* **Framework:** Spring Boot
* **ORM:** JPA (Hibernate)
* **Build Tool:** Gradle

---

## 📊 도메인 모델 (ERD 설계)

이 프로젝트는 총 4개의 핵심 엔티티(`Member`, `Store`, `Menu`, `Orders`)로 구성되어 있으며, 각 엔티티 간의 연관관계를 JPA로 매핑했습니다.

* **회원 (Member)**
  * 사용자의 식별자, 이름, 주소 정보를 가집니다.
  * 회원은 여러 번의 주문을 할 수 있습니다. `Member (1) : Orders (N)`


* **가게 (Store)**
  * 가게 식별자, 이름, 주소 정보를 가집니다.
  * 가게는 여러 개의 메뉴를 가질 수 있습니다. `Store (1) : Menu (N)`


* **메뉴 (Menu)**
  * 메뉴 식별자, 이름, 가격 정보를 가집니다.
  * 특정 가게에 소속됩니다. `Menu (N) : Store (1)`


* **주문 (Orders)**
  * 주문 식별자, 주문 일시, 주문 상태(`ORDER`, `CANCEL`) 정보를 가집니다.
  * 특정 회원이 특정 메뉴를 주문하는 행위를 나타내는 다대다(N:M) 관계의 연결 엔티티 역할을 수행합니다.
  * `Orders (N) : Member (1)`, `Orders (N) : Menu (1)`



---

## 💡 주요 구현 기능 및 JPA 핵심 학습 포인트

본 프로젝트는 JPA를 활용한 데이터 접근 기술 구현에 초점을 맞추고 있습니다.

### 1. 객체 지향적인 양방향 연관관계 매핑

* **`@OneToMany`, `@ManyToOne` 활용:** 모든 다중성 관계를 명확히 매핑했습니다.
* **연관관계 편의 메서드:** 객체 양방향 관계에서 어느 한쪽만 데이터를 세팅하는 실수를 방지하기 위해 `Member.addOrder()`, `Orders.setMember()`, `Menu.setStore()` 등의 편의 메서드를 엔티티 내부에 구현하여 객체 지향적인 설계를 유지했습니다.

### 2. 영속성 컨텍스트와 트랜잭션 수동 제어

* Spring의 `@Transactional`에 전적으로 의존하지 않고, `MemberService`, `OrderService`, `StoreService` 등에서 `EntityManagerFactory`를 통해 `EntityManager`를 생성하여 생명주기를 직접 관리합니다.
* `EntityTransaction`을 통한 수동 트랜잭션 처리(begin, commit, rollback)를 구현하여 JPA 내부 트랜잭션 동작 방식을 직관적으로 확인할 수 있습니다.
* `MenuService`에서는 Spring의 `@Transactional`을 혼용하여 선언적 트랜잭션 관리와 프로그래밍 방식의 트랜잭션 관리를 비교 학습할 수 있습니다.

### 3. 지연 로딩 (Lazy Loading) 설정

* 즉시 로딩(EAGER)으로 인한 N+1 문제와 불필요한 쿼리 실행을 방지하기 위해, 모든 연관관계(`@ManyToOne`)에 `FetchType.LAZY`를 적용하여 실무적인 성능 최적화 기반을 마련했습니다.

### 4. Fetch Join을 활용한 성능 최적화

* `MemberService.getMemberOrders()` 메서드에서 JPQL의 `join fetch` 문법(`select m from Member m join fetch m.orders`)을 사용하여 회원 조회 시 연관된 주문 정보까지 한 번의 쿼리로 가져오도록 N+1 문제를 해결했습니다.

### 5. 영속성 전이(Cascade)와 고아 객체(OrphanRemoval) 관리

* `Store`와 `Menu`의 관계에서 `CascadeType.ALL`과 `orphanRemoval = true` 옵션을 적용했습니다.
* 부모 엔티티(가게)가 삭제되거나 연관관계가 끊어질 때, 자식 엔티티(메뉴)의 생명주기도 함께 관리되도록 구현했습니다.

---

## 🚀 Service 주요 기능 요약

| 서비스 | 기능 | 상세 설명 |
| --- | --- | --- |
| **MemberService** | 회원 관리 | 회원 가입, 전체 회원 조회, 단건 조회, 특정 회원의 주문 목록 조회(Fetch Join 적용) |
| **StoreService** | 가게 관리 | 가게 등록, 전체/단건 조회, 가게 삭제(연관된 메뉴 함께 삭제) |
| **MenuService** | 메뉴 관리 | 특정 가게에 메뉴 등록, 전체/단건/가게별 메뉴 조회, 메뉴 가격/이름 수정(Dirty Checking) |
| **OrderService** | 주문 관리 | 회원 및 메뉴 ID 기반 주문 생성, 주문 취소(상태 변경), 주문 정보 DTO 변환 조회 |

