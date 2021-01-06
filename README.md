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
        if (str == null)
            return appendNull();
        int len = str.length();
        ensureCapacityInternal(count + len);
        str.getChars(0, len, value, count);
        count += len;
        return this;
    }
```
- append主要分为null和非null,首先判断最加的字符串是不是null,如果是null调用appendNull()方法，如果不是null,获取len(追加字符串的长度)后调用ensurCapacityInternal()方法。
- ensurCapacityInternal()方法的参数是count+len,我们测试代码中append("123"),第一次进来，count是0，因此就是0+3,第二次count就变成了3，因此可以看出该方法的参数是要存放字符串的长度。
##### 进入ensurCapacityInternal()方法
```
    private void ensureCapacityInternal(int minimumCapacity) {
        // overflow-conscious code
        if (minimumCapacity - value.length > 0) {
            value = Arrays.copyOf(value,
                    newCapacity(minimumCapacity));
        }
    }
```
     - 判断字符串的长度是否大于目前char[]数组的长度
