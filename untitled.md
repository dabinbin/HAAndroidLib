---
description: create by bin
---

# rxjava简单使用介绍

rxjava的从入门到放弃:  [https://mp.weixin.qq.com/s/-D1\_DV6amGfz4GPlpdDpJQ](https://mp.weixin.qq.com/s/-D1_DV6amGfz4GPlpdDpJQ)  rxjava的反对意见, 说的还挺好的.

他的意思就是rxjava太难了, 而且很多操作符意义不明,  还会产生很多意想不到的结果,  总的来说, 他的话, 在学习rxjava的过程中, 有很大的参考价值, 感谢他!感谢兴修提供的反对意见,  就是因为有这些反对意见, 我们学习的时候才能够小心谨慎,  不会因为学了点啥而沾沾自喜.....

简单来说rxjava就是异步......

异步是什么意思呢,  一个\(android\)进程最少有一个主线程, n多个子线程\(因为不可能啥都在主线程做\)

**所以rxjava的主要用于, 线程间的调度**\(记笔记, 我特地加粗了\)

