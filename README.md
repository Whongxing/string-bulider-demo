# string-bulider-demo
StringBulider中append、inster、delete方法分析
# 首先是Stringbulider的构造函数
```
public StringBuilder() {
        super(16);
    }
//super(16)跟踪进去是
  AbstractStringBuilder(int capacity) {
      value = new char[capacity];
  }
```
可以看到，stringBuilder是调用了父类的构造方法并且传了一个16进去
