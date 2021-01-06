public class Test {
    public static void main(String[] args) {
        StringBuilder  str = new StringBuilder("");
        str.append("123");
        //str.append(null);
        str.append("null");
        str.insert(0,"w");
        System.out.println(str);
    }
}
