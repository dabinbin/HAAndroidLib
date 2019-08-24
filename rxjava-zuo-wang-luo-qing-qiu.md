---
description: create by bin
---

# rxjava 做网络请求

**1.**retrofit + call

```java
getUser(userId, new Callback<User>() {
    @Override
    public void success(User user) {
        new Thread() {
            @Override
            public void run() {
                processUser(user); //查询本地数据库, 尝试修正 User 数据
                runOnUiThread(new Runnable() { // 切回 UI 线程
                    @Override
                    public void run() {
                        userView.setUser(user);
                    }
                });
            }).start();
    }

    @Override
    public void failure(RetrofitError error) {
        // Error handling
        ...
    }
};
```

2.retrofit + rxjava

```java
getUser(userId)
    .doOnNext(new Action1<User>() {
        @Override
        public void call(User user) {
            processUser(user);
        })
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(new Observer<User>() {
        @Override
        public void onNext(User user) {
            userView.setUser(user);
        }

        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable error) {
            // Error handling
            ...
        }
    });
```

后台代码和前台代码全都写在一条链中，明显清晰了很多。

再举一个例子\(访问两个接口\)：假设 `/user` 接口并不能直接访问，而需要填入一个在线获取的 `token` ，代码应该怎么写？

`Callback` 方式，可以使用嵌套的 `Callback`：

```java
@GET("/token")
public void getToken(Callback<String> callback);

@GET("/user")
public void getUser(@Query("token") String token, @Query("userId") String userId, Callback<User> callback);

...

getToken(new Callback<String>() {
    @Override
    public void success(String token) {
        getUser(token, userId, new Callback<User>() {
            @Override
            public void success(User user) {
                userView.setUser(user);
            }

            @Override
            public void failure(RetrofitError error) {
                // Error handling
                ...
            }
        };
    }

    @Override
    public void failure(RetrofitError error) {
        // Error handling
        ...
    }
});
```

而使用 RxJava 的话，代码是这样的：\(网络访问虽然是异步的, 但我还是能一条链下来, 请求完一个接口再请求一个接口, 牛逼\)

```java
@GET("/token")
public Observable<String> getToken();

@GET("/user")
public Observable<User> getUser(@Query("token") String token, @Query("userId") String userId);

...

getToken()
    .flatMap(new Func1<String, Observable<User>>() {
        @Override
        public Observable<User> onNext(String token) {
            return getUser(token, userId);
        })
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(new Observer<User>() {
        @Override
        public void onNext(User user) {
            userView.setUser(user);
        }

        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable error) {
            // Error handling
            ...
        }
    });
```

想想两个网络请求, 再嵌套一个数据库操作会怎么样......

```java
@GET("/token")
public void getToken(Callback<String> callback);

@GET("/user")
public void getUser(@Query("token") String token, @Query("userId") String userId, Callback<User> callback);

...

getToken(new Callback<String>() {
    @Override
    public void success(String token) {
        getUser(token, userId, new Callback<User>() {
            @Override
            public void success(User user) {
                new Thread() {
                    @Override
                    public void run() {
                        processUser(user); //查询本地数据库, 尝试修正 User 数据
                        runOnUiThread(new Runnable() { // 切回 UI 线程
                            @Override
                            public void run() {
                                userView.setUser(user);
                            }
                        });
                    }).start();
            }

            @Override
            public void failure(RetrofitError error) {
                // Error handling
                ...
            }
        };
    }

    @Override
    public void failure(RetrofitError error) {
        // Error handling
        ...
    }
});
```

我最后一次复制代码.......混点字数

```java
getToken()
    .flatMap(new Func1<String, Observable<User>>() {
        @Override
        public Observable<User> onNext(String token) {
            return getUser(token, userId);
        })
    .doOnNext(new Action1<User>() {
        @Override
        public void call(User user) {
            processUser(user);
        })
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(new Observer<User>() {
        @Override
        public void onNext(User user) {
            userView.setUser(user);
        }

        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable error) {
            // Error handling
            ...
        }
    });
```

### 延伸...\(其实可能应该先讲基础知识\)

组合多个Observable。

**zip**： 使用一个函数组合多个Observable发射的数据集合，然后再发射这个结果。如果多个Observable发射的数据量不一样，则以最少的Observable为标准进行压合。内部通过`OperatorZip`进行压合。

```java
Observable.zip(Network.getGankApi().getBeauties(200, 1),
                Network.getZhuangbiApi().search("装逼"),
                new BiFunction<List<Item>, List<ZhuangbiImage>, List<Item>>() {
                    @Override
                    public List<Item> apply(List<Item> gankItems, List<ZhuangbiImage> zhuangbiImages) {
                        List<Item> items = new ArrayList<Item>();
                        for (int i = 0; i < gankItems.size() / 2 && i < zhuangbiImages.size(); i++) {
                            items.add(gankItems.get(i * 2));
                            items.add(gankItems.get(i * 2 + 1));
                            Item zhuangbiItem = new Item();
                            ZhuangbiImage zhuangbiImage = zhuangbiImages.get(i);
                            zhuangbiItem.description = zhuangbiImage.description;
                            zhuangbiItem.imageUrl = zhuangbiImage.image_url;
                            items.add(zhuangbiItem);
                        }
                        return items;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Item>>() {
                    @Override
                    public void accept(@NonNull List<Item> items) throws Exception {
                        swipeRefreshLayout.setRefreshing(false);
                        adapter.setItems(items);
                    }
                });
```

其他, 还有

**concat**： 按顺序连接多个Observables。需要注意的是`Observable.concat(a,b)`等价于`a.concatWith(b)`。

```java
Observable<Integer> observable1=Observable.just(1,2,3,4);
Observable<Integer>  observable2=Observable.just(4,5,6);

Observable.concat(observable1,observable2)
            .subscribe(item->Log.d("JG",item.toString()));//1,2,3,4,4,5,6
```

**startWith**： 在数据序列的开头增加一项数据。`startWith`的内部也是调用了`concat`

```java
 Observable.just(1,2,3,4,5)
            .startWith(6,7,8)
    .subscribe(item->Log.d("JG",item.toString()));//6,7,8,1,2,3,4,5
```

**merge**： 将多个Observable合并为一个。不同于concat，merge不是按照添加顺序连接，而是按照时间线来连接。其中`mergeDelayError`将异常延迟到其它没有错误的Observable发送完毕后才发射。而`merge`则是一遇到异常将停止发射数据，发送onError通知。











  


