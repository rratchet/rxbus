# 事件总线RxBus

## 使用方法

### 获取RxBus

推荐获取一个RxBus的单例：

```java
Bus bus = BusProvider.getInstance();
```

### 注册、订阅、注销

#### 1、简单的声明和使用注解 @Subscribe


注册：

```java
bus.register(this);
```

订阅（这方法应该是public和使用only一个single参数）：

```Java
@Subscribe
public void onEvent(Object event) {
    // TODO: Do something
}
```

#### 2、使用定制的订阅器　subscriber：

```Java
EventSubscriber<Object> subscriber = EventSubscriber.create(Object.class,
    new Consumer<Object>() {
        @Override
        public void accept(Object event) throws Exception {
            // TODO: Do something
        }
    })
    .withFilter(new Predicate<Object>() { // 过滤事件
        @Override
        public boolean test(Object event) throws Exception {
            return "Specific message".equals(event.message);
        }
    })
    .withScheduler(Schedulers.trampoline());

bus.registerSubscriber(this, subscriber);
```

#### ３、使用定制的事件 RxEvent & 订阅器 subscriber：

```Java
RxEvent event = new RxEvent("tag", "type");
event.setData(data);

EventSubscriber<RxEvent> subscriber = EventSubscriber.create(RxEvent.class,
    new Consumer<RxEvent>() {
        @Override
        public void accept(RxEvent event) throws Exception {
            // TODO: Do something
        }
    })
    .withFilter(new Predicate<RxEvent>() { // 过滤事件
        @Override
        public boolean test(RxEvent event) throws Exception {
            // TODO: Do something
            return event.getData() != null;
        }
    })
    .withScheduler(Schedulers.trampoline());

bus.registerSubscriber(this, event, subscriber);
```

请也记得注销RxBus当适当的时候

```java
bus.unregister(this);
```

### 发布事件

为了发布一个事件，调用post方法

```java
bus.post(event);
```