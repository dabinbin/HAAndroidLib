---
description: 几个当时没有解答出来的问题
---

# 课后习题

### 1.complete 会不会调\(不会\)

![](.gitbook/assets/image%20%2812%29.png)

### 2.just\(aaa\[\]\)

![](.gitbook/assets/image%20%281%29.png)

![](.gitbook/assets/image%20%285%29.png)

### 3.背压策略 last

```java
Flowable.range(0,Integer.MAX_VALUE)
                        .onBackpressureLatest()
                        .subscribe(aLong -> {
                            Thread.sleep(100);
                            Log.i("mingbin",aLong+"");
                        });
```

Log 一直打印1 到int.max

内存情况, 启动这段代码后  内存反而降低了很神奇￼

![](.gitbook/assets/image%20%2811%29.png)

后来研究了一下, 发现自己代码写错了,背压不起作用.....

回想一下背压的定义----背压是指在异步场景中，被观察者发送事件速度远快于观察者的处理速度的情况下，一种告诉上游的被观察者降低发送速度的策略。这里上游数据发射和下游的数据处理在各自的 **"独立线程"** 中执行，如果在同一个线程中不存在背压的情形。下游对数据的处理会堵塞上游数据的发送，上游发送一条数据后会等下游处理完之后再发送下一条。...

代码应该改为

```java
Flowable.range(0,Integer.MAX_VALUE)
                        .onBackpressureLatest()
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.newThread())
                        .subscribe(aLong -> {
                            Thread.sleep(100);
                            Log.i("mingbin",aLong+"");
                        });//mingbin 0-127 然后直接到int.max
```

打印0-127, 在127停了很久.....然后突然直接到int.max终止

![](.gitbook/assets/image.png)

### 4.关于翻译的问题

latest一般翻译为最新的意思,  但是如果翻译成最新的话 就不符合这个背压的策略了, 于是 ....应该请一下英语八级的大佬出来:....

"what do you think of his latest play?"

"I like it much better than his last one.".

这两句话怎么翻译呢?

1,  你可能翻译为: "你觉得他最新的一次表演怎么样? "     "比起上次的那个,我更喜欢这个."   \(中国人平均水平\)

2. "他最后的那场演出你觉得怎样?"   "比他上一场好多了。"   这样翻译,  latest = 最后的,  就能对应上这个策略的问题













