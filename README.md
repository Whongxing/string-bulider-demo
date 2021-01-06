# string-bulider-demo
StringBulider中append、inster、delete方法分析
# 测试代码
- 1、分析构造函数被
- 2、分析append方法
- 3、不能append(null),不能过编译，报红
- 4、String如何处理append("null")
```
public static void main(String[] args) {
        StringBuilder  str = new StringBuilder("");

        str.append("123");

        str.append(null);

        str.append("null");
    }
```
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
可以看到，stringBuilder是调用了父类的构造方法并且传了一个16进去，而父类的构造函数是创建了一个16的char[]数组。
# append()方法解析
### 源码
```
    @Override
    public StringBuilder append(String str) {
        super.append(str);
        return this;
    }
    
    //调用父类的AbstractStringBulider的方法
    public AbstractStringBuilder append(String str) {
        //如果传进来的是null
        if (str == null)
            //下方解释
            return appendNull();
            
        //如果不是null,获取str.length的长度
        int len = str.length();
        //重要方法，下面解释
        ensureCapacityInternal(count + len);
        str.getChars(0, len, value, count);
        count += len;
        return this;
    }
```
