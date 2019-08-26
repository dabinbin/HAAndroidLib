---
description: 几个当时没有解答出来的问题
---

# 课后习题

### 1.complete 会不会调\(不会\)

![](.gitbook/assets/image%20%2810%29.png)

### 2.just\(aaa\[\]\)

![](.gitbook/assets/image.png)

![](.gitbook/assets/image%20%286%29.png)

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

![](.gitbook/assets/image%20%289%29.png)

