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

Library `putString` log strings with 'log' tag. Applications can `getString` then use the log strings.


### Methods

```
public boolean isMiningRunning()
public int startMining(String url, String user, String password, int n_threads)
public int startBenchmark(int n_threads)
public int stopMining()
```

## Tips

Library core uses `git submodule`. Therefore, run `git submodule update` after cloning.
