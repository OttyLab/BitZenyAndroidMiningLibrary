# BitZeny Miner for Android

## Summary
This is a BitZeny Miner for Android based on [cpuminer](https://github.com/bitzeny/cpuminer). cpuminer is modifed to work as a library through JNI.

## Library


### Constructors


```
public BitZenyMiningLibrary()
```
```
public BitZenyMiningLibrary(Handler handler)
```

Library `putString` logs with 'log' tag.


### Methods

```
public native boolean isMiningRunning()
public native int startMining(String url, String user, String password, int n_threads)
public native int startBenchmark(int n_threads)
public native int stopMining()
```
