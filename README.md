# nostalgia [![](https://jitpack.io/v/billy96322/nostalgia.svg)](https://jitpack.io/#billy96322/nostalgia)

编译时注解的rxbus

### 特性

- 可以过滤生命周期
- 无需public修饰
- 可配置接受次数
- 事件以字符串作为标识

### 初始化
```Java
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Nostalgia.initialize(AndroidSchedulers.mainThread());
    }
}
```

### 发送，接受事件
```Java
public class MainActivity extends AppCompatActivity {

    Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 在需要接受事件时绑定
        unbinder = Nostalgia.bind(this, new RxLifecycleAdapter(this));

    }

    // @Receive是接受事件必要的注解
    // tag参数是消息的标识，是必填参数。
    // scheduler是方法提执行的线程是选填参数，默认为MAINTHREAD即主线程
    @Receive(tag = "empty_msg", scheduler = Scheduler.IO)
    void onEmptyParam() {

    }

    // @LifecycleFilter注解是过滤生命周期时使用的
    // 如下例子所示，方法提仅在resume到pause生命周期之间才会执行
    @Receive(tag = "msg")
    @LifecycleFilter(from = ActivityLifecycle.RESUME, to = ActivityLifecycle.PAUSE)
    void onBaseTypeParamWhenVisible(int i) {

    }

    // @Take注解是配置接受次数
    // 如下例子所示，该消息最多接受三次
    @Take(times = 3)
    @Receive(tag = "msg")
    void onBaseTypeParamTake3Times(int i) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 在不需要接受事件解除绑定
        unbinder.unbind();
    }

    public void send(View view) {
        // 发送事件
        Nostalgia.post("msg", 10);
    }
}

```

### 绑定
当当前对象需要接受过滤生命周期的消息时(即当前类使用到了@LifecycleFilter)，需要绑定生命周期的适配器
，nostalgia提供了两种rxlifecycle的适配器。
如果使用了[trello-rxlifecycle](https://github.com/trello/RxLifecycle)则用法如下
```Java
  unbinder = Nostalgia.bind(this, new RxLifecycleAdapter(lifecycle()));
```
如果使用了[zhihu-rxlifecycle](https://github.com/zhihu/RxLifecycle)或者是没有使用rxlifecycle则用法如下
```Java
  unbinder = Nostalgia.bind(this, new RxLifecycleAdapter(this));
```
如果当前没有使用到@LifecycleFilter注解，则不需要传递此参数
```Java
  unbinder = Nostalgia.bind(this);
```

### 添加依赖到项目
在项目根目录的build.gradle添加
```gradle
allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }
		}
}
```

添加依赖(具体版本参照readme上的版本图标)
``` gradle
dependencies {

        compile 'com.github.billy96322.nostalgia:core:0.0.1'
        
        annotationProcessor 'com.github.billy96322.nostalgia:processor:0.0.1'
	
	// nostalgia 需要依赖 RxAndroid 以提供初始化时的配置
	compile 'io.reactivex.rxjava2:rxandroid:2.0.1'
        
        // 以下根据需要选择一项
        // trello-adapter
        compile 'com.github.billy96322.nostalgia:lifecycleadapter-rxlifecycle:0.0.1'
        
        // zhihu-adapter
        compile 'com.github.billy96322.nostalgia:lifecycleadapter-zhihu-rxlifecycle:0.0.1'
        
}
