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
# 一、首先是Stringbulider的构造函数
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
# 二、append()方法解析
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
     - 如果字符串长度比char[]大，新建一个char[]并通过Arrays.copyOf把原来的char[]拷贝进去，核心是newCapacity()方法，入参为要存的字符串长度。
     - 当然如果小于的话，这个方法其实就是走个过场，没什么用，直接调用的是append()方法中的str.getChars(0, len, value, count);
```
    private int newCapacity(int minCapacity) {
        // overflow-conscious code
        int newCapacity = (value.length << 1) + 2;
        if (newCapacity - minCapacity < 0) {
            newCapacity = minCapacity;
        }
        return (newCapacity <= 0 || MAX_ARRAY_SIZE - newCapacity < 0)
            ? hugeCapacity(minCapacity)
            : newCapacity;
    }
```
- newCapacity和minCapacity
- 可以看到newCapacity = 原数组长度左移一位 +  2，也就是2倍+2
- 然后用扩容后的2倍+2和要存的字符串长度比较，字符串的长度长，那么直接将字符串的长度设定为扩容的长度。
- 最后是，如果 newCapacity 超过了当前数组的最大值的时候，执行 hugeCapacity()方法，如果没有，返回新扩容后的数组
##### str.getChars(0, len, value, count)方法源码
```
    public void getChars(int srcBegin, int srcEnd, char dst[], int dstBegin) {
        if (srcBegin < 0) {
            throw new StringIndexOutOfBoundsException(srcBegin);
        }
        if (srcEnd > value.length) {
            throw new StringIndexOutOfBoundsException(srcEnd);
        }
        if (srcBegin > srcEnd) {
            throw new StringIndexOutOfBoundsException(srcEnd - srcBegin);
        }
        System.arraycopy(value, srcBegin, dst, dstBegin, srcEnd - srcBegin);
    }
```
- 从入参来看，getChar(0, 要拼接字符串的长度，当前数组，未拼接时候字符串的长度)
- 如果要拼接字符串长度大于当前数组长度抛异常，如果0>要拼接字符串长度，抛异常
- 通过调用 System.arraycopy()系统方法完成将当前字符串的 scrBegin ~ srcEnd 复制到字符数组的 dstBegin 位置。
- 将第一个参数-第二个参数长度的字符串复制到第三个参数中第四个参数的位置
# 处理append(null)
```
    private AbstractStringBuilder appendNull() {
        int c = count;
        ensureCapacityInternal(c + 4);
        final char[] value = this.value;
        value[c++] = 'n';
        value[c++] = 'u';
        value[c++] = 'l';
        value[c++] = 'l';
        count = c;
        return this;
    }
```
- 确保容量足够后，追加4个字符到字符数组。
# 三、stringBulider.delete()方法解析
##### 源码
```
    public StringBuilder delete(int start, int end) {
        super.delete(start, end);
        return this;
    }
    
    public AbstractStringBuilder delete(int start, int end) {
        if (start < 0)
            throw new StringIndexOutOfBoundsException(start);
        if (end > count)
            end = count;
        if (start > end)
            throw new StringIndexOutOfBoundsException();
        int len = end - start;
        if (len > 0) {
            System.arraycopy(value, start+len, value, start, count-end);
            count -= len;
        }
        return this;
    }
```
- 如果start小于0，直接抛异常
- 判断end是不是大于数组长度，如果大于让end等于数组长度
- 判断start是否大于end,如果大于，直接抛异常
- 如果本身字符数组是空的，直接返回，否则调用System.arraycopy(value, start+len, value, start, count-end); 这是一个本地方法
- 原来字符数组，要删除的起始位置+要删除的长度， 原来的字符数组 ，删除的起始位置 ， 字符串长度-要删除的终止位置
### 例如
StringBulider  是      abcdfecghgi
删除   stringBulider.delete(2,5);

需要知道的是  【原来的abcdfecghgi】, 【2+(5-2)要删除的长度】删除终止位 ,  【原来的字符数组】 ，  【开始位置】， 【末尾剩了几位】 

# stringBulider.inster()方法解析拼前缀

