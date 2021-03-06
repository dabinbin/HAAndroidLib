# rejava与timer

写一个timer, 从1数到100, 在主线程更新ui, 并在ondestroy停止

```java
CountDownTimer timer = new CountDownTimer(1000000,1000) {
    @Override
    public void onTick(long millisUntilFinished) {
        tvNum.setText("" + millisUntilFinished/1000);
    }

    @Override
    public void onFinish() {

    }
};
timer.start();

@Override
protected void onDestroy() {
    super.onDestroy();
    if(timer!=null){
        timer.cancel();
        timer = null;
    }
}
```

 用rxjava写, 是这样的, 只需要5行

```java
Observable.intervalRange(0, 100, 0, 1, TimeUnit.SECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .compose(bindUntilEvent(ActivityEvent.DESTROY))
        .map(String::valueOf)//转化成string
        .subscribe(aLong -> tvNum.setText(aLong));
```

 顺便举一个延时任务的例子, 延时5秒后更新ui

```java
Observable.timer(5, TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .compose(bindUntilEvent(ActivityEvent.DESTROY))
                        .subscribe(aLong -> tvDelayNum.setText("做了!!!!!!!!!!!!!!!!!!"));

new Handler().postDelayed(() -> tvDelayNum.setText("做了!!!!!!!!!!!!!!!!!!"),5000);
```

这里看到hander更简单,  但是 会有泄漏问题.......要把这个问题改好就可以看出价值了,  当然晓刚有用rxjava实现的线程框架了, 这里只是对比来看看

```java
 Handler handler = new Handler();
 
 ...
                
 handler.postDelayed(() -> tvDelayNum.setText("做了!!!!!!!!!!!!!!!!!!"),5000);
 
 ...
 
@Override
protected void onDestroy() {
    super.onDestroy();
    if(handler!=null){
        handler.removeCallbacksAndMessages(null);
        handler = null;
    }
}
```



